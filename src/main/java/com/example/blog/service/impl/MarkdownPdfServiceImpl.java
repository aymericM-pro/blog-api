package com.example.blog.service.impl;

import com.example.blog.config.GcsProperties;
import com.example.blog.enums.StorageError;
import com.example.blog.exception.BusinessException;
import com.example.blog.service.MarkdownPdfService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkdownPdfServiceImpl implements MarkdownPdfService {

    private static final Parser MD_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final Storage storage;
    private final GcsProperties gcsProperties;

    @Override
    public String convertAndUpload(String markdownUrl, String filename) {
        String markdownContent = fetchMarkdown(markdownUrl);
        byte[] pdfBytes = renderToPdf(markdownContent);

        String objectName = buildObjectName(filename);
        BlobId blobId = BlobId.of(gcsProperties.bucketName(), objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("application/pdf")
                .build();

        storage.create(blobInfo, pdfBytes);

        String url = buildPublicUrl(objectName);
        log.info("Markdown→PDF uploaded: {}", url);
        return url;
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private String fetchMarkdown(String markdownUrl) {
        String gcsPrefix = gcsProperties.publicUrlPrefix() + "/" + gcsProperties.bucketName() + "/";
        if (markdownUrl.startsWith(gcsPrefix)) {
            return fetchFromGcs(markdownUrl, gcsPrefix);
        }
        return fetchFromHttp(markdownUrl);
    }

    private String fetchFromGcs(String markdownUrl, String gcsPrefix) {
        String objectName = markdownUrl.substring(gcsPrefix.length());
        try {
            byte[] bytes = storage.readAllBytes(gcsProperties.bucketName(), objectName);
            String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            log.info("Markdown fetched from GCS: {} ({} chars)", objectName, content.length());
            return content;
        } catch (Exception e) {
            log.error("GCS read failed for object {}: {}", objectName, e.getMessage(), e);
            throw new BusinessException(StorageError.UPLOAD_FAILED);
        }
    }

    private String fetchFromHttp(String markdownUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(markdownUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("Failed to fetch markdown from {}: HTTP {}", markdownUrl, response.statusCode());
                throw new BusinessException(StorageError.UPLOAD_FAILED);
            }
            log.info("Markdown fetched from {} ({} chars)", markdownUrl, response.body().length());
            return response.body();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching markdown from {}: {}", markdownUrl, e.getMessage(), e);
            throw new BusinessException(StorageError.UPLOAD_FAILED);
        }
    }

    private byte[] renderToPdf(String markdownContent) {
        String html = buildFullHtml(markdownContent);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("PDF rendering failed: {}", e.getMessage(), e);
            throw new BusinessException(StorageError.UPLOAD_FAILED);
        }
    }

    private String buildFullHtml(String markdownContent) {
        String body = HTML_RENDERER.render(MD_PARSER.parse(markdownContent));
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 48px; color: #1a1a2e; line-height: 1.6; }
                        h1, h2, h3, h4 { color: #16213e; margin-top: 1.5em; }
                        h1 { font-size: 2em; border-bottom: 2px solid #e94560; padding-bottom: 0.3em; }
                        h2 { font-size: 1.5em; border-bottom: 1px solid #ddd; padding-bottom: 0.2em; }
                        a { color: #e94560; }
                        code { background: #f4f4f4; padding: 2px 5px; border-radius: 3px; font-size: 0.9em; }
                        pre { background: #f4f4f4; padding: 12px 16px; border-radius: 4px; overflow-x: auto; }
                        pre code { background: none; padding: 0; }
                        blockquote { border-left: 4px solid #e94560; margin: 0; padding-left: 16px; color: #555; }
                        table { border-collapse: collapse; width: 100%%; }
                        th, td { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
                        th { background: #f0f0f0; }
                        img { max-width: 100%%; }
                    </style>
                </head>
                <body>%s</body>
                </html>
                """.formatted(body);
    }

    private String buildObjectName(String filename) {
        String base = Optional.ofNullable(filename)
                .filter(f -> !f.isBlank())
                .map(f -> f.strip().replaceAll("[^a-zA-Z0-9_-]", "_"))
                .orElseGet(() -> UUID.randomUUID().toString());
        return "pdfs/" + base + "-" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
    }

    private String buildPublicUrl(String objectName) {
        return gcsProperties.publicUrlPrefix()
                + "/" + gcsProperties.bucketName()
                + "/" + objectName;
    }
}

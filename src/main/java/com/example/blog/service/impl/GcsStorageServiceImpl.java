package com.example.blog.service.impl;

import com.example.blog.config.GcsProperties;
import com.example.blog.enums.StorageError;
import com.example.blog.exception.BusinessException;
import com.example.blog.service.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsStorageServiceImpl implements StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    private final Storage storage;
    private final GcsProperties gcsProperties;

    @Override
    public String upload(MultipartFile file, String folder) {
        validateFile(file);

        String extension = extractExtension(file.getOriginalFilename());
        String objectName = buildObjectName(folder, extension);

        try {
            BlobId blobId = BlobId.of(gcsProperties.bucketName(), objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            String url = buildPublicUrl(objectName);
            log.info("GCS upload succeeded: {}", url);
            return url;

        } catch (IOException e) {
            log.error("GCS upload failed for object {}: {}", objectName, e.getMessage(), e);
            throw new BusinessException(StorageError.UPLOAD_FAILED);
        } catch (StorageException e) {
            log.error("GCS SDK error during upload: {}", e.getMessage(), e);
            throw new BusinessException(StorageError.UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String url) {
        String objectName = extractObjectNameFromUrl(url);

        try {
            BlobId blobId = BlobId.of(gcsProperties.bucketName(), objectName);
            boolean deleted = storage.delete(blobId);
            if (!deleted) {
                log.warn("GCS delete: object not found (already absent?), url={}", url);
            }
        } catch (StorageException e) {
            log.error("GCS SDK error during delete: {}", e.getMessage(), e);
            throw new BusinessException(StorageError.DELETE_FAILED);
        }
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(StorageError.EMPTY_FILE);
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException(StorageError.FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException(StorageError.INVALID_FILE_TYPE);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String buildObjectName(String folder, String extension) {
        String prefix = (folder != null && !folder.isBlank()) ? folder.strip() + "/" : "";
        return prefix + UUID.randomUUID() + "." + extension;
    }

    private String buildPublicUrl(String objectName) {
        return gcsProperties.publicUrlPrefix()
                + "/" + gcsProperties.bucketName()
                + "/" + objectName;
    }

    private String extractObjectNameFromUrl(String url) {
        String expectedPrefix = gcsProperties.publicUrlPrefix()
                + "/" + gcsProperties.bucketName() + "/";
        if (url == null || !url.startsWith(expectedPrefix)) {
            throw new BusinessException(StorageError.INVALID_FILE_URL);
        }
        return url.substring(expectedPrefix.length());
    }
}

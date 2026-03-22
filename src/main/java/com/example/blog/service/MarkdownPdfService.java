package com.example.blog.service;

public interface MarkdownPdfService {

    /**
     * Fetches the Markdown file at the given URL, converts it to a PDF,
     * uploads it to GCS and returns the public URL.
     *
     * @param markdownUrl URL pointing to a .md file
     * @param filename    optional base name for the GCS object (without extension)
     * @return the public HTTPS URL of the uploaded PDF
     */
    String convertAndUpload(String markdownUrl, String filename);
}

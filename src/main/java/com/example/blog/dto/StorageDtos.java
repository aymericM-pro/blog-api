package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StorageDtos {

    public record UploadResponse(String url) {}

    public record FileResponse(String name, String url, long sizeBytes, String contentType) {}

    public record MarkdownToPdfRequest(
            @NotBlank(message = "Markdown file URL is required")
            @Pattern(regexp = ".*\\.md$", message = "URL must point to a .md file")
            String url,
            String filename
    ) {}
}

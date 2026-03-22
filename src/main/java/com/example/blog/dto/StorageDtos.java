package com.example.blog.dto;

public class StorageDtos {

    public record UploadResponse(String url) {}

    public record FileResponse(String name, String url, long sizeBytes, String contentType) {}
}

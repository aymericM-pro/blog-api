package com.example.blog.controller;

import com.example.blog.dto.StorageDtos.FileResponse;
import com.example.blog.dto.StorageDtos.MarkdownToPdfRequest;
import com.example.blog.dto.StorageDtos.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Storage", description = "File upload endpoints")
@RequestMapping("/api/v1/storage")
public interface StorageController {

    @Operation(
            summary = "Upload an image to GCS",
            description = "Accepted types: jpeg, png, webp, gif. Max size: 5 MB.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder
    );

    @Operation(
            summary = "List files in GCS",
            description = "Returns all files in the bucket, optionally filtered by folder prefix.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/files")
    ResponseEntity<List<FileResponse>> listFiles(
            @RequestParam(value = "folder", required = false, defaultValue = "") String folder
    );

    @Operation(
            summary = "Convert Markdown to PDF and upload to GCS",
            description = "Renders the Markdown content as a styled PDF, stores it in GCS and returns the public URL.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/markdown-to-pdf")
    ResponseEntity<UploadResponse> markdownToPdf(
            @Valid @RequestBody MarkdownToPdfRequest request
    );
}

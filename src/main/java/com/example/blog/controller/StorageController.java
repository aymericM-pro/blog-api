package com.example.blog.controller;

import com.example.blog.dto.StorageDtos.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
}

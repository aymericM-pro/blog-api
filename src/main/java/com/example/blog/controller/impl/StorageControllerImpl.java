package com.example.blog.controller.impl;

import com.example.blog.controller.StorageController;
import com.example.blog.dto.StorageDtos.FileResponse;
import com.example.blog.dto.StorageDtos.UploadResponse;
import com.example.blog.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageControllerImpl implements StorageController {

    private final StorageService storageService;

    @Override
    public ResponseEntity<UploadResponse> upload(MultipartFile file, String folder) {
        String url = storageService.upload(file, folder);
        return ResponseEntity.ok(new UploadResponse(url));
    }

    @Override
    public ResponseEntity<List<FileResponse>> listFiles(String folder) {
        return ResponseEntity.ok(storageService.list(folder));
    }
}

package com.example.blog.service;

import com.example.blog.dto.StorageDtos.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    /**
     * Uploads a file to GCS and returns its public URL.
     *
     * @param file   the multipart file to upload
     * @param folder the GCS folder prefix, e.g. "avatars" or "covers"
     * @return the public HTTPS URL of the uploaded object
     */
    String upload(MultipartFile file, String folder);

    /**
     * Deletes the GCS object identified by the given public URL.
     * Idempotent: does not throw if the object is already absent.
     *
     * @param url the public URL previously returned by {@link #upload}
     */
    void delete(String url);

    /**
     * Lists all objects in the given folder prefix (or the whole bucket if blank).
     *
     * @param folder the GCS folder prefix, e.g. "avatars" — pass blank for all files
     * @return list of file metadata
     */
    List<FileResponse> list(String folder);
}

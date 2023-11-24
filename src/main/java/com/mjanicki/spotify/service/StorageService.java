package com.mjanicki.spotify.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public void init();

    public String save(MultipartFile file, String type, String fileName);
}

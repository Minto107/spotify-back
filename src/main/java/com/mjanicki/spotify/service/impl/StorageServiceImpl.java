package com.mjanicki.spotify.service.impl;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjanicki.spotify.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

    @Value(value = "${spotify.upload.baseDir}")
    private String baseDir;

    @Value(value = "${spotify.upload.audio}")
    private String audioSubDir;

    @Value(value = "${spotify.upload.art}")
    private String imgSubDir;

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(baseDir));
            Files.createDirectories(Paths.get(baseDir + audioSubDir));
            Files.createDirectories(Paths.get(baseDir + imgSubDir));
          } catch (IOException e) {
            throw new RuntimeException("Could not initialize directories for upload!");
          }    
        }

    @Override
    public String save(MultipartFile file, String type, String fileName) {
        try {
            String saveLocation = "";
            switch (type) {
                case "mp3":
                    saveLocation = baseDir + audioSubDir;
                    break;
                case "img":
                    saveLocation = baseDir + imgSubDir;
                    break;
                default:
                    saveLocation = baseDir;
                    break;
            }
            final Path location = Paths.get(saveLocation);
            Files.copy(file.getInputStream(), location.resolve(fileName));
            return saveLocation + fileName;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) throw new RuntimeException("A file of that name already exists.");
            e.printStackTrace();
            }
        return null;
    }
    
}

package com.lumina_bank.analyticsservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorage {

    private final Path root = Paths.get("reports");

    public String save(String filename, byte[] content) {
        try {
            Files.createDirectories(root);
            Path path = root.resolve(filename);
            Files.write(path, content);
            return path.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource load(String path) {
        return new FileSystemResource(path);
    }
}

package com.example.taskmanager.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    void importFromFile(MultipartFile file);
}

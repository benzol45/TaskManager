package com.example.taskmanager.controller;

import com.example.taskmanager.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {
    private final ImportService importService;

    @PostMapping
    public void importInformation(@RequestParam("file") MultipartFile file) {
        importService.importFromFile(file);
    }
}

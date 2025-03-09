package org.example.dropboxbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dropboxbackend.model.File;
import org.example.dropboxbackend.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.store(file));
    }

    @GetMapping
    public ResponseEntity<List<File>> getAllFiles() {
        log.info("getAllFiles -- enter");
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = fileService.loadAsResource(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

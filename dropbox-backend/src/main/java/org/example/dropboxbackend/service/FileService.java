package org.example.dropboxbackend.service;

import jakarta.annotation.PostConstruct;
import org.example.dropboxbackend.model.File;
import org.example.dropboxbackend.repository.FileRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final Path rootLocation;

    public FileService(FileRepository fileRepository){
        this.fileRepository = fileRepository;
        this.rootLocation = Paths.get("uploads");  // base path from the src
    }

    @PostConstruct
    public void init(){
        try{
            Files.createDirectories(rootLocation);
        }
        catch(IOException e){
            throw new RuntimeException("Could not initialize storage folder", e);
        }
    }

    public File store(MultipartFile file){
        try{
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String newFileName = System.currentTimeMillis() + "_" + fileName;
            Path destination = this.rootLocation.resolve(newFileName);

            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);  // save the file to storage

            File fileEntity = new File();
            fileEntity.setFileName(fileName);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setFilePath(destination.toString());
            fileEntity.setLastModified(LocalDateTime.now());

            return fileRepository.save(fileEntity);
        }
        catch(IOException e){
            throw new RuntimeException("Could not store file", e);
        }
    }

    public List<File> getAllFiles(){
        return fileRepository.findAll();
    }

    public Resource loadAsResource(Long id){
        File fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        return new FileSystemResource(fileEntity.getFilePath());
    }
}

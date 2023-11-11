package com.example.controller;

import java.io.IOException;//исключения
import java.util.List;//коллекция List
import java.util.Objects;


import com.example.model.FileData;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;//для работы с загружаемыми файлами в Spring

import com.example.service.FileService;

@RestController

public class FilesController  {

    @PostMapping("/files")
    public ResponseEntity<FileData> uploadFile(@RequestParam("file") MultipartFile uploadedFile) throws IOException {
        FileService.save(uploadedFile);
        return null;
    }

    @GetMapping ("/files")
    public ResponseEntity<List<String>> getFileNames() throws IOException {
        List<String> file =  FileService.loadAll();
        return ResponseEntity.ok(file);
    }

    @GetMapping ("/files/{fileName}")

    public ResponseEntity<FileData> getFileByName(@PathVariable String fileName) throws IOException {

        return ResponseEntity.ok(FileService.load(fileName));
    }
    /*
    @PutMapping("/files/{fileName}")
    public ResponseEntity<FileData> updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile updatedFile) {
        try {
            for (FileData file : files) {
                if (file.getFileName().equals(fileName)) {
                    file.setModificationDate(new Date());
                    file.setFileName(updatedFile.getOriginalFilename());
                    file.setFileType(updatedFile.getContentType());
                    file.setFileSize(updatedFile.getSize());
                    file.setFileContent(updatedFile.getBytes());
                    return ResponseEntity.ok(file);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


     */
    @DeleteMapping ("/files")
    public ResponseEntity<Void> deleteAllFiles() {
        FileService.deleteAll();
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) throws IOException {
        FileService.delete(fileName);

        return null;
    }



}
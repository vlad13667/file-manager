package com.example.controller;

import java.io.IOException;//исключения
import java.util.List;//коллекция List


import com.example.model.FileData;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;//для работы с загружаемыми файлами в Spring

import com.example.service.FileService;

@RestController

public class FilesController  {

    @PostMapping("/files")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadedFile) throws IOException {
        FileData file = FileService.save(uploadedFile);

        return new ResponseEntity<>("Успешная загрузка файла " + file.getFileName(), HttpStatus.CREATED);

    }

    @GetMapping ("/files")
    public ResponseEntity<List<String>> getFileNames() throws IOException {
        List<String> file =  FileService.loadAll();
        return ResponseEntity.ok(file);
    }

    @GetMapping ("/files/{fileName}")

    public ResponseEntity<?> getFileByName(@PathVariable String fileName) throws IOException {
        FileData file = FileService.load(fileName);


        return file != null ?
                new ResponseEntity(file, HttpStatus.OK)
                : new ResponseEntity<>("Файл с именем '" + fileName + "' не найден", HttpStatus.NOT_FOUND);
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
    public ResponseEntity<?> deleteAllFiles() {
        FileService.deleteAll();
        return new ResponseEntity<>("Все файлы удалены",HttpStatus.OK);

    }
    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) throws IOException {
        boolean isDeleted = FileService.delete(fileName);

        return isDeleted ?
                new ResponseEntity<>("Файл с именем '" + fileName + "'удалён",HttpStatus.OK)
                : new ResponseEntity<>("Файл с именем '" + fileName + "'ненайден", HttpStatus.NOT_FOUND);
    }



}
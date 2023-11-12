package com.example.controller;


import java.io.IOException;//исключения
import java.io.InputStream;
import java.time.LocalDateTime;

import java.util.List;//коллекция List



import com.example.model.FileData;


import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;//для работы с загружаемыми файлами в Spring

import com.example.service.FileService;

@RestController

public class FilesController  {



    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15 Мб
    private static java.util.Arrays Arrays;
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
                // Список разрешенных MIME-типов файлов
                "application/msword", // .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                "application/vnd.ms-excel", // .xls
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "application/pdf", // .pdf
                "text/plain", // .txt
                "application/vnd.ms-powerpoint", // .ppt
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" // .pptx
        );
    @PostMapping("/files")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile uploadedFile) throws IOException {
        if (uploadedFile.getSize() > MAX_FILE_SIZE) {
            return new ResponseEntity<>("Ошибка: размер файла превышает 15 Мб", HttpStatus.BAD_REQUEST);
        }

        if (!ALLOWED_FILE_TYPES.contains(uploadedFile.getContentType())) {
            return new ResponseEntity<>("Ошибка: недопустимый тип файла", HttpStatus.BAD_REQUEST);
        }
        FileData file = FileService.save(uploadedFile);

        return new ResponseEntity<>("Успешная загрузка файла " + file.getFileName(), HttpStatus.CREATED);

    }

    @GetMapping("/files/download/multiple")
    public ResponseEntity<byte[]> downloadMultipleFiles(@RequestParam List<String> fileNames) {
       FileService.multupla(fileNames);
        return null;
    }
    @GetMapping ("/files/{fileName}")

    public ResponseEntity<?> getFileByName(@PathVariable String fileName) throws IOException {
        FileData file = FileService.load(fileName);


        return file != null ?
                new ResponseEntity(file, HttpStatus.OK)
                : new ResponseEntity<>("Файл с именем '" + fileName + "' не найден", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) throws IOException {
        try {
            // Эта строка получает  файл как InputStream от  сервиса.

            InputStream file = FileService.download(fileName);

            //Устанавливаем contentType файла как MediaType.APPLICATION_OCTET_STREAM, чтобы файл мог быть прямо загружен
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment;filename=" + fileName)
                    .body(new InputStreamResource(file));
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public ResponseEntity<List<FileData>> getFileNames(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) List<String> types) throws IOException {

        List<FileData> files = FileService.loadAllFiltered(name, dateFrom, dateTo, types);
        return ResponseEntity.ok(files);
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
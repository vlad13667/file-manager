package com.example.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;


import com.example.model.FileData;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService{
    private static List<FileData> files = new ArrayList<>();


    @Value("${upload.path}") //вставляет путь из application.properties
    private String uploadPath;

    @PostConstruct //Выполнение метода после конструировния бина
    //создаёт деректорию для загрузки если её не существует
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку для загрузки!");
        }
    }


    public static FileData save(MultipartFile uploadedFile) throws IOException {



        FileData file = new FileData();
        file.setUploadDate(new Date());
        file.setChangeDate(new Date());
        file.setFileName(uploadedFile.getOriginalFilename());
        file.setFileType(uploadedFile.getContentType());
        file.setFileSize(uploadedFile.getSize());
        file.setFileContent(uploadedFile.getBytes());
        files.add(file);
        return file;

    }





    //Загрузка файла по имени
    public static FileData load(String fileName) throws IOException {
        for (FileData file : files) {
            if (file.getFileName().equals(fileName)) {
                FileData fileInfo = new  FileData();
                fileInfo.setUploadDate(file.getUploadDate());
                fileInfo.setChangeDate(file.getChangeDate());
                fileInfo.setFileName(file.getFileName());
                fileInfo.setFileType(file.getFileType());
                fileInfo.setFileSize(file.getFileSize());
                fileInfo.setFileContent(file.getFileContent());
                return fileInfo;
            }
        }
        return null;
    }

        // удаление всех файлов
    public static void deleteAll() {
        files.clear();
    }

    public static boolean delete(String fileName) throws IOException {
        FileData foundFile = null;
        for (FileData file : files) {
            if (file.getFileName().equals(fileName)) {
                foundFile = file;
                break;
            }
        }
        if (foundFile != null) {
            files.remove(foundFile);
            return true;
            //return ResponseEntity.noContent().build();
        }
        return false;
       // return ResponseEntity.notFound().build();
    }



    public static List<String> loadAll() throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (FileData file : files) {
            fileNames.add(file.getFileName());
        }
        return(fileNames);

    }




}
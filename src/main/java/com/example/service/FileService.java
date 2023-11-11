package com.example.service;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.model.FileData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService{
    private static List<FileData> files = new ArrayList<>();


    @Value("${upload.path}") //вставляет путь из application.properties
    private String uploadPath;




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
                return file;
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
        }

       return false;
    }



    public static List<String> loadAll() throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (FileData file : files) {
            fileNames.add(file.getFileName());
        }
        return(fileNames);

    }




}
package com.example.service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;


import com.example.model.FileData;
import com.example.exeptions.Exception;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService implements FileServiceint {


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

    // Загрузка нового файла
    public void save(MultipartFile file) {
        try {
            Path root = Paths.get(uploadPath);
            //проверка существования директории
            if (!Files.exists(root)) {
                init();
            }
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename())); //копирование загружаемого файла в директорию
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Загрузка файла по имени
    public FileData load(String filename) throws IOException {
        Path file;

            file = Paths.get(uploadPath).resolve(filename);


        return pathToFileData(file);
    }

        // удаление всех файлов
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath).toFile());//рекурсивное удаление файлов
    }

    public boolean delete(String fileName) throws IOException {
        Path file = Paths.get(uploadPath).resolve(fileName);
        Files.delete(file);
        return true;

    }


    public List<FileData> loadAll() throws IOException {
        Path root = Paths.get(uploadPath);
        return Files.walk(root, 1).filter(path -> !path.equals(root)).map(path -> {
            try {
                return pathToFileData(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }
    private FileData pathToFileData(Path path) throws IOException {

        FileData fileData = new FileData();

        String filename = path.getFileName().toString();

        Path file = Paths.get(uploadPath).resolve(filename); //сбор пути к файлу

        Resource resource = new UrlResource(file.toUri());

        fileData.setFileName(filename);

        fileData.setFileUrl(String.valueOf(resource.getURL()));

        fileData.setFileType(FilenameUtils.getExtension(filename));

        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        fileData.setUploadDate(String.valueOf(attr.creationTime()));

        fileData.setChangeDate(String.valueOf(attr.lastModifiedTime()));
        try {
            fileData.setFileSize(Files.size(path));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }

        return fileData;
    }


}
package com.example.service;

import java.io.*;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.example.exeptions.FileCreationException;
import com.example.exeptions.FileProcessingException;
import com.example.model.FileData;
import com.example.model.FileDataRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import io.swagger.annotations.ApiParam;

@Service
public class FileService {

    @Autowired
    private FileDataRepository fileDataRepository;
    //private static List<FileData> files = new ArrayList<>();


    public ResponseEntity<byte[]> multupla(List<String> fileNames) {
        try {
            // поток который записывает содержимое файла в байты
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            // записывает данные в zip
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream);
            try {


                for (String fileName : fileNames) {

                    InputStream fileStream = download(fileName);

                    // Создает новый ZipEntry с указанным именем файла и добавляет его в zip-архив.
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOutputStream.putNextEntry(zipEntry);

                    // копирования содержимого файла (fileStream) в сжатый zip-поток (zipOutputStream).
                    IOUtils.copy(fileStream, zipOutputStream);
                    fileStream.close();
                    zipOutputStream.closeEntry();
                }
            } catch (IOException e) {
                throw new FileProcessingException("Ошибка обработки файла");

            }


            zipOutputStream.close();
            byte[] bytes = byteOutputStream.toByteArray();
            //Создается новый экземпляр HttpHeaders и добавляет в него заголовок Content-Disposition. Это делает файл доступным для загрузки с именем "files.zip".
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(bytes);


        } catch (IOException e) {
            throw new FileCreationException("Zip архив не может быть создан");
        }


    }


    public FileData save(@ApiParam(value = "Файл для сохранения") MultipartFile uploadedFile) throws IOException {

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FileData file = new FileData();
        file.setUploadDate(currentDateTime.format(formatter));
        file.setChangeDate(currentDateTime.format(formatter));
        String[] words = file.getFileName().split("\\.");
        file.setFileType(words[1]);
        file.setFileName(words[0]);

        file.setFileUrl("/files/download/" + file.getFileName());
        file.setFileSize(uploadedFile.getSize());
        file.setFileContent(uploadedFile.getBytes());


        FileData oldFile = fileDataRepository.findByFileName(uploadedFile.getOriginalFilename());
        if (oldFile != null) {
            fileDataRepository.delete(oldFile);
        }
        fileDataRepository.save(file);
        return file;
    }
    //Загрузка файла по имени

    public ResponseEntity<FileData> load(String fileName) throws IOException {

        FileData file = fileDataRepository.findByFileName(fileName);
        if (file != null) {

            return ResponseEntity.status(HttpStatus.OK).body(file);
        }
        throw new FileNotFoundException("Файла с именем '" + fileName + "' не существует.");
    }

    // удаление всех файлов

    public void deleteAll() {

        fileDataRepository.deleteAll();

    }

    //удаление файла по имени
    public boolean delete(String fileName) throws IOException {
        FileData foundFile = fileDataRepository.findByFileName(fileName);
        if (foundFile != null) {
            fileDataRepository.delete(foundFile);
            return true;
        }
        return false;
    }

    //Скачивание файла
    public ByteArrayInputStream download(String fileName) throws FileNotFoundException {


        FileData foundFile = fileDataRepository.findByFileName(fileName);
        if (foundFile != null) {
            byte[] fileContent = foundFile.getFileContent();
            return new ByteArrayInputStream(fileContent);
        }
        throw new FileNotFoundException("Файла с именем '" + fileName + "' не существует.");
    }






    public ResponseEntity<Object> loadAllFiltered(String name, LocalDateTime dateFrom, LocalDateTime dateTo, List<String> types) throws FileNotFoundException {
        Specification<FileData> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(fileDataHasName(name));
        }
        if (dateFrom != null) {
            spec = spec.and(fileDataHasDateFrom(dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and(fileDataHasDateTo(dateTo));
        }
        if (types != null && !types.isEmpty()) {
            spec = spec.and(fileDataHasTypes(types));
        }

        List<FileData> fileDataList = fileDataRepository.findAll(spec);

        if (fileDataList.isEmpty()) {
            throw new FileNotFoundException("Файла с введенными фильтрами не найден");
        }
        return ResponseEntity.ok().body(fileDataList);
    }

    private Specification<FileData> fileDataHasName(String name) {
        return (root, query, cb) -> cb.like(root.get("fileName"), "%" + name + "%");
    }

    private Specification<FileData> fileDataHasDateFrom(LocalDateTime dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("uploadDate").as(LocalDateTime.class), dateFrom);
    }

    private Specification<FileData> fileDataHasDateTo(LocalDateTime dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("uploadDate").as(LocalDateTime.class), dateTo);
    }

    private Specification<FileData> fileDataHasTypes(List<String> types) {
        return (root, query, cb) -> root.get("fileType").in(types);
    }

}
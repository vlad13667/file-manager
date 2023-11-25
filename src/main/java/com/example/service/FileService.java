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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ;
        FileData file = new FileData();
        file.setUploadDate(currentDateTime.format(formatter));
        file.setChangeDate(currentDateTime.format(formatter));
        file.setFileName(uploadedFile.getOriginalFilename());
        file.setFileType(uploadedFile.getContentType());
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
            file.setFileUrl("/files/download/" + file.getFileName());
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
       /* List<FileData> fileDataList = new ArrayList<>();
        List<FileData> allFiles = fileDataRepository.findAll();

        for (FileData file : allFiles) {
            boolean matchesName = name == null || file.getFileName().contains(name);
            LocalDateTime uploadDate = file.getUploadDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            boolean matchesDate = (dateFrom == null || uploadDate.isAfter(dateFrom) || uploadDate.isEqual(dateFrom))
                    && (dateTo == null || !uploadDate.isAfter(dateTo));
            boolean matchesType = types == null || types.contains(file.getFileType());

            if (matchesName && matchesDate && matchesType) {
                FileData fileDataCopy = new FileData();
                fileDataCopy.setUploadDate(file.getUploadDate());
                fileDataCopy.setChangeDate(file.getChangeDate());
                fileDataCopy.setFileName(file.getFileName());
                fileDataCopy.setFileType(file.getFileType());
                fileDataCopy.setFileSize(file.getFileSize());
                fileDataCopy.setFileUrl("/files/download/" + file.getFileName());
                fileDataList.add(fileDataCopy);
            }
        }

        if (fileDataList.isEmpty()) {
            throw new FileNotFoundException("Файла с введенными фильтрами не найден");
        }
        return ResponseEntity.ok().body(fileDataList);
    }

        */
        return null;
    }
}
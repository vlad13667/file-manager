package com.example.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.example.exeptions.FileCreationException;
import com.example.exeptions.FileProcessingException;
import com.example.model.FileData;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Service
@Api(value = "FileService", description = "Сервис для работы с файлами")
public class FileService {
    private static List<FileData> files = new ArrayList<>();


    public ResponseEntity<byte[]> multupla(List<String> fileNames) {
        try {
            // поток который записывает содержимое файла в байты
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            // записывает данные в zip
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream);
            try {


                for (String fileName : fileNames) {

                    InputStream fileStream = FileService.download(fileName);

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


        FileData file = new FileData();
        file.setUploadDate(new Date());
        file.setChangeDate(new Date());
        file.setFileName(uploadedFile.getOriginalFilename());
        file.setFileType(uploadedFile.getContentType());
        file.setFileSize(uploadedFile.getSize());
        file.setFileContent(uploadedFile.getBytes());


        FileData oldFile = null;
        for (FileData f : files) {
            if (f.getFileName().equals(uploadedFile.getOriginalFilename())) {
                oldFile = f;
                break;
            }
        }
        if (oldFile != null) {
            files.remove(oldFile);
        }
        files.add(file);
        return file;
    }
    //Загрузка файла по имени
    @ApiOperation(value = "Загрузить файл", response = FileData.class)
    public ResponseEntity<FileData> load(String fileName) throws IOException {

        for (FileData file : files) {
            if (file.getFileName().equals(fileName)) {
                file.setFileUrl("/files/download/" + file.getFileName());

                return ResponseEntity.status(HttpStatus.OK).body(file);
            }
        }
        throw new FileNotFoundException("Файла с именем '" + fileName + "' не существует.");
    }

    // удаление всех файлов
    @ApiOperation(value = "Удалить все файлы")
    public void deleteAll() {

        files.clear();

    }

    //удаление файла по имени
    public boolean delete(String fileName) throws IOException {
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

    //Скачивание файла
    public static ByteArrayInputStream download(String fileName) throws FileNotFoundException {


        FileData foundFile = null;
        for (FileData file : files) {
            if (file.getFileName().equals(fileName)) {
                foundFile = file;
                break;
            }
        }
        if (foundFile != null) {
            byte[] fileContent = foundFile.getFileContent();
            return new ByteArrayInputStream(fileContent);
        }


        throw new FileNotFoundException("Файла с именем '" + fileName + "' не существует.");
    }


    public ResponseEntity<Object> loadAllFiltered(String name, LocalDateTime dateFrom, LocalDateTime dateTo, List<String> types) throws FileNotFoundException {
        List<FileData> fileDataList = new ArrayList<>();

        for (FileData file : files) {
            // проверка на подстроку
            boolean matchesName = name == null || file.getFileName().contains(name);

            LocalDateTime uploadDate = file.getUploadDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            boolean matchesDate =
                    (dateFrom == null || uploadDate.isAfter(dateFrom) || uploadDate.isEqual(dateFrom)) &&
                            (dateTo == null || !uploadDate.isAfter(dateTo));

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
            throw new FileNotFoundException("Файла с ведеными фильтрами не найден");
        }
        return ResponseEntity.ok().body(fileDataList);
    }


}
package com.example.service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.example.model.FileData;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService{
    private static List<FileData> files = new ArrayList<>();





public static ResponseEntity<byte[]> multupla(List<String> fileNames)
{
    try {
        // поток который записывает содержимое файла в байты
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        // записывает данные в zip
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream);

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

        zipOutputStream.close();
        byte[] bytes = byteOutputStream.toByteArray();
        //Создается новый экземпляр HttpHeaders и добавляет в него заголовок Content-Disposition. Это делает файл доступным для загрузки с именем "files.zip".
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=files.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);

    } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }


    public static FileData save(MultipartFile uploadedFile) throws IOException {

        if (uploadedFile.getOriginalFilename() == "") {
            throw new IllegalArgumentException("Файл не указан");
        }

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
        throw new IllegalArgumentException("Файл "+ fileName + " не найден");
    }

        // удаление всех файлов
    public static void deleteAll() {

            files.clear();

    }

    //удаление файла по имени
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

    //Скачивание файла
    public static InputStream download(String fileName) throws IOException {
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
        throw new IllegalArgumentException("Файл " + fileName + " не найден");
    }


    public static List<FileData> loadAllFiltered(String name, LocalDateTime dateFrom, LocalDateTime dateTo, List<String> types) {
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
                fileDataCopy.setFileContent(file.getFileContent());

                fileDataList.add(fileDataCopy);
            }
        }

        if (fileDataList.isEmpty()){
            throw new IllegalArgumentException("Ни один файл не соответствует предоставленным фильтрам");
        }

        return fileDataList;
    }


}
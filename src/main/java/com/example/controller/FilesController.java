package com.example.controller;


import com.example.exeptions.FileInvalidTypeException;
import com.example.model.FileData;
import com.example.service.FileService;
import io.swagger.annotations.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
@RestController

public class FilesController {


    private final FileService fileService;

    // С помощью этого конструктора сервис будет автоматически инжектирован Spring'ом
    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    private static java.util.Arrays Arrays;
    private final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
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

    @ApiOperation(value = "Загрузика файла на сервер", response = ResponseEntity.class)
    @PostMapping("/files")

    public ResponseEntity<String> uploadFile(HttpServletRequest request, @RequestPart("file") MultipartFile uploadedFile) throws IOException {
        if (!ALLOWED_FILE_TYPES.contains(uploadedFile.getContentType())) {
            throw new FileInvalidTypeException("Ошибка: недопустимый тип файла");
        }
        FileData file = fileService.save(uploadedFile);


        return new ResponseEntity<>("Успешная загрузка файла " + file.getFileName(), HttpStatus.CREATED);

    }
    @ApiOperation(value = "Загрузите несколько файлов в формате zip", response = ResponseEntity.class, notes = "Укажите имена файлов для загрузки их в виде одного zip-файла.")
    @ApiImplicitParam(name = "fileNames", value = "List of File Names", required = true, dataType = "List", paramType = "query")
    @GetMapping("/files/download/multiple")
    public ResponseEntity<ByteArrayResource> downloadMultipleFiles(@RequestParam List<String> fileNames) {
        byte[] data = fileService.multupla(fileNames).getBody();
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @ApiOperation(value = "Получить подробную информацию о файле по имени", response = ResponseEntity.class)

    @GetMapping("/files/{fileName}")

    public ResponseEntity<FileData> getFileByName(@PathVariable String fileName) throws IOException {
        ResponseEntity<FileData> file = fileService.load(fileName);
        return file;
    }
    @ApiOperation(value = "Загрузить файла", response = ResponseEntity.class, notes = "Укажите имя файла для загрузки файла.")
    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) throws IOException {

        // Эта строка получает  файл как InputStream от  сервиса.

        InputStream file = fileService.download(fileName);
        //Устанавливаем contentType файла как MediaType.APPLICATION_OCTET_STREAM, чтобы файл мог быть прямо загружен
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + fileName)
                .body(new InputStreamResource(file));


    }

    @ApiOperation(value = "Получение списка  файлов с помощью фильтров", response = ResponseEntity.class, notes = "Могут быть применены дополнительные фильтры.")
    @GetMapping("/files")
    public ResponseEntity<?> getFileNames(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) List<String> types) throws IOException {

        return fileService.loadAllFiltered(name, dateFrom, dateTo, types);

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
    @ApiOperation(value = "Удаление всех файлов с сервера", response = ResponseEntity.class)
    @DeleteMapping("/files")
    public ResponseEntity<?> deleteAllFiles() {
        fileService.deleteAll();
        return new ResponseEntity<>("Все файлы удалены", HttpStatus.OK);

    }

    @ApiOperation(value = "Удаление файла по имени с сервера", response = ResponseEntity.class, notes = "Укажите имя файла, чтобы удалить файл..")
    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) throws IOException {
        boolean isDeleted = fileService.delete(fileName);

        if (isDeleted) {
            return ResponseEntity.ok("Файл с именем '" + fileName + "' удалён.");
        } else {
            throw new FileNotFoundException("Файла с именем '" + fileName + "' несуществует.");
        }
    }


}
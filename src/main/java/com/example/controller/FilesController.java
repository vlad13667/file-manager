package com.example.controller;

import java.io.IOException;//исключения
import java.util.List;//коллекция List
import com.example.model.FileData;
import com.example.model.UploadResponseMessage;
import com.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;//внедрение зависимостей spring
import org.springframework.http.HttpStatus;//статусы http-ответов
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;//обработка http delete-запроса
import org.springframework.web.bind.annotation.GetMapping;// обработка HTTP GET-запросов
import org.springframework.web.bind.annotation.PathVariable;//извлечения переменных из URL в RESTful
import org.springframework.web.bind.annotation.PostMapping;//обработки HTTP POST-запросов
import org.springframework.web.bind.annotation.RequestParam;//извлечения параметров запроса из URL
import org.springframework.web.bind.annotation.RestController;//обрабатывает HTTP-запросы и возвращает данные в формате JSON
import org.springframework.web.multipart.MultipartFile;//для работы с загружаемыми файлами в Spring


@RestController

public class FilesController {
    private final FileService fileService;

    @Autowired
    public FilesController(FileService fileService)
    {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<UploadResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.save(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UploadResponseMessage("Файл успешно загружен: " + file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new UploadResponseMessage("Не удалось загрузить файл: " + file.getOriginalFilename() + "!"));
        }
    }

    @GetMapping ("/files")
    public ResponseEntity<List<FileData>> getListFiles() throws IOException {
        //преобразовываю список файлов в поток где к каждому файлу присваиваеться его информация
        final List<FileData> FileData = fileService.loadAll();
        return ResponseEntity.status(HttpStatus.OK).body(FileData);
    }

    @GetMapping ("/files/{filename}")
    public FileData load (@PathVariable(name = "filename") String filename) throws IOException {
        final FileData file = fileService.load(filename);
        return ResponseEntity.status(HttpStatus.OK).body(file).getBody();
    }

    @DeleteMapping ("/files")
      public void deleteAll() {
          fileService.deleteAll();
   }
    @DeleteMapping("/files/{filename}")
    public  ResponseEntity<?> deleted(@PathVariable(name = "filename") String filename) throws IOException {
         final boolean deleted = fileService.delete(filename);
        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
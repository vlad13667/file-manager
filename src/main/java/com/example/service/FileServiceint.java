package com.example.service;

import com.example.model.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileServiceint {
    void init();
    void save(MultipartFile file);
    FileData load(String filename) throws IOException;
    void deleteAll();
    boolean delete(String fileName) throws IOException;
    List<FileData> loadAll() throws IOException;

}

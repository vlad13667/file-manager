package com.example.service;

import com.example.model.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileServiceint {
    void init();

    static FileData save(MultipartFile file) throws IOException {
        return null;
    }

    FileData load(String filename) throws IOException;
    void deleteAll();
    boolean delete(String fileName) throws IOException;
    List<String> loadAll() throws IOException;

}

package com.example.test;

import com.example.model.FileData;
import com.example.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class test3 {
    FileService fileService = Mockito.mock(FileService.class);

    private List<FileData> files = new ArrayList<>();
    private static final String TEST_FILE_NAME = "test_file";

    @BeforeEach
    public void setUp() {
        FileData fileData = new FileData();
        fileData.setFileName(TEST_FILE_NAME);

        files = new ArrayList<>();
        files.add(fileData);
    }

    @Test
    public void testDeleteExistingFile() throws IOException {
        when(fileService.delete("test_file")).thenReturn(true);

        assertTrue(fileService.delete("test_file"));
        assertEquals(1, files.size());
    }

    @Test
    public void testDeleteNonExistingFile() throws IOException {
        when(fileService.delete("nonExistingFile")).thenReturn(false);

        assertFalse(fileService.delete("nonExistingFile"));
        assertEquals(1, files.size());
    }
}
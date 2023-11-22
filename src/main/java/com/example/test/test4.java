package com.example.test;

import com.example.model.FileData;
import com.example.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class test4 {
    private static final String TEST_FILE_NAME = "test_file";
    private static final String TEST_FILE_URL = "/files/download/" + TEST_FILE_NAME;

    private List<FileData> files = new ArrayList<>();

    @Spy
    private List<FileData> spyFiles = files;
    private FileService yourClass;

    @BeforeEach
    void setUp() {
        FileData fileData = new FileData();
        byte[] content = new byte[] {10, 20, 30, 40, 50};
        fileData.setFileName(TEST_FILE_NAME);

        spyFiles = new ArrayList<>(); // Инициализируйте список здесь
        spyFiles.add(fileData);
    }

    @Test
    void downloadShouldReturnCorrectStreamWhenFileExists() throws FileNotFoundException {
        ByteArrayInputStream actualResult = yourClass.download("test_file");
        byte[] actualBytes = new byte[5];
        actualResult.read(actualBytes, 0, 5);
        byte[] expectedBytes = new byte[] {10, 20, 30, 40, 50};
        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    void downloadShouldThrowExceptionWhenFileDoesNotExist() {
        assertThrows(FileNotFoundException.class, () -> yourClass.download("nonexistentFile"));
    }
}

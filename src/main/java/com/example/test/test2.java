package com.example.test;

import com.example.model.FileData;
import com.example.service.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class test2 {
    @Mock
    private FileService fileService; // обратите внимание на эту строку

    private static final String TEST_FILE_NAME = "test_file";
    private static final String TEST_FILE_URL = "/files/download/" + TEST_FILE_NAME;

    private List<FileData> files = new ArrayList<>();

    @Spy
    private List<FileData> spyFiles = files;

    @Before
    public void setup() throws IOException {
        // Arrange
        FileData fileData = new FileData();
        fileData.setFileName(TEST_FILE_NAME);

        spyFiles = new ArrayList<>(); // Инициализируйте список здесь
        spyFiles.add(fileData);

        doReturn(new ResponseEntity<>(fileData, HttpStatus.OK)).when(fileService).load(TEST_FILE_NAME);
        doThrow(new FileNotFoundException()).when(fileService).load("non_exist_file");
    }

    @Test
    public void testLoad_existingFile_returnsFileData() throws IOException {
        // Act
        ResponseEntity<FileData> result = fileService.load(TEST_FILE_NAME);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(TEST_FILE_NAME, result.getBody().getFileName());
        // assertEquals(TEST_FILE_URL, result.getBody().getFileUrl());
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoad_nonExistingFile_throwsFileNotFoundException() throws IOException {
        // Act
        ResponseEntity<FileData> result = fileService.load("non_exist_file");
    }
}
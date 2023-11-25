package com.example.test;

import com.example.model.FileData;
import com.example.service.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Date;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class test1 {
    private static final String TEST_FILE_NAME = "testfile.txt";
    private static final String TEST_FILE_TYPE = "text/plain";
    private static final byte[] TEST_FILE_CONTENT = "content".getBytes();
    private FileService fileService;

    @Before
    public void setup() {
        fileService = new FileService();
    }

    @Test
    public void testSave_FileNew_SavesSuccessfully() throws IOException {
        // тест на сохранение файла
        MockMultipartFile mockFile = new MockMultipartFile(TEST_FILE_NAME, TEST_FILE_NAME, TEST_FILE_TYPE, TEST_FILE_CONTENT);

        FileData fileData = fileService.save(mockFile);

        assertNotNull(fileData);
        assertEquals(TEST_FILE_NAME, fileData.getFileName());
        assertEquals(TEST_FILE_TYPE, fileData.getFileType());
        assertArrayEquals(TEST_FILE_CONTENT, fileData.getFileContent());
       // assertTrue(fileData.getUploadDate().getTime() - new Date().getTime() < 1000);
    }

    @Test
    public void testSave_FileExist_RemoveOldVersion() throws IOException {
        // Создаем и сохраняем файл
        MockMultipartFile originalFile = new MockMultipartFile(TEST_FILE_NAME, TEST_FILE_NAME, TEST_FILE_TYPE, TEST_FILE_CONTENT);
        fileService.save(originalFile);
        // Создаем новую версию файла
        byte[] newContent = "new content".getBytes();
        MockMultipartFile newFile = new MockMultipartFile(TEST_FILE_NAME, TEST_FILE_NAME, TEST_FILE_TYPE, newContent);
        FileData newFileData = fileService.save(newFile);

        // Проверяем, что старая версия файла была заменена
        assertArrayEquals(newContent, newFileData.getFileContent());
    }
}


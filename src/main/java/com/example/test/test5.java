package com.example.test;

import com.example.model.FileData;
import com.example.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class test5 {
    private FileService fileService;
    private List<FileData> files;

    @Spy
    private List<FileData> spyFiles = files;
    private static final String TEST_FILE_NAME = "test_file";
    private FileService yourClass;


    @BeforeEach
    public void setup() {
        FileData fileData = new FileData();
        byte[] content = new byte[]{10, 20, 30, 40, 50};
        fileData.setFileName(TEST_FILE_NAME);

        spyFiles = new ArrayList<>(); // Инициализируйте список здесь
        spyFiles.add(fileData);
    }

    @Test
    public void testLoadAllFiltered() throws FileNotFoundException {
        String name = "test";
        LocalDateTime dateFrom = LocalDateTime.now().minusDays(1);
        LocalDateTime dateTo = LocalDateTime.now();
        List<String> types = Arrays.asList("type1", "type2");

        ResponseEntity<Object> result = fileService.loadAllFiltered(name, dateFrom, dateTo, types);

        // здесь добавьте проверки (Assertions) связанные с вашим кейсом. Это может включать проверку наличия определенных файлов, отсутствие других, корректности поля fileUrl и т.д.

        // Пример:
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());

        List<FileData> fileDataList = (List<FileData>) result.getBody();
        assertFalse(fileDataList.isEmpty());

        // добавить другие проверки...
    }

}
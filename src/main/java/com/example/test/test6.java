package com.example.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.controller.FilesController;
import com.example.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;

import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.List;
@RunWith(SpringRunner.class)
@WebMvcTest
public class test6 {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    public void testDownloadMultipleFiles() throws Exception {
        // подготовьте тестовые данные
        List<String> fileNames = Arrays.asList("file1.txt", "file2.txt");
        byte[] testData = new byte[]{1, 2, 3, 4, 5};

        // настройте поведение fileService
        when(fileService.multupla(any(List.class))).thenReturn(ResponseEntity.ok(testData));

        // получите фактический ответ
        MvcResult result = mockMvc.perform(get("/files/download/multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fileNames", String.valueOf(fileNames)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=files.zip"))
                .andExpect((ResultMatcher) content().bytes(testData))
                .andReturn();

        // проверьте body ответа
        //String responseBody = result.getResponse().getContentAsString();
       // assertThat(responseBody, equalTo(testData));

        // проверьте был ли вызван метод `multupla` с правильными аргументами
        verify(fileService, times(1)).multupla(fileNames);
    }
}
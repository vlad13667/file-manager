package com.example.model;

import javax.persistence.*;

@Entity
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "upload_date")
    private String uploadDate;

    @Column(name = "change_date")
    private String changeDate;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_content")
    private byte[] fileContent;

    public String getFileType()
    {
        return fileType;
    }
    public void setFileType(String typeFile)
    {
        this.fileType = typeFile;
    }

    public String getFileName() {
        return fileName;
    }
    public String getUploadDate()
    {
        return uploadDate;
    }
    public void setUploadDate(String uploadDate)
    {
        this.uploadDate = uploadDate;
    }
    public String getChangeDate()
    {
        return changeDate;
    }
    public void setChangeDate(String changeDate)
    {
        this.changeDate = changeDate;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
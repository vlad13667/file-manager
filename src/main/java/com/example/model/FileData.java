package com.example.model;

import java.util.Date;

public class FileData {

    private String fileName;
    private Date uploadDate;
    private Date changeDate;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
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
    public Date getUploadDate()
    {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate)
    {
        this.uploadDate = uploadDate;
    }
    public Date getChangeDate()
    {
        return changeDate;
    }
    public void setChangeDate(Date changeDate)
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
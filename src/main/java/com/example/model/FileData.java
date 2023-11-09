package com.example.model;

public class FileData {

    private String fileName;
    private String uploadDate;
    private String changeDate;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
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
}
package com.example.model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileDataRepository extends JpaRepository<FileData, Integer>, JpaSpecificationExecutor<FileData> {
    FileData findByFileName(String fileName);

}


package com.example.model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileData, Integer> {
    FileData findByFileName(String fileName);

}


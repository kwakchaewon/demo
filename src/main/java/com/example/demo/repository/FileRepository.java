package com.example.demo.repository;

import com.example.demo.entity.FileTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileTable, Long> {
//    List<FileTable> saveAll(List<FileTable> files);
}

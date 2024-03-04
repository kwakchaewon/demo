package com.example.demo.repository;

import com.example.demo.dto.request.FileRequestForm;
import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> saveAll(List<File> files);
}

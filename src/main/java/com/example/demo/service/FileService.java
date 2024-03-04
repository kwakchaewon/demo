package com.example.demo.service;

import com.example.demo.dto.request.FileRequestForm;
import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import com.example.demo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Transactional
    public void saveFiles(Board board, List<FileRequestForm> _files){
        if(CollectionUtils.isEmpty(_files)){
            return;
        }

        List<File> files = new ArrayList<>();

        for(FileRequestForm file: _files){
            files.add(file.toEntity(board));
        }
        fileRepository.saveAll(files);
    }
}

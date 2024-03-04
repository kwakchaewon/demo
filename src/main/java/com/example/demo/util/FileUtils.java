package com.example.demo.util;

import com.example.demo.dto.request.FileRequestForm;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    /**
     *  DB 저장할 파일 정보 List 반환
     */
    private final String uploadPath = Paths.get("C:", "dev", "upload-files").toString();

    public List<FileRequestForm> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<FileRequestForm> files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles){
            if(multipartFile.isEmpty()){
                continue;
            }
            files.add(uploadFile(multipartFile));
        }
        return files;
    }

    /**
     * 단일 파일 업로드
     */
    public FileRequestForm uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()){
            return null;
        }

        String savedName = generateSaveFilename(multipartFile.getOriginalFilename());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")).toString();
        String uploadPath = getUploadPath(today) + File.separator + savedName;
        File uploadFile = new File(uploadPath);

        try {
            multipartFile.transferTo(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileRequestForm.builder()
                .originalName(multipartFile.getOriginalFilename())
                .savedName(savedName)
                .size(multipartFile.getSize())
                .build();
    }

    /**
     * 저장 파일명 생성
     */
    private String generateSaveFilename(String fileName){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(fileName);
        return uuid + "." + extension;
    }

    /**
     * 업로드 경로 반환1
     */
    private String getUploadPath() {
        return makeDirectories(uploadPath);
    }

    /**
     * 업로드 경로 반환2
     */
    private String getUploadPath(final String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    /**
     * 업로드 디렉토리 생성
     */
    private String makeDirectories(final String path) {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        return dir.getPath();
    }
}

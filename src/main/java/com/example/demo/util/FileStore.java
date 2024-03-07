package com.example.demo.util;

import com.example.demo.dto.UploadFileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStore {
    @Value("${FILE_SAVED_DIR}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + "/" + filename;
    }

    public String savedFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }

        // 작성자가 업로드한 파일명 -> 서버 내부에서 관리하는 파일명
        String originalFilename = multipartFile.getOriginalFilename();

        // 파일명을 중복되지 않게끔 UUID로 정하고 ".확장자"는 그대로
        String savedFilename = this.getSavedFileName(originalFilename);

        // 파일을 저장하는 부분 -> 파일경로 + storeFilename 에 저장
//        multipartFile.transferTo(new File(getFullPath(savedFilename)));

        // 저장하려는 경로에 폴더가 없으면 생성
        if (!Files.exists(Paths.get(fileDir))){
            try {
                // 폴더 생성
                Files.createDirectories(Paths.get(fileDir));
                System.out.println("폴더가 생성되었습니다.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        multipartFile.transferTo(new File(this.getFullPath(savedFilename)));

        return savedFilename;
    }

    private String getSavedFileName(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos + 1);
        return UUID.randomUUID() + "." + ext;
    }

    // 이미지 파일 여부 확인 메서드
    public boolean isImage(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();

        return ext.equals("jpg") || ext.equals("jpeg") ||
                ext.equals("png") || ext.equals("gif") ||
                ext.equals("bmp") || ext.equals("tiff");
    }

}

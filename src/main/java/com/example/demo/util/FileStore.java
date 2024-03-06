package com.example.demo.util;

import com.example.demo.dto.UploadFileDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {
    // 루트 경로
    private final String rootPath = System.getProperty("user.dir");

    // 프로젝트 루트 경로 files 디렉토리
    private final String fileDir = rootPath + "/files/";

    public String getFullPath(String filename) { return fileDir + filename; }

    public String savedFile(MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty()) {
            return null;
        }

        // 작성자가 업로드한 파일명 -> 서버 내부에서 관리하는 파일명
        String originalFilename = multipartFile.getOriginalFilename();

        // 파일명을 중복되지 않게끔 UUID로 정하고 ".확장자"는 그대로
        String savedFilename = this.getSavedFileName(originalFilename);

        // 파일을 저장하는 부분 -> 파일경로 + storeFilename 에 저장
        multipartFile.transferTo(new File(getFullPath(savedFilename)));

        return savedFilename;
    }

    private String getSavedFileName(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos + 1);
        return UUID.randomUUID()+"." +ext;
    }


}

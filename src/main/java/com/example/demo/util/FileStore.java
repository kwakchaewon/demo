package com.example.demo.util;
import com.example.demo.util.exception.Constants;
import com.example.demo.util.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Component
public class FileStore {
    private final String fileDir = System.getProperty("user.dir") + "/files";

    public String getFullPath(String filename) {
        return fileDir + "/" + filename;
    }

    /**
     * 파일 저장
     * @param multipartFile
     * @return savedFilename: UUID 기반 파일명
     * @throws IOException
     */
    public String savedFile(Optional<MultipartFile> multipartFile) throws IOException {

        // 1. 파일이 없다면 null 반환
        if (!multipartFile.isPresent()) {
            return null;
        }

        // 2.1 originalFilename: 원본 파일명

        String originalFilename = multipartFile.get().getOriginalFilename();

        // 2.2 savedFilename: UUID 기반 파일명
        String savedFilename = this.getSavedFileName(originalFilename);

        // 3. 저장 경로 폴더 생성 메서드
        if (!Files.exists(Paths.get(fileDir))) {
            Files.createDirectories(Paths.get(fileDir));
            System.out.println("폴더가 생성되었습니다.");
        }

        // 4. 로컬 파일 저장
        multipartFile.get().transferTo(new File(this.getFullPath(savedFilename)));
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

    public void deleteFile(String savedFileName) throws CustomException {
        File file = new File(this.getFullPath(savedFileName));
        try {
            file.delete();
        } catch (Exception e) {
            throw new CustomException(HttpStatus.NOT_FOUND, Constants.ExceptionClass.FILE_NOTFOUND);
        }
    }

}

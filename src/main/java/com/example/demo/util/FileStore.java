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

/**
 * 파일 저장과 관련된 util 메서드를 저장하는 클래스
 * 파일저장, 이미지 파일여부 확인, 파일 삭제, 파일 디렉토리 반환 기능
 */
@Component
public class FileStore {
    private final String fileDir = System.getProperty("user.dir") + "/files";

    public String getFullPath(String filename) {
        return fileDir + "/" + filename;
    }

    /**
     * 파일 저장
     * @return savedFilename: UUID 기반 파일명
     */
    public String savedFile(Optional<MultipartFile> multipartFile) throws IOException {

        // 1. 파일이 없다면 null 반환
        if (!multipartFile.isPresent()) {
            return null;
        }

        // originalFilename: 원본 파일명
        // savedFilename: UUID 기반 파일명
        String originalFilename = multipartFile.get().getOriginalFilename();
        String savedFilename = this.getSavedFileName(originalFilename);

        // 2. 저장 경로 폴더 생성
        if (!Files.exists(Paths.get(fileDir))) {
            try {
                Files.createDirectories(Paths.get(fileDir));
            }catch (IOException e){
                System.out.println("e = " + e);
                throw new IOException("");
            }
            System.out.println("경로 파일 생성에 실패했습니다.");
        }

        // 3. 로컬 파일 저장
        try {
            multipartFile.get().transferTo(new File(this.getFullPath(savedFilename)));
        }catch (IOException e){
            System.out.println("e = " + e);
            throw new IOException("디렉토리 저장에 실패했습니다.");
        }
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
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ExceptionClass.FILE_NOTFOUND);
        }
    }

}

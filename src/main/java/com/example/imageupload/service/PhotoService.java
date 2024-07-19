package com.example.imageupload.service;

import com.example.imageupload.dto.StateResponse;
import com.example.imageupload.entity.Member;
import com.example.imageupload.entity.Photo;
import com.example.imageupload.repository.MemberRepository;
import com.example.imageupload.repository.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private static final String UPLOAD_DIR = "./uploads/";

    private final PhotoRepository photoRepository;
    private final MemberRepository memberRepository;

    public StateResponse uploadImage(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Check file type
                String fileType = file.getContentType();
                if (!("image/png".equals(fileType) || "image/jpeg".equals(fileType))) {
                    return new StateResponse(false);
                }

                // Create upload directory if not exists
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Save the file with a unique name
                String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
                String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
                Path path = Paths.get(UPLOAD_DIR + newFileName);
                Files.write(path, file.getBytes());

                // Find the member
                Member member = memberRepository.findById(1L)
                        .orElseThrow(EntityNotFoundException::new);

                // Save photo information in the database
                Photo photo = Photo.builder()
                        .keyValue(newFileName)  // 저장된 파일명을 저장
                        .originName(file.getOriginalFilename())
                        .member(member)
                        .build();

                photoRepository.save(photo);

                // Return file information
                return new StateResponse(true);
            } catch (IOException e) {
                e.printStackTrace();
                return new StateResponse(false);
            }
        } else {
            return new StateResponse(false);
        }
    }

    public List<Photo> getImages() {
        return photoRepository.findAll();
    }
}

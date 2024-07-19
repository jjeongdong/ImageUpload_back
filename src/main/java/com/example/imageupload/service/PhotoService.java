package com.example.imageupload.service;

import com.example.imageupload.dto.StateResponse;
import com.example.imageupload.entity.Member;
import com.example.imageupload.entity.Photo;
import com.example.imageupload.repository.MemberRepository;
import com.example.imageupload.repository.PhotoRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MemberRepository memberRepository;
    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    public StateResponse uploadImage(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Check file type
                String fileType = file.getContentType();
                if (!("image/png".equals(fileType) || "image/jpeg".equals(fileType))) {
                    return new StateResponse(false);
                }

                // Save the file with a unique name
                String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
                String newFileName = UUID.randomUUID().toString() + "." + fileExtension;

                // Find the member
                Member member = memberRepository.findById(1L)
                        .orElseThrow(EntityNotFoundException::new);

                // S3에 파일 업로드
                S3Resource s3Resource = s3Template.upload(bucketName, "raw/" + newFileName, file.getInputStream(), ObjectMetadata.builder().contentType(fileExtension).build());

                Photo photo = Photo.builder()
                        .keyValue(s3Resource.getURL().toString())
                        .originName(file.getOriginalFilename())
                        .member(member)
                        .build();

                photoRepository.save(photo);

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

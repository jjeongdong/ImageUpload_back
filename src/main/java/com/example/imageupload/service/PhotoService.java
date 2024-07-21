package com.example.imageupload.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.imageupload.dto.res.PreSignedUrlResponse;
import com.example.imageupload.dto.res.StateResponse;
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
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final MemberRepository memberRepository;
    private final S3Template s3Template;
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

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
                String newFileName = UUID.randomUUID() + "." + fileExtension;

                // Find the member
                Member member = memberRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

                // S3에 파일 업로드
                S3Resource s3Resource = s3Template.upload(bucketName, "raw/" + newFileName, file.getInputStream(), ObjectMetadata.builder().contentType(fileExtension).build());

                Photo photo = Photo.builder().keyValue(s3Resource.getFilename()).imgUrl(s3Resource.getURL().toString()).originName(file.getOriginalFilename()).member(member).build();

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

    public StateResponse deleteImage(Long photoId) {
        try {
            Photo photo = photoRepository.findById(photoId).orElseThrow(EntityNotFoundException::new);

            // Delete the object from S3
            s3Template.deleteObject(bucketName, "raw/" + photo.getKeyValue());
            s3Template.deleteObject(bucketName, "w140/" + photo.getKeyValue());
            s3Template.deleteObject(bucketName, "w600/" + photo.getKeyValue());

            // Delete the photo record from the database
            photoRepository.delete(photo);

            return new StateResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new StateResponse(false);
        }
    }

    /**
     * presigned url 발급
     *
     * @param prefix           버킷 디렉토리 이름
     * @param originalFilename 클라이언트가 전달한 파일명 파라미터
     * @return presigned url
     */
    public PreSignedUrlResponse getPreSignedUrl(String prefix, String originalFilename) {
        String fileName = createPath(prefix, originalFilename);
        String keyValue = fileName.split("/")[1];
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucketName, fileName);
        URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        String imgUrl = generateFileAccessUrl(fileName);

        Member member = memberRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        Photo photo = Photo.builder()
                .imgUrl(imgUrl)
                .originName(originalFilename)
                .keyValue(keyValue)
                .member(member)
                .build();

        photoRepository.save(photo);
        return PreSignedUrlResponse.toPreSignedUrlResponse(presignedUrl.toString());
    }

    /**
     * 파일 업로드용(PUT) presigned url 생성
     *
     * @param bucket   버킷 이름
     * @param fileName S3 업로드용 파일 이름
     * @return presigned url
     */
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName).withMethod(HttpMethod.PUT).withExpiration(getPreSignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    /**
     * presigned url 유효 기간 설정
     *
     * @return 유효기간
     */
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /**
     * 파일 고유 ID를 생성
     *
     * @return 36자리의 UUID
     */
    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 파일의 전체 경로를 생성
     *
     * @param prefix   디렉토리 경로
     * @param fileName 파일 이름
     * @return 파일의 전체 경로
     */
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + fileName);
    }

    /**
     * 파일의 접근 URL을 생성
     *
     * @param fileName S3에 저장된 파일 이름
     * @return 파일 접근 URL
     */
    private String generateFileAccessUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}

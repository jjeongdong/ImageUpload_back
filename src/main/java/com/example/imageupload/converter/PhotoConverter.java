package com.example.imageupload.converter;

import com.example.imageupload.dto.res.PhotoDownloadUrlListResponse;
import com.example.imageupload.dto.res.PhotoListResponseDTO;
import com.example.imageupload.dto.res.PhotoResponseDTO;
import com.example.imageupload.entity.Photo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class PhotoConverter {

    public static PhotoResponseDTO toPhotoResponseDTO(Photo photo) {
        return PhotoResponseDTO.builder()
                .id(photo.getId())
                .imageName(photo.getKeyValue())
                .imageUrl(photo.getImgUrl())
                .build();
    }

    public static PhotoListResponseDTO toPhotoListResponseDTO(Page<Photo> photoList) {
        List<PhotoResponseDTO> photoResponseDTOList = photoList.stream()
                .map(PhotoConverter::toPhotoResponseDTO).collect(Collectors.toList());

        return PhotoListResponseDTO.builder()
                .isLast(photoList.isLast())
                .isFirst(photoList.isFirst())
                .totalPage(photoList.getTotalPages())
                .totalElements(photoList.getTotalElements())
                .listSize(photoResponseDTOList.size())
                .missionList(photoResponseDTOList)
                .build();
    }

    public static PhotoDownloadUrlListResponse toPhotoDownloadUrlListResponse(List<Long> photoIdList) {
        List<String> downloadUrlList = photoIdList.stream()
                .map(photoId -> "http://localhost:8080/images/download/" + photoId)
                .collect(Collectors.toList());

        return PhotoDownloadUrlListResponse.builder().photoDownloadUrlList(downloadUrlList).build();
    }
}

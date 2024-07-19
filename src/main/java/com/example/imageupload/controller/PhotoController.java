package com.example.imageupload.controller;

import com.example.imageupload.dto.PhotoGetResponse;
import com.example.imageupload.dto.StateResponse;
import com.example.imageupload.entity.Photo;
import com.example.imageupload.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/images")
    public StateResponse uploadImage(@RequestParam("image") MultipartFile file) {
        return photoService.uploadImage(file);
    }

    @GetMapping("/images")
    public List<PhotoGetResponse> getImages() {
        return photoService.getImages().stream()
                .map(PhotoGetResponse::toPhotoGetResponse)
                .collect(Collectors.toList());
    }
}

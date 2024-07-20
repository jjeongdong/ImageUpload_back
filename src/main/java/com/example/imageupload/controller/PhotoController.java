package com.example.imageupload.controller;

import com.example.imageupload.dto.req.PreSignedUrlRequest;
import com.example.imageupload.dto.res.PhotoGetResponse;
import com.example.imageupload.dto.res.PreSignedUrlResponse;
import com.example.imageupload.dto.res.StateResponse;
import com.example.imageupload.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/images")
@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public StateResponse uploadPhoto(@RequestParam("image") MultipartFile file) {
        return photoService.uploadImage(file);
    }

    @GetMapping
    public List<PhotoGetResponse> getPhotoList() {
        return photoService.getImages().stream()
                .map(PhotoGetResponse::toPhotoGetResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public StateResponse deletePhoto(@PathVariable Long id) {
        return photoService.deleteImage(id);
    }

    @PostMapping("/preSignedUrl")
    public List<PreSignedUrlResponse> getPreSignedUrl(@RequestBody List<PreSignedUrlRequest> preSignedUrlRequestList) {
        return preSignedUrlRequestList.stream()
                .map(preSignedUrlRequest -> photoService.getPreSignedUrl(preSignedUrlRequest.getPrefix(), preSignedUrlRequest.getImageName()))
                .collect(Collectors.toList());
    }
}

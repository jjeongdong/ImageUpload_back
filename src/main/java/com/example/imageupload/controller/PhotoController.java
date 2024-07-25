package com.example.imageupload.controller;

import com.example.imageupload.dto.req.PreSignedUrlRequest;
import com.example.imageupload.dto.res.PhotoGetResponse;
import com.example.imageupload.dto.res.PreSignedUrlListResponse;
import com.example.imageupload.dto.res.PreSignedUrlResponse;
import com.example.imageupload.dto.res.StateResponse;
import com.example.imageupload.entity.Photo;
import com.example.imageupload.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/images")
@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public ResponseEntity<StateResponse> uploadPhoto(@RequestParam("image") MultipartFile file) {
        StateResponse stateResponse = photoService.uploadPhoto(file);

        return ResponseEntity.ok().body(stateResponse);
    }

    @GetMapping
    public ResponseEntity<List<PhotoGetResponse>> getPhotoList() {
        List<PhotoGetResponse> photoList = photoService.getPhotoList().stream()
                .map(PhotoGetResponse::toPhotoGetResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(photoList);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<StateResponse> deletePhoto(@PathVariable Long photoId) {
        StateResponse stateResponse = photoService.deletePhoto(photoId);

        return ResponseEntity.ok().body(stateResponse);
    }

    @PostMapping("/preSignedUrl")
    public ResponseEntity<PreSignedUrlListResponse> getPreSignedUrl(@RequestBody PreSignedUrlRequest preSignedUrlRequestList) {
        List<String> preSignedUrlList = preSignedUrlRequestList.getImageNameList().stream()
                .map(preSignedUrlRequest -> photoService.getPreSignedUrl("raw", preSignedUrlRequest).getPreSignedUrl())
                .collect(Collectors.toList());

        PreSignedUrlListResponse preSignedUrlListResponse = new PreSignedUrlListResponse(preSignedUrlList);
        return ResponseEntity.ok().body(preSignedUrlListResponse);
    }

    @GetMapping("/download/{photoId}")
    public ResponseEntity<ByteArrayResource> downloadPhoto(@PathVariable Long photoId) throws IOException {
        Photo photo = photoService.getPhoto(photoId);
        String downloadImageName = photo.getKeyValue();
        byte[] bytes = photoService.downloadPhoto(photoId);
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .contentLength(bytes.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + downloadImageName + "\"")
                .body(byteArrayResource);
    }
}

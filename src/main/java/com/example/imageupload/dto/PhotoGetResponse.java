package com.example.imageupload.dto;

import com.example.imageupload.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoGetResponse {

    private String key;

    private String originName;

    public static PhotoGetResponse toPhotoGetResponse(Photo photo) {
        return PhotoGetResponse.builder()
                .key(photo.getKeyValue())
                .originName(photo.getOriginName())
                .build();
    }
}

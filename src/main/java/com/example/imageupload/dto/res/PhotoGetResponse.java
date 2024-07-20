package com.example.imageupload.dto.res;

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

    private Long id;

    private String key;

    private String imgUrl;

    private String originName;

    public static PhotoGetResponse toPhotoGetResponse(Photo photo) {
        return PhotoGetResponse.builder()
                .id(photo.getId())
                .key(photo.getKeyValue())
                .imgUrl(photo.getImgUrl())
                .originName(photo.getOriginName())
                .build();
    }
}

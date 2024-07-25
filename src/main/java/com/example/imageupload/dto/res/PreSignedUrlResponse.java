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
public class PreSignedUrlResponse {

    private String preSignedUrl;

    public static PreSignedUrlResponse toPreSignedUrlResponse(String preSignedUrl) {
        return PreSignedUrlResponse.builder()
                .preSignedUrl(preSignedUrl)
                .build();
    }
}

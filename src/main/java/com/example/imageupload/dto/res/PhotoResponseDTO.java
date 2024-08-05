package com.example.imageupload.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResponseDTO {

    private Long id;
    private String imageName;
    private String imageUrl;
}

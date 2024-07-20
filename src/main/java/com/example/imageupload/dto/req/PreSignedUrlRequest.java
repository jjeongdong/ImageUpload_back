package com.example.imageupload.dto.req;

import lombok.Getter;

@Getter
public class PreSignedUrlRequest {

    private String prefix;

    private String imageName;
}

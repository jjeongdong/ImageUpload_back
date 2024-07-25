package com.example.imageupload.dto.req;

import lombok.Getter;

import java.util.List;

@Getter
public class PreSignedUrlRequest {

    private List<String> imageNameList;
}

# 프리사인드 URL을 이용한 이미지 업로드 및 리사이징 서비스

이 프로젝트는 프리사인드 URL을 생성하여 클라이언트가 이미지를 업로드할 수 있도록 하고, 업로드된 이미지를 AWS Lambda를 통해 자동으로 리사이징하는 Spring Boot 기반의 서비스입니다.

## 동작 방식

![image](https://github.com/user-attachments/assets/28c4136e-cf6d-4119-8161-66d28bd94562)


1. **프리사인드 URL 요청**
    - 클라이언트는 업로드할 이미지의 정보(예: 이미지 이름, 경로 등)를 포함한 요청을 서버에 보냅니다.
      
    - 이 요청은 `/preSignedUrl` 엔드포인트로 POST 요청을 통해 전송됩니다.
    
    ```java
    @PostMapping("/preSignedUrl")
    public ResponseEntity<List<PreSignedUrlResponse>> getPreSignedUrl(@RequestBody List<PreSignedUrlRequest> preSignedUrlRequestList) {
        List<PreSignedUrlResponse> preSignedUrlList = preSignedUrlRequestList.stream()
                .map(preSignedUrlRequest -> photoService.getPreSignedUrl(preSignedUrlRequest.getPrefix(), preSignedUrlRequest.getImageName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(preSignedUrlList);
    }
    ```
    
2. **프리사인드 URL 생성**
    - 서버는 요청받은 정보를 바탕으로 AWS S3와 같은 스토리지 서비스에 업로드할 수 있는 프리사인드 URL을 생성합니다.
      
    - `photoService.getPreSignedUrl` 메서드는 해당 이미지를 업로드할 수 있는 프리사인드 URL을 반환합니다.
3. **프리사인드 URL 응답**
    - 서버는 생성된 프리사인드 URL을 클라이언트에게 응답으로 보냅니다. 이 URL은 제한된 시간 동안 유효하며, 해당 시간 내에만 업로드가 가능합니다.
4. **이미지 업로드**
    - 클라이언트는 응답받은 프리사인드 URL을 사용하여 이미지 파일을 직접 스토리지 서비스(S3 등)에 업로드합니다.
      
    - HTTP PUT 요청을 사용하여 이미지 파일을 업로드할 수 있습니다.
    
    
5. **이미지 리사이징 (AWS Lambda)**
    - 이미지를 업로드하면 AWS S3에서 해당 이벤트를 감지하여 AWS Lambda 함수를 트리거합니다.
      
    - 트리거된 Lambda 함수는 업로드된 이미지를 리사이징하고, 리사이징된 이미지를 S3 버킷의 다른 위치에 저장합니다.

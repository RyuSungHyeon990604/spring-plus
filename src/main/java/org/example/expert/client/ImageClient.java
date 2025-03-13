package org.example.expert.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.dto.response.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageClient {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public ImageResponse uploadFile(String folder, MultipartFile file) {
        String key = String.format("%s%s_%s",folder, UUID.randomUUID(),file.getOriginalFilename());
        PutObjectRequest objectAclRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())//이거 해줘야 이미지 볼수있음, 안하면 다운로드만 됨
                .build();
        try {
            s3Client.putObject(objectAclRequest, RequestBody.fromBytes(file.getBytes()));
            return generateFileUrl(key);
        } catch (IOException e) {
            log.warn("Failed to upload file", e);

            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    private ImageResponse generateFileUrl(String key) {
        return new ImageResponse(String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key), key);
    }
}

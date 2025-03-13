package org.example.expert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.accessKey:}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secretKey:}")
    private String secretAccessKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region));

        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            // 로컬 환경: Access Key, Secret Key를 사용
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            builder.credentialsProvider(StaticCredentialsProvider.create(awsCreds));
        } else {
            // EC2 환경: IAM Role을 자동으로 사용
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return builder.build();
    }

}

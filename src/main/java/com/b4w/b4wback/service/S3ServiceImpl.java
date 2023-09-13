package com.b4w.b4wback.service;

import com.amazonaws.HttpMethod;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.b4w.b4wback.service.interfaces.S3Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
@Service
@Validated
public class S3ServiceImpl implements S3Service {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3=amazonS3;
    }

    @Override
    public String generatePresignedUploadImageUrl(String url,Integer expirationTime) {
        Date expiration=new Date(System.currentTimeMillis() + expirationTime);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, url)
                .withExpiration(expiration).withMethod(HttpMethod.PUT);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    @Override
    public String generatePresignedDownloadImageUrl(String url, Integer expirationTime) {
        Date expiration=new Date(System.currentTimeMillis() + expirationTime);
        if (amazonS3.doesObjectExist(bucketName,url)){
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, url)
                    .withExpiration(expiration).withMethod(HttpMethod.GET);
            return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        }
        return "default";
    }
}

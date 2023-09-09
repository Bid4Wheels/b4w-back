package com.b4w.b4wback.service;

import com.amazonaws.HttpMethod;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.b4w.b4wback.exception.EntityNotFoundException;
import com.b4w.b4wback.repository.UserRepository;
import com.b4w.b4wback.service.interfaces.JwtService;
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

    @Value("${aws.users.objectKey}")
    private String usersObjectKey;

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
    public String generatePresignedDownloadImageUrl(long id, Integer expirationTime) {
        String userUrl=usersObjectKey+ id;
        Date expiration=new Date(System.currentTimeMillis() + expirationTime);
        if (amazonS3.doesObjectExist(bucketName,userUrl)){
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, userUrl)
                    .withExpiration(expiration).withMethod(HttpMethod.GET);
            return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        }
        return "default";
    }
}

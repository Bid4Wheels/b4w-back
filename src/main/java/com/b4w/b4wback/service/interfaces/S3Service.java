package com.b4w.b4wback.service.interfaces;



public interface S3Service {
    String generatePresignedUploadImageUrl(String url,Integer expirationTime);

    String generatePresignedDownloadImageUrl(String url ,Integer expirationTime);
}

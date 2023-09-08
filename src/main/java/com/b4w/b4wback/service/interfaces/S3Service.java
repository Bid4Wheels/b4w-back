package com.b4w.b4wback.service.interfaces;



public interface S3Service {
    String getUploadURL(String userEmail);

    String getDownloadURL(long id);
}

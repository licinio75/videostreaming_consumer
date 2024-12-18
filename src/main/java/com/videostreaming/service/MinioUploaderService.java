package com.videostreaming.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class MinioUploaderService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private String bucketName;

    public void uploadSegments(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        for (File file : dir.listFiles()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(file.getName())
                                .stream(fileInputStream, file.length(), -1)
                                .build()
                );
            } catch (Exception e) {
                throw new IOException("Error uploading segments to MinIO", e);
            }
        }
    }
}

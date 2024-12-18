package com.videostreaming.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinioVideoDownloaderService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private String bucketName;

    public String downloadVideo(String videoUrl, String destinationPath) throws IOException {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(videoUrl)
                            .build()
            );

            FileOutputStream fileOutputStream = new FileOutputStream(destinationPath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            fileOutputStream.close();

            return destinationPath;
        } catch (Exception e) {
            throw new IOException("Error downloading video from MinIO", e);
        }
    }
}

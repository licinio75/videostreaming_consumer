package com.videostreaming.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.videostreaming.kafka.VideoData;

@Service
public class VideoConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired 
    private MinioVideoDownloaderService minioVideoDownloaderService;

    @Autowired 
    private FFmpegProcessorService fFmpegProcessorService;

    @Autowired
    private MinioUploaderService minioUploaderService;

    @Value("${kafka.topic.name}")
    private String kafkaTopicName;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, String> record) {
        String message = record.value();
        System.out.println("Received message: " + message);

        // Additional logging for debugging 
        System.out.println("Key: " + record.key()); 
        System.out.println("Partition: " + record.partition()); 
        System.out.println("Offset: " + record.offset());

        processVideo(message);
    }

    private void processVideo(String message) {
        try {
            VideoData videoData = objectMapper.readValue(message, VideoData.class);
            String videoUrl = videoData.getVideoUrl();
            String videoId = videoData.getVideoId();

            String destinationPath = "/tmp/" + videoId + ".mp4";
            minioVideoDownloaderService.downloadVideo(videoUrl, destinationPath);

            String outputPath = "/tmp/" + videoId;
            fFmpegProcessorService.transcodeAndSegment(destinationPath, outputPath, "720p");
            minioUploaderService.uploadSegments(outputPath);
            
            System.out.println("Video processing completed for video ID: " + videoId);
        } catch (Exception e) {
            System.err.println("Error processing video: " + e.getMessage());
        }
    }
}

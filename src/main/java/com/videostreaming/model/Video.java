package com.videostreaming.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "videos")
public class Video {
    @Id
    private String id;
    private String name;       // Nombre del archivo
    private String url;        // URL del video (local o MinIO)
    private String status;     // Estado: "UPLOADED", "PROCESSING", "READY"
    private String uploadDate; // Fecha de subida
}

package com.videostreaming.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.springframework.stereotype.Service;

@Service
public class FFmpegProcessorService {

    public void transcodeAndSegment(String inputPath, String outputPath, String resolution) throws Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath + "/output_" + resolution + ".mp4", grabber.getImageWidth(), grabber.getImageHeight())) {
            
            grabber.start();

            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.start();

            while (true) {
                var frame = grabber.grab();
                if (frame == null) {
                    break;
                }
                recorder.record(frame);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Segment the video (use FFmpeg command line tool for now)
        String command = "ffmpeg -i " + outputPath + "/output_" + resolution + ".mp4 -hls_time 10 -hls_playlist_type vod -hls_segment_filename '" + outputPath + "/segment_%03d.ts' " + outputPath + "/playlist.m3u8";
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }
}

package com.cloudsquare.coss.api.lecture.service;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProcessFfmpegExecutor implements FfmpegExecutor {

    private final String ffmpegPath;

    public ProcessFfmpegExecutor(@Value("${app.video.ffmpeg-path:ffmpeg}") String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    @Override
    public void captureFrame(Path inputFile, Path outputFile, int second) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                ffmpegPath,
                "-y",
                "-ss", String.valueOf(second),
                "-i", inputFile.toAbsolutePath().toString(),
                "-frames:v", "1",
                outputFile.toAbsolutePath().toString())
                .redirectErrorStream(true)
                .start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("ffmpeg process failed with exitCode=" + exitCode);
        }
    }
}

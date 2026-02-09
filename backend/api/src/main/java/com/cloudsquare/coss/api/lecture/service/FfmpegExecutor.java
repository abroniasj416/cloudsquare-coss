package com.cloudsquare.coss.api.lecture.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FfmpegExecutor {

    void captureFrame(Path inputFile, Path outputFile, int second) throws IOException, InterruptedException;
}

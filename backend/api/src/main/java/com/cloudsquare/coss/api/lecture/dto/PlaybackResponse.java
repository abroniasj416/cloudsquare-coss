package com.cloudsquare.coss.api.lecture.dto;

public record PlaybackResponse(
        String playbackUrl,
        long expiresInSeconds
) {
}

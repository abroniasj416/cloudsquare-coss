package com.cloudsquare.coss.api.lecture.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public class ObjectStorageProperties {

    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucket;
    private String videoPrefix;
    private String cdnBaseUrl;
    private long presignedPutExpiresSeconds;
    private long presignedGetExpiresSeconds;
    private String region = "us-east-1";

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getVideoPrefix() {
        return videoPrefix;
    }

    public void setVideoPrefix(String videoPrefix) {
        this.videoPrefix = videoPrefix;
    }

    public String getCdnBaseUrl() {
        return cdnBaseUrl;
    }

    public void setCdnBaseUrl(String cdnBaseUrl) {
        this.cdnBaseUrl = cdnBaseUrl;
    }

    public long getPresignedPutExpiresSeconds() {
        return presignedPutExpiresSeconds;
    }

    public void setPresignedPutExpiresSeconds(long presignedPutExpiresSeconds) {
        this.presignedPutExpiresSeconds = presignedPutExpiresSeconds;
    }

    public long getPresignedGetExpiresSeconds() {
        return presignedGetExpiresSeconds;
    }

    public void setPresignedGetExpiresSeconds(long presignedGetExpiresSeconds) {
        this.presignedGetExpiresSeconds = presignedGetExpiresSeconds;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

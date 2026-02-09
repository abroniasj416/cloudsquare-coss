package com.cloudsquare.coss.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "app.storage.access-key=test-access",
        "app.storage.secret-key=test-secret",
        "app.storage.endpoint=http://localhost:9000",
        "app.storage.bucket=test-bucket",
        "app.storage.video-prefix=test-prefix",
        "app.storage.presigned-put-expires-seconds=300",
        "app.storage.presigned-get-expires-seconds=300"
})
class ApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
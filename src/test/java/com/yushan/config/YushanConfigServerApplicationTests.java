package com.yushan.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.server.git.uri=https://github.com/phutruonnttn/yushan-microservices-config-server.git",
    "eureka.client.enabled=false"
})
class YushanConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }
}

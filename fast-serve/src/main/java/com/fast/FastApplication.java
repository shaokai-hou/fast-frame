package com.fast;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Fast Frame 后端服务启动类
 *
 * @author fast-frame
 */
@Slf4j
@SpringBootApplication
public class FastApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastApplication.class, args);
    }
}
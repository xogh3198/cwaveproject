package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
@ComponentScan(basePackages = {"com.example.demo"})
// 아래 어노테이션을 추가하여 JPA Repository를 스캔할 패키지를 명확히 지정합니다.
@EnableJpaRepositories(basePackages = "com.example.demo.repository") 
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//카프카 주석 처리
@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
//@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class, org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.example.demo"})
// 아래 어노테이션을 추가하여 JPA Repository를 스캔할 패키지를 명확히 지정합니다.
@EnableJpaRepositories(basePackages = "com.example.demo.repository") 
@EnableCaching
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
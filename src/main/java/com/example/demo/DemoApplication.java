package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @SpringBootApplication 어노테이션은 다음 세 가지 기능을 포함합니다.
// 1. @Configuration: 애플리케이션의 설정 클래스임을 명시
// 2. @EnableAutoConfiguration: 클래스패스 기반으로 빈들을 자동으로 설정
// 3. @ComponentScan: 현재 패키지(com.example.demo)와 그 하위의 @Component, @Service, @Repository 등을 스캔하여 빈으로 등록
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo"})
@EnableJpaRepositories(basePackages = "com.example.demo.repository") // JPA 리포지토리 스캔 활성화
public class DemoApplication {

	public static void main(String[] args) {
		// Spring Boot 애플리케이션을 실행하는 메인 메서드입니다.
		SpringApplication.run(DemoApplication.class, args);
	}

}
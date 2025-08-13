package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "공연 예매 시스템 API",
        description = "공연 예매, 결제, 대기열 관리 시스템의 REST API 문서",
        version = "v1.0.0",
        contact = @Contact(
            name = "개발팀",
            email = "dev@example.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "로컬 개발 서버"),
        @Server(url = "https://api.booking.com", description = "운영 서버")
    }
)
public class SwaggerConfig {
    // SpringDoc OpenAPI 3는 자동 설정을 제공하므로 별도의 Bean 설정이 필요하지 않습니다.
    // @OpenAPIDefinition 어노테이션만으로도 기본 설정이 완료됩니다.
}

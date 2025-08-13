# 공연 예매 시스템 API 문서 (Swagger)

## Swagger UI 접속 방법

애플리케이션 실행 후 다음 URL로 접속하여 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 문서 JSON**: http://localhost:8080/api-docs

## 주요 API 엔드포인트

### 1. 공연 관리 (Performance)
- `GET /api/performances` - 전체 공연 목록 조회
- `GET /api/performances/{id}` - 특정 공연 상세 정보 조회
- `GET /api/performances/{id}/schedule` - 공연 일정 및 잔여 좌석 조회
- `POST /api/performances/{id}/queue` - 대기열 진입
- `POST /api/performances/{id}/reserve` - 좌석 예매

### 2. 결제 관리 (Payment)
- `POST /api/payments/{reservationId}/confirm` - 결제 확인 처리

### 3. 예매 관리 (Reservation)
- `GET /api/reservations` - 전체 예매 목록 조회
- `GET /api/reservations/{id}` - 예매 상세 조회

## API 사용 예시

### 1. 공연 목록 조회
```bash
curl -X GET "http://localhost:8080/api/performances" -H "accept: application/json"
```

### 2. 공연 상세 정보 조회
```bash
curl -X GET "http://localhost:8080/api/performances/1" -H "accept: application/json"
```

### 3. 좌석 예매
```bash
curl -X POST "http://localhost:8080/api/performances/1/reserve" \
  -H "accept: application/json" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "seatNumber=A1&schedule=2024-12-25 19:00"
```

### 4. 결제 처리
```bash
curl -X POST "http://localhost:8080/api/payments/1/confirm" -H "accept: application/json"
```

## Swagger 설정

### 의존성
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
```

### 설정 파일 (application.properties)
```properties
# Swagger/OpenAPI 설정
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=alpha
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.try-it-out-enabled=true
```

## 주요 기능

1. **자동 API 문서 생성**: Controller의 어노테이션을 기반으로 자동 생성
2. **대화형 API 테스트**: Swagger UI에서 직접 API 호출 가능
3. **스키마 검증**: Request/Response 모델의 스키마 정보 제공
4. **예시 데이터**: API 호출 시 참고할 수 있는 예시 데이터 제공

## 개발 팁

- `@Operation`: 각 API 메서드의 요약과 설명 추가
- `@ApiResponses`: 가능한 응답 코드와 설명 정의
- `@Parameter`: 파라미터에 대한 설명과 예시 추가
- `@Schema`: 모델 클래스의 필드 설명과 예시 데이터 정의
- `@Tag`: Controller 레벨에서 API 그룹 분류

## 참고사항

- Spring Boot 3.x 버전에서는 SpringDoc OpenAPI 3를 사용합니다
- 기존의 SpringFox는 Spring Boot 3.x와 호환되지 않습니다
- 모든 API는 JSON 형태로 요청/응답합니다

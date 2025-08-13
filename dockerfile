# --- 1단계: 빌드 스테이지 (build-stage) ---
# Java와 Gradle이 설치된 이미지를 사용하여 프로젝트를 빌드합니다.
# 이 이미지는 빌드에만 사용되고 최종 이미지에는 포함되지 않습니다.
FROM gradle:8.14.3-jdk21-jammy AS build-stage

# 컨테이너 내부의 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# Gradle Wrapper와 설정 파일을 먼저 복사하여 종속성 캐싱을 최적화합니다.
# gradle/wrapper 디렉토리와 gradlew, settings.gradle, build.gradle 파일 복사
COPY --chown=gradle:gradle . /app

# 권한 변경
RUN chmod +x gradlew

# 프로젝트 빌드 (테스트는 건너뜁니다)
RUN ./gradlew clean build -x test

# --- 2단계: 최종 이미지 스테이지 (final-stage) ---
# 애플리케이션 실행에 필요한 JRE만 포함된 경량 이미지를 사용합니다.
FROM openjdk:21-slim-bullseye AS final-stage

# 컨테이너 내부의 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
# COPY --from=build-stage [원본 경로] [대상 경로]
COPY --from=build-stage /app/build/libs/*.jar /app/app.jar

# 애플리케이션 실행 시 기본 명령
# java -jar 명령을 사용하여 Spring Boot 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

# 컨테이너가 8080 포트에서 리스닝함을 알립니다 (문서화용).
EXPOSE 8080
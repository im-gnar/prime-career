# Tech Blog Service

기술 블로그 서비스의 백엔드 API 서버입니다.

## 기술 스택

* Java 17
* Spring Boot 3.2.0
* Spring Data JPA
* MySQL 8
* Gradle

## 시작하기

### 필수 요구사항

* JDK 17
* MySQL 8.0
* Gradle 7.x

### 환경 구성

1. MySQL 데이터베이스 생성
```sql
CREATE DATABASE techblog;
CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON techblog.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
```

2. 프로젝트 클론
```bash
git clone https://github.com/company-name/tech-blog.git
cd tech-blog
```

3. 애플리케이션 실행
```bash
./gradlew bootRun
```

## API 명세

### 게시글 API

| Method | URI | Description |
|--------|-----|-------------|
| POST | /api/posts | 게시글 작성 |
| GET | /api/posts | 게시글 목록 조회 |
| GET | /api/posts/{id} | 게시글 상세 조회 |
| PUT | /api/posts/{id} | 게시글 수정 |
| DELETE | /api/posts/{id} | 게시글 삭제 |

## 프로젝트 구조

```
src/main/java/com/company/techblog
├── TechBlogApplication.java
├── controller/
├── domain/
├── repository/
├── service/
└── dto/
```

## 개발 가이드

1. 브랜치 관리
    * main: 제품 출시 브랜치
    * develop: 개발 브랜치
    * feature/*: 기능 개발 브랜치

2. 커밋 메시지 컨벤션
    * feat: 새로운 기능 추가
    * fix: 버그 수정
    * docs: 문서 수정
    * style: 코드 포맷팅
    * refactor: 코드 리팩토링
    * test: 테스트 코드
    * chore: 빌드 업무 수정

## 테스트

```bash
./gradlew test
```

## 라이센스

This project is licensed under the MIT License - see the LICENSE file for details
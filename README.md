# Tuna

> 음악을 공유하고, 서로의 취향을 발견하는 음악 커뮤니티
> 

## 프로젝트 소개

Tuna는 사용자가 좋아하는 음악과 짧은 감상평을 공유하고,
다른 사용자와 댓글을 통해 소통할 수 있는 음악 공유 플랫폼입니다.

음원 파일을 직접 업로드하는 대신 YouTube, Spotify 등의 음악 링크를
게시글에 첨부하여 음악을 공유합니다.

마음에 드는 게시글은 자신만의 플레이리스트에 저장하여 관리할 수 있습니다.

## 서비스 화면

### 메인 화면
![main](/docs/imgs/main.png)

### 게시글 상세 화면
![post_detail](/docs/imgs/post_detail.png)

### 플레이리스트 상세 화면
![playlist_detail](/docs/imgs/playlist_detail.png)

## 주요 기능

- 음악 링크와 감상평을 포함한 게시글 작성
- Youtube 영상 임베드 및 재생
- 게시글 검색
- 게시글 댓글 작성
- 원하는 게시글을 플레이리스트에 저장
- 개인 게시글 및 플레이리스트 관리
- 세션 기반 회원 인증
- 일반 사용자 / 관리자 권한 관리


## 기술 스택

**Backend**

- Java 25
- Spring Boot 4.1
- Spring MVC
- Spring Security
- Spring JDBC
- Spring Data JPA

**Frontend**

- Thymeleaf
- JavaScript
- Tailwind CSS
- YouTube IFrame Player API

**Database**

- MySQL
- Flyway

## 개인 개발 및 개선

### Spring Data JPA 도입
- 기존 JDBC Template 기반 Repository를 JPA 기반으로 점진적 전환
- Entity 및 연관관계 재설계

### Flyway 도입
- SQL 초기화 파일 중심의 스키마 관리 방식 개선
- 버전 기반 DB Migration 관리

### 코드 구조 개선
- 기존 팀 프로젝트 코드 리팩토링
- 데이터 접근 계층 구조 개선

## Technical Decisions

### JDBC Template에서 JPA로 전환
기존 JDBC Template에서 JPA로 전환하기로 결정한 이유는 유사한 CRUD 로직이 반복되는 프로젝트 특성상, JDBC Template의 RowMapper와 반복적인 SQL 작성에서 발생하는 보일러 플레이트를 줄이고자 Spring Data JPA를 도입했습니다. 또한 기존에 더 익숙하게 사용해 온 기술이기 때문에 유지보수와 추가 개발 측면에서도 적합하다 판단했습니다.

### Flyway 도입
기존에는 schema.sql을 이용해 데이터베이스 스키마를 관리했지만, 스키마 변경사항을 수동으로 공유하고 환경별로 반영하는 과정에서 로컬 DB와 운영 DB 간 스키마 불일치 및 변경사항 누락이 발생했습니다.

이를 개선하기 위해 Flyway를 도입하여 스키마 변경사항을 버전별 Migration으로 관리하고, 변경 이력을 코드와 함께 추적할 수 있도록 했습니다.

### 왜 Cursor Pagination을 사용했는가
서비스의 게시글 목록은 페이지 번호 방식이 아닌 무한 스크롤 방식으로 제공됩니다. Offset 기반 페이지네이션은 이전 조회 이후 데이터가 추가되거나 삭제될 경우 조회 위치가 변경되어, 다음 데이터를 불러오는 과정에서 일부 데이터가 중복되거나 누락될 수 있습니다.

무한 스크롤 환경에서 보다 안정적으로 다음 데이터를 조회하기 위해 마지막으로 조회한 데이터의 식별자를 기준으로 조회하는 Cursor 기반 페이지네이션을 적용했습니다.

정렬 기준은 createdAt과 id를 조합한 복합 Cursor Key를 사용합니다.
createdAt만 사용할 경우 동일한 시간에 생성된 데이터의 순서를 명확하게 결정할 수 없기 때문에, id를 보조 정렬 기준으로 사용하여 일관된 정렬 순서를 보장하도록 설계했습니다.

클라이언트에 전달하는 Cursor는 다음 정보를 포함합니다.

- Cursor 포맷 버전 (`VERSION`)
- 조회 방향 (`direction`)
- 생성 시각 (`createdAt`)
- 식별자 (`id`)

이 값들을 하나의 payload로 구성한 뒤 URL-safe Base64로 인코딩하여 Cursor Token으로 전달합니다.

Cursor 포맷에 버전을 포함하여 향후 Cursor 구조가 변경되더라도 기존 Cursor와 새로운 Cursor를 구분할 수 있도록 했으며, 조회 방향을 포함하여 순방향과 역방향 페이지네이션을 동일한 Cursor 구조로 처리할 수 있도록 설계했습니다.


## Project History

Tuna는 멋쟁이사자처럼 백엔드 부트캠프 25기에서
4인 팀 프로젝트로 시작되었습니다.

팀 프로젝트 종료 이후 개인 저장소로 Fork하여
기존 JDBC Template 기반 데이터 접근 계층을 Spring Data JPA로 전환하고, Flyway를 도입하여 데이터베이스 스키마 변경 이력을 관리하는 작업을 진행하고 있습니다.

## Original Team

> 아래 내용은 최초 팀 프로젝트 개발 당시 담당 영역입니다.
> 

### **김두희**

- [doo3721](https://github.com/doo3721)
- 팀장
- 레포지토리, 프로젝트 관리
- 도메인
    - 회원가입 기능 구현
    - 게시글 검색 기능 구현
    - 댓글 기능 구현

### **이원준**

- [Dev-Wonjoon](https://github.com/Dev-Wonjoon)
- SQL 관리
- PPT 제작, 발표
- 와이어프레임 제작
- 도메인
    - 플레이리스트 기능 구현
    - 프론트엔드 전반 구현 및 최적화
    - 게시글, 플레이리스트 페이지네이션 구현

### **이수환**

- [Suhwan623](https://github.com/Suhwan623)
- 도메인
    - 로그인, 로그아웃 기능 구현
    - 마이페이지 기능 구현
    - 관리자 생성 기능 구현
    - 회원 관리 기능 구현

### **정승우**

- [alpha99k](https://github.com/alpha99k)
- 산출물 관리
- 시연영상 제작
- 도메인
    - 게시글 CRUD 기능 구현
    - 메인화면 기능 구현
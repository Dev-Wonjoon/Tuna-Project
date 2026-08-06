# 게시글 수정시 데이터 불일치 문제

## 📌 문제 발생

1. 문제 유형 (핵심 태그) 정의

- 데이터 불일치
1. 어떤 기능에서 문제가 발생했는가?
    - 게시글 조회할때 수정이 되지 않았을 때에는 생성일로 나와야 하는데 상세보기만 해도 수정일이 바뀌고 수정됨 처리가 되는 문제

---

## 🔍 원인 분석

- 왜 발생했는가?
    - MySQL/MariaDB 테이벌 스키마 정의 시, `updated_at` 컬럼에 `ON UPDATE CURRENT_TIMESTAMP` 옵션 설정으로 인해 `SELECT` 조회 과정에서 발생한 특정 update 쿼리가 수행될 때 DB 차원에서 `updated_at` 컬럼을 자동으로 현재 시간으로 갱신
- 어떤 코드/구조에서 문제가 있었는가?
    - SQL 테이블 생성(DDL) 시 `updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` 구문 사용

---

## 🛠 해결 방법

- 어떻게 해결했는가?
    - **DDL 스키마 수정:** `updated_at` 컬럼에서 자동 갱신 구문(`ON UPDATE CURRENT_TIMESTAMP`)을 제거하고, 기본값(`DEFAULT CURRENT_TIMESTAMP`)만 유지하도록 변경

---

## 🧠 배운점

- 트러블 슈팅을 통해 깨달은 부분
    - `ON UPDATE CURRENT_TIMESTAMP`는 편리하지만, 조회수 증가나 단순 카운트 변경 등 **동일 레코드의 다른 컬럼이 업데이트될 때도 수정일시가 원치 않게 바뀔 수 있음**을 깨달았습니다.
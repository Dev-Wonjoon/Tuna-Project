# 회원탈퇴 시 세션 만료 페이지로 이동되는 문제

## 📌 문제 발생

1. 문제 유형 (핵심 태그) 정의

- 세션관리
1. 어떤 기능에서 문제가 발생했는가?

   마이페이지에서 **회원 탈퇴 기능**을 구현한 뒤, 탈퇴 버튼을 누르면 회원 정보는 정상적으로 삭제되었지만 홈 화면으로 이동하지 않고 로그인 페이지에서 **"세션이 만료되었습니다. 다시 로그인해주세요."**라는 메시지가 출력되었다.

   회원 탈퇴 후에는 로그아웃 처리와 함께 홈 화면(`/`)으로 이동하도록 구현하고 싶었지만, Spring Security가 현재 요청을 **만료된 세션(Invalid Session)** 으로 판단하여 `invalidSessionUrl("/login?expired")`로 리다이렉트하고 있었다.


---

## 🔍 원인 분석

- 왜 발생했는가?
- 초기 구현에서는 회원 삭제와 로그아웃을 다음과 같이 수행하였다.

```java
memberService.deleteById(userDetails.getMember().getId());

new SecurityContextLogoutHandler().logout(
        request,
        response,
        authentication
);
```

로그아웃과 회원 삭제의 실행 순서가 문제라고 생각하여 순서를 변경해 보았지만 동일한 현상이 발생하였다.

원인을 분석한 결과, `SecurityContextLogoutHandler.logout()`은 단순히 로그아웃만 수행하는 것이 아니라 **현재 세션을 무효화(Invalidate)** 하는 기능도 함께 수행한다.

프로젝트의 `SecurityConfig`에는 다음과 같이 설정되어 있었다.

```java
.sessionManagement(session -> session
        .invalidSessionUrl("/login?expired")
        .maximumSessions(1)
        .maxSessionsPreventsLogin(false)
)
```

세션이 무효화된 상태에서 새로운 요청이 발생하면 Spring Security는 이를 **유효하지 않은 세션(Invalid Session)** 으로 판단하고 `invalidSessionUrl("/login?expired")`로 자동 리다이렉트하였다.

그 결과 홈 화면이 아닌 로그인 페이지에서 **"세션이 만료되었습니다."** 메시지가 출력되었다.

---

## 🛠 해결 방법

- 어떻게 해결했는가?

처음에는 로그아웃과 회원 삭제의 실행 순서를 변경하여 문제를 해결하려고 했지만 결과는 동일하였다.

원인을 분석한 결과 `invalidSessionUrl()` 설정이 원인이라는 것을 확인하였다.

따라서 `invalidSessionUrl("/login?expired")` 설정을 제거하여 회원 탈퇴 후 세션이 무효화되더라도 로그인 페이지로 이동하지 않도록 수정하였다.

```java
.sessionManagement(session -> session
        .maximumSessions(1)
        .maxSessionsPreventsLogin(false)
)
```

또한 회원 탈퇴 후에는

```java
return "redirect:/?logout";
```

으로 홈 화면으로 이동하도록 변경하였으며, 홈 화면에서는 `?logout` 파라미터를 확인하여 **로그아웃 완료 토스트 메시지**를 출력하도록 구현하였다.

---

## 🧠 배운점

- `SecurityContextLogoutHandler.logout()`은 로그아웃뿐만 아니라 **SecurityContext 초기화, Authentication 제거, HttpSession 무효화**까지 수행한다는 점을 이해하였다.
- `invalidSessionUrl()`은 세션이 무효화되었을 때 자동으로 이동할 페이지를 지정하는 기능이라는 것을 학습하였다.
- Spring Security의 로그아웃 처리 과정과 세션 관리 방식을 직접 경험하면서 인증 및 세션 관리 흐름을 보다 깊이 이해할 수 있었다.
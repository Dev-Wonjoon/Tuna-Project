# View에서 에러메세지가 동시에 출력된다

## 📌 문제 발생

1. 문제 유형 (핵심 태그) 정의
    - UI/UX 접근성 문제
2. 어떤 기능에서 문제가 발생했는가?
    - 회원가입 시 한 필드의 valid가 두 개 이상 에러가 나면 메세지가 동시에 출력되는 상황

---

## 🔍 원인 분석

- 왜 발생했는가?
    - valid에 대한 우선순위를 정하지 않아 메세지가 모두 출력이 되었다
- 어떤 코드/구조에서 문제가 있었는가?
    - valid에 대한 순서를 따로 정의하지 않은 구조가 문제가 되었다

---

## 🛠 해결 방법

- GroupSequance, Validated 어노테이션을 사용했다
    - ValidationGroups 클래스

        ```java
        public class ValidationGroups {
            public interface NotBlankGroup {}
            public interface EmailGroup {}
            public interface PatternGroup {}
            public interface SizeGroup {}
        }
        ```

        - validation에 적용할 마커 인터페이스들을 정의한다
    - ValidationSequence 클래스

        ```java
        @GroupSequence({
                Default.class,
                ValidationGroups.NotBlankGroup.class,
                ValidationGroups.EmailGroup.class,
                ValidationGroups.PatternGroup.class,
                ValidationGroups.SizeGroup.class
        })
        public interface ValidationSequence {
        }
        ```

        - GroupSequence에 나열한 순서대로 우선순위가 정해진다
    - DTO 클래스

        ```java
        @NotBlank(
        				message = "닉네임은 필수 입력 항목입니다.",
        				groups = ValidationGroups.NotBlankGroup.class
        )
        @Pattern(
                regexp = "^[A-Za-z0-9가-힣]+$",
                message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.",
                groups = ValidationGroups.PatternGroup.class
        )
        @Size(
                min=2, max = 10,
                message = "닉네임은 2자 이상 10자 이하로 입력해야 합니다.",
                groups = ValidationGroups.SizeGroup.class
        )
        private String name;
        ```

        - 필드의 validation 어노테이션에 groups를 지정한다
    - Controller 클래스

        ```java
        @PostMapping("/signup")
        public String signup(
                @Validated(ValidationSequence.class) @ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto,
                BindingResult bindingResult
        )
        ```

        - Validated 어노테이션을 사용해 ValidationSequence 클래스를 적용한다
- 그 결과 에러메시지가 우선순위가 먼저인 것 하나만 출력이 된다

  ![](image.png)


---

## 🧠 배운점 및 피드백

- 고친 결과 깔끔해지긴 했지만, 에러 우선순위가 모든 필드에 적용되어 우선순위가 높은 하나의 필드에만 적용된다
- 각 필드의 에러메세지가 독립적인 우선순위를 가지며 출력되는 방법도 알아봤다
    - 다만, 그 경우에는 커스텀 어노테이션을 작성해 validation을 전부 만들어야 해서 시간상 어려울 것 같다.
- 때문에 우선 힌트를 출력하여 UX적으로 불편함을 최소화 시키는 방향으로 진행했다.

---

## 🔍 출처

- https://eckrin.tistory.com/201
- https://dncjf64.tistory.com/302
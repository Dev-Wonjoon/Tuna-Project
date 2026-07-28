package net.tuna.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberDto {
    private Long id;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 50, message = "이메일은 50자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(max = 50, message = "비밀번호는 50자 이하로 입력해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min=2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해야 합니다.")
    private String name;

    private Role role;

    private LocalDateTime createdAt;
}

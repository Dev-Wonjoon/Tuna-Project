package net.tuna.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.tuna.member.validation.ValidationGroups;

@Getter
@Setter
public class RequestSignUpDto {
    @NotBlank(message = "이메일은 필수 입력 항목입니다.", groups = ValidationGroups.NotBlankGroup.class)
    @Pattern(
            regexp = "^[A-Za-z0-9]+@[A-Za-z0-9]+(\\.[A-Za-z]+)+$",
            message = "이메일 형식은 [아이디]@[사이트].[도메인]으로, 영문, 숫자, '@', '.' 만 사용해야 합니다.",
            groups = ValidationGroups.PatternGroup.class
    )
    @Size(
            min = 5, max = 50,
            message = "이메일은 5 ~ 50 글자로 입력해야 합니다.",
            groups = ValidationGroups.SizeGroup.class
    )
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.", groups = ValidationGroups.NotBlankGroup.class)
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

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.", groups = ValidationGroups.NotBlankGroup.class)
    @Size(
            min = 8, max = 50,
            message = "비밀번호는 8자리 이상 50자 이하로 입력해야 합니다.",
            groups = ValidationGroups.SizeGroup.class
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.", groups = ValidationGroups.NotBlankGroup.class)
    @Size(
            min = 8, max = 50,
            message = "비밀번호는 8자리 이상 50자 이하로 입력해야 합니다.",
            groups = ValidationGroups.SizeGroup.class
    )
    private String passwordConfirm;


}

package net.tuna.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistNameUpdateDto {
    @NotBlank(message = "플레이리스트 이름을 입력해주세요.")
    @Size(min=1, max=30, message = "플레이리스트 이름을 1자 이상 30자 이하여야 합니다.")
    @Pattern(regexp = "^[A-Za-z가-힣0-9 ]+$", message = "한글, 영문, 숫자만 사용할 수 있습니다.")
    private String name;

    public PlaylistNameUpdateDto(String name) {
        this.name = name;
    }
}

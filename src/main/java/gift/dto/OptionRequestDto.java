package gift.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionRequestDto(
        @NotBlank
        @Size(max = 50, message = "옵션 이름은 공백 포함 최대 50자까지 입력 할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "특수기호는 ( ) [ ] + - & / _ 만 허용됩니다.")
        String name,

        @NotNull
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 99_999_999, message = "수량은 1억개 미만이어야 합니다.")
        int quantity
) {
}




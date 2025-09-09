package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull
        Long optionId,
        @NotNull
        @Min(1)
        int quantity,
        String message
) {

}

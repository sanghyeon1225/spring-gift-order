package gift.dto;

import jakarta.validation.constraints.NotNull;

public record WishRequestDto (
        @NotNull Long productId
) {
}

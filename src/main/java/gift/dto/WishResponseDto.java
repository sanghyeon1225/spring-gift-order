package gift.dto;

import java.math.BigDecimal;

public record WishResponseDto (
    Long wishlistId,
    Long productId,
    String productName,
    BigDecimal price,
    String imageUrl
) {
}

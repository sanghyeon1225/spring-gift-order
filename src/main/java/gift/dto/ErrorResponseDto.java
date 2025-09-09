package gift.dto;

public record ErrorResponseDto(
        String errorCode,
        String message
) {
}

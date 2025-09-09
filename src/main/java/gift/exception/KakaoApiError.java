package gift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum KakaoApiError {
    // 4xx Client Error
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 오류가 발생했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    UNKNOWN_CLIENT_ERROR("알 수 없는 클라이언트 오류가 발생했습니다."),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 내부 서버에서 오류가 발생했습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "카카오 서버 게이트웨이에 문제가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "카카오 서비스가 점검 중 입니다."),
    UNKNOWN_SERVER_ERROR("알 수 없는 서버 오류가 발생했습니다.");

    private HttpStatus status;
    private final String message;

    KakaoApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    KakaoApiError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static KakaoApiError from(HttpStatusCode statusCode) {
        if (statusCode.is4xxClientError()) {
            return switch (statusCode.value()) {
                case 400 -> BAD_REQUEST;
                case 401 -> UNAUTHORIZED;
                case 403 -> FORBIDDEN;
                default -> UNKNOWN_CLIENT_ERROR;
            };
        }
        if (statusCode.is5xxServerError()) {
            return switch (statusCode.value()) {
                case 500 -> INTERNAL_SERVER_ERROR;
                case 502 -> BAD_GATEWAY;
                case 503 -> SERVICE_UNAVAILABLE;
                default -> UNKNOWN_SERVER_ERROR;
            };
        }
        return UNKNOWN_SERVER_ERROR;
    }
}

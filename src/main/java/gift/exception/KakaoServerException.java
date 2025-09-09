package gift.exception;

public class KakaoServerException extends RuntimeException {
    public KakaoServerException(String message) {
        super(message);
    }

    public KakaoServerException(String message, Throwable cause) {
        super(message, cause);
    }
}

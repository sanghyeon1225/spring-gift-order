package gift.exception;

public class OutOfQuantityException extends RuntimeException {
    public OutOfQuantityException(String message) {
        super(message);
    }
}

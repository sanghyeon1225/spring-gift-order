package gift.exception;

import gift.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("INVALID_CREDENTIALS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponseDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponseDto);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("EMAIL_ALREADY_EXISTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    @ExceptionHandler(DuplicateWishException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateWishException(DuplicateWishException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("DUPLICATE_WISHLIST", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    @ExceptionHandler(InvalidSortOptionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidSortOptionException(InvalidSortOptionException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("INVALID_SORT_OPTION", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponseDto);
    }

    @ExceptionHandler(DuplicateOptionNameException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateOptionNameException(DuplicateOptionNameException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto("DUPLICATE_OPTION_NAME", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDto);
    }

    @ExceptionHandler(OutOfQuantityException.class)
    public ResponseEntity<ErrorResponseDto> handleOutOfStockException(OutOfQuantityException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("OUT_OF_QUANTITY", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidEntityDataException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidEntityDataException(InvalidEntityDataException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("INVALID_ENTITY_DATA", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(KakaoClientException.class)
    public ResponseEntity<ErrorResponseDto> handleKakaoClientException(
            KakaoClientException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("KAKAO_CLIENT_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(KakaoServerException.class)
    public ResponseEntity<ErrorResponseDto> handleKakaoServerException(KakaoServerException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("KAKAO_SERVER_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(errorResponse);
    }

}

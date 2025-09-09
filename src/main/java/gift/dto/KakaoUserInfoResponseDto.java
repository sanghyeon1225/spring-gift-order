package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gift.exception.ResourceNotFoundException;
import java.util.Map;

public record KakaoUserInfoResponseDto(
        Long id,
        @JsonProperty("kakao_account")
        Map<String, Object> kakaoAccount
) {
    public String getEmail() {
        if (kakaoAccount == null || !kakaoAccount.containsKey("email")) {
            throw new ResourceNotFoundException("카카오 계정에서 이메일 정보를 찾을 수 없습니다.");
        }
        return kakaoAccount.get("email").toString();
    }
}

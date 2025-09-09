package gift.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public record KakaoTokenRequestDto (
        String grantType,
        String clientId,
        String redirectUri,
        String code
) {
    public KakaoTokenRequestDto(String clientId, String redirectUri, String code) {
        this("authorization_code", clientId, redirectUri, code);
    }

    public MultiValueMap<String, String> dtoToFormData() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", grantType);
        form.add("client_id", clientId);
        form.add("redirect_uri", redirectUri);
        form.add("code", code);
        return form;
    }
}

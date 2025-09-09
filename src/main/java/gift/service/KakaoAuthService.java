package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenRequestDto;
import gift.dto.KakaoTokenResponseDto;
import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.exception.KakaoClientException;
import gift.exception.KakaoApiError;
import gift.exception.KakaoServerException;
import gift.security.JwtTokenProvider;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class KakaoAuthService {
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 3000;
    private static final int READ_TIMEOUT_MILLISECONDS = 3000;
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com";
    private static final String KAKAO_API_URL = "https://kapi.kakao.com";
    private final RestClient authClient;
    private final RestClient apiClient;
    private final KakaoProperties kakaoProperties;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoAuthService(KakaoProperties kakaoProperties, MemberService memberService,
            JwtTokenProvider jwtTokenProvider) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLISECONDS);

        this.authClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(KAKAO_AUTH_URL)
                .build();

        this.apiClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(KAKAO_API_URL)
                .build();

        this.kakaoProperties = kakaoProperties;

        this.memberService = memberService;

        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String getAccessToken(String authorizeCode) {
        KakaoTokenRequestDto body = new KakaoTokenRequestDto(
                kakaoProperties.getClientId(),
                kakaoProperties.getRedirectUri(),
                authorizeCode
        );

        try {
            KakaoTokenResponseDto response = authClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body.dtoToFormData())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                        KakaoApiError error = KakaoApiError.from(res.getStatusCode());
                        String errorMessage = error.getMessage();
                        throw new KakaoClientException(errorMessage + "응답 코드: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, res) -> {
                        KakaoApiError error = KakaoApiError.from(res.getStatusCode());
                        String errorMessage = error.getMessage();
                        throw new KakaoServerException(errorMessage + "응답 코드: " + res.getStatusCode());
                    })
                    .body(KakaoTokenResponseDto.class);

            return response.accessToken();
        } catch (ResourceAccessException e) {
            throw new KakaoServerException("카카오 서버와 통신이 원활하지 않습니다.", e);
        }
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        try {
            return apiClient.get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                        KakaoApiError error = KakaoApiError.from(res.getStatusCode());
                        String errorMessage = error.getMessage();
                        throw new KakaoClientException(errorMessage + "응답 코드: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, res) -> {
                        KakaoApiError error = KakaoApiError.from(res.getStatusCode());
                        String errorMessage = error.getMessage();
                        throw new KakaoServerException(errorMessage + "응답 코드: " + res.getStatusCode());
                    })
                    .body(KakaoUserInfoResponseDto.class);
        } catch (ResourceAccessException e) {
            throw new KakaoServerException("카카오 서버와 통신이 원활하지 않습니다.", e);
        }
    }

    public MemberResponseDto kakaoLogin(String authorizeCode) {
        String accessToken = getAccessToken(authorizeCode);
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(accessToken);
        return memberService.processKakaoLogin(kakaoUserInfoResponseDto, accessToken);
    }
}

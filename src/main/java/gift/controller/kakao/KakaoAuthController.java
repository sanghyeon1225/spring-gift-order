package gift.controller.kakao;

import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.security.JwtTokenProvider;
import gift.service.KakaoAuthService;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController (
            KakaoAuthService kakaoAuthService
    ) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<MemberResponseDto> kakaoLogin(@RequestParam("code") String authorizeCode) {
        return ResponseEntity.ok(kakaoAuthService.kakaoLogin(authorizeCode));
    }
}

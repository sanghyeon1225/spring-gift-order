package gift.controller.kakao;

import gift.config.KakaoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoLoginPageController {
    private final String loginUrl;

    public KakaoLoginPageController(KakaoProperties kakaoProperties) {
        loginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + kakaoProperties.getClientId() + "&redirect_uri=" + kakaoProperties.getRedirectUri();
    }

    @GetMapping("/kakao/login")
    public String loginPage(Model model) {
        model.addAttribute("location", loginUrl);

        return "kakao/login";
    }
}

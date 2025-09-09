package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.entity.Order;
import gift.exception.KakaoApiError;
import gift.exception.KakaoClientException;
import gift.exception.KakaoServerException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class KakaoMessageService {
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 3000;
    private static final int READ_TIMEOUT_MILLISECONDS = 3000;
    private static final String KAKAO_API_URL = "https://kapi.kakao.com";
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public KakaoMessageService(ObjectMapper objectMapper) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLISECONDS);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(KAKAO_API_URL)
                .build();

        this.objectMapper = objectMapper;
    }

    public void sendOrderMessage(String accessToken, Order order) {
        Map<String, Object> template = createTemplate(order);
        String templateJson = serializeTemplate(template);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template_object", templateJson);
        try {
            restClient.post()
                    .uri("/v2/api/talk/memo/default/send")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
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
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new KakaoServerException("카카오 서버와 통신이 원활하지 않습니다.", e);
        }
    }

    private Map<String, Object> createTemplate(Order order) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("object_type", "commerce");

        String productUrl = "http://localhost:8080/products/" + order.getOption().getProduct().getId();

        Map<String, Object> content = new HashMap<>();
        content.put("title", order.getOption().getProduct().getName());
        content.put("image_url", order.getOption().getProduct().getImageUrl());
        content.put("link", Map.of(
                "web_url", productUrl,
                "mobile_web_url", productUrl
        ));
        template.put("content", content);

        Map<String, Object> commerce = new HashMap<>();
        commerce.put("regular_price", order.getOption().getProduct().getPrice().intValue());
        template.put("commerce", commerce);

        return template;
    }

    private String serializeTemplate(Map<String, Object> template) {
        try {
            return objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("템플릿 직렬화에 실패했습니다.", e);
        }
    }
}

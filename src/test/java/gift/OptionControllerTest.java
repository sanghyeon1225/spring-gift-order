package gift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.OptionService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OptionControllerTest {
    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();
    private String url;
    private String accessToken;

    private Long productId;
    private Long optionId;
    private List<OptionRequestDto> options;

    @Autowired
    private OptionService optionService;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/api/products";
        String memberUrl = "http://localhost:" + port + "/api/members";
        MemberRequestDto memberRequestDto = new MemberRequestDto("test@example.com", "password");

        client.post()
                .uri(memberUrl + "/register")
                .body(memberRequestDto)
                .retrieve()
                .toBodilessEntity();

        var loginResponse = client.post()
                .uri(memberUrl + "/login")
                .body(memberRequestDto)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        this.accessToken = loginResponse.getBody().token();

        options = List.of(
                new OptionRequestDto("테스트용 옵션", 100),
                new OptionRequestDto("테스트용 옵션2", 100)
        );

        ProductRequestDto productRequestDto = new ProductRequestDto(
                "치킨",
                BigDecimal.valueOf(10000),
                "https://picsum.photos/200",
                false,
                options
        );

        var response = client.post()
                .uri(url)
                .header("Authorization", "Bearer " + this.accessToken)
                .body(productRequestDto)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        this.productId = response.getBody().id();
        this.optionId = optionService.getOptions(productId).get(0).id();
    }

    @AfterEach
    void rollback() {
        jdbcClient.sql("DELETE FROM options")
                .update();
        jdbcClient.sql("DELETE FROM products")
                .update();
        jdbcClient.sql("DELETE FROM members")
                .update();
    }

    @Test
    void 상품_옵션_조회() {
        var response = client.get()
                .uri(url + "/" + productId + "/options")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<OptionResponseDto>>() {});

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasSize(2),
                () -> assertThat(response.getBody().get(0).name()).isEqualTo("테스트용 옵션")
        );
    }

    @Test
    void 상품_옵션_추가() {
        OptionRequestDto optionRequestDto = new OptionRequestDto("테스트용 옵션3", 100);

        var response = client.post()
                .uri(url + "/" + productId + "/options")
                .header("Authorization", "Bearer " + accessToken)
                .body(optionRequestDto)
                .retrieve()
                .toEntity(OptionResponseDto.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody().name()).isEqualTo("테스트용 옵션3")
        );
    }

    @Test
    void 상품_옵션_수정() {
        OptionRequestDto optionRequestDto = new OptionRequestDto("수정된 옵션", 1000);

        var response = client.put()
                .uri(url + "/" + productId + "/options/" + optionId)
                .header("Authorization", "Bearer " + accessToken)
                .body(optionRequestDto)
                .retrieve()
                .toEntity(OptionResponseDto.class);

        assertAll (
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().name()).isEqualTo("수정된 옵션"),
                ()  -> assertThat(response.getBody().quantity()).isEqualTo(1000)
        );
    }

    @Test
    void 상품_옵션_삭제() {
        var response = client.delete()
                .uri(url + "/" + productId + "/options/" + optionId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 중복된_옵션_이름_생성() {
        OptionRequestDto optionRequestDto = new OptionRequestDto("테스트용 옵션", 7777);

        assertThatThrownBy(() ->
                client.post()
                        .uri(url + "/" + productId + "/options")
                        .header("Authorization", "Bearer " + accessToken)
                        .body(optionRequestDto)
                        .retrieve()
                        .toEntity(OptionResponseDto.class)
        ).isInstanceOf(HttpClientErrorException.Conflict.class);
    }
}

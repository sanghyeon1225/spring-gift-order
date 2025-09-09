package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.dto.OptionRequestDto;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.dto.UpdateProductRequestDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {
    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();
    private String url;
    private String accessToken;
    private List<OptionRequestDto> options;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/api/products";
        MemberRequestDto memberRequestDto = new MemberRequestDto("test@example.com", "password");
        String memberUrl = "http://localhost:" + port + "/api/members";

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
    }

    @Autowired
    private JdbcClient jdbcClient;

    @AfterEach
    void rollback() {
        jdbcClient.sql("DELETE FROM members")
                .update();
    }

    @Test
    void 올바른_상품_생성() {
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

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().name()).isEqualTo("치킨")
        );
    }

    @Test
    void MD승인_없이_카카오_이름이_들어간_상품_생성() {
        ProductRequestDto productRequestDto = new ProductRequestDto(
                "카카오치킨",
                BigDecimal.valueOf(10000),
                "https://picsum.photos/200",
                false,
                options
        );

        assertThatThrownBy(() ->
                client.post()
                        .uri(url)
                        .header("Authorization", "Bearer " + this.accessToken)
                        .body(productRequestDto)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }

    @Test
    void MD승인_받고_카카오_이름이_들어간_상품_생성() {
        ProductRequestDto productRequestDto = new ProductRequestDto(
                "카카오치킨",
                BigDecimal.valueOf(10000),
                "https://picsum.photos/200",
                true,
                options
        );

        var response = client.post()
                .uri(url)
                .header("Authorization", "Bearer " + this.accessToken)
                .body(productRequestDto)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().name()).isEqualTo("카카오치킨")
        );
    }

    @Test
    void MD승인_없이_카카오_이름으로_상품_수정() {
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

        assertThat(response.getBody()).isNotNull();
        Long productId = response.getBody().id();

        UpdateProductRequestDto updateProductRequestDto = new UpdateProductRequestDto(
                productId,
                "카카오치킨",
                BigDecimal.valueOf(10000),
                "https://picsum.photos/200",
                false
        );

        assertThatThrownBy(() ->
                client.put()
                        .uri(url + "/" + productId)
                        .header("Authorization", "Bearer " + this.accessToken)
                        .body(updateProductRequestDto)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}

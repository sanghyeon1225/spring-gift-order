package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.dto.OptionRequestDto;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.dto.WishRequestDto;
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
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WishControllerTest {
    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();
    private String productUrl;
    private String wishUrl;
    private String accessToken;
    private Long productId;
    private List<OptionRequestDto> options;

    @BeforeEach
    void setUp() {
        productUrl = "http://localhost:" + port + "/api/products";
        wishUrl = "http://localhost:" + port + "/api/wishes";
        MemberRequestDto memberRequestDto = new MemberRequestDto("test@example.com", "password");
        String memberUrl = "http://localhost:" + port + "/api/members";
        options = List.of(
                new OptionRequestDto("테스트용 옵션", 100),
                new OptionRequestDto("테스트용 옵션2", 100)
        );

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

        ProductRequestDto productRequestDto = new ProductRequestDto(
                "치킨",
                BigDecimal.valueOf(10000),
                "https://picsum.photos/200",
                false,
                options
        );

        var response = client.post()
                .uri(productUrl)
                .header("Authorization", "Bearer " + this.accessToken)
                .body(productRequestDto)
                .retrieve()
                .toEntity(ProductResponseDto.class);

        this.productId = response.getBody().id();
    }

    @Autowired
    private JdbcClient jdbcClient;

    @AfterEach
    void rollback() {
        jdbcClient.sql("DELETE FROM wishlist")
                .update();
        jdbcClient.sql("DELETE FROM options")
                .update();
        jdbcClient.sql("DELETE FROM products")
                .update();
        jdbcClient.sql("DELETE FROM members")
                .update();
    }

    @Test
    void 위시리스트_상품_추가() {
        var wishRequestDto = new WishRequestDto(this.productId);

        var response = client.post()
                .uri(wishUrl)
                .header("Authorization", "Bearer " + this.accessToken)
                .body(wishRequestDto)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void 위시리스트_상품_삭제() {
        client.post()
                .uri(wishUrl)
                .header("Authorization", "Bearer " + this.accessToken)
                .body(new WishRequestDto(this.productId))
                .retrieve()
                .toBodilessEntity();

        var deleteResponse = client.delete()
                .uri(wishUrl + "/" + this.productId)
                .header("Authorization", "Bearer " + this.accessToken)
                .retrieve()
                .toBodilessEntity();

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

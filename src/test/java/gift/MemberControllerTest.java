package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
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
public class MemberControllerTest {
    @LocalServerPort
    private int port;

    private RestClient client = RestClient.builder().build();
    private String url;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/api/members";
    }

    @Autowired
    private JdbcClient jdbcClient;

    @AfterEach
    void rollback() {
        jdbcClient.sql("DELETE FROM members")
                .update();
    }

    @Test
    void 정상_회원가입() {
        var memberRequestDto = new MemberRequestDto(
                "test@example.com",
                "test123");
        var response = client.post()
                .uri(url + "/register")
                .body(memberRequestDto)
                .retrieve()
                .toEntity(MemberResponseDto.class);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().token()).isNotBlank()
        );
    }

    @Test
    void 비밀번호_잘못된_로그인() {
        var registerRequestDto = new MemberRequestDto(
                "test@example.com",
                "test123");
        var response = client.post()
                .uri(url + "/register")
                .body(registerRequestDto)
                .retrieve()
                .toEntity(MemberResponseDto.class);

        var loginRequestDto = new MemberRequestDto(
                "test@example.com",
                "wrong_password");

        assertThatThrownBy(() ->
                client.post()
                        .uri(url+ "/login")
                        .body(loginRequestDto)
                        .retrieve()
                        .toBodilessEntity()
        ).isInstanceOf(HttpClientErrorException.Forbidden.class);
    }
}

package gift;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.LoginInterceptor;
import gift.auth.LoginMemberArgumentResolver;
import gift.controller.OrderController;
import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.exception.OutOfQuantityException;
import gift.security.JwtTokenProvider;
import gift.service.OrderService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private LoginInterceptor loginInterceptor;

    @BeforeEach
    void setUp() throws Exception {
        Member mockMember = new Member("testMember@email.com", "password", "LOCAL");
        given(loginInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(loginMemberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockMember);
    }

    @Test
    void 상품_정상_주문() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(1L, 2, "test message");
        OrderResponseDto responseDto = new OrderResponseDto(1L, 1L, 2, LocalDateTime.now(), "test message");

        given(orderService.createOrder(any(Member.class), any(OrderRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.id()))
                .andExpect(jsonPath("$.optionId").value(responseDto.optionId()))
                .andExpect(jsonPath("$.quantity").value(responseDto.quantity()))
                .andExpect(jsonPath("$.message").value(responseDto.message()));
    }

    @Test
    void 상품_수량_부족_주문() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(1L, 999, "test message");
        String errorMessage = "재고가 부족합니다.";

        given(orderService.createOrder(any(Member.class), any(OrderRequestDto.class)))
                .willThrow(new OutOfQuantityException(errorMessage));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("OUT_OF_QUANTITY"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}

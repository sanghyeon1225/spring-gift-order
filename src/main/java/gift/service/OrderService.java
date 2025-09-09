package gift.service;

import gift.dto.OrderRequestDto;
import gift.dto.OrderResponseDto;
import gift.entity.Member;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Wish;
import gift.exception.ResourceNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoMessageService kakaoMessageService;

    public OrderService(OrderRepository orderRepository, OptionRepository optionRepository,
            WishRepository wishRepository, KakaoMessageService kakaoMessageService) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoMessageService = kakaoMessageService;
    }

    @Transactional
    public OrderResponseDto createOrder(Member member, OrderRequestDto orderRequestDto) {
        Option option = optionRepository.findById(orderRequestDto.optionId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 옵션을 찾을 수 없습니다."));

        option.subtractQuantity(orderRequestDto.quantity());

        Order order = new Order(option, member, orderRequestDto.quantity(), orderRequestDto.message());
        Order savedOrder = orderRepository.save(order);

        Optional<Wish> findWish = wishRepository.findByMemberAndProduct(member, option.getProduct());
        findWish.ifPresent(wishRepository::delete);

        kakaoMessageService.sendOrderMessage(member.getKakaoAccessToken(), savedOrder);
        return OrderResponseDto.from(savedOrder);
    }
}

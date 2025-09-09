package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.dto.OptionRequestDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class WishRepositoryTest {
    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member member;
    private Product product;

    private List<OptionRequestDto> options = List.of(
            new OptionRequestDto("테스트용 옵션", 100),
            new OptionRequestDto("테스트용 옵션2", 100)
    );

    @BeforeEach
    void Setup() {
        member = memberRepository.saveAndFlush(new Member("test@naver.com", "qwe123", "LOCAL"));
        product = productRepository.saveAndFlush(new Product("test 상품", BigDecimal.valueOf(3000), "testurl@naver.com", options));
    }

    @Test
    void 위시리스트_저장() {
        Wish wish = new Wish(member, product);
        Wish savedWish = wishRepository.save(wish);

        assertAll(
                () -> assertThat(savedWish.getId()).isNotNull(),
                () -> assertThat(savedWish.getMember().getPassword()).isEqualTo("qwe123"),
                () -> assertThat(savedWish.getProduct().getId()).isEqualTo(product.getId())
        );
    }

    @Test
    void 위시리스트_조회() {
        Wish wish = new Wish(member, product);
        wishRepository.save(wish);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

        Page<Wish> wishPage = wishRepository.findByMember(member, pageable);
        List<Wish> wishes = wishPage.getContent();

        assertAll(
                () -> assertThat(wishPage.getTotalElements()).isEqualTo(1),
                () -> assertThat(wishPage.getTotalPages()).isEqualTo(1),
                () -> assertThat(wishes.get(0).getProduct().getPrice()).isEqualTo(BigDecimal.valueOf(3000)),
                () -> assertThat(wishes.get(0).getProduct().getName()).isEqualTo("test 상품")
        );
    }

}

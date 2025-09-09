package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.dto.OptionRequestDto;
import gift.entity.Product;
import gift.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private List<OptionRequestDto> options = List.of(
            new OptionRequestDto("테스트용 옵션", 100),
            new OptionRequestDto("테스트용 옵션2", 100)
        );

    @Test
    void 상품_생성() {
        Product savedProduct = productRepository.save(new Product("test 상품", BigDecimal.valueOf(3000), "testurl@example.com", options));

        assertAll(
                () -> assertThat(savedProduct.getId()).isNotNull(),
                () -> assertThat(savedProduct.getName()).isEqualTo("test 상품")
        );
    }

    @Test
    void 상품_조회() {
        List<Product> products = productRepository.findAll();

        assertThat(products.size()).isEqualTo(3); // data.sql에서 3개의 상품이 추가되므로 사이즈를 3으로 비교
    }

    @Test
    void 상품_수정() {
        Product savedProduct = productRepository.save(new Product("test 상품", BigDecimal.valueOf(3000), "testurl@example.com", options));
        Long productId = savedProduct.getId();

        savedProduct.updateProduct("test 상품 수정", BigDecimal.valueOf(7777), "update_url@example.com");

        Product updatedProduct = productRepository.findById(productId).orElseThrow();

        assertAll(
                () -> assertThat(updatedProduct.getName()).isEqualTo("test 상품 수정"),
                () -> assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(7777))
        );
    }

    @Test
    void 상품_삭제() {
        Product savedProduct = productRepository.save(new Product("test 상품", BigDecimal.valueOf(3000), "testurl@example.com", options));
        Long productId = savedProduct.getId();

        productRepository.deleteById(productId);

        assertThat(productRepository.findById(productId)).isEmpty();
    }

}

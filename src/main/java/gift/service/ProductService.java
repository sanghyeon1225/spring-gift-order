package gift.service;

import gift.dto.OptionRequestDto;
import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.dto.UpdateProductRequestDto;
import gift.entity.Product;
import gift.exception.DuplicateOptionNameException;
import gift.exception.ResourceNotFoundException;
import gift.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        // 중복된 이름의 옵션이 있는지 확인
        List<String> optionNames = productRequestDto.options().stream()
                .map(OptionRequestDto::name)
                .toList();
        Set<String> uniqueOptionNames = new HashSet<>(optionNames);
        if (optionNames.size() != uniqueOptionNames.size()) {
            throw new DuplicateOptionNameException("요청에 중복된 옵션 이름이 존재할 수 없습니다.");
        }

        Product product = new Product(
                productRequestDto.name(),
                productRequestDto.price(),
                productRequestDto.imageUrl(),
                productRequestDto.options()
        );

        Product saveProduct = productRepository.save(product);
        return new ProductResponseDto(
                saveProduct.getId(),
                saveProduct.getName(),
                saveProduct.getPrice(),
                saveProduct.getImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(p -> new ProductResponseDto(
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getImageUrl()
            ));
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 id 입니다."));

        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageUrl()
        );
    }

    @Transactional
    public ProductResponseDto updateProduct(UpdateProductRequestDto productRequestDto) {
        Product product = productRepository.findById(productRequestDto.id())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 id 입니다."));

        product.updateProduct(productRequestDto.name(), productRequestDto.price(), productRequestDto.imageUrl());

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }


    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("존재하지 않는 id 입니다.");
        }

        productRepository.deleteById(id);
    }
}

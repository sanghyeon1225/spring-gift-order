package gift.service;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.ResourceNotFoundException;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDto> getOptions(Long productId) {
        Product product = findProductById(productId);

        return product.getOptions().stream()
                .map(option -> new OptionResponseDto(
                        option.getId(),
                        option.getName(),
                        option.getQuantity())
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public OptionResponseDto addOption(Long productId, OptionRequestDto optionRequestDto) {
        Product product = findProductById(productId);

        Option option = new Option(
                optionRequestDto.name(),
                optionRequestDto.quantity(),
                product
        );

        product.addOption(option);

        Option savedOption = optionRepository.save(option);
        return new OptionResponseDto(
                savedOption.getId(),
                savedOption.getName(),
                savedOption.getQuantity()
        );
    }

    @Transactional
    public OptionResponseDto updateOption(Long productId, Long optionId, OptionRequestDto optionRequestDto) {
        Product product = findProductById(productId);

        product.updateOption(optionId, optionRequestDto.name(), optionRequestDto.quantity());

        Option updateOption = findOptionById(optionId);

        return new OptionResponseDto(
                updateOption.getId(),
                updateOption.getName(),
                updateOption.getQuantity()
        );
    }

    @Transactional
    public void deleteOption(Long productId, Long optionId) {
        Product product = findProductById(productId);

        product.removeOption(optionId);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));
    }
    private Option findOptionById(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("옵션을 찾을 수 없습니다. ID: " + optionId));
    }

    @Transactional
    public void subtractQuantity(Long optionId, int amount) {
        Option option = findOptionById(optionId);

        option.subtractQuantity(amount);
    }
}

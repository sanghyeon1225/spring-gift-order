package gift.entity;

import gift.dto.OptionRequestDto;
import gift.exception.DuplicateOptionNameException;
import gift.exception.InvalidEntityDataException;
import gift.exception.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 15)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product() {}

    public Product(String name, BigDecimal price, String imageUrl, List<OptionRequestDto> optionRequestDtoList) {
        if (optionRequestDtoList == null || optionRequestDtoList.isEmpty()) {
            throw new InvalidEntityDataException("상품에는 최소 한 개 이상의 옵션이 필요합니다.");
        }
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;

        optionRequestDtoList.forEach(optionRequestDto -> {
            this.addOption(new Option(optionRequestDto.name(), optionRequestDto.quantity(), this));
        });
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void updateProduct(String name, BigDecimal price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void addOption(Option option) {
        boolean isNameDuplicate = this.options.stream()
                .anyMatch(existingOption -> existingOption.getName().equals(option.getName()));

        if (isNameDuplicate) {
            throw new DuplicateOptionNameException("이미 존재하는 옵션 이름입니다: " + option.getName());
        }
        this.options.add(option);
        option.setProduct(this);
    }

    public void updateOption(Long optionId, String name, int quantity) {
        Option option = findOptionByOptionId(optionId);

        if (!option.getName().equals(name)) {
            boolean isNameDuplicate = this.options.stream()
                    .anyMatch(existingOption -> existingOption.getName().equals(name));
            if (isNameDuplicate) {
                throw new DuplicateOptionNameException("이미 존재하는 옵션 이름입니다: " + option.getName());
            }
        }
        option.updateOption(name, quantity);
    }

    public void removeOption(Long optionId) {
        if (this.options.size() <= 1) {
            throw new InvalidEntityDataException("상품에는 최소 한 개의 옵션이 존재해야 하므로 삭제할 수 없습니다.");
        }
        Option option = findOptionByOptionId(optionId);
        this.options.remove(option);
    }

    private Option findOptionByOptionId(Long optionId) {
        return this.options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품에 ID가 " + optionId + "인 옵션이 없습니다."));
    }

}

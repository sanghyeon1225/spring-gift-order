package gift.entity;

import gift.exception.InvalidEntityDataException;
import gift.exception.OutOfQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.regex.Pattern;

@Entity
@Table(name = "options", uniqueConstraints = {
        @UniqueConstraint(
                name = "UniqueOptionName",
                columnNames = {"name", "product_id"}
        )
})
public class Option {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 100_000_000;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Option() {}

    public Option(String name, int quantity, Product product) {
        validate(name, quantity);
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void updateOption(String name, int quantity) {
        validate(name, quantity);
        this.name = name;
        this.quantity = quantity;
    }

    public void subtractQuantity(int amount) {
        if (this.quantity < amount) {
            throw new OutOfQuantityException("수량이 부족합니다. (남은 수량: " + this.quantity + ")");
        }

        this.quantity -= amount;
    }

    private void validate(String name, int quantity) {
        validateName(name);

        validateQuantity(quantity);
    }

    private void validateName(String name) {
        if ( name == null || name.isBlank()) {
            throw new InvalidEntityDataException("옵션 이름은 null 이거나 공백일 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidEntityDataException("옵션 이름은 공백 포함 최대 " + MAX_NAME_LENGTH + "자까지 입력 할 수 있습니다.");
        }
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidEntityDataException("옵션 이름의 특수기호는 ( ) [ ] + - & / _ 만 허용됩니다.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY) {
            throw new InvalidEntityDataException("수량은 " + MIN_QUANTITY + "개 이상이어야 합니다.");
        }
        if (quantity >= MAX_QUANTITY) {
            throw new InvalidEntityDataException("수량은 " + MAX_QUANTITY + "개 미만이어야 합니다.");
        }
    }
}

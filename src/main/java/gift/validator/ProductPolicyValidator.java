package gift.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductPolicyValidator implements ConstraintValidator<ProductPolicy, ProductPolicyProvider> {

    @Override
    public boolean isValid(ProductPolicyProvider dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (!isPolicyApplicable(dto)) {
            return true;
        }

        return dto.merchandiserApproved();
    }

    private boolean isPolicyApplicable(ProductPolicyProvider dto) {
        return dto.name() != null && dto.name().contains("카카오");
    }
}

package shopping.cart.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import shopping.cart.dto.request.CartItemInsertRequest;

@Component
public class CartItemInsertRequestValidator implements Validator {

    private static final String CODE_PRODUCT_ID = "productId";
    private static final String CODE_REQUIRED = "required";

    @Override
    public boolean supports(final Class<?> clazz) {
        return CartItemInsertRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CartItemInsertRequest cartItemInsertRequest = (CartItemInsertRequest) target;
        final Long productId = cartItemInsertRequest.getProductId();

        if (productId == null) {
            errors.rejectValue(CODE_PRODUCT_ID, CODE_REQUIRED, new Object[]{CODE_PRODUCT_ID}, null);
        }
    }
}

package shopping.cart.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import shopping.cart.dto.request.CartItemUpdateRequest;

@Component
public class CartItemUpdateRequestValidator implements Validator {

    private static final String FIELD_QUANTITY = "quantity";
    private static final String CODE_REQUIRED = "required";

    @Override
    public boolean supports(final Class<?> clazz) {
        return CartItemUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final CartItemUpdateRequest cartItemUpdateRequest = (CartItemUpdateRequest) target;
        final Integer quantity = cartItemUpdateRequest.getQuantity();

        if (quantity == null) {
            errors.rejectValue(FIELD_QUANTITY, CODE_REQUIRED, new Object[]{FIELD_QUANTITY}, null);
        }
    }
}

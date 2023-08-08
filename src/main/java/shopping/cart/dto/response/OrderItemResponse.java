package shopping.cart.dto.response;

import shopping.cart.domain.entity.OrderItem;

public final class OrderItemResponse {

    private String productName;
    private long price;
    private String imageFileName;
    private int quantity;

    private OrderItemResponse() {
    }

    private OrderItemResponse(final String productName, final long price,
        final String imageFileName, final int quantity) {
        this.productName = productName;
        this.price = price;
        this.imageFileName = imageFileName;
        this.quantity = quantity;
    }

    public static OrderItemResponse from(final OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getProductName(), orderItem.getPrice().getValue(),
            orderItem.getImageFileName(), orderItem.getQuantity().getValue());
    }

    public String getProductName() {
        return productName;
    }

    public long getPrice() {
        return price;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public int getQuantity() {
        return quantity;
    }
}

package io.vyndra.order.domain.model;

/**
 * A single line item within an {@link Order}.
 *
 * <p>Captures what product was ordered, in what quantity, and at what unit price.
 */
public record OrderItem(
        String productId,
        String productName,
        int quantity,
        Money unitPrice
) {

    /**
     * Validates that all fields are non-null and quantity is positive.
     */
    public OrderItem {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be blank");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("unitPrice must not be null");
        }
    }

    /**
     * Calculates the total price for this line item.
     *
     * @return unit price multiplied by quantity
     */
    public Money totalPrice() {
        return unitPrice.multiply(quantity);
    }
}

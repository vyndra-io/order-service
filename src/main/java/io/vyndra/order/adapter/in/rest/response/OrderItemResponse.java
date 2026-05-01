package io.vyndra.order.adapter.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vyndra.order.domain.model.OrderItem;

import java.math.BigDecimal;

/**
 * Response representation of a single order line item.
 */
@Schema(description = "A line item within an order")
public record OrderItemResponse(

        @Schema(description = "Product identifier", example = "prod-42")
        String productId,

        @Schema(description = "Human-readable product name", example = "Wireless Keyboard")
        String productName,

        @Schema(description = "Quantity ordered", example = "2")
        int quantity,

        @Schema(description = "Unit price in USD", example = "49.99")
        BigDecimal unitPrice,

        @Schema(description = "Total price for this line item (quantity × unitPrice)", example = "99.98")
        BigDecimal totalPrice
) {

    /**
     * Builds a response from a domain {@link OrderItem}.
     *
     * @param item the domain item
     * @return the response representation
     */
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPrice().amount(),
                item.totalPrice().amount()
        );
    }
}

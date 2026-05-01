package io.vyndra.order.adapter.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response representation of an {@link Order}.
 */
@Schema(description = "Order details")
public record OrderResponse(

        @Schema(description = "Unique order identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Identifier of the customer who placed the order", example = "cust-001")
        String customerId,

        @Schema(description = "Current lifecycle status of the order")
        OrderStatus status,

        @Schema(description = "Line items in the order")
        List<OrderItemResponse> items,

        @Schema(description = "Total order value in USD", example = "149.97")
        BigDecimal totalAmount,

        @Schema(description = "Currency code for all monetary values", example = "USD")
        String currency,

        @Schema(description = "Timestamp when the order was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the order was last updated")
        Instant updatedAt
) {

    /**
     * Builds a response from a domain {@link Order}.
     *
     * @param order the domain order
     * @return the response representation
     */
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId().toString(),
                order.getCustomerId(),
                order.getStatus(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.totalAmount().amount(),
                order.totalAmount().currency(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}

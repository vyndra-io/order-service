package io.vyndra.order.adapter.in.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * A single line item within a {@link PlaceOrderRequest}.
 */
@Schema(description = "A product line item in an order")
public record OrderItemRequest(

        @Schema(description = "Product identifier", example = "prod-42")
        @NotBlank(message = "productId must not be blank")
        String productId,

        @Schema(description = "Human-readable product name", example = "Wireless Keyboard")
        @NotBlank(message = "productName must not be blank")
        String productName,

        @Schema(description = "Quantity ordered", example = "2")
        @Positive(message = "quantity must be positive")
        int quantity,

        @Schema(description = "Unit price in USD", example = "49.99")
        @Positive(message = "unitPrice must be positive")
        BigDecimal unitPrice
) {}

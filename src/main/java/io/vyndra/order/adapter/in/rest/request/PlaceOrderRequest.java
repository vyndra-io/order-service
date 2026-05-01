package io.vyndra.order.adapter.in.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * HTTP request body for placing a new order.
 */
@Schema(description = "Request to place a new order")
public record PlaceOrderRequest(

        @Schema(description = "Identifier of the customer placing the order", example = "cust-001")
        @NotBlank(message = "customerId must not be blank")
        String customerId,

        @Schema(description = "Line items to include in the order")
        @NotEmpty(message = "items must not be empty")
        @Valid
        List<OrderItemRequest> items
) {}

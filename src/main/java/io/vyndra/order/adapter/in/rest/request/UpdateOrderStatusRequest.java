package io.vyndra.order.adapter.in.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.vyndra.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * HTTP request body for updating an order's status.
 */
@Schema(description = "Request to update the status of an order")
public record UpdateOrderStatusRequest(

        @Schema(description = "The target status", example = "CONFIRMED")
        @NotNull(message = "status must not be null")
        OrderStatus status
) {}

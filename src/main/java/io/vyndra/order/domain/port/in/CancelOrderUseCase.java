package io.vyndra.order.domain.port.in;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;

/**
 * Driving port for cancelling an order.
 */
public interface CancelOrderUseCase {

    /**
     * Cancels the order with the given identifier.
     *
     * @param id the order identifier
     * @return the cancelled order
     * @throws IllegalArgumentException if the order is not found
     * @throws IllegalStateException    if the order cannot be cancelled in its current state
     */
    Order cancelOrder(OrderId id);
}

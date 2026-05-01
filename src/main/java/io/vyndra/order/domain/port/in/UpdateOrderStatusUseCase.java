package io.vyndra.order.domain.port.in;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;
import io.vyndra.order.domain.model.OrderStatus;

/**
 * Driving port for transitioning an order through its lifecycle.
 */
public interface UpdateOrderStatusUseCase {

    /**
     * Advances the order's status to the given target state.
     *
     * @param id     the order identifier
     * @param target the desired next status
     * @return the updated order
     * @throws IllegalArgumentException if the order is not found
     * @throws IllegalStateException    if the transition is not allowed
     */
    Order updateStatus(OrderId id, OrderStatus target);
}

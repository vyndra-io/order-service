package io.vyndra.order.domain.port.out;

import io.vyndra.order.domain.model.Order;

/**
 * Driven port for publishing order-related domain events to the message broker.
 */
public interface OrderEventPublisher {

    /**
     * Publishes an event signalling that a new order has been placed.
     *
     * @param order the newly placed order
     */
    void publishOrderCreated(Order order);

    /**
     * Publishes an event signalling that an order has been cancelled.
     *
     * @param order the cancelled order
     */
    void publishOrderCancelled(Order order);
}

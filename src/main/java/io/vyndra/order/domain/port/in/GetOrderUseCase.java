package io.vyndra.order.domain.port.in;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Driving port for reading order data.
 */
public interface GetOrderUseCase {

    /**
     * Retrieves an order by its identifier.
     *
     * @param id the order identifier
     * @return the order, or empty if not found
     */
    Optional<Order> getOrder(OrderId id);

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of orders, possibly empty
     */
    List<Order> getOrdersByCustomer(String customerId);
}

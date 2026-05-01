package io.vyndra.order.domain.port.out;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Driven port for persisting and retrieving {@link Order} aggregates.
 */
public interface OrderRepository {

    /**
     * Persists a new or updated order.
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);

    /**
     * Finds an order by its identifier.
     *
     * @param id the order identifier
     * @return the order, or empty if not found
     */
    Optional<Order> findById(OrderId id);

    /**
     * Finds all orders for a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of orders, possibly empty
     */
    List<Order> findByCustomerId(String customerId);
}

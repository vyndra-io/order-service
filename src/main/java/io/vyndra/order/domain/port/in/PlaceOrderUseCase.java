package io.vyndra.order.domain.port.in;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderItem;

import java.util.List;

/**
 * Driving port for placing a new order.
 */
public interface PlaceOrderUseCase {

    /**
     * Places a new order for the given customer.
     *
     * @param customerId the identifier of the customer placing the order
     * @param items      the line items to include in the order
     * @return the newly created order
     * @throws IllegalArgumentException if customerId is blank or items is empty
     */
    Order placeOrder(String customerId, List<OrderItem> items);
}

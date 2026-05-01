package io.vyndra.order.domain.service;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;
import io.vyndra.order.domain.model.OrderItem;
import io.vyndra.order.domain.model.OrderStatus;
import io.vyndra.order.domain.port.in.CancelOrderUseCase;
import io.vyndra.order.domain.port.in.GetOrderUseCase;
import io.vyndra.order.domain.port.in.PlaceOrderUseCase;
import io.vyndra.order.domain.port.in.UpdateOrderStatusUseCase;
import io.vyndra.order.domain.port.out.OrderEventPublisher;
import io.vyndra.order.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing all order use cases.
 *
 * <p>Coordinates between domain logic and driven ports (repository, event publisher).
 * Each method runs within a transaction boundary.
 */
@Service
@Transactional
public class OrderService implements PlaceOrderUseCase, GetOrderUseCase,
        UpdateOrderStatusUseCase, CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order placeOrder(String customerId, List<OrderItem> items) {
        Order order = Order.place(customerId, items);
        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCreated(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(OrderId id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Order updateStatus(OrderId id, OrderStatus target) {
        Order order = findOrThrow(id);
        applyStatusTransition(order, target);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(OrderId id) {
        Order order = findOrThrow(id);
        order.cancel();
        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCancelled(saved);
        return saved;
    }

    private Order findOrThrow(OrderId id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    private void applyStatusTransition(Order order, OrderStatus target) {
        switch (target) {
            case CONFIRMED -> order.confirm();
            case SHIPPED -> order.markShipped();
            case DELIVERED -> order.markDelivered();
            case CANCELLED -> order.cancel();
            default -> throw new IllegalArgumentException("Unsupported status transition to: " + target);
        }
    }
}

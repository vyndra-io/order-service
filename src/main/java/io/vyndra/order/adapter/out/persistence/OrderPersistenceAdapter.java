package io.vyndra.order.adapter.out.persistence;

import io.vyndra.order.domain.model.*;
import io.vyndra.order.domain.port.out.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing {@link OrderRepository} using Spring Data JPA.
 *
 * <p>Translates between domain objects and JPA entities in both directions.
 */
@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId().value(),
                order.getCustomerId(),
                order.getStatus(),
                order.totalAmount().currency(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
        order.getItems().forEach(item ->
                entity.getItems().add(new OrderItemEntity(
                        entity,
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice().amount()
                ))
        );
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(i -> new OrderItem(
                        i.getProductId(),
                        i.getProductName(),
                        i.getQuantity(),
                        Money.usd(i.getUnitPrice())
                ))
                .toList();
        return Order.reconstitute(
                new OrderId(entity.getId()),
                entity.getCustomerId(),
                items,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

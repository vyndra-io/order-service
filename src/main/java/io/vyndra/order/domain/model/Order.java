package io.vyndra.order.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Order aggregate root.
 *
 * <p>An order represents a customer's intent to purchase one or more products.
 * All state transitions are enforced through domain methods to keep invariants intact.
 */
public class Order {

    private final OrderId id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Order(OrderId id, String customerId, List<OrderItem> items,
                  OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a new order in {@link OrderStatus#PENDING} state.
     *
     * @param customerId the identifier of the customer placing the order
     * @param items      the line items; must not be empty
     * @return the new order
     * @throws IllegalArgumentException if customerId is blank or items is empty
     */
    public static Order place(String customerId, List<OrderItem> items) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        Instant now = Instant.now();
        return new Order(OrderId.generate(), customerId, items, OrderStatus.PENDING, now, now);
    }

    /**
     * Reconstitutes an order from persisted state.
     *
     * @param id         the order's identifier
     * @param customerId the customer identifier
     * @param items      the line items
     * @param status     the current status
     * @param createdAt  the creation timestamp
     * @param updatedAt  the last-updated timestamp
     * @return the reconstituted order
     */
    public static Order reconstitute(OrderId id, String customerId, List<OrderItem> items,
                                     OrderStatus status, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, items, status, createdAt, updatedAt);
    }

    /**
     * Confirms a pending order, transitioning it to {@link OrderStatus#CONFIRMED}.
     *
     * @throws IllegalStateException if the order is not in {@link OrderStatus#PENDING} state
     */
    public void confirm() {
        requireStatus(OrderStatus.PENDING);
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the order as shipped, transitioning it to {@link OrderStatus#SHIPPED}.
     *
     * @throws IllegalStateException if the order is not in {@link OrderStatus#CONFIRMED} state
     */
    public void markShipped() {
        requireStatus(OrderStatus.CONFIRMED);
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the order as delivered, transitioning it to {@link OrderStatus#DELIVERED}.
     *
     * @throws IllegalStateException if the order is not in {@link OrderStatus#SHIPPED} state
     */
    public void markDelivered() {
        requireStatus(OrderStatus.SHIPPED);
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = Instant.now();
    }

    /**
     * Cancels the order if it has not yet been shipped.
     *
     * @throws IllegalStateException if the order is in {@link OrderStatus#SHIPPED},
     *                               {@link OrderStatus#DELIVERED}, or {@link OrderStatus#CANCELLED} state
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED
                || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot cancel order in status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    /**
     * Calculates the total value of all line items in this order.
     *
     * @return the sum of all item total prices
     */
    public Money totalAmount() {
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(Money.usd(java.math.BigDecimal.ZERO), Money::add);
    }

    public OrderId getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    /** @return an unmodifiable view of the order's line items */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void requireStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Expected status " + expected + " but was " + this.status);
        }
    }
}

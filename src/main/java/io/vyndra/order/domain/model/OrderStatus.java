package io.vyndra.order.domain.model;

/**
 * Lifecycle states of an {@link Order}.
 */
public enum OrderStatus {

    /** Order has been placed but not yet confirmed. */
    PENDING,

    /** Order has been confirmed and is being prepared. */
    CONFIRMED,

    /** Order has been handed to the shipping service. */
    SHIPPED,

    /** Order has been delivered to the customer. */
    DELIVERED,

    /** Order has been cancelled. */
    CANCELLED
}

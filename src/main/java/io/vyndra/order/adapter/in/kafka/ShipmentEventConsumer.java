package io.vyndra.order.adapter.in.kafka;

import io.vyndra.order.domain.model.OrderId;
import io.vyndra.order.domain.model.OrderStatus;
import io.vyndra.order.domain.port.in.UpdateOrderStatusUseCase;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer adapter for shipment-related events published by the shipping service.
 *
 * <p>Translates incoming Avro messages into domain commands and delegates to use cases.
 */
@Component
public class ShipmentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventConsumer.class);

    private final UpdateOrderStatusUseCase updateOrderStatus;

    public ShipmentEventConsumer(UpdateOrderStatusUseCase updateOrderStatus) {
        this.updateOrderStatus = updateOrderStatus;
    }

    /**
     * Handles {@code shipment-status-updated} events and advances the associated order's status.
     *
     * @param record the Avro event record from the shipping service
     */
    @KafkaListener(topics = "${app.kafka.topics.shipment-status-updated}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onShipmentStatusUpdated(GenericRecord record) {
        String orderId = record.get("orderId").toString();
        String shipmentStatus = record.get("status").toString();
        log.info("Received shipment-status-updated for order={} status={}", orderId, shipmentStatus);

        OrderStatus target = mapShipmentStatus(shipmentStatus);
        if (target != null) {
            try {
                updateOrderStatus.updateStatus(OrderId.of(orderId), target);
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.warn("Could not update order status for orderId={}: {}", orderId, e.getMessage());
            }
        }
    }

    private OrderStatus mapShipmentStatus(String shipmentStatus) {
        return switch (shipmentStatus) {
            case "DISPATCHED" -> OrderStatus.SHIPPED;
            case "DELIVERED" -> OrderStatus.DELIVERED;
            default -> {
                log.debug("Ignoring unmapped shipment status: {}", shipmentStatus);
                yield null;
            }
        };
    }
}

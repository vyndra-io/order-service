package io.vyndra.order.adapter.out.kafka;

import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.port.out.OrderEventPublisher;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka adapter implementing {@link OrderEventPublisher} using Avro-serialized messages.
 *
 * <p>Schemas are loaded from the classpath and messages are published to configured topics.
 */
@Component
public class OrderEventPublisherAdapter implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisherAdapter.class);

    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    @Value("${app.kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${app.kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    private final Schema orderCreatedSchema;
    private final Schema orderCancelledSchema;

    public OrderEventPublisherAdapter(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedSchema = loadSchema("/avro/order-created.avsc");
        this.orderCancelledSchema = loadSchema("/avro/order-cancelled.avsc");
    }

    @Override
    public void publishOrderCreated(Order order) {
        GenericRecord record = new GenericData.Record(orderCreatedSchema);
        record.put("orderId", order.getId().toString());
        record.put("customerId", order.getCustomerId());
        record.put("totalAmount", order.totalAmount().amount().doubleValue());
        record.put("currency", order.totalAmount().currency());
        record.put("status", order.getStatus().name());
        record.put("createdAt", order.getCreatedAt().toEpochMilli());

        kafkaTemplate.send(orderCreatedTopic, order.getId().toString(), record);
        log.info("Published order-created event for orderId={}", order.getId());
    }

    @Override
    public void publishOrderCancelled(Order order) {
        GenericRecord record = new GenericData.Record(orderCancelledSchema);
        record.put("orderId", order.getId().toString());
        record.put("customerId", order.getCustomerId());
        record.put("reason", "Customer requested cancellation");
        record.put("cancelledAt", order.getUpdatedAt().toEpochMilli());

        kafkaTemplate.send(orderCancelledTopic, order.getId().toString(), record);
        log.info("Published order-cancelled event for orderId={}", order.getId());
    }

    private Schema loadSchema(String path) {
        try (var stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Avro schema not found on classpath: " + path);
            }
            return new Schema.Parser().parse(stream);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Avro schema: " + path, e);
        }
    }
}

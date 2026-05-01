package io.vyndra.order.domain.model;

import java.util.UUID;

/**
 * Strongly-typed identifier for an {@link Order}.
 *
 * <p>Wrapping the raw UUID in a value object prevents accidental mix-ups between
 * identifiers of different aggregate types.
 */
public record OrderId(UUID value) {

    /**
     * Creates a new random {@code OrderId}.
     *
     * @return a new {@code OrderId} backed by a random UUID
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * Parses an {@code OrderId} from its string representation.
     *
     * @param value the UUID string
     * @return the parsed {@code OrderId}
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static OrderId of(String value) {
        return new OrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

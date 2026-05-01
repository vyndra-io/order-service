package io.vyndra.order.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Monetary value paired with a currency code.
 *
 * <p>All arithmetic is performed with {@link RoundingMode#HALF_UP} and two decimal places.
 */
public record Money(BigDecimal amount, String currency) {

    /**
     * Validates that amount is non-null and non-negative, and that currency is non-blank.
     */
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be blank");
        }
    }

    /**
     * Creates a {@code Money} instance for the given amount in USD.
     *
     * @param amount the monetary amount
     * @return a USD-denominated {@code Money}
     */
    public static Money usd(BigDecimal amount) {
        return new Money(amount.setScale(2, RoundingMode.HALF_UP), "USD");
    }

    /**
     * Adds another {@code Money} to this one.
     *
     * @param other the amount to add; must share the same currency
     * @return the sum
     * @throws IllegalArgumentException if currencies differ
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount).setScale(2, RoundingMode.HALF_UP), currency);
    }

    /**
     * Multiplies this monetary value by an integer quantity.
     *
     * @param quantity the multiplier; must be positive
     * @return the product
     */
    public Money multiply(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP), currency);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: " + currency + " vs " + other.currency);
        }
    }
}

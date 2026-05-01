package io.vyndra.order.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link OrderEntity}.
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * Finds all orders belonging to a specific customer.
     *
     * @param customerId the customer identifier
     * @return a list of matching order entities
     */
    List<OrderEntity> findByCustomerId(String customerId);
}

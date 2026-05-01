package io.vyndra.order.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vyndra.order.adapter.in.rest.request.PlaceOrderRequest;
import io.vyndra.order.adapter.in.rest.request.UpdateOrderStatusRequest;
import io.vyndra.order.adapter.in.rest.response.OrderResponse;
import io.vyndra.order.domain.model.Money;
import io.vyndra.order.domain.model.Order;
import io.vyndra.order.domain.model.OrderId;
import io.vyndra.order.domain.model.OrderItem;
import io.vyndra.order.domain.port.in.CancelOrderUseCase;
import io.vyndra.order.domain.port.in.GetOrderUseCase;
import io.vyndra.order.domain.port.in.PlaceOrderUseCase;
import io.vyndra.order.domain.port.in.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST adapter exposing the order management API.
 *
 * <p>All input validation happens here; domain logic stays in use cases.
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management API")
public class OrderController {

    private final PlaceOrderUseCase placeOrder;
    private final GetOrderUseCase getOrder;
    private final UpdateOrderStatusUseCase updateOrderStatus;
    private final CancelOrderUseCase cancelOrder;

    public OrderController(PlaceOrderUseCase placeOrder, GetOrderUseCase getOrder,
                           UpdateOrderStatusUseCase updateOrderStatus, CancelOrderUseCase cancelOrder) {
        this.placeOrder = placeOrder;
        this.getOrder = getOrder;
        this.updateOrderStatus = updateOrderStatus;
        this.cancelOrder = cancelOrder;
    }

    /**
     * Places a new order.
     *
     * @param request the order details
     * @return the created order with HTTP 201
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(i -> new OrderItem(i.productId(), i.productName(), i.quantity(),
                        Money.usd(i.unitPrice())))
                .toList();
        Order order = placeOrder.placeOrder(request.customerId(), items);
        return OrderResponse.from(order);
    }

    /**
     * Retrieves an order by its identifier.
     *
     * @param id the order UUID
     * @return the order, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order UUID") @PathVariable String id) {
        return getOrder.getOrder(OrderId.of(id))
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerId the customer identifier
     * @return list of orders for the customer
     */
    @GetMapping
    @Operation(summary = "Get orders for a customer")
    @ApiResponse(responseCode = "200", description = "Orders retrieved")
    public List<OrderResponse> getOrdersByCustomer(
            @Parameter(description = "Customer identifier") @RequestParam String customerId) {
        return getOrder.getOrdersByCustomer(customerId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    /**
     * Updates the status of an order.
     *
     * @param id      the order UUID
     * @param request the desired next status
     * @return the updated order
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update the status of an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponse updateStatus(
            @Parameter(description = "Order UUID") @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = updateOrderStatus.updateStatus(OrderId.of(id), request.status());
        return OrderResponse.from(order);
    }

    /**
     * Cancels an order.
     *
     * @param id the order UUID
     * @return the cancelled order
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponse cancelOrder(
            @Parameter(description = "Order UUID") @PathVariable String id) {
        return OrderResponse.from(cancelOrder.cancelOrder(OrderId.of(id)));
    }
}

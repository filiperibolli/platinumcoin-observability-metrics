package com.platinumcoin.observability.infrastructure.rest;

import com.platinumcoin.observability.application.domain.Order;
import com.platinumcoin.observability.application.usecase.CreateOrderUseCase;
import com.platinumcoin.observability.application.usecase.FindOrderNoCacheUseCase;
import com.platinumcoin.observability.application.usecase.FindOrderUseCase;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;
    private final FindOrderNoCacheUseCase findOrderNoCacheUseCase;

    @PostMapping
    @Timed("orders.process.time")
    public ResponseEntity<Order> createOrder(
            @RequestHeader("X-Request-Id") String requestId,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idemKey,
            @RequestBody @Valid Order order) {

        log.info("[{}] Criando pedido. Idempotency-Key: {}", requestId, idemKey);
        Order created = createOrderUseCase.execute(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Timed("cache.process.time")
    public ResponseEntity<Order> getOrder(
            @RequestHeader("X-Request-Id") String requestId,
            @PathVariable UUID id) {

        log.info("[{}] [COM CACHE] Buscando pedido com ID: {}", requestId, id);
        Order order = findOrderUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/no-cache/{id}")
    @Timed("no-cache.process.time")
    public ResponseEntity<Order> getOrderNoCache(
            @RequestHeader("X-Request-Id") String requestId,
            @PathVariable UUID id) {

        log.info("[{}] [SEM CACHE] Buscando pedido direto no banco. ID: {}", requestId, id);
        Order order = findOrderNoCacheUseCase.execute(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/generate-test-data")
    @Transactional
    public ResponseEntity<List<UUID>> generateTestData(
            @RequestHeader("X-Request-Id") String requestId,
            @RequestParam(defaultValue = "100") int amount) {

        log.info("[{}] Gerando {} pedidos de teste", requestId, amount);

        List<UUID> ids = IntStream.range(0, amount)
                .mapToObj(i -> {
                    UUID id = UUID.randomUUID();
                    BigDecimal total = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(10.0, 1000.0));
                    Order order = Order.createOrder(id, total);
                    createOrderUseCase.execute(order);
                    return id;
                })
                .toList();

        return ResponseEntity.ok(ids);
    }
}
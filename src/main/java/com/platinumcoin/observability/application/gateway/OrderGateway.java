package com.platinumcoin.observability.application.gateway;


import com.platinumcoin.observability.application.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderGateway {
    Order save(Order order);
    Optional<Order> findById(UUID id);
}

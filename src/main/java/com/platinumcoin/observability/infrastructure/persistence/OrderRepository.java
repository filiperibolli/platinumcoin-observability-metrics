package com.platinumcoin.observability.infrastructure.persistence;

import com.platinumcoin.observability.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository  extends JpaRepository<OrderEntity, UUID> {}

package com.platinumcoin.observability.application.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.platinumcoin.observability.application.domain.exception.DomainException;
import com.platinumcoin.observability.application.domain.exception.ErrorDetail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    @NotNull(message = "id is required")
    private UUID id;

    @NotNull(message = "total is required")
    private BigDecimal total;

    public static Order createOrder(UUID id, BigDecimal total) {
        Order o = Order.builder()
                .id(id)
                .total(total)
                .build();
        validate(o);
        return o;
    }

    private static void validate(Order order) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if(!(violations.isEmpty())){
            List<ErrorDetail> errors =
                    violations.stream()
                            .map(v->
                                    new ErrorDetail(v.getPropertyPath().toString(),
                                                    v.getMessage(),
                                                    v.getInvalidValue()))
                            .toList();
            String msg = errors.get(0).errorMessage();
            throw new DomainException(msg, "domain_exception", errors);
        }
    }
}
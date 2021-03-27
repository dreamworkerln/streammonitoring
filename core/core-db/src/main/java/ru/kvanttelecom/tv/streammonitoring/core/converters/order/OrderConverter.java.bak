package ru.geekbrains.handmade.ltmbackend.core.converters.order;

import ru.geekbrains.handmade.ltmbackend.core.converters._base.AbstractConverter;
import ru.geekbrains.handmade.ltmbackend.core.entities.Order;
import ru.geekbrains.handmade.ltmbackend.core.specifications.order.OrderSpecBuilder;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.order.OrderDto;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.order.OrderSpecDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter extends AbstractConverter<Order, OrderDto, OrderSpecDto> {

    @Autowired
    public OrderConverter(OrderMapper orderMapper, OrderSpecBuilder orderSpecBuilder) {
        this.entityMapper = orderMapper;
        this.specBuilder = orderSpecBuilder;

        this.entityClass = Order.class;
        this.dtoClass = OrderDto.class;
        this.specClass = OrderSpecDto.class;
    }

    @Override
    protected void validate(Order order) {
        super.validate(order);

        // ... custom Order validation
    }
}

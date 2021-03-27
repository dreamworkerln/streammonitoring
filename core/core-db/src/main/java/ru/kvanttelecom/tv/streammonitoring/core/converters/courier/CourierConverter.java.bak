package ru.geekbrains.handmade.ltmbackend.core.converters.courier;


import ru.geekbrains.handmade.ltmbackend.core.converters._base.AbstractConverter;
import ru.geekbrains.handmade.ltmbackend.core.entities.Courier;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto._base.AbstractSpecDto;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.courier.CourierDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourierConverter extends AbstractConverter<Courier, CourierDto, AbstractSpecDto> {

    @Autowired
    public CourierConverter(CourierMapper courierMapper) {
        this.entityMapper = courierMapper;

        this.entityClass = Courier.class;
        this.dtoClass = CourierDto.class;
        this.specClass = AbstractSpecDto.class;
    }


    @Override
    protected void validate(Courier courier) {
        super.validate(courier);

        // ... custom validation
    }
}

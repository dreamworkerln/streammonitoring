package ru.geekbrains.handmade.ltmbackend.core.converters.courier;

import ru.geekbrains.handmade.ltmbackend.core.converters._base.AbstractMapper;
import ru.geekbrains.handmade.ltmbackend.core.converters.user.UserMapper;
import ru.geekbrains.handmade.ltmbackend.core.entities.Courier;
import ru.geekbrains.handmade.ltmbackend.core.services.CourierService;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.courier.CourierDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Mapper(config = AbstractMapper.class,
    uses = {UserMapper.class})
public abstract class CourierMapper extends AbstractMapper<Courier, CourierDto> {

    @Autowired
    private CourierService courierService;

    @PostConstruct
    private void postConstruct() {
        this.baseRepoAccessService = courierService;
        //constructor = new EntityConstructor();
    }

    public abstract CourierDto toDto(Courier courier);

    @Mapping(target = "orderList", ignore = true)
    public abstract Courier toEntity(CourierDto courierDto);

    @AfterMapping
    void afterMapping(CourierDto source, @MappingTarget Courier target) {
        merge(source, target);
    }

//    protected class EntityConstructor extends Constructor<Courier, CourierDto> {
//        @Override
//        public Courier create(CourierDto dto, Courier entity) {
//            return new Courier(
//                entity.getUser(),
//                dto.getCourierSpecificData()
//            );
//        }
//    }

}

package ru.kvanttelecom.tv.streammonitoring.core.mappers._base;


import org.mapstruct.*;
import ru.dreamworkerln.spring.utils.common.SpringBeanUtilsEx;
import ru.kvanttelecom.tv.streammonitoring.core.dto._base.AbstractDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseRepoAccessService;

import java.util.List;


@MapperConfig(/*componentModel = "spring",*/ unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class AbstractMapper<E extends AbstractEntity, D extends AbstractDto> {

    protected BaseRepoAccessService<E> baseRepoAccessService;


    /**
     * Merge Entity converted from DTO(target) to entity loaded from database(result),
     * exclude null fields on target and PersistentBag(lazy-loaded fields) on result
     * @param source - unconverted Dto from client
     * @param target - converted Dto -> Entity
     * result - entity uploaded by target.id from DB
     * @return merge result
     */
    public E merge(D source, E target) {

        // assign to result converted source dto
        E result = target;

        // two scenarios:
        // 1. source.id != null (source previously was borrowed from DB) - need to UPDATE existing entity in DB

        // entity.id has protected setter -> target.id always be null
        // (it's impossible to convert dto.id to entity.id)

        // Update existing entity in DB
        if(source.getId() != null) {
            // load result from DB (result.id will be != null)
            result = baseRepoAccessService.findById(source.getId())
                .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + source.getId() + " not found"));

            // Merge target to result
            // Copy non-null fields from target to result
            SpringBeanUtilsEx.copyPropertiesExcludeNull(target, result);
            // So got in result fields updated from target(source), and result have id

            // Next need to validate result and so on ...
        }

        // 2. source.id == null (target.id also == null) - need to persist new entity
        return result;
    }

    public abstract E toEntity(D dto);

    public abstract D toDto(E entity);

    public abstract List<D> toDtoList(List<E> entityList);

    public abstract List<E> toEntityList(List<D> dtoList);



//    @AfterMapping
//    public E afterMapping(D source, @MappingTarget E target) {
//        return merge(source, target);
//    }

//    // ====================================================
//
//    // allow to obtain new object from descendants classes
//
//    *
//     * Создаватель сущностей<br>
//     * К примеру - когда приехало DTO, а в базе соответствующая сущность еще не создана,
//     * и просто переконвертировать dto в entity нельзя,
//     * там, к примеру, надо к-то данные еще заполнить в других сущностях либо че-то прочесть из других сущностей.
//     * @param <E>
//     * @param <D>
//
//    protected abstract class Constructor<E extends AbstractEntity, D extends AbstractDto> {
//
//
//        // метод create нельзя размещать внутри AbstractMapper - mapstruct начнет ругань,
//        // что он не знает, куда прикрутить этот ваш create() и что им делать.
//        *
//         *
//         * @param dto то что приехало на входе (в контроллер)
//         * @param entity то, что Mapstruct осилил создать своими силами
//         * @return новое entity,
//
//        public abstract E create(D dto, E entity);
//    }
}

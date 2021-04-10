package ru.kvanttelecom.tv.streammonitoring.core.converters._base;


import org.mapstruct.*;
import ru.dreamworkerln.spring.utils.common.SpringBeanUtilsEx;
import ru.kvanttelecom.tv.streammonitoring.core.dto._base.AbstractDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.services._base.BaseRepoAccessService;

import java.util.List;


@MapperConfig(unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class AbstractMapper<E extends AbstractEntity, D extends AbstractDto> {

    protected BaseRepoAccessService<E> baseRepoAccessService;

//    protected Constructor<E,D> constructor;

//    public void setBaseRepoAccessService(BaseRepoAccessService<E> baseRepoAccessService) {
//        this.baseRepoAccessService = baseRepoAccessService;
//    }

//    /**
//     * Set id for Entity  //-created, -created
//     */
//    public void idMap(AbstractDto source,
//                      AbstractEntity target) {
//
//        Utils.fieldSetter("id", target, source.getId());
//    }

    /**
     * Merge Entity converted from DTO(target) to entity loaded from database(result),
     * exclude null fields on target and PersistentBag(lazy-loaded fields) on result
     * @param source - unconverted Dto from client
     * @param target - converted Dto -> Entity
     * result - entity uploaded by target.id from DB
     * @return merge result
     */
    public E merge(D source, E target) {

        // assign to result entity, converted from dto by mapstruct
        E result = target;

        // source.getId() - cause entity.id has protected setter and target.id always be null
        // Update existing entity
        if(source.getId() != null) {
            //result = baseRepoAccessService.findByIdEager(source.getId());  // Загружаем сущность целиком, без lazy initialization
            result = baseRepoAccessService.findById(source.getId())
                .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + source.getId() + " not found"));
            // Merge entity from DTO to entity loaded from DB
        }

        
        SpringBeanUtilsEx.copyPropertiesExcludeNull(target, result);
        return result;
    }

    public abstract E toEntity(D dto);

    //@Mapping(target = "{created,updated,enabled}", ignore = true)
//    @Mapping(target = "enabled", ignore = true)
//    @Mapping(target = "created", ignore = true)
//    @Mapping(target = "updated", ignore = true)
    public abstract D toDto(E entity);

    public abstract List<D> toDtoList(List<E> entityList);

    public abstract List<E> toEntityList(List<D> dtoList);



    @AfterMapping
    public E afterMapping(D source, @MappingTarget E target) {
        return merge(source, target);
    }

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

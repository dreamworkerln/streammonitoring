package ru.kvanttelecom.tv.streammonitoring.core.specifications._base;

import org.springframework.data.jpa.domain.Specification;
import ru.kvanttelecom.tv.streammonitoring.core.dto._base.AbstractSpecDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

public interface SpecBuilder<E extends AbstractEntity, S extends AbstractSpecDto> {
    Specification<E> build(S specDto);
}

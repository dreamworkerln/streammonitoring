package ru.kvanttelecom.tv.streammonitoring.core.caches;

import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import java.util.List;
import java.util.Optional;

public interface Cachelevel<T extends AbstractEntity> {

    Optional<T> findById(Long id);
    List<T> findAllById(Iterable<Long> ids);
    List<T> findAll();
    T save(T t);
    List<T> saveAll(Iterable<T> list);
    void delete(T t);
    void deleteAll(Iterable<T> list);
    int size();
}

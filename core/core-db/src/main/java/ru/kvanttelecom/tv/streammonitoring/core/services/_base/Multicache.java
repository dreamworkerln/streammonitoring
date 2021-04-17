package ru.kvanttelecom.tv.streammonitoring.core.services._base;

import lombok.extern.slf4j.Slf4j;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Сервис с многоуровневым кешем.
 * Обычно имеет верхним уровнем HashMap, нижним уровнем БД.
 * При старте все данные поднимаются из БД в верхний уровень.
 * Далее вся работа производится в верхнем уровне.
 * При вызове методов save(...) данные сразу опускаются в БД.
 * БД используется для durability на время останова приложения.
 * Поэтому данных должно быть не много, иначе OOM.
 *
 * Можно добавить только один уровень - только RAM или только БД


 */
@Slf4j
public abstract class Multicache<T extends AbstractEntity> {


    // levels[0] - это HashmapCache
    // levels[1] - это RepoAccessService
    private List<Cachelevel<T>> levels;

    private Cachelevel<T> firstLevel;
    private Cachelevel<T> lastLevel;

    /**
     *  Initialize cache levels here in descendants
     */
    protected abstract List<Cachelevel<T>> addLevels();

    @PostConstruct
    private void init() {

        // get all cache levels
        levels = addLevels();

        // Validation
        if (levels == null || levels.size() == 0) {
            throw new IllegalArgumentException("Неправильный кеш");
        }

        firstLevel = levels.get(0);
        lastLevel  = levels.get(levels.size() - 1);

        // initialize cache levels --------------------------------

        // get all data from lowest level
        List<T> all = lastLevel.findAll();

        // save it to upper levels
        for(int i = levels.size() - 2; i >= 0; i--) {
            levels.get(i).saveAll(all);
        }
    }

    /**
     * Find entity by id
     */
    public Optional<T> findById(Long id) {
        return firstLevel.findById(id);
    }


    /**
     * Find all entities
     */
    public List<T> findAll() {
        return firstLevel.findAll();
    }


    /**
     * Find all entities by id
     */
    public List<T> findAllById(Iterable<Long> ids) {
        return firstLevel.findAllById(ids);
    }



    /**
     * Save entity
     */
    public T save(T t) {
        // save to DB
        t = lastLevel.save(t);

        // save to upper levels
        for (int i = levels.size() - 2; i >=0; i--) {
            t = levels.get(i).save(t);
        }
        return t;
    }

    /**
     * Save group of entities
     */
    public List<T> saveAll(Iterable<T> list) {

        List<T> result;

        // save to DB
        result = lastLevel.saveAll(list);

        // save to upper levels
        for (int i = levels.size() - 2; i >=0; i--) {
            result = levels.get(i).saveAll(result);
        }
        return result;
    }

    /**
     * Delete entity
     */
    public void delete(T t) {

        //Assert.notNull(t,  t.getClass() + " == null");
        //Assert.notNull(t.getId(), t.getClass() + ".id == null");

        for (int i = levels.size() - 1; i >=0; i--) {
            levels.get(i).delete(t);
        }
    }

    /**
     * Delete group of entities
     */
    public void deleteAll(Iterable<T> values) {

        for (int i = levels.size() - 1; i >=0; i--) {
            levels.get(i).deleteAll(values);
        }
    }


    public int size() {
        return firstLevel.size();
    }
}

package ru.kvanttelecom.tv.streammonitoring.core.services._base;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Кеширует сущности в RAM
 * <br>(Сохраняет сущности + прицепленные к ним графы других объектов, уж что передали в save())
 * Если сущностей много/они большие (понасохраняли большие графы) - будет OOM
 * @param <T>
 */
@Slf4j
public class BaseCachingService<T extends AbstractEntity> {

    protected BaseRepoAccessService<T> repoAccessService;

    private final List<Index<?,T>> indexes = new ArrayList<>();

    private final Index<Long,T> idIndex = new Index<>(AbstractEntity::getId);
    

    public BaseCachingService(BaseRepoAccessService<T> repoAccessService) {
        this.repoAccessService = repoAccessService;

        addIndex(idIndex);
    }


    /**
     * Initialize all indexes from DB
     */
    @PostConstruct
    private void init() {
        for (Index<?, T> i : indexes) {
            i.init(repoAccessService.findAll());
        }
    }


    /**
     * Don't forget to register custom index in descendant classes
     * @param index custom index
     */
    protected void addIndex(Index<?, T> index) {
        indexes.add(index);
    }

    /**
     * Find entity by id
     * <br> cached
     */
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(idIndex.get(id));
    }


    public Optional<T> findById(Long id, EntityGraph entityGraph) {
        return repoAccessService.findById(id, entityGraph);
    }

    /**
     * Find entity by id or throw error
     * <br> cached
     */
    public T findByIdOrError(Long id) {
        return findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public T findByIdOrError(Long id, EntityGraph entityGraph) {
        return repoAccessService.findById(id, entityGraph)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public Optional<T> findOne(Specification<T> spec) {
        return repoAccessService.findOne(spec);
    }

    /**
     * Find all entities by id
     * <br> cached
     */
    public List<T> findAllById(Iterable<Long> listId) {
        return idIndex.getByKeys(listId);
    }

    /**
     * Find all entities
     * <br> cached
     */
    public List<T> findAll() {
        return idIndex.values();
    }

    public List<T> findAll(EntityGraph entityGraph) {
        return repoAccessService.findAll(entityGraph);
    }

    public List<T> findAll(Specification<T> spec) {
        return repoAccessService.findAll(spec);
    }

    public Page<T> findAll(Specification<T> spec, PageRequest pageable) {
        return repoAccessService.findAll(spec, pageable);
    }

    /**
     * Save entity
     * <br> cached
     */
    public T save(T t) {
        // save to DB
        t = repoAccessService.save(t);
        // save to indexes
        for (Index<?, T> i : indexes) {
            i.put(t);
        }
        return t;
    }

    /**
     * Save group of entities
     * <br> cached
     */
    public List<T> saveAll(Iterable<T> list) {
        // save to DB
        List<T> result = repoAccessService.saveAll(list);
        // save to indexes
        for (Index<?, T> i : indexes) {
            i.putAll(result);
        }
        return result;
    }

    /**
     * Delete entity
     * <br> cached
     */
    public void delete(T t) {

        Assert.notNull(t,  t.getClass() + " == null");
        Assert.notNull(t.getId(), t.getClass() + ".id == null");

        // delete from DB
        repoAccessService.delete(t);
        // delete from indexes
        indexes.forEach(i -> i.remove(t));
    }

    /**
     * Delete group of entities
     * <br> cached
     */
    public void deleteAll(Iterable<T> values) {
        // delete from DB
        repoAccessService.deleteAll(values);
        // delete from indexes
        indexes.forEach(i -> values.forEach(i::remove));
    }

//    public void flush() {
//        repoAccessService.flush();
//    }
}



//    protected void addIndex(Index<?, T> index, ) {
//
//        String indexName = index.getName();
//
//        // validate index name uniqueness
//        if(indexes.containsKey(indexName)) {
//            throw new IllegalArgumentException("Adding index " + indexName + " - already exists, class: " +
//                this.getClass().getSimpleName());
//        }
//
//        indexes.put(indexName, index);
//        indexRepositoryLoaders.put(indexName, indexRepositoryLoader);
//    }

// --------------------------------------------------------------------------


//    protected Optional<T> findByIndex(Object key, Index<?, T> index) {
//
//        // get from cache
//        Optional<T> result = Optional.ofNullable(index.get(key));
//        // not found in cache
//        if(result.isEmpty()) {
//            result = indexRepositoryLoaders.get(index.getName()).apply(key);
//            // if found in DB then save to cache
//            result.ifPresent(index::put);
//        }
//        return result;
//    }

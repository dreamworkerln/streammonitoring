package ru.kvanttelecom.tv.streammonitoring.core.caches;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import ru.dreamworkerln.spring.utils.common.Utils;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class MapCache<T extends AbstractEntity> implements Cachelevel<T>  {

    private AtomicLong idGen = new AtomicLong(1);

    private final List<Index<?,T>> indexes = new ArrayList<>();

    private final Index<Long,T> idIndex = new Index<>(AbstractEntity::getId);

    @Getter
    @Setter
    private boolean autogenId = false;

    public MapCache() {
        addIndex(idIndex);
    }

    /**
     * Don't forget to register custom index in descendant classes
     * @param index custom index
     */
    public void addIndex(Index<?, T> index) {
        indexes.add(index);
    }

    // -----------------------------------------------------------------------------------

    @Override
    public Optional<T> findById(Long id) {
        return idIndex.findByKey(id);
    }


    @Override
    public List<T> findAll() {
        return idIndex.findAll();
    }

    @Override
    public List<T> findAllById(Iterable<Long> ids) {
        return idIndex.findAllByKeys(ids);
    }

    @Override
    public T save(T t) {

        // Id auto generation
        if(autogenId && t.getId() == null){Utils.fieldSetter("id", t, idGen.getAndIncrement());}

        // update all indexes
        for (Index<?, T> index : indexes) {
            if (index.isAutoAddition()) {
                index.save(t);
            }
        }
        return t;
    }

    @Override
    public List<T> saveAll(Iterable<T> list) {

        // Id auto generation
        if(autogenId){list.forEach(t -> {if(t.getId() == null) Utils.fieldSetter("id", t, idGen.getAndIncrement());});}


        // update all indexes
        for (Index<?, T> index : indexes) {
            if (index.isAutoAddition()) {
                index.saveAll(list);
            }
        }
        return Lists.newArrayList(list);
    }

    @Override
    public void delete(T t) {
        // update all indexes
        for (Index<?, T> index : indexes) {
            index.delete(t);
        }
    }

    @Override
    public void deleteAll(Iterable<T> list) {
        // update all indexes
        for (Index<?, T> index : indexes) {
            index.deleteAll(list);
        }
    }

    @Override
    public int size() {
        return idIndex.size();
    }
}

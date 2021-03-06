package ru.kvanttelecom.tv.streammonitoring.core.services._base;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.caches.Cachelevel;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Transactional
@Slf4j
public abstract class RepoAccessService<T extends AbstractEntity> implements Cachelevel<T> {

    private final CustomRepository<T, Long> baseRepository;

    protected RepoAccessService(CustomRepository<T, Long> baseRepository) {
        this.baseRepository = baseRepository;
    }

    public Optional<T> findById(Long id) {
        return baseRepository.findById(id);
    }

    public Optional<T> findById(Long id, EntityGraph entityGraph) {
        return baseRepository.findById(id, entityGraph);
    }

    public T findByIdOrError(Long id) {
        return baseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public T findByIdOrError(Long id, EntityGraph entityGraph) {
        return baseRepository.findById(id, entityGraph)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public Optional<T> findOne(Specification<T> spec) {
        return baseRepository.findOne(spec);
    }

    public List<T> findAllById(Iterable<Long> ids) {
        return baseRepository.findAllById(ids);
    }

    public List<T> findAll() {
        return baseRepository.findAll();
    }

    public List<T> findAll(EntityGraph entityGraph) {
        List<T> result = new ArrayList<>();
        baseRepository.findAll(entityGraph).forEach(result::add);
        return result;
    }

    public List<T> findAll(Specification<T> spec) { 
        return baseRepository.findAll(spec);
    }

    public Page<T> findAll(Specification<T> spec, PageRequest pageable) {
        return baseRepository.findAll(spec, pageable);
    }

    public T save(T t) {
        return baseRepository.save(t);
    }

    public List<T> saveAll(Iterable<T> list) {
        return baseRepository.saveAll(list);
    }

    public void delete(T t) {
        baseRepository.delete(t);
    }

    public void deleteAll(Iterable<T> list) {
        baseRepository.deleteAll(list);
    }

    @Override
    public int size() {
        return (int)baseRepository.count();
    }

    @Override
    public boolean containsKey(Long id) {
        return baseRepository.existsById(id);
    }
}

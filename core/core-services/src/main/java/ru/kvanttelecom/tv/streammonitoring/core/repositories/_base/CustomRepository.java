package ru.kvanttelecom.tv.streammonitoring.core.repositories._base;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import ru.kvanttelecom.tv.streammonitoring.core.cache.NaturalKey;

import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
@Transactional
public interface CustomRepository<T, ID extends Serializable> extends EntityGraphJpaRepository<T, ID>, EntityGraphJpaSpecificationExecutor<T> {

    @Transactional
    void refresh(T t);

    @Transactional
    void merge(T t);

    @Transactional
    void detach(T t);
    
    PersistenceUnitUtil getPersistenceUnitUtil();

    void truncateLazy(T v);

    Optional<T> findByKey(NaturalKey keygen);

    boolean existsByKey(NaturalKey keygen);
}
package ru.kvanttelecom.tv.streammonitoring.core.repositories._base;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
@Transactional
public interface CustomRepository<T, ID extends Serializable> extends EntityGraphJpaRepository<T, ID>, EntityGraphJpaSpecificationExecutor<T> {


//    //@Query("select p.id from #{#entityName} p")
//    List<Long> findAllIds();

    @Transactional
    void refresh(T t);

    @Transactional
    void merge(T t);

    @Transactional
    void detach(T t);
    //PersistenceUnitUtil getPersistenceUnitUtil();
}
package ru.kvanttelecom.tv.streammonitoring.monitor.repositories;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamState;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.CustomRepository;
import ru.kvanttelecom.tv.streammonitoring.core.repositories._base.RepositoryWithEntityManager;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Repository that not working with DB, just another level of indirection
 */
@Component
public class FakeStreamStateRepository extends RepositoryWithEntityManager<StreamState, Long> {

    public FakeStreamStateRepository(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(null, null);
    }
}

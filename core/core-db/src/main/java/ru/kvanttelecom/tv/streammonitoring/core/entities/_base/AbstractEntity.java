package ru.kvanttelecom.tv.streammonitoring.core.entities._base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base for simple entities and
 * entities with InheritanceType.TABLE_PER_CLASS
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @Column(name = "id")
    // https://stackoverflow.com/questions/2951454/should-transient-property-be-used-in-equals-hashcode-tostring
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    protected Long id;


    @Column(name = "created", updatable = false)
    @CreationTimestamp
    @Getter
    protected Instant created;


    @Column(name = "updated")
    @UpdateTimestamp
    @Getter
    protected Instant updated;

    @Setter
    @Getter
    protected boolean enabled = true;

    protected AbstractEntity() {}

}

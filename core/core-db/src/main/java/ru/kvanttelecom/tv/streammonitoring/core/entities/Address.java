package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;


@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "address")
public class Address extends AbstractEntity {


    @Embedded
    @Getter
    private Point coordinates;

    @Getter
    private String postAddress;

    protected Address() {}
    public Address(String postAddress, Point coordinates) {
        this.postAddress = postAddress;
        this.coordinates = coordinates;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Objects.equals(coordinates, address.coordinates) &&
            Objects.equals(postAddress, address.postAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, postAddress);
    }

    @Override
    public String toString() {
        return "Address{" +
            "postAddress=" + postAddress +
            ", coordinates='" + coordinates + '\'' +
            '}';
    }
}

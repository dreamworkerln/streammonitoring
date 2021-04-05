package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@EqualsAndHashCode(callSuper = true)
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
}

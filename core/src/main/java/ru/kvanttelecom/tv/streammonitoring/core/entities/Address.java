package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.data.Point;
import ru.kvanttelecom.tv.streammonitoring.core.entities.base.AbstractEntity;

import javax.persistence.Column;
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

    public Address() {}
    public Address(String postAddress, Point coordinates) {
        this.postAddress = postAddress;
        this.coordinates = coordinates;

    }
}

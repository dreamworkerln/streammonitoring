package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Client extends AbstractEntity {

    @Getter
    private String name;


    @OneToMany(mappedBy= "client", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private final List<Stream> streamList = new ArrayList<>();

    public Client(String name) {
        this.name = name;
    }
}

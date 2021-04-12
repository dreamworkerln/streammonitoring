package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Getter;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client")
public class Client extends AbstractEntity {

    @Getter
    private String name;

    @OneToMany(mappedBy= "client", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private final List<Stream> streamList = new ArrayList<>();

    protected Client() {}
    public Client(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return name.equals(client.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

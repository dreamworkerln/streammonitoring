package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server",
    indexes = {
    @Index(name = "server_name_unq", columnList = "name", unique = true)
})
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Server extends AbstractEntity {

    @Getter
    private String name;

    @Getter
    private String url;

    @OneToMany(mappedBy= "server", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private final List<Stream> streamList = new ArrayList<>();

    public Server(String name, String url) {
        this.name = name;
        this.url = url;
    }
}

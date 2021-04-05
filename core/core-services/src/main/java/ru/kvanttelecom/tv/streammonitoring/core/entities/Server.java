package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server",
    indexes = {
    @Index(name = "server_name_unq", columnList = "hostname, domainName", unique = true)
})
@Data
@EqualsAndHashCode(callSuper=false)
public class Server extends AbstractEntity {

    @Getter
    private String hostname; // linux hostname

    @Getter
    private String domainName; // full, without most right dot

    @OneToMany(mappedBy= "server", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private final List<Stream> streamList = new ArrayList<>();

    protected Server() {}

    public Server(String hostname, String domainName) {
        this.hostname = hostname;
        this.domainName = domainName;
    }

    @Override
    public String toString() {
        return "Server{" +
            "hostname='" + hostname + '\'' +
            ", domainName='" + domainName + '\'' +
            '}';
    }
}

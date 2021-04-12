package ru.kvanttelecom.tv.streammonitoring.core.entities;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.kvanttelecom.tv.streammonitoring.core.entities._base.AbstractEntity;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "server",
    indexes = {
    //@Index(name = "server_name_unq", columnList = "hostname, domainName", unique = true),
    @Index(name = "server_hostname_unq", columnList = "hostname", unique = true),
        @Index(name = "server_domainName_unq", columnList = "domainName", unique = true),
})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
public class Server extends AbstractEntity {

    @Getter
    private String hostname; // linux hostname

    @Getter
    private String domainName; // full, without most right dot

    @OneToMany(mappedBy= "server", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<Stream> streamList = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;
        Server server = (Server) o;
        return hostname.equals(server.hostname) &&
            domainName.equals(server.domainName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, domainName);
    }
}

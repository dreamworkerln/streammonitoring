package ru.kvanttelecom.tv.streammonitoring.core.mappers.stream;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kvanttelecom.tv.streammonitoring.core.mappers._base.AbstractMapper;
import ru.kvanttelecom.tv.streammonitoring.core.dto.stream.StreamDto;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Address;
import ru.kvanttelecom.tv.streammonitoring.core.entities.Server;
import ru.kvanttelecom.tv.streammonitoring.core.entities.stream.Stream;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.ServerMultiService;
import ru.kvanttelecom.tv.streammonitoring.core.services.caching.StreamMultiService;

import javax.annotation.PostConstruct;


/*
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
 */

@Mapper(config = AbstractMapper.class)
public abstract class StreamMapper extends AbstractMapper<Stream, StreamDto> {

    @Autowired
    private StreamMultiService streamMultiService;

    @Autowired
    private ServerMultiService serverMultiService;

    @PostConstruct
    private void postConstruct() {
        this.entityAccessService = streamMultiService;
    }







//    @PostConstruct
//    private void postConstruct() {
//        this.baseRepoAccessService = streamService;
//    }

    @Mapping(target = "hostname", source = "server.hostname")
    @Mapping(target = "alive", ignore = true)
    @Mapping(target = "flapping", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "postalAddress", source = "address",  qualifiedByName = "findPostalAddress")
    @Mapping(target = "coordinates", source = "address",  qualifiedByName = "findCoordinates")
    public abstract StreamDto toDto(Stream stream);

    @Mapping(target = "server", source = "hostname",  qualifiedByName = "findServerByHostname")
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "address", ignore = true)
    public abstract Stream toEntity(StreamDto streamDto);


    @Named("findServerByHostname")
    Server findServerByHostname(String hostname) {
        return serverMultiService.findByHostname(hostname).orElseThrow(() ->
            new IllegalArgumentException("Server by hostname '" + hostname + "' not found"));
    }

    @Named("findPostalAddress")
    String findPostalAddress(Address address) {
        return address != null ? address.getPostAddress() : null;
    }

    @Named("findCoordinates")
    String findCoordinates(Address address) {
        return address != null && address.getCoordinates() != null ?
            address.getCoordinates().toString() : null;
    }

    // Стримера не знают о наших Id, в качестве PK используется StreamKey,
    // поэтому подсосем Id вручную (если есть)
    @BeforeMapping
    public void beforeMapping(StreamDto source, @MappingTarget Stream target) {
        streamMultiService.findByKey(source.getStreamKey())
            .ifPresent(l -> source.setId(l.getId()));
    }

    @AfterMapping
    public Stream afterMapping(StreamDto source, @MappingTarget Stream target) {
        return merge(source, target);
    }




//    @AfterMapping
//    private Stream afterMapping(StreamDto source, @MappingTarget Stream target) {
//
//        String name = source.getHostname();
//        // in hostname maybe as server.hostname as server.domainName
//
//        Optional<Server> oServer = serverService.findByHostname(name)
//            .or(() -> serverService.findByDomainName(name));
//        Server server = oServer
//            .orElseThrow(() -> new IllegalArgumentException("Server " + name + " not found"));
//        target.setServer(server);
//
//        return target;
//    }

//    @AfterMapping
//    public Stream afterMapping(StreamDto source, @MappingTarget Stream target) {
//
//        UserDto result = target;
//        result.getAccount().setUser(result);
//        return super.afterMapping(dto. )
//    }



//    protected class EntityConstructor extends Constructor<User, UserDto> {
//
//        //private UserRoleService userRoleService;
//
//        @Override
//        public User create(UserDto dto, User entity) {
//
//        // Mapstruct 1.4 maybe will support constructors with params
//            return null;
//            //return new User();
////            return new User(
////            dto.getUsername(),
////            dto.getPassword(),
////            dto.getFirstName(),
////            dto.getLastName(),
////            dto.getAge(),
////            dto.getEmail(),
////            dto.getPhoneNumber());
//        }
//
//    }


    //@Mapping(target = "postalAddress", expression = "java(stream.getAddress().getPostAddress())")
    //@Mapping(target = "coordinates", expression = "java(stream.getAddress().getCoordinates().toString())")
}

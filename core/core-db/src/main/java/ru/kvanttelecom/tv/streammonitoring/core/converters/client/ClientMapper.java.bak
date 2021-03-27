package ru.geekbrains.handmade.ltmbackend.core.converters.client;

import ru.geekbrains.handmade.ltmbackend.core.converters._base.AbstractMapper;
import ru.geekbrains.handmade.ltmbackend.core.converters.user.UserMapper;
import ru.geekbrains.handmade.ltmbackend.core.entities.Client;
import ru.geekbrains.handmade.ltmbackend.core.services.ClientService;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.client.ClientDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Mapper(config = AbstractMapper.class,
    uses = {UserMapper.class})
public abstract class ClientMapper extends AbstractMapper<Client, ClientDto> {

    @Autowired
    private ClientService clientService;

    @PostConstruct
    private void postConstruct() {
        this.baseRepoAccessService = clientService;
        //constructor = new EntityConstructor();
    }

    public abstract ClientDto toDto(Client client);

    //@Mapping(target = "user", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    public abstract Client toEntity(ClientDto clientDto);

    @AfterMapping
    void afterMapping(ClientDto source, @MappingTarget Client target) {

        //idMap(source, target);
        merge(source, target);
    }

//    protected class EntityConstructor extends Constructor<Client, ClientDto> {
//        @Override
//        public Client create(ClientDto dto, Client entity) {
//            return new Client(
//                entity.getUser(),
//                dto.getClientSpecificData()
//            );
//        }
//    }

}

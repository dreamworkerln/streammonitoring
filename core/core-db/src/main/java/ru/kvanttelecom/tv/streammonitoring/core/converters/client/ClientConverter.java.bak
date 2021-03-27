package ru.geekbrains.handmade.ltmbackend.core.converters.client;


import ru.geekbrains.handmade.ltmbackend.core.converters._base.AbstractConverter;
import ru.geekbrains.handmade.ltmbackend.core.entities.Client;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto._base.AbstractSpecDto;
import ru.geekbrains.handmade.ltmbackend.jrpc_protocol.dto.client.ClientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientConverter extends AbstractConverter<Client, ClientDto, AbstractSpecDto> {

    @Autowired
    public ClientConverter(ClientMapper clientMapper) {
        this.entityMapper = clientMapper;

        this.entityClass = Client.class;
        this.dtoClass = ClientDto.class;
        this.specClass = AbstractSpecDto.class;
    }


    @Override
    protected void validate(Client client) {
        super.validate(client);

        // ... custom validation
    }
}

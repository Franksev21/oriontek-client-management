package com.oriontek.application.handlers.command;

import com.oriontek.application.commands.address.AddressCommand;
import com.oriontek.application.commands.client.ClientCommands;
import com.oriontek.application.commands.client.ClientCommands.ClientCommandResult;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.domain.exception.DuplicateEmailException;
import com.oriontek.infrastructure.persistence.entity.AddressEntity;
import com.oriontek.infrastructure.persistence.entity.ClientEntity;
import com.oriontek.infrastructure.persistence.repository.AddressJpaRepository;
import com.oriontek.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClientCommandHandler {

    private final ClientJpaRepository clientRepo;
    private final AddressJpaRepository addressRepo;

    public ClientCommandHandler(ClientJpaRepository clientRepo, AddressJpaRepository addressRepo) {
        this.clientRepo = clientRepo;
        this.addressRepo = addressRepo;
    }

    public ClientCommandResult handle(ClientCommands.CreateClientCommand cmd) {
        if (clientRepo.existsByEmail(cmd.email())) {
            throw new DuplicateEmailException(cmd.email());
        }

        var client = new ClientEntity(cmd.firstName(), cmd.lastName(), cmd.email(), cmd.phone());

        if (cmd.addresses() != null && !cmd.addresses().isEmpty()) {
            var addresses = mapAddresses(cmd.addresses(), client);
            client.setAddresses(addresses);
        }

        var saved = clientRepo.save(client);
        return new ClientCommandResult.Success(saved.getId(), "Client created successfully");
    }

    public ClientCommandResult handle(ClientCommands.UpdateClientCommand cmd) {
        var client = clientRepo.findById(cmd.id())
            .orElseThrow(() -> new ClientNotFoundException(cmd.id()));

        if (clientRepo.existsByEmailAndIdNot(cmd.email(), cmd.id())) {
            throw new DuplicateEmailException(cmd.email());
        }

        client.setFirstName(cmd.firstName());
        client.setLastName(cmd.lastName());
        client.setEmail(cmd.email());
        client.setPhone(cmd.phone());

        clientRepo.save(client);
        return new ClientCommandResult.Success(cmd.id(), "Client updated successfully");
    }

    public ClientCommandResult handle(ClientCommands.DeleteClientCommand cmd) {
        var client = clientRepo.findById(cmd.id())
            .orElseThrow(() -> new ClientNotFoundException(cmd.id()));

        // Soft delete
        client.softDelete();
        client.getAddresses().forEach(AddressEntity::softDelete);
        clientRepo.save(client);

        return new ClientCommandResult.Success(cmd.id(), "Client deleted successfully");
    }

    private List<AddressEntity> mapAddresses(
        List<AddressCommand.CreateAddressCommand> cmds,
        ClientEntity client
    ) {
        boolean hasPrimary = cmds.stream().anyMatch(AddressCommand.CreateAddressCommand::primary);

        return cmds.stream().map(cmd -> {
            var addr = new AddressEntity();
            addr.setClient(client);
            addr.setStreet(cmd.street());
            addr.setCity(cmd.city());
            addr.setState(cmd.state());
            addr.setCountry(cmd.country());
            addr.setZipCode(cmd.zipCode());
            // If none marked primary, first one becomes primary
            addr.setPrimary(hasPrimary ? cmd.primary() : cmds.indexOf(cmd) == 0);
            return addr;
        }).toList();
    }
}

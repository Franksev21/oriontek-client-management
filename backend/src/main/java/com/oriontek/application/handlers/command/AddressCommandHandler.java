package com.oriontek.application.handlers.command;

import com.oriontek.application.commands.address.AddressCommand;
import com.oriontek.application.commands.address.AddressCommand.AddressCommandResult;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.domain.exception.ResourceNotFoundException;
import com.oriontek.infrastructure.persistence.entity.AddressEntity;
import com.oriontek.infrastructure.persistence.repository.AddressJpaRepository;
import com.oriontek.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressCommandHandler {

    private final AddressJpaRepository addressRepo;
    private final ClientJpaRepository clientRepo;

    public AddressCommandHandler(AddressJpaRepository addressRepo, ClientJpaRepository clientRepo) {
        this.addressRepo = addressRepo;
        this.clientRepo = clientRepo;
    }

    public AddressCommandResult handle(AddressCommand.CreateAddressCommand cmd, java.util.UUID clientId) {
        var client = clientRepo.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (cmd.primary()) {
            addressRepo.clearPrimaryForClient(clientId);
        }

        var address = new AddressEntity();
        address.setClient(client);
        address.setStreet(cmd.street());
        address.setCity(cmd.city());
        address.setState(cmd.state());
        address.setCountry(cmd.country());
        address.setZipCode(cmd.zipCode());
        address.setPrimary(cmd.primary());

        var saved = addressRepo.save(address);
        return new AddressCommandResult.Success(saved.getId(), "Address added successfully");
    }

    public AddressCommandResult handle(AddressCommand.UpdateAddressCommand cmd) {
        var address = addressRepo.findByIdAndClientId(cmd.id(), cmd.clientId())
            .orElseThrow(() -> new ResourceNotFoundException("Address", cmd.id()));

        if (cmd.primary()) {
            addressRepo.clearPrimaryForClient(cmd.clientId());
        }

        address.setStreet(cmd.street());
        address.setCity(cmd.city());
        address.setState(cmd.state());
        address.setCountry(cmd.country());
        address.setZipCode(cmd.zipCode());
        address.setPrimary(cmd.primary());

        addressRepo.save(address);
        return new AddressCommandResult.Success(cmd.id(), "Address updated successfully");
    }

    public AddressCommandResult handle(AddressCommand.DeleteAddressCommand cmd) {
        var address = addressRepo.findByIdAndClientId(cmd.id(), cmd.clientId())
            .orElseThrow(() -> new ResourceNotFoundException("Address", cmd.id()));

        address.softDelete();
        addressRepo.save(address);
        return new AddressCommandResult.Success(cmd.id(), "Address deleted successfully");
    }
}

package com.oriontek.application.handlers;

import com.oriontek.application.commands.address.AddressCommand;
import com.oriontek.application.commands.address.AddressCommand.AddressCommandResult;
import com.oriontek.application.handlers.command.AddressCommandHandler;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.domain.exception.ResourceNotFoundException;
import com.oriontek.infrastructure.persistence.entity.AddressEntity;
import com.oriontek.infrastructure.persistence.entity.ClientEntity;
import com.oriontek.infrastructure.persistence.repository.AddressJpaRepository;
import com.oriontek.infrastructure.persistence.repository.ClientJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressCommandHandler")
class AddressCommandHandlerTest {

    @Mock private AddressJpaRepository addressRepo;
    @Mock private ClientJpaRepository clientRepo;
    @InjectMocks private AddressCommandHandler handler;

    private ClientEntity mockClient;
    private AddressEntity mockAddress;
    private UUID clientId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        mockClient = new ClientEntity("María", "Rodríguez", "maria@test.com", "809-555-0101");
        mockClient.setId(clientId);
        mockAddress = new AddressEntity();
        mockAddress.setId(addressId);
        mockAddress.setClient(mockClient);
        mockAddress.setStreet("Calle Duarte 45");
        mockAddress.setCity("Santo Domingo");
        mockAddress.setCountry("República Dominicana");
    }

    @Nested
    @DisplayName("Add address")
    class AddAddress {

        @Test
        @DisplayName("should add address to existing client")
        void shouldAddAddressSuccessfully() {
            var cmd = new AddressCommand.CreateAddressCommand(
                "Calle Duarte 45", "Santo Domingo", "Distrito Nacional",
                "República Dominicana", "10101", true
            );
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(mockClient));
            when(addressRepo.save(any())).thenReturn(mockAddress);

            var result = handler.handle(cmd, clientId);

            assertThat(result).isInstanceOf(AddressCommandResult.Success.class);
            verify(addressRepo).save(any(AddressEntity.class));
        }

        @Test
        @DisplayName("should throw ClientNotFoundException when client not found")
        void shouldThrowWhenClientNotFound() {
            var cmd = new AddressCommand.CreateAddressCommand(
                "Calle Test", "Santo Domingo", null, "RD", null, false
            );
            when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> handler.handle(cmd, clientId))
                .isInstanceOf(ClientNotFoundException.class);

            verify(addressRepo, never()).save(any());
        }

        @Test
        @DisplayName("should clear primary when new address is primary")
        void shouldClearPrimaryWhenNewIsPrimary() {
            var cmd = new AddressCommand.CreateAddressCommand(
                "Av. 27 de Febrero", "Santo Domingo", null, "RD", null, true
            );
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(mockClient));
            when(addressRepo.save(any())).thenReturn(mockAddress);

            handler.handle(cmd, clientId);

            verify(addressRepo).clearPrimaryForClient(clientId);
        }
    }

    @Nested
    @DisplayName("Update address")
    class UpdateAddress {

        @Test
        @DisplayName("should update address successfully")
        void shouldUpdateAddressSuccessfully() {
            var cmd = new AddressCommand.UpdateAddressCommand(
                addressId, clientId, "Nueva Calle", "Santiago", null, "RD", null, false
            );
            when(addressRepo.findByIdAndClientId(addressId, clientId))
                .thenReturn(Optional.of(mockAddress));
            when(addressRepo.save(any())).thenReturn(mockAddress);

            var result = handler.handle(cmd);

            assertThat(result).isInstanceOf(AddressCommandResult.Success.class);
            assertThat(mockAddress.getStreet()).isEqualTo("Nueva Calle");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when address not found")
        void shouldThrowWhenAddressNotFound() {
            var cmd = new AddressCommand.UpdateAddressCommand(
                addressId, clientId, "Calle", "Ciudad", null, "RD", null, false
            );
            when(addressRepo.findByIdAndClientId(addressId, clientId))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> handler.handle(cmd))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete address")
    class DeleteAddress {

        @Test
        @DisplayName("should soft delete address successfully")
        void shouldSoftDeleteAddress() {
            when(addressRepo.findByIdAndClientId(addressId, clientId))
                .thenReturn(Optional.of(mockAddress));
            when(addressRepo.save(any())).thenReturn(mockAddress);

            var result = handler.handle(
                new AddressCommand.DeleteAddressCommand(addressId, clientId)
            );

            assertThat(result).isInstanceOf(AddressCommandResult.Success.class);
            assertThat(mockAddress.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw when address not found")
        void shouldThrowWhenNotFound() {
            when(addressRepo.findByIdAndClientId(addressId, clientId))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                handler.handle(new AddressCommand.DeleteAddressCommand(addressId, clientId))
            ).isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

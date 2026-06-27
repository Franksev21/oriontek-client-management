package com.oriontek.application.handlers;

import com.oriontek.application.commands.client.ClientCommands;
import com.oriontek.application.commands.client.ClientCommands.ClientCommandResult;
import com.oriontek.application.handlers.command.ClientCommandHandler;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.domain.exception.DuplicateEmailException;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientCommandHandler")
class ClientCommandHandlerTest {

    @Mock private ClientJpaRepository clientRepo;
    @Mock private AddressJpaRepository addressRepo;
    @InjectMocks private ClientCommandHandler handler;

    private ClientEntity mockClient;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        mockClient = new ClientEntity("María", "Rodríguez", "maria@test.com", "809-555-0101");
        mockClient.setId(clientId);
    }

    @Nested
    @DisplayName("Create client")
    class CreateClient {

        @Test
        @DisplayName("should create client successfully")
        void shouldCreateClientSuccessfully() {
            var cmd = new ClientCommands.CreateClientCommand(
                "María", "Rodríguez", "maria@test.com", "809-555-0101", List.of()
            );
            when(clientRepo.existsByEmail("maria@test.com")).thenReturn(false);
            when(clientRepo.save(any())).thenReturn(mockClient);

            var result = handler.handle(cmd);

            assertThat(result).isInstanceOf(ClientCommandResult.Success.class);
            verify(clientRepo).save(any(ClientEntity.class));
        }

        @Test
        @DisplayName("should throw DuplicateEmailException when email already exists")
        void shouldThrowWhenEmailDuplicated() {
            var cmd = new ClientCommands.CreateClientCommand(
                "María", "Rodríguez", "maria@test.com", null, List.of()
            );
            when(clientRepo.existsByEmail("maria@test.com")).thenReturn(true);

            assertThatThrownBy(() -> handler.handle(cmd))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("maria@test.com");

            verify(clientRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update client")
    class UpdateClient {

        @Test
        @DisplayName("should update client successfully")
        void shouldUpdateClientSuccessfully() {
            var cmd = new ClientCommands.UpdateClientCommand(
                clientId, "María Updated", "Rodríguez", "new@test.com", "809-000-0000"
            );
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(mockClient));
            when(clientRepo.existsByEmailAndIdNot("new@test.com", clientId)).thenReturn(false);
            when(clientRepo.save(any())).thenReturn(mockClient);

            var result = handler.handle(cmd);

            assertThat(result).isInstanceOf(ClientCommandResult.Success.class);
            assertThat(mockClient.getFirstName()).isEqualTo("María Updated");
        }

        @Test
        @DisplayName("should throw ClientNotFoundException when client does not exist")
        void shouldThrowWhenClientNotFound() {
            var cmd = new ClientCommands.UpdateClientCommand(
                clientId, "Test", "User", "test@test.com", null
            );
            when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> handler.handle(cmd))
                .isInstanceOf(ClientNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete client (soft delete)")
    class DeleteClient {

        @Test
        @DisplayName("should soft delete client and addresses")
        void shouldSoftDeleteClient() {
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(mockClient));
            when(clientRepo.save(any())).thenReturn(mockClient);

            var result = handler.handle(new ClientCommands.DeleteClientCommand(clientId));

            assertThat(result).isInstanceOf(ClientCommandResult.Success.class);
            assertThat(mockClient.getDeletedAt()).isNotNull();
            verify(clientRepo).save(mockClient);
        }

        @Test
        @DisplayName("should throw ClientNotFoundException when client not found")
        void shouldThrowWhenNotFound() {
            when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                handler.handle(new ClientCommands.DeleteClientCommand(clientId))
            ).isInstanceOf(ClientNotFoundException.class);
        }
    }
}

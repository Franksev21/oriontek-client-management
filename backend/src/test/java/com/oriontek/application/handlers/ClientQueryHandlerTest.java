package com.oriontek.application.handlers;

import com.oriontek.application.handlers.query.ClientQueryHandler;
import com.oriontek.application.queries.client.ClientQueries;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.infrastructure.persistence.entity.ClientEntity;
import com.oriontek.infrastructure.persistence.repository.AddressJpaRepository;
import com.oriontek.infrastructure.persistence.repository.ClientJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientQueryHandler")
class ClientQueryHandlerTest {

    @Mock private ClientJpaRepository clientRepo;
    @Mock private AddressJpaRepository addressRepo;
    @InjectMocks private ClientQueryHandler handler;

    private ClientEntity mockClient;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        mockClient = new ClientEntity("Juan", "Pérez", "juan@test.com", "809-555-0102");
        mockClient.setId(clientId);
    }

    @Test
    @DisplayName("should return client when found by id")
    void shouldReturnClientById() {
        when(clientRepo.findById(clientId)).thenReturn(Optional.of(mockClient));

        var result = handler.handle(new ClientQueries.GetClientByIdQuery(clientId));

        assertThat(result.email()).isEqualTo("juan@test.com");
        assertThat(result.fullName()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("should throw ClientNotFoundException when not found")
    void shouldThrowWhenNotFound() {
        when(clientRepo.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            handler.handle(new ClientQueries.GetClientByIdQuery(clientId))
        ).isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    @DisplayName("should return paged clients when no search term")
    void shouldReturnPagedClients() {
        var page = new PageImpl<>(List.of(mockClient));
        when(clientRepo.findAll(any(Pageable.class))).thenReturn(page);

        var query = new ClientQueries.GetAllClientsQuery(null, 0, 10, "firstName", "asc");
        var result = handler.handle(query);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("should search clients when search term provided")
    void shouldSearchClients() {
        var page = new PageImpl<>(List.of(mockClient));
        when(clientRepo.searchClients(eq("juan"), any(Pageable.class))).thenReturn(page);

        var query = new ClientQueries.GetAllClientsQuery("juan", 0, 10, "firstName", "asc");
        var result = handler.handle(query);

        assertThat(result.content()).hasSize(1);
        verify(clientRepo).searchClients(eq("juan"), any(Pageable.class));
    }
}

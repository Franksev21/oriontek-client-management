package com.oriontek.presentation.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oriontek.application.commands.client.ClientCommands;
import com.oriontek.application.commands.client.ClientCommands.ClientCommandResult;
import com.oriontek.application.dto.response.ResponseDtos.*;
import com.oriontek.application.handlers.command.AddressCommandHandler;
import com.oriontek.application.handlers.command.ClientCommandHandler;
import com.oriontek.application.handlers.query.ClientQueryHandler;
import com.oriontek.application.queries.client.ClientQueries;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.domain.exception.DuplicateEmailException;
import com.oriontek.infrastructure.config.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ClientController")
class ClientControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ClientCommandHandler commandHandler;
    @MockBean private AddressCommandHandler addressCommandHandler;
    @MockBean private ClientQueryHandler queryHandler;

    private final UUID clientId = UUID.fromString("a1b2c3d4-0000-0000-0000-000000000001");

    @SuppressWarnings("unchecked")
    private PagedResponse<ClientSummaryResponse> pagedOf(List<ClientSummaryResponse> items) {
        return new PagedResponse<>(items, 0, 10, (long) items.size(), 1, true, true);
    }

    @Nested
    @DisplayName("GET /api/v1/clients")
    class GetAllClients {

        @Test
        @DisplayName("should return 200 with paged clients")
        void shouldReturn200() throws Exception {
            var summary = new ClientSummaryResponse(
                clientId, "María Rodríguez", "maria@test.com", "809-555-0101", 2, LocalDateTime.now()
            );
            when(queryHandler.handle(any(ClientQueries.GetAllClientsQuery.class)))
                .thenReturn(pagedOf(List.of(summary)));

            mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].fullName").value("María Rodríguez"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("should support search and filter params")
        void shouldSupportParams() throws Exception {
            when(queryHandler.handle(any(ClientQueries.GetAllClientsQuery.class)))
                .thenReturn(pagedOf(List.of()));

            mockMvc.perform(get("/api/v1/clients")
                    .param("search", "María")
                    .param("city", "Santo Domingo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/clients/{id}")
    class GetClientById {

        @Test
        @DisplayName("should return 200 with client detail")
        void shouldReturn200() throws Exception {
            var client = new ClientResponse(
                clientId, "María", "Rodríguez", "María Rodríguez",
                "maria@test.com", "809-555-0101", List.of(), 0,
                LocalDateTime.now(), LocalDateTime.now()
            );
            when(queryHandler.handle(any(ClientQueries.GetClientByIdQuery.class)))
                .thenReturn(client);

            mockMvc.perform(get("/api/v1/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("María Rodríguez"))
                .andExpect(jsonPath("$.data.email").value("maria@test.com"));
        }

        @Test
        @DisplayName("should return 404 when client not found")
        void shouldReturn404() throws Exception {
            when(queryHandler.handle(any(ClientQueries.GetClientByIdQuery.class)))
                .thenThrow(new ClientNotFoundException(clientId));

            mockMvc.perform(get("/api/v1/clients/{id}", clientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/clients")
    class CreateClient {

        @Test
        @DisplayName("should return 201 when client created")
        void shouldReturn201() throws Exception {
            var cmd = new ClientCommands.CreateClientCommand(
                "María", "Rodríguez", "maria@test.com", "809-555-0101", List.of()
            );
            when(commandHandler.handle(any(ClientCommands.CreateClientCommand.class)))
                .thenReturn(new ClientCommandResult.Success(clientId, "Created"));

            mockMvc.perform(post("/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("should return 400 when validation fails")
        void shouldReturn400() throws Exception {
            var invalid = new ClientCommands.CreateClientCommand(
                "", "", "not-an-email", null, List.of()
            );

            mockMvc.perform(post("/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("should return 409 when email duplicated")
        void shouldReturn409() throws Exception {
            var cmd = new ClientCommands.CreateClientCommand(
                "María", "Rodríguez", "maria@test.com", null, List.of()
            );
            when(commandHandler.handle(any(ClientCommands.CreateClientCommand.class)))
                .thenThrow(new DuplicateEmailException("maria@test.com"));

            mockMvc.perform(post("/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cmd)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/clients/{id}")
    class DeleteClient {

        @Test
        @DisplayName("should return 200 when deleted")
        void shouldReturn200() throws Exception {
            when(commandHandler.handle(any(ClientCommands.DeleteClientCommand.class)))
                .thenReturn(new ClientCommandResult.Success(clientId, "Deleted"));

            mockMvc.perform(delete("/api/v1/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(commandHandler.handle(any(ClientCommands.DeleteClientCommand.class)))
                .thenThrow(new ClientNotFoundException(clientId));

            mockMvc.perform(delete("/api/v1/clients/{id}", clientId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/clients/stats")
    class GetStats {

        @Test
        @DisplayName("should return 200 with stats")
        void shouldReturn200() throws Exception {
            var stats = new ClientStatsResponse(5L, 8L, 1.6, 3L);
            when(queryHandler.handle(any(ClientQueries.GetClientStatsQuery.class)))
                .thenReturn(stats);

            mockMvc.perform(get("/api/v1/clients/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalClients").value(5))
                .andExpect(jsonPath("$.data.totalAddresses").value(8));
        }
    }
}

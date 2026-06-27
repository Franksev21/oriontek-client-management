package com.oriontek.presentation.rest;

import com.oriontek.application.commands.address.AddressCommand;
import com.oriontek.application.commands.client.ClientCommands;
import com.oriontek.application.dto.response.ResponseDtos.*;
import com.oriontek.application.handlers.command.AddressCommandHandler;
import com.oriontek.application.handlers.command.ClientCommandHandler;
import com.oriontek.application.handlers.query.ClientQueryHandler;
import com.oriontek.application.queries.client.ClientQueries;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {

    private final ClientCommandHandler commandHandler;
    private final AddressCommandHandler addressCommandHandler;
    private final ClientQueryHandler queryHandler;

    public ClientController(
        ClientCommandHandler commandHandler,
        AddressCommandHandler addressCommandHandler,
        ClientQueryHandler queryHandler
    ) {
        this.commandHandler = commandHandler;
        this.addressCommandHandler = addressCommandHandler;
        this.queryHandler = queryHandler;
    }

    @GetMapping
    @Operation(summary = "List all clients")
    public ResponseEntity<ApiResponse<PagedResponse<ClientSummaryResponse>>> getAllClients(
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "firstName") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var query = new ClientQueries.GetAllClientsQuery(search, page, size, sortBy, sortDir);
        var result = queryHandler.handle(query);
        return ResponseEntity.ok(ApiResponse.ok("Clients retrieved successfully", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable UUID id) {
        var result = queryHandler.handle(new ClientQueries.GetClientByIdQuery(id));
        return ResponseEntity.ok(ApiResponse.ok("Client retrieved successfully", result));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get client statistics")
    public ResponseEntity<ApiResponse<ClientStatsResponse>> getStats() {
        var result = queryHandler.handle(new ClientQueries.GetClientStatsQuery());
        return ResponseEntity.ok(ApiResponse.ok("Stats retrieved successfully", result));
    }

    @PostMapping
    @Operation(summary = "Create a new client")
    public ResponseEntity<ApiResponse<Object>> createClient(
        @Valid @RequestBody ClientCommands.CreateClientCommand command
    ) {
        var result = commandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Client created successfully", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing client")
    public ResponseEntity<ApiResponse<Object>> updateClient(
        @PathVariable UUID id,
        @Valid @RequestBody ClientCommands.UpdateClientCommand command
    ) {
        var cmd = new ClientCommands.UpdateClientCommand(
            id, command.firstName(), command.lastName(), command.email(), command.phone()
        );
        var result = commandHandler.handle(cmd);
        return ResponseEntity.ok(ApiResponse.ok("Client updated successfully", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a client")
    public ResponseEntity<ApiResponse<Object>> deleteClient(@PathVariable UUID id) {
        var result = commandHandler.handle(new ClientCommands.DeleteClientCommand(id));
        return ResponseEntity.ok(ApiResponse.ok("Client deleted successfully", result));
    }

    @PostMapping("/{clientId}/addresses")
    @Operation(summary = "Add address to client")
    public ResponseEntity<ApiResponse<Object>> addAddress(
        @PathVariable UUID clientId,
        @Valid @RequestBody AddressCommand.CreateAddressCommand command
    ) {
        var result = addressCommandHandler.handle(command, clientId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Address added successfully", result));
    }

    @PutMapping("/{clientId}/addresses/{addressId}")
    @Operation(summary = "Update a client address")
    public ResponseEntity<ApiResponse<Object>> updateAddress(
        @PathVariable UUID clientId,
        @PathVariable UUID addressId,
        @Valid @RequestBody AddressCommand.UpdateAddressCommand command
    ) {
        var cmd = new AddressCommand.UpdateAddressCommand(
            addressId, clientId,
            command.street(), command.city(), command.state(),
            command.country(), command.zipCode(), command.primary()
        );
        var result = addressCommandHandler.handle(cmd);
        return ResponseEntity.ok(ApiResponse.ok("Address updated successfully", result));
    }

    @DeleteMapping("/{clientId}/addresses/{addressId}")
    @Operation(summary = "Delete a client address")
    public ResponseEntity<ApiResponse<Object>> deleteAddress(
        @PathVariable UUID clientId,
        @PathVariable UUID addressId
    ) {
        var result = addressCommandHandler.handle(
            new AddressCommand.DeleteAddressCommand(addressId, clientId)
        );
        return ResponseEntity.ok(ApiResponse.ok("Address deleted successfully", result));
    }
}

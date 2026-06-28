package com.oriontek.presentation.rest;

import com.oriontek.application.commands.address.AddressCommand;
import com.oriontek.application.commands.client.ClientCommands;
import com.oriontek.application.dto.response.ResponseDtos.*;
import com.oriontek.application.handlers.command.AddressCommandHandler;
import com.oriontek.application.handlers.command.ClientCommandHandler;
import com.oriontek.application.handlers.query.ClientQueryHandler;
import com.oriontek.application.queries.client.ClientQueries;
import io.swagger.v3.oas.annotations.Operation;
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

    public ClientController(ClientCommandHandler commandHandler, AddressCommandHandler addressCommandHandler, ClientQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.addressCommandHandler = addressCommandHandler;
        this.queryHandler = queryHandler;
    }

    @GetMapping
    @Operation(summary = "List clients with advanced filters")
    public ResponseEntity<ApiResponse<PagedResponse<ClientSummaryResponse>>> getAllClients(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) Integer minAddresses,
        @RequestParam(required = false) Integer maxAddresses,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "firstName") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var query = new ClientQueries.GetAllClientsQuery(search, city, country, minAddresses, maxAddresses, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.ok("Clients retrieved", queryHandler.handle(query)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Client retrieved", queryHandler.handle(new ClientQueries.GetClientByIdQuery(id))));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ClientStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Stats retrieved", queryHandler.handle(new ClientQueries.GetClientStatsQuery())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createClient(@Valid @RequestBody ClientCommands.CreateClientCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Client created", commandHandler.handle(command)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientCommands.UpdateClientCommand command) {
        var cmd = new ClientCommands.UpdateClientCommand(id, command.firstName(), command.lastName(), command.email(), command.phone());
        return ResponseEntity.ok(ApiResponse.ok("Client updated", commandHandler.handle(cmd)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteClient(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Client deleted", commandHandler.handle(new ClientCommands.DeleteClientCommand(id))));
    }

    @PostMapping("/{clientId}/addresses")
    public ResponseEntity<ApiResponse<Object>> addAddress(@PathVariable UUID clientId, @Valid @RequestBody AddressCommand.CreateAddressCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Address added", addressCommandHandler.handle(command, clientId)));
    }

    @PutMapping("/{clientId}/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Object>> updateAddress(@PathVariable UUID clientId, @PathVariable UUID addressId, @Valid @RequestBody AddressCommand.UpdateAddressCommand command) {
        var cmd = new AddressCommand.UpdateAddressCommand(addressId, clientId, command.street(), command.city(), command.state(), command.country(), command.zipCode(), command.primary());
        return ResponseEntity.ok(ApiResponse.ok("Address updated", addressCommandHandler.handle(cmd)));
    }

    @DeleteMapping("/{clientId}/addresses/{addressId}")
    public ResponseEntity<ApiResponse<Object>> deleteAddress(@PathVariable UUID clientId, @PathVariable UUID addressId) {
        return ResponseEntity.ok(ApiResponse.ok("Address deleted", addressCommandHandler.handle(new AddressCommand.DeleteAddressCommand(addressId, clientId))));
    }
}

package com.oriontek.application.commands.client;

import com.oriontek.application.commands.address.AddressCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public final class ClientCommands {

    // Create client command
    public record CreateClientCommand(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Phone number is invalid")
        String phone,

        @Valid
        List<AddressCommand.CreateAddressCommand> addresses
    ) {}

    // Update client command
    public record UpdateClientCommand(
        @NotNull(message = "Client ID is required")
        java.util.UUID id,

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "Email is required")
        @Email
        String email,

        @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "Phone number is invalid")
        String phone
    ) {}

    // Delete client command (soft delete)
    public record DeleteClientCommand(
        @NotNull java.util.UUID id
    ) {}

    // Sealed interface for command results — Java 21 feature
    public sealed interface ClientCommandResult
        permits ClientCommandResult.Success, ClientCommandResult.Failure {

        record Success(java.util.UUID id, String message) implements ClientCommandResult {}
        record Failure(String reason, String field) implements ClientCommandResult {}
    }

    private ClientCommands() {}
}

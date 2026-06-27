package com.oriontek.application.commands.address;

import jakarta.validation.constraints.*;

import java.util.UUID;

public final class AddressCommand {

    public record CreateAddressCommand(
        @NotBlank(message = "Street is required")
        @Size(max = 255)
        String street,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        String country,

        @Size(max = 20)
        String zipCode,

        boolean primary
    ) {}

    public record UpdateAddressCommand(
        @NotNull UUID id,
        @NotNull UUID clientId,

        @NotBlank @Size(max = 255)
        String street,

        @NotBlank @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @NotBlank @Size(max = 100)
        String country,

        @Size(max = 20)
        String zipCode,

        boolean primary
    ) {}

    public record DeleteAddressCommand(
        @NotNull UUID id,
        @NotNull UUID clientId
    ) {}

    public sealed interface AddressCommandResult
        permits AddressCommandResult.Success, AddressCommandResult.Failure {

        record Success(UUID id, String message) implements AddressCommandResult {}
        record Failure(String reason, String field) implements AddressCommandResult {}
    }

    private AddressCommand() {}
}

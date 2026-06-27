package com.oriontek.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class ResponseDtos {

    public record AddressResponse(
        UUID id,
        String street,
        String city,
        String state,
        String country,
        String zipCode,
        boolean primary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record ClientResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        List<AddressResponse> addresses,
        int addressCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record ClientSummaryResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        int addressCount,
        LocalDateTime createdAt
    ) {}

    public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
    ) {}

    public record ClientStatsResponse(
        long totalClients,
        long totalAddresses,
        double avgAddressesPerClient,
        long newThisMonth
    ) {}

    public record ApiResponse<T>(
        boolean success,
        String message,
        T data
    ) {
        public static <T> ApiResponse<T> ok(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }

    private ResponseDtos() {}
}

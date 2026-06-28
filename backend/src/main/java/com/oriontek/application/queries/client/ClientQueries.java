package com.oriontek.application.queries.client;

import java.util.UUID;

public final class ClientQueries {

    public record GetClientByIdQuery(UUID id) {}

    public record GetAllClientsQuery(
        String search,
        String city,
        String country,
        Integer minAddresses,
        Integer maxAddresses,
        int page,
        int size,
        String sortBy,
        String sortDir
    ) {
        public GetAllClientsQuery {
            if (page < 0) page = 0;
            if (size < 1 || size > 100) size = 10;
            if (sortBy == null || sortBy.isBlank()) sortBy = "firstName";
            if (sortDir == null || sortDir.isBlank()) sortDir = "asc";
        }
    }

    public record GetClientStatsQuery() {}

    private ClientQueries() {}
}

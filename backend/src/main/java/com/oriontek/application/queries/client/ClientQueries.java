package com.oriontek.application.queries.client;

import java.util.UUID;

public final class ClientQueries {

    public record GetClientByIdQuery(UUID id) {}

    public record GetAllClientsQuery(
        String search,   // optional search term
        int page,
        int size,
        String sortBy,   // field to sort by
        String sortDir   // asc or desc
    ) {
        // Compact constructor with defaults
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

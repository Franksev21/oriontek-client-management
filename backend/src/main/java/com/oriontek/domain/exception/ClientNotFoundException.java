package com.oriontek.domain.exception;

import java.util.UUID;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(UUID id) {
        super("Client not found with id: " + id);
    }
}

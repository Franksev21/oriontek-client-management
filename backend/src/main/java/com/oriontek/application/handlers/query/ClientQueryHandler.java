package com.oriontek.application.handlers.query;

import com.oriontek.application.dto.response.ResponseDtos.*;
import com.oriontek.application.queries.client.ClientQueries;
import com.oriontek.domain.exception.ClientNotFoundException;
import com.oriontek.infrastructure.persistence.entity.AddressEntity;
import com.oriontek.infrastructure.persistence.entity.ClientEntity;
import com.oriontek.infrastructure.persistence.repository.AddressJpaRepository;
import com.oriontek.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientQueryHandler {

    private final ClientJpaRepository clientRepo;
    private final AddressJpaRepository addressRepo;

    public ClientQueryHandler(ClientJpaRepository clientRepo, AddressJpaRepository addressRepo) {
        this.clientRepo = clientRepo;
        this.addressRepo = addressRepo;
    }

    public ClientResponse handle(ClientQueries.GetClientByIdQuery query) {
        var client = clientRepo.findById(query.id())
            .orElseThrow(() -> new ClientNotFoundException(query.id()));
        return toDetailResponse(client);
    }

    public PagedResponse<ClientSummaryResponse> handle(ClientQueries.GetAllClientsQuery query) {
        var sort = Sort.by(
            query.sortDir().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            query.sortBy()
        );
        var pageable = PageRequest.of(query.page(), query.size(), sort);

        String search = (query.search() != null && !query.search().isBlank()) ? query.search().trim() : null;
        String city = (query.city() != null && !query.city().isBlank()) ? query.city().trim() : null;
        String country = (query.country() != null && !query.country().isBlank()) ? query.country().trim() : null;

        var page = (search != null || city != null || country != null)
            ? clientRepo.findWithFilters(search, city, country, pageable)
            : clientRepo.findAllActive(pageable);

        var content = page.getContent().stream()
            .map(this::toSummaryResponse)
            .toList();

        return new PagedResponse<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    public ClientStatsResponse handle(ClientQueries.GetClientStatsQuery query) {
        long totalClients = clientRepo.countActive();
        long totalAddresses = addressRepo.count();
        double avg = totalClients > 0 ? (double) totalAddresses / totalClients : 0.0;
        var firstOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        var allClients = clientRepo.findAll();
        long newThisMonth = allClients.stream()
            .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(firstOfMonth) && c.getDeletedAt() == null)
            .count();
        return new ClientStatsResponse(totalClients, totalAddresses, avg, newThisMonth);
    }

    private ClientResponse toDetailResponse(ClientEntity c) {
        var addresses = c.getAddresses().stream().map(this::toAddressResponse).toList();
        return new ClientResponse(
            c.getId(), c.getFirstName(), c.getLastName(),
            c.getFirstName() + " " + c.getLastName(),
            c.getEmail(), c.getPhone(), addresses, addresses.size(),
            c.getCreatedAt(), c.getUpdatedAt()
        );
    }

    private ClientSummaryResponse toSummaryResponse(ClientEntity c) {
        return new ClientSummaryResponse(
            c.getId(), c.getFirstName() + " " + c.getLastName(),
            c.getEmail(), c.getPhone(), c.getAddresses().size(), c.getCreatedAt()
        );
    }

    private AddressResponse toAddressResponse(AddressEntity a) {
        return new AddressResponse(
            a.getId(), a.getStreet(), a.getCity(), a.getState(),
            a.getCountry(), a.getZipCode(), a.isPrimary(),
            a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}

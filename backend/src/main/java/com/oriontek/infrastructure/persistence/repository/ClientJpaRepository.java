package com.oriontek.infrastructure.persistence.repository;

import com.oriontek.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {

    // Search by name or email (case-insensitive) with pagination
    @Query("""
        SELECT c FROM ClientEntity c
        WHERE (
            LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.email)     LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<ClientEntity> searchClients(@Param("search") String search, Pageable pageable);

    // Find all active (non-deleted) with pagination
    Page<ClientEntity> findAll(Pageable pageable);

    // Check email uniqueness (excluding current client on update)
    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByEmail(String email);

    // Find including soft-deleted (for admin use)
    @Query("SELECT c FROM ClientEntity c WHERE c.id = :id")
    Optional<ClientEntity> findByIdIncludingDeleted(@Param("id") UUID id);

    // Stats query
    @Query("SELECT COUNT(c) FROM ClientEntity c")
    long countActive();
}

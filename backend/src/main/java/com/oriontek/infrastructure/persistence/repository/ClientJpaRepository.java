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

    @Query("SELECT c FROM ClientEntity c WHERE c.deletedAt IS NULL AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ClientEntity> searchClients(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM ClientEntity c WHERE c.deletedAt IS NULL")
    Page<ClientEntity> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(c) > 0 FROM ClientEntity c WHERE c.email = :email AND c.deletedAt IS NULL AND c.id <> :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") UUID id);

    @Query("SELECT COUNT(c) > 0 FROM ClientEntity c WHERE c.email = :email AND c.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT c FROM ClientEntity c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<ClientEntity> findById(@Param("id") UUID id);

    @Query("SELECT COUNT(c) FROM ClientEntity c WHERE c.deletedAt IS NULL")
    long countActive();
}

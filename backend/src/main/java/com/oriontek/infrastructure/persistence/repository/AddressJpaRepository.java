package com.oriontek.infrastructure.persistence.repository;

import com.oriontek.infrastructure.persistence.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

    List<AddressEntity> findByClientId(UUID clientId);

    Optional<AddressEntity> findByIdAndClientId(UUID id, UUID clientId);

    @Modifying
    @Query("UPDATE AddressEntity a SET a.primary = false WHERE a.client.id = :clientId")
    void clearPrimaryForClient(@Param("clientId") UUID clientId);

    long countByClientId(UUID clientId);
}

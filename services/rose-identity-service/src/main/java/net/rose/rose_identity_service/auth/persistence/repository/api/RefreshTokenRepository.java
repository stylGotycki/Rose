package net.rose.rose_identity_service.auth.persistence.repository.api;

import net.rose.rose_identity_service.auth.persistence.entity.RefreshTokenEntity;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);


    List<RefreshTokenEntity> findAllByUserId(UserId userId);

}
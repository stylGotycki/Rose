package net.rose.rose_identity_service.auth.persistence.repository.api;

import net.rose.rose_identity_service.auth.persistence.entity.RevokedAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessTokenEntity, UUID> {

    boolean existsByTokenId(String tokenId);


    void deleteAllByExpiresAtBefore(Instant now);

}
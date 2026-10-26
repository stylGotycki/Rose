package net.rose.rose_identity_service.auth.persistence.repository.api;

import net.rose.rose_identity_service.auth.persistence.entity.EmailVerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends
        JpaRepository<EmailVerificationTokenEntity, UUID> {

    Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);

}
package net.rose.rose_identity_service.auth.persistence.repository.api;

import net.rose.rose_identity_service.auth.persistence.entity.AuthEntity;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<AuthEntity, UUID> {

    boolean existsByEmail(String email);


    Optional<AuthEntity> findByEmail(String email);


    Optional<AuthEntity> findByUserId(UserId userId);

}

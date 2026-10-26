package net.rose.rose_identity_service.user.persistence.repository.api;

import net.rose.rose_identity_service.user.domain.model.UserStatus;
import net.rose.rose_identity_service.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmailOrPhoneNumber(
            String email,
            String phoneNumber
    );

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    List<UserEntity>
    findTop10ByStatusAndEmailContainingIgnoreCaseOrderByEmailAsc(
            UserStatus status,
            String email
    );
}
package net.rose.rose_identity_service.auth.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_identity_service.core.persistence.id.UserId;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "auth_credentials",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_credentials_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_auth_credentials_user_id", columnNames = "user_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private UserId userId;


    @Column(name = "email", nullable = false, length = 320)
    private String email;


    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @Column(name = "last_login_at")
    private Instant lastLoginAt;


    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

}
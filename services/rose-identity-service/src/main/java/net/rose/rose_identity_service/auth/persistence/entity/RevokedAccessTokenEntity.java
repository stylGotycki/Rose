package net.rose.rose_identity_service.auth.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "revoked_access_tokens",
        indexes = {
                @Index(name = "idx_revoked_access_tokens_token_id", columnList = "token_id"),
                @Index(name = "idx_revoked_access_tokens_expires_at", columnList = "expires_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokedAccessTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "token_id", nullable = false, unique = true, length = 64)
    private String tokenId;


    @Column(name = "revoked_at", nullable = false, updatable = false)
    private Instant revokedAt;


    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;


    @PrePersist
    void prePersist() {
        if (revokedAt == null) {
            revokedAt = Instant.now();
        }
    }

}
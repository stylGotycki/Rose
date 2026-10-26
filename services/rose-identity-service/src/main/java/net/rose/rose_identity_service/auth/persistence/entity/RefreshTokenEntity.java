package net.rose.rose_identity_service.auth.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_identity_service.core.persistence.id.UserId;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private UserId userId;


    @Column(name = "token_hash", nullable = false, length = 128)
    private String tokenHash;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;


    @Column(name = "revoked_at")
    private Instant revokedAt;


    @Column(name = "last_used_at")
    private Instant lastUsedAt;


    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }


    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }


    public boolean isRevoked() {
        return revokedAt != null;
    }


    public boolean isActive() {
        return !isExpired() && !isRevoked();
    }

}
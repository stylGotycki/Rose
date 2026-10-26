package net.rose.rose_identity_service.auth.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_identity_service.core.persistence.id.UserId;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "password_reset_tokens",
        indexes = {
                @Index(name = "idx_password_reset_tokens_token_hash", columnList = "token_hash"),
                @Index(name = "idx_password_reset_tokens_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
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

    @Column(name = "used_at")
    private Instant usedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
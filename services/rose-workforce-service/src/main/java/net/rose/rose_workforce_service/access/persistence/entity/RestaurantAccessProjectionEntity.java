package net.rose.rose_workforce_service.access.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurant_access_projection")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantAccessProjectionEntity {

    @Id
    @Column(name = "member_id", nullable = false, updatable = false)
    private UUID memberId;

    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private UUID restaurantId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "role", nullable = false, length = 32)
    private String role;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "source_changed_at", nullable = false)
    private Instant sourceChangedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void update(
            String role,
            String status,
            Instant sourceChangedAt,
            Instant updatedAt
    ) {
        this.role = role;
        this.status = status;
        this.sourceChangedAt = sourceChangedAt;
        this.updatedAt = updatedAt;
    }
}
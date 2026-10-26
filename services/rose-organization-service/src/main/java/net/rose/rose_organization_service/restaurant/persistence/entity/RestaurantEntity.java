package net.rose.rose_organization_service.restaurant.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "restaurants",
        indexes = {
                @Index(name = "idx_restaurants_status", columnList = "status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;


    @Column(name = "name", nullable = false, length = 128)
    private String name;


    @Column(name = "description", length = 1024)
    private String description;


    @Column(name = "tax_id", length = 32)
    private String taxId;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RestaurantStatus status;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    @Column(name = "archived_at")
    private Instant archivedAt;


    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = RestaurantStatus.ACTIVE;
        }
    }


    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }


    public boolean isArchived() {
        return status == RestaurantStatus.ARCHIVED;
    }

}
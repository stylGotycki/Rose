package net.rose.rose_organization_service.location.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.model.LocationStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "locations",
        indexes = {
                @Index(name = "idx_locations_restaurant_id", columnList = "restaurant_id"),
                @Index(name = "idx_locations_status", columnList = "status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "restaurant_id", nullable = false))
    private RestaurantId restaurantId;


    @Column(name = "name", nullable = false, length = 128)
    private String name;


    @Column(name = "city", nullable = false, length = 128)
    private String city;


    @Column(name = "street", nullable = false, length = 128)
    private String street;


    @Column(name = "building_number", nullable = false, length = 32)
    private String buildingNumber;


    @Column(name = "postal_code", nullable = false, length = 16)
    private String postalCode;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private LocationStatus status;


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
            status = LocationStatus.ACTIVE;
        }
    }


    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }


    public boolean isArchived() {
        return status == LocationStatus.ARCHIVED;
    }

}
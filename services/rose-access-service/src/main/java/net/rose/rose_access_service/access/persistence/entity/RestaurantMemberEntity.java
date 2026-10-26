package net.rose.rose_access_service.access.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberStatus;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "restaurant_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_restaurant_member_user",
                        columnNames = {"restaurant_id", "user_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "restaurant_id", nullable = false))
    private RestaurantId restaurantId;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id", nullable = false))
    private UserId userId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RestaurantMemberRole role;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RestaurantMemberStatus status;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public void archive() {
        this.status = RestaurantMemberStatus.ARCHIVED;
    }


    public void restore() {
        this.status = RestaurantMemberStatus.ACTIVE;
    }


    public void changeRole(RestaurantMemberRole role) {
        this.role = role;
    }
}
package net.rose.rose_menu_service.menu.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "menus",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_menu_restaurant_name",
                        columnNames = {"restaurant_id", "name"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "restaurant_id", nullable = false))
    private RestaurantId restaurantId;


    @Column(nullable = false, length = 128)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MenuStatus status;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public void archive() {
        this.status = MenuStatus.ARCHIVED;
    }


    public void activate() {
        this.status = MenuStatus.ACTIVE;
    }
}
package net.rose.rose_menu_service.menu.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "menu_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "menu_id", nullable = false))
    private MenuId menuId;


    @Column(nullable = false, length = 128)
    private String name;


    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MenuCategoryStatus status;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public void archive() {
        this.status = MenuCategoryStatus.ARCHIVED;
    }
}
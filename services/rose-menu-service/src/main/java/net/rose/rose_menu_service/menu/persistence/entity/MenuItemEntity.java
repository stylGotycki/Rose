package net.rose.rose_menu_service.menu.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "category_id", nullable = false))
    private MenuCategoryId categoryId;


    @Column(nullable = false, length = 128)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    @Column(nullable = false, length = 3)
    private String currency;


    @Column(nullable = false)
    private Boolean available;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MenuItemStatus status;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public void archive() {
        this.status = MenuItemStatus.ARCHIVED;
    }
}
package net.rose.rose_order_service.menu.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "menu_item_projection")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuItemProjectionEntity {

    @Id
    @Column(
            name = "menu_item_id",
            nullable = false,
            updatable = false
    )
    private UUID menuItemId;

    @Column(
            name = "restaurant_id",
            nullable = false,
            updatable = false
    )
    private UUID restaurantId;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @Column(
            name = "price",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal price;

    @Column(
            name = "currency",
            nullable = false,
            length = 16
    )
    private String currency;

    @Column(name = "orderable", nullable = false)
    private boolean orderable;

    @Column(name = "source_changed_at", nullable = false)
    private Instant sourceChangedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void update(
            String name,
            BigDecimal price,
            String currency,
            boolean orderable,
            Instant sourceChangedAt,
            Instant updatedAt
    ) {
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.orderable = orderable;
        this.sourceChangedAt = sourceChangedAt;
        this.updatedAt = updatedAt;
    }
}
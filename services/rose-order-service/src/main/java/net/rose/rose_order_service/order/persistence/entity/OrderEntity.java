package net.rose.rose_order_service.order.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.core.persistence.id.UserId;
import net.rose.rose_order_service.order.domain.model.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "restaurant_id",
                    nullable = false
            )
    )
    private RestaurantId restaurantId;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "created_by_user_id",
                    nullable = false
            )
    )
    private UserId createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(length = 512)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public static OrderEntity create(
            RestaurantId restaurantId,
            UserId createdByUserId,
            String note
    ) {
        return OrderEntity.builder()
                .restaurantId(restaurantId)
                .createdByUserId(createdByUserId)
                .status(OrderStatus.DRAFT)
                .note(normalizeNote(note))
                .build();
    }

    public boolean isDraft() {
        return status == OrderStatus.DRAFT;
    }

    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft orders can be submitted"
            );
        }

        this.status = OrderStatus.SUBMITTED;
    }

    public void markInPreparation() {
        if (status == OrderStatus.SUBMITTED) {
            this.status = OrderStatus.IN_PREPARATION;
        }
    }

    public void markReady() {
        if (status != OrderStatus.IN_PREPARATION
                && status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Order cannot be marked as ready"
            );
        }

        this.status = OrderStatus.READY;
    }

    public void complete() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException(
                    "Only ready orders can be completed"
            );
        }

        this.status = OrderStatus.COMPLETED;
    }

    private static String normalizeNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }

        return note.trim();
    }
}
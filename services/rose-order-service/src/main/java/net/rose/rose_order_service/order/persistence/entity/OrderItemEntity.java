package net.rose.rose_order_service.order.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_order_service.core.persistence.id.MenuItemId;
import net.rose.rose_order_service.core.persistence.id.OrderId;
import net.rose.rose_order_service.order.domain.model.OrderItemStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "order_id",
                    nullable = false
            )
    )
    private OrderId orderId;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "menu_item_id",
                    nullable = false
            )
    )
    private MenuItemId menuItemId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderItemStatus status;

    @Column(length = 512)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public static OrderItemEntity create(
            OrderId orderId,
            MenuItemId menuItemId,
            String name,
            BigDecimal unitPrice,
            String currency,
            int quantity,
            String note
    ) {
        return OrderItemEntity.builder()
                .orderId(orderId)
                .menuItemId(menuItemId)
                .name(name)
                .unitPrice(unitPrice)
                .currency(currency)
                .quantity(quantity)
                .status(OrderItemStatus.PENDING)
                .note(normalizeNote(note))
                .build();
    }


    public void update(int quantity, String note) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        this.quantity = quantity;
        this.note = normalizeNote(note);
    }


    private static String normalizeNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }

        return note.trim();
    }

    public void startPreparation() {
        if (status != OrderItemStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending order items can be started"
            );
        }

        this.status = OrderItemStatus.IN_PREPARATION;
    }

    public void markReady() {
        if (status != OrderItemStatus.IN_PREPARATION) {
            throw new IllegalStateException(
                    "Only items in preparation can be marked as ready"
            );
        }

        this.status = OrderItemStatus.READY;
    }

    public void markServed() {
        if (status != OrderItemStatus.READY) {
            throw new IllegalStateException(
                    "Only ready order items can be marked as served"
            );
        }

        this.status = OrderItemStatus.SERVED;
    }

}
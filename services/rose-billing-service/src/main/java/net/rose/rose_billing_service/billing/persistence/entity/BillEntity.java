package net.rose.rose_billing_service.billing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_billing_service.billing.domain.model.BillStatus;
import net.rose.rose_billing_service.core.persistence.id.OrderId;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bills")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "order_id",
                    nullable = false,
                    unique = true
            )
    )
    private OrderId orderId;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "restaurant_id",
                    nullable = false
            )
    )
    private RestaurantId restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BillStatus status;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant paidAt;

    public static BillEntity create(
            OrderId orderId,
            RestaurantId restaurantId,
            BigDecimal totalAmount,
            String currency
    ) {
        return BillEntity.builder()
                .orderId(orderId)
                .restaurantId(restaurantId)
                .status(BillStatus.OPEN)
                .totalAmount(totalAmount)
                .currency(currency)
                .build();
    }

    public void pay() {
        if (status != BillStatus.OPEN) {
            throw new IllegalStateException(
                    "Only open bills can be paid"
            );
        }

        this.status = BillStatus.PAID;
        this.paidAt = Instant.now();
    }
}
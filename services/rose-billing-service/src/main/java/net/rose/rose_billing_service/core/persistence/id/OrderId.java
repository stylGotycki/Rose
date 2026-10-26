package net.rose.rose_billing_service.core.persistence.id;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class OrderId {

    private UUID id;

    private OrderId(UUID id) {
        this.id = Objects.requireNonNull(
                id,
                "Order id cannot be null"
        );
    }

    public static OrderId of(UUID id) {
        return new OrderId(id);
    }
}
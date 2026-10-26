package net.rose.rose_order_service.core.persistence.id;


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
public class RestaurantId {

    private UUID id;


    private RestaurantId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static RestaurantId of(UUID id) {
        return new RestaurantId(id);
    }

}

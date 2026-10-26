package net.rose.rose_organization_service.core.persistence.id;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RestaurantId implements Serializable {

    private UUID id;


    private RestaurantId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static RestaurantId of(UUID id) {
        return new RestaurantId(id);
    }

}
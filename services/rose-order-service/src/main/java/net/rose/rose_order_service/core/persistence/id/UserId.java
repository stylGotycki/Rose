package net.rose.rose_order_service.core.persistence.id;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserId implements Serializable {

    private UUID id;


    private UserId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static UserId of(UUID id) {
        return new UserId(id);
    }

}
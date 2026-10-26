package net.rose.rose_identity_service.core.persistence.id;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class LocationId implements Serializable {

    private UUID id;


    private LocationId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static LocationId of(UUID id) {
        return new LocationId(id);
    }


}
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
public class EmployeeId {

    private UUID id;


    private EmployeeId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static EmployeeId of(UUID id) {
        return new EmployeeId(id);
    }

}

package net.rose.rose_workforce_service.core.persistence.id;

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
public class MenuId {

    private UUID id;


    private MenuId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static MenuId of(UUID id) {
        return new MenuId(id);
    }

}

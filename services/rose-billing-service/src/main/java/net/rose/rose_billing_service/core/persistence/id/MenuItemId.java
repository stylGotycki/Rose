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
public class MenuItemId {

    private UUID id;


    private MenuItemId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static MenuItemId of(UUID id) {
        return new MenuItemId(id);
    }

}

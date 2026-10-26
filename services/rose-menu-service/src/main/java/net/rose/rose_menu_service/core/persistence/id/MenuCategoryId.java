package net.rose.rose_menu_service.core.persistence.id;

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
public class MenuCategoryId {

    private UUID id;


    private MenuCategoryId(UUID id) {
        this.id = Objects.requireNonNull(id);
    }


    public static MenuCategoryId of(UUID id) {
        return new MenuCategoryId(id);
    }

}

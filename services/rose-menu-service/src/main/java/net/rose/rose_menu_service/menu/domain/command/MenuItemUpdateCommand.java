package net.rose.rose_menu_service.menu.domain.command;

import java.math.BigDecimal;

public record MenuItemUpdateCommand(
        String name,
        String description,
        BigDecimal price,
        String currency,
        Boolean available
) {
}

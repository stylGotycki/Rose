package net.rose.rose_menu_service.menu.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api")
public interface MenuItemRestController {

    @PostMapping("/menu-categories/{categoryId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    MenuItemReadDto create(
            @PathVariable UUID categoryId,
            @RequestBody @Valid MenuItemCreateDto dto
    );


    @GetMapping("/menu-categories/{categoryId}/items")
    CollectionRestDto<MenuItemReadDto> getAllByCategory(
            @PathVariable UUID categoryId
    );


    @GetMapping("/menu-items/{itemId}")
    MenuItemReadDto getById(
            @PathVariable UUID itemId
    );


    @PutMapping("/menu-items/{itemId}")
    MenuItemReadDto update(
            @PathVariable UUID itemId,
            @RequestBody @Valid MenuItemUpdateDto dto
    );


    @DeleteMapping("/menu-items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(
            @PathVariable UUID itemId
    );

    @GetMapping("/restaurants/{restaurantId}/menu-items/{itemId}")
    MenuItemReadDto getAvailableByRestaurant(
            @PathVariable UUID restaurantId,
            @PathVariable UUID itemId
    );
}
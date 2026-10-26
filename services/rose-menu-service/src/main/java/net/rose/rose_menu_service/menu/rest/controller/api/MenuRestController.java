package net.rose.rose_menu_service.menu.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api")
public interface MenuRestController {

    @PostMapping("/restaurants/{restaurantId}/menus")
    @ResponseStatus(HttpStatus.CREATED)
    MenuReadDto create(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid MenuCreateDto dto
    );


    @GetMapping("/restaurants/{restaurantId}/menus")
    CollectionRestDto<MenuReadDto> getAllByRestaurant(
            @PathVariable UUID restaurantId
    );


    @GetMapping("/menus/{menuId}")
    MenuReadDto getById(
            @PathVariable UUID menuId
    );


    @PutMapping("/menus/{menuId}")
    MenuReadDto update(
            @PathVariable UUID menuId,
            @RequestBody @Valid MenuUpdateDto dto
    );


    @PatchMapping("/menus/{menuId}/activate")
    MenuReadDto activate(
            @PathVariable UUID menuId
    );


    @DeleteMapping("/menus/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(
            @PathVariable UUID menuId
    );
}
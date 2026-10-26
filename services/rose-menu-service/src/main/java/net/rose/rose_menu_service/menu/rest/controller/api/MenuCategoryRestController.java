package net.rose.rose_menu_service.menu.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api")
public interface MenuCategoryRestController {

    @PostMapping("/menus/{menuId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    MenuCategoryReadDto create(
            @PathVariable UUID menuId,
            @RequestBody @Valid MenuCategoryCreateDto dto
    );


    @GetMapping("/menus/{menuId}/categories")
    CollectionRestDto<MenuCategoryReadDto> getAllByMenu(
            @PathVariable UUID menuId
    );


    @GetMapping("/menu-categories/{categoryId}")
    MenuCategoryReadDto getById(
            @PathVariable UUID categoryId
    );


    @PutMapping("/menu-categories/{categoryId}")
    MenuCategoryReadDto update(
            @PathVariable UUID categoryId,
            @RequestBody @Valid MenuCategoryUpdateDto dto
    );


    @DeleteMapping("/menu-categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(
            @PathVariable UUID categoryId
    );
}
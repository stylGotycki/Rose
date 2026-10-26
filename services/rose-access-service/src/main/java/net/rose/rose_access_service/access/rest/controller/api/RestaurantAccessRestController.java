package net.rose.rose_access_service.access.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberCreateDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberReadDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberUpdateDto;
import net.rose.rose_access_service.core.rest.dto.CollectionRestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api")
public interface RestaurantAccessRestController {

    @PostMapping("/restaurants/{restaurantId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    RestaurantMemberReadDto addMember(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid RestaurantMemberCreateDto dto
    );

    @GetMapping("/restaurants/{restaurantId}/members")
    CollectionRestDto<RestaurantMemberReadDto> getMembers(
            @PathVariable UUID restaurantId
    );

    @PutMapping("/restaurant-members/{memberId}")
    RestaurantMemberReadDto updateRole(
            @PathVariable UUID memberId,
            @RequestBody @Valid RestaurantMemberUpdateDto dto
    );

    @DeleteMapping("/restaurant-members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(
            @PathVariable UUID memberId
    );

    @GetMapping("/restaurants/{restaurantId}/members/me")
    RestaurantMemberReadDto getCurrentMember(
            @PathVariable UUID restaurantId
    );
}
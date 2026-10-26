package net.rose.rose_access_service.access.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessCommandService;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessQueryService;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.access.rest.controller.api.RestaurantAccessRestController;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberCreateDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberReadDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberUpdateDto;
import net.rose.rose_access_service.access.rest.mapper.RestaurantMemberRestMapper;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.rest.dto.CollectionRestDto;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultRestaurantAccessRestController implements RestaurantAccessRestController {

    private final RestaurantAccessCommandService commandService;


    private final RestaurantAccessQueryService queryService;


    private final RestaurantMemberRestMapper mapper;


    @Override
    public RestaurantMemberReadDto addMember(UUID restaurantId, RestaurantMemberCreateDto dto) {
        RestaurantMemberEntity member = commandService.addMember(
                mapper.toCommand(restaurantId, dto)
        );

        return mapper.toDto(member);
    }


    @Override
    public RestaurantMemberReadDto getCurrentMember(UUID restaurantId) {
        RestaurantMemberEntity member = queryService.findCurrentActiveMember(
                RestaurantId.of(restaurantId)
        );

        return mapper.toDto(member);
    }


    @Override
    public CollectionRestDto<RestaurantMemberReadDto> getMembers(UUID restaurantId) {
        return new CollectionRestDto<>(
                queryService.findActiveByRestaurantId(RestaurantId.of(restaurantId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public RestaurantMemberReadDto updateRole(UUID memberId, RestaurantMemberUpdateDto dto) {
        return mapper.toDto(
                commandService.updateRole(memberId, mapper.toCommand(dto))
        );
    }


    @Override
    public void archive(UUID memberId) {
        commandService.archive(memberId);
    }
}
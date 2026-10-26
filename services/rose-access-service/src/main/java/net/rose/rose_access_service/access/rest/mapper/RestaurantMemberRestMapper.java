package net.rose.rose_access_service.access.rest.mapper;

import net.rose.rose_access_service.access.domain.command.RestaurantMemberCreateCommand;
import net.rose.rose_access_service.access.domain.command.RestaurantMemberUpdateRoleCommand;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberCreateDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberReadDto;
import net.rose.rose_access_service.access.rest.dto.RestaurantMemberUpdateDto;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantMemberRestMapper {

    public RestaurantMemberCreateCommand toCommand(UUID restaurantId, RestaurantMemberCreateDto dto) {
        return new RestaurantMemberCreateCommand(
                RestaurantId.of(restaurantId),
                UserId.of(dto.userId()),
                dto.role()
        );
    }

    public RestaurantMemberUpdateRoleCommand toCommand(RestaurantMemberUpdateDto dto) {
        return new RestaurantMemberUpdateRoleCommand(
                dto.role()
        );
    }

    public RestaurantMemberReadDto toDto(RestaurantMemberEntity entity) {
        return new RestaurantMemberReadDto(
                entity.getId(),
                entity.getRestaurantId().getId(),
                entity.getUserId().getId(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
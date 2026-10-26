package net.rose.rose_organization_service.restaurant.rest.mapper;

import net.rose.rose_organization_service.core.persistence.id.UserId;
import net.rose.rose_organization_service.restaurant.domain.command.CreateRestaurantCommand;
import net.rose.rose_organization_service.restaurant.domain.command.UpdateRestaurantCommand;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantDetails;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantCreateDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantReadDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRestMapper {

    public CreateRestaurantCommand toCommand(RestaurantCreateDto dto, UserId ownerUserId) {
        return new CreateRestaurantCommand(
                ownerUserId,
                dto.name(),
                dto.description(),
                dto.taxId()
        );
    }


    public UpdateRestaurantCommand toCommand(
            RestaurantUpdateDto dto
    ) {
        return new UpdateRestaurantCommand(
                dto.name(),
                dto.description(),
                dto.taxId()
        );
    }


    public RestaurantReadDto toDto(RestaurantDetails details) {
        return new RestaurantReadDto(
                details.id().getId(),
                details.name(),
                details.description(),
                details.taxId(),
                details.status(),
                details.createdAt(),
                details.updatedAt()
        );
    }

}
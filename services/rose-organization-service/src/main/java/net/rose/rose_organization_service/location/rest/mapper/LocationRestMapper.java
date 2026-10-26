package net.rose.rose_organization_service.location.rest.mapper;

import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.command.CreateLocationCommand;
import net.rose.rose_organization_service.location.domain.command.UpdateLocationCommand;
import net.rose.rose_organization_service.location.domain.model.LocationDetails;
import net.rose.rose_organization_service.location.rest.dto.LocationCreateDto;
import net.rose.rose_organization_service.location.rest.dto.LocationReadDto;
import net.rose.rose_organization_service.location.rest.dto.LocationUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class LocationRestMapper {

    public CreateLocationCommand toCommand(
            LocationCreateDto dto,
            RestaurantId restaurantId
    ) {
        return new CreateLocationCommand(
                restaurantId,
                dto.name(),
                dto.city(),
                dto.street(),
                dto.buildingNumber(),
                dto.postalCode()
        );
    }

    public UpdateLocationCommand toCommand(
            LocationUpdateDto dto,
            LocationId locationId
    ) {
        return new UpdateLocationCommand(
                locationId,
                dto.name(),
                dto.city(),
                dto.street(),
                dto.buildingNumber(),
                dto.postalCode()
        );
    }

    public LocationReadDto toDto(
            LocationDetails details
    ) {
        return new LocationReadDto(
                details.id().getId(),
                details.restaurantId().getId(),
                details.name(),
                details.city(),
                details.street(),
                details.buildingNumber(),
                details.postalCode(),
                details.status(),
                details.createdAt(),
                details.updatedAt()
        );
    }
}
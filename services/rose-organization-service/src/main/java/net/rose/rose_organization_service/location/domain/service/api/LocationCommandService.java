package net.rose.rose_organization_service.location.domain.service.api;

import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.location.domain.command.CreateLocationCommand;
import net.rose.rose_organization_service.location.domain.command.UpdateLocationCommand;

public interface LocationCommandService {

    LocationId create(CreateLocationCommand command);

    void update(UpdateLocationCommand command);

    void archive(LocationId locationId);
}
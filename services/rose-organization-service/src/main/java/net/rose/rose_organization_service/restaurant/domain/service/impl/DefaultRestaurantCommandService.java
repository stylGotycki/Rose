package net.rose.rose_organization_service.restaurant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.core.config.messaging.OrganizationMessagingProperties;
import net.rose.rose_organization_service.core.exception.NotFoundException;
import net.rose.rose_organization_service.core.integration.event.IntegrationEvent;
import net.rose.rose_organization_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.restaurant.domain.command.CreateRestaurantCommand;
import net.rose.rose_organization_service.restaurant.domain.command.UpdateRestaurantCommand;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantStatus;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantCommandService;
import net.rose.rose_organization_service.restaurant.integration.event.RestaurantCreatedIntegrationEvent;
import net.rose.rose_organization_service.restaurant.integration.event.RestaurantCreatedIntegrationEventFactory;
import net.rose.rose_organization_service.restaurant.persistence.entity.RestaurantEntity;
import net.rose.rose_organization_service.restaurant.persistence.repository.api.RestaurantRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultRestaurantCommandService implements RestaurantCommandService {

    private final RestaurantRepository repository;


    private final OutboxEventWriter outboxEventWriter;

    private final RestaurantCreatedIntegrationEventFactory
            restaurantCreatedEventFactory;

    private final OrganizationMessagingProperties messagingProperties;


    @Override
    @Transactional
    public RestaurantId create(CreateRestaurantCommand command) {
        RestaurantEntity entity = RestaurantEntity.builder()
                .name(command.name())
                .description(command.description())
                .taxId(command.taxId())
                .status(RestaurantStatus.ACTIVE)
                .build();

        RestaurantEntity saved = repository.save(entity);

        RestaurantId restaurantId =
                RestaurantId.of(saved.getId());

        IntegrationEvent<RestaurantCreatedIntegrationEvent> event =
                restaurantCreatedEventFactory.create(
                        restaurantId,
                        command.ownerUserId(),
                        saved.getName()
                );

        outboxEventWriter.write(
                messagingProperties.exchange(),
                messagingProperties.restaurantCreatedRoutingKey(),
                event
        );

        return restaurantId;
    }


    @Override
    @Transactional
    public void update(RestaurantId restaurantId, UpdateRestaurantCommand command) {
        RestaurantEntity entity = getActiveEntity(restaurantId);

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setTaxId(command.taxId());
    }

    @Override

    @Transactional
    public void archive(RestaurantId restaurantId) {
        RestaurantEntity entity = getActiveEntity(restaurantId);

        entity.setStatus(RestaurantStatus.ARCHIVED);
        entity.setArchivedAt(Instant.now());
    }


    private RestaurantEntity getActiveEntity(RestaurantId restaurantId) {
        return repository.findById(restaurantId.getId())
                .filter(entity -> !entity.isArchived())
                .orElseThrow(() -> NotFoundException.of("Restaurant", restaurantId));
    }
}

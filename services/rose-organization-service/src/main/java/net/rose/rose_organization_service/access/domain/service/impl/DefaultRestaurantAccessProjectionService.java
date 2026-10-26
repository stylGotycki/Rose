package net.rose.rose_organization_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessProjectionService;
import net.rose.rose_organization_service.access.integration.event.RestaurantMemberChangedEvent;
import net.rose.rose_organization_service.access.persistence.entity.RestaurantAccessProjectionEntity;
import net.rose.rose_organization_service.access.persistence.repository.api.RestaurantAccessProjectionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultRestaurantAccessProjectionService
        implements RestaurantAccessProjectionService {

    private final RestaurantAccessProjectionRepository repository;

    @Override
    public void apply(RestaurantMemberChangedEvent event) {
        validate(event);

        Instant now = Instant.now();

        RestaurantAccessProjectionEntity projection =
                repository.findById(event.memberId())
                        .orElseGet(() ->
                                RestaurantAccessProjectionEntity.builder()
                                        .memberId(event.memberId())
                                        .restaurantId(event.restaurantId())
                                        .userId(event.userId())
                                        .role(event.role())
                                        .status(event.status())
                                        .sourceChangedAt(event.changedAt())
                                        .updatedAt(now)
                                        .build()
                        );

        if (projection.getSourceChangedAt() != null
                && projection.getSourceChangedAt()
                .isAfter(event.changedAt())) {
            return;
        }

        projection.update(
                event.role(),
                event.status(),
                event.changedAt(),
                now
        );

        repository.save(projection);
    }

    private void validate(RestaurantMemberChangedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Restaurant member event cannot be null"
            );
        }

        if (event.memberId() == null
                || event.restaurantId() == null
                || event.userId() == null) {
            throw new IllegalArgumentException(
                    "Restaurant member event identifiers cannot be null"
            );
        }

        if (event.role() == null || event.role().isBlank()) {
            throw new IllegalArgumentException(
                    "Restaurant member role cannot be blank"
            );
        }

        if (event.status() == null || event.status().isBlank()) {
            throw new IllegalArgumentException(
                    "Restaurant member status cannot be blank"
            );
        }

        if (event.changedAt() == null) {
            throw new IllegalArgumentException(
                    "Changed at cannot be null"
            );
        }
    }
}
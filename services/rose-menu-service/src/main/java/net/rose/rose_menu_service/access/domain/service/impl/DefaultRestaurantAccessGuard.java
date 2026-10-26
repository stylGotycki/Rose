package net.rose.rose_menu_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.access.persistence.entity.RestaurantAccessProjectionEntity;
import net.rose.rose_menu_service.access.persistence.repository.api.RestaurantAccessProjectionRepository;
import net.rose.rose_menu_service.core.config.security.CurrentUserProvider;
import net.rose.rose_menu_service.core.exception.ForbiddenException;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultRestaurantAccessGuard implements RestaurantAccessGuard {

    private final RestaurantAccessProjectionRepository repository;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public RestaurantMemberRole requireActiveAccess(
            RestaurantId restaurantId
    ) {
        UUID userId = currentUserProvider
                .getCurrentUserId()
                .getId();

        RestaurantAccessProjectionEntity member =
                repository.findByRestaurantIdAndUserId(
                                restaurantId.getId(),
                                userId
                        )
                        .orElseThrow(() ->
                                new ForbiddenException(
                                        "User has no access to this restaurant"
                                )
                        );

        if (!"ACTIVE".equals(member.getStatus())) {
            throw new ForbiddenException(
                    "Restaurant membership is not active"
            );
        }

        try {
            return RestaurantMemberRole.valueOf(member.getRole());
        } catch (IllegalArgumentException exception) {
            throw new ForbiddenException(
                    "Unsupported restaurant member role: "
                            + member.getRole()
            );
        }
    }

    @Override
    public void requireAnyRole(
            RestaurantId restaurantId,
            RestaurantMemberRole... allowedRoles
    ) {
        RestaurantMemberRole currentRole =
                requireActiveAccess(restaurantId);

        boolean allowed = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == currentRole);

        if (!allowed) {
            throw new ForbiddenException(
                    "User does not have the required restaurant role"
            );
        }
    }
}
package net.rose.rose_access_service.access.domain.service.api;

import net.rose.rose_access_service.access.domain.command.RestaurantMemberCreateCommand;
import net.rose.rose_access_service.access.domain.command.RestaurantMemberUpdateRoleCommand;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;

import java.util.UUID;

public interface RestaurantAccessCommandService {

    RestaurantMemberEntity addMember(
            RestaurantMemberCreateCommand command
    );

    void synchronizeOwner(
            RestaurantId restaurantId,
            UserId ownerUserId
    );

    RestaurantMemberEntity updateRole(
            UUID memberId,
            RestaurantMemberUpdateRoleCommand command
    );

    void archive(UUID memberId);
}
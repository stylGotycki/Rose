package net.rose.rose_access_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.domain.command.RestaurantMemberCreateCommand;
import net.rose.rose_access_service.access.domain.command.RestaurantMemberUpdateRoleCommand;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberStatus;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessCommandService;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_access_service.access.integration.publisher.RestaurantAccessGrantedPublisher;
import net.rose.rose_access_service.access.integration.publisher.RestaurantMemberChangedPublisher;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.access.persistence.repository.api.RestaurantMemberRepository;
import net.rose.rose_access_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_access_service.core.exception.EntityAlreadyExistsException;
import net.rose.rose_access_service.core.exception.NotFoundException;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultRestaurantAccessCommandService
        implements RestaurantAccessCommandService {

    private final RestaurantMemberRepository repository;

    private final RestaurantAccessGuard accessGuard;

    private final RestaurantMemberChangedPublisher memberChangedPublisher;

    private final RestaurantAccessGrantedPublisher accessGrantedPublisher;

    @Override
    @Transactional
    public RestaurantMemberEntity addMember(
            RestaurantMemberCreateCommand command
    ) {
        accessGuard.requireRole(
                command.restaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        var existingMember =
                repository.findByRestaurantIdAndUserId(
                        command.restaurantId(),
                        command.userId()
                );

        if (existingMember.isPresent()) {
            RestaurantMemberEntity member = existingMember.get();

            if (member.getStatus() == RestaurantMemberStatus.ACTIVE) {
                throw new EntityAlreadyExistsException(
                        "User already has access to this restaurant"
                );
            }

            member.restore();
            member.changeRole(command.role());

            RestaurantMemberEntity savedMember =
                    repository.save(member);

            memberChangedPublisher.publish(savedMember);
            accessGrantedPublisher.publish(savedMember);

            return savedMember;
        }

        RestaurantMemberEntity member =
                RestaurantMemberEntity.builder()
                        .restaurantId(command.restaurantId())
                        .userId(command.userId())
                        .role(command.role())
                        .status(RestaurantMemberStatus.ACTIVE)
                        .build();

        RestaurantMemberEntity savedMember =
                repository.save(member);

        memberChangedPublisher.publish(savedMember);
        accessGrantedPublisher.publish(savedMember);

        return savedMember;
    }

    @Override
    @Transactional
    public void synchronizeOwner(
            RestaurantId restaurantId,
            UserId ownerUserId
    ) {
        var existingMember =
                repository.findByRestaurantIdAndUserId(
                        restaurantId,
                        ownerUserId
                );

        if (existingMember.isPresent()) {
            RestaurantMemberEntity member = existingMember.get();

            member.restore();
            member.changeRole(RestaurantMemberRole.OWNER);

            RestaurantMemberEntity savedMember =
                    repository.save(member);

            memberChangedPublisher.publish(savedMember);
            return;
        }

        RestaurantMemberEntity owner =
                RestaurantMemberEntity.builder()
                        .restaurantId(restaurantId)
                        .userId(ownerUserId)
                        .role(RestaurantMemberRole.OWNER)
                        .status(RestaurantMemberStatus.ACTIVE)
                        .build();

        RestaurantMemberEntity savedOwner =
                repository.save(owner);

        memberChangedPublisher.publish(savedOwner);
    }

    @Override
    @Transactional
    public RestaurantMemberEntity updateRole(
            UUID memberId,
            RestaurantMemberUpdateRoleCommand command
    ) {
        RestaurantMemberEntity member =
                repository.findByIdAndStatus(
                                memberId,
                                RestaurantMemberStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Restaurant member not found"
                                )
                        );

        accessGuard.requireRole(
                member.getRestaurantId(),
                RestaurantMemberRole.OWNER
        );

        if (member.getRole() == RestaurantMemberRole.OWNER
                && command.role() != RestaurantMemberRole.OWNER) {
            throw new BusinessRuleViolationException(
                    "Restaurant owner role cannot be changed"
            );
        }

        member.changeRole(command.role());

        RestaurantMemberEntity savedMember =
                repository.save(member);

        memberChangedPublisher.publish(savedMember);

        return savedMember;
    }

    @Override
    @Transactional
    public void archive(UUID memberId) {
        RestaurantMemberEntity member =
                repository.findByIdAndStatus(
                                memberId,
                                RestaurantMemberStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Restaurant member not found"
                                )
                        );

        accessGuard.requireRole(
                member.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        if (member.getRole() == RestaurantMemberRole.OWNER) {
            throw new BusinessRuleViolationException(
                    "Restaurant owner cannot be archived"
            );
        }

        member.archive();

        RestaurantMemberEntity savedMember =
                repository.save(member);

        memberChangedPublisher.publish(savedMember);
    }
}
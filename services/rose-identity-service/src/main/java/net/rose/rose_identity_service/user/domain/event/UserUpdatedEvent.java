package net.rose.rose_identity_service.user.domain.event;

import net.rose.rose_identity_service.core.persistence.id.UserId;


public record UserUpdatedEvent(
        UserId userId
) {
}

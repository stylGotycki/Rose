package net.rose.rose_identity_service.user.domain.event;

import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.model.UserStatus;


public record UserStatusChangedEvent(
        UserId userId,
        UserStatus newStatus
) {
}

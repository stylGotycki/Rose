package net.rose.rose_identity_service.core.config.security;

import net.rose.rose_identity_service.core.exception.UnauthorizedException;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public CurrentUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CurrentUser currentUser)) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        return currentUser;
    }

    public UserId getCurrentUserId() {
        return getCurrentUser().userId();
    }


}

package net.rose.rose_identity_service.user.domain.service.api;

import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.model.UserProfile;

import java.util.List;

public interface UserQueryService {

    List<UserProfile> getAll();

    List<UserProfile> searchActiveByEmail(String email);

    UserProfile getById(UserId userId);

    UserProfile getByEmail(String email);

    boolean existsById(UserId userId);

    boolean existsByEmail(String email);
}
package net.rose.rose_identity_service.user.rest.mapper;

import net.rose.rose_identity_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_identity_service.user.domain.command.UpdateUserCommand;
import net.rose.rose_identity_service.user.domain.model.UserProfile;
import net.rose.rose_identity_service.user.rest.dto.UserReadDto;
import net.rose.rose_identity_service.user.rest.dto.UserUpdateDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRestMapper {


    public CollectionRestDto<UserReadDto> toDto(List<UserProfile> profiles) {
        return CollectionRestDto.<UserReadDto>builder()
                .items(profiles.stream().map(this::toDto).toList())
                .build();
    }


    public UserReadDto toDto(UserProfile profile) {
        return new UserReadDto(
                profile.id().getId(),
                profile.firstName(),
                profile.lastName(),
                profile.email(),
                profile.status()
        );
    }


    public UpdateUserCommand toCommand(UserUpdateDto dto) {
        return new UpdateUserCommand(
                dto.firstName(),
                dto.lastName()
        );
    }

}
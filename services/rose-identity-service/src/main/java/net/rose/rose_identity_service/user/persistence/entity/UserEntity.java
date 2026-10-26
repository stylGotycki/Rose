package net.rose.rose_identity_service.user.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rose.rose_identity_service.user.domain.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false, name = "first_name")
    private String firstName;


    @Column(nullable = false, name = "last_name")
    private String lastName;


    @Column(nullable = false, unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserStatus status;


    @Column(nullable = true, unique = false, name = "phone_number")
    private String phoneNumber;


    @Column(nullable = false, name = "created_at")
    private Instant createdAt;


    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;


    @Column(name = "verified_at")
    private Instant verifiedAt;


    @Column(name = "disabled_at")
    private Instant disabledAt;


    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = UserStatus.PENDING_VERIFICATION;
        }
    }


    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

}

package net.rose.rose_workforce_service.employee.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.core.persistence.id.UserId;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeRole;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_employee_restaurant_email",
                        columnNames = {"restaurant_id", "email"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "restaurant_id",
                    nullable = false
            )
    )
    private RestaurantId restaurantId;

    @Embedded
    @AttributeOverride(
            name = "id",
            column = @Column(
                    name = "user_id",
                    nullable = true
            )
    )
    private UserId userId;

    @Column(
            name = "first_name",
            nullable = false,
            length = 64
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 64
    )
    private String lastName;

    @Column(
            nullable = false,
            length = 128
    )
    private String email;

    @Column(length = 32)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 32
    )
    private EmployeeRole role;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 32
    )
    private EmployeeStatus status;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void archive() {
        this.status = EmployeeStatus.ARCHIVED;
    }

    public boolean isArchived() {
        return this.status == EmployeeStatus.ARCHIVED;
    }
}
package net.rose.rose_workforce_service.schedule.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.schedule.domain.model.ScheduleShiftStatus;
import net.rose.rose_workforce_service.schedule.domain.model.ShiftPosition;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule_shifts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleShiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "restaurant_id", nullable = false))
    private RestaurantId restaurantId;


    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "employee_id", nullable = false))
    private EmployeeId employeeId;


    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;


    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ShiftPosition position;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ScheduleShiftStatus status;


    @Column(columnDefinition = "TEXT")
    private String note;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public void cancel() {
        this.status = ScheduleShiftStatus.CANCELLED;
    }


    public void complete() {
        this.status = ScheduleShiftStatus.COMPLETED;
    }
}
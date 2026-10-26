package net.rose.rose_identity_service.user.integration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_identity_service.auth.domain.service.api.EmailSender;
import net.rose.rose_identity_service.core.integration.event.IntegrationEvent;
import net.rose.rose_identity_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_identity_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.model.UserProfile;
import net.rose.rose_identity_service.user.domain.service.api.UserQueryService;
import net.rose.rose_identity_service.user.integration.event.RestaurantAccessGrantedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RestaurantAccessGrantedListener {

    private static final String SUPPORTED_EVENT_TYPE =
            "access.restaurant-access-granted";

    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final InboxEventProcessor inboxEventProcessor;

    private final UserQueryService userQueryService;

    private final EmailSender emailSender;

    @RabbitListener(
            queues = "${rose.messaging.access.access-granted-queue}"
    )
    void on(
            @Payload
            IntegrationEvent<RestaurantAccessGrantedEvent> event
    ) {
        validate(event);

        RestaurantAccessGrantedEvent payload = event.payload();

        InboxProcessingResult result =
                inboxEventProcessor.process(
                        event,
                        () -> sendAccessGrantedEmail(payload)
                );

        if (result == InboxProcessingResult.ALREADY_PROCESSED) {
            log.info(
                    "Skipped already processed restaurant-access-granted event: eventId={}",
                    event.eventId()
            );
            return;
        }

        log.info(
                "Sent restaurant access notification: eventId={}, userId={}, restaurantId={}",
                event.eventId(),
                payload.userId(),
                payload.restaurantId()
        );
    }

    private void sendAccessGrantedEmail(
            RestaurantAccessGrantedEvent event
    ) {
        UserProfile user = userQueryService.getById(
                UserId.of(event.userId())
        );

        String body = """
                Witaj %s,

                nadano Ci dostęp do restauracji w systemie ROSE.

                Rola: %s
                Identyfikator restauracji: %s

                Po zalogowaniu do aplikacji restauracja będzie dostępna
                na liście restauracji przypisanych do Twojego konta.

                Jeżeli nie spodziewasz się tego dostępu, skontaktuj się
                z osobą zarządzającą restauracją.
                """.formatted(
                user.firstName(),
                event.role(),
                event.restaurantId()
        );

        emailSender.send(
                user.email(),
                "Nadano dostęp do restauracji w ROSE",
                body
        );
    }

    private void validate(
            IntegrationEvent<RestaurantAccessGrantedEvent> event
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Restaurant-access-granted event cannot be null"
            );
        }

        if (!SUPPORTED_EVENT_TYPE.equals(event.eventType())) {
            throw new IllegalArgumentException(
                    "Unsupported event type: "
                            + event.eventType()
            );
        }

        if (event.eventVersion() != SUPPORTED_EVENT_VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported event version: "
                            + event.eventVersion()
            );
        }

        if (event.payload() == null) {
            throw new IllegalArgumentException(
                    "Event payload cannot be null"
            );
        }
    }
}
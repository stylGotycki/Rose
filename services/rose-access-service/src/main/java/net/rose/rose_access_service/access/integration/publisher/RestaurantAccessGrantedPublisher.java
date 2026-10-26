package net.rose.rose_access_service.access.integration.publisher;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.integration.event.RestaurantAccessGrantedEvent;
import net.rose.rose_access_service.access.integration.event.RestaurantAccessGrantedEventFactory;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.config.messaging.AccessMessagingProperties;
import net.rose.rose_access_service.core.integration.event.IntegrationEvent;
import net.rose.rose_access_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantAccessGrantedPublisher {

    private final RestaurantAccessGrantedEventFactory eventFactory;

    private final OutboxEventWriter outboxEventWriter;

    private final AccessMessagingProperties messagingProperties;

    public void publish(RestaurantMemberEntity member) {
        IntegrationEvent<RestaurantAccessGrantedEvent> event =
                eventFactory.create(member);

        outboxEventWriter.write(
                messagingProperties.exchange(),
                messagingProperties.accessGrantedRoutingKey(),
                event
        );
    }
}
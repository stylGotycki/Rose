package net.rose.rose_access_service.access.integration.publisher;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.integration.event.RestaurantMemberChangedEvent;
import net.rose.rose_access_service.access.integration.event.RestaurantMemberChangedEventFactory;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.config.messaging.AccessMessagingProperties;
import net.rose.rose_access_service.core.integration.event.IntegrationEvent;
import net.rose.rose_access_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantMemberChangedPublisher {

    private final RestaurantMemberChangedEventFactory eventFactory;

    private final OutboxEventWriter outboxEventWriter;

    private final AccessMessagingProperties messagingProperties;

    public void publish(RestaurantMemberEntity member) {
        IntegrationEvent<RestaurantMemberChangedEvent> event =
                eventFactory.create(member);

        outboxEventWriter.write(
                messagingProperties.exchange(),
                messagingProperties.memberChangedRoutingKey(),
                event
        );
    }
}
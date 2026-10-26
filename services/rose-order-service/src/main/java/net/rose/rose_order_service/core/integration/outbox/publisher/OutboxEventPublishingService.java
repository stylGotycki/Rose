package net.rose.rose_order_service.core.integration.outbox.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_order_service.core.integration.outbox.persistence.entity.OutboxEventEntity;
import net.rose.rose_order_service.core.integration.outbox.persistence.repository.api.OutboxEventRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPublishingService {

    private static final Duration CONFIRM_TIMEOUT =
            Duration.ofSeconds(5);

    private static final Duration RETRY_DELAY =
            Duration.ofSeconds(10);

    private final OutboxEventRepository repository;

    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void publish(UUID eventId) {
        OutboxEventEntity event = repository.findById(eventId)
                .orElseThrow();

        try {
            Message message = buildMessage(event);

            CorrelationData correlationData =
                    new CorrelationData(
                            event.getEventId().toString()
                    );

            rabbitTemplate.send(
                    event.getExchangeName(),
                    event.getRoutingKey(),
                    message,
                    correlationData
            );

            CorrelationData.Confirm confirm =
                    correlationData.getFuture()
                            .get(
                                    CONFIRM_TIMEOUT.toMillis(),
                                    TimeUnit.MILLISECONDS
                            );

            if (!confirm.ack()) {
                throw new IllegalStateException(
                        "RabbitMQ rejected event: "
                                + confirm.reason()
                );
            }

            if (correlationData.getReturned() != null) {
                throw new IllegalStateException(
                        "RabbitMQ returned event: "
                                + Objects.requireNonNull(correlationData
                                        .getReturned())
                                .getReplyText()
                );
            }

            event.markPublished(Instant.now());

            log.info(
                    "Published outbox event: eventId={}, eventType={}",
                    event.getEventId(),
                    event.getEventType()
            );
        } catch (Exception exception) {
            event.registerFailure(
                    exception.getMessage(),
                    Instant.now().plus(RETRY_DELAY)
            );

            log.error(
                    "Could not publish outbox event: eventId={}, attempt={}",
                    event.getEventId(),
                    event.getAttempts(),
                    exception
            );
        }
    }

    private Message buildMessage(OutboxEventEntity event) {
        return MessageBuilder
                .withBody(
                        event.getPayload()
                                .getBytes(StandardCharsets.UTF_8)
                )
                .setContentType("application/json")
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setMessageId(event.getEventId().toString())
                .setHeader(
                        "eventType",
                        event.getEventType()
                )
                .setHeader(
                        "eventVersion",
                        event.getEventVersion()
                )
                .build();
    }
}
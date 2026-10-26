package net.rose.rose_identity_service.core.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableConfigurationProperties(AccessMessagingProperties.class)
public class RabbitMessagingConfig {

    private static final String DEAD_LETTER_EXCHANGE = "";

    @Bean
    DirectExchange accessEventsExchange(
            AccessMessagingProperties properties
    ) {
        return new DirectExchange(
                properties.exchange(),
                true,
                false
        );
    }

    @Bean
    Queue restaurantAccessGrantedDeadLetterQueue(
            AccessMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(
                        properties.accessGrantedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Queue restaurantAccessGrantedQueue(
            AccessMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(properties.accessGrantedQueue())
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(
                        properties.accessGrantedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Binding restaurantAccessGrantedBinding(
            @Qualifier("restaurantAccessGrantedQueue")
            Queue queue,
            @Qualifier("accessEventsExchange")
            DirectExchange exchange,
            AccessMessagingProperties properties
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.accessGrantedRoutingKey());
    }

    @Bean
    MessageConverter rabbitMessageConverter(
            JsonMapper jsonMapper
    ) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
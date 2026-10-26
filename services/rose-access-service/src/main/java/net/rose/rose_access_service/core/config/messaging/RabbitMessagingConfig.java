package net.rose.rose_access_service.core.config.messaging;

import org.springframework.amqp.core.*;
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
@EnableConfigurationProperties({
        AccessMessagingProperties.class,
        OrganizationMessagingProperties.class
})
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
    DirectExchange organizationEventsExchange(
            OrganizationMessagingProperties properties
    ) {
        return new DirectExchange(
                properties.exchange(),
                true,
                false
        );
    }

    @Bean
    Queue restaurantCreatedDeadLetterQueue(
            OrganizationMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(
                        properties.restaurantCreatedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Queue restaurantCreatedQueue(
            OrganizationMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(properties.restaurantCreatedQueue())
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(
                        properties.restaurantCreatedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Binding restaurantCreatedBinding(
            @Qualifier("restaurantCreatedQueue")
            Queue queue,
            @Qualifier("organizationEventsExchange")
            DirectExchange exchange,
            OrganizationMessagingProperties properties
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.restaurantCreatedRoutingKey());
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
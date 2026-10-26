package net.rose.rose_menu_service.core.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        MenuMessagingProperties.class
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
    DirectExchange menuEventsExchange(
            MenuMessagingProperties properties
    ) {
        return new DirectExchange(
                properties.exchange(),
                true,
                false
        );
    }

    @Bean
    Queue restaurantMemberChangedDeadLetterQueue(
            AccessMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(
                        properties.memberChangedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Queue restaurantMemberChangedQueue(
            AccessMessagingProperties properties
    ) {
        return QueueBuilder
                .durable(properties.memberChangedQueue())
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(
                        properties.memberChangedDeadLetterQueue()
                )
                .build();
    }

    @Bean
    Binding restaurantMemberChangedBinding(
            @Qualifier("restaurantMemberChangedQueue")
            Queue queue,
            @Qualifier("accessEventsExchange")
            DirectExchange exchange,
            AccessMessagingProperties properties
    ) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(properties.memberChangedRoutingKey());
    }

    @Bean
    MessageConverter rabbitMessageConverter(
            JsonMapper jsonMapper
    ) {
        return new JacksonJsonMessageConverter(
                jsonMapper
        );
    }

    @Bean
    RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(
                rabbitMessageConverter
        );
        template.setMandatory(true);

        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory
    rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(
                rabbitMessageConverter
        );
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
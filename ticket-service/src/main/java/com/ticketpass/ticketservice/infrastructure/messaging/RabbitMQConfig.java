package com.ticketpass.ticketservice.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "ticketpass.exchange";
    public static final String BOOKING_REQUESTS_QUEUE = "booking.requests";
    public static final String PAYMENT_STATUS_QUEUE = "payment.status";
    public static final String BOOKING_REQUESTS_ROUTING_KEY = "booking.request.created";
    public static final String PAYMENT_STATUS_ROUTING_KEY = "payment.status.updated";

    // Dead Letter Exchange and Queue Names
    public static final String DLX_NAME = "ticketpass.dlx";
    public static final String BOOKING_REQUESTS_DLQ = "booking.requests.dlq";
    public static final String PAYMENT_STATUS_DLQ = "payment.status.dlq";

    @Bean
    public TopicExchange ticketpassExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange ticketpassDlx() {
        return new TopicExchange(DLX_NAME);
    }

    @Bean
    public Queue bookingRequestsQueue() {
        return QueueBuilder.durable(BOOKING_REQUESTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", BOOKING_REQUESTS_DLQ)
                .build();
    }

    @Bean
    public Queue paymentStatusQueue() {
        return QueueBuilder.durable(PAYMENT_STATUS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", PAYMENT_STATUS_DLQ)
                .build();
    }

    @Bean
    public Queue bookingRequestsDlq() {
        return QueueBuilder.durable(BOOKING_REQUESTS_DLQ).build();
    }

    @Bean
    public Queue paymentStatusDlq() {
        return QueueBuilder.durable(PAYMENT_STATUS_DLQ).build();
    }

    @Bean
    public Binding bookingRequestsBinding(Queue bookingRequestsQueue, TopicExchange ticketpassExchange) {
        return BindingBuilder.bind(bookingRequestsQueue).to(ticketpassExchange).with(BOOKING_REQUESTS_ROUTING_KEY);
    }

    @Bean
    public Binding paymentStatusBinding(Queue paymentStatusQueue, TopicExchange ticketpassExchange) {
        return BindingBuilder.bind(paymentStatusQueue).to(ticketpassExchange).with(PAYMENT_STATUS_ROUTING_KEY);
    }

    @Bean
    public Binding bookingRequestsDlqBinding(Queue bookingRequestsDlq, TopicExchange ticketpassDlx) {
        return BindingBuilder.bind(bookingRequestsDlq).to(ticketpassDlx).with(BOOKING_REQUESTS_DLQ);
    }

    @Bean
    public Binding paymentStatusDlqBinding(Queue paymentStatusDlq, TopicExchange ticketpassDlx) {
        return BindingBuilder.bind(paymentStatusDlq).to(ticketpassDlx).with(PAYMENT_STATUS_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

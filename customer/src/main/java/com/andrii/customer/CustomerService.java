package com.andrii.customer;

import com.andrii.amqp.RabbitMQMessageProducer;
import com.andrii.clients.fraud.FraudCheckResponse;
import com.andrii.clients.fraud.FraudClient;
import com.andrii.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    //private final FraudClient fraudClient;
    private final RestTemplate restTemplate;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
                "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );
        if (fraudCheckResponse != null && fraudCheckResponse.getIsFraudster()) {
            throw new IllegalStateException("fraudster");
        }
//        FraudCheckResponse fraudCheckResponse =
//                fraudClient.isFraudster(customer.getId());
//
//        if (fraudCheckResponse.getIsFraudster()) {
//            throw new IllegalStateException("fraudster");
//        }


        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to microservices...",
                        customer.getFirstName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}

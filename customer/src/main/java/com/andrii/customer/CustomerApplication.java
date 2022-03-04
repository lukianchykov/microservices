package com.andrii.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(
        scanBasePackages = {
                "com.andrii.customer",
                "com.andrii.amqp",
        }
)
@EnableEurekaClient
@EnableFeignClients(
        basePackages = "com.andrii.clients"
)
@PropertySources({
        @PropertySource(
                {       "classpath:clients-default.properties",
                        "classpath:clients-docker.properties",
                        "classpath:clients-kube.properties"
                })
})
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}

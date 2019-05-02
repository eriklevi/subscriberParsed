package com.example.subscriber;

import com.example.subscriber.mqtt.MQTTSubscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class SubscriberApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SubscriberApplication.class);
        final ApplicationContext context = application.run(args);
        MQTTSubscriber subscriber = context.getBean(MQTTSubscriber.class);
    }
}

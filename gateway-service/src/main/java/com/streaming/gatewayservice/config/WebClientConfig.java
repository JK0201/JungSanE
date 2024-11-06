package com.streaming.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static com.streaming.gatewayservice.constant.ApiPath.LoadBalancerUri.USER_SERVICE_LB;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userServiceWebClient(ReactorLoadBalancerExchangeFilterFunction loadBalancerFilter) {
        return WebClient.builder()
                .filter(loadBalancerFilter)
                .baseUrl(USER_SERVICE_LB)
                .build();
    }
}

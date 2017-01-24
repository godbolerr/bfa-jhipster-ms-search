package com.bfa.searchms.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.bfa.searchms")
public class FeignConfiguration {

}

package com.project_kata.global_invoices_kata_mngr.infrastructure.soap;

import com.project_kata.global_invoices_kata_mngr.infrastructure.config.SoapNumberConversionProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class NumberConversionClientConfig {

    @Bean
    RestClient numberConversionRestClient(RestClient.Builder builder,
                                          SoapNumberConversionProperties properties) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(properties.connectTimeout())
                .withReadTimeout(properties.readTimeout());
        return builder
                .baseUrl(properties.url())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }
}

package com.project_kata.global_invoices_kata_mngr.infrastructure.soap;

import com.project_kata.global_invoices_kata_mngr.infrastructure.config.SoapNumberConversionProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Llama al servicio SOAP publico real de DataFlex NumberConversion")
class DataFlexSoapAdapterLiveTest {

    @Test
    void convertsAgainstTheRealService() {
        SoapNumberConversionProperties props = new SoapNumberConversionProperties(
                "https://www.dataaccess.com/webservicesserver/NumberConversion.wso",
                Duration.ofSeconds(3), Duration.ofSeconds(6));
        RestClient restClient = RestClient.builder()
                .baseUrl(props.url())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(
                        ClientHttpRequestFactorySettings.defaults()
                                .withConnectTimeout(props.connectTimeout())
                                .withReadTimeout(props.readTimeout())))
                .build();

        assertThat(new DataFlexSoapAdapter(restClient).toText(new BigDecimal("150")))
                .get().asString().containsIgnoringCase("fifty");
    }
}

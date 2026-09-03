package com.project_kata.global_invoices_kata_mngr.infrastructure.soap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DataFlexSoapAdapterTest {

    private static final String URL = "https://soap.test/NumberConversion.wso";

    private static final String OK_RESPONSE = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <m:NumberToWordsResponse xmlns:m="http://www.dataaccess.com/webservicesserver/">
                  <m:NumberToWordsResult>one thousand one hundred and ninety </m:NumberToWordsResult>
                </m:NumberToWordsResponse>
              </soap:Body>
            </soap:Envelope>""";

    private static final String FAULT_RESPONSE = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body><soap:Fault><faultstring>boom</faultstring></soap:Fault></soap:Body>
            </soap:Envelope>""";

    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private DataFlexSoapAdapter adapter;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder().baseUrl(URL);
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new DataFlexSoapAdapter(builder.build());
    }

    @Test
    void returnsWordsAndSendsTheExpectedEnvelope() {
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(containsString("<ubiNum>1190</ubiNum>")))
                .andRespond(withSuccess(OK_RESPONSE, MediaType.TEXT_XML));

        Optional<String> result = adapter.toText(new BigDecimal("1190.00"));

        assertThat(result).contains("one thousand one hundred and ninety");
        server.verify();
    }

    @Test
    void appendsCentsWhenPresent() {
        server.expect(requestTo(URL)).andRespond(withSuccess(OK_RESPONSE, MediaType.TEXT_XML));

        assertThat(adapter.toText(new BigDecimal("1190.50")))
                .contains("one thousand one hundred and ninety and 50/100");
    }

    @Test
    void returnsEmptyOnHttpError() {
        server.expect(requestTo(URL)).andRespond(withServerError());

        assertThat(adapter.toText(new BigDecimal("150"))).isEmpty();
    }

    @Test
    void returnsEmptyOnSoapFault() {
        server.expect(requestTo(URL)).andRespond(withSuccess(FAULT_RESPONSE, MediaType.TEXT_XML));

        assertThat(adapter.toText(new BigDecimal("150"))).isEmpty();
    }

    @Test
    void returnsEmptyOnMalformedXml() {
        server.expect(requestTo(URL)).andRespond(withSuccess("<<< not xml", MediaType.TEXT_XML));

        assertThat(adapter.toText(new BigDecimal("150"))).isEmpty();
    }

    @Test
    void cachesByIntegerAmountSoTheServiceIsHitOnce() {
        server.expect(ExpectedCount.once(), requestTo(URL))
                .andRespond(withSuccess(OK_RESPONSE, MediaType.TEXT_XML));

        adapter.toText(new BigDecimal("1190.00"));
        adapter.toText(new BigDecimal("1190.99"));

        server.verify();
    }

    @Test
    void returnsEmptyForNegativeAmountWithoutCallingService() {
        assertThat(adapter.toText(new BigDecimal("-5"))).isEmpty();
        server.verify();
    }
}

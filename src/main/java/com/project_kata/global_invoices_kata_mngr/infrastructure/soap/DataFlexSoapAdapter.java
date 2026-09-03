package com.project_kata.global_invoices_kata_mngr.infrastructure.soap;

import com.project_kata.global_invoices_kata_mngr.domain.port.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador del servicio SOAP legacy DataFlex NumberConversion (operacion {@code NumberToWords}).
 * Aisla el detalle SOAP del dominio. Ante cualquier fallo del servicio publico
 * devuelve Optional.empty() en vez de propagar la excepcion.
 */
@Component
class DataFlexSoapAdapter implements NumberToTextConverter {

    private static final Logger log = LoggerFactory.getLogger(DataFlexSoapAdapter.class);

    private static final String NS = "http://www.dataaccess.com/webservicesserver/";
    private static final String RESULT_ELEMENT = "NumberToWordsResult";
    private static final int MAX_CACHE_ENTRIES = 500;

    private final RestClient restClient;
    private final Map<BigInteger, String> cache = new ConcurrentHashMap<>();

    DataFlexSoapAdapter(RestClient numberConversionRestClient) {
        this.restClient = numberConversionRestClient;
    }

    @Override
    public Optional<String> toText(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            return Optional.empty();
        }
        BigInteger integerPart = amount.toBigInteger();

        String words = cache.get(integerPart);
        if (words == null) {
            words = callService(integerPart).orElse(null);
            if (words == null) {
                return Optional.empty();
            }
            if (cache.size() < MAX_CACHE_ENTRIES) {
                cache.putIfAbsent(integerPart, words);
            }
        }
        return Optional.of(appendCents(words, amount));
    }

    private Optional<String> callService(BigInteger number) {
        try {
            String response = restClient.post()
                    .contentType(MediaType.TEXT_XML)
                    .header("SOAPAction", "")
                    .body(envelope(number))
                    .retrieve()
                    .body(String.class);
            return Optional.of(parseResult(response));
        } catch (Exception ex) {
            log.warn("Conversion a letras no disponible para {}: {}", number, ex.toString());
            return Optional.empty();
        }
    }

    private static String envelope(BigInteger number) {
        return """
                <?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <NumberToWords xmlns="%s">
                      <ubiNum>%s</ubiNum>
                    </NumberToWords>
                  </soap:Body>
                </soap:Envelope>""".formatted(NS, number);
    }

    private static String parseResult(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        NodeList nodes = document.getElementsByTagNameNS(NS, RESULT_ELEMENT);
        if (nodes.getLength() == 0 || nodes.item(0).getTextContent() == null
                || nodes.item(0).getTextContent().isBlank()) {
            throw new IllegalStateException("Respuesta SOAP sin " + RESULT_ELEMENT);
        }
        return nodes.item(0).getTextContent().trim();
    }

    private static String appendCents(String words, BigDecimal amount) {
        BigInteger cents = amount.remainder(BigDecimal.ONE).movePointRight(2).abs().toBigInteger();
        return cents.signum() == 0 ? words : words + " and " + cents + "/100";
    }
}

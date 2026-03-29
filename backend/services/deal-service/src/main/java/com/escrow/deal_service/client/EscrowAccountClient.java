package com.escrow.deal_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class EscrowAccountClient {

    private final RestClient restClient;

    public EscrowAccountClient(@Value("${app.clients.escrow-account-service-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void openAccount(UUID dealId, BigDecimal amount, String currency, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bearerToken);

        Map<String, Object> payload = Map.of(
                "dealId", dealId,
                "amount", amount,
                "currency", currency
        );

        restClient.post()
                .uri("/api/v1/escrow-accounts/open")
                .headers(h -> h.addAll(headers))
                .body(new HttpEntity<>(payload, headers).getBody())
                .retrieve()
                .toBodilessEntity();
    }
}

package com.escrow.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("escrow")
            .withInitScript("init-schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BENEFICIARY_ID = UUID.randomUUID();

    @Test
    void getOrCreateAccount() throws Exception {
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                .andExpect(jsonPath("$.balance").value(10000.00))
                .andExpect(jsonPath("$.currency").value("RUB"));

        // Second call returns same account
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10000.00));
    }

    @Test
    void holdAndReleaseFunds() throws Exception {
        UUID depositorId = UUID.randomUUID();
        UUID beneficiaryId = UUID.randomUUID();
        UUID dealId = UUID.randomUUID();

        // Ensure accounts exist
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", depositorId.toString()))
                .andExpect(status().isOk());

        // Hold funds
        String holdBody = """
                {
                    "dealId": "%s",
                    "depositorId": "%s",
                    "amount": 3000.00
                }
                """.formatted(dealId, depositorId);

        mockMvc.perform(post("/internal/payments/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(holdBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HELD"));

        // Check depositor balance decreased
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", depositorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(7000.00));

        // Release funds to beneficiary
        String releaseBody = """
                {
                    "dealId": "%s",
                    "beneficiaryId": "%s"
                }
                """.formatted(dealId, beneficiaryId);

        mockMvc.perform(post("/internal/payments/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(releaseBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RELEASED"));

        // Check beneficiary balance increased
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", beneficiaryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(13000.00));
    }

    @Test
    void holdInsufficientFunds() throws Exception {
        UUID depositorId = UUID.randomUUID();
        UUID dealId = UUID.randomUUID();

        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", depositorId.toString()))
                .andExpect(status().isOk());

        String holdBody = """
                {
                    "dealId": "%s",
                    "depositorId": "%s",
                    "amount": 99999.00
                }
                """.formatted(dealId, depositorId);

        mockMvc.perform(post("/internal/payments/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(holdBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds"));
    }

    @Test
    void holdIdempotency() throws Exception {
        UUID depositorId = UUID.randomUUID();
        UUID dealId = UUID.randomUUID();

        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", depositorId.toString()))
                .andExpect(status().isOk());

        String holdBody = """
                {
                    "dealId": "%s",
                    "depositorId": "%s",
                    "amount": 1000.00
                }
                """.formatted(dealId, depositorId);

        // First hold
        mockMvc.perform(post("/internal/payments/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(holdBody))
                .andExpect(status().isOk());

        // Second hold (idempotent — no double debit)
        mockMvc.perform(post("/internal/payments/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(holdBody))
                .andExpect(status().isOk());

        // Balance should only be debited once
        mockMvc.perform(get("/api/payments/account")
                        .header("X-User-Id", depositorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(9000.00));
    }

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}

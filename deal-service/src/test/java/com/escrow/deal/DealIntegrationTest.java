package com.escrow.deal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
class DealIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("escrow")
            .withInitScript("init-schema.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID DEPOSITOR_ID = UUID.randomUUID();
    private static final UUID BENEFICIARY_ID = UUID.randomUUID();

    @Test
    void createAndGetDeal() throws Exception {
        String body = """
                {
                    "title": "Test Deal",
                    "description": "Integration test deal",
                    "amount": 5000.00,
                    "currency": "RUB",
                    "beneficiaryId": "%s"
                }
                """.formatted(BENEFICIARY_ID);

        MvcResult createResult = mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Deal"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.depositorId").value(DEPOSITOR_ID.toString()))
                .andExpect(jsonPath("$.beneficiaryId").value(BENEFICIARY_ID.toString()))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String dealId = created.get("id").asText();

        // Get as depositor
        mockMvc.perform(get("/api/deals/" + dealId)
                        .header("X-User-Id", DEPOSITOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dealId));

        // Get as beneficiary
        mockMvc.perform(get("/api/deals/" + dealId)
                        .header("X-User-Id", BENEFICIARY_ID.toString()))
                .andExpect(status().isOk());

        // Get events
        mockMvc.perform(get("/api/deals/" + dealId + "/events")
                        .header("X-User-Id", DEPOSITOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("DEAL_CREATED"))
                .andExpect(jsonPath("$[0].newStatus").value("CREATED"));
    }

    @Test
    void listDeals() throws Exception {
        String body = """
                {
                    "title": "List Test Deal",
                    "amount": 1000.00,
                    "beneficiaryId": "%s"
                }
                """.formatted(BENEFICIARY_ID);

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/deals")
                        .header("X-User-Id", DEPOSITOR_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void cancelDeal() throws Exception {
        String body = """
                {
                    "title": "Cancel Test",
                    "amount": 2000.00,
                    "beneficiaryId": "%s"
                }
                """.formatted(BENEFICIARY_ID);

        MvcResult result = mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isCreated())
                .andReturn();

        String dealId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/deals/" + dealId + "/cancel")
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Cannot cancel again
        mockMvc.perform(post("/api/deals/" + dealId + "/cancel")
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isConflict());
    }

    @Test
    void accessDeniedForUnrelatedUser() throws Exception {
        String body = """
                {
                    "title": "Access Test",
                    "amount": 3000.00,
                    "beneficiaryId": "%s"
                }
                """.formatted(BENEFICIARY_ID);

        MvcResult result = mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-User-Id", DEPOSITOR_ID.toString())
                        .header("X-User-Role", "DEPOSITOR"))
                .andExpect(status().isCreated())
                .andReturn();

        String dealId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        UUID randomUser = UUID.randomUUID();

        mockMvc.perform(get("/api/deals/" + dealId)
                        .header("X-User-Id", randomUser.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}

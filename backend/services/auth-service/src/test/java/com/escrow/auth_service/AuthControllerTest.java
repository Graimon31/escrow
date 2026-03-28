package com.escrow.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginShouldReturnToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void adminZoneShouldRejectWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/admin-zone"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminZoneShouldRejectDepositorToken() throws Exception {
        var tokenResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"depositor","password":"depositor123"}
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = tokenResponse.split("\"accessToken\":\"")[1].split("\"", 2)[0];

        mockMvc.perform(get("/api/v1/auth/admin-zone")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }
}

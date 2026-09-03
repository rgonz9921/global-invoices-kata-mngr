package com.project_kata.global_invoices_kata_mngr.infrastructure;

import com.project_kata.global_invoices_kata_mngr.domain.model.TypeRoleUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    void seedOperador() {
        seedUser(OPERADOR_EMAIL, OPERADOR_PASSWORD, TypeRoleUser.OPERADOR);
    }

    private static String credentials(String email, String password) {
        return "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password);
    }

    @Test
    void loginWithValidCredentialsReturnsBearerToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(OPERADOR_EMAIL, OPERADOR_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordReturns401Json() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(OPERADOR_EMAIL, "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void loginWithBlankEmailReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields").isNotEmpty());
    }

    @Test
    void meWithoutTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void meWithValidTokenReturnsCurrentUser() throws Exception {
        String token = login(OPERADOR_EMAIL, OPERADOR_PASSWORD);

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(OPERADOR_EMAIL))
                .andExpect(jsonPath("$.role").value("OPERADOR"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void meWithTamperedTokenReturns401NotServerError() throws Exception {
        String token = login(OPERADOR_EMAIL, OPERADOR_PASSWORD);
        String tampered = token.substring(0, token.length() - 3) + "abc";

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void loginReturnsUsableToken() throws Exception {
        assertThat(login(OPERADOR_EMAIL, OPERADOR_PASSWORD)).isNotBlank();
    }
}

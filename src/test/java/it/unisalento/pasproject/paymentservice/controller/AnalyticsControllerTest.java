package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.TestSecurityConfig;
import it.unisalento.pasproject.paymentservice.dto.AdminAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.dto.UserAnalyticsDTO;
import it.unisalento.pasproject.paymentservice.service.AnalyticsService;
import it.unisalento.pasproject.paymentservice.service.UserCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(TestSecurityConfig.class)
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCheckService userCheckService;

    @MockBean
    private AnalyticsService analyticsService;

    private List<UserAnalyticsDTO> userAnalyticsDTOList;
    private List<AdminAnalyticsDTO> adminAnalyticsDTOList;

    @BeforeEach
    void setUp() {
        UserAnalyticsDTO userAnalyticsDTO = new UserAnalyticsDTO();
        userAnalyticsDTO.setUserEmail("test@example.com");
        userAnalyticsDTO.setMonth(5);
        userAnalyticsDTO.setYear(2023);
        userAnalyticsDTO.setDelayAmount(0.0F);

        userAnalyticsDTOList = List.of(userAnalyticsDTO);

        AdminAnalyticsDTO adminAnalyticsDTO = new AdminAnalyticsDTO();
        adminAnalyticsDTO.setMonth(2);
        adminAnalyticsDTO.setYear(2023);
        adminAnalyticsDTO.setDelayAmount(0.0F);

        adminAnalyticsDTOList = List.of(adminAnalyticsDTO);
    }

    @Test
    @WithMockUser(roles = "UTENTE")
    void getUserAnalytics_whenUserIsUtente_shouldReturnUserAnalytics() throws Exception {
        when(userCheckService.getCurrentUserEmail()).thenReturn("test@example.com");
        when(analyticsService.getUserAnalytics(eq("test@example.com"), any(LocalDateTime.class), any(LocalDateTime.class), eq("daily"))).thenReturn(userAnalyticsDTOList);

        mockMvc.perform(get("/api/payment/analytics/user")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "daily")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail", is("test@example.com")))
                .andExpect(jsonPath("$[0].month", is(5)))
                .andExpect(jsonPath("$[0].year", is(2023)))
                .andExpect(jsonPath("$[0].delayAmount", is(0.0)));
    }

    @Test
    @WithMockUser(roles = "UTENTE")
    void getUserAnalytics_whenRequestIsInvalid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/user")
                        .param("month", "13")
                        .param("year", "2023")
                        .param("granularity", "daily")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserAnalytics_whenUserIsNotUtente_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/user")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "daily")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserAnalytics_whenUserIsUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/user")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "daily")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminAnalytics_whenUserIsAdmin_shouldReturnAdminAnalytics() throws Exception {
        when(analyticsService.getAdminAnalytics(eq(""), any(LocalDateTime.class), any(LocalDateTime.class), eq("monthly"))).thenReturn(adminAnalyticsDTOList);

        mockMvc.perform(get("/api/payment/analytics/admin")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "monthly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month", is(2)))
                .andExpect(jsonPath("$[0].year", is(2023)))
                .andExpect(jsonPath("$[0].delayAmount", is(0.0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminAnalytics_whenRequestIsInvalid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/admin")
                        .param("month", "13")
                        .param("year", "2023")
                        .param("granularity", "monthly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAdminAnalytics_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/admin")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "monthly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAdminAnalytics_whenUserIsUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/payment/analytics/admin")
                        .param("month", "5")
                        .param("year", "2023")
                        .param("granularity", "monthly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

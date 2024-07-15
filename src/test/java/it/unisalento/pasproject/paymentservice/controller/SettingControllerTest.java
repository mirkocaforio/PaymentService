package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.TestSecurityConfig;
import it.unisalento.pasproject.paymentservice.dto.SettingDTO;
import it.unisalento.pasproject.paymentservice.service.CheckOutSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(SettingController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class SettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckOutSetting checkOutSetting;

    @InjectMocks
    private SettingController settingController;

    private SettingDTO settingDTO;

    @BeforeEach
    void setUp() {
        settingDTO = new SettingDTO();
        settingDTO.setId("1");
        settingDTO.setDelayInterest(0.0F);
        settingDTO.setMediumEnergyCost(0.0);
        settingDTO.setMediumResourceConsumption(0.0F);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSettings_whenUserIsAdmin_shouldReturnSettings() throws Exception {
        when(checkOutSetting.getSetting()).thenReturn(settingDTO);

        mockMvc.perform(get("/api/settings/payment/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.delayInterest", is(0.0)))
                .andExpect(jsonPath("$.mediumEnergyCost", is(0.0)))
                .andExpect(jsonPath("$.mediumResourceConsumption", is(0.0)));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getSettings_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/settings/payment/get"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setSettings_whenUserIsAdmin_shouldUpdateAndReturnSettings() throws Exception {
        when(checkOutSetting.updateSetting(any(SettingDTO.class))).thenReturn(settingDTO);

        mockMvc.perform(put("/api/settings/payment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"delayInterest\": 1.0,\"mediumEnergyCost\": 2.0, \"mediumResourceConsumption\": 3.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.delayInterest", is(0.0)))
                .andExpect(jsonPath("$.mediumEnergyCost", is(0.0)))
                .andExpect(jsonPath("$.mediumResourceConsumption", is(0.0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void setSettings_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/settings/payment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"delayInterest\": 1.0,\"mediumEnergyCost\": 2.0, \"mediumResourceConsumption\": 3.0}"))
                .andExpect(status().isForbidden());
    }
}

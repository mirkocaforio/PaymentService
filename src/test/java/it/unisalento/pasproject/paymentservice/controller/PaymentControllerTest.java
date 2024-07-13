package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.TestSecurityConfig;
import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private InvoiceDTO invoiceDTO;

    @BeforeEach
    void setUp() {
        invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setInvoiceTotalAmount(100.0F);
        invoiceDTO.setInvoiceStatus(InvoiceDTO.Status.PAID);
    }

    @Test
    @WithMockUser(roles = "UTENTE")
    void updateInvoice_whenUserIsUtente_shouldReturnUpdatedInvoice() throws Exception {
        when(paymentService.updateInvoice(anyString())).thenReturn(invoiceDTO);

        mockMvc.perform(put("/api/payment/pay/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber", is("12345")))
                .andExpect(jsonPath("$.invoiceTotalAmount", is(100.0)))
                .andExpect(jsonPath("$.invoiceStatus", is(InvoiceDTO.Status.PAID.toString())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateInvoice_whenUserIsNotUtente_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/payment/pay/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateInvoice_whenUserIsUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/api/payment/pay/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
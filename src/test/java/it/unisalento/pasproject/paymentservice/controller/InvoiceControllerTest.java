package it.unisalento.pasproject.paymentservice.controller;

import it.unisalento.pasproject.paymentservice.TestSecurityConfig;
import it.unisalento.pasproject.paymentservice.dto.InvoiceDTO;
import it.unisalento.pasproject.paymentservice.dto.InvoiceListDTO;
import it.unisalento.pasproject.paymentservice.service.PaymentQueryFilters;
import it.unisalento.pasproject.paymentservice.service.PaymentService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(TestSecurityConfig.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private InvoiceListDTO invoiceListDTO;

    @BeforeEach
    void setUp() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setInvoiceNumber("12345");
        invoiceDTO.setInvoiceTotalAmount(100.0F);
        invoiceDTO.setInvoiceStatus(InvoiceDTO.Status.PAID);

        invoiceListDTO = new InvoiceListDTO();
        invoiceListDTO.setInvoicesList(List.of(invoiceDTO));
    }

    @Test
    @WithMockUser(roles = "UTENTE")
    void getAllInvoice_whenUserIsUtente_shouldReturnAllInvoices() throws Exception {
        when(paymentService.getAllInvoices()).thenReturn(invoiceListDTO);

        mockMvc.perform(get("/api/invoice/find/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoicesList", hasSize(1)))
                .andExpect(jsonPath("$.invoicesList[0].invoiceNumber", is("12345")))
                .andExpect(jsonPath("$.invoicesList[0].invoiceTotalAmount", is(100.0)))
                .andExpect(jsonPath("$.invoicesList[0].invoiceStatus", is(InvoiceDTO.Status.PAID.toString())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllInvoice_whenUserIsNotUtente_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/invoice/find/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllInvoice_whenUserIsUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/invoice/find/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "UTENTE")
    void getInvoiceByFilters_whenUserIsUtente_shouldReturnFilteredInvoices() throws Exception {
        PaymentQueryFilters filters = new PaymentQueryFilters();
        when(paymentService.getInvoicesByFilters(any(PaymentQueryFilters.class))).thenReturn(invoiceListDTO);

        mockMvc.perform(get("/api/invoice/find")
                        .param("someFilter", "someValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoicesList", hasSize(1)))
                .andExpect(jsonPath("$.invoicesList[0].invoiceNumber", is("12345")))
                .andExpect(jsonPath("$.invoicesList[0].invoiceTotalAmount", is(100.0)))
                .andExpect(jsonPath("$.invoicesList[0].invoiceStatus", is(InvoiceDTO.Status.PAID.toString())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getInvoiceByFilters_whenUserIsNotUtente_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/invoice/find")
                        .param("someFilter", "someValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getInvoiceByFilters_whenUserIsUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/invoice/find")
                        .param("someFilter", "someValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

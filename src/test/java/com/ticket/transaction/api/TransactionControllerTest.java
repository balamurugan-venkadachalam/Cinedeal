package com.ticket.transaction.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.transaction.domain.TicketCalculation;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.domain.TransactionCalculation;
import com.ticket.transaction.mapper.TransactionMapper;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.model.TransactionRequest;
import com.ticket.transaction.model.TransactionResponse;
import com.ticket.transaction.service.TicketPricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketPricingService ticketPricingService;

    @MockBean
    private TransactionMapper transactionMapper;

    @Test
    void calculatePricing_shouldReturnTransactionRequest_whenValidRequest() throws Exception {
        //GIVEN
        TransactionRequest request = TransactionRequest.builder()
                .transactionId(1L)
                .customers(getCustomers())
                .build();
        TransactionCalculation calculation = createMockCalculation(1L, 50.00);
        TransactionResponse response = createMockResponse(1L, 50.00);

        when(ticketPricingService.calculatePrice(anyLong(), anyList())).thenReturn(calculation);
        when(transactionMapper.toResponse(any())).thenReturn(response);




        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.totalCost").value(50.00));
    }

    @Test
    void calculatePricing_shouldReturnBadRequest_whenCustomersMissing() throws Exception {
        //GIVEN
        TransactionRequest request =  TransactionRequest.builder().transactionId(2L).build();

        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("customers: size must be between 1 and 50"));

    }

    @Test
    void calculatePricing_shouldReturnBadRequest_whenCustomersIsEmpty() throws Exception {
        //GIVEN
        TransactionRequest request =  TransactionRequest.builder()
                .transactionId(2L)
                .customers(List.of())
                .build();

        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("customers: size must be between 1 and 50"));

    }

    @Test
    void calculatePricing_shouldReturnBadRequest_whenCustomerAgeMissing() throws Exception {
        //GIVEN
        Customer customer = Customer.builder()
                .name("Alex K")
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .transactionId(1L)
                .customers(List.of(customer))
                .build();

        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("customers[0].age: must not be null"));

    }

    @Test
    void calculatePricing_shouldReturnBadRequest_whenTransactionIdMissing() throws Exception {
        //GIVEN
        TransactionRequest request = TransactionRequest.builder()
                .customers(getCustomers())
                .build();

        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("transactionId: must not be null"));

    }



    private List<Customer> getCustomers() {
        return List.of(
                Customer.builder().name("Alex K").age(25).build(),
                Customer.builder().name("Jone David").age(30).build()
        );
    }


    private TransactionCalculation createMockCalculation(Long id, double totalCost) {
        return TransactionCalculation.builder()
                .transactionId(id)
                .ticketCalculation(TicketCalculation.builder()
                        .ticketType(TicketType.ADULT)
                        .quantity(2)
                        .totalCost(totalCost)
                        .discountApplied(false)
                        .build())
                .totalCost(totalCost)
                .build();
    }

    private TransactionResponse createMockResponse(Long id, double totalCost) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(id);
        response.setTotalCost(totalCost);
        return response;
    }

}


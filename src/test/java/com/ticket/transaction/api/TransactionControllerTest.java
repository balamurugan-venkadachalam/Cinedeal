package com.ticket.transaction.api;

import com.ticket.transaction.exception.GlobalExceptionHandler;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.model.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;


@WebMvcTest(TransactionController.class)
@Import({GlobalExceptionHandler.class})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculatePricing_shouldReturnTransactionRequest_whenValidRequest() throws Exception {
        //GIVEN
        TransactionRequest request = TransactionRequest.builder()
                .transactionId(1)
                .customers(gerCustomers())
                .build();


        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.tickets").exists())
                .andExpect(jsonPath("$.totalCost").exists());
    }


    @Test
    void calculatePricing_shouldReturnBadRequest_whenCustomersMissing() throws Exception {
        //GIVEN
        TransactionRequest request =  TransactionRequest.builder().transactionId(2).build();

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
                .transactionId(2)
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
                .transactionId(1)
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
                .customers(gerCustomers())
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


    private static List<Customer> gerCustomers() {

        Customer customer1 = Customer.builder()
                .name("Alex K")
                .age(25)
                .build();

        Customer customer2 = Customer.builder()
                .name("Jone David")
                .age(30)
                .build();

        return List.of(customer1, customer2);
    }

}


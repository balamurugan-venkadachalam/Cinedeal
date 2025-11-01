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
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId(1);

        request.setCustomers(gerCustomers());

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
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId(1);

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
    void calculatePricing_shouldReturnBadRequest_whenTransactionIdMissing() throws Exception {
        //GIVEN
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId(1);
        Customer customer1 = new Customer();
        customer1.setName("Alex K");
        request.setCustomers(List.of(customer1));

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
    void calculatePricing_shouldReturnBadRequest_whenCustomerAgeMissing() throws Exception {
        //GIVEN
        TransactionRequest request = new TransactionRequest();
        request.setCustomers(gerCustomers());
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
        Customer customer1 = new Customer();
        customer1.setName("Alex K");
        customer1.setAge(30);

        Customer customer2 = new Customer();
        customer2.setName("Jone David");
        customer2.setAge(25);

        return List.of(customer1, customer2);
    }

}


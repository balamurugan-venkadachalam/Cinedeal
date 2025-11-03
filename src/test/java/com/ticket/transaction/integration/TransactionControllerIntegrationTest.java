package com.ticket.transaction.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.model.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void calculatePricing_shouldReturnTransactionRequest_whenValidRequest() throws Exception {
        //GIVEN
        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer("John Smith", 70));
        customers.add(createCustomer("Jane Doe", 5));
        customers.add(createCustomer("Bob Doe", 6));

        TransactionRequest request = TransactionRequest.builder()
                .transactionId(1L)
                .customers(customers)
                .build();


        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"transactionId\":1,\"tickets\":[{\"ticketType\":\"Children\",\"quantity\":2,\"totalCost\":10.00},{\"ticketType\":\"Senior\",\"quantity\":1,\"totalCost\":17.50}],\"totalCost\":27.50}", false));
    }



    private static Customer createCustomer(String name, int age) {
         return Customer.builder()
                .name(name)
                .age(age)
                .build();
    }

}


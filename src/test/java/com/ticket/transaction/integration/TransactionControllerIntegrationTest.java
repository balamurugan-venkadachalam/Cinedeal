package com.ticket.transaction.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.model.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Customer createCustomer(String name, int age) {
        return Customer.builder()
                .name(name)
                .age(age)
                .build();
    }

    @Test
    void testCalculateTicketPrices_shouldApplyBulkDiscountForChildrenTickets_WhenThreeOrMoreChildren() throws Exception {
        //GIVEN
        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer("Senior 1", 70));
        customers.add(createCustomer("Child 1", 5));
        customers.add(createCustomer("Child 2", 6));

        TransactionRequest request = TransactionRequest.builder()
                .transactionId(1L)
                .customers(customers)
                .build();


        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.totalCost").value(27.50))
                .andExpect(jsonPath("$.tickets.length()").value(2))
                .andExpect(jsonPath("$.tickets[0].ticketType").value("Children"))
                .andExpect(jsonPath("$.tickets[0].quantity").value(2))
                .andExpect(jsonPath("$.tickets[0].totalCost").value(10.00))
                .andExpect(jsonPath("$.tickets[1].ticketType").value("Senior"))
                .andExpect(jsonPath("$.tickets[1].quantity").value(1))
                .andExpect(jsonPath("$.tickets[1].totalCost").value(17.50));
    }

    @Test
    void testCalculateTicketPrices_shouldApplyBulkDiscountForMixedTicketTypes_WhenMultipleChildrenPresent() throws Exception {
        //GIVEN
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId(2L);

        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer("Adult 1", 36));
        customers.add(createCustomer("Child 1", 3));
        customers.add(createCustomer("Child 2", 8));
        customers.add(createCustomer("Child 3", 9));
        customers.add(createCustomer("Teen 1", 17));
        request.setCustomers(customers);

        //WHEN & THEN
        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactionId").value(2))
                .andExpect(jsonPath("$.totalCost").value(48.25))
                .andExpect(jsonPath("$.tickets.length()").value(3))
                .andExpect(jsonPath("$.tickets[0].ticketType").value("Adult"))
                .andExpect(jsonPath("$.tickets[0].quantity").value(1))
                .andExpect(jsonPath("$.tickets[0].totalCost").value(25.00))
                .andExpect(jsonPath("$.tickets[1].ticketType").value("Children"))
                .andExpect(jsonPath("$.tickets[1].quantity").value(3))
                .andExpect(jsonPath("$.tickets[1].totalCost").value(11.25))
                .andExpect(jsonPath("$.tickets[2].ticketType").value("Teen"))
                .andExpect(jsonPath("$.tickets[2].quantity").value(1))
                .andExpect(jsonPath("$.tickets[2].totalCost").value(12.00));

    }

    @Test
    void testCalculateTicketPrices_shouldCalculateMixedTicketTypes_WithAdultSeniorTeenAndChildren() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionId(3L);

        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer("Adult 1", 36));
        customers.add(createCustomer("Senior 1", 95));
        customers.add(createCustomer("Teen 1", 15));
        customers.add(createCustomer("Child 1", 10));
        request.setCustomers(customers);

        mockMvc.perform(post("/api/v1/transactions/calculate-pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.transactionId").value(3))
                .andExpect(jsonPath("$.totalCost").value(59.50))
                .andExpect(jsonPath("$.tickets.length()").value(4))
                .andExpect(jsonPath("$.tickets[0].ticketType").value("Adult"))
                .andExpect(jsonPath("$.tickets[0].quantity").value(1))
                .andExpect(jsonPath("$.tickets[0].totalCost").value(25.00))
                .andExpect(jsonPath("$.tickets[1].ticketType").value("Children"))
                .andExpect(jsonPath("$.tickets[1].quantity").value(1))
                .andExpect(jsonPath("$.tickets[1].totalCost").value(5.00))
                .andExpect(jsonPath("$.tickets[2].ticketType").value("Senior"))
                .andExpect(jsonPath("$.tickets[2].quantity").value(1))
                .andExpect(jsonPath("$.tickets[2].totalCost").value(17.50))
                .andExpect(jsonPath("$.tickets[3].ticketType").value("Teen"))
                .andExpect(jsonPath("$.tickets[3].quantity").value(1))
                .andExpect(jsonPath("$.tickets[3].totalCost").value(12.00));

    }


}


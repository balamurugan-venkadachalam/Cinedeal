package com.ticket.transaction.api;

import com.ticket.transaction.model.TransactionRequest;
import com.ticket.transaction.model.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class TransactionController implements TransactionsApi {

    public ResponseEntity<TransactionResponse> calculatePricing(@Valid  TransactionRequest transactionRequest) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transactionRequest.getTransactionId());
        response.setTickets(Collections.emptyList());
        response.setTotalCost(10.00);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
}

package com.ticket.transaction.api;

import com.ticket.transaction.mapper.TransactionMapper;
import com.ticket.transaction.model.TransactionRequest;
import com.ticket.transaction.model.TransactionResponse;
import com.ticket.transaction.service.TicketPricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionsApi {

    private final TicketPricingService ticketPricingService;
    private final TransactionMapper transactionMapper;

    public ResponseEntity<TransactionResponse> calculatePricing(@Valid TransactionRequest transactionRequest) {
        var transactionCalculation = ticketPricingService.calculatePrice(transactionRequest.getTransactionId(), transactionRequest.getCustomers());

        return ResponseEntity.ok(transactionMapper.toResponse(transactionCalculation));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
}

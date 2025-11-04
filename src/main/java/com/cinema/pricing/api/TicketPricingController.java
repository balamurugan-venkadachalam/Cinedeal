package com.cinema.pricing.api;

import com.cinema.pricing.mapper.TransactionMapper;
import com.cinema.pricing.model.TransactionRequest;
import com.cinema.pricing.model.TransactionResponse;
import com.cinema.pricing.service.TicketPricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketPricingController implements TransactionsApi {

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

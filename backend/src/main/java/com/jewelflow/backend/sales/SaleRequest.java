package com.jewelflow.backend.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleRequest {

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Item id is required")
    private Long itemId;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String notes;
}

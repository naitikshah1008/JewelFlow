package com.jewelflow.backend.customer;

import com.jewelflow.backend.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping
    public List<Customer> getAllCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return customerService.getAllCustomers(keyword, includeArchived);
    }

    @GetMapping("/page")
    public PageResponse<Customer> getCustomersPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(defaultValue = "false") boolean includeArchived
    ) {
        return customerService.getCustomersPage(keyword, includeArchived, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request
    ) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PostMapping("/{id}/restore")
    public Customer restoreCustomer(@PathVariable Long id) {
        return customerService.restoreCustomer(id);
    }
}

package com.jewelflow.backend.customer;

import com.jewelflow.backend.common.PageRequestFactory;
import com.jewelflow.backend.common.PageResponse;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
            "createdAt", "createdAt",
            "fullName", "fullName",
            "phoneNumber", "phoneNumber",
            "city", "city",
            "updatedAt", "updatedAt"
    );

    private final CustomerRepository customerRepository;

    public Customer createCustomer(CustomerRequest request) {
        ensurePhoneNumberIsAvailable(request.getPhoneNumber(), null);
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .notes(request.getNotes())
                .build();
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return getAllCustomers(null, false);
    }

    public List<Customer> getAllCustomers(String keyword) {
        return getAllCustomers(keyword, false);
    }

    public List<Customer> getAllCustomers(String keyword, boolean includeArchived) {
        return customerRepository.searchCustomers(normalizeKeyword(keyword), includeArchived);
    }

    public PageResponse<Customer> getCustomersPage(
            String keyword,
            boolean includeArchived,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    ) {
        return PageResponse.from(customerRepository.searchCustomersPage(
                normalizeKeyword(keyword),
                includeArchived,
                PageRequestFactory.create(page, size, sortBy, direction, ALLOWED_SORTS, "createdAt")
        ));
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getCustomerById(id);
        ensurePhoneNumberIsAvailable(request.getPhoneNumber(), id);
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPostalCode(request.getPostalCode());
        customer.setNotes(request.getNotes());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setArchived(true);
        customer.setArchivedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    public Customer restoreCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setArchived(false);
        customer.setArchivedAt(null);
        return customerRepository.save(customer);
    }

    private void ensurePhoneNumberIsAvailable(String phoneNumber, Long currentCustomerId) {
        customerRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(existingCustomer -> {
                    boolean sameCustomer = currentCustomerId != null
                            && existingCustomer.getId().equals(currentCustomerId);
                    if (!sameCustomer) {
                        throw new IllegalArgumentException("Customer already exists with phone number: " + phoneNumber);
                    }
                });
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null || keyword.isBlank() ? null : "%" + keyword.trim().toLowerCase() + "%";
    }
}

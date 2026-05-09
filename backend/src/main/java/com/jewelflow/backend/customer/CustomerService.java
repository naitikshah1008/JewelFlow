package com.jewelflow.backend.customer;

import com.jewelflow.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

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
        return getAllCustomers(null);
    }

    public List<Customer> getAllCustomers(String keyword) {
        return customerRepository.searchCustomers(normalizeKeyword(keyword));
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
        customerRepository.delete(customer);
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

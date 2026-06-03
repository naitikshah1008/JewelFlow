package com.jewelflow.backend.customer;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final CustomerService customerService = new CustomerService(customerRepository);

    @Test
    void createCustomerRejectsDuplicatePhoneNumber() {
        Customer existingCustomer = Customer.builder()
                .id(1L)
                .fullName("Existing Customer")
                .phoneNumber("9876543210")
                .build();
        when(customerRepository.findByPhoneNumber("9876543210")).thenReturn(Optional.of(existingCustomer));

        CustomerRequest request = new CustomerRequest();
        request.setFullName("New Customer");
        request.setPhoneNumber("9876543210");
        request.setEmail("new@example.com");

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer already exists with phone number: 9876543210");
    }

    @Test
    void deleteCustomerArchivesInsteadOfDeleting() {
        Customer customer = Customer.builder()
                .id(1L)
                .fullName("Demo Customer")
                .phoneNumber("9876543210")
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        customerService.deleteCustomer(1L);

        assertThat(customer.isArchived()).isTrue();
        assertThat(customer.getArchivedAt()).isNotNull();
        verify(customerRepository).save(customer);
        verify(customerRepository, never()).delete(customer);
    }
}

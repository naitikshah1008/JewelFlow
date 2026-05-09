package com.jewelflow.backend.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    @Query("""
            select customer
            from Customer customer
            where :keyword is null
               or lower(customer.fullName) like :keyword
               or lower(customer.phoneNumber) like :keyword
               or lower(customer.email) like :keyword
            order by customer.createdAt desc
            """)
    List<Customer> searchCustomers(@Param("keyword") String keyword);
}

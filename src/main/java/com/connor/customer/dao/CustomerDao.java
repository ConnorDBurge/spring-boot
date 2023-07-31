package com.connor.customer.dao;

import com.connor.customer.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existCustomerWithEmail(String email);
    boolean existCustomerWithId(Long id);
    void deleteCustomer(Long id);
    void updateCustomer(Customer customer);
}

package com.connor.customer.dao;

import com.connor.AbstractTestContainer;
import com.connor.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerJPADataAccessServiceTest extends AbstractTestContainer {

    private CustomerJPADataAccessService underTest;
    @Mock private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        Long id = 1L;
        underTest.selectCustomerById(id);
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 99)
        );
        underTest.insertCustomer(customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void existCustomerWithEmail() {
        String email = faker.internet().safeEmailAddress();
        underTest.existCustomerWithEmail(email);
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existCustomerWithId() {
        Long id = 1L;
        underTest.existCustomerWithId(id);
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomer() {
        Long id = 1L;
        underTest.deleteCustomer(id);
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                faker.number().numberBetween(18, 99)
        );
        underTest.updateCustomer(customer);
        verify(customerRepository).save(customer);
    }
}

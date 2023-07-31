package com.connor.customer.dao;

import com.connor.AbstractTestContainer;
import com.connor.customer.dao.CustomerJBDCDataAccessService;
import com.connor.customer.model.Customer;
import com.connor.customer.utils.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJBDCDataAccessServiceTest extends AbstractTestContainer {

    private CustomerJBDCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private String email;
    private Customer customer;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJBDCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
        email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        customer = new Customer(
                faker.name().fullName(),
                email,
                faker.number().numberBetween(18, 99)
        );
        underTest.insertCustomer(customer);
    }

    @Test
    void selectAllCustomers() {
        List<Customer> actual = underTest.selectAllCustomers();
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectByCustomerId() {
        Long id = -1L;
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actualEmail = underTest.existCustomerWithEmail(email);
        boolean actualId = underTest.existCustomerWithId(id);

        assertThat(actualEmail).isTrue();
        assertThat(actualId).isTrue();
    }

    @Test
    void existNoCustomerWithEmail() {
        boolean actual = underTest.existCustomerWithEmail("does-not-exist@email.com");
        assertThat(actual).isFalse();
    }

    @Test
    void existCustomerWithEmail() {
        boolean actual = underTest.existCustomerWithEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void existCustomerWithId() {
        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = underTest.existCustomerWithId(id);
        assertThat(actual).isTrue();
    }

    @Test
    void existNoCustomerWithId() {
        boolean actual = underTest.existCustomerWithId(-1L);
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomer() {
        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        boolean actual = underTest.existCustomerWithId(id);
        assertThat(actual).isTrue();
        underTest.deleteCustomer(id);
        actual = underTest.existCustomerWithId(id);
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomer() {
        customer.setName("New Guy");
        customer.setEmail("new-email@email.com");
        underTest.updateCustomer(customer);

        Optional<Customer> actual = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst();

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
           assertThat(actual.get().getName()).isNotEqualTo(customer.getName());
           assertThat(actual.get().getEmail()).isNotEqualTo(customer.getEmail());
           assertThat(actual.get().getAge()).isEqualTo(customer.getAge());
        });
    }
}

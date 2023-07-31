package com.connor.customer.dao;

import com.connor.AbstractTestContainer;
import com.connor.customer.dao.CustomerRepository;
import com.connor.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainer {

    @Autowired
    private CustomerRepository underTest;
    private String email;

    @BeforeEach
    void setUp() {
        email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                faker.number().numberBetween(18, 99)
        );
        underTest.save(customer);
    }

    @Test
    void existsCustomerByEmail() {
        boolean actual = underTest.existsCustomerByEmail(email);
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerById() {
        Long id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = underTest.existsCustomerById(id);
        assertThat(actual).isTrue();
    }
}

package com.connor;

import com.connor.customer.model.Customer;
import com.connor.customer.dao.CustomerRepository;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Name name =  faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            List<Customer> customers = List.of(
                    new Customer(
                            firstName + " " + lastName,
                            firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com",
                            faker.number().numberBetween(18, 80)
                    )
            );
            customerRepository.saveAll(customers);
        };
    }
}

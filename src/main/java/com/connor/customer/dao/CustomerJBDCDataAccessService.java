package com.connor.customer.dao;

import com.connor.customer.model.Customer;
import com.connor.customer.utils.CustomerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJBDCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJBDCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;
        return jdbcTemplate.query(sql, (customerRowMapper));
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, (customerRowMapper), id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existCustomerWithEmail(String email) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, (customerRowMapper), email)
                .stream()
                .findFirst()
                .isPresent();
    }

    @Override
    public boolean existCustomerWithId(Long id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, (customerRowMapper), id)
                .stream()
                .findFirst()
                .isPresent();
    }

    @Override
    public void deleteCustomer(Long id) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = """
                UPDATE public.customer
                SET name  = ?,
                    email = ?,
                    age   = ?
                WHERE id = ?;
                """;
        jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getId());
    }
}

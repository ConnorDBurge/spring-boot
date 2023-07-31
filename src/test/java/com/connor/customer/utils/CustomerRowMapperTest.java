package com.connor.customer.utils;

import com.connor.customer.model.Customer;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("email")).thenReturn("test@email.com");
        when(resultSet.getString("name")).thenReturn("test");
        when(resultSet.getInt("age")).thenReturn(27);

        Customer actual = customerRowMapper.mapRow(resultSet, 1);
        Customer expected = new Customer(1L, "test", "test@email.com", 27);

        assert actual != null;
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
    }
}

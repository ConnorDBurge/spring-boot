package com.connor.customer.business;

import com.connor.customer.dao.CustomerDao;
import com.connor.customer.model.Customer;
import com.connor.customer.payload.CustomerRegistrationRequest;
import com.connor.exception.DuplicateResourceException;
import com.connor.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 27);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Customer actual = underTest.getCustomerById(id);

        assertThat(actual).isEqualTo(customer);
        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        Long id = 1L;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer could not be found");
        verify(customerDao).selectCustomerById(id);
    }

    @Test
    void addCustomer() {
        String email = "alex@gmail.com";
        when(customerDao.existCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 27);
        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer captorValue = customerArgumentCaptor.getValue();
        assertThat(captorValue.getId()).isNull();
        assertThat(captorValue.getName()).isEqualTo(request.name());
        assertThat(captorValue.getEmail()).isEqualTo(request.email());
        assertThat(captorValue.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistAddCustomer() {
        String email = "alex@gmail.com";
        when(customerDao.existCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 27);
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email already exist");
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        Long id = 1L;
        when(customerDao.existCustomerWithId(id)).thenReturn(true);
        underTest.deleteCustomerById(id);
        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void willThrowWhenNoDeleteCustomerById() {
        Long id = 1L;
        when(customerDao.existCustomerWithId(id)).thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer could not be found");
    }

    @Test
    void updateCustomer() {
    }
}

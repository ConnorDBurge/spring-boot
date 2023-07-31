package com.connor.customer.business;

import com.connor.customer.dao.CustomerDao;
import com.connor.customer.model.Customer;
import com.connor.customer.payload.CustomerRegistrationRequest;
import com.connor.customer.payload.CustomerUpdateRequest;
import com.connor.exception.DuplicateResourceException;
import com.connor.exception.RequestValidationException;
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
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", "alex@gmail.com", 27);
        when(customerDao.existCustomerWithEmail(request.email())).thenReturn(false);
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
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", "alex@gmail.com", 27);
        when(customerDao.existCustomerWithEmail(request.email())).thenReturn(true);

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
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 27);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Connor", "connor@gmail.com", 28);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existCustomerWithEmail(request.email())).thenReturn(false);
        underTest.updateCustomer(id, request);

        assertThat(customer.getName()).isEqualTo(request.name());
        assertThat(customer.getEmail()).isEqualTo(request.email());
        assertThat(customer.getAge()).isEqualTo(request.age());
        verify(customerDao).updateCustomer(customer);
    }

    @Test
    void onlyUpdateCustomerName() {
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 27);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Connor", null, null);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        underTest.updateCustomer(id, request);

        assertThat(customer.getName()).isEqualTo(request.name());
        verify(customerDao).updateCustomer(customer);
    }

    @Test
    void willThrowNoChangesUpdateCustomer() {
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 27);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Alex", "alex@gmail.com", 27);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no changes found");
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowDuplicateEmailUpdateCustomer() {
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 27);
        CustomerUpdateRequest request = new CustomerUpdateRequest("Connor", "connor@gmail.com", 28);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existCustomerWithEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");
        verify(customerDao, never()).updateCustomer(any());
    }
}

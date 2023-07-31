package com.connor.customer.business;

import com.connor.customer.model.Customer;
import com.connor.customer.payload.CustomerRegistrationRequest;
import com.connor.customer.payload.CustomerUpdateRequest;
import com.connor.customer.dao.CustomerDao;
import com.connor.exception.DuplicateResourceException;
import com.connor.exception.RequestValidationException;
import com.connor.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Long id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new ResourceNotFoundException("Customer could not be found"));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Customer with email already exist");
        }
        customerDao.insertCustomer(new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        ));
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDao.existCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("Customer could not be found");
        }
        customerDao.deleteCustomer(customerId);
    }

    public void updateCustomer(Long customerId, CustomerUpdateRequest requestBody) {
        Customer customer = getCustomerById(customerId);

        boolean changes = false;

        if (requestBody.name() != null && !customer.getName().equals(requestBody.name())) {
            customer.setName(requestBody.name());
            changes = true;
        }

        if (requestBody.age() != null && !customer.getAge().equals(requestBody.age())) {
            customer.setAge(requestBody.age());
            changes = true;
        }

        if (requestBody.email() != null && !customer.getEmail().equals(requestBody.email())) {
            if (customerDao.existCustomerWithEmail(requestBody.email())) {
                throw new DuplicateResourceException("Email is already taken");
            }

            customer.setEmail(requestBody.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no changes found");
        }

        customerDao.updateCustomer(customer);
    }
}

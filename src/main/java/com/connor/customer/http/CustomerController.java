package com.connor.customer.http;

import com.connor.customer.model.Customer;
import com.connor.customer.payload.CustomerRegistrationRequest;
import com.connor.customer.payload.CustomerUpdateRequest;
import com.connor.customer.business.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable Long customerId) {
        return customerService.getCustomerById(customerId);
    }

    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("/{customerId}")
    public void updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerUpdateRequest requestBody) {
        customerService.updateCustomer(customerId, requestBody);
    }
}

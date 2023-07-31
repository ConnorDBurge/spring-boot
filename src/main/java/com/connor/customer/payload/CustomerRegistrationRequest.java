package com.connor.customer.payload;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}

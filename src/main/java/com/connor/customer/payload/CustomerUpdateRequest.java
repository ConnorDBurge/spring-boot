package com.connor.customer.payload;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}

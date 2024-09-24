package com.isi.ecommerce.customer;

public record CustomerResponse(
        Integer id,
        String firstname,
        String lastname,
        String email,
        String address
) {
}

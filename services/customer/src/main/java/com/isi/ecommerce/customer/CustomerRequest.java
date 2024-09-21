package com.isi.ecommerce.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        Integer id,
        @NotNull(message = "Le prénom du client est obligatoire")
        String firstname,
        @NotNull(message = "Le nom du client est obligatoire")
        String lastname,
        @NotNull(message = "L'e-mail du client est requis")
        @Email(message = "L'e-mail du client n'est pas une adresse e-mail valide")
        String email,
        @NotNull(message = "L'adresse du client est obligatoire")
        String address
) {

}


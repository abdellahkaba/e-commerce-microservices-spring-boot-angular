package com.isi.ecommerce.customer;


import com.isi.ecommerce.exception.CustomerNotFoundException;
import com.isi.ecommerce.exception.EmailConflictException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;


    public Integer createCustomer(CustomerRequest request) {
        if (repository.findByEmail(request.email()).isPresent()){
            throw new EmailConflictException("L'email existe deja !");
        }
        var customer = this.repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public List<CustomerResponse> findAllCustomers() {
        return  this.repository.findAll()
                .stream()
                .map(this.mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public CustomerResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Aucun client trouvé avec le fourni ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void updateCustomer(CustomerRequest request) {
        var customer = this.repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID: %s", request.id())
                ));
        mergeCustomer(customer, request);
        this.repository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.email()) &&
                !request.email().equals(customer.getEmail()) &&
                repository.findByEmail(request.email()).isPresent()) {
            throw new EmailConflictException("L'email existe déjà");
        }
        if (StringUtils.isNotBlank(request.firstname())) {
            customer.setFirstname(request.firstname());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
    }


}

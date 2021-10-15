package dev.emmanuel.banking.customer.service;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.CreateCustomerRequest;
import dev.emmanuel.banking.customer.exception.CustomerAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateCustomerService {

  private final CustomerRepository customerRepository;

  public Mono<Customer> create(CreateCustomerRequest request) {
    return this.customerRepository
      .findByName(request.name())
      .flatMap(this::customerAlreadyExistent)
      .switchIfEmpty(saveNewCustomer(request));
  }

  private Mono<Customer> saveNewCustomer(CreateCustomerRequest request) {
    return Mono.defer(() -> {
      Customer newCustomer = new Customer(null, request.name(), State.CREATED);
      return customerRepository.save(newCustomer);
    });
  }

  private Mono<Customer> customerAlreadyExistent(Customer existent) {
    return Mono.error(new CustomerAlreadyExistsException("Customer already exists", existent.name()));
  }

}

package dev.emmanuel.banking.customer.service;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.ChangeCustomerStateRequest;
import dev.emmanuel.banking.customer.event.CustomerEventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChangeCustomerStateService {

  private final CustomerEventBus customerEventBus;
  private final CustomerRepository customerRepository;

  public Mono<Customer> changeState(ChangeCustomerStateRequest request) {
    return customerRepository
      .findById(request.customerId())
      .map(customer -> customer.withState(request.newState()))
      .flatMap(customerRepository::save)
      .doOnSuccess(this::publishCustomerStateChangedEvent);
  }

  private void publishCustomerStateChangedEvent(Customer customer) {
    customerEventBus.customerChanged(customer);
  }

}

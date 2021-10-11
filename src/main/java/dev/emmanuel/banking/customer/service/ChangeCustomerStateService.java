package dev.emmanuel.banking.customer.service;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.event.CustomerChangedEventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChangeCustomerStateService {

  private final CustomerChangedEventBus customerChangedEventBus;
  private final CustomerRepository customerRepository;

  public Mono<Customer> changeState(int id, State newState) {
    return customerRepository
      .findById(id)
      .map(customer -> customer.withState(newState))
      .flatMap(customerRepository::save)
      .doOnSuccess(this::publishCustomerStateChangedEvent);
  }

  private void publishCustomerStateChangedEvent(Customer customer) {
    customerChangedEventBus.publishCustomerChange(customer);
  }

}

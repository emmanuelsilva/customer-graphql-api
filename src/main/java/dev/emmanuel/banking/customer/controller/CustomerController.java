package dev.emmanuel.banking.customer.controller;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.ChangeCustomerStateRequest;
import dev.emmanuel.banking.customer.dto.request.CreateCustomerRequest;
import dev.emmanuel.banking.customer.event.CustomerEventBus;
import dev.emmanuel.banking.customer.service.ChangeCustomerStateService;
import dev.emmanuel.banking.customer.service.CreateCustomerService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CustomerController  {

  private final CreateCustomerService createCustomerService;
  private final ChangeCustomerStateService changeCustomerStateService;
  private final CustomerEventBus customerEventBus;
  private final CustomerRepository customerRepository;

  @QueryMapping("customers")
  public Flux<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  @QueryMapping("findCustomerByName")
  public Mono<Customer> findByName(@Argument("name") String name) {
    return customerRepository.findByName(name);
  }

  @QueryMapping("findCustomersByState")
  public Flux<Customer> findAllByState(@Argument("state") State state) {
    return customerRepository.findAllByState(state);
  }

  @MutationMapping("addCustomer")
  public Mono<Customer> addCustomer(@Argument("request") CreateCustomerRequest request) {
    return createCustomerService.create(request);
  }

  @MutationMapping("changeState")
  public Mono<Customer> changeState(@Argument("request") ChangeCustomerStateRequest request) {
    return changeCustomerStateService.changeState(request);
  }

  @SubscriptionMapping("customerChangeEvents")
  public Publisher<Customer> subscribeToChangeStateEvents(@Argument("customerId") int customerId) {
    return customerEventBus.subscribeFor(customerId);
  }
}

package dev.emmanuel.banking.customer.service;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.CreateCustomerRequest;
import dev.emmanuel.banking.customer.exception.CustomerAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCustomerServiceTest {

  private CustomerRepository customerRepository;
  private CreateCustomerService createCustomerService;

  @BeforeEach
  void setUp() {
    customerRepository = mock(CustomerRepository.class);
    createCustomerService = new CreateCustomerService(customerRepository);
  }

  @Test
  void shouldRejectDuplicateName() {
    var name = "customer-name";
    var request = new CreateCustomerRequest(name);

    var customerSameNameOnDatabase = new Customer(1, name, State.CREATED);
    when(customerRepository.findByName(name)).thenReturn(Mono.just(customerSameNameOnDatabase));

    StepVerifier
      .create(createCustomerService.create(request))
      .expectError(CustomerAlreadyExistsException.class)
      .verify();
  }

  @Test
  void shouldSaveNewCustomer() {
    var name = "customer-name";
    var request = new CreateCustomerRequest(name);
    var savedCustomer = new Customer(1, name, State.CREATED);

    when(customerRepository.findByName(name)).thenReturn(Mono.empty());
    when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(savedCustomer));

    StepVerifier
      .create(createCustomerService.create(request))
      .expectNext(savedCustomer)
      .verifyComplete();

    verify(customerRepository, times(1)).save(any(Customer.class));
  }
}
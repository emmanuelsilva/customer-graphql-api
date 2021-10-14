package dev.emmanuel.banking.customer.service;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.ChangeCustomerStateRequest;
import dev.emmanuel.banking.customer.event.CustomerEventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static dev.emmanuel.banking.helper.MockitoAnswerPipeline.will;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

class ChangeCustomerStateServiceTest {

  private CustomerEventBus customerEventBus;
  private CustomerRepository customerRepository;
  private ChangeCustomerStateService changeCustomerStateService;

  @BeforeEach
  void setUp() {
    customerEventBus = mock(CustomerEventBus.class);
    customerRepository = mock(CustomerRepository.class);
    changeCustomerStateService = new ChangeCustomerStateService(customerEventBus, customerRepository);
  }

  @Test
  void shouldChangeStateOnRepository() {
    var customerId = 1;
    var request = new ChangeCustomerStateRequest(customerId, State.ACTIVATED);

    var customerOnDatabase = new Customer(customerId, "name", State.CREATED);
    when(customerRepository.findById(customerId)).thenReturn(Mono.just(customerOnDatabase));

    when(customerRepository.save(any(Customer.class))).then(
      will(returnsFirstArg()).to(Mono::just)
    );

    StepVerifier
      .create(changeCustomerStateService.changeState(request))
      .consumeNextWith(changedCustomer -> {
        verify(customerRepository).save(changedCustomer);
        assertEquals(customerId, changedCustomer.id());
        assertEquals(request.newState(), changedCustomer.state());
      })
      .verifyComplete();
  }

  @Test
  void shouldPublishCustomerChangedEvent() {
    var customerId = 1;
    var request = new ChangeCustomerStateRequest(customerId, State.ACTIVATED);

    var customerOnDatabase = new Customer(customerId, "name", State.CREATED);
    when(customerRepository.findById(customerId)).thenReturn(Mono.just(customerOnDatabase));

    when(customerRepository.save(any(Customer.class))).then(
      will(returnsFirstArg()).to(Mono::just)
    );

    StepVerifier
      .create(changeCustomerStateService.changeState(request))
      .consumeNextWith((updatedCustomer) -> {
        verify(customerEventBus).customerChanged(updatedCustomer);
      })
      .verifyComplete();
  }
}
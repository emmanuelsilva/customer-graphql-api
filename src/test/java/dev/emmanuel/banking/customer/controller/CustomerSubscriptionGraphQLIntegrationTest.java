package dev.emmanuel.banking.customer.controller;


import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import dev.emmanuel.banking.customer.dto.request.ChangeCustomerStateRequest;
import dev.emmanuel.banking.customer.service.ChangeCustomerStateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Predicate;

@SpringBootTest
public class CustomerSubscriptionGraphQLIntegrationTest extends GraphQLIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private ChangeCustomerStateService changeCustomerStateService;

  @Test
  public void shouldReceiveChangesAfterSubscribeForEvents() {
    var customerOnDatabase = customerRepository
      .save(new Customer(null, "Customer", State.CREATED))
      .block();

    Flux<GraphQlTester.ResponseSpec> customerChangedEvents = subscribeToReceiveChangeEvents(customerOnDatabase);

    changeCustomerState(customerOnDatabase.id(), State.ACTIVATED);
    changeCustomerState(customerOnDatabase.id(), State.DEACTIVATED);

    StepVerifier
      .create(customerChangedEvents)
      .consumeNextWith(event -> event
        .path("customerChangeEvents")
        .entity(Customer.class)
        .matches(hasExpectedState(customerOnDatabase.id(), State.ACTIVATED))
      )
      .consumeNextWith(event ->
        event
          .path("customerChangeEvents")
          .entity(Customer.class)
          .matches(hasExpectedState(customerOnDatabase.id(), State.DEACTIVATED))
      )
      .thenCancel()
      .verify(Duration.ofSeconds(5));
  }


  private Predicate<Customer> hasExpectedState(int expectedId, State expectedState) {
    return (customer) -> customer.id().equals(expectedId) && customer.state().equals(expectedState);
  }

  private void changeCustomerState(int id, State newState) {
    var changeCustomerStateRequest = new ChangeCustomerStateRequest(id, newState);

    changeCustomerStateService
      .changeState(changeCustomerStateRequest)
      .block();
  }

  private Flux<GraphQlTester.ResponseSpec> subscribeToReceiveChangeEvents(Customer customerOnDatabase) {

    var subscriptionGraphQLRequest =
      """
      subscription {
        customerChangeEvents(customerId: %d) {
          id,
          name,
          state
        }
      }
      """;

    return graphQlTester
      .query(subscriptionGraphQLRequest.formatted(customerOnDatabase.id()))
      .executeSubscription()
      .toFlux();
  }

}

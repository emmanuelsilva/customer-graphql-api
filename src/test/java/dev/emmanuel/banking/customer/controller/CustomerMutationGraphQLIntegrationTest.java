package dev.emmanuel.banking.customer.controller;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@SpringBootTest
public class CustomerMutationGraphQLIntegrationTest extends GraphQLIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  @BeforeEach
  public void setUp() {
    customerRepository.deleteAll().block();
  }

  @Test
  public void shouldCreateCustomer() {
    var customerName = "New Customer";

    var addCustomerMutation =
      """
      mutation {
        addCustomer(request: {
          name: "%s"
        }) {
          id,
          name,
          state
        }
      }
      """;

    graphQlTester
      .query(addCustomerMutation.formatted(customerName))
      .execute()
      .path("addCustomer")
      .entity(Customer.class)
      .satisfies(createdCustomer -> Objects.nonNull(createdCustomer.id()))
      .satisfies(createdCustomer -> createdCustomer.name().equals(customerName));
  }

  @Test
  public void shouldUpdateCustomerState() {
    var customerOnDatabase = customerRepository.save(new Customer(null, "Customer", State.CREATED)).block();
    var customerId = customerOnDatabase.id();
    var newState = State.ACTIVATED;

    var updateCustomerStateMutation =
      """
      mutation {
        changeState(request: {
          customerId: %d,
          newState: %s
        }) {
          id,
          name,
          state
        }
      }
      """;

    graphQlTester
      .query(updateCustomerStateMutation.formatted(customerId, newState))
      .execute()
      .path("changeState")
      .entity(Customer.class)
      .satisfies(updatedCustomer -> updatedCustomer.id().equals(customerId))
      .satisfies(updatedCustomer -> updatedCustomer.state().equals(newState));
  }

}

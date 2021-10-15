package dev.emmanuel.banking.customer.controller;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CustomerQueryIntegrationTest extends GraphQLIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  void shouldListAllCustomersFromDatabase() {

    List<Customer> customersOnDatabase = givenCustomersOnDatabase();

    var graphQLQuery =
      """
      {
        customers {
          id,
          name,
          state
        }
      }
      """;

    this.graphQlTester
      .query(graphQLQuery)
      .execute()
      .path("customers")
      .entityList(Customer.class)
      .contains(customersOnDatabase.toArray(new Customer[0]));
  }

  private List<Customer> givenCustomersOnDatabase() {
    var firstCustomer = new Customer(null, "First Customer", State.CREATED);
    var secondCustomer = new Customer(null, "Second Customer", State.CREATED);

    return customerRepository
      .saveAll(List.of(firstCustomer, secondCustomer))
      .collectList()
      .block();
  }

}

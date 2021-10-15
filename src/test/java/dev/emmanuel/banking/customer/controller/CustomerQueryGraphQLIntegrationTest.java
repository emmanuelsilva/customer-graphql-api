package dev.emmanuel.banking.customer.controller;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import dev.emmanuel.banking.customer.domain.entity.State;
import dev.emmanuel.banking.customer.domain.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CustomerQueryGraphQLIntegrationTest extends GraphQLIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  @BeforeEach
  public void setUp() {
    customerRepository.deleteAll().block();
  }

  @Test
  void shouldListAllCustomersFromDatabase() {
    List<Customer> customersOnDatabase = givenListOfCustomersOnDatabase();

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
      .containsExactly(customersOnDatabase.toArray(new Customer[0]));
  }

  @Test
  public void shouldFindCustomerByName() {
    var customerOnDatabase = customerRepository
      .save(new Customer(null,"Customer name", State.CREATED))
      .block();

    var graphQLQuery =
      """
      {
        findCustomerByName(name:"%s") {
          id,
          name,
          state
        }
      }  
      """;

    this.graphQlTester
      .query(graphQLQuery.formatted(customerOnDatabase.name()))
      .execute()
      .path("findCustomerByName")
      .entity(Customer.class)
      .isEqualTo(customerOnDatabase);
  }

  private List<Customer> givenListOfCustomersOnDatabase() {
    var firstCustomer = new Customer(null,"First Customer", State.CREATED);
    var secondCustomer = new Customer(null,"Second Customer", State.CREATED);

    return customerRepository
      .saveAll(List.of(firstCustomer, secondCustomer))
      .collectList()
      .block();
  }

}

package dev.emmanuel.banking.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.boot.test.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;

@AutoConfigureGraphQlTester
public abstract class GraphQLIntegrationTest {

  @Autowired
  protected GraphQlTester graphQlTester;

}

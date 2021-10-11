# Customer GraphQL API

Example of project using Spring Boot to build a GraphQL API that supports Query, Mutation and Subscription by using WebSockets.

## GraphQL 

- API Endpoint: http://localhost:8080/graphql
- WS Subscription endpoint: ws://localhost:8080/graphql
- GraphQL Playground URL: http://localhost:8080/graphiql/

## Queries

Operations exposed to query data

### List all customers
```graphql
{
  customers {
    id,
    name,
    state
  }
}
```

### Find a customer by name
```graphql
{
  findCustomerByName(name: "Emmanuel") {
    id
    name
    state
  }
}
```
## Mutations

Operation to change customers

### Create a customer
```graphql
mutation {
  addCustomer(name: "New Customer") {
    id
  }
}
```

### Change state of a customer
```graphql
mutation {
  changeState(customerId: 1, newState: CREATED) {
    id,
    name,
    state
  }
}
```

## Subscriptions

Operations to fetch changes in real-time by consuming server side events through Web sockets.

WS endpoint: ws://localhost:8080/graphql

```graphql
subscription {
  changeStateEvents(customerId: 1) { id, name, state}
}
```
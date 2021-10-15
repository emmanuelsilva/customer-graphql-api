# Customer GraphQL API
* * *
Hi there :wave:

This is an example of project using Spring Boot to build a GraphQL API that supports `Query`, `Mutation` and Websocket `Subscription`.

## GraphQL 
* * *

- API Endpoint: http://localhost:8080/graphql
- WS Subscription endpoint: ws://localhost:8080/graphql
- GraphQL Playground URL: http://localhost:8080/graphiql/

### Queries
* * *

Operations exposed to query data

#### List all customers
```graphql
{
  customers {
    id,
    name,
    state
  }
}
```

#### Find a customer by name
```graphql
{
  findCustomerByName(name: "Emmanuel") {
    id
    name
    state
  }
}
```
### Mutations
* * *

Operation to change customers

#### Create a customer
```graphql
mutation {
  addCustomer(request: {
    name: "New Customer"
  }) {
    id
  }
}
```

#### Change state of a customer
```graphql
mutation {
  changeState(request: {
    customerId: 1, newState: CREATED
  }) {
    id,
    name,
    state
  }
}
```

### Subscriptions
* * *

Operations to fetch changes in real-time by consuming server side events through Web sockets.

WS endpoint: ws://localhost:8080/graphql

```graphql
subscription {
  customerChangeEvents(customerId: 1) { id, name, state}
}
```

## TODO
* * *
- Authentication
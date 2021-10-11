package dev.emmanuel.banking.customer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("customers")
public record Customer(@Id Integer id, String name, State state) {

  public Customer withState(State state) {
    return new Customer(id, name, state);
  }

}

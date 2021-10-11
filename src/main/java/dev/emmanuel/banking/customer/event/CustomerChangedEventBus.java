package dev.emmanuel.banking.customer.event;

import dev.emmanuel.banking.customer.domain.entity.Customer;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class CustomerChangedEventBus {

  private final Sinks.Many<Customer> processor;

  public CustomerChangedEventBus() {
    this.processor = Sinks.many().multicast().onBackpressureBuffer();
  }

  public void publishCustomerChange(Customer customer) {
    this.processor.tryEmitNext(customer);
  }

  public Publisher<Customer> subscribeFor(int customerId) {
    return this.processor
      .asFlux()
      .filter(customer -> customer.id() == customerId);
  }

}

package dev.emmanuel.banking.customer.dto.request;

import dev.emmanuel.banking.customer.domain.entity.State;

public record ChangeCustomerStateRequest(Integer customerId, State newState) {
}

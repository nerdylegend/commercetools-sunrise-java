package com.commercetools.sunrise.models.customers;

import com.commercetools.sunrise.core.sessions.ResourceStoringOperations;
import com.google.inject.ImplementedBy;
import io.sphere.sdk.customers.Customer;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Keeps some parts from the customer in session, such as customer ID, email and some general info.
 */
@ImplementedBy(CustomerInSessionImpl.class)
public interface CustomerInSession extends ResourceStoringOperations<Customer> {

    Optional<String> findCustomerId();

    Optional<String> findCustomerGroupId();

    Optional<String> findCustomerEmail();

    @Override
    void store(@Nullable final Customer customer);

    @Override
    void remove();
}

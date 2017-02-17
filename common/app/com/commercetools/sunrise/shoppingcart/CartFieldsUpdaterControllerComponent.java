package com.commercetools.sunrise.shoppingcart;

import com.commercetools.sunrise.common.injection.RequestScoped;
import com.commercetools.sunrise.common.sessions.customer.CustomerInSession;
import com.commercetools.sunrise.framework.ControllerComponent;
import com.commercetools.sunrise.hooks.actions.CartLoadedActionHook;
import com.commercetools.sunrise.hooks.actions.CustomerSignedInActionHook;
import com.commercetools.sunrise.hooks.requests.CartCreateCommandHook;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.CartDraftBuilder;
import io.sphere.sdk.carts.CartDraftDsl;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetCountry;
import io.sphere.sdk.carts.commands.updateactions.SetCustomerEmail;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.expansion.ExpansionPathContainer;
import io.sphere.sdk.models.Address;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

@RequestScoped
public class CartFieldsUpdaterControllerComponent implements ControllerComponent, CartCreateCommandHook, CartLoadedActionHook, CustomerSignedInActionHook {

    private final CountryCode country;
    private final CustomerInSession customerInSession;
    private final SphereClient sphereClient;

    @Inject
    public CartFieldsUpdaterControllerComponent(final CountryCode country, final CustomerInSession customerInSession, final SphereClient sphereClient) {
        this.country = country;
        this.customerInSession = customerInSession;
        this.sphereClient = sphereClient;
    }

    @Override
    public CartCreateCommand onCartCreateCommand(final CartCreateCommand cartCreateCommand) {
        final CartDraft cartDraft = cartDraftWithAdditionalInfo(cartCreateCommand.getDraft());
        return CartCreateCommand.of(cartDraft)
                .withExpansionPaths(cartCreateCommand.expansionPaths());
    }

    private CartDraftDsl cartDraftWithAdditionalInfo(final CartDraft cartDraft) {
        return CartDraftBuilder.of(cartDraft)
                .country(country)
                .shippingAddress(Address.of(country))
                .customerId(customerInSession.findCustomerId().orElse(null))
                .customerEmail(customerInSession.findCustomerEmail().orElse(null))
                .build();
    }

    @Override
    public CompletionStage<CustomerSignInResult> onCustomerSignedInAction(final CustomerSignInResult customerSignInResult,
                                                                          @Nullable final ExpansionPathContainer<CustomerSignInResult> expansionPathContainer) {
        return Optional.ofNullable(customerSignInResult.getCart())
                .map(cart -> buildRequest(cart, customerSignInResult.getCustomer()))
                .filter(command -> !command.getUpdateActions().isEmpty())
                .map(command -> sphereClient.execute(command)
                        // Expansion path is honored, since only possible expansion is on customer (on the CreateCustomerCommand)
                        // and customer is kept as it was received in the original response
                        .thenApply(updatedCart -> buildCustomerSignInResult(customerSignInResult.getCustomer(), updatedCart)))
                .orElseGet(() -> completedFuture(customerSignInResult));
    }

    @Override
    public CompletionStage<Cart> onCartLoadedAction(final Cart cart, final ExpansionPathContainer<Cart> expansionPathContainer) {
        // TODO Handle case where some line items do not exist for this country
        final CartUpdateCommand updateCommand = buildRequest(cart, null);
        if (!updateCommand.getUpdateActions().isEmpty()) {
            return sphereClient.execute(updateCommand.withExpansionPaths(expansionPathContainer));
        } else {
            return completedFuture(cart);
        }
    }

    protected CartUpdateCommand buildRequest(final Cart cart, @Nullable final Customer customer) {
        return CartUpdateCommand.of(cart, buildUpdateActions(cart, customer));
    }

    private List<UpdateAction<Cart>> buildUpdateActions(final Cart cart, @Nullable final Customer customer) {
        final List<UpdateAction<Cart>> updateActions = new ArrayList<>();
        final boolean hasDifferentCountry = !country.equals(cart.getCountry());
        if (hasDifferentCountry) {
            final Address shippingAddress = Optional.ofNullable(cart.getShippingAddress())
                    .map(address -> address.withCountry(country))
                    .orElseGet(() -> Address.of(country));
            updateActions.add(SetShippingAddress.of(shippingAddress));
            updateActions.add(SetCountry.of(country));
        }
        final boolean hasDifferentCustomerEmail = customer != null && !customer.getEmail().equals(cart.getCustomerEmail());
        if (hasDifferentCustomerEmail) {
            updateActions.add(SetCustomerEmail.of(customer.getEmail()));
        }
        return updateActions;
    }

    private CustomerSignInResult buildCustomerSignInResult(final Customer customer, @Nullable final Cart cart) {
        return new CustomerSignInResult() {
            @Override
            public Customer getCustomer() {
                return customer;
            }

            @Nullable
            @Override
            public Cart getCart() {
                return cart;
            }
        };
    }
}

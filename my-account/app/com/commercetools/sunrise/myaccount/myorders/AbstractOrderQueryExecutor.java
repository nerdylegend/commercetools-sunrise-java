package com.commercetools.sunrise.myaccount.myorders;

import com.commercetools.sunrise.common.controllers.AbstractSphereRequestExecutor;
import com.commercetools.sunrise.hooks.HookRunner;
import com.commercetools.sunrise.hooks.requests.OrderQueryHook;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.queries.OrderQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.concurrent.CompletionStage;

public abstract class AbstractOrderQueryExecutor extends AbstractSphereRequestExecutor {

    protected AbstractOrderQueryExecutor(final SphereClient sphereClient, final HookRunner hookRunner) {
        super(sphereClient, hookRunner);
    }

    protected final CompletionStage<PagedQueryResult<Order>> executeRequest(final OrderQuery baseQuery) {
        final OrderQuery query = OrderQueryHook.runHook(getHookRunner(), baseQuery);
        return getSphereClient().execute(query);
    }
}

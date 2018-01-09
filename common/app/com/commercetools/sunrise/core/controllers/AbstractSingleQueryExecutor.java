package com.commercetools.sunrise.core.controllers;

import com.commercetools.sunrise.core.hooks.HookRunner;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.ResourceQuery;
import play.libs.concurrent.HttpExecution;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public abstract class AbstractSingleQueryExecutor<T, Q extends ResourceQuery<T>> extends AbstractSphereRequestExecutor implements ResourceFetcher<T> {

    protected AbstractSingleQueryExecutor(final SphereClient sphereClient, final HookRunner hookRunner) {
        super(sphereClient, hookRunner);
    }

    protected final CompletionStage<Optional<T>> executeRequest(final Q baseRequest) {
        return executeRequest(baseRequest, this::selectResource);
    }

    protected final CompletionStage<Optional<T>> executeRequest(final Q baseRequest, final Function<PagedQueryResult<T>, Optional<T>> resourceSelector) {
        final Q request = runQueryHook(baseRequest);
        return getSphereClient().execute(request)
                .thenApply(resourceSelector)
                .thenApplyAsync(resourceOpt -> {
                    resourceOpt.ifPresent(this::runResourceLoadedHook);
                    return resourceOpt;
                }, HttpExecution.defaultContext());
    }

    protected Optional<T> selectResource(final PagedQueryResult<T> result) {
        return result.head();
    }

    protected abstract Q runQueryHook(Q baseRequest);

    protected abstract CompletionStage<?> runResourceLoadedHook(T resource);
}
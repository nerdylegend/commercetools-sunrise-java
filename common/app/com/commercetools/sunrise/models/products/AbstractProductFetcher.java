package com.commercetools.sunrise.models.products;

import com.commercetools.sunrise.core.controllers.AbstractSingleQueryExecutor;
import com.commercetools.sunrise.core.hooks.HookRunner;
import com.commercetools.sunrise.core.hooks.ctpevents.ProductProjectionLoadedHook;
import com.commercetools.sunrise.core.hooks.ctpevents.ProductVariantLoadedHook;
import com.commercetools.sunrise.core.hooks.ctprequests.ProductProjectionQueryHook;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

public abstract class AbstractProductFetcher extends AbstractSingleQueryExecutor<ProductProjection, ProductProjectionQuery> implements ProductFetcher {

    protected AbstractProductFetcher(final SphereClient sphereClient, final HookRunner hookRunner) {
        super(sphereClient, hookRunner);
    }

    @Override
    public CompletionStage<Optional<ProductWithVariant>> apply(final String productIdentifier, final String variantIdentifier) {
        return buildRequest(productIdentifier)
                .map(request -> executeRequest(request, result -> selectResource(result, productIdentifier))
                        .thenApply(productOpt -> productOpt
                                .map(product -> {
                                    final ProductVariant variant = selectProductVariant(product, variantIdentifier);
                                    ProductVariantLoadedHook.runHook(getHookRunner(), product, variant);
                                    return ProductWithVariant.of(product, variant);
                                })))
                .orElseGet(() -> completedFuture(Optional.empty()));
    }

    @Override
    protected ProductProjectionQuery runQueryHook(final ProductProjectionQuery baseRequest) {
        return ProductProjectionQueryHook.runHook(getHookRunner(), baseRequest);
    }

    @Override
    protected CompletionStage<?> runResourceLoadedHook(final ProductProjection resource) {
        return ProductProjectionLoadedHook.runHook(getHookRunner(), resource);
    }

    @Override
    protected final Optional<ProductProjection> selectResource(final PagedQueryResult<ProductProjection> result) {
        return super.selectResource(result);
    }

    protected Optional<ProductProjection> selectResource(final PagedQueryResult<ProductProjection> result, final String identifier) {
        return selectResource(result);
    }

    protected abstract Optional<ProductProjectionQuery> buildRequest(String identifier);

    protected abstract ProductVariant selectProductVariant(final ProductProjection product, final String variantIdentifier);
}
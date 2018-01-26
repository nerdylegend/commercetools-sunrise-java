package com.commercetools.sunrise.productcatalog.products.search.sort;

import com.commercetools.sunrise.models.search.sort.viewmodels.AbstractSortSelectorViewModelFactory;
import com.commercetools.sunrise.models.search.sort.viewmodels.SortFormSelectableOptionViewModelFactory;
import io.sphere.sdk.products.ProductProjection;

import javax.inject.Inject;

public final class ProductSearchSortSelectorViewModelFactory extends AbstractSortSelectorViewModelFactory<ProductProjection> {

    @Inject
    public ProductSearchSortSelectorViewModelFactory(final ProductSortFormSettings productSortFormSettings,
                                                     final SortFormSelectableOptionViewModelFactory sortFormSelectableOptionViewModelFactory) {
        super(productSortFormSettings, sortFormSelectableOptionViewModelFactory);
    }
}
package com.commercetools.sunrise.productcatalog.products.search.facetedsearch.categorytree.viewmodels;

import com.commercetools.sunrise.core.i18n.I18nResolver;
import com.commercetools.sunrise.core.injection.RequestScoped;
import com.commercetools.sunrise.models.categories.CachedCategoryTree;
import com.commercetools.sunrise.models.search.facetedsearch.terms.viewmodels.AbstractTermFacetViewModelFactory;
import com.commercetools.sunrise.models.search.facetedsearch.viewmodels.FacetOption;
import com.commercetools.sunrise.productcatalog.products.search.facetedsearch.categorytree.CategoryTreeFacetedSearchFormSettings;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.search.TermFacetResult;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Mapper that transforms facet options with Category IDs into a hierarchical list of facet options.
 * The IDs are then replaced by the Category name, in a language according to the provided locales.
 */
@RequestScoped
public final class CategoryTreeFacetViewModelFactory extends AbstractTermFacetViewModelFactory<CategoryTreeFacetedSearchFormSettings> {

    private final CachedCategoryTree cachedCategoryTree;
    private final CategoryTreeFacetOptionViewModelFactory categoryTreeFacetOptionViewModelFactory;

    @Inject
    public CategoryTreeFacetViewModelFactory(final I18nResolver i18nResolver,
                                             final CachedCategoryTree cachedCategoryTree,
                                             final CategoryTreeFacetOptionViewModelFactory categoryTreeFacetOptionViewModelFactory) {
        super(i18nResolver);
        this.cachedCategoryTree = cachedCategoryTree;
        this.categoryTreeFacetOptionViewModelFactory = categoryTreeFacetOptionViewModelFactory;
    }

    @Override
    protected List<FacetOption> createOptions(final CategoryTreeFacetedSearchFormSettings settings, final TermFacetResult facetResult) {
        final Category selectedValue = settings.getSelectedValue(Http.Context.current()).orElse(null);
        return cachedCategoryTree.blockingGet().getSubtreeRoots().stream()
                .map(root -> categoryTreeFacetOptionViewModelFactory.create(facetResult, root, selectedValue))
                .filter(root -> root.getCount() > 0)
                .collect(toList());
    }
}
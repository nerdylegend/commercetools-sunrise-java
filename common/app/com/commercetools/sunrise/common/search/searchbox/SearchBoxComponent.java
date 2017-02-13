package com.commercetools.sunrise.common.search.searchbox;

import com.commercetools.sunrise.framework.ControllerComponent;
import com.commercetools.sunrise.hooks.consumers.PageDataReadyHook;
import com.commercetools.sunrise.hooks.requests.ProductProjectionSearchHook;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Locale;

import static com.commercetools.sunrise.common.forms.QueryStringUtils.findSelectedValueFromQueryString;

public final class SearchBoxComponent implements ControllerComponent, PageDataReadyHook, ProductProjectionSearchHook {

    private final Locale locale;
    private final String searchText;

    @Inject
    public SearchBoxComponent(final Locale locale, final SearchBoxSettings searchBoxSettings, final Http.Request httpRequest) {
        this.locale = locale;
        this.searchText = findSelectedValueFromQueryString(searchBoxSettings, httpRequest);
    }

    @Override
    public ProductProjectionSearch onProductProjectionSearch(final ProductProjectionSearch search) {
        if (!searchText.isEmpty()) {
            return search.withText(locale, searchText);
        } else {
            return search;
        }
    }

    @Override
    public void onPageDataReady(final PageData pageData) {
        if (!searchText.isEmpty() && pageData.getContent() instanceof WithSearchBoxViewModel) {
            final WithSearchBoxViewModel content = (WithSearchBoxViewModel) pageData.getContent();
            content.setSearchTerm(searchText);
        }
    }
}

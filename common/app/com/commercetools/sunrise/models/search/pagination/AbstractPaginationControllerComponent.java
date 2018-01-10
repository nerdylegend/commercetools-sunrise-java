package com.commercetools.sunrise.models.search.pagination;

import com.commercetools.sunrise.core.components.ControllerComponent;
import com.commercetools.sunrise.core.hooks.application.PageDataReadyHook;
import com.commercetools.sunrise.core.viewmodels.OldPageData;
import com.commercetools.sunrise.models.search.pagination.viewmodels.AbstractEntriesPerPageSelectorViewModelFactory;
import com.commercetools.sunrise.models.search.pagination.viewmodels.AbstractPaginationViewModelFactory;
import com.commercetools.sunrise.models.search.pagination.viewmodels.WithPaginationViewModel;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.queries.PagedResult;
import play.mvc.Http;

import javax.annotation.Nullable;

public abstract class AbstractPaginationControllerComponent extends Base implements ControllerComponent, PageDataReadyHook {

    private final PaginationSettings paginationSettings;
    private final EntriesPerPageFormSettings entriesPerPageFormSettings;

    private final AbstractPaginationViewModelFactory paginationViewModelFactory;
    private final AbstractEntriesPerPageSelectorViewModelFactory entriesPerPageSelectorViewModelFactory;

    protected AbstractPaginationControllerComponent(final PaginationSettings paginationSettings,
                                                    final EntriesPerPageFormSettings entriesPerPageFormSettings,
                                                    final AbstractPaginationViewModelFactory paginationViewModelFactory,
                                                    final AbstractEntriesPerPageSelectorViewModelFactory entriesPerPageSelectorViewModelFactory) {
        this.paginationSettings = paginationSettings;
        this.entriesPerPageFormSettings = entriesPerPageFormSettings;
        this.paginationViewModelFactory = paginationViewModelFactory;
        this.entriesPerPageSelectorViewModelFactory = entriesPerPageSelectorViewModelFactory;
    }

    protected final PaginationSettings getPaginationSettings() {
        return paginationSettings;
    }

    protected final EntriesPerPageFormSettings getEntriesPerPageSettings() {
        return entriesPerPageFormSettings;
    }

    @Nullable
    protected abstract PagedResult<?> getPagedResult();

    @Override
    public void onPageDataReady(final OldPageData oldPageData) {
        final PagedResult<?> pagedResult = getPagedResult();
        if (pagedResult != null && oldPageData.getContent() instanceof WithPaginationViewModel) {
            final WithPaginationViewModel content = (WithPaginationViewModel) oldPageData.getContent();
            final Long currentPage = paginationSettings.getSelectedValueOrDefault(Http.Context.current());
            content.setPagination(paginationViewModelFactory.create(pagedResult, currentPage));
            content.setDisplaySelector(entriesPerPageSelectorViewModelFactory.create(pagedResult));
        }
    }
}

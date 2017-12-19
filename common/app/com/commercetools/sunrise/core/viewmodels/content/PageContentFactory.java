package com.commercetools.sunrise.core.viewmodels.content;

import com.commercetools.sunrise.core.viewmodels.SimpleViewModelFactory;

public abstract class PageContentFactory<M extends PageContent, I> extends SimpleViewModelFactory<M, I> {

    @Override
    protected void initialize(final M viewModel, final I input) {
        fillTitle(viewModel, input);
        fillMessages(viewModel, input);
    }

    /**
     * @deprecated fill the title from within the template like on cart.hbs
     * @param viewModel
     * @param input
     */
    @Deprecated
    protected void fillTitle(final M viewModel, final I input){

    }

    protected void fillMessages(final M viewModel, final I input) {
        viewModel.addMessages(extractMessages());
    }
}

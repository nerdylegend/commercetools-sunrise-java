package com.commercetools.sunrise.common.template.engine;

import com.commercetools.sunrise.common.pages.PageContent;
import com.google.inject.ImplementedBy;
import play.twirl.api.Content;

import javax.annotation.Nullable;
import java.util.concurrent.CompletionStage;

@ImplementedBy(HtmlTemplateRenderer.class)
public interface TemplateRenderer {

    CompletionStage<Content> render(final PageContent pageContent, final String templateName, @Nullable final String cmsKey);

    default CompletionStage<Content> render(final PageContent pageContent, final String templateName) {
        return render(pageContent, templateName, null);
    }
}

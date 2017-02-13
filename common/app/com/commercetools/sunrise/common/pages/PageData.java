package com.commercetools.sunrise.common.pages;

import com.commercetools.sunrise.common.models.ViewModel;

public final class PageData extends ViewModel {

    private PageHeader header;
    private PageFooter footer;
    private PageContent content;
    private PageMeta meta;

    public PageData() {
    }

    public PageHeader getHeader() {
        return header;
    }

    public void setHeader(final PageHeader header) {
        this.header = header;
    }

    public PageFooter getFooter() {
        return footer;
    }

    public void setFooter(final PageFooter footer) {
        this.footer = footer;
    }

    public PageContent getContent() {
        return content;
    }

    public void setContent(final PageContent content) {
        this.content = content;
    }

    public PageMeta getMeta() {
        return meta;
    }

    public void setMeta(final PageMeta meta) {
        this.meta = meta;
    }
}

package com.commercetools.sunrise.core.renderers.handlebars;

import com.commercetools.sunrise.core.i18n.I18nResolver;
import com.commercetools.sunrise.core.viewmodels.PageData;
import com.commercetools.sunrise.core.viewmodels.formatters.PriceFormatter;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import io.sphere.sdk.models.LocalizedString;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.money.MonetaryAmount;
import java.util.Map;
import java.util.Set;

@Singleton
public final class SunriseJavaBeanValueResolver implements ValueResolver {

    private final ValueResolver delegate = JavaBeanValueResolver.INSTANCE;
    private final I18nResolver i18nResolver;
    private final PriceFormatter priceFormatter;

    @Inject
    SunriseJavaBeanValueResolver(final I18nResolver i18nResolver, final PriceFormatter priceFormatter) {
        this.i18nResolver = i18nResolver;
        this.priceFormatter = priceFormatter;
    }

    @Override
    public Object resolve(final Object context, final String name) {
        Object result = delegate.resolve(context, name);
        if (result instanceof LocalizedString) {
            result = resolveLocalizedString((LocalizedString) result);
        } else if (result instanceof MonetaryAmount) {
            result = resolveMonetaryAmount((MonetaryAmount) result);
        } else if (result == UNRESOLVED && context instanceof PageData) {
            result = resolveExtendedViewModel((PageData) context, name);
        }
        return result;
    }

    @Override
    public Set<Map.Entry<String, Object>> propertySet(final Object context) {
        return delegate.propertySet(context);
    }

    @Override
    public Object resolve(final Object context) {
        return delegate.resolve(context);
    }

    @Nullable
    private Object resolveExtendedViewModel(final PageData pageData, final String name) {
        final Object result = pageData.get(name);
        return result != null ? result : UNRESOLVED;
    }

    private String resolveLocalizedString(final LocalizedString localizedString) {
        return i18nResolver.get(localizedString).orElse("");
    }

    private String resolveMonetaryAmount(final MonetaryAmount monetaryAmount) {
        return priceFormatter.format(monetaryAmount);
    }
}

package com.commercetools.sunrise.common.utils;

import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.productdiscounts.DiscountedPrice;
import io.sphere.sdk.products.PriceLike;
import io.sphere.sdk.products.ProductVariant;

import javax.money.MonetaryAmount;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public final class ProductPriceUtils {

    private ProductPriceUtils() {
    }

    public static Optional<MonetaryAmount> calculateAppliedProductPrice(final ProductVariant variant) {
        return findSelectedPrice(variant)
                .map(ProductPriceUtils::calculateAppliedPrice);
    }

    public static MonetaryAmount calculateAppliedProductPrice(final LineItem lineItem) {
        return calculateAppliedPrice(lineItem.getPrice());
    }

    public static Optional<MonetaryAmount> calculatePreviousProductPrice(final ProductVariant variant) {
        return findSelectedPrice(variant)
                .flatMap(ProductPriceUtils::calculatePreviousPrice);
    }

    public static Optional<MonetaryAmount> calculatePreviousProductPrice(final LineItem lineItem) {
        return calculatePreviousPrice(lineItem.getPrice());
    }

    private static MonetaryAmount calculateAppliedPrice(final PriceLike price) {
        return findPriceWithDiscount(price)
                .orElseGet(price::getValue);
    }

    private static Optional<MonetaryAmount> calculatePreviousPrice(final PriceLike price) {
        return findPriceWithDiscount(price)
                .map(priceWithDiscount -> price.getValue());
    }

    private static Optional<MonetaryAmount> findPriceWithDiscount(final PriceLike price) {
        return Optional.ofNullable(price.getDiscounted())
                .map(DiscountedPrice::getValue);
    }

    private static Optional<PriceLike> findSelectedPrice(final ProductVariant variant) {
        final PriceLike price = firstNonNull(variant.getPrice(), variant.getScopedPrice());
        return Optional.ofNullable(price);
    }
}

package controllers.shoppingcart;

import com.commercetools.sunrise.core.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.core.controllers.cache.NoCache;
import com.commercetools.sunrise.core.controllers.metrics.LogMetrics;
import com.commercetools.sunrise.core.reverserouters.shoppingcart.cart.CartReverseRouter;
import com.commercetools.sunrise.core.reverserouters.shoppingcart.checkout.CheckoutReverseRouter;
import com.commercetools.sunrise.core.renderers.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.core.renderers.ContentRenderer;
import com.commercetools.sunrise.models.carts.CartDiscountCodesExpansionControllerComponent;
import com.commercetools.sunrise.models.carts.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.models.carts.CartFinder;
import com.commercetools.sunrise.shoppingcart.checkout.CheckoutStepControllerComponent;
import com.commercetools.sunrise.shoppingcart.checkout.address.CheckoutAddressControllerAction;
import com.commercetools.sunrise.shoppingcart.checkout.address.CheckoutAddressFormData;
import com.commercetools.sunrise.shoppingcart.checkout.address.SunriseCheckoutAddressController;
import com.commercetools.sunrise.shoppingcart.checkout.address.viewmodels.CheckoutAddressPageContentFactory;
import io.sphere.sdk.carts.Cart;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@LogMetrics
@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        CheckoutStepControllerComponent.class,
        CartOperationsControllerComponentSupplier.class,
        CartDiscountCodesExpansionControllerComponent.class,
})
public final class CheckoutAddressController extends SunriseCheckoutAddressController {

    private final CartReverseRouter cartReverseRouter;
    private final CheckoutReverseRouter checkoutReverseRouter;

    @Inject
    public CheckoutAddressController(final ContentRenderer contentRenderer,
                                     final FormFactory formFactory,
                                     final CheckoutAddressFormData formData,
                                     final CartFinder cartFinder,
                                     final CheckoutAddressControllerAction controllerAction,
                                     final CheckoutAddressPageContentFactory pageContentFactory,
                                     final CartReverseRouter cartReverseRouter,
                                     final CheckoutReverseRouter checkoutReverseRouter) {
        super(contentRenderer, formFactory, formData, cartFinder, controllerAction, pageContentFactory);
        this.cartReverseRouter = cartReverseRouter;
        this.checkoutReverseRouter = checkoutReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "checkout-address";
    }

    @Override
    public String getCmsPageKey() {
        return "default";
    }

    @Override
    public CompletionStage<Result> handleNotFoundCart() {
        return redirectToCall(cartReverseRouter.cartDetailPageCall());
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Cart updatedCart, final CheckoutAddressFormData formData) {
        return redirectToCall(checkoutReverseRouter.checkoutShippingPageCall());
    }
}
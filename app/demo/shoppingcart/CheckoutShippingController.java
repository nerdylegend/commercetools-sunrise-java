package demo.shoppingcart;

import com.commercetools.sunrise.common.reverserouter.CartReverseRouter;
import com.commercetools.sunrise.common.reverserouter.CheckoutReverseRouter;
import com.commercetools.sunrise.common.template.engine.TemplateRenderer;
import com.commercetools.sunrise.hooks.RequestHookContext;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.checkout.shipping.CheckoutShippingExecutor;
import com.commercetools.sunrise.shoppingcart.checkout.shipping.DefaultCheckoutShippingFormData;
import com.commercetools.sunrise.shoppingcart.checkout.shipping.ShippingSettings;
import com.commercetools.sunrise.shoppingcart.checkout.shipping.SunriseCheckoutShippingController;
import com.commercetools.sunrise.shoppingcart.checkout.shipping.view.CheckoutShippingPageContentFactory;
import io.sphere.sdk.carts.Cart;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public final class CheckoutShippingController extends SunriseCheckoutShippingController<DefaultCheckoutShippingFormData> {

    private final CartReverseRouter cartReverseRouter;
    private final CheckoutReverseRouter checkoutReverseRouter;

    @Inject
    public CheckoutShippingController(final TemplateRenderer templateRenderer,
                                      final RequestHookContext hookContext,
                                      final CartFinder cartFinder,
                                      final FormFactory formFactory,
                                      final CheckoutShippingExecutor checkoutShippingExecutor,
                                      final CheckoutShippingPageContentFactory checkoutShippingPageContentFactory,
                                      final ShippingSettings shippingSettings,
                                      final CartReverseRouter cartReverseRouter,
                                      final CheckoutReverseRouter checkoutReverseRouter) {
        super(templateRenderer, hookContext, cartFinder, formFactory, checkoutShippingExecutor, checkoutShippingPageContentFactory, shippingSettings);
        this.cartReverseRouter = cartReverseRouter;
        this.checkoutReverseRouter = checkoutReverseRouter;
    }

    @Override
    public Class<DefaultCheckoutShippingFormData> getFormDataClass() {
        return DefaultCheckoutShippingFormData.class;
    }

    @Override
    public CompletionStage<Result> handleNotFoundCart() {
        return redirectTo(cartReverseRouter.showCart());
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Cart updatedCart, final DefaultCheckoutShippingFormData formData) {
        return redirectTo(checkoutReverseRouter.checkoutPaymentPageCall());
    }
}

package controllers.myaccount;

import com.commercetools.sunrise.email.EmailDeliveryException;
import com.commercetools.sunrise.core.components.controllers.PageHeaderControllerComponentSupplier;
import com.commercetools.sunrise.core.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.core.controllers.cache.NoCache;
import com.commercetools.sunrise.core.controllers.metrics.LogMetrics;
import com.commercetools.sunrise.core.renderers.ContentRenderer;
import com.commercetools.sunrise.core.renderers.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.core.reverserouters.myaccount.recoverpassword.RecoverPasswordReverseRouter;
import com.commercetools.sunrise.core.viewmodels.content.messages.MessageType;
import com.commercetools.sunrise.myaccount.authentication.recoverpassword.recover.RecoverPasswordControllerAction;
import com.commercetools.sunrise.myaccount.authentication.recoverpassword.recover.RecoverPasswordFormData;
import com.commercetools.sunrise.myaccount.authentication.recoverpassword.recover.SunriseRecoverPasswordController;
import com.commercetools.sunrise.myaccount.authentication.recoverpassword.recover.viewmodels.RecoverPasswordPageContentFactory;
import io.sphere.sdk.customers.CustomerToken;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@LogMetrics
@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        PageHeaderControllerComponentSupplier.class
})
public final class RecoverPasswordController extends SunriseRecoverPasswordController {
    private final RecoverPasswordReverseRouter recoverPasswordReverseRouter;

    @Inject
    RecoverPasswordController(final ContentRenderer contentRenderer, final FormFactory formFactory,
                              final RecoverPasswordPageContentFactory pageContentFactory,
                              final RecoverPasswordFormData formData,
                              final RecoverPasswordControllerAction controllerAction,
                              final RecoverPasswordReverseRouter recoverPasswordReverseRouter) {
        super(contentRenderer, formFactory, pageContentFactory, formData, controllerAction);
        this.recoverPasswordReverseRouter = recoverPasswordReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "my-account-forgot-password";
    }

    @Override
    public String getCmsPageKey() {
        return "default";
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final CustomerToken customerToken, final RecoverPasswordFormData formData) {
        saveMessage(MessageType.SUCCESS, "my-account:forgotPassword.feedbackMessage");
        return redirectToCall(recoverPasswordReverseRouter.requestRecoveryEmailPageCall());
    }

    @Override
    protected CompletionStage<Result> handleNotFoundEmail(final Form<? extends RecoverPasswordFormData> form) {
        saveFormError(form, "Email not found");
        return showFormPageWithErrors(null, form);
    }

    @Override
    protected CompletionStage<Result> handleEmailDeliveryException(final Form<? extends RecoverPasswordFormData> form, final EmailDeliveryException emailDeliveryException) {
        saveFormError(form, "Email delivery error");
        return internalServerErrorResultWithPageContent(createPageContent(null, form));
    }
}
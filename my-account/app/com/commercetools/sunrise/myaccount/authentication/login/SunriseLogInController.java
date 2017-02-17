package com.commercetools.sunrise.myaccount.authentication.login;

import com.commercetools.sunrise.common.controllers.SunriseTemplateFormController;
import com.commercetools.sunrise.common.controllers.WithTemplateFormFlow;
import com.commercetools.sunrise.common.pages.PageContent;
import com.commercetools.sunrise.common.template.engine.TemplateRenderer;
import com.commercetools.sunrise.framework.annotations.SunriseRoute;
import com.commercetools.sunrise.hooks.ComponentRegistry;
import com.commercetools.sunrise.myaccount.authentication.login.view.LogInPageContentFactory;
import io.sphere.sdk.client.ClientErrorException;
import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.errors.CustomerInvalidCredentials;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public abstract class SunriseLogInController<F extends LogInFormData> extends SunriseTemplateFormController implements WithTemplateFormFlow<F, Void, CustomerSignInResult> {

    private final LogInExecutor logInExecutor;
    private final LogInPageContentFactory logInPageContentFactory;

    protected SunriseLogInController(final ComponentRegistry componentRegistry, final TemplateRenderer templateRenderer,
                                     final FormFactory formFactory, final LogInExecutor logInExecutor,
                                     final LogInPageContentFactory logInPageContentFactory) {
        super(componentRegistry, templateRenderer, formFactory);
        this.logInExecutor = logInExecutor;
        this.logInPageContentFactory = logInPageContentFactory;
    }

    @Inject
    private void registerThemeLinks(final LogInThemeLinksControllerComponent themeLinksControllerComponent) {
        register(themeLinksControllerComponent);
    }

    @Override
    public String getTemplateName() {
        return "my-account-login";
    }

    @SunriseRoute("showLogInForm")
    public CompletionStage<Result> show(final String languageTag) {
        return showFormPage(null);
    }

    @SunriseRoute("processLogInForm")
    public CompletionStage<Result> process(final String languageTag) {
        return processForm(null);
    }

    @Override
    public CompletionStage<CustomerSignInResult> executeAction(final Void input, final F formData) {
        return logInExecutor.apply(formData);
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Void input, final Form<F> form, final ClientErrorException clientErrorException) {
        if (isInvalidCredentialsError(clientErrorException)) {
            saveFormError(form, "Invalid credentials"); // TODO i18n
        } else {
            saveUnexpectedFormError(form, clientErrorException);
        }
        return showFormPageWithErrors(input, form);
    }

    @Override
    public abstract CompletionStage<Result> handleSuccessfulAction(final CustomerSignInResult result, final F formData);

    @Override
    public PageContent createPageContent(final Void input, final Form<F> form) {
        return logInPageContentFactory.create(form);
    }

    @Override
    public void preFillFormData(final Void input, final F formData) {
        // Do nothing
    }

    protected final boolean isInvalidCredentialsError(final ClientErrorException clientErrorException) {
        return clientErrorException instanceof ErrorResponseException
                && ((ErrorResponseException) clientErrorException).hasErrorCode(CustomerInvalidCredentials.CODE);
    }
}

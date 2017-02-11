package com.commercetools.sunrise.myaccount.addressbook.addresslist;

import com.commercetools.sunrise.common.contexts.RequestScoped;
import com.commercetools.sunrise.common.models.AddressBeanFactory;
import com.commercetools.sunrise.common.models.ViewModelFactory;
import com.commercetools.sunrise.common.reverserouter.AddressBookSimpleReverseRouter;
import io.sphere.sdk.models.Address;

import javax.inject.Inject;
import java.util.Locale;

@RequestScoped
public class AddressInfoBeanFactory extends ViewModelFactory<AddressInfoBean, Address> {

    private final Locale locale;
    private final AddressBeanFactory addressBeanFactory;
    private final AddressBookSimpleReverseRouter addressBookReverseRouter;

    @Inject
    public AddressInfoBeanFactory(final Locale locale, final AddressBeanFactory addressBeanFactory, final AddressBookSimpleReverseRouter addressBookReverseRouter) {
        this.locale = locale;
        this.addressBeanFactory = addressBeanFactory;
        this.addressBookReverseRouter = addressBookReverseRouter;
    }

    @Override
    protected AddressInfoBean getViewModelInstance() {
        return new AddressInfoBean();
    }

    @Override
    public final AddressInfoBean create(final Address data) {
        return super.create(data);
    }

    @Override
    protected final void initialize(final AddressInfoBean model, final Address data) {
        fillAddress(model, data);
        fillAddressEditUrl(model, data);
        fillAddressDeleteUrl(model, data);
    }

    protected void fillAddress(final AddressInfoBean model, final Address address) {
        model.setAddress(addressBeanFactory.create(address));
    }

    protected void fillAddressEditUrl(final AddressInfoBean model, final Address address) {
        model.setAddressEditUrl(addressBookReverseRouter.changeAddressInAddressBookCall(locale.toLanguageTag(), address.getId()).url());
    }

    protected void fillAddressDeleteUrl(final AddressInfoBean model, final Address address) {
        model.setAddressDeleteUrl(addressBookReverseRouter.removeAddressFromAddressBookProcessFormCall(locale.toLanguageTag(), address.getId()).url());
    }
}
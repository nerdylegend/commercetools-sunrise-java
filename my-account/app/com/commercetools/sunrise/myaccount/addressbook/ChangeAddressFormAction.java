package com.commercetools.sunrise.myaccount.addressbook;

import com.commercetools.sunrise.core.FormAction;
import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultChangeAddressFormAction.class)
public interface ChangeAddressFormAction extends FormAction<ChangeAddressFormData> {

}
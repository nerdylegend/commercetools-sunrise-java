package com.commercetools.sunrise.hooks;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(HookContextImpl.class)
public interface RequestHookRunner extends HookRunner {

    CompletionStage<?> waitForComponentsToFinish();
}

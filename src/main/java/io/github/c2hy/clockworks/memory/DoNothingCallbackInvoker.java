package io.github.c2hy.clockworks.memory;

import io.github.c2hy.clockworks.core.common.CallbackInvoker;

public class DoNothingCallbackInvoker implements CallbackInvoker {
    @Override
    public void invoke(String callbackUrl, String key) {
        System.err.println("Do nothing callback invoked " + callbackUrl + " " + key);
    }
}

package com.eas.client.queries;

import com.eas.client.Callback;

public abstract class QueryCallbackAdapter implements Callback<Query>{

	@Override
    public void cancel() {
    }

}

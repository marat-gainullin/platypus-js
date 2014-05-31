package com.eas.client.queries;

import com.bearsoft.rowset.Callback;

public abstract class QueryCallbackAdapter implements Callback<Query>{

	@Override
    public void cancel() {
    }

}

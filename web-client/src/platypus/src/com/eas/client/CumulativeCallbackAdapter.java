/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mg
 */
public abstract class CumulativeCallbackAdapter<F> extends CallbackAdapter<Void, F> {

	protected List<F> reasons = new ArrayList<>();
	protected int exepectedCallsCount;
	protected int calls;

	public CumulativeCallbackAdapter(int aExpectedCallsCount) {
		super();
		exepectedCallsCount = aExpectedCallsCount;
	}

	protected void complete() {
		if (++calls == exepectedCallsCount) {
			if (reasons.isEmpty()) {
				super.onSuccess(null);
			} else {
				failed(reasons);
			}
		}
	}

	public void onSuccess(Void result) {
		complete();
	}

	public void onFailure(F reason) {
		reasons.add(reason);
		complete();
	}

	protected abstract void failed(List<F> aReasons);
}

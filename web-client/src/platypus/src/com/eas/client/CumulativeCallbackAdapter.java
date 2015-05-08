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
public abstract class CumulativeCallbackAdapter<T, F> extends CallbackAdapter<T, F> {

	protected List<F> reasons = new ArrayList<>();
	protected int exepectedCallsCount;
	protected int calls;

	public CumulativeCallbackAdapter(int aExpectedCallsCount) {
		super();
		exepectedCallsCount = aExpectedCallsCount;
	}

	public void onSuccess(T result) {
		if (++calls == exepectedCallsCount) {
			super.onSuccess(result);
		}
	}
	
	public void onFailure(F reason) {
		reasons.add(reason);
		if (++calls == exepectedCallsCount) {
			failed(reasons);
		}		
	}
	
	protected abstract void failed(List<F> aReasons);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.CallbackAdapter;

/**
 * 
 * @author mg
 */
public abstract class CumulativeCallbackAdapter<T, F> extends CallbackAdapter<T, F> {

	protected int exepectedExecutesCount;
	protected int executed;

	public CumulativeCallbackAdapter(int aExecutesCount) {
		super();
		exepectedExecutesCount = aExecutesCount;
	}

	public void onSuccess(T result) {
		if (++executed == exepectedExecutesCount) {
			super.onSuccess(result);
		}
	}
}

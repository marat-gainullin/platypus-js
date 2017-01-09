/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;

/**
 * 
 * @author mg
 */
public abstract class CallbackAdapter<T, F> implements Callback<T, F> {

	protected abstract void doWork(T aResult) throws Exception;

	@Override
	public void onSuccess(T aResult) {
		try {
			doWork(aResult);
		} catch (Exception ex) {
			Logger.getLogger(CallbackAdapter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}

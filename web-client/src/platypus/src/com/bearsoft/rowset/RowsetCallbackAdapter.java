/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.eas.client.Callback;

/**
 * 
 * @author mg
 */
public abstract class RowsetCallbackAdapter implements Callback<Rowset> {

	protected boolean cancelled;

	@Override
	public void run(Rowset aRowset) throws Exception {
		if (!cancelled)
			doWork(aRowset);
	}

	protected abstract void doWork(Rowset aRowset) throws Exception;

	@Override
	public void cancel() {
		cancelled = true;
	}
}

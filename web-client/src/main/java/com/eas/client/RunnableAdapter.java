package com.eas.client;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RunnableAdapter implements Runnable {

	protected abstract void doWork() throws Exception;

	@Override
	public void run() {
		try {
			doWork();
		} catch (Exception ex) {
			Logger.getLogger(RunnableAdapter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}

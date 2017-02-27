package com.eas.application;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;

public class Entry implements EntryPoint {
	/**
	 * The entry point method, called automatically by loading a module that
	 * declares an implementing class as an entry-point
	 */
	@Override
	public void onModuleLoad() {
		try {
			Application.run();
		} catch (Exception ex) {
			Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}

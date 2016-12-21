package com.eas.ui;

import com.google.gwt.view.client.ProvidesKey;


public class ObjectKeyProvider implements ProvidesKey<Object> {

	@Override
	public String getKey(Object item) {
		String res = "";
		if(item != null)
			res = item.toString();
		return res;
	}
}

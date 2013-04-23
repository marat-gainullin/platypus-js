package com.eas.client.gxtcontrols;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class ObjectKeyProvider implements ModelKeyProvider<Object> {

	@Override
	public String getKey(Object item) {
		String res = "";
		if(item != null)
			res = item.toString();
		return res;
	}
}

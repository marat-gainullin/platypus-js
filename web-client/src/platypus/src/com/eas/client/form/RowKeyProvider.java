package com.eas.client.form;

import com.bearsoft.rowset.Row;
import com.google.gwt.view.client.ProvidesKey;

public class RowKeyProvider implements ProvidesKey<Row> {

	@Override
	public String getKey(Row item) {
		String res = "";
		for (Object key : item.getPKValues())
		{
			if (!res.isEmpty())
				res += "; ";
			res += String.valueOf(key);
		}
		return res;
	}
}

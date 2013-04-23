package com.eas.client.gxtcontrols;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.bearsoft.rowset.Row;

public class RowKeyProvider implements ModelKeyProvider<Row> {

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

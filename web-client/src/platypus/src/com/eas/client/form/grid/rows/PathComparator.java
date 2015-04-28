package com.eas.client.form.grid.rows;

import java.util.Comparator;

import com.eas.client.Utils;
import com.google.gwt.core.client.JavaScriptObject;

public class PathComparator implements Comparator<JavaScriptObject> {

	protected String field;
	protected boolean ascending = true;

	public PathComparator(String aField, boolean aAscending) {
		super();
		field = aField;
		ascending = aAscending;
	}

	protected native int oCompare(Object o1, Object o2)/*-{
		var od1 = $wnd.P.boxAsJs(o1);
		var od2 = $wnd.P.boxAsJs(o2);
		if(od1 == null && od2 == null)
			return 0;
		else if(od1 == null)
			return 1;
		else if(od2 == null)
			return -1;
		if(od1 == od2)
			return 0;
		else if(od1 > od2)
			return 1;
		else
			return -1;
	}-*/;
	
	@Override
	public int compare(JavaScriptObject o1, JavaScriptObject o2) {
		Object oData1 = Utils.getPathData(o1, field);
		Object oData2 = Utils.getPathData(o2, field);
		int res = oCompare(oData1, oData2);
		return ascending ? res : -res;
	}

}

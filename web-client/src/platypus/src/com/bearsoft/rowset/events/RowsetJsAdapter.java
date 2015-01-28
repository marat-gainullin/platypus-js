package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Utils.JsObject;
import com.google.gwt.core.client.JavaScriptObject;

public class RowsetJsAdapter extends RowsetAdapter {
	
	protected final JavaScriptObject onContentChanged;
	
	public RowsetJsAdapter(JavaScriptObject aOnContentChanged){
		super();
		onContentChanged = aOnContentChanged;
	}

	protected void contentChanged() {
		onContentChanged.<JsObject> cast().apply(null, JsObject.createArray());
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
		contentChanged();
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		contentChanged();
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		contentChanged();
	}

	@Override
	public void rowsetSorted(RowsetSortEvent event) {
		contentChanged();
	}
}

package com.eas.client.form.grid.rows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.rowset.Utils.JsObject;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ListDataProvider;

public class JsArrayDataProvider extends ListDataProvider<JavaScriptObject> implements IndexOfProvider<JavaScriptObject> {


	protected JsObject data;
	protected Map<JavaScriptObject, Integer> indicies;
	protected Runnable onResize;
	protected Runnable onLoadStart;
	protected Callback<Void, String> onError;

	public JsArrayDataProvider(Runnable aOnResize, Runnable aOnLoadStart, Callback<Void, String> aOnError) {
		super();
		onResize = aOnResize;
		onLoadStart = aOnLoadStart;
		onError = aOnError;
	}

	public JavaScriptObject getData() {
		return data;
	}

	protected void bind() {
	}

	protected void unbind() {
	}

	public void setData(JavaScriptObject aValue) {
		if (data != null)
			unbind();
		data = aValue != null ? aValue.<JsObject> cast() : null;
		if (data != null){
			rescan();
			bind();
		}
	}

	@Override
	public List<JavaScriptObject> getList() {
		return new JsArrayList(data);
	}

	protected void invalidate() {
		indicies = null;
	}

	protected void validate() {
		if (indicies == null) {
			indicies = new HashMap<>();
			List<JavaScriptObject> targetList = getList();
			for (int i = 0; i < targetList.size(); i++) {
				indicies.put(targetList.get(i), i);
			}
		}
	}

	@Override
	public int indexOf(JavaScriptObject aItem) {
		validate();
		Integer idx = indicies.get(aItem);
		return idx != null ? idx.intValue() : -1;
	}

	@Override
	public void rescan() {
		invalidate();
		validate();
	}
}

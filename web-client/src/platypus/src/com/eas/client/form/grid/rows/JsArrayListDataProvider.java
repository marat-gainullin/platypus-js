package com.eas.client.form.grid.rows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.beans.PropertyChangeEvent;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ListDataProvider;

public class JsArrayListDataProvider extends ListDataProvider<JavaScriptObject> implements IndexOfProvider<JavaScriptObject>, JsDataContainer {

	protected JsObject data;
	protected HandlerRegistration boundToData;
	protected Map<JavaScriptObject, Integer> indicies;
	protected Runnable onResize;
	protected Runnable onLoadStart;
	protected Callback<Void, String> onError;

	public JsArrayListDataProvider(Runnable aOnResize, Runnable aOnLoadStart, Callback<Void, String> aOnError) {
		super();
		onResize = aOnResize;
		onLoadStart = aOnLoadStart;
		onError = aOnError;
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	protected boolean readdQueued;

	private void enqueueReadd() {
		readdQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (readdQueued) {
					readdQueued = false;
					getList().clear();
					if (data != null)
						getList().addAll(new JsArrayList(data));
					if (onResize != null)
						onResize.run();
				}
			}
		});
	}

	protected void bind() {
		if (data != null) {
			boundToData = Utils.listen(data, "length", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					enqueueReadd();
				}
			});
			enqueueReadd();
		}
	}

	@Override
	public void flush() {
		super.flush();
	}

	protected void unbind() {
		if (boundToData != null) {
			boundToData.removeHandler();
			boundToData = null;
			enqueueReadd();
		}
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue != null ? aValue.<JsObject> cast() : null;
			bind();
		}
	}

	protected void invalidate() {
		indicies = null;
	}

	protected void validate() {
		if (indicies == null) {
			indicies = new HashMap<>();
			List<JavaScriptObject> targetList = getList();
			if (targetList != null) {
				for (int i = 0; i < targetList.size(); i++) {
					indicies.put(targetList.get(i), i);
				}
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

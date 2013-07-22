package com.eas.client.gxtcontrols.grid.fillers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;

public class JsListStoreFiller implements ListStoreFiller<JavaScriptObject> {

	protected ListStore<JavaScriptObject> store;
	protected JsArray<JavaScriptObject> data;
	protected ListLoader<ListLoadConfig, ListLoadResult<JavaScriptObject>> loader;
	protected Callback<ListLoadResult<JavaScriptObject>, Throwable> loadCallback;
	protected int loadCallbackCounter;

	public JsListStoreFiller(ListStore<JavaScriptObject> aStore, JsArray<JavaScriptObject> aData) {
		super();
		store = aStore;
		data = aData;
		loader = new ListLoader<ListLoadConfig, ListLoadResult<JavaScriptObject>>(new DataProxy<ListLoadConfig, ListLoadResult<JavaScriptObject>>() {

			@Override
			public void load(ListLoadConfig loadConfig, Callback<ListLoadResult<JavaScriptObject>, Throwable> callback) {
				if (loadCallbackCounter++ == 0)
					loadCallback = callback;
			}

		});
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, JavaScriptObject, ListLoadResult<JavaScriptObject>>(store));
	}

	public void willLoad(){
		loader.load();
	}
	
	public void loaded() {
		if (loadCallbackCounter > 0 && --loadCallbackCounter == 0) {
			loadCallback.onSuccess(new ListLoadResult<JavaScriptObject>() {

				@Override
				public List<JavaScriptObject> getData() {
					List<JavaScriptObject> list = new ArrayList();
					if (data != null) {
						for (int i = 0; i < data.length(); i++)
							list.add(data.get(i));
					}
					return list;
				}

			});
		}
	}

	@Override
	public ListLoader<ListLoadConfig, ListLoadResult<JavaScriptObject>> getLoader() {
		return loader;
	}

	@Override
	public ListStore<JavaScriptObject> getStore() {
		return store;
	}
}

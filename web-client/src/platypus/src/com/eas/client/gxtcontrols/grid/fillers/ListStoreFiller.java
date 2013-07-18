package com.eas.client.gxtcontrols.grid.fillers;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;

public interface ListStoreFiller <O extends Object>{

	public ListLoader<ListLoadConfig, ListLoadResult<O>> getLoader();
	
	public ListStore<O> getStore();
	
	public void willLoad();
	
	public void loaded();
}

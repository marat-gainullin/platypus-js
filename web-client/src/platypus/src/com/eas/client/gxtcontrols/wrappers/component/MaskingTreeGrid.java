package com.eas.client.gxtcontrols.wrappers.component;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class MaskingTreeGrid<M> extends TreeGrid<M> {
	private HandlerRegistration loaderRegistration;
	private LoaderHandler<M, List<M>> loadHandler = new LoaderHandler<M, List<M>>() {

		@Override
		public void onBeforeLoad(BeforeLoadEvent<M> event) {
			MaskingTreeGrid.this.onLoaderBeforeLoad();
		}

		@Override
		public void onLoadException(LoadExceptionEvent<M> event) {
			MaskingTreeGrid.this.onLoaderLoadException();
		}

		@Override
		public void onLoad(LoadEvent<M, List<M>> event) {
			MaskingTreeGrid.this.onLoadLoader();
		}
	};

	public MaskingTreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn) {
		super(store, cm, treeColumn);
	}

	/**
	 * Creates a new tree grid.
	 * 
	 * @param store
	 *            the tree store
	 * @param cm
	 *            the column model
	 * @param treeColumn
	 *            the tree column
	 * @param appearance
	 *            the grid appearance
	 */
	public MaskingTreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn, GridAppearance appearance) {
		super(store, cm, treeColumn, appearance);
	}

	/**
	 * Creates a new tree grid.
	 * 
	 * @param store
	 *            the tree store
	 * @param cm
	 *            the column model
	 * @param treeColumn
	 *            the tree column
	 * @param appearance
	 *            the grid appearance
	 * @param treeAppearance
	 *            the tree appearance
	 */
	public MaskingTreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn, GridAppearance appearance, TreeAppearance treeAppearance) {
		super(store, cm, treeColumn, appearance, treeAppearance);
	}

	public void setTreeLoader(TreeLoader<M> aLoader) {
		if (loaderRegistration != null) {
			loaderRegistration.removeHandler();
			loaderRegistration = null;
		}
		super.setTreeLoader(aLoader);
		if (aLoader != null) {
			loaderRegistration = loader.addLoaderHandler(loadHandler);
		}
	}
	
	@Override
	protected void onDataChange(M parent) {
		super.onDataChange(parent);
		getStore().fireEvent(new StoreDataChangeEvent<M>());
	}	
	
}

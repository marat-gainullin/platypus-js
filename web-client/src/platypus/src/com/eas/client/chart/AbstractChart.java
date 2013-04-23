package com.eas.client.chart;

import java.util.Collections;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.RowKeyProvider;
import com.eas.client.gxtcontrols.grid.fillers.ListStoreFiller;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoaderHandler;
import com.sencha.gxt.messages.client.DefaultMessages;

public abstract class AbstractChart extends Chart<Row> {

	public static Color[] seriesColors = new Color[] { new RGB(194, 0, 36), new RGB(240, 165, 10), new RGB(32, 68, 186) };
	public static final int AXIS_LABEL_FONTSIZE = 14;
	private LoaderHandler<ListLoadConfig, ListLoadResult<Row>> loadHandler = new LoaderHandler<ListLoadConfig, ListLoadResult<Row>>() {

		@Override
		public void onBeforeLoad(BeforeLoadEvent<ListLoadConfig> event) {
			AbstractChart.this.onLoaderBeforeLoad();
		}

		@Override
		public void onLoadException(LoadExceptionEvent<ListLoadConfig> event) {
			AbstractChart.this.onLoaderLoadException();
		}

		@Override
		public void onLoad(LoadEvent<ListLoadConfig, ListLoadResult<Row>> event) {
			AbstractChart.this.onLoadLoader();
		}
	};
	//
	private boolean loadMask = true;
	private HandlerRegistration loaderRegistration;
	protected ListLoader<ListLoadConfig, ListLoadResult<Row>> loader;
	protected Entity entity;
	protected ListStoreFiller storeFiller = null;

	public AbstractChart(Entity aEntity) {
		super();
		setAnimated(true);
		setShadow(true);
		setShadowChart(true);
		entity = aEntity;
		setStore(new ListStore<Row>(new RowKeyProvider()));
		storeFiller = new ListStoreFiller(getStore(), aEntity, Collections.singleton(entity));
		final Legend<Row> legend = new Legend<Row>();
		legend.setPosition(Position.RIGHT);
		legend.setItemHighlighting(true);
		legend.setItemHiding(true);
		setLegend(legend);
		setLoader(storeFiller.getLoader());
	}

	public ListLoader<ListLoadConfig, ListLoadResult<Row>> getLoader() {
		return loader;
	}

	/**
	 * Sets the loader.
	 * 
	 * @param aLoader
	 *            the loader
	 */
	public void setLoader(ListLoader<ListLoadConfig, ListLoadResult<Row>> aLoader) {
		if (loaderRegistration != null) {
			loaderRegistration.removeHandler();
			loaderRegistration = null;
		}
		loader = aLoader;
		if (aLoader != null) {
			loaderRegistration = loader.addLoaderHandler(loadHandler);
		}
	}

	/**
	 * Returns true if the load mask in enabled.
	 * 
	 * @return the load mask state
	 */
	public boolean isLoadMask() {
		return loadMask;
	}

	public void setLoadMask(boolean aValue) {
		loadMask = aValue;
	}

	/**
	 * Invoked before the loader loads new data, displays the Loading... mask if
	 * it is enabled.
	 */
	protected void onLoaderBeforeLoad() {
		getStore().clear();
		if (isLoadMask()) {
			mask(DefaultMessages.getMessages().loadMask_msg());
		}
	}

	/**
	 * Invoked if the loader encounters an exception, cancels the Loading...
	 * mask if it is enabled.
	 */
	protected void onLoaderLoadException() {
		if (isLoadMask()) {
			unmask();
		}
	}

	/**
	 * Invoked after the loader loads new data, cancels the Loading... mask if
	 * it is enabled.
	 */
	protected void onLoadLoader() {
		if (isLoadMask()) {
			unmask();
		}
		redrawChart();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (loader != null) {
					loader.load();
				}
			}
		});
	}
}

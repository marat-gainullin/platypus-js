package com.eas.client.chart;

import java.util.Collections;

import com.bearsoft.rowset.Row;
import com.eas.client.form.Form;
import com.eas.client.gxtcontrols.RowKeyProvider;
import com.eas.client.gxtcontrols.grid.fillers.JsListStoreFiller;
import com.eas.client.gxtcontrols.grid.fillers.ListStoreFiller;
import com.eas.client.gxtcontrols.grid.fillers.RowsListStoreFiller;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
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

public abstract class AbstractChart extends Chart<Object> {

	public static Color[] seriesColors = new Color[] { new RGB(194, 0, 36), new RGB(240, 165, 10), new RGB(32, 68, 186) };
	public static final int AXIS_LABEL_FONTSIZE = 14;
	private LoaderHandler<ListLoadConfig, ListLoadResult<Object>> loadHandler = new LoaderHandler<ListLoadConfig, ListLoadResult<Object>>() {

		@Override
		public void onBeforeLoad(BeforeLoadEvent<ListLoadConfig> event) {
			AbstractChart.this.onLoaderBeforeLoad();
		}

		@Override
		public void onLoadException(LoadExceptionEvent<ListLoadConfig> event) {
			AbstractChart.this.onLoaderLoadException();
		}

		@Override
		public void onLoad(LoadEvent<ListLoadConfig, ListLoadResult<Object>> event) {
			AbstractChart.this.onLoadLoader();
		}
	};
	//
	private boolean loadMask = true;
	private HandlerRegistration loaderRegistration;
	protected ListLoader<ListLoadConfig, ListLoadResult<Object>> loader;
	protected ListStoreFiller<Object> filler;
	protected PublishedComponent jsPublished;

	protected static ListStoreFiller<? extends Object> createEntityFiller(Entity aEntity) {
		if (aEntity != null) {
			return new RowsListStoreFiller(new ListStore<Row>(new RowKeyProvider()), aEntity, Collections.singleton(aEntity));
		} else
			return null;
	}

	protected static ListStoreFiller<? extends Object> createJsArrayFiller(JsArray<JavaScriptObject> aData) {
		if (aData != null) {
			return new JsListStoreFiller(new ListStore<JavaScriptObject>(new JavaScriptObjectKeyProvider()), aData);
		} else
			return null;
	}

	public static ListStoreFiller<Object> createChartFiller(Object aData) {
		if (aData instanceof Entity)
			return (ListStoreFiller<Object>) createEntityFiller((Entity) aData);
		else if (aData instanceof JavaScriptObject)
			return (ListStoreFiller<Object>) createJsArrayFiller(((JavaScriptObject) aData).<JsArray<JavaScriptObject>> cast());
		else
			return null;
	}

	public AbstractChart(ListStoreFiller<Object> aFiller) {
		super();
		setAnimated(true);
		setShadow(true);
		setShadowChart(true);
		final Legend<Object> legend = new Legend<Object>();
		legend.setPosition(Position.RIGHT);
		legend.setItemHighlighting(true);
		legend.setItemHiding(true);
		setLegend(legend);

		filler = aFiller;
		if (filler != null) {
			onLoaderLoadException();
			bindStore(filler.getStore());
			setLoader(filler.getLoader());
			getLoader().load();
			dataChanged();
		}
	}

	public void changeDataSource(Object aDataSource) {
		filler = createChartFiller(aDataSource);
		if (filler != null) {
			onLoaderLoadException();
			bindStore(filler.getStore());
			setLoader(filler.getLoader());
			getLoader().load();
			dataChanged();
		}
	}

	public void dataWillChange() {
		if (getLoader() != null)
			getLoader().load();
	}

	public void dataChanged() {
		if (filler != null)
			filler.loaded();
	}

	public PublishedComponent getJsPublished() {
		return jsPublished;
	}

	public void setJsPublished(PublishedComponent aPublished) {
		jsPublished = aPublished;
		setData(Form.PUBLISHED_DATA_KEY, aPublished);
	}

	public ListLoader<ListLoadConfig, ListLoadResult<Object>> getLoader() {
		return loader;
	}

	/**
	 * Sets the loader.
	 * 
	 * @param aLoader
	 *            the loader
	 */
	private void setLoader(ListLoader<ListLoadConfig, ListLoadResult<Object>> aLoader) {
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

	/*
	 * @Override protected void onAttach() { super.onAttach();
	 * Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	 * 
	 * @Override public void execute() { dataWillChange(); dataChanged(); } });
	 * }
	 */
}

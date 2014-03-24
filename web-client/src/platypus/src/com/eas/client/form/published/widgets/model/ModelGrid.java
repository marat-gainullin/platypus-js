package com.eas.client.form.published.widgets.model;

import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.rowset.Row;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.GridCrossUpdaterAction;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<Row> implements HasPublished {

	protected Entity rowsSource;
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected Runnable crossUpdaterAction;
	protected CrossUpdater crossUpdater;
	protected FindWindow finder;

	/**
	 * 
	 */
	public ModelGrid() {
		super(new RowKeyProvider());
		finder = new FindWindow(this);
		crossUpdaterAction = new GridCrossUpdaterAction(this);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
	}

	protected void applyColorsFontCursor() {
		if (published.isBackgroundSet())
			ControlsUtils.applyBackground(this, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(this, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(this, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(this, published.getCursor());
	}

	public Runnable getCrossUpdaterAction() {
		return crossUpdaterAction;
	}

	public void addUpdatingTriggerEntity(Entity aTrigger) {
		crossUpdater.add(aTrigger);
	}

	public Entity getRowsSource() {
		return rowsSource;
	}

	public void setRowsSource(Entity aValue) {
		if (rowsSource != aValue) {
			rowsSource = aValue;
		}
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue != null ? aValue.<PublishedComponent> cast() : null;
		if (published != null) {
			// Here was cycle setting published to each column
		}
	}

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public void selectRow(Row aRow) {
	}

	public void unselectRow(Row aRow) {
	}

	public List<JavaScriptObject> getJsSelected() throws Exception {
		return null;
	}

	public void clearSelection() {
	}

	public boolean isEditable() {
		return false;
	}

	public void setEditable(boolean aValue) {
	}

	public boolean isDeletable() {
		return false;
	}

	public void setDeletable(boolean aValue) {
	}

	public boolean isInsertable() {
		return false;
	}

	public void setInsertable(boolean aValue) {
	}

	public boolean makeVisible(Row aRow, boolean needToSelect) {
		return false;
	}

	public void find() {
		finder.show();
		finder.toFront();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		finder.close();
	}

	public PublishedStyle complementPublishedStyle(PublishedStyle aStyle) {
		PublishedStyle complemented = aStyle;
		if (published.isBackgroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setBackground(published.getBackground());
		}
		if (published.isForegroundSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setForeground(published.getForeground());
		}
		if (published.isFontSet()) {
			if (complemented == null)
				complemented = PublishedStyle.create();
			complemented.setFont(published.getFont());
		}
		return complemented;
	}
}

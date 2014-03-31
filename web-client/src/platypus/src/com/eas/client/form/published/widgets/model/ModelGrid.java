package com.eas.client.form.published.widgets.model;

import java.util.List;

import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.rowset.Row;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.grid.FindWindow;
import com.eas.client.form.grid.GridCrossUpdaterAction;
import com.eas.client.form.grid.RowsetPositionSelectionHandler;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.grid.columns.CheckServiceColumn;
import com.eas.client.form.grid.columns.RadioServiceColumn;
import com.eas.client.form.grid.selection.MultiRowSelectionModel;
import com.eas.client.form.grid.selection.SingleRowSelectionModel;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.view.client.SelectionModel;

/**
 * Class intended to wrap a grid or tree grid. It also contains grid API.
 * 
 * @author mg
 * 
 */
public class ModelGrid extends Grid<Row> implements HasPublished {

	public static final int ROWS_HEADER_TYPE_NONE = 0;
	public static final int ROWS_HEADER_TYPE_USUAL = 1;
	public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
	public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;

	protected Entity rowsSource;
	protected JavaScriptObject onRender;
	protected PublishedComponent published;
	protected Runnable crossUpdaterAction;
	protected CrossUpdater crossUpdater;
	protected FindWindow finder;
	protected int rowsHeaderType;
	// runtime
	protected HandlerRegistration positionSelectionHandler;

	public ModelGrid() {
		super(new RowKeyProvider());
		finder = new FindWindow(this);
		crossUpdaterAction = new GridCrossUpdaterAction(this);
		crossUpdater = new CrossUpdater(crossUpdaterAction);
	}

	public int getRowsHeaderType() {
		return rowsHeaderType;
	}

	public void setRowsHeaderType(int aValue) {
		if (rowsHeaderType != aValue) {
			if (rowsHeaderType != ROWS_HEADER_TYPE_CHECKBOX && rowsHeaderType != ROWS_HEADER_TYPE_RADIOBUTTON && rowsHeaderType != ROWS_HEADER_TYPE_USUAL) {
				removeColumn(0);
			}
			rowsHeaderType = aValue;
			SelectionModel<Row> sm;
			if (rowsHeaderType == ROWS_HEADER_TYPE_CHECKBOX) {
				sm = new MultiRowSelectionModel();
				insertColumn(0, new CheckServiceColumn(sm), "\\", null);
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_RADIOBUTTON) {
				sm = new SingleRowSelectionModel();
				insertColumn(0, new RadioServiceColumn(sm), "\\", null);
			} else if (rowsHeaderType == ROWS_HEADER_TYPE_USUAL) {
				sm = new MultiRowSelectionModel();
				IdentityColumn<Row> col = new IdentityColumn<>(new RowMarkerCell(rowsSource));
				insertColumn(0, col, "\\", null);
			} else {
				sm = new MultiRowSelectionModel();
			}
			setSelectionModel(sm);
		}
	}

	@Override
	public void setSelectionModel(SelectionModel<Row> aValue) {
		assert aValue != null : "Selection model can't be null.";
		SelectionModel<? super Row> oldValue = getSelectionModel();
		if (aValue != oldValue) {
			if (positionSelectionHandler != null)
				positionSelectionHandler.removeHandler();
			setSelectionModel(aValue);
			positionSelectionHandler = aValue.addSelectionChangeHandler(new RowsetPositionSelectionHandler(rowsSource, aValue));
		}
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

package com.eas.client.form.grid.columns;

import java.util.Comparator;

import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.gwt.ui.widgets.grid.GridSection;
import com.bearsoft.gwt.ui.widgets.grid.cells.CellHasReadonly;
import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.StringEditorCell;
import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Utils.JsObject;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.Publisher;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelDecoratorBox;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ModelColumn extends GridColumn<JavaScriptObject, Object> implements FieldUpdater<JavaScriptObject, Object>, ChangesHost, HasPublished {

	protected String field;
	protected ModelDecoratorBox<Object> editor;
	protected ModelGrid grid;
	protected double designedWidth;
	protected double widthDelta;
	protected boolean resizable = true;
	protected boolean moveable = true;
	protected boolean readonly;
	protected boolean visible = true;
	protected boolean selectOnly;
	protected Comparator<JavaScriptObject> comparator;
	protected JavaScriptObject published;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;

	public ModelColumn() {
		super(new TreeExpandableCell<JavaScriptObject, Object>(new StringEditorCell()));
		if (getTargetCell() instanceof RenderedEditorCell<?>) {
			((RenderedEditorCell<Object>) getTargetCell()).setReadonly(new CellHasReadonly() {

				@Override
				public boolean isReadonly() {
					return readonly || !grid.isEditable();
				}

			});
			((RenderedEditorCell<Object>) getTargetCell()).setOnEditorClose(new RenderedEditorCell.EditorCloser() {
				@Override
				public void closed(Element aTable) {
					final GridSection<?> toFocus = GridSection.getInstance(aTable);
					if (toFocus != null) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {

							@Override
							public void execute() {
								toFocus.setFocus(true);
							}
						});
					}
				}
			});
		}
		setFieldUpdater(this);
		setDefaultSortAscending(true);
		setPublished(JavaScriptObject.createObject());
	}

	protected Cell<Object> getTargetCell() {
		return ((TreeExpandableCell<JavaScriptObject, Object>) getCell()).getCell();
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		if (grid != aValue) {
			if (grid != null) {
				grid.getSortHandler().setComparator(this, null);
			}
			grid = aValue;
			if (grid != null) {
				if (grid.getSortHandler() != null) {
					if (isSortable()) {
						grid.getSortHandler().setComparator(this, comparator);
					} else {
						grid.getSortHandler().setComparator(this, null);
					}
				}
				grid.setColumnWidth(this, getWidth(), Style.Unit.PX);
				if (visible)
					grid.showColumn(this);
				else
					grid.hideColumn(this);
			}
		}
	}

	public String getField() {
		return field;
	}

	public void setField(String aValue) {
		if (field == null ? aValue != null : !field.equals(aValue)) {
			field = aValue;
			// comparator = new RowsComparator(new
			// SortingCriterion(columnModelRef.getColIndex(), true));
		}
	}

	@Override
	public boolean isChanged(JavaScriptObject anElement) {
		return false;
	}

	@Override
	public Object getValue(JavaScriptObject anElement) {
		if (anElement != null && field != null && !field.isEmpty()) {
			return ControlsUtils.getPathData(anElement, field);
		} else
			return null;
	}

	@Override
	public void update(int aIndex, JavaScriptObject anElement, Object value) {
		if (anElement != null && field != null && !field.isEmpty()) {
			ControlsUtils.setPathData(anElement, field, value);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
			if (grid != null) {
				if (visible) {
					grid.showColumn(this);
				} else {
					grid.hideColumn(this);
				}
			}
		}
	}

	public void updateVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
		}
	}

	public double getDesignedWidth() {
		return designedWidth;
	}

	public double getWidth() {
		return designedWidth + widthDelta;
	}

	public void setWidth(double aValue) {
		if (getWidth() != aValue) {
			designedWidth = aValue;
			widthDelta = 0;
			if (grid != null) {
				grid.setColumnWidth(this, getWidth(), Style.Unit.PX);
			}
		}
	}

	public void updateWidth(double aValue) {
		if (getWidth() != aValue) {
			widthDelta = Math.max(0, aValue - designedWidth);
		}
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean aValue) {
		readonly = aValue;
	}

	@Override
	public boolean isSortable() {
		return super.isSortable();
	}

	@Override
	public void setSortable(boolean aValue) {
		super.setSortable(aValue);
		if (grid != null) {
			if (aValue) {
				grid.getSortHandler().setComparator(this, comparator);
			} else {
				grid.getSortHandler().setComparator(this, null);
			}
		}
	}

	public boolean isSelectOnly() {
		return selectOnly;
	}

	public void setSelectOnly(boolean aValue) {
		selectOnly = aValue;
	}

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		onRender = aValue;
	}

	public JavaScriptObject getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(JavaScriptObject aValue) {
		onSelect = aValue;
		if (editor != null) {
			editor.setOnSelect(onSelect);
		}
	}

	public ModelDecoratorBox<Object> getEditor() {
		return editor;
	}

	public void setEditor(ModelDecoratorBox<Object> aEditor) {
		if (editor != aEditor) {
			if (editor != null) {
				editor.setOnSelect(null);
			}
			editor = aEditor;
			if (editor != null) {
				editor.setOnSelect(onSelect);
				editor.setPublished(JavaScriptObject.createObject());
			}
		}
	}

	public static PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject aOnRender, com.google.gwt.cell.client.Cell.Context context, String aField, String aDisplay)
	        throws Exception {
		if (aOnRender != null) {
			JavaScriptObject renderedRow = renderedRow(context);
			if (renderedRow != null) {
				Object data = aField != null && !aField.isEmpty() ? ControlsUtils.getPathData(renderedRow, aField) : null;
				PublishedCell cell = Publisher.publishCell(data, aDisplay);
				JsArrayMixed args = JavaScriptObject.createArray().cast();
				args.push(JsEvents.publishOnRenderEvent(aThis, null, null, renderedRow, cell));
				aOnRender.<JsObject>cast().apply(aThis, args);
				return cell;
			}
		}
		return null;
	}

	protected static JavaScriptObject renderedRow(com.google.gwt.cell.client.Cell.Context context) throws Exception {
		Object key = context.getKey();
		return key instanceof JavaScriptObject ? (JavaScriptObject) key : null;
	}

	protected void bindGridDisplayCallback(final String aTargetElementId, final PublishedCell aCell) {
		aCell.setDisplayCallback(new Runnable() {
			@Override
			public void run() {
				Element padded = Document.get().getElementById(aTargetElementId);
				if (padded != null) {
					aCell.styleToElementBackgroundToTd(padded);
					int paddingLeft = RenderedEditorCell.CELL_PADDING;
					ImageResource icon = aCell.getStyle().getIcon();
					if (icon != null) {
						paddingLeft += icon.getWidth();
					}
					padded.getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
					String toRender = aCell.getDisplay();
					if (toRender == null)
						toRender = "&#160;";
					padded.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString(toRender));
				}
			}
		});
	}

	protected static void bindIconCallback(final PublishedStyle aStyle, final String aTargetElementId) {
		aStyle.setIconCallback(new Runnable() {

			@Override
			public void run() {
				Element padded = Document.get().getElementById(aTargetElementId);
				if (padded != null) {
					int paddingLeft = RenderedEditorCell.CELL_PADDING + (aStyle.getIcon() != null ? aStyle.getIcon().getWidth() : 0);
					padded.getStyle().setPaddingLeft(paddingLeft, Style.Unit.PX);
				}
			}

		});
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
		}
	}
}

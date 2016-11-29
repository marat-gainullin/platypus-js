package com.eas.grid.columns;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.bound.ModelCheck;
import com.eas.bound.ModelCombo;
import com.eas.bound.ModelDecoratorBox;
import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.core.Utils.JsObject;
import com.eas.grid.GridColumn;
import com.eas.grid.GridSection;
import com.eas.grid.ModelGrid;
import com.eas.grid.RenderedCellContext;
import com.eas.grid.cells.CellHasReadonly;
import com.eas.grid.cells.CellRenderer;
import com.eas.grid.cells.CheckBoxCell;
import com.eas.grid.cells.RenderedEditorCell;
import com.eas.grid.cells.TreeExpandableCell;
import com.eas.grid.rows.PathComparator;
import com.eas.ui.PublishedCell;
import com.eas.ui.EventsPublisher;
import com.eas.widgets.WidgetsUtils;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class ModelColumn extends GridColumn<JavaScriptObject, Object> implements FieldUpdater<JavaScriptObject, Object>, ChangesHost, HasPublished {

	protected String field;
	protected String sortField;
	protected ModelDecoratorBox<? extends Object> editor;
	protected ModelGrid grid;
	protected double minWidth = 15;
	protected double maxWidth = Integer.MAX_VALUE;
	protected double designedWidth = 75;
	protected double widthDelta;
	protected boolean readonly;
	protected boolean visible = true;
	protected boolean selectOnly;
	protected Comparator<JavaScriptObject> comparator;
	protected JavaScriptObject published;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;

	protected ModelColumn(Cell<Object> aCell) {
		super(aCell);
	}

	public ModelColumn() {
		super(new TreeExpandableCell<JavaScriptObject, Object>(null));
		setFieldUpdater(this);
		setDefaultSortAscending(true);
		setPublished(JavaScriptObject.createObject());
	}

	public static String renderDecorated(SafeHtmlBuilder rendered, String aId, PublishedCell aCell, SafeHtmlBuilder sb) {
		return StyleIconDecorator.decorate(rendered.toSafeHtml(), aId, aCell, HasVerticalAlignment.ALIGN_MIDDLE, sb);
	}

	protected Cell<Object> getTargetCell() {
		return ((TreeExpandableCell<JavaScriptObject, Object>) getCell()).getCell();
	}

	public Comparator<JavaScriptObject> getComparator() {
		return comparator;
	}

	public ModelGrid getGrid() {
		return grid;
	}

	public void setGrid(ModelGrid aValue) {
		if (grid != aValue) {
			if (grid != null && grid.getSortHandler() != null) {
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
			comparator = new PathComparator(sortField != null && !sortField.isEmpty() ? sortField : field, true);
			if (grid != null && grid.getSortHandler() != null) {
				grid.getSortHandler().setComparator(this, comparator);
			}
		}
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String aValue) {
		if (sortField == null ? aValue != null : !sortField.equals(aValue)) {
			sortField = aValue;
			comparator = new PathComparator(sortField != null && !sortField.isEmpty() ? sortField : field, true);
			if (grid != null && grid.getSortHandler() != null) {
				grid.getSortHandler().setComparator(this, comparator);
			}
		}
	}

	public double getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(double aValue) {
		minWidth = aValue;
	}

	public double getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(double aValue) {
		maxWidth = aValue;
	}

	@Override
	public boolean isChanged(JavaScriptObject anElement) {
		return false;
	}

	@Override
	public Object getValue(JavaScriptObject anElement) {
		if (anElement != null && field != null && !field.isEmpty()) {
			return Utils.getPathData(anElement, field);
		} else
			return null;
	}

	@Override
	public void update(int aIndex, JavaScriptObject anElement, Object value) {
		if (anElement != null && field != null && !field.isEmpty() && !readonly && grid.isEditable()) {
			Utils.setPathData(anElement, field, Utils.toJs(value));
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
			((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setVisible(aValue);
			if (grid != null) {
				if (visible) {
					grid.showColumn(this);
				} else {
					grid.hideColumn(this);
				}
				grid.onResize();
			}
		}
	}

	public void updateVisible(boolean aValue) {
		if (visible != aValue) {
			visible = aValue;
			((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setVisible(aValue);
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
		if (grid != null && grid.getSortHandler() != null) {
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
		if (onRender != aValue) {
			onRender = aValue;
			if (editor != null) {
				editor.setOnRender(onRender);
			}
		}
	}

	public JavaScriptObject getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(JavaScriptObject aValue) {
		if (onSelect != aValue) {
			onSelect = aValue;
			if (editor != null) {
				editor.setOnSelect(onSelect);
			}
		}
	}

	public ModelDecoratorBox<? extends Object> getEditor() {
		return editor;
	}

	protected boolean gridRedrawQueued;

	protected void enqueueGridRedraw() {
		gridRedrawQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (gridRedrawQueued) {
					gridRedrawQueued = false;
					if (grid != null) {
						grid.redraw();
					}
				}
			}
		});
	}

	public void setEditor(ModelDecoratorBox<? extends Object> aEditor) {
		if (editor != aEditor) {
			if (editor != null) {
				editor.setOnRender(null);
				editor.setOnSelect(null);
				if (editor instanceof ModelCombo) {
					((ModelCombo) editor).setOnRedraw(null);
				}
			}
			editor = aEditor;
			if (editor != null) {
				editor.setOnRender(onRender);
				editor.setOnSelect(onSelect);
				if (editor instanceof ModelCombo) {
					((ModelCombo) editor).setOnRedraw(new Runnable() {

						@Override
						public void run() {
							enqueueGridRedraw();
						}

					});
				}
			}
			if (editor instanceof ModelCheck) {
				((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setCell(new CheckBoxCell() {
					@Override
					public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Object value, NativeEvent event, ValueUpdater<Object> valueUpdater) {
						String type = event.getType();
						if (BrowserEvents.CHANGE.equals(type) && (readonly || !grid.isEditable())) {
							InputElement input = parent.<XElement>cast().firstChildByTagName("input").cast();
							boolean checked = input.isChecked();
							input.setChecked(!checked);
						} else
							super.onBrowserEvent(context, parent, value, event, valueUpdater);
					}

					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context, Object aValue, SafeHtmlBuilder sb) {
						try {
							if (getEditor() instanceof ModelDecoratorBox<?>) {
								ModelDecoratorBox<Object> modelEditor = (ModelDecoratorBox<Object>) getEditor();
								aValue = modelEditor.convert(Utils.toJava(aValue));
							}
							super.render(context, aValue, sb);
						} catch (Exception ex) {
							Logger.getLogger(ModelColumn.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

				});
			} else {
				((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setCell(new RenderedEditorCell<Object>(editor) {
					@Override
					protected void renderCell(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
						try {
							if (editor instanceof ModelDecoratorBox<?>) {
								ModelDecoratorBox<Object> modelEditor = (ModelDecoratorBox<Object>) getEditor();
								value = modelEditor.convert(Utils.toJava(value));
							}
							super.renderCell(context, value, sb);
						} catch (Exception ex) {
							Logger.getLogger(ModelColumn.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					@Override
					public void startEditing(com.google.gwt.cell.client.Cell.Context context, Element aBoxPositionTemplate, Element aBoxParent, Object value, ValueUpdater<Object> valueUpdater,
					        Runnable onEditorClose) {
						try {
							if (getEditor() instanceof ModelDecoratorBox<?>) {
								ModelDecoratorBox<Object> modelEditor = (ModelDecoratorBox<Object>) getEditor();
								value = modelEditor.convert(Utils.toJava(value));
							}
							grid.setActiveEditor(getEditor());
							super.startEditing(context, aBoxPositionTemplate, aBoxParent, value, valueUpdater, onEditorClose);
						} catch (Exception ex) {
							Logger.getLogger(ModelColumn.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});
				RenderedEditorCell<Object> cell = (RenderedEditorCell<Object>) getTargetCell();
				cell.setReadonly(new CellHasReadonly() {

					@Override
					public boolean isReadonly() {
						return readonly || !grid.isEditable();
					}

				});
				cell.setOnEditorClose(new RenderedEditorCell.EditorCloser() {
					@Override
					public void closed(Element aTable) {
						grid.setActiveEditor(null);
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
				cell.setRenderer(new CellRenderer<Object>() {

					@Override
					public boolean render(Context context, String aId, Object value, SafeHtmlBuilder sb) {
						JavaScriptObject onRender = ModelColumn.this.getOnRender() != null ? ModelColumn.this.getOnRender() : ModelColumn.this.getGrid().getOnRender();
						if (onRender != null) {
							if (editor instanceof ModelDecoratorBox<?>) {
								ModelDecoratorBox<Object> modelEditor = (ModelDecoratorBox<Object>) getEditor();
								value = modelEditor.convert(Utils.toJava(value));
							}
							String display = null;
							Object oldValue = ((HasValue<Object>) editor).getValue();
							((HasValue<Object>) editor).setValue(value);
							try {
								display = ((HasText) editor).getText();
							} finally {
								// We have to take care about old value because
								// of edited cell while grid rendering.
								((HasValue<Object>) editor).setValue(oldValue);
							}
							SafeHtmlBuilder lsb = new SafeHtmlBuilder();
							PublishedCell cellToRender = calcContextPublishedCell(ModelColumn.this.getPublished(), onRender, context, ModelColumn.this.getField(), display);
							if (cellToRender != null) {
								if (cellToRender.getDisplay() != null)
									display = cellToRender.getDisplay();
							}

							if (display == null)
								lsb.append(SafeHtmlUtils.fromTrustedString("&#160;"));
							else
								lsb.append(SafeHtmlUtils.fromString(display));
							grid.complementPublishedStyle(cellToRender);
							String decorId = renderDecorated(lsb, aId, cellToRender, sb);
							if (cellToRender != null) {
								if (context instanceof RenderedCellContext) {
									((RenderedCellContext) context).setPublishedCell(cellToRender);
								}
								ModelColumn.bindDisplayCallback(cellToRender, decorId);
								ModelColumn.bindIconCallback(cellToRender, decorId);
							}
							return true;
						} else {
							return false;
						}
					}

				});
			}
		}
	}

	public static PublishedCell calcContextPublishedCell(JavaScriptObject aThis, JavaScriptObject aOnRender, com.google.gwt.cell.client.Cell.Context context, String aField, String aDisplay) {
		if (aOnRender != null) {
			Object key = context.getKey();
			JavaScriptObject renderedElement = key instanceof JavaScriptObject ? (JavaScriptObject) key : null;
			if (renderedElement != null) {
				Object data = aField != null && !aField.isEmpty() ? Utils.getPathData(renderedElement, aField) : null;
				PublishedCell cell = WidgetsUtils.publishCell(data, aDisplay);
				JsArrayMixed args = JavaScriptObject.createArray().cast();
				args.push(EventsPublisher.publishOnRenderEvent(aThis, null, null, renderedElement, cell));
				aOnRender.<JsObject> cast().apply(aThis, args);
				return cell;
			}
		}
		return null;
	}

	protected static void bindDisplayCallback(final PublishedCell aCell, final String aTargetElementId) {
		aCell.setDisplayCallback(new Runnable() {
			@Override
			public void run() {
				Element identifiedTextSection = Document.get().getElementById(aTargetElementId);
				if (identifiedTextSection != null) {
					aCell.styleToElementBackgroundToTd(identifiedTextSection);
					String toRender = aCell.getDisplay();
					if (toRender == null)
						toRender = "&#160;";
					identifiedTextSection.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString(toRender));
				}
			}
		});
	}

	protected static void bindIconCallback(final PublishedCell aCell, final String aTargetElementId) {
		aCell.setIconCallback(new Runnable() {

			@Override
			public void run() {
				Element identifiedTextSection = Document.get().getElementById(aTargetElementId);
				if (identifiedTextSection != null) {
					ImageElement iconSection = (ImageElement) identifiedTextSection.getPreviousSiblingElement();
					if (iconSection != null) {
						if (aCell.getIcon() != null) {
							iconSection.setSrc(aCell.getIcon().getSafeUri().asString());
						} else {
							iconSection.setSrc(null);
						}
					}
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

	public void sort() {
		grid.addSort(this, true);
	}

	public void sortDesc() {
		grid.addSort(this, false);
	}

	public void unsort() {
		grid.unsortColumn(this);
	}

}

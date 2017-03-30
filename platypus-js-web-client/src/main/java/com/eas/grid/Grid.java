package com.eas.grid;

import java.util.ArrayList;
import java.util.List;

import com.eas.core.XElement;
import com.eas.grid.builders.NullHeaderOrFooterBuilder;
import com.eas.grid.builders.ThemedHeaderOrFooterBuilder;
import com.eas.grid.columns.ModelColumn;
import com.eas.grid.columns.header.HasSortList;
import com.eas.grid.columns.header.HeaderNode;
import com.eas.menu.MenuItemCheckBox;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.PublishedColor;
import com.eas.ui.XDataTransfer;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SetSelectionModel;

/**
 * 
 * @author mg
 * @param <T>
 */
public abstract class Grid<T> extends SimplePanel implements ProvidesResize, RequiresResize, HasSortList, Focusable {

	protected interface DynamicCellStyles extends SafeHtmlTemplates {

		public static DynamicCellStyles INSTANCE = GWT.create(DynamicCellStyles.class);

		@Template(".{0}{" + "border-style: solid;" + "border-top-width: {1}px;" + "border-bottom-width: {1}px;"
				+ "border-left-width: {2}px;" + "border-right-width: {2}px;" + "border-color: {3};}")
		public SafeHtml td(String aCssRuleName, double hBorderWidth, double vBorderWidth, String aLinesColor);

		@Template(".{0}{" + "position: relative; white-space: nowrap; height: {1}px;}")
		public SafeHtml cell(String aCssRuleName, double aRowsHeight);
	}

	public static final String GRID_SHELL_STYLE = "grid-shell";
	public static final String RULER_STYLE = "grid-ruler";
	public static final String COLUMN_PHANTOM_STYLE = "grid-column-phantom";
	public static final String COLUMNS_CHEVRON_STYLE = "grid-columns-chevron";
	public static final int MINIMUM_COLUMN_WIDTH = 15;
	//
	protected FlexTable hive;
	protected SimplePanel headerLeftContainer;
	protected GridSection<T> headerLeft;
	protected SimplePanel headerRightContainer;
	protected GridSection<T> headerRight;
	protected SimplePanel frozenLeftContainer;
	protected GridSection<T> frozenLeft;
	protected SimplePanel frozenRightContainer;
	protected GridSection<T> frozenRight;
	protected SimplePanel scrollableLeftContainer;
	protected GridSection<T> scrollableLeft;
	protected ScrollPanel scrollableRightContainer;
	protected GridSection<T> scrollableRight;
	protected SimplePanel footerLeftContainer;
	protected GridSection<T> footerLeft;
	protected ScrollPanel footerRightContainer;
	protected GridSection<T> footerRight;
	//
	protected HTML columnsChevron = new HTML();
	//
	protected final ColumnSortList sortList = new ColumnSortList();
	protected int rowsHeight;
	protected boolean showHorizontalLines = true;
	protected boolean showVerticalLines = true;
	protected boolean showOddRowsInOtherColor = true;
	protected PublishedColor gridColor;
	protected PublishedColor oddRowsColor = PublishedColor.create(241, 241, 241, 255);

	protected String dynamicTDClassName = "grid-td-" + Document.get().createUniqueId();
	protected String dynamicCellClassName = "grid-cell-" + Document.get().createUniqueId();
	protected String dynamicOddRowsClassName = "grid-odd-row-" + Document.get().createUniqueId();
	protected String dynamicEvenRowsClassName = "grid-even-row-" + Document.get().createUniqueId();
	protected StyleElement tdsStyleElement = Document.get().createStyleElement();
	protected StyleElement cellsStyleElement = Document.get().createStyleElement();
	protected StyleElement oddRowsStyleElement = Document.get().createStyleElement();
	protected StyleElement evenRowsStyleElement = Document.get().createStyleElement();

	protected ListDataProvider<T> dataProvider;

	protected int frozenColumns;
	protected int frozenRows;

	public Grid(ProvidesKey<T> aKeyProvider) {
		super();
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
		getElement().appendChild(tdsStyleElement);
		getElement().appendChild(cellsStyleElement);
		getElement().appendChild(oddRowsStyleElement);
		getElement().appendChild(evenRowsStyleElement);
		setRowsHeight(30);
		hive = new FlexTable();
		setWidget(hive);
		hive.setCellPadding(0);
		hive.setCellSpacing(0);
		hive.setBorderWidth(0);
		headerLeft = new GridSection<T>(aKeyProvider);
		headerLeftContainer = new ScrollPanel(headerLeft);
		headerRight = new GridSection<T>(aKeyProvider);
		headerRightContainer = new ScrollPanel(headerRight);
		frozenLeft = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerLeft.redrawFooters();
				frozenLeftRendered();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerLeft.redrawFooters();
				frozenLeftRendered();
			}

			@Override
			protected void onFocus() {
				super.onFocus();
				Element focused = getKeyboardSelectedElement();
				if (focused != null)
					focused.setTabIndex(tabIndex);
				FocusEvent.fireNativeEvent(Document.get().createFocusEvent(), Grid.this);
			}

			@Override
			protected void onBlur() {
				super.onBlur();
				FocusEvent.fireNativeEvent(Document.get().createBlurEvent(), Grid.this);
			}
		};

		frozenLeftContainer = new ScrollPanel(frozenLeft);
		frozenRight = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerRight.redrawFooters();
				frozenRightRendered();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerRight.redrawFooters();
				frozenRightRendered();
			}

			@Override
			protected void onFocus() {
				super.onFocus();
				Element focused = getKeyboardSelectedElement();
				if (focused != null)
					focused.setTabIndex(tabIndex);
				FocusEvent.fireNativeEvent(Document.get().createFocusEvent(), Grid.this);
			}

			@Override
			protected void onBlur() {
				super.onBlur();
				FocusEvent.fireNativeEvent(Document.get().createBlurEvent(), Grid.this);
			}
		};
		frozenRightContainer = new ScrollPanel(frozenRight);
		scrollableLeft = new GridSection<T>(aKeyProvider) {

			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerLeft.redrawFooters();
				scrollableLeftRendered();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerLeft.redrawFooters();
				scrollableLeftRendered();
			}

			@Override
			protected void onFocus() {
				super.onFocus();
				Element focused = getKeyboardSelectedElement();
				if (focused != null)
					focused.setTabIndex(tabIndex);
				FocusEvent.fireNativeEvent(Document.get().createFocusEvent(), Grid.this);
			}

			@Override
			protected void onBlur() {
				super.onBlur();
				FocusEvent.fireNativeEvent(Document.get().createBlurEvent(), Grid.this);
			}
		};
		scrollableLeftContainer = new ScrollPanel(scrollableLeft);
		scrollableRight = new GridSection<T>(aKeyProvider) {
		    
			@Override
			protected void replaceAllChildren(List<T> values, SafeHtml html) {
				super.replaceAllChildren(values, html);
				footerRight.redrawFooters();
				scrollableRightRendered();
			}

			@Override
			protected void replaceChildren(List<T> values, int start, SafeHtml html) {
				super.replaceChildren(values, start, html);
				footerRight.redrawFooters();
				scrollableRightRendered();
			}

			@Override
			protected void onFocus() {
				super.onFocus();
				Element focused = getKeyboardSelectedElement();
				if (focused != null)
					focused.setTabIndex(tabIndex);
				FocusEvent.fireNativeEvent(Document.get().createFocusEvent(), Grid.this);
			}

			@Override
			protected void onBlur() {
				super.onBlur();
				FocusEvent.fireNativeEvent(Document.get().createBlurEvent(), Grid.this);
			}
		};
		scrollableRightContainer = new ScrollPanel(scrollableRight);
		footerLeft = new GridSection<>(aKeyProvider);
		footerLeftContainer = new ScrollPanel(footerLeft);
		footerRight = new GridSection<>(aKeyProvider);
		footerRightContainer = new ScrollPanel(footerRight);
		// positioning context / overflow setup
		// overflow
		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer,
				frozenRightContainer, scrollableLeftContainer, footerLeftContainer, footerRightContainer }) {
			w.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		}
		// scrollableRightContainer.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
		// default value
		// context
		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer,
				frozenRightContainer, scrollableLeftContainer, scrollableRightContainer, footerLeftContainer,
				footerRightContainer }) {
			w.getElement().getFirstChildElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		}
		scrollableLeftContainer.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		scrollableLeftContainer.getElement().getStyle().setBottom(0, Style.Unit.PX);
		scrollableLeftContainer.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		scrollableRightContainer.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		scrollableRightContainer.getElement().getStyle().setBottom(0, Style.Unit.PX);
		scrollableRightContainer.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		// propagation of some widths
		headerLeft.setWidthPropagator(new GridWidthPropagator<T>(headerLeft) {

			@Override
			public void changed() {
				super.changed();
				propagateHeaderWidth();
			}

		});
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenLeft, frozenRight,
				scrollableLeft, scrollableRight, footerLeft, footerRight }) {
			section.setWidthPropagator(new GridWidthPropagator<>(section));
		}
		headerLeft.setColumnsPartners(new AbstractCellTable[] { frozenLeft, scrollableLeft, footerLeft });
		headerRight.setColumnsPartners(new AbstractCellTable[] { frozenRight, scrollableRight, footerRight });
		ColumnsRemover leftColumnsRemover = new ColumnsRemoverAdapter<T>(headerLeft, frozenLeft, scrollableLeft,
				footerLeft);
		ColumnsRemover rightColumnsRemover = new ColumnsRemoverAdapter<T>(headerRight, frozenRight, scrollableRight,
				footerRight);
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft,
				footerLeft }) {
			section.setColumnsRemover(leftColumnsRemover);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenRight,
				scrollableRight, footerRight }) {
			section.setColumnsRemover(rightColumnsRemover);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { frozenLeft, scrollableLeft,
				footerLeft }) {
			section.setHeaderSource(headerLeft);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { frozenRight, scrollableRight,
				footerRight }) {
			section.setHeaderSource(headerRight);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerLeft, frozenLeft,
				scrollableLeft }) {
			section.setFooterSource(footerLeft);
		}
		for (GridSection<T> section : (GridSection<T>[]) new GridSection<?>[] { headerRight, frozenRight,
				scrollableRight }) {
			section.setFooterSource(footerRight);
		}

		// hive organization
		hive.setWidget(0, 0, headerLeftContainer);
		hive.setWidget(0, 1, headerRightContainer);
		hive.setWidget(1, 0, frozenLeftContainer);
		hive.setWidget(1, 1, frozenRightContainer);
		hive.setWidget(2, 0, scrollableLeftContainer);
		scrollableLeftContainer.getElement().getParentElement().getStyle().setPosition(Style.Position.RELATIVE);
		hive.setWidget(2, 1, scrollableRightContainer);
		scrollableRightContainer.getElement().getParentElement().getStyle().setPosition(Style.Position.RELATIVE);
		hive.setWidget(3, 0, footerLeftContainer);
		hive.setWidget(3, 1, footerRightContainer);

		for (Widget w : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer,
				frozenRightContainer, scrollableLeftContainer, scrollableRightContainer, footerLeftContainer,
				footerRightContainer }) {
			w.setWidth("100%");
			w.setHeight("100%");
		}
		// misc
		for (Widget w : new Widget[] { headerRightContainer, frozenRightContainer, footerRightContainer,
				scrollableLeftContainer }) {
			w.getElement().getParentElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		}
		hive.getElement().getStyle().setTableLayout(Style.TableLayout.FIXED);
		hive.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		for (CellTable<?> tbl : new CellTable<?>[] { headerLeft, headerRight, frozenLeft, frozenRight, scrollableLeft,
				scrollableRight, footerLeft, footerRight }) {
			tbl.setTableLayoutFixed(true);
		}
		// header
		headerLeft.setHeaderBuilder(new ThemedHeaderOrFooterBuilder<T>(headerLeft, false, this));
		headerLeft.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(headerLeft, true));
		headerRight.setHeaderBuilder(new ThemedHeaderOrFooterBuilder<T>(headerRight, false, this));
		headerRight.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(headerRight, true));
		// footer
		footerLeft.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(footerLeft, false));
		footerLeft.setFooterBuilder(new ThemedHeaderOrFooterBuilder<T>(footerLeft, true));
		footerRight.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(footerRight, false));
		footerRight.setFooterBuilder(new ThemedHeaderOrFooterBuilder<T>(footerRight, true));
		// data bodies
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft,
				scrollableRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.setHeaderBuilder(new NullHeaderOrFooterBuilder<T>(gSection, false));
			gSection.setFooterBuilder(new NullHeaderOrFooterBuilder<T>(gSection, true));
		}
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, headerRight, frozenLeft, frozenRight,
				scrollableLeft, scrollableRight, footerLeft, footerRight }) {
			section.setAutoHeaderRefreshDisabled(true);
		}
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, headerRight, footerLeft, footerRight }) {
			section.setAutoFooterRefreshDisabled(true);
		}
		// cells
		installCellBuilders();

		scrollableRightContainer.addScrollHandler(new ScrollHandler() {

			@Override
			public void onScroll(ScrollEvent event) {
				int aimLeft = scrollableRightContainer.getElement().getScrollLeft();
				if (isHeaderVisible()) {
					headerRightContainer.getElement().setScrollLeft(aimLeft);
					int factLeftDelta0 = aimLeft - headerRightContainer.getElement().getScrollLeft();
					if (factLeftDelta0 > 0) {
						headerRightContainer.getElement().getStyle().setRight(factLeftDelta0, Style.Unit.PX);
					} else {
						headerRightContainer.getElement().getStyle().clearRight();
					}
				}
				if (frozenColumns > 0 || frozenRows > 0) {
					int aimTop = scrollableRightContainer.getElement().getScrollTop();

					scrollableLeftContainer.getElement().setScrollTop(aimTop);
					int factTopDelta = aimTop - scrollableLeftContainer.getElement().getScrollTop();
					if (factTopDelta > 0) {
						scrollableLeftContainer.getElement().getStyle().setBottom(factTopDelta, Style.Unit.PX);
					} else {
						scrollableLeftContainer.getElement().getStyle().setBottom(0, Style.Unit.PX);
						//scrollableLeftContainer.getElement().getStyle().clearBottom();
					}
					frozenRightContainer.getElement().setScrollLeft(aimLeft);
					int factLeftDelta1 = aimLeft - frozenRightContainer.getElement().getScrollLeft();
					if (factLeftDelta1 > 0) {
						frozenRightContainer.getElement().getStyle().setRight(factLeftDelta1, Style.Unit.PX);
					} else {
						frozenRightContainer.getElement().getStyle().clearRight();
					}
					footerRightContainer.getElement()
							.setScrollLeft(scrollableRightContainer.getElement().getScrollLeft());
					int factLeftDelta2 = aimLeft - footerRightContainer.getElement().getScrollLeft();
					if (factLeftDelta2 > 0) {
						footerRightContainer.getElement().getStyle().setRight(factLeftDelta2, Style.Unit.PX);
					} else {
						footerRightContainer.getElement().getStyle().clearRight();
					}
				}
			}

		});
		ghostLine = Document.get().createDivElement();
		ghostLine.addClassName(RULER_STYLE);
		ghostLine.getStyle().setPosition(Style.Position.ABSOLUTE);
		ghostLine.getStyle().setTop(0, Style.Unit.PX);
		ghostColumn = Document.get().createDivElement();
		ghostColumn.addClassName(COLUMN_PHANTOM_STYLE);
		ghostColumn.getStyle().setPosition(Style.Position.ABSOLUTE);
		ghostColumn.getStyle().setTop(0, Style.Unit.PX);
		addDomHandler(new DragEnterHandler() {

			@Override
			public void onDragEnter(DragEnterEvent event) {
				if (DraggedColumn.instance != null) {
					if (DraggedColumn.instance.isMove()) {
						event.preventDefault();
						event.stopPropagation();
						DraggedColumn<T> target = findTargetDraggedColumn(event.getNativeEvent().getEventTarget());
						if (target != null) {
							showColumnMoveDecorations(target);
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("move");
						} else {
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
						}
					} else {
					}
				}
			}
		}, DragEnterEvent.getType());
		addDomHandler(new DragHandler() {

			@Override
			public void onDrag(DragEvent event) {
				if (DraggedColumn.instance != null && DraggedColumn.instance.isResize()) {
					event.stopPropagation();
				}
			}
		}, DragEvent.getType());
		addDomHandler(new DragOverHandler() {

			@Override
			public void onDragOver(DragOverEvent event) {
				if (DraggedColumn.instance != null) {
					event.preventDefault();
					event.stopPropagation();
					if (DraggedColumn.instance.isMove()) {
						DraggedColumn<T> target = findTargetDraggedColumn(event.getNativeEvent().getEventTarget());
						if (target != null) {
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("move");
						} else {
							hideColumnDecorations();
							event.getDataTransfer().<XDataTransfer> cast().setDropEffect("none");
						}
					} else {
						Element hostElement = Grid.this.getElement();
						int clientX = event.getNativeEvent().getClientX();
						int hostAbsX = hostElement.getAbsoluteLeft();
						int hostScrollX = hostElement.getScrollLeft();
						int docScrollX = hostElement.getOwnerDocument().getScrollLeft();
						int relativeX = clientX - hostAbsX + hostScrollX + docScrollX;
						ghostLine.getStyle().setLeft(relativeX, Style.Unit.PX);
						ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
						if (ghostLine.getParentElement() != hostElement) {
							hostElement.appendChild(ghostLine);
						}
					}
				}
			}
		}, DragOverEvent.getType());
		addDomHandler(new DragLeaveHandler() {

			@Override
			public void onDragLeave(DragLeaveEvent event) {
				if (DraggedColumn.instance != null) {
					event.stopPropagation();
					if (DraggedColumn.instance.isMove()) {
						if (event.getNativeEvent().getEventTarget() == (JavaScriptObject) Grid.this.getElement()) {
							hideColumnDecorations();
						}
					}
				}
			}
		}, DragLeaveEvent.getType());
		addDomHandler(new DragEndHandler() {

			@Override
			public void onDragEnd(DragEndEvent event) {
				if (DraggedColumn.instance != null) {
					event.stopPropagation();
					hideColumnDecorations();
					DraggedColumn.instance = null;
				}
			}
		}, DragEndEvent.getType());
		addDomHandler(new DropHandler() {

			@Override
			public void onDrop(DropEvent event) {
				DraggedColumn<?> source = DraggedColumn.instance;
				DraggedColumn<T> target = targetDraggedColumn;
				hideColumnDecorations();
				DraggedColumn.instance = null;
				if (source != null) {
					event.preventDefault();
					event.stopPropagation();
					if (source.isMove()) {
						AbstractCellTable<T> sourceSection = (AbstractCellTable<T>) source.getTable();
						// target table may be any section in our grid
						if (target != null) {
							Header<?> sourceHeader = source.getHeader();
							Header<?> targetHeader = target.getHeader();
							if (sourceHeader instanceof DraggableHeader<?>
									&& targetHeader instanceof DraggableHeader<?>) {
								DraggableHeader<T> sourceDH = (DraggableHeader<T>) sourceHeader;
								DraggableHeader<T> targetDH = (DraggableHeader<T>) targetHeader;
								moveColumnNode(sourceDH.getHeaderNode(), targetDH.getHeaderNode());
							} else {
								int sourceIndex = source.getColumnIndex();
								int targetIndex = target.getColumnIndex();
								GridSection<T> targetSection = (GridSection<T>) target.getTable();

								boolean isSourceLeft = sourceSection == headerLeft || sourceSection == frozenLeft
										|| sourceSection == scrollableLeft || sourceSection == footerLeft;
								boolean isTargetLeft = targetSection == headerLeft || targetSection == frozenLeft
										|| targetSection == scrollableLeft || targetSection == footerLeft;
								sourceSection = isSourceLeft ? headerLeft : headerRight;
								targetSection = isTargetLeft ? headerLeft : headerRight;
								int generalSourceIndex = isSourceLeft ? sourceIndex : sourceIndex + frozenColumns;
								int generalTargetIndex = isTargetLeft ? targetIndex : targetIndex + frozenColumns;
								Header<?> header = sourceSection.getHeader(sourceIndex);
								if (header instanceof DraggableHeader) {
									((DraggableHeader) header).setTable(targetSection);
								}
								if (generalSourceIndex != generalTargetIndex) {
									Column<T, ?> column = (Column<T, ?>) source.getColumn();
									if (!(header instanceof DraggableHeader)
											|| ((DraggableHeader) header).isMoveable()) {
										moveColumn(generalSourceIndex, generalTargetIndex);
									}
								}
							}
						}
					} else {
						Header<?> header = source.getHeader();
						if (!(header instanceof DraggableHeader) || ((DraggableHeader) header).isResizable()) {
							int newWidth = Math.max(
									event.getNativeEvent().getClientX() - source.getCellElement().getAbsoluteLeft(),
									MINIMUM_COLUMN_WIDTH);
							// Source and target tables are the same, so we can
							// cast to DraggedColumn<T> with no care
							setColumnWidthFromHeaderDrag(((DraggedColumn<T>) source).getColumn(), newWidth,
									Style.Unit.PX);
						}
					}
				}
			}
		}, DropEvent.getType());

		columnsChevron.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		columnsChevron.getElement().addClassName(COLUMNS_CHEVRON_STYLE);
		getElement().appendChild(columnsChevron.getElement());
		columnsChevron.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PlatypusPopupMenu columnsMenu = new PlatypusPopupMenu();
				fillColumns(columnsMenu, headerLeft);
				fillColumns(columnsMenu, headerRight);
				columnsMenu.setPopupPosition(columnsChevron.getAbsoluteLeft(), columnsChevron.getAbsoluteTop());
				columnsMenu.showRelativeTo(columnsChevron);
			}

			private void fillColumns(MenuBar aTarget, final GridSection<T> aSection) {
				for (int i = 0; i < aSection.getColumnCount(); i++) {
					Header<?> h = aSection.getHeader(i);
					final Column<T, ?> column = aSection.getColumn(i);
					SafeHtml rendered;
					if (h.getValue() instanceof String) {
						String hVal = (String) h.getValue();
						rendered = hVal.startsWith("<html>") ? SafeHtmlUtils.fromTrustedString(hVal.substring(6))
								: SafeHtmlUtils.fromString(hVal);
					} else {
						Cell.Context context = new Cell.Context(0, i, h.getKey());
						SafeHtmlBuilder sb = new SafeHtmlBuilder();
						h.render(context, sb);
						rendered = sb.toSafeHtml();
					}
					MenuItemCheckBox miCheck = new MenuItemCheckBox(!aSection.isColumnHidden(column),
							rendered.asString(), true);
					miCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if (Boolean.TRUE.equals(event.getValue())) {
								showColumn(column);
							} else {
								hideColumn(column);
							}
							Grid.this.onResize();
						}

					});
					aTarget.addItem(miCheck);
				}
			}

		}, ClickEvent.getType());

		ColumnSortEvent.Handler sectionSortHandler = new ColumnSortEvent.Handler() {

			@Override
			public void onColumnSort(ColumnSortEvent event) {
				boolean isCtrlKey = ((GridSection<?>) event.getSource()).isCtrlKey();
				boolean contains = false;
				int containsAt = -1;
				for (int i = 0; i < sortList.size(); i++) {
					if (sortList.get(i).getColumn() == event.getColumn()) {
						contains = true;
						containsAt = i;
						break;
					}
				}
				if (!contains) {
					if (!isCtrlKey) {
						sortList.clear();
					}
					sortList.insert(sortList.size(), new ColumnSortList.ColumnSortInfo(event.getColumn(), true));
				} else {
					boolean wasAscending = sortList.get(containsAt).isAscending();
					if (!isCtrlKey) {
						sortList.clear();
						if (wasAscending) {
							sortList.push(new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
						}
					} else {
						sortList.remove(sortList.get(containsAt));
						if (wasAscending) {
							sortList.insert(containsAt, new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
						}
					}
				}
				ColumnSortEvent.fire(Grid.this, sortList);
			}
		};
		headerLeft.getColumnSortList().setLimit(1);
		headerLeft.addColumnSortHandler(sectionSortHandler);
		headerRight.getColumnSortList().setLimit(1);
		headerRight.addColumnSortHandler(sectionSortHandler);
		gridColor = PublishedColor.create(211, 211, 211, 255);
		regenerateDynamicTDStyles();
		regenerateDynamicOddRowsStyles();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
		setStyleName(GRID_SHELL_STYLE);
	}

	protected void installCellBuilders() {
		for (GridSection<?> section : new GridSection<?>[] { frozenLeft, frozenRight, scrollableLeft,
				scrollableRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.setTableBuilder(new ThemedCellTableBuilder<>(gSection, dynamicTDClassName, dynamicCellClassName,
					dynamicOddRowsClassName, dynamicEvenRowsClassName));
		}
	}

	@Override
	public ColumnSortList getSortList() {
		return sortList;
	}

	/**
	 * Add a handler to handle {@link ColumnSortEvent}s.
	 * 
	 * @param handler
	 *            the {@link ColumnSortEvent.Handler} to add
	 * @return a {@link HandlerRegistration} to remove the handler
	 */
	public HandlerRegistration addColumnSortHandler(ColumnSortEvent.Handler handler) {
		return addHandler(handler, ColumnSortEvent.getType());
	}

	public ListDataProvider<T> getDataProvider() {
		return dataProvider;
	}

	protected DraggedColumn<T> findTargetDraggedColumn(JavaScriptObject aEventTarget) {
		if (Element.is(aEventTarget)) {
			GridSection<T> targetSection = null;
			Element targetCell = null;
			Element currentTarget = Element.as(aEventTarget);
			if (COLUMN_PHANTOM_STYLE.equals(currentTarget.getClassName())
					|| RULER_STYLE.equals(currentTarget.getClassName())) {
				return targetDraggedColumn;
			}
			while ((targetCell == null || targetSection == null) && currentTarget != null
					&& currentTarget != Grid.this.getElement()) {
				if (targetCell == null) {
					if ("td".equalsIgnoreCase(currentTarget.getTagName())
							|| "th".equalsIgnoreCase(currentTarget.getTagName())) {
						targetCell = currentTarget;
					}
				}
				if (targetSection == null) {
					if (currentTarget == headerLeft.getElement()) {
						targetSection = headerLeft;
					} else if (currentTarget == frozenLeft.getElement()) {
						targetSection = frozenLeft;
					} else if (currentTarget == scrollableLeft.getElement()) {
						targetSection = scrollableLeft;
					} else if (currentTarget == footerLeft.getElement()) {
						targetSection = footerLeft;
					} else if (currentTarget == headerRight.getElement()) {
						targetSection = headerRight;
					} else if (currentTarget == frozenRight.getElement()) {
						targetSection = frozenRight;
					} else if (currentTarget == scrollableRight.getElement()) {
						targetSection = scrollableRight;
					} else if (currentTarget == footerRight.getElement()) {
						targetSection = footerRight;
					}
				}
				currentTarget = currentTarget.getParentElement();
			}
			if (targetSection != null && targetCell != null) {
				Column<T, ?> col = targetSection.getHeaderBuilder().getColumn(targetCell);
				Header<?> header = targetSection.getHeaderBuilder().getHeader(targetCell);
				if (col != null && header != null)
					return new DraggedColumn<T>(col, header, targetSection, targetCell, Element.as(aEventTarget));
				else
					return null;
			}
			return null;
		} else {
			return null;
		}
	}

	protected Element ghostLine;
	protected Element ghostColumn;
	protected DraggedColumn<T> targetDraggedColumn;

	protected void hideColumnDecorations() {
		ghostLine.removeFromParent();
		ghostColumn.removeFromParent();
		targetDraggedColumn = null;
	}

	protected void showColumnMoveDecorations(DraggedColumn<T> target) {
		targetDraggedColumn = target;
		Element hostElement = getElement();
		Element thtdElement = target.getCellElement();
		int thLeft = thtdElement.getAbsoluteLeft();
		thLeft = thLeft - getAbsoluteLeft() + hostElement.getScrollLeft();
		ghostLine.getStyle().setLeft(thLeft, Style.Unit.PX);
		ghostLine.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
		ghostColumn.getStyle().setLeft(thLeft, Style.Unit.PX);
		ghostColumn.getStyle().setWidth(thtdElement.getOffsetWidth(), Style.Unit.PX);
		ghostColumn.getStyle().setHeight(hostElement.getClientHeight(), Style.Unit.PX);
		if (ghostLine.getParentElement() != hostElement) {
			ghostLine.removeFromParent();
			hostElement.appendChild(ghostLine);
		}
		if (ghostColumn.getParentElement() != hostElement) {
			ghostColumn.removeFromParent();
			hostElement.appendChild(ghostColumn);
		}
	}

	public String getDynamicCellClassName() {
		return dynamicTDClassName;
	}

	public boolean isShowHorizontalLines() {
		return showHorizontalLines;
	}

	public void setShowHorizontalLines(boolean aValue) {
		if (showHorizontalLines != aValue) {
			showHorizontalLines = aValue;
			regenerateDynamicTDStyles();
		}
	}

	public boolean isShowVerticalLines() {
		return showVerticalLines;
	}

	public void setShowVerticalLines(boolean aValue) {
		if (showVerticalLines != aValue) {
			showVerticalLines = aValue;
			regenerateDynamicTDStyles();
		}
	}

	protected void regenerateDynamicTDStyles() {
		tdsStyleElement.setInnerSafeHtml(DynamicCellStyles.INSTANCE.td(dynamicTDClassName, showHorizontalLines ? 1 : 0,
				showVerticalLines ? 1 : 0, gridColor != null ? gridColor.toStyled() : ""));
	}

	protected void regenerateDynamicOddRowsStyles() {
		if (showOddRowsInOtherColor && oddRowsColor != null) {
			oddRowsStyleElement.setInnerHTML(
					"." + dynamicOddRowsClassName + "{background-color: " + oddRowsColor.toStyled() + "}");
		} else {
			oddRowsStyleElement.setInnerHTML("");
		}
	}

	public PublishedColor getGridColor() {
		return gridColor;
	}

	public void setGridColor(PublishedColor aValue) {
		gridColor = aValue;
		regenerateDynamicTDStyles();
	}

	public PublishedColor getOddRowsColor() {
		return oddRowsColor;
	}

	public void setOddRowsColor(PublishedColor aValue) {
		oddRowsColor = aValue;
		regenerateDynamicOddRowsStyles();
	}

	public int getRowsHeight() {
		return rowsHeight;
	}

	public void setRowsHeight(int aValue) {
		if (rowsHeight != aValue && aValue >= 10) {
			rowsHeight = aValue;
			cellsStyleElement.setInnerSafeHtml(DynamicCellStyles.INSTANCE.cell(dynamicCellClassName, rowsHeight));
			onResize();
		}
	}

	private void propagateHeaderWidth() {
		double lw = headerLeft.getElement().getOffsetWidth();
		headerLeftContainer.getElement().getParentElement().getStyle().setWidth(lw, Style.Unit.PX);
		frozenLeftContainer.getElement().getParentElement().getStyle().setWidth(lw, Style.Unit.PX);
		scrollableLeftContainer.getElement().getParentElement().getStyle().setWidth(lw, Style.Unit.PX);
		footerLeftContainer.getElement().getParentElement().getStyle().setWidth(lw, Style.Unit.PX);
		double rw = getElement().getClientWidth() - lw;
		headerRightContainer.getElement().getParentElement().getStyle().setWidth(rw, Style.Unit.PX);
		frozenRightContainer.getElement().getParentElement().getStyle().setWidth(rw, Style.Unit.PX);
		scrollableRightContainer.getElement().getParentElement().getStyle().setWidth(rw, Style.Unit.PX);
		footerRightContainer.getElement().getParentElement().getStyle().setWidth(rw, Style.Unit.PX);
	}

	protected void propagateHeightButScrollable() {
		int r0Height = Math.max(headerLeft.getOffsetHeight(), headerRight.getOffsetHeight());
		headerLeftContainer.getElement().getParentElement().getStyle().setHeight(r0Height, Style.Unit.PX);
		headerRightContainer.getElement().getParentElement().getStyle().setHeight(r0Height, Style.Unit.PX);
		int r1Height = Math.max(frozenLeft.getOffsetHeight(), frozenRight.getOffsetHeight());
		frozenLeftContainer.getElement().getParentElement().getStyle().setHeight(r1Height, Style.Unit.PX);
		frozenRightContainer.getElement().getParentElement().getStyle().setHeight(r1Height, Style.Unit.PX);
		int r3Height = Math.max(footerLeft.getOffsetHeight(), footerRight.getOffsetHeight());
		footerLeftContainer.getElement().getParentElement().getStyle().setHeight(r3Height, Style.Unit.PX);
		footerRightContainer.getElement().getParentElement().getStyle().setHeight(r3Height, Style.Unit.PX);
		// special care about scrollable
		// row height is free, but cells' height is setted by hand in order to
		// support gecko browsers
		// scrollableLeftContainer.getElement().getParentElement().getStyle().setHeight(100,
		// Style.Unit.PCT);
		// scrollableRightContainer.getElement().getParentElement().getStyle().setHeight(100,
		// Style.Unit.PCT);
		// some code for opera...
		//scrollableLeftContainer.getElement().getStyle().clearHeight();
		//scrollableRightContainer.getElement().getStyle().clearHeight();
		// it seems that after clearing the height, hive offsetHeight is changed
		// ...
		//scrollableLeftContainer.getElement().getStyle()
		//		.setHeight(hive.getElement().getOffsetHeight() - r0Height - r1Height - r3Height, Style.Unit.PX);
		//scrollableRightContainer.getElement().getStyle()
		//		.setHeight(hive.getElement().getOffsetHeight() - r0Height - r1Height - r3Height, Style.Unit.PX);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		adopt(columnsChevron);
	}

	@Override
	protected void onDetach() {
		orphan(columnsChevron);
		super.onDetach();
	}

	protected void onColumnsResize() {
		// no op here because of natural columns width's
	}

	@Override
	public void onResize() {
		if (isAttached()) {
			hive.setSize("100%", "100%");//(getElement().getClientWidth() + "px", getElement().getClientHeight() + "px");
			propagateHeaderWidth();
			onColumnsResize();
			propagateHeightButScrollable();
			// columnsChevron.setHeight(Math.max(headerLeftContainer.getOffsetHeight(),
			// headerRightContainer.getOffsetHeight()) + "px");
			for (Widget child : new Widget[] { headerLeftContainer, headerRightContainer, frozenLeftContainer,
					frozenRightContainer, scrollableLeftContainer, scrollableRightContainer }) {
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}
		}
	}

	public boolean isHeaderVisible() {
		return !Style.Display.NONE.equals(headerLeft.getElement().getStyle().getDisplay())
				&& !Style.Display.NONE.equals(headerRight.getElement().getStyle().getDisplay());
	}

	public void setHeaderVisible(boolean aValue) {
		hive.getRowFormatter().setVisible(0, aValue);
		if (aValue) {
			columnsChevron.getElement().getStyle().clearDisplay();
			headerLeftContainer.getElement().getStyle().clearDisplay();
			headerRightContainer.getElement().getStyle().clearDisplay();
		} else {
			columnsChevron.getElement().getStyle().setDisplay(Style.Display.NONE);
			headerLeftContainer.getElement().getStyle().setDisplay(Style.Display.NONE);
			headerRightContainer.getElement().getStyle().setDisplay(Style.Display.NONE);
		}
		if (isAttached())
			onResize();
	}

	public int getFrozenColumns() {
		return frozenColumns;
	}

	public void setFrozenColumns(int aValue) {
		if (aValue >= 0 && frozenColumns != aValue) {
			if (aValue >= 0) {
				frozenColumns = aValue;
				if (getDataColumnCount() > 0 && aValue <= getDataColumnCount()) {
					refreshColumns();
				}
			}
		}
	}

	public int getFrozenRows() {
		return frozenRows;
	}

	public void setFrozenRows(int aValue) {
		if (aValue >= 0 && frozenRows != aValue) {
			frozenRows = aValue;
			if (dataProvider != null && aValue <= dataProvider.getList().size()) {
				setupVisibleRanges();
			}
		}
	}

	public boolean isShowOddRowsInOtherColor() {
		return showOddRowsInOtherColor;
	}

	public void setShowOddRowsInOtherColor(boolean aValue) {
		if (showOddRowsInOtherColor != aValue) {
			showOddRowsInOtherColor = aValue;
			regenerateDynamicOddRowsStyles();
		}
	}

	/**
	 * 
	 * @param sModel
	 */
	public void setSelectionModel(SelectionModel<T> sModel) {
		headerLeft.setSelectionModel(sModel);
		headerRight.setSelectionModel(sModel);
		frozenLeft.setSelectionModel(sModel);
		frozenRight.setSelectionModel(sModel);
		scrollableLeft.setSelectionModel(sModel);
		scrollableRight.setSelectionModel(sModel);
	}

	public SetSelectionModel<? super T> getSelectionModel() {
		return (SetSelectionModel<? super T>)scrollableRight.getSelectionModel();
	}

	/**
	 * @param aDataProvider
	 */
	public void setDataProvider(ListDataProvider<T> aDataProvider) {
		if (dataProvider != aDataProvider) {
			unbindDataProvider();
			dataProvider = aDataProvider;
			bindDataProvider();
			setupVisibleRanges();
		}
	}

	protected void unbindDataProvider() {
		if (dataProvider != null) {
			dataProvider.removeDataDisplay(headerLeft);
			dataProvider.removeDataDisplay(headerRight);
			dataProvider.removeDataDisplay(frozenLeft);
			dataProvider.removeDataDisplay(frozenRight);
			dataProvider.removeDataDisplay(scrollableLeft);
			dataProvider.removeDataDisplay(scrollableRight);
			dataProvider.removeDataDisplay(footerLeft);
			dataProvider.removeDataDisplay(footerRight);
		}
	}

	protected void bindDataProvider() {
		if (dataProvider != null) {
			dataProvider.addDataDisplay(headerLeft);
			dataProvider.addDataDisplay(headerRight);
			dataProvider.addDataDisplay(frozenLeft);
			dataProvider.addDataDisplay(frozenRight);
			dataProvider.addDataDisplay(scrollableLeft);
			dataProvider.addDataDisplay(scrollableRight);
			dataProvider.addDataDisplay(footerLeft);
			dataProvider.addDataDisplay(footerRight);
		}
	}

	public void setupVisibleRanges() {
		List<T> list = dataProvider != null ? dataProvider.getList() : null;
		int generalLength = list != null ? list.size() : 0;
		int lfrozenRows = generalLength >= frozenRows ? frozenRows : generalLength;
                if(lfrozenRows == 0){
                    hive.getRowFormatter().setVisible(1, false);
                } else {
                    hive.getRowFormatter().setVisible(1, true);
                }
		int scrollableRowCount = generalLength - lfrozenRows;
		//
		headerLeft.setVisibleRange(new Range(0, 0));
		headerRight.setVisibleRange(new Range(0, 0));
		frozenLeft.setVisibleRange(new Range(0, lfrozenRows));
		frozenRight.setVisibleRange(new Range(0, lfrozenRows));
		scrollableLeft.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
		scrollableRight.setVisibleRange(new Range(lfrozenRows, scrollableRowCount >= 0 ? scrollableRowCount : 0));
		footerLeft.setVisibleRange(new Range(0, 0));
		footerRight.setVisibleRange(new Range(0, 0));
                //Since footerLeft and footerRight have bpth zero range,
                //hide them entirely
		hive.getRowFormatter().setVisible(3, false);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onResize();
			}

		});
	}

	public void addColumn(Column<T, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		addColumn(false, getDataColumnCount(), aColumn, aWidth, aHeader, aFooter, hidden);
	}

	public void addColumn(int aIndex, Column<T, ?> aColumn, String aWidth, Header<?> aHeader, Header<?> aFooter,
			boolean hidden) {
		addColumn(true, aIndex, aColumn, aWidth, aHeader, aFooter, hidden);
	}

	public void addColumn(boolean forceRefreshColumns, int aIndex, Column<T, ?> aColumn, String aWidth,
			Header<?> aHeader, Header<?> aFooter, boolean hidden) {
		/*
		 * if (aHeader instanceof DraggableHeader<?>) { DraggableHeader<T> h =
		 * (DraggableHeader<T>) aHeader; h.setColumn(aColumn); } WARNING! Before
		 * uncomment, answer the question: DraggableHeader can change its
		 * column?
		 */
		if (aIndex < frozenColumns) {
			if (aHeader instanceof DraggableHeader<?>) {
				DraggableHeader<T> h = (DraggableHeader<T>) aHeader;
				h.setTable(headerLeft);
			}
			headerLeft.insertColumn(aIndex, aColumn, aHeader);
			frozenLeft.insertColumn(aIndex, aColumn);
			scrollableLeft.insertColumn(aIndex, aColumn);
			footerLeft.insertColumn(aIndex, aColumn, null, aFooter);
			headerLeft.setColumnWidth(aColumn, aWidth);// column partners will
														// take care of width
														// seetings in other
														// sections
			//
			if (forceRefreshColumns)
				refreshColumns();
		} else {
			if (aHeader instanceof DraggableHeader<?>) {
				DraggableHeader<T> h = (DraggableHeader<T>) aHeader;
				h.setTable(headerRight);
			}
			headerRight.insertColumn(aIndex - frozenColumns, aColumn, aHeader);
			frozenRight.insertColumn(aIndex - frozenColumns, aColumn);
			scrollableRight.insertColumn(aIndex - frozenColumns, aColumn);
			footerRight.insertColumn(aIndex - frozenColumns, aColumn, null, aFooter);
			headerRight.setColumnWidth(aColumn, aWidth);// column partners will
														// take care of width
														// seetings in other
														// sections
			//
		}
		if (hidden) {
			hideColumn(aColumn);
		}
	}

	public void moveColumn(int aFromIndex, int aToIndex) {
		Header<?> footer = getColumnFooter(aFromIndex);
		Header<?> header = getColumnHeader(aFromIndex);
		String width = getColumnWidth(aFromIndex);
		Column<T, ?> column = getColumn(aFromIndex);
		clearColumnWidth(aFromIndex);
		removeColumn(aFromIndex);
		addColumn(aToIndex, column, width, header, footer, false);
		headerLeft.getWidthPropagator().changed();
	}

	public void moveColumnNode(HeaderNode<T> aSubject, HeaderNode<T> aInsertBefore) {
	}

	public void hideColumn(Column<T, ?> aColumn) {
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft, footerLeft,
				headerRight, frozenRight, scrollableRight, footerRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.hideColumn(aColumn);
		}
	}

	public void showColumn(Column<T, ?> aColumn) {
		for (GridSection<?> section : new GridSection<?>[] { headerLeft, frozenLeft, scrollableLeft, footerLeft,
				headerRight, frozenRight, scrollableRight, footerRight }) {
			GridSection<T> gSection = (GridSection<T>) section;
			gSection.showColumn(aColumn);
		}
	}

	/*
	 * public void insertColumn(int aIndex, Column<T, ?> aColumn, String
	 * aHeaderValue, Header<?> aFooter) { if (aIndex < frozenColumns) {
	 * headerLeft.insertColumn(aIndex, aColumn, new
	 * DraggableHeader<T>(aHeaderValue, headerLeft, aColumn, getElement()));
	 * frozenLeft.insertColumn(aIndex, aColumn);
	 * scrollableLeft.insertColumn(aIndex, aColumn);
	 * footerLeft.insertColumn(aIndex, aColumn, null, aFooter);
	 * refreshColumns(); } else { headerRight.insertColumn(aIndex, aColumn, new
	 * DraggableHeader<T>(aHeaderValue, headerRight, aColumn, getElement()));
	 * frozenRight.insertColumn(aIndex, aColumn);
	 * scrollableRight.insertColumn(aIndex, aColumn);
	 * footerRight.insertColumn(aIndex, aColumn, null, aFooter); } }
	 */
	public void removeColumn(int aIndex) {
		if (aIndex < frozenColumns) {
			headerLeft.removeColumn(aIndex);// ColumnsRemover will care
											// about columns sharing
			refreshColumns();
		} else {
			headerRight.removeColumn(aIndex - frozenColumns);// ColumnsRemover
																// will care
			// about columns sharing
		}
	}

	protected void refreshColumns() {
		List<Column<T, ?>> cols = new ArrayList<>();
		List<Header<?>> headers = new ArrayList<>();
		List<Header<?>> footers = new ArrayList<>();
		List<String> widths = new ArrayList<>();
		List<Boolean> hidden = new ArrayList<>();
		for (int i = headerRight.getColumnCount() - 1; i >= 0; i--) {
			Column<T, ?> col = headerRight.getColumn(i);
			cols.add(0, col);
			widths.add(0, headerRight.getColumnWidth(col, true));
			headers.add(0, headerRight.getHeader(i));
			footers.add(0, footerRight.getFooter(i));
			hidden.add(0, headerRight.isColumnHidden(col));
			headerRight.removeColumn(i);// ColumnsRemover will care about
										// columns sharing
		}
		for (int i = headerLeft.getColumnCount() - 1; i >= 0; i--) {
			Column<T, ?> col = headerLeft.getColumn(i);
			cols.add(0, col);
			widths.add(0, headerLeft.getColumnWidth(col, true));
			headers.add(0, headerLeft.getHeader(i));
			footers.add(0, footerLeft.getFooter(i));
			hidden.add(0, headerLeft.isColumnHidden(col));
			headerLeft.removeColumn(i);// ColumnsRemover will care about
										// columns sharing
		}
		headerLeft.setWidth("0px", true);
		frozenLeft.setWidth("0px", true);
		scrollableLeft.setWidth("0px", true);
		footerLeft.setWidth("0px", true);
		headerRight.setWidth("0px", true);
		frozenRight.setWidth("0px", true);
		scrollableRight.setWidth("0px", true);
		footerRight.setWidth("0px", true);
		for (int i = 0; i < cols.size(); i++) {
			Column<T, ?> col = cols.get(i);
			Header<?> h = headers.get(i);
			Header<?> f = footers.get(i);
			String w = widths.get(i);
			Boolean b = hidden.get(i);
			addColumn(col, w, h, f, b);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				onResize();
			}

		});
	}

	public void setColumnWidthFromHeaderDrag(Column<T, ?> aColumn, double aWidth, Style.Unit aUnit) {
		setColumnWidth(aColumn, aWidth, aUnit);
		propagateHeightButScrollable();
	}

	public void setColumnWidth(Column<T, ?> aColumn, double aWidth, Style.Unit aUnit) {
		if (headerLeft.getColumnIndex(aColumn) != -1) {
			headerLeft.setColumnWidth(aColumn, aWidth, aUnit);
			frozenLeft.setColumnWidth(aColumn, aWidth, aUnit);
			scrollableLeft.setColumnWidth(aColumn, aWidth, aUnit);
			footerLeft.setColumnWidth(aColumn, aWidth, aUnit);
		} else if (headerRight.getColumnIndex(aColumn) != -1) {
			headerRight.setColumnWidth(aColumn, aWidth, aUnit);
			frozenRight.setColumnWidth(aColumn, aWidth, aUnit);
			scrollableRight.setColumnWidth(aColumn, aWidth, aUnit);
			footerRight.setColumnWidth(aColumn, aWidth, aUnit);
		} else {
			// Logger.getLogger(Grid.class.getName()).log(Level.WARNING,
			// "Unknown column is met while setting column width");
		}
	}

	public void redrawRow(int index) {
		frozenLeft.redrawRow(index);
		frozenRight.redrawRow(index);
		scrollableLeft.redrawRow(index);
		scrollableRight.redrawRow(index);
	}

	public void redraw() {
		headerRight.redraw();
		frozenLeft.redraw();
		frozenRight.redraw();
		scrollableLeft.redraw();
		scrollableRight.redraw();
		footerLeft.redraw();
		footerRight.redraw();
	}

	public void redrawHeaders() {
		headerLeft.redrawHeaders();
		headerRight.redrawHeaders();
	}

	public void redrawFooters() {
		footerLeft.redrawFooters();
		footerRight.redrawFooters();
	}

	public int getDataColumnCount() {
		return (headerLeft != null ? headerLeft.getColumnCount() : 0)
				+ (headerRight != null ? headerRight.getColumnCount() : 0);
	}

	public Column<T, ?> getDataColumn(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumn(aIndex)
					: headerRight.getColumn(aIndex - headerLeft.getColumnCount());
		} else
			return null;
	}

	public Header<?> getColumnHeader(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getHeader(aIndex)
					: headerRight.getHeader(aIndex - headerLeft.getColumnCount());
		} else
			return null;
	}

	public Column<T, ?> getColumn(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumn(aIndex)
					: headerRight.getColumn(aIndex - headerLeft.getColumnCount());
		} else
			return null;
	}

	public String getColumnWidth(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			Column<T, ?> col = getColumn(aIndex);
			return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getColumnWidth(col)
					: headerRight.getColumnWidth(col);
		} else
			return null;
	}

	public Header<?> getColumnFooter(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			return aIndex >= 0 && aIndex < headerLeft.getColumnCount() ? headerLeft.getFooter(aIndex)
					: headerRight.getFooter(aIndex - headerLeft.getColumnCount());
		} else
			return null;
	}

	public void clearColumnWidth(int aIndex) {
		if (aIndex >= 0 && aIndex < getDataColumnCount()) {
			Column<T, ?> col = getColumn(aIndex);
			if (aIndex >= 0 && aIndex < headerLeft.getColumnCount()) {
				headerLeft.clearColumnWidth(aIndex);
				headerLeft.clearColumnWidth(col);
			} else {
				headerRight.clearColumnWidth(aIndex - headerLeft.getColumnCount());
				headerRight.clearColumnWidth(col);
			}
		}
	}

	public TableCellElement getViewCell(int aRow, int aCol) {
		GridSection<T> targetSection;
		if (aRow < frozenRows) {
			if (aCol < frozenColumns) {
				targetSection = frozenLeft;
			} else {
				aCol -= frozenColumns;
				targetSection = frozenRight;
			}
		} else {
			aRow -= frozenRows;
			if (aCol < frozenColumns) {
				targetSection = scrollableLeft;
			} else {
				aCol -= frozenColumns;
				targetSection = scrollableRight;
			}
		}
		return targetSection.getCell(aRow, aCol);
	}

	public void focusViewCell(int aRow, int aCol) {
		GridSection<T> targetSection;
		if (aRow < frozenRows) {
			if (aCol < frozenColumns) {
				targetSection = frozenLeft;
			} else {
				aCol -= frozenColumns;
				targetSection = frozenRight;
			}
		} else {
			aRow -= frozenRows;
			if (aCol < frozenColumns) {
				targetSection = scrollableLeft;
			} else {
				aCol -= frozenColumns;
				targetSection = scrollableRight;
			}
		}
		targetSection.focusCell(aRow, aCol);
	}

	public void unsort() {
		sortList.clear();
		ColumnSortEvent.fire(Grid.this, sortList);
		redrawHeaders();
	}

	public void addSort(ModelColumn aColumn, boolean isAscending) {
		if (aColumn.isSortable()) {
			boolean contains = false;
			int containsAt = -1;
			for (int i = 0; i < sortList.size(); i++) {
				if (sortList.get(i).getColumn() == aColumn) {
					contains = true;
					containsAt = i;
					break;
				}
			}
			if (contains) {
				boolean wasAscending = sortList.get(containsAt).isAscending();
				if (wasAscending == isAscending) {
					return;
				}

			}
			sortList.insert(sortList.size(), new ColumnSortList.ColumnSortInfo(aColumn, isAscending));
			ColumnSortEvent.fire(Grid.this, sortList);
			redrawHeaders();
		}
	}

	public void unsortColumn(ModelColumn aColumn) {
		if (aColumn.isSortable()) {
			boolean contains = false;
			int containsAt = -1;
			for (int i = 0; i < sortList.size(); i++) {
				if (sortList.get(i).getColumn() == aColumn) {
					contains = true;
					containsAt = i;
					break;
				}
			}
			if (contains) {
				sortList.remove(sortList.get(containsAt));
				ColumnSortEvent.fire(Grid.this, sortList);
				redrawHeaders();
			}
		}
	}

	protected int tabIndex;

	protected Element calcFocusedElement() {
		Element focusedEelement = scrollableRight.getKeyboardSelectedElement();
		if (focusedEelement == null)
			focusedEelement = scrollableLeft.getKeyboardSelectedElement();
		if (focusedEelement == null)
			focusedEelement = frozenLeft.getKeyboardSelectedElement();
		if (focusedEelement == null)
			focusedEelement = frozenRight.getKeyboardSelectedElement();
		if (focusedEelement == null)
			focusedEelement = getElement();
		return focusedEelement;
	}

	protected void frozenLeftRendered() {
		checkRenderingCompleted();
	}

	protected void frozenRightRendered() {
		checkRenderingCompleted();
	}

	protected void scrollableLeftRendered() {
		checkRenderingCompleted();
	}

	protected void scrollableRightRendered() {
		checkRenderingCompleted();
	}
	
	private ScheduledCommand renderingCompletedCommand;
	
	private void checkRenderingCompleted(){
	    renderingCompletedCommand = new ScheduledCommand() {

            @Override
            public void execute() {
                if (renderingCompletedCommand == this) {
                    renderingCompletedCommand = null;
                    renderingCompleted();
                }
            }
        };
		Scheduler.get().scheduleDeferred(renderingCompletedCommand);
	}

	protected void renderingCompleted(){
		Element focusedElement = calcFocusedElement();
		if (focusedElement == getElement())
			getElement().setTabIndex(tabIndex);
		else
			getElement().removeAttribute("tabindex");
	}
	
	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
	}

	@Override
	public void setFocus(boolean focused) {
		Element focusedElement = calcFocusedElement();
		focusedElement.setTabIndex(tabIndex);
		if (focused)
			focusedElement.focus();
		else
			focusedElement.blur();
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
		Element focusedElement = calcFocusedElement();
		focusedElement.setTabIndex(tabIndex);
	}
}

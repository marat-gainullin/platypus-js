package com.bearsoft.gwt.ui.containers;

import com.bearsoft.gwt.ui.HasImageResource;
import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.menu.MenuItemImageText;
import com.bearsoft.gwt.ui.widgets.ImageLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class TabsDecoratedPanel extends SimplePanel implements RequiresResize, ProvidesResize, IndexedPanel, HasSelectionHandlers<Widget> {

	public interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div class=\"{0}\"></div>")
		SafeHtml classedDiv(String aClasses);
	}

	private static final Template template = GWT.create(Template.class);

	protected boolean tabsOnTop = true;
	protected FlowPanel chevron = new FlowPanel();
	protected TabLayoutPanel tabs;
	protected double barHeight;
	protected Style.Unit barUnit;
	//
	protected LayoutPanel tabBarContainer;
	protected Widget tabBar;
	protected Widget tabsContent;
	//
	protected Widget selected;

	public TabsDecoratedPanel(double aBarHeight, Style.Unit aBarUnit) {
		super();
		barHeight = aBarHeight;
		barUnit = aBarUnit;
		tabs = new TabLayoutPanel(barHeight, barUnit) {

			@Override
			protected void initWidget(Widget w) {
				super.initWidget(w);
				assert w instanceof LayoutPanel;
				tabBarContainer = (LayoutPanel) w;
			}

			private void checkIndex(int index) {
				assert (index >= 0) && (index < getWidgetCount()) : "Index out of bounds";
			}

			@Override
			public void insert(Widget child, Widget tab, int beforeIndex) {
				child.getElement().getStyle().clearWidth();
				child.getElement().getStyle().clearHeight();
				//if (child instanceof FocusWidget) {
					child.getElement().getStyle().clearRight();
					child.getElement().getStyle().setWidth(100, Style.Unit.PCT);
					com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().ensureInjected();
					child.getElement().addClassName(com.bearsoft.gwt.ui.CommonResources.INSTANCE.commons().borderSized());
				//}
				super.insert(child, tab, beforeIndex);
			}

			@Override
			public void selectTab(final int index, boolean fireEvents) {
				super.selectTab(index, fireEvents);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						if (index >= 0 && index < getWidgetCount()) {
							Widget w = getWidget(index);
							if (w instanceof RequiresResize) {
								((RequiresResize) w).onResize();
							}
						}
					}

				});
			}
		};
		tabs.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selected = event.getSelectedItem() != -1 ? tabs.getWidget(event.getSelectedItem()) : null;
				SelectionEvent.fire(TabsDecoratedPanel.this, selected);
			}

		});
		tabBar = tabBarContainer.getWidget(0);
		tabsContent = tabBarContainer.getWidget(1);
		tabs.setAnimationDuration(500);
		final Button scrollLeft = new Button(template.classedDiv("tabs-chevron-left"), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int offsetLeft = tabBar.getElement().getOffsetLeft();
				if (offsetLeft < 0) {
					tabBar.getElement().getStyle().setLeft(Math.min(offsetLeft + 100, 0), Style.Unit.PX);
				}
			}
		});
		final Button scrollRight = new Button(template.classedDiv("tabs-chevron-right"), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Widget lastTab = tabs.getTabWidget(tabs.getWidgetCount() - 1);
				int rightMostX = lastTab.getParent().getElement().getOffsetLeft() + lastTab.getParent().getElement().getOffsetWidth();
				int tabBarParentWidth = tabBar.getElement().getParentElement().getOffsetWidth() - chevron.getElement().getOffsetWidth();
				int offsetLeft = tabBar.getElement().getOffsetLeft();
				int newOffsetLeft = offsetLeft - 100;
				int width = rightMostX + newOffsetLeft;
				if (width > tabBarParentWidth) {
					tabBar.getElement().getStyle().setLeft(Math.min(newOffsetLeft, 0), Style.Unit.PX);
				} else {
					tabBar.getElement().getStyle().setLeft(Math.min(tabBarParentWidth - rightMostX, 0), Style.Unit.PX);
				}
			}
		});
		Button tabsList = new Button(template.classedDiv("tabs-chevron-list"), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final PopupPanel pp = new PopupPanel();
				pp.setAutoHideEnabled(true);
				pp.setAutoHideOnHistoryEventsEnabled(true);
				pp.setAnimationEnabled(true);
				MenuBar menu = new MenuBar(true);
				for (int i = 0; i < tabs.getWidgetCount(); i++) {
					final Widget content = tabs.getWidget(i);
					Widget w = tabs.getTabWidget(i);
					if (w instanceof SimplePanel) {
						SimplePanel sp = (SimplePanel) w;
						w = sp.getWidget();
					}
					ScheduledCommand tabSelector = new ScheduledCommand() {

						@Override
						public void execute() {
							tabs.selectTab(content);
							pp.hide();
							Widget targetTab = tabs.getTabWidget(content);
							int tabCenterX = targetTab.getParent().getElement().getOffsetLeft()
									+ targetTab.getParent().getElement().getOffsetWidth() / 2;
							int tabBarParentWidth = tabBar.getElement().getParentElement().getOffsetWidth()
									- chevron.getElement().getOffsetWidth();
							int newOffsetLeft = tabBarParentWidth / 2 - tabCenterX;

							Widget lastTab = tabs.getTabWidget(tabs.getWidgetCount() - 1);
							int rightMostX = lastTab.getParent().getElement().getOffsetLeft()
									+ lastTab.getParent().getElement().getOffsetWidth();
							int width = rightMostX + newOffsetLeft;
							if (width > tabBarParentWidth) {
								tabBar.getElement().getStyle().setLeft(Math.min(newOffsetLeft, 0), Style.Unit.PX);
							} else {
								tabBar.getElement().getStyle().setLeft(Math.min(tabBarParentWidth - rightMostX, 0), Style.Unit.PX);
							}
						}

					};
					SafeUri imageUri = null;
					if (w instanceof Image) {
						Image image = (Image) w;
						imageUri = UriUtils.fromTrustedString(image.getUrl());
					} else if (w instanceof HasImageResource) {
						HasImageResource imageHost = (HasImageResource) w;
						if (imageHost.getImageResource() != null) {
							imageUri = imageHost.getImageResource().getSafeUri();
						}
					}
					if (w instanceof HasHTML) {
						HasHTML h = (HasHTML) w;
						menu.addItem(new MenuItemImageText(h.getHTML(), true, imageUri, tabSelector));
					} else if (w instanceof HasText) {
						HasText l = (HasText) w;
						menu.addItem(new MenuItemImageText(l.getText(), false, imageUri, tabSelector));
					}
				}
				pp.setWidget(menu);
				pp.showRelativeTo(chevron);
			}
		});
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
		tabs.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		tabs.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		tabs.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		setWidget(tabs);
		scrollLeft.getElement().getStyle().setPadding(0, Style.Unit.PX);
		scrollLeft.getElement().getStyle().setMargin(0, Style.Unit.PX);
		scrollRight.getElement().getStyle().setPadding(0, Style.Unit.PX);
		scrollRight.getElement().getStyle().setMargin(0, Style.Unit.PX);
		tabsList.getElement().getStyle().setPadding(0, Style.Unit.PX);
		tabsList.getElement().getStyle().setMargin(0, Style.Unit.PX);
		chevron.add(scrollLeft);
		chevron.add(scrollRight);
		chevron.add(tabsList);
		chevron.getElement().addClassName("tabs-chevron");
		chevron.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		assert tabBarContainer != null;
		tabBarContainer.getWidgetContainerElement(tabBar).appendChild(chevron.getElement());
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}

	public boolean isTabsOnTop() {
		return tabsOnTop;
	}

	public void setTabsOnTop(boolean aValue) {
		if (tabsOnTop != aValue) {
			tabsOnTop = aValue;
			applyTabsOnTop();
		}
	}

	protected void applyTabsOnTop() {
		if (tabBar != null && tabBarContainer != null && tabsContent != null) {
			final String tabBarLeft = tabBar.getElement().getStyle().getLeft();
			Element tabBarContainerElement = tabBarContainer.getWidgetContainerElement(tabBar);
			tabBarContainerElement.getStyle().clearTop();
			tabBarContainerElement.getStyle().clearHeight();
			tabBarContainerElement.getStyle().clearBottom();
			Element tabContentContainerElement = tabBarContainer.getWidgetContainerElement(tabsContent);
			tabContentContainerElement.getStyle().clearTop();
			tabContentContainerElement.getStyle().clearHeight();
			tabContentContainerElement.getStyle().clearBottom();
			if (tabsOnTop) {
				tabBarContainer.setWidgetTopHeight(tabBar, 0, Style.Unit.PX, barHeight, barUnit);
				tabBarContainer.setWidgetTopBottom(tabsContent, barHeight, barUnit, 0, Style.Unit.PX);
			} else {
				tabBarContainer.setWidgetBottomHeight(tabBar, 0, Style.Unit.PX, barHeight, barUnit);
				tabBarContainer.setWidgetTopBottom(tabsContent, 0, Style.Unit.PX, barHeight, barUnit);
			}
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					tabBar.getElement().getStyle().setProperty("left", tabBarLeft);
				}
			});
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		adopt(chevron);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		orphan(chevron);
	}

	@Override
	public void onResize() {
		tabs.onResize();
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param asHtml
	 *            <code>true</code> to treat the specified text as HTML
	 */
	public void add(Widget child, String text, boolean asHtml) {
		tabs.insert(child, text, asHtml, tabs.getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param asHtml
	 *            <code>true</code> to treat the specified text as HTML
	 */
	public void add(Widget child, String text, boolean asHtml, ImageResource aImage) {
		tabs.insert(child, new ImageLabel(text, asHtml, aImage), tabs.getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 */
	public void add(Widget child, String text) {
		tabs.insert(child, text, tabs.getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param html
	 *            the html to be shown on its tab
	 */
	public void add(Widget child, SafeHtml html) {
		tabs.add(child, html.asString(), true);
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param tab
	 *            the widget to be placed in the associated tab
	 */
	public void add(Widget child, Widget tab) {
		tabs.insert(child, tab, tabs.getWidgetCount());
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, int beforeIndex) {
		tabs.insert(child, "", beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param html
	 *            the html to be shown on its tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, SafeHtml html, int beforeIndex) {
		tabs.insert(child, html.asString(), true, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param asHtml
	 *            <code>true</code> to treat the specified text as HTML
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, String text, boolean asHtml, int beforeIndex) {
		Widget contents;
		if (asHtml) {
			contents = new HTML(text);
		} else {
			contents = new Label(text);
		}
		tabs.insert(child, contents, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, String text, int beforeIndex) {
		tabs.insert(child, text, false, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 * 
	 * @param child
	 *            the widget to be added
	 * @param tab
	 *            the widget to be placed in the associated tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, Widget tab, int beforeIndex) {
		tabs.insert(child, tab, beforeIndex);
	}

	/**
	 * Set the duration of the animated transition between tabs.
	 * 
	 * @param duration
	 *            the duration in milliseconds.
	 */
	public void setAnimationDuration(int duration) {
		tabs.setAnimationDuration(duration);
	}

	/**
	 * Set whether or not transitions slide in vertically or horizontally.
	 * 
	 * @param isVertical
	 *            true for vertical transitions, false for horizontal
	 */
	public void setAnimationVertical(boolean isVertical) {
		tabs.setAnimationVertical(isVertical);
	}

	/**
	 * Sets a tab's HTML contents.
	 * 
	 * Use care when setting an object's HTML; it is an easy way to expose
	 * script-based security problems. Consider using
	 * {@link #setTabHTML(int, SafeHtml)} or {@link #setTabText(int, String)}
	 * whenever possible.
	 * 
	 * @param index
	 *            the index of the tab whose HTML is to be set
	 * @param html
	 *            the tab's new HTML contents
	 */
	public void setTabHTML(int index, String html) {
		tabs.setTabHTML(index, html);
	}

	/**
	 * Sets a tab's HTML contents.
	 * 
	 * @param index
	 *            the index of the tab whose HTML is to be set
	 * @param html
	 *            the tab's new HTML contents
	 */
	public void setTabHTML(int index, SafeHtml html) {
		tabs.setTabHTML(index, html);
	}

	/**
	 * Sets a tab's text contents.
	 * 
	 * @param index
	 *            the index of the tab whose text is to be set
	 * @param text
	 *            the object's new text
	 */
	public void setTabText(int index, String text) {
		tabs.setTabText(index, text);
	}

	@Override
	public boolean remove(Widget w) {
		return tabs.remove(w);
	}

	@Override
	public Widget getWidget(int index) {
		return tabs.getWidget(index);
	}

	@Override
	public int getWidgetCount() {
		return tabs.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget child) {
		return tabs.getWidgetIndex(child);
	}

	@Override
	public boolean remove(int aIndex) {
		return tabs.remove(aIndex);
	}

	/**
	 * Programmatically selects the specified tab and fires events.
	 * 
	 * @param child
	 *            the child whose tab is to be selected
	 */
	public void selectTab(Widget child) {
		tabs.selectTab(child);
	}

	/**
	 * Programmatically selects the specified tab.
	 * 
	 * @param child
	 *            the child whose tab is to be selected
	 * @param fireEvents
	 *            true to fire events, false not to
	 */
	public void selectTab(Widget child, boolean fireEvents) {
		tabs.selectTab(child, fireEvents);
	}

	/**
	 * Programmatically selects the specified tab and fires events.
	 * 
	 * @param index
	 *            the index of the tab to be selected
	 */
	public void selectTab(int index) {
		tabs.selectTab(index);
	}

	/**
	 * Programmatically selects the specified tab.
	 * 
	 * @param index
	 *            the index of the tab to be selected
	 * @param fireEvents
	 *            true to fire events, false not to
	 */
	public void selectTab(int index, boolean fireEvents) {
		tabs.selectTab(index, fireEvents);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
}

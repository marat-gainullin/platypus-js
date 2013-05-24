package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class PlatypusTabsContainer extends SimpleContainer implements HasSelectionHandlers<Widget>, SelectionHandler<Widget> {

	protected TabPanel tabs = new TabPanel();

	public PlatypusTabsContainer() {
		super();
		getElement().getStyle().setBackgroundColor("transparent");
		tabs.setTabScroll(true);
		tabs.setAnimScroll(true);
		tabs.setCloseContextMenu(true);
		setWidget(tabs);
		tabs.addSelectionHandler(this);
	}

	@Override
	public void onSelection(SelectionEvent<Widget> event) {
		SelectionEvent.fire(this, null); 
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Widget> handler) {
	    return addHandler(handler, SelectionEvent.getType());
	}

	public void add(Component aComponent, TabItemConfig aConfig) {
		tabs.add(aComponent, aConfig);
		if (isAttached())
			forceLayout();
	}

	public void setSelectedComponent(Component aComponent) {
		if (aComponent != null)
			tabs.setActiveWidget(aComponent);
	}

	public int getSelectedIndex() {
		return tabs.getWidgetIndex(tabs.getActiveWidget());
	}

	public void setSelectedIndex(int aIndex) {
		if (aIndex >= 0 && aIndex < tabs.getWidgetCount())
			tabs.setActiveWidget(tabs.getWidget(aIndex));
	}

	public Component getSelectedComponent() {
		Widget w = tabs.getActiveWidget();
		if (w instanceof Component)
			return (Component) w;
		else
			return null;
	}

	public int getTabsCount() {
		return tabs.getWidgetCount();
	}

	public Widget getTabWidget(int aIndex) {
		return tabs.getWidget(aIndex);
	}

	public void removeTabWidget(int aIndex) {
		tabs.remove(aIndex);
	}

	public void removeTabWidget(Widget aWidget) {
		tabs.remove(aWidget);
	}

	@Override
	public void clear() {
		// tabs.clear();
		// super.clear();
		for (int i = tabs.getWidgetCount(); i > 0; i--) {
			tabs.remove(i - 1);
		}
	}
}

package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.TabsDecoratedPanel;
import com.bearsoft.gwt.ui.widgets.ImageLabel;
import com.eas.client.ImageResourceCallback;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public class TabbedPane extends TabsDecoratedPanel implements HasPublished, HasSelectionHandlers<Widget> {

	protected JavaScriptObject published;

	protected Widget selected;

	public TabbedPane() {
		super(30, Style.Unit.PX);
		tabs.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selected = event.getSelectedItem() != -1 ? tabs.getWidget(event.getSelectedItem()) : null;
			}

		});
	}
	
    public void add(Widget child, String text, boolean asHtml, PlatypusImageResource aImage) {
    	final ImageLabel tabsLabel = new ImageLabel(text, asHtml, aImage);
        tabs.insert(child, tabsLabel, tabs.getWidgetCount());
        if(aImage != null){
        	aImage.addCallback(new ImageResourceCallback() {
				
				@Override
				public void run(PlatypusImageResource aResource) {
					tabsLabel.setImage(aResource);
				}
			});
        }
    }

	public int getSelectedIndex() {
		return selected != null ? tabs.getSelectedIndex() : -1;
	}

	public void setSelectedIndex(int aIndex) {
		if (aIndex != -1)
			tabs.selectTab(aIndex);
		else
			setSelected(null);
	}

	public Widget getSelected() {
		return selected;
	}

	public void setSelected(Widget aWidget) {
		if (selected != aWidget && (aWidget == null || tabs.getWidgetIndex(aWidget) != -1)) {
			selected = aWidget;
			if (selected != null)
				tabs.selectTab(selected);
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "selectedIndex", {
			get : function() {
				return aComponent.@com.eas.client.form.published.containers.TabbedPane::getSelectedIndex()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.containers.TabbedPane::setSelectedIndex(I)(aValue);
			}
		});
		Object.defineProperty(published, "selectedComponent", {
			get : function() {
				var comp = aComponent.@com.eas.client.form.published.containers.TabbedPane::getSelected()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aValue) {
				if(aValue != null)
					aComponent.@com.eas.client.form.published.containers.TabbedPane::setSelected(Lcom/google/gwt/user/client/ui/Widget;)(aValue.unwrap());
			}
		});
		published.add = function(toAdd, aTabTitle, aTabIcon){
			if(toAdd && toAdd.unwrap){
				if(!aTabTitle)
					aTabTitle = "";
				if(!aTabIcon)
					aTabIcon = null;
				if(aTabTitle.indexOf("<html>") == 0)
					aComponent.@com.eas.client.form.published.containers.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle.substring(6), true, aTabIcon);
				else
					aComponent.@com.eas.client.form.published.containers.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle, false, aTabIcon);
			}
		};
	}-*/;
}

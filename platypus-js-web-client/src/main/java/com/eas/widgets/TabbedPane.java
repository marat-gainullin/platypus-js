package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasImageResource;
import com.eas.ui.HasJsFacade;
import com.eas.ui.events.AddEvent;
import com.eas.ui.events.AddHandler;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasAddHandlers;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasRemoveHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.RemoveEvent;
import com.eas.ui.events.RemoveHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.boxes.ImageLabel;
import com.eas.widgets.containers.TabsDecoratedPanel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedPane extends TabsDecoratedPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers,
        HasRemoveHandlers, HasChildrenPosition {

	public static class TabLabel extends FlowPanel implements HasHTML, HasImageResource {

		protected ImageLabel label;
		protected SimplePanel closer = new SimplePanel();
		protected Widget subject;

		public TabLabel(final TabLayoutPanel aParentTabs, final Widget subject, String aText, boolean asHtml, ImageResource aImage) {
			label = new ImageLabel(aText, asHtml, aImage);
			closer.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			closer.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
			closer.getElement().addClassName("tab-close-tool");
			closer.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					aParentTabs.remove(subject);
				}

			}, ClickEvent.getType());
			add(label);
			add(closer);
			label.setImageResource(aImage);
		}

		@Override
		public String getText() {
			return label.getText();
		}

		@Override
		public void setText(String text) {
			label.setText(text);
		}

		@Override
		public String getHTML() {
			return label.getHTML();
		}

		@Override
		public void setHTML(String html) {
			label.setHTML(html);
		}

		@Override
        public ImageResource getImageResource() {
	        return label.getImageResource();
        }

		@Override
        public void setImageResource(ImageResource aValue) {
			label.setImageResource(aValue);
        }

	}

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	public TabbedPane() {
		super(30, Style.Unit.PX);
		getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		getElement().getStyle().setPosition(Style.Position.RELATIVE);
	}

	@Override
	public HandlerRegistration addAddHandler(AddHandler handler) {
		return addHandler(handler, AddEvent.getType());
	}

	@Override
	public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
		return addHandler(handler, RemoveEvent.getType());
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
	}

	@Override
	public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu;
	}

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {

					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if (!oldValue && enabled) {
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public void add(final Widget child, String text, boolean asHtml, ImageResource aImage) {
		tabs.insert(child, new TabLabel(tabs, child, text, asHtml, aImage), tabs.getWidgetCount());
		AddEvent.fire(this, child);
	}

	public void add(final Widget child, TabLabel aLabel) {
		tabs.insert(child, aLabel, tabs.getWidgetCount());
		AddEvent.fire(this, child);
	}

	public TabLayoutPanel getTabs(){
		return tabs;
	}
	
	@Override
	public boolean remove(Widget w) {
		boolean res = super.remove(w);
		if (res) {
			RemoveEvent.fire(this, w);
		}
		return res;
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
				return aWidget.@com.eas.widgets.TabbedPane::getSelectedIndex()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.TabbedPane::setSelectedIndex(I)(aValue);
			}
		});
		Object.defineProperty(published, "selectedComponent", {
			get : function() {
				var comp = aWidget.@com.eas.widgets.TabbedPane::getSelected()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aValue) {
				if(aValue != null)
					aWidget.@com.eas.widgets.TabbedPane::setSelected(Lcom/google/gwt/user/client/ui/Widget;)(aValue.unwrap());
			}
		});
		published.add = function(toAdd, aTabTitle, aTabIcon){
			if(toAdd && toAdd.unwrap){
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(!aTabTitle)
					aTabTitle = "";
				if(!aTabIcon)
					aTabIcon = null;
				if(aTabTitle.indexOf("<html>") == 0)
					aWidget.@com.eas.widgets.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle.substring(6), true, aTabIcon);
				else
					aWidget.@com.eas.widgets.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle, false, aTabIcon);
			}
		};
	}-*/;

	@Override
	public int getTop(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return 0;
	}

	@Override
	public int getLeft(Widget aWidget) {
		assert aWidget.getParent() == this : "widget should be a child of this container";
		return 0;
	}
}

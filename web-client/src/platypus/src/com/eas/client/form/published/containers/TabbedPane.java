package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.HasImageResource;
import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.TabsDecoratedPanel;
import com.bearsoft.gwt.ui.widgets.ImageLabel;
import com.eas.client.ImageResourceCallback;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.AddEvent;
import com.eas.client.form.events.AddHandler;
import com.eas.client.form.events.HasAddHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasRemoveHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.RemoveEvent;
import com.eas.client.form.events.RemoveHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.VerticalAlign;
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
import com.google.gwt.user.client.ui.Widget;

public class TabbedPane extends TabsDecoratedPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasAddHandlers,
        HasRemoveHandlers, HasChildrenPosition {

	protected class TabLabel extends FlowPanel implements HasHTML, HasImageResource {

		protected ImageLabel label;
		protected SimplePanel closer = new SimplePanel();
		protected Widget subject;

		public TabLabel(final Widget subject, String aText, boolean asHtml, PlatypusImageResource aImage) {
			label = new ImageLabel(aText, asHtml, aImage);
			closer.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			closer.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
			closer.getElement().addClassName("tab-close-tool");
			closer.addDomHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					tabs.remove(subject);
				}

			}, ClickEvent.getType());
			add(label);
			add(closer);
			if (aImage != null) {
				aImage.addCallback(new ImageResourceCallback() {

					@Override
					public void run(PlatypusImageResource aResource) {
						label.setImage(aResource);
					}
				});
			}
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

	}

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;
	protected JavaScriptObject published;

	public TabbedPane() {
		super(30, Style.Unit.PX);
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

	public void add(final Widget child, String text, boolean asHtml, PlatypusImageResource aImage) {
		tabs.insert(child, new TabLabel(child, text, asHtml, aImage), tabs.getWidgetCount());
		AddEvent.fire(this, child);
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
				return aWidget.@com.eas.client.form.published.containers.TabbedPane::getSelectedIndex()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.containers.TabbedPane::setSelectedIndex(I)(aValue);
			}
		});
		Object.defineProperty(published, "selectedComponent", {
			get : function() {
				var comp = aWidget.@com.eas.client.form.published.containers.TabbedPane::getSelected()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aValue) {
				if(aValue != null)
					aWidget.@com.eas.client.form.published.containers.TabbedPane::setSelected(Lcom/google/gwt/user/client/ui/Widget;)(aValue.unwrap());
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
					aWidget.@com.eas.client.form.published.containers.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle.substring(6), true, aTabIcon);
				else
					aWidget.@com.eas.client.form.published.containers.TabbedPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;ZLcom/google/gwt/resources/client/ImageResource;)(toAdd.unwrap(), aTabTitle, false, aTabIcon);
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

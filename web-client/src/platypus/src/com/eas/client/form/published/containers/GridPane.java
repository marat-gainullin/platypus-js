/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.GridPanel;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * 
 * @author mg
 */
public class GridPane extends GridPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu, HasEventsExecutor{

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected boolean enabled = true;
	protected String name;	
	protected JavaScriptObject published;
	
	public GridPane() {
		super(1, 1);
	}
	
	public GridPane(int aRows, int aCols) {
		super(aRows, aCols);
	}
	
	public GridPane(int aRows, int aCols, int aVGap, int aHGap) {
		this(aRows, aCols);
		setHgap(aHGap);
		setVgap(aVGap);
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
		if(!oldValue && enabled){
			getElement().<XElement>cast().unmask();
		}else if(oldValue && !enabled){
			getElement().<XElement>cast().disabledMask();
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
		published.add = function(toAdd, aRow, aCol){
			if(toAdd && toAdd.unwrap){
				if(toAdd.parent == published)
					throw 'A widget already added to this container';
				if(arguments.length < 3)
					throw 'aRow and aCol are required parameters';				
				aWidget.@com.eas.client.form.published.containers.GridPane::setWidget(IILcom/google/gwt/user/client/ui/Widget;)(1 * aRow, 1 * aCol, toAdd.unwrap());
			}
		}
		published.remove = function(aChild) {
			if (aChild && aChild.unwrap) {
				aWidget.@com.eas.client.form.published.containers.GridPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.child = function(aRow, aCol) {
			if (arguments.length > 1) {
				var widget = aWidget.@com.eas.client.form.published.containers.GridPane::getWidget(II)(1 * aRow, 1 * aCol);
				return !!widget ? @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget) : null;
			}else
				return null;
		};
		Object.defineProperty(published, "rows", {
			get : function(){
				return aWidget.@com.eas.client.form.published.containers.GridPane::getRowCount()();
			}
		});
		Object.defineProperty(published, "columns", {
			get : function(){
				return aWidget.@com.eas.client.form.published.containers.GridPane::getColumnCount()();
			}
		});
		Object.defineProperty(published, "children", {
			get : function(){
				var ch = [];
				for(var r = 0; r < published.rows; r++){
					for(var c = 0; c < published.columns; c++){
						var index = published.columns * r + c;
						var comp = published.child(r, c);
						if(comp != null){
							ch[ch.length] = comp;
						}
					}
				}
				return ch;
			}
		});
		Object.defineProperty(published, "count", {
			get : function(){
				var ch = published.children;
				return ch.length;
			}
		});
	}-*/;
}

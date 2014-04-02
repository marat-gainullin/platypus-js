/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.published.containers;

import java.util.HashMap;
import java.util.Map;

import com.bearsoft.gwt.ui.containers.CardsPanel;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class CardPane extends CardsPanel implements HasJsFacade, HasEnabled, HasComponentPopupMenu {

	protected PlatypusPopupMenu menu;
	protected boolean enabled;
	protected String name;	
	protected JavaScriptObject published;
	
	private Map<String, Widget> cards = new HashMap<String, Widget>();

	public CardPane(int aVGap, int aHGap) {
		super();
		setHgap(aHGap);
		setVgap(aVGap);
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
				menuTriggerReg = super.addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT && menu != null) {
							menu.showRelativeTo(CardPane.this);
						}
					}

				}, ClickEvent.getType());
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		enabled = aValue;
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public void show(String aCardName) {
		if (cards.containsKey(aCardName)) {
			Widget toShow = cards.get(aCardName);
			showWidget(getWidgetIndex(toShow));
		}
	}

	public void add(Widget aWidget, String aCardName) {
		if (cards.containsKey(aCardName))
			super.remove(cards.get(aCardName));
		super.add(aWidget);
		cards.put(aCardName, aWidget);
	}

	@Override
	public boolean remove(Widget child) {
		for (String cardName : cards.keySet()) {
			if (cards.get(cardName) == child) {
				cards.remove(cardName);
				break;
			}
		}
		return super.remove(child);
	}

	@Override
	public void clear() {
		cards.clear();
		super.clear();
	}

	public Widget getWidget(String aCardName) {
		return cards.get(aCardName);
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
		published.add = function(toAdd, aCardName){
			if(toAdd && toAdd.unwrap){
				aComponent.@com.eas.client.form.published.containers.CardPane::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(toAdd.unwrap(), aCardName);
			}
		};
		published.child = function(aIndex_or_aCardName) {
			var widget;
			if (isNaN(aIndex_or_aCardName)) {
				widget = aComponent.@com.eas.client.form.published.containers.CardPane::getWidget(Ljava/lang/String;)(aIndex_or_aCardName);
			}else {
				var index = parseInt(aIndex_or_aCardName, 10);
				widget = aComponent.@com.eas.client.form.published.containers.CardPane::getWidget(I)(index);
			}
			return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
		};
		published.show = function(aCardName) {
			aComponent.@com.eas.client.form.published.containers.CardPane::show(Ljava/lang/String;)(aCardName);
		};			
	}-*/;
}

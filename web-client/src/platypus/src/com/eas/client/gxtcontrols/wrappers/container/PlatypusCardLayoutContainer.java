/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.gxtcontrols.wrappers.container;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;

/**
 * 
 * @author vy
 */
public class PlatypusCardLayoutContainer extends CardLayoutContainer {
	private int hGap = 0;
	private int vGap = 0;
	private Map<String, IsWidget> cards = new HashMap();

	public PlatypusCardLayoutContainer(int aVGap, int aHGap) {
		super();
		hGap = aHGap;
		vGap = aVGap;
	}

	public void show(String aCardName) {
		if (cards.containsKey(aCardName))
			super.setActiveWidget(cards.get(aCardName));
	}

	public void add(Widget aWidget, String aCardName) {
		if (cards.containsKey(aCardName))
			super.remove(cards.get(aCardName));
		super.add(aWidget, new MarginData(vGap, hGap, vGap, hGap));
		cards.put(aCardName, aWidget);
		if (isAttached())
			forceLayout();
	}

	public int getHgap() {
		return hGap;
	}

	public int getVgap() {
		return vGap;
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
		return asWidgetOrNull(cards.get(aCardName));
	}

}

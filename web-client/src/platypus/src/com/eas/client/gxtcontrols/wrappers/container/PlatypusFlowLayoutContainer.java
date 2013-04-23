package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.dom.client.Style;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;

public class PlatypusFlowLayoutContainer extends FlowLayoutContainer {

	private int leftGap = 0;
	private int topGap = 0;
	private int rightGap = 0;
	private int bottomGap = 0;

	public PlatypusFlowLayoutContainer(int aVGap, int aHGap) {
		super();
		leftGap = aHGap/2;
		topGap = aVGap/2;
		rightGap = aHGap - leftGap;
		bottomGap = aVGap - topGap;
	}

	public int getHGap() {
		return topGap + bottomGap;
	}

	public int getVGap() {
		return leftGap + rightGap;
	}

	public void add(Component aChild) {
        Style style = aChild.getElement().getStyle();
        style.setDisplay(Style.Display.INLINE_BLOCK);                
        style.setVerticalAlign(Style.VerticalAlign.TOP);
		super.add(aChild, new MarginData(new Margins(topGap, rightGap, bottomGap, leftGap)));

	}
}

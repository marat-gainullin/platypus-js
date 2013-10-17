package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;

public class PlatypusBorderLayoutContainer extends BorderLayoutContainer {

	private int hGap = 0;
	private int vGap = 0;

	public PlatypusBorderLayoutContainer(int aVGap, int aHGap) {
		super();
		hGap = aHGap;
		vGap = aVGap;
	}

	public int getHGap() {
		return hGap;
	}

	public int getVGap() {
		return vGap;
	}

	public void add(Widget aWidget, LayoutRegion aPlace, double aSize) {  
		if (aPlace == LayoutRegion.NORTH) {
			setNorthWidget(aWidget, new BorderLayoutData(aSize));
		} else if (aPlace == LayoutRegion.WEST) {
			setWestWidget(aWidget, new BorderLayoutData(aSize));
		} else if (aPlace == LayoutRegion.CENTER) {
			setCenterWidget(aWidget, new MarginData(vGap, hGap, vGap, hGap));
		} else if (aPlace == LayoutRegion.EAST) {
			setEastWidget(aWidget, new BorderLayoutData(aSize));
		} else if (aPlace == LayoutRegion.SOUTH) {
			setSouthWidget(aWidget, new BorderLayoutData(aSize));
		}
		// TODO: may be replace with lastSize = null; setSize(width, height); - like in Platypus[VH]BoxLayoutContainer
		if (isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					forceLayout();
				}
			});
		}
	}

	public void add(Widget aWidget, String aPlace, double aSize) {
		aPlace = aPlace.toUpperCase();
		add(aWidget, LayoutRegion.valueOf(aPlace), aSize);
	}

	public void add(Widget aWidget, LayoutRegion region, Size aSize) {
		add(aWidget, region, region == LayoutRegion.EAST || region == LayoutRegion.WEST ? aSize.getWidth() : aSize.getHeight());
	}

	public void add(Widget aWidget, String aPlace, Size aSize) {
		aPlace = aPlace.toUpperCase();
		LayoutRegion region = LayoutRegion.valueOf(aPlace);
		add(aWidget, region, aSize);
	}

	@Override
	public boolean remove(Widget child) {
		boolean res = super.remove(child);
		if (res && isAttached())
			forceLayout();
		return res;
	}
}

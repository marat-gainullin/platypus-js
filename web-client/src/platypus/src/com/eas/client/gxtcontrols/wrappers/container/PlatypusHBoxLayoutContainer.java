package com.eas.client.gxtcontrols.wrappers.container;

import com.eas.client.gxtcontrols.Sizer;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;

public class PlatypusHBoxLayoutContainer extends HBoxLayoutContainer {

	public PlatypusHBoxLayoutContainer() {
		super(HBoxLayoutContainer.HBoxLayoutAlign.STRETCH);
	}

	@Override
	public void add(Widget child) {
		BoxLayoutData flex = new BoxLayoutData(new Margins(0, 0, 0, 0));
		// flex.setFlex(1);
		child.setLayoutData(flex);
		super.add(child);
		if (isAttached())
			forceLayout();
	}

	@Override
	public boolean remove(Widget child) {
		boolean res = super.remove(child);
		if (isAttached())
			forceLayout();
		return res;
	}

	@Override
	protected void onInsert(int index, Widget child) {
		super.onInsert(index, child);
		ajustSize();
	}

	@Override
	protected void onRemove(Widget child) {
		super.onRemove(child);
		ajustSize();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		ajustSize();
	}

	protected void ajustSize() {
		if (isAttached() && getParent() instanceof PlatypusScrollContainer) {
			forceLayout();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					Size size = Sizer.boxSize(PlatypusHBoxLayoutContainer.this);
					setPixelSize(size.getWidth(), Sizer.getWidgetHeight(PlatypusHBoxLayoutContainer.this.getParent()));
				}
			});
		}
	}

	public void ajustWidth(Widget aChild, int aValue) {
		if (aChild != null) {
			aChild.setPixelSize(aValue, Sizer.getWidgetHeight(aChild));
			ajustSize();
		}
	}
}

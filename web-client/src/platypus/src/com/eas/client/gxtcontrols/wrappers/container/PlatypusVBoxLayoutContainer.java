package com.eas.client.gxtcontrols.wrappers.container;

import com.eas.client.gxtcontrols.Sizer;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;

public class PlatypusVBoxLayoutContainer extends VBoxLayoutContainer {

	public PlatypusVBoxLayoutContainer() {
		super(VBoxLayoutContainer.VBoxLayoutAlign.STRETCH);
	}

	@Override
	public void add(Widget child) {
		BoxLayoutData flex = new BoxLayoutData(new Margins(0, 0, 0, 0));
		// flex.setFlex(1);
		child.setLayoutData(flex);
		super.add(child);
		if (isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					forceLayout();
				}
			});
		}
	}

	@Override
	public boolean remove(Widget child) {
		boolean res = super.remove(child);
		if (isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					forceLayout();
				}
			});
		}
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

	void ajustSize() {
		if (isAttached() && getParent() instanceof PlatypusScrollContainer) {
			forceLayout();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					Size size = Sizer.boxSize(PlatypusVBoxLayoutContainer.this);
					setPixelSize(Sizer.getWidgetWidth(PlatypusVBoxLayoutContainer.this.getParent()), size.getHeight());
				}
			});
		}
	}

	public void ajustHeight(Widget aChild, int aValue) {
		if (aChild != null) {
			if(aChild.getParent() instanceof PlatypusFieldSet)
				aChild.getParent().setPixelSize(Sizer.getWidgetWidth(aChild), aValue);
			else
				aChild.setPixelSize(Sizer.getWidgetWidth(aChild), aValue);
			ajustSize();
		}
	}
}

package com.eas.ui;

import com.eas.core.XElement;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style;

public class StandaloneRootPanel extends SimplePanel implements RequiresResize {

	public StandaloneRootPanel(Element aElement) {
		super(aElement);
		aElement.<XElement> cast().addResizingTransitionEnd(this);
		onAttach();
	}

	@Override
	public void setWidget(Widget w) {
		super.setWidget(w);
		w.element.style.position = 'absolute';
		w.element.style.width =100 + '%');
		w.element.style.height =100 + '%');
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onResize();
			}
		});
	}

	@Override
	public void onResize() {
		Widget child = getWidget();
		if (child instanceof RequiresResize) {
			((RequiresResize) child).onResize();
		}
	}
}

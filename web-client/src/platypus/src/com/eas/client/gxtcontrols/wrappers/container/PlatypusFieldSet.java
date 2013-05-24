package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;

public class PlatypusFieldSet extends FieldSet {

	// ??????????????????????????????????
	private int CONTENT_SHIFT_HEIGHT = 16;

	private int leftPadding = 1;
	private int topPadding = 1;
	private int rightPadding = 1;
	private int bottomPadding = 1;

	private SimpleContainer content = new SimpleContainer();

	private XElement legend = null;
	private Style legendStyle = null;

	private int legendWidth = 0;

	public PlatypusFieldSet() {
		this(1);
	}

	public PlatypusFieldSet(int aPadding) {
		super.setWidget(content);
		getElement().getStyle().setPadding(aPadding, Style.Unit.PX);

		leftPadding = aPadding;
		topPadding = aPadding;
		rightPadding = aPadding;
		bottomPadding = aPadding;

		legend = getElement().child("legend");
		XElement legendSpan = legend.child("span");
		XElement legendDiv = legend.child("div");

		legend.removeChildren();

		Element div = DOM.createDiv();
		div.appendChild(legendDiv);
		div.appendChild(legendSpan);

		Style divstyle = div.getStyle();
		divstyle.setProperty("whiteSpace", "nowrap");
		divstyle.setProperty("overflow", "hidden");

		legend.appendChild(div);
		legendStyle = legend.getStyle();
		getElement().getStyle().setMargin(0, Style.Unit.PX);
	}

	private void setLegendWidth() {
		if (isAttached()) {
			legendStyle.clearWidth();
			if (legend.getWidth(false) > legendWidth) {
				legendStyle.setWidth(legendWidth, Style.Unit.PX);
			}
		}
	}

	@Override
	protected void onResize(int width, int height) {
		int dW = getElement().getWidth(false) - getElement().getWidth(true);
		int dH = getElement().getHeight(false) - getElement().getHeight(true);
		super.onResize(width, height);
		content.setPixelSize(width - leftPadding - rightPadding, height - dH / 2 - topPadding - CONTENT_SHIFT_HEIGHT);
		legendWidth = width - dW - dW;
		setLegendWidth();
	}

	@Override
	public void setHeadingText(String heading) {
		super.setHeadingText(heading);
		setLegendWidth();
	}

	@Override
	public void setWidget(Widget aWidget) {
		content.setWidget(aWidget);
	}

	@Override
	public void setWidget(IsWidget isWidget) {
		setWidget(asWidgetOrNull(isWidget));
	}

	@Override
	public void add(Widget aWidget) {
		setWidget(aWidget);
	}

	@Override
	public void add(Widget aWidget, MarginData aLayoutData) {
		content.add(aWidget, aLayoutData);
	}

	@Override
	public void add(IsWidget isWidget) {
		setWidget(asWidgetOrNull(isWidget));
	}

	@Override
	public Widget getWidget() {
		return content.getWidget();
	}

	@Override
	public Widget getWidget(int index) {
		if (index == 0)
			return getWidget();
		return null;
	}

	@Override
	public int getWidgetCount() {
		return content.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget aWidget) {
		return content.getWidgetIndex(aWidget);
	}

	@Override
	public int getWidgetIndex(IsWidget isWidget) {
		return getWidgetIndex(asWidgetOrNull(isWidget));
	}

	// @Override
	public void clear() {
		content.clear();
	}

	@Override
	public boolean remove(int index) {
		return content.remove(index);
	}

	@Override
	public boolean remove(Widget child) {
		return content.remove(child);
	}

	@Override
	public boolean remove(IsWidget child) {
		return remove(asWidgetOrNull(child));
	}
}

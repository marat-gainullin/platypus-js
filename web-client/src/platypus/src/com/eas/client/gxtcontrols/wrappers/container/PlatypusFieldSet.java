package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.DisableEvent;
import com.sencha.gxt.widget.core.client.event.DisableEvent.DisableHandler;
import com.sencha.gxt.widget.core.client.event.EnableEvent;
import com.sencha.gxt.widget.core.client.event.EnableEvent.EnableHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;

public class PlatypusFieldSet extends FieldSet {

	private XElement legend;
	private Style legendStyle;

	private int legendWidth = 0;
	private GroupingHandlerRegistration handlers = new GroupingHandlerRegistration();

	public PlatypusFieldSet() {
		this(GWT.<FieldSetAppearance> create(FieldSetAppearance.class));
	}

	public PlatypusFieldSet(FieldSetAppearance appearance) {
		super(appearance);
		legend = getElement().child("legend");
		legendStyle = legend.getStyle();
		// legendStyle.setProperty("whiteSpace", "nowrap");
		// legendStyle.setProperty("overflow", "hidden");
		XElement content = appearance.getContainerTarget(getElement());
		content.makePositionable(true);
		content.getStyle().setTop(0, Unit.PX);
		// getElement().getStyle().setMargin(0, Style.Unit.PX);
	}

	@Override
	public void setWidget(Widget w) {
		handlers.removeHandler();
		super.setWidget(w);
		if (w instanceof Component) {
			handlers.add(((Component) w).addHideHandler(new HideHandler() {

				@Override
				public void onHide(HideEvent event) {
					PlatypusFieldSet.this.hide();
				}

			}));
			handlers.add(((Component) w).addShowHandler(new ShowHandler() {

				@Override
				public void onShow(ShowEvent event) {
					PlatypusFieldSet.this.show();
				}

			}));
			handlers.add(((Component) w).addEnableHandler(new EnableHandler() {

				@Override
				public void onEnable(EnableEvent event) {
					PlatypusFieldSet.this.enable();
				}

			}));
			handlers.add(((Component) w).addDisableHandler(new DisableHandler() {

				@Override
				public void onDisable(DisableEvent event) {
					PlatypusFieldSet.this.disable();
				}

			}));
		}
	}

	private void applyLegendWidth() {
		if (isAttached()) {
			legendStyle.clearWidth();
			if (legend.getWidth(false) > legendWidth) {
				legendStyle.setWidth(legendWidth, Style.Unit.PX);
			}
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
	    getContainerTarget().setHeight(height, true);
		int dW = getElement().getWidth(false) - getElement().getWidth(true);
		legendWidth = width - 2 * dW;
		applyLegendWidth();
	}

	@Override
	public void setHeadingText(String heading) {
		super.setHeadingText(heading);
		applyLegendWidth();
	}
}

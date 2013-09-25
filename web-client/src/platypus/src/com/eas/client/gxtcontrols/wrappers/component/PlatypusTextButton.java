package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance.ButtonCellResources;
import com.sencha.gxt.widget.core.client.button.TextButton;

public class PlatypusTextButton extends TextButton {

	protected static ButtonCellResources buttonResources = GWT.create(ButtonCellResources.class);

	protected int iconTextGap = 4;

	public int getIconTextGap() {
		return iconTextGap;
	}

	public void setIconTextGap(int aValue) {
		if (iconTextGap != aValue) {
			iconTextGap = aValue;
			redraw();
		}
	}

	@Override
	protected void onRedraw() {
		super.onRedraw();
		XElement el = getElement().cast();
		XElement imageWrap = el.selectNode("."+buttonResources.style().iconWrap());
		if (imageWrap != null && getIcon() != null) {
			if (getIconAlign() == IconAlign.RIGHT)
				imageWrap.getStyle().setPaddingLeft(iconTextGap, Unit.PX);
			else if (getIconAlign() == IconAlign.LEFT)
				imageWrap.getStyle().setPaddingRight(iconTextGap, Unit.PX);
			else if (getIconAlign() == IconAlign.BOTTOM)
				imageWrap.getStyle().setPaddingTop(iconTextGap, Unit.PX);
			else if (getIconAlign() == IconAlign.TOP)
				imageWrap.getStyle().setPaddingBottom(iconTextGap, Unit.PX);
		}
		ControlsUtils.reapplyStyle(this);
	}

}

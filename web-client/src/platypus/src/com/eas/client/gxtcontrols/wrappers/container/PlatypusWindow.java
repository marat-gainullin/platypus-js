package com.eas.client.gxtcontrols.wrappers.container;

import java.util.List;

import com.eas.client.form.Form;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;

/**
 * 
 * @author mg
 */
public class PlatypusWindow extends Window {

	protected Element containerToFit;
	protected PlatypusDesktopContainer desktop;
	protected ToolButton maxButton;
	protected ToolButton minButton;
	protected Form form;

	public PlatypusWindow(PlatypusDesktopContainer aDesktop, Form aForm) {
		super();
		desktop = aDesktop;
		form = aForm;
		setMaximizable(true);
		setMinimizable(true);
		setAnimCollapse(false);
	}

	public Form getForm() {
		return form;
	}

	@Override
	public void setContainer(Element aContainer) {
		super.setContainer(aContainer);
		containerToFit = aContainer;
	}

	@Override
	protected void onAfterFirstAttach() {
		super.onAfterFirstAttach();
		if (desktop != null) {
			manager = desktop.getManager();
		}
	}

	@Override
	public void show() {
		if (!hidden || !fireCancellableEvent(new BeforeShowEvent())) {
			return;
		}
		// remove hide style, else layout fails
		removeStyleName(getHideMode().value());
		// addStyleName(HideMode.OFFSETS.value());
		if (!isAttached()) {
			if (desktop != null) {
				desktop.add(this);
			} else {
				RootPanel.get().add(this);
			}
		}
		getElement().makePositionable(true);
		onShow();
		manager.register(this);

		afterShow();
		notifyShow();
	}

	@Override
	protected void onHide() {
		super.onHide();
		form = null;
	}

	@Override
	protected void fitContainer() {
		if (desktop != null) {
			if (containerToFit != null) {
				super.fitContainer();
			} else {
				setPosition(0, 0);
				Rectangle bounds = getParent().getElement().<XElement> cast().getBounds();
				setPixelSize(bounds.getWidth(), bounds.getHeight());
			}
		} else {
			super.fitContainer();
		}
	}

	@Override
	protected void initTools() {
		boolean oldMinimizable = isMinimizable();
		boolean oldMaximizable = isMaximizable();
		boolean oldClosable = isClosable();
		setMinimizable(true);
		setMaximizable(true);
		setClosable(true);
		try {
			super.initTools();
			List<Widget> wTools = header.getTools();
			minButton = (ToolButton) wTools.get(0);
			maxButton = (ToolButton) wTools.get(1);
		} finally {
			setMinimizable(oldMinimizable);
			setMaximizable(oldMaximizable);
			setClosable(oldClosable);
		}
	}

	@Override
	public void setMinimizable(boolean minimizable) {
		super.setMinimizable(minimizable);
		if (minButton != null)
			minButton.setVisible(isMinimizable());
	}

	@Override
	public void setMaximizable(boolean maximizable) {
		super.setMaximizable(maximizable);
		if (maxButton != null)
			maxButton.setVisible(isMaximizable());
	}

	@Override
	public void setClosable(boolean closable) {
		super.setClosable(closable);
		if (closeBtn != null)
			closeBtn.setVisible(isClosable());
	}

	@Override
	public void minimize() {
		if (isMinimizable() && !isCollapsed() && !isMaximized()) {
			maxButton.setVisible(false);
			minButton.setVisible(false);
			restoreBtn.setVisible(true);
			collapse();
			super.minimize();// event firing
		}
	}

	@Override
	public void maximize() {
		if (isMaximizable() && !isMaximized() && !isCollapsed()) {
			super.maximize();
			minButton.setVisible(false);
		}
	}

	@Override
	public void restore() {
		if (isCollapsed()) {
			expand();
			if (isMaximizable()) {
				maxButton.setVisible(true);
			}
			if (isMinimizable()) {
				minButton.setVisible(true);
			}
			restoreBtn.setVisible(false);
			fireEvent(new RestoreEvent());
		} else if (isMaximized()) {
			super.restore();
			if (isMinimizable()) {
				minButton.setVisible(true);
			}
		}
	}

	/*
	 * @Override public void setPosition(int left, int top) {
	 * super.setPosition(left >= 0 ? left : 0, top >= 0 ? top : 0); }
	 */
	@Override
	public void center() {
		Point p = getElement().getAlignToXY(getParent().getElement(), new Style.AnchorAlignment(Style.Anchor.CENTER, Style.Anchor.CENTER), null);
		setPagePosition(p.getX(), p.getY());
	}
}
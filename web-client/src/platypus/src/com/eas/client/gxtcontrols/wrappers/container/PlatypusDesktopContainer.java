package com.eas.client.gxtcontrols.wrappers.container;

import com.eas.client.form.Form;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.container.Container;

/**
 * 
 * @author mg
 */
public class PlatypusDesktopContainer extends Container {

	public static final int DEFAULT_WINDOWS_SPACING_X = 15;
	public static final int DEFAULT_WINDOWS_SPACING_Y = 10;
	protected WindowManager manager = new WindowManager();
	protected Point consideredPosition = new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y);

	public PlatypusDesktopContainer() {
		super();
		setElement(DOM.createDiv());
		getElement().getStyle().setOverflow(Style.Overflow.AUTO);
	}

	public WindowManager getManager() {
		return manager;
	}

	public void minimizeAll() {
		for (Widget wd : manager.getWindows()) {
			if (wd instanceof PlatypusWindow) {
				PlatypusWindow pw = (PlatypusWindow) wd;
				pw.minimize();
			}
		}
	}

	public void maximizeAll() {
		for (Widget wd : manager.getWindows()) {
			if (wd instanceof PlatypusWindow) {
				PlatypusWindow pw = (PlatypusWindow) wd;
				pw.maximize();
			}
		}
	}

	public void restoreAll() {
		for (Widget wd : manager.getWindows()) {
			if (wd instanceof PlatypusWindow) {
				PlatypusWindow pw = (PlatypusWindow) wd;
				pw.restore();
			}
		}
	}

	public void closeAll() {
		for (Widget wd : manager.getWindows().toArray(new Widget[] {})) {
			if (wd instanceof PlatypusWindow) {
				PlatypusWindow pw = (PlatypusWindow) wd;
				pw.hide();
			}
		}
	}

	public JavaScriptObject getForms() {
		JsArray<JavaScriptObject> modules = JavaScriptObject.createArray().cast();
		for (Widget wd : manager.getWindows()) {
			if (wd instanceof PlatypusWindow) {
				PlatypusWindow pw = (PlatypusWindow) wd;
				Form form = pw.getForm();
				if (form != null) {
					modules.push(form.getModule());
				}
			}
		}
		return modules;
	}

	public Point getConsideredPosition() {
		return consideredPosition;
	}

	@Override
	public void add(Widget child) {
		super.add(child);
		if (child instanceof PlatypusWindow) {
			refreshConsideredPosition();
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		refreshConsideredPosition();
	}

	private void refreshConsideredPosition() {
		if (consideredPosition.getX() > getElement().getClientWidth() / 2) {
			consideredPosition.setX(0);
		}
		if (consideredPosition.getY() > getElement().getClientHeight() / 2) {
			consideredPosition.setY(0);
		}
		consideredPosition.setX(consideredPosition.getX() + DEFAULT_WINDOWS_SPACING_X);
		consideredPosition.setY(consideredPosition.getY() + DEFAULT_WINDOWS_SPACING_Y);
	}
}

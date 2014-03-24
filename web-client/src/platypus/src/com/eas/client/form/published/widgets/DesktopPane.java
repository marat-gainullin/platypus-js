package com.eas.client.form.published.widgets;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.containers.window.WindowUI;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class DesktopPane extends FlowPanel implements RequiresResize, ProvidesResize, HasPublished {

	protected JavaScriptObject published;

	public static final int DEFAULT_WINDOWS_SPACING_X = 15;
	public static final int DEFAULT_WINDOWS_SPACING_Y = 10;
	protected List<WindowUI> manager = new ArrayList<>();
	protected Point consideredPosition = new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y);

	public DesktopPane() {
		super();
		getElement().getStyle().setOverflow(Style.Overflow.AUTO);
	}

	public List<WindowUI> getManager() {
		return manager;
	}

	public void minimizeAll() {
		for (WindowUI wd : manager) {
			wd.minimize();
		}
	}

	public void maximizeAll() {
		for (WindowUI wd : manager) {
			wd.maximize();
		}
	}

	public void restoreAll() {
		for (WindowUI wd : manager) {
			wd.restore();
		}
	}

	public void closeAll() {
		for (WindowUI wd : manager) {
			wd.close();
		}
		manager.clear();
	}

	public Point getConsideredPosition() {
		return consideredPosition;
	}

	@Override
	public void add(Widget child) {
		super.add(child);
		if (child instanceof WindowUI) {
			child.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			refreshConsideredPosition();
		}
	}

	@Override
	public void onResize() {
		refreshConsideredPosition();
	}

	private void refreshConsideredPosition() {
		if (consideredPosition.getX() > getElement().getClientWidth() / 2) {
			consideredPosition.plus(new Point(-consideredPosition.getX(), 0));// setX(0)
		}
		if (consideredPosition.getY() > getElement().getClientHeight() / 2) {
			consideredPosition.plus(new Point(0, -consideredPosition.getY()));// setY(0)
		}
		consideredPosition.plus(new Point(DEFAULT_WINDOWS_SPACING_X, DEFAULT_WINDOWS_SPACING_Y));
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "forms", {
			get:function(){
				return aWidget.@com.eas.client.form.published.widgets.DesktopPane::getForms()();
			} 
		});
		published.closeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::closeAll()();				
		}
		published.minimizeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::minimizeAll()();				
		}
		published.maximizeAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::maximizeAll()();				
		}
		published.restoreAll = function(){
			aWidget.@com.eas.client.form.published.widgets.DesktopPane::restoreAll()();				
		}
	}-*/;
}

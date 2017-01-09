/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public abstract class DraggableDecoration extends Widget {

	protected int mouseScreenX;
	protected int mouseScreenY;
	protected double targetScrollLeft;
	protected double targetScrollTop;
	protected double targetWidth;
	protected double targetHeight;

	public DraggableDecoration(String... aClasses) {
		super();
		Element e = Document.get().createDivElement();
		setElement(e);
		for (String className : aClasses) {
			e.addClassName(className);
		}
		addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				DOM.setCapture(getElement());
				event.preventDefault();
				event.stopPropagation();
				mouseScreenX = event.getScreenX();
				mouseScreenY = event.getScreenY();
				event.stopPropagation();
				Widget target = getMovableTarget();
				assert target != null : "movable target widget must present to accept position changes";
				String tLeft = target.getElement().getStyle().getLeft();
				targetScrollLeft = Double.parseDouble(tLeft.substring(0, tLeft.length() - 2));
				String tTop = target.getElement().getStyle().getTop();
				targetScrollTop = Double.parseDouble(tTop.substring(0, tTop.length() - 2));

				target = getResizableTarget();
				assert target != null : "resizable target widget must present to accept size changes";
				String tWidth = target.getElement().getStyle().getWidth();
				targetWidth = Double.parseDouble(tWidth.substring(0, tWidth.length() - 2));
				String tHeight = target.getElement().getStyle().getHeight();
				targetHeight = Double.parseDouble(tHeight.substring(0, tHeight.length() - 2));
				mousePressed();
			}
		}, MouseDownEvent.getType());
		addDomHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				event.preventDefault();
				event.stopPropagation();
				if (DOM.getCaptureElement() == getElement()) {
					DOM.releaseCapture(getElement());
					int dx = event.getScreenX() - mouseScreenX;
					int dy = event.getScreenY() - mouseScreenY;
					changeTarget(getMovableTarget(), getResizableTarget(), dx, dy, event);
				}
				mouseReleased();
			}
		}, MouseUpEvent.getType());
		addDomHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (DOM.getCaptureElement() == getElement()) {
					int dx = event.getScreenX() - mouseScreenX;
					int dy = event.getScreenY() - mouseScreenY;
					event.preventDefault();
					event.stopPropagation();
					if (dx != 0 || dy != 0) {
						mouseDragged();
					}
					changeTarget(getMovableTarget(), getResizableTarget(), dx, dy, event);
				}
			}

		}, MouseMoveEvent.getType());
	}

	public void assignMouseState(DraggableDecoration s) {
		mouseScreenX = s.mouseScreenX;
		mouseScreenY = s.mouseScreenY;
		targetScrollLeft = s.targetScrollLeft;
		targetScrollTop = s.targetScrollTop;
		targetWidth = s.targetWidth;
		targetHeight = s.targetHeight;
	}

	protected abstract void mousePressed();

	protected abstract void mouseReleased();

	protected abstract void mouseDragged();

	protected abstract Widget getMovableTarget();

	protected abstract Widget getResizableTarget();

	protected abstract boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent);

	@Override
	public void onAttach() {
		super.onAttach();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}

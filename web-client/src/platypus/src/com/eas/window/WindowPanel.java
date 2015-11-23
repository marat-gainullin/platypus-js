/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window;

import com.eas.core.XElement;
import com.eas.window.events.ActivateEvent;
import com.eas.window.events.ActivateHandler;
import com.eas.window.events.BeforeCloseEvent;
import com.eas.window.events.BeforeCloseHandler;
import com.eas.window.events.ClosedEvent;
import com.eas.window.events.ClosedHandler;
import com.eas.window.events.DeactivateEvent;
import com.eas.window.events.DeactivateHandler;
import com.eas.window.events.MaximizeEvent;
import com.eas.window.events.MaximizeHandler;
import com.eas.window.events.MinimizeEvent;
import com.eas.window.events.MinimizeHandler;
import com.eas.window.events.MoveEvent;
import com.eas.window.events.MoveHandler;
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Document;

/**
 * 
 * @author mg
 */
public class WindowPanel extends DraggablePanel implements WindowUI, HasAnimation {

	protected static final String ACTIVE_SUFFIX_NAMNE = "active";

	public static class Caption extends HTML {

		public Caption(String aHtml) {
			super();
			setHTML(aHtml);
			setStyleName("window-caption");
		}
	}

	protected Widget captionWidget;
	protected VerticalPanel verticalPanel;
	protected JavaScriptObject transitionOnResizer;
	protected Widget verticalPanelContent;
	protected boolean animationEnabled = true;
	protected boolean movable = true;
	protected boolean maximized;
	protected boolean minimized;
	protected boolean active;
	protected boolean maximizable = true;
	protected boolean minimizable = true;
	protected boolean closable = true;

	public WindowPanel() {
		super(true);
		addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				focus();
			}
		}, MouseDownEvent.getType());
		verticalPanel = new VerticalPanel();
		super.setWidget(verticalPanel);
		setCaptionWidget(new Caption(""));
		setUndecorated(false);
		getElement().addClassName("window-panel");
		getElement().<XElement> cast().addResizingTransitionEnd(this);
		getMovableTarget().getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	@Override
	public HasHTML getCaptionWidget() {
		if (captionWidget instanceof HasHTML) {
			return (HasHTML) captionWidget;
		} else {
			return null;
		}
	}

	protected HandlerRegistration mdHandler;
	protected HandlerRegistration muHandler;
	protected HandlerRegistration mmHandler;

	protected int mouseScreenX;
	protected int mouseScreenY;
	protected int targetScrollLeft;
	protected int targetScrollTop;

	protected boolean dragged;

	@Override
	public final void setCaptionWidget(HasHTML aCaptionWidget) {
		if (captionWidget != aCaptionWidget) {
			if (captionWidget != null) {
				captionWidget.removeFromParent();
			}
			if (mdHandler != null) {
				mdHandler.removeHandler();
			}
			if (muHandler != null) {
				muHandler.removeHandler();
			}
			if (mmHandler != null) {
				mmHandler.removeHandler();
			}
			if (aCaptionWidget instanceof Widget) {
				captionWidget = (Widget) aCaptionWidget;
			} else {
				captionWidget = new Caption(aCaptionWidget.getHTML());
			}
			if (captionWidget != null) {
				mdHandler = captionWidget.addDomHandler(new MouseDownHandler() {

					@Override
					public void onMouseDown(MouseDownEvent event) {
						event.preventDefault();
						event.stopPropagation();
						focus();
						if (movable && !maximized) {
							DOM.setCapture(captionWidget.getElement());
							mouseScreenX = event.getScreenX();
							mouseScreenY = event.getScreenY();
							String tLeft = getMovableTarget().getElement().getStyle().getLeft();
							targetScrollLeft = Integer.parseInt(tLeft.substring(0, tLeft.length() - 2));
							String tTop = getMovableTarget().getElement().getStyle().getTop();
							targetScrollTop = Integer.parseInt(tTop.substring(0, tTop.length() - 2));
						}
					}

				}, MouseDownEvent.getType());
				muHandler = captionWidget.addDomHandler(new MouseUpHandler() {

					@Override
					public void onMouseUp(MouseUpEvent event) {
						dragged = false;
						event.preventDefault();
						event.stopPropagation();
						if (movable && !maximized) {
							DOM.releaseCapture(captionWidget.getElement());
							endMoving();
						}
					}

				}, MouseUpEvent.getType());
				mmHandler = captionWidget.addDomHandler(new MouseMoveHandler() {

					@Override
					public void onMouseMove(MouseMoveEvent event) {
						if (movable && !maximized) {
							if (DOM.getCaptureElement() == captionWidget.getElement()) {
								event.preventDefault();
								event.stopPropagation();
								int dx = event.getScreenX() - mouseScreenX;
								int dy = event.getScreenY() - mouseScreenY;
								if (!dragged && (dx != 0 || dy != 0)) {
									dragged = true;
									beginMoving();
								}
								setPosition(targetScrollLeft + dx >= 0 ? targetScrollLeft + dx : 0, targetScrollTop + dy >= 0 ? targetScrollTop + dy : 0);
							}
						}
					}

				}, MouseMoveEvent.getType());
				verticalPanel.insert(captionWidget, 0);
			}
		}
	}

	@Override
	protected void decorate() {
		super.decorate();
		content.setStyleName("content-decor");
		if (isActive()) {
			content.addStyleDependentName(ACTIVE_SUFFIX_NAMNE);
		} else {
			content.removeStyleDependentName(ACTIVE_SUFFIX_NAMNE);
		}
		if (captionWidget != null) {
			captionWidget.getElement().getStyle().clearDisplay();
		}
	}

	@Override
	protected void undecorate() {
		super.undecorate();
		content.getElement().removeClassName("content-decor");
		content.getElement().removeClassName("content-decor-active");
		if (captionWidget != null) {
			captionWidget.getElement().getStyle().setDisplay(Style.Display.NONE);
		}
	}

	@Override
	public void setWidget(Widget w) {
		if (verticalPanelContent != null) {
			verticalPanelContent.getElement().<XElement> cast().removeTransitionEndListener(transitionOnResizer);
			verticalPanelContent.getElement().removeClassName("window-content");
			verticalPanelContent.removeFromParent();
		}
		verticalPanelContent = w;
		if (verticalPanelContent != null) {
			transitionOnResizer = verticalPanelContent.getElement().<XElement> cast().addResizingTransitionEnd(this);
			verticalPanel.add(verticalPanelContent);
			verticalPanelContent.getElement().addClassName("window-content");
		}
		if (isAttached()) {
			onResize();
		}
	}

	@Override
	public Widget getWidget() {
		return verticalPanelContent;
	}

	@Override
	protected boolean isNresizable() {
		return resizable && !maximized && !minimized;
	}

	@Override
	protected boolean isSresizable() {
		return resizable && !maximized && !minimized;
	}

	@Override
	protected boolean isWresizable() {
		return resizable && !maximized;
	}

	@Override
	protected boolean isEresizable() {
		return resizable && !maximized;
	}

	@Override
	public boolean isMinimized() {
		return minimized;
	}

	@Override
	public boolean isMaximized() {
		return maximized;
	}

	@Override
	public boolean isMinimizable() {
		return minimizable;
	}

	@Override
	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;
	}

	@Override
	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	@Override
	public boolean isClosable() {
		return closable;
	}

	@Override
	public boolean isMaximizable() {
		return maximizable;
	}

	@Override
	public void setMaximizable(boolean maximizable) {
		this.maximizable = maximizable;
	}

	@Override
	public void maximize() {
		if (!maximized) {
			restoreSnapshot();
			maximized = true;
			updateDecorCursors();
			snapshotMetrics();
			// editing
			Element movableElement = getMovableTarget().getElement();
			Style targetStyle = getWidget().getElement().getStyle(); // content
			int leftInset = getWidget().getElement().getAbsoluteLeft() - movableElement.getAbsoluteLeft();
			int rightInset = movableElement.getAbsoluteRight() - getWidget().getElement().getAbsoluteRight();
			int hInsets = leftInset + rightInset;
			int topInset = getWidget().getElement().getAbsoluteTop() - movableElement.getAbsoluteTop();
			int bottomInset = movableElement.getAbsoluteBottom() - getWidget().getElement().getAbsoluteBottom();
			int vInsets = topInset + bottomInset;
			Element parentElement = getMovableTarget().getParent().getElement();
			int parentScrollWidth = parentElement.getScrollWidth();
			int parentScrollHeight;
			if (parentElement == Document.get().getBody())// Some browsers
			                                              // return incorrect
			                                              // height for body
			                                              // element
			{
				parentScrollHeight = Document.get().getClientHeight();
			} else {
				parentScrollHeight = parentElement.getScrollHeight();
			}
			targetStyle.setWidth(parentScrollWidth - hInsets, Style.Unit.PX);
			targetStyle.setHeight(parentScrollHeight - vInsets, Style.Unit.PX);
			// editing
			targetStyle = movableElement.getStyle(); // window
			targetStyle.setLeft(0, Style.Unit.PX);
			targetStyle.setTop(0, Style.Unit.PX);
			onResize();
			MaximizeEvent.<WindowUI> fire(this, this);
		}
	}

	@Override
	public void setPosition(double aLeft, double aTop) {
		super.setPosition(aLeft, aTop);
		MoveEvent.fire(this, this, aLeft, aTop);
	}

	@Override
	public void setSize(double aWidth, double aHeight) {
		super.setSize(aWidth, aHeight);
		ResizeEvent.<WindowPanel> fire(this, (int) aWidth, (int) aHeight);
	}

	private void snapshotMetrics() throws NumberFormatException {
		// measurement
		Style targetStyle = getWidget().getElement().getStyle(); // content
		String sHeight = targetStyle.getHeight();
		contentHeightToRestore = Double.parseDouble(sHeight.substring(0, sHeight.length() - 2));
		String sWidth = targetStyle.getWidth();
		contentWidthToRestore = Double.parseDouble(sWidth.substring(0, sWidth.length() - 2));
		targetStyle = getMovableTarget().getElement().getStyle(); // window
		String sLeft = targetStyle.getLeft();
		leftToRestore = Double.parseDouble(sLeft.substring(0, sLeft.length() - 2));
		String sTop = targetStyle.getTop();
		topToRestore = Double.parseDouble(sTop.substring(0, sTop.length() - 2));
	}

	protected boolean preMinimizedStateMaximized;
	protected boolean preMinimzedStateNormal;

	/**
	 * Minimizes the window. Minimize here is setting zero as height.
	 * Descendants may decide to do something else. minimize does a snapshot of
	 * all window's metrics. So subsequent restore or restoreSnapshot calls will
	 * lead to correct restoration of the window.
	 */
	@Override
	public void minimize() {
		if (!minimized) {
			preMinimizedStateMaximized = maximized;
			preMinimzedStateNormal = !maximized && !minimized;
			restoreSnapshot();
			minimized = true;
			updateDecorCursors();
			// measure
			snapshotMetrics();
			// edit
			Style targetStyle = getWidget().getElement().getStyle(); // content
			targetStyle.setHeight(0, Style.Unit.PX);
			onResize();
			MinimizeEvent.<WindowUI> fire(this, this);
		}
	}

	protected Double leftToRestore;
	protected Double topToRestore;
	protected Double contentWidthToRestore;
	protected Double contentHeightToRestore;

	@Override
	public void restoreSnapshot() {
		if (minimized || maximized) {
			minimized = false;
			maximized = false;
			if (leftToRestore != null) {
				getMovableTarget().getElement().getStyle().setLeft(leftToRestore, Style.Unit.PX);
			}
			leftToRestore = null;
			if (topToRestore != null) {
				getMovableTarget().getElement().getStyle().setTop(topToRestore, Style.Unit.PX);
			}
			topToRestore = null;
			if (contentWidthToRestore != null) {
				getWidget().getElement().getStyle().setWidth(contentWidthToRestore, Style.Unit.PX);
			}
			contentWidthToRestore = null;
			if (contentHeightToRestore != null) {
				getWidget().getElement().getStyle().setHeight(contentHeightToRestore, Style.Unit.PX);
			}
			contentHeightToRestore = null;
			onResize();
			RestoreEvent.<WindowUI> fire(this, this);
		}
	}

	@Override
	public void restore() {
		if (minimized || maximized) {
			if (maximized || preMinimzedStateNormal) {
				restoreSnapshot();
				updateDecorCursors();
			} else if (preMinimizedStateMaximized) {
				maximize();
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void activate() {
		if (!active) {
			active = true;
			if (captionWidget != null) {
				captionWidget.addStyleDependentName(ACTIVE_SUFFIX_NAMNE);
			}
			Widget[] widgets = new Widget[] { n, s, w, e, ne, nw, se, sw, content };
			for (Widget _w : widgets) {
				_w.addStyleDependentName(ACTIVE_SUFFIX_NAMNE);
			}
			ActivateEvent.<WindowUI> fire(this, this);
		}
	}

	@Override
	public void deactivate() {
		if (active) {
			active = false;
			if (captionWidget != null) {
				captionWidget.removeStyleDependentName(ACTIVE_SUFFIX_NAMNE);
			}
			Widget[] widgets = new Widget[] { n, s, w, e, ne, nw, se, sw, content };
			for (Widget _w : widgets) {
				_w.removeStyleDependentName(ACTIVE_SUFFIX_NAMNE);
			}
			DeactivateEvent.<WindowUI> fire(this, this);
		}
	}

	public void toFront() {
		Element selfEl = getMovableTarget().getElement();
		Element parentEl = selfEl.getParentElement();
		if (parentEl != null) {
			Node lastChildNode = parentEl.getLastChild();
			if (lastChildNode instanceof Element && lastChildNode != selfEl) {
				parentEl.insertAfter(selfEl, lastChildNode);
			}
		}
	}

	/**
	 * Activates and brings the window to front. Descandants may focus default
	 * widget in this method.
	 */
	@Override
	public void focus() {
		activate();
		toFront();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		onResize();
		OpenEvent.<WindowUI> fire(this, this);
	}

	protected void fireCloseEvent() {
		ClosedEvent.<WindowUI> fire(this, this);
	}

	@Override
	public void close() {
		if (!BeforeCloseEvent.<WindowUI> fire(this, this)) {
			getMovableTarget().removeFromParent();
			fireCloseEvent();
		}
	}

	@Override
	public boolean isMovable() {
		return movable;
	}

	@Override
	public void setMovable(boolean aValue) {
		movable = aValue;
		updateCaptionCursor();
	}

	@Override
	public boolean isAnimationEnabled() {
		return animationEnabled;
	}

	@Override
	public void setAnimationEnabled(boolean aValue) {
		if (animationEnabled != aValue) {
			animationEnabled = aValue;
			verticalPanelContent.getElement().getStyle().clearProperty("transition");
			if (!animationEnabled) {
				verticalPanelContent.getElement().getStyle().setProperty("transition", "none");
			}
		}
	}

	@Override
	protected void beginResizing() {
		super.beginResizing();
		verticalPanelContent.getElement().getStyle().setProperty("transition", "none");
		getMovableTarget().getElement().getStyle().setProperty("transition", "none");
		getWidget().getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
	}

	@Override
	protected void endResizing() {
		super.endResizing();
		getWidget().getElement().getStyle().clearVisibility();
		if (animationEnabled) {
			verticalPanelContent.getElement().getStyle().clearProperty("transition");
			getMovableTarget().getElement().getStyle().clearProperty("transition");
		}
	}

	protected void beginMoving() {
		getMovableTarget().getElement().getStyle().setProperty("transition", "none");
		getWidget().getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
	}

	protected void endMoving() {
		getWidget().getElement().getStyle().clearVisibility();
		if (animationEnabled) {
			getMovableTarget().getElement().getStyle().clearProperty("transition");
		}
	}

	protected void updateCaptionCursor() {
		if (captionWidget != null) {
			if (movable) {
				captionWidget.getElement().getStyle().clearCursor();
			} else {
				captionWidget.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
			}
		}
	}

	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<WindowUI> handler) {
		return addHandler(handler, OpenEvent.getType());
	}

	@Override
	public HandlerRegistration addClosedHandler(ClosedHandler<WindowUI> handler) {
		return addHandler(handler, ClosedEvent.getType());
	}

	@Override
	public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<WindowUI> handler) {
		return addHandler(handler, BeforeCloseEvent.getType());
	}

	@Override
	public HandlerRegistration addActivateHandler(ActivateHandler<WindowUI> handler) {
		return addHandler(handler, ActivateEvent.getType());
	}

	@Override
	public HandlerRegistration addDeactivateHandler(DeactivateHandler<WindowUI> handler) {
		return addHandler(handler, DeactivateEvent.getType());
	}

	@Override
	public HandlerRegistration addMinimizeHandler(MinimizeHandler<WindowUI> handler) {
		return addHandler(handler, MinimizeEvent.getType());
	}

	@Override
	public HandlerRegistration addMaximizeHandler(MaximizeHandler<WindowUI> handler) {
		return addHandler(handler, MaximizeEvent.getType());
	}

	@Override
	public HandlerRegistration addRestoreHandler(RestoreHandler<WindowUI> handler) {
		return addHandler(handler, RestoreEvent.getType());
	}

	@Override
	public HandlerRegistration addMoveHandler(MoveHandler<WindowUI> handler) {
		return addHandler(handler, MoveEvent.getType());
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}
}

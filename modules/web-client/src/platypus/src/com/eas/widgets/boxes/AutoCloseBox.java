package com.eas.widgets.boxes;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AutoCloseBox extends SimplePanel implements HasOpenHandlers<AutoCloseBox>, HasCloseHandlers<AutoCloseBox> {

	protected HandlerRegistration nativePreviewHandlerRegistration;
	protected HandlerRegistration historyHandlerRegistration;
	private List<Element> autoHidePartners;

	public AutoCloseBox() {
		super();
	}

	public AutoCloseBox(Widget aContent) {
		this();
		setWidget(aContent);
	}

	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 * 
	 * @param partner
	 *            the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner) {
		assert partner != null : "partner cannot be null";
		if (autoHidePartners == null) {
			autoHidePartners = new ArrayList<Element>();
		}
		autoHidePartners.add(partner);
	}

	/**
	 * Remove an autoHide partner.
	 * 
	 * @param partner
	 *            the auto hide partner to remove
	 */
	public void removeAutoHidePartner(Element partner) {
		assert partner != null : "partner cannot be null";
		if (autoHidePartners != null) {
			autoHidePartners.remove(partner);
		}
	}

	public void show(Element aParentElement) {
		nativePreviewHandlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				previewNativeEvent(event);
			}
		});
		historyHandlerRegistration = History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				close();
			}
		});
		// Logical attach
		RootPanel.get().add(this);
		// Phisycal re-attach
		getElement().removeFromParent();
		aParentElement.insertAfter(getElement(), aParentElement.getFirstChild());
		if (getWidget() instanceof RequiresResize) {
			((RequiresResize) getWidget()).onResize();
		}
		OpenEvent.fire(this, this);
	}

	public void close() {
		if (nativePreviewHandlerRegistration != null) {
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}
		if (historyHandlerRegistration != null) {
			historyHandlerRegistration.removeHandler();
			historyHandlerRegistration = null;
		}
		removeFromParent();
		CloseEvent.fire(this, this);
	}

	@Override
	public void setWidget(Widget w) {
		if (w != null) {
			w.getElement().getStyle().setWidth(100, Style.Unit.PCT);
			w.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			w.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		}
		super.setWidget(w);
	}

	/**
	 * Does the event target this popup?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets the popup
	 */
	private boolean eventTargetsPopup(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}

	/**
	 * Does the event target one of the partner elements?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets a partner
	 */
	private boolean eventTargetsPartner(NativeEvent event) {
		if (autoHidePartners == null) {
			return false;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			for (Element elem : autoHidePartners) {
				if (elem.isOrHasChild(Element.as(target))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Preview the {@link NativePreviewEvent}.
	 * 
	 * @param event
	 *            the {@link NativePreviewEvent}
	 */
	private void previewNativeEvent(NativePreviewEvent event) {
		// If the event has been canceled or consumed, ignore it
		if (!event.isCanceled()) {
			// If the event targets the auto close or the partner, consume it.
			Event nativeEvent = Event.as(event.getNativeEvent());
			boolean eventTargetsPopupOrPartner = eventTargetsPopup(nativeEvent) || eventTargetsPartner(nativeEvent);
			if (eventTargetsPopupOrPartner) {
				event.consume();
			}

			// Switch on the event type
			int type = nativeEvent.getTypeInt();
			switch (type) {
			case Event.ONMOUSEDOWN:
			case Event.ONTOUCHSTART:
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null) {
					event.consume();
					return;
				}
				if (!eventTargetsPopupOrPartner) {
					close();
					return;
				}
				break;
			case Event.ONMOUSEUP:
			case Event.ONMOUSEMOVE:
			case Event.ONCLICK:
			case Event.ONDBLCLICK:
			case Event.ONTOUCHEND: {
				// Don't eat events if event capture is enabled, as this can
				// interfere with dialog dragging, for example.
				if (DOM.getCaptureElement() != null) {
					event.consume();
					return;
				}
				break;
			}

			case Event.ONFOCUS: {
				EventTarget evTarget = nativeEvent.getEventTarget();
				if (Element.is(evTarget)) {
					Element target = Element.as(evTarget);
					if (!eventTargetsPopupOrPartner && (target != null)) {
						blur(target);
						event.cancel();
						return;
					}
				}
				break;
			}
			}
		}
	}

	/**
	 * Remove focus from an Element.
	 * 
	 * @param elt
	 *            The Element on which <code>blur()</code> will be invoked
	 */
	private native void blur(Element elt) /*-{
		// Issue 2390: blurring the body causes IE to disappear to the background
		if (elt.blur && elt != $doc.body) {
			elt.blur();
		}
	}-*/;

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<AutoCloseBox> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<AutoCloseBox> handler) {
		return addHandler(handler, OpenEvent.getType());
	}

}

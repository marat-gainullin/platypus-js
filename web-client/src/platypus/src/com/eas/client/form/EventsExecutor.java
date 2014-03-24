package com.eas.client.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.containers.window.WindowUI;
import com.bearsoft.gwt.ui.containers.window.events.MoveEvent;
import com.bearsoft.gwt.ui.containers.window.events.MoveHandler;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.events.AddEvent;
import com.eas.client.form.events.AddHandler;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.RemoveEvent;
import com.eas.client.form.events.RemoveHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasAddHandlers;
import com.eas.client.form.events.HasRemoveHandlers;
import com.bearsoft.gwt.ui.containers.window.events.HasMoveHandlers;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.widgets.PlatypusCheckBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public class EventsExecutor implements MouseOutHandler, MouseOverHandler, MouseDownHandler, MouseUpHandler, MouseWheelHandler, MouseMoveHandler, KeyDownHandler, KeyUpHandler,
        KeyPressHandler, FocusHandler, ShowHandler, ResizeHandler, HideHandler, RemoveHandler, MoveHandler<WindowUI>, AddHandler, BlurHandler, SelectionHandler<Widget>, ClickHandler, DoubleClickHandler,
        ChangeHandler {

	private JavaScriptObject actionPerformed;
	private JavaScriptObject mouseExited;
	private JavaScriptObject mouseClicked;
	private JavaScriptObject mousePressed;
	private JavaScriptObject mouseReleased;
	private JavaScriptObject mouseEntered;
	private JavaScriptObject mouseWheelMoved;
	private JavaScriptObject mouseDragged;
	private JavaScriptObject mouseMoved;
	private JavaScriptObject stateChanged;// TODO: Make it clear if it is about tabs, radio group or a combo items.
	private JavaScriptObject componentResized;
	private JavaScriptObject componentMoved;
	private JavaScriptObject componentShown;
	private JavaScriptObject componentHidden;
	private JavaScriptObject componentAdded;
	private JavaScriptObject componentRemoved;
	private JavaScriptObject focusGained;
	private JavaScriptObject focusLost;
	private JavaScriptObject keyTyped;
	private JavaScriptObject keyPressed;
	private JavaScriptObject keyReleased;

	private static enum MOUSE {
		NULL, PRESSED, MOVED, DRAGGED
	}

	public static EventsExecutor createExecutor(Widget aComponent, JavaScriptObject aEventsThis) throws Exception {
		final EventsExecutor executor = new EventsExecutor(aComponent, aEventsThis);

		if (aComponent instanceof HasChangeHandlers)
			((HasChangeHandlers) aComponent).addChangeHandler(executor);

		if (aComponent instanceof HasShowHandlers)
			((HasShowHandlers) aComponent).addShowHandler(executor);

		if (aComponent instanceof HasResizeHandlers)
			((HasResizeHandlers) aComponent).addResizeHandler(executor);

		if (aComponent instanceof HasHideHandlers)
			((HasHideHandlers) aComponent).addHideHandler(executor);

		if (aComponent instanceof HasRemoveHandlers)
			((HasRemoveHandlers) aComponent).addRemoveHandler(executor);

		if (aComponent instanceof HasAddHandlers)
			((HasAddHandlers) aComponent).addAddHandler(executor);

		if (aComponent instanceof HasMoveHandlers<?>)
			((HasMoveHandlers<WindowUI>) aComponent).addMoveHandler(executor);

		if (aComponent instanceof HasSelectionHandlers<?>)
			((HasSelectionHandlers<Widget>) aComponent).addSelectionHandler(executor);

		return executor;
	}

	private MOUSE mouseState = MOUSE.NULL;

	private Widget component;
	private JavaScriptObject eventThis;

	public EventsExecutor(Widget aComponent, JavaScriptObject aEventsThis) {
		super();
		component = aComponent;
		eventThis = aEventsThis;
	}

	public void setEventThis(JavaScriptObject aValue) {
		eventThis = aValue;
	}

	public JavaScriptObject getActionPerformed() {
		return actionPerformed;
	}

	public JavaScriptObject getMouseExited() {
		return mouseExited;
	}

	public JavaScriptObject getMouseClicked() {
		return mouseClicked;
	}

	public JavaScriptObject getMousePressed() {
		return mousePressed;
	}

	public JavaScriptObject getMouseReleased() {
		return mouseReleased;
	}

	public JavaScriptObject getMouseEntered() {
		return mouseEntered;
	}

	public JavaScriptObject getMouseWheelMoved() {
		return mouseWheelMoved;
	}

	public JavaScriptObject getMouseDragged() {
		return mouseDragged;
	}

	public JavaScriptObject getMouseMoved() {
		return mouseMoved;
	}

	public JavaScriptObject getStateChanged() {
		return stateChanged;
	}

	public JavaScriptObject getComponentResized() {
		return componentResized;
	}

	public JavaScriptObject getComponentMoved() {
		return componentMoved;
	}

	public JavaScriptObject getComponentShown() {
		return componentShown;
	}

	public JavaScriptObject getComponentHidden() {
		return componentHidden;
	}

	public JavaScriptObject getComponentAdded() {
		return componentAdded;
	}

	public JavaScriptObject getComponentRemoved() {
		return componentRemoved;
	}

	/*
	 * public JavaScriptObject getItemStateChanged() { return itemStateChanged;
	 * }
	 */

	public JavaScriptObject getFocusGained() {
		return focusGained;
	}

	public JavaScriptObject getFocusLost() {
		return focusLost;
	}

	/*
	 * public JavaScriptObject getPropertyChange() { return propertyChange; }
	 */
	public JavaScriptObject getKeyTyped() {
		return keyTyped;
	}

	public JavaScriptObject getKeyPressed() {
		return keyPressed;
	}

	public JavaScriptObject getKeyReleased() {
		return keyReleased;
	}

	protected HandlerRegistration mouseOutReg;

	public void setMouseExited(JavaScriptObject aValue) {
		if (mouseExited != aValue) {
			if (mouseOutReg != null)
				mouseOutReg.removeHandler();
			mouseExited = aValue;
			if (mouseExited != null)
				mouseOutReg = component.addDomHandler(this, MouseOutEvent.getType());
		}
	}

	public void setActionPerformed(JavaScriptObject aValue) {
		actionPerformed = aValue;
	}

	protected HandlerRegistration mouseClickedReg;
	protected HandlerRegistration mouseDblClickedReg;

	public void setMouseClicked(JavaScriptObject aValue) {
		if (mouseClicked != aValue) {
			if (mouseClickedReg != null)
				mouseClickedReg.removeHandler();
			if (mouseDblClickedReg != null)
				mouseDblClickedReg.removeHandler();
			mouseClicked = aValue;
			if (mouseClicked != null) {
				mouseClickedReg = component.addDomHandler(this, ClickEvent.getType());
				mouseDblClickedReg = component.addDomHandler(this, DoubleClickEvent.getType());
			}
		}
	}

	protected HandlerRegistration mouseDownReg;

	public void setMousePressed(JavaScriptObject aValue) {
		if (mousePressed != aValue) {
			if (mouseDownReg != null)
				mouseDownReg.removeHandler();
			mousePressed = aValue;
			if (mousePressed != null)
				mouseDownReg = component.addDomHandler(this, MouseDownEvent.getType());
		}
	}

	protected HandlerRegistration mouseUpReg;

	public void setMouseReleased(JavaScriptObject aValue) {
		if (mouseReleased != aValue) {
			if (mouseUpReg != null)
				mouseUpReg.removeHandler();
			mouseReleased = aValue;
			if (mouseReleased != null)
				mouseUpReg = component.addDomHandler(this, MouseUpEvent.getType());
		}
	}

	protected HandlerRegistration mouseOverReg;

	public void setMouseEntered(JavaScriptObject aValue) {
		if (mouseEntered != aValue) {
			if (mouseOverReg != null)
				mouseOverReg.removeHandler();
			mouseEntered = aValue;
			if (mouseEntered != null)
				mouseOverReg = component.addDomHandler(this, MouseOverEvent.getType());
		}
	}

	protected HandlerRegistration mouseWheelReg;

	public void setMouseWheelMoved(JavaScriptObject aValue) {
		if (mouseWheelMoved != aValue) {
			if (mouseWheelReg != null)
				mouseWheelReg.removeHandler();
			mouseWheelMoved = aValue;
			if (mouseWheelMoved != null)
				mouseWheelReg = component.addDomHandler(this, MouseWheelEvent.getType());
		}
	}

	protected HandlerRegistration mouseMoveReg;

	public void setMouseMoved(JavaScriptObject aValue) {
		if (mouseMoved != aValue) {
			if (mouseMoveReg != null)
				mouseMoveReg.removeHandler();
			mouseMoved = aValue;
			if (mouseMoved != null)
				mouseMoveReg = component.addDomHandler(this, MouseMoveEvent.getType());
		}
	}

	public void setStateChanged(JavaScriptObject aValue) {
		stateChanged = aValue;
	}

	public void setComponentResized(JavaScriptObject aValue) {
		componentResized = aValue;
	}

	public void setComponentMoved(JavaScriptObject aValue) {
		componentMoved = aValue;
	}

	public void setComponentShown(JavaScriptObject aValue) {
		componentShown = aValue;
	}

	public void setComponentHidden(JavaScriptObject aValue) {
		componentHidden = aValue;
	}

	public void setComponentAdded(JavaScriptObject aValue) {
		componentAdded = aValue;
	}

	public void setComponentRemoved(JavaScriptObject aValue) {
		componentRemoved = aValue;
	}

	public void setMouseDragged(JavaScriptObject aValue) {
		mouseDragged = aValue;
	}

	protected HandlerRegistration focusReg;

	public void setFocusGained(JavaScriptObject aValue) {
		if (focusGained != aValue) {
			if (focusReg != null)
				focusReg.removeHandler();
			focusGained = aValue;
			if (focusGained != null && component instanceof HasFocusHandlers) {
				focusReg = ((HasFocusHandlers) component).addFocusHandler(this);
			}
		}
	}
	
	protected HandlerRegistration blurReg;

	public void setFocusLost(JavaScriptObject aValue) {
		if (focusLost != aValue) {
			if (blurReg != null)
				blurReg.removeHandler();
			focusLost = aValue;
			if (focusLost != null && component instanceof HasBlurHandlers) {
				blurReg = ((HasBlurHandlers) component).addBlurHandler(this);
			}
		}
	}
	
	public void setFocusLost1(JavaScriptObject aValue) {
		focusLost = aValue;
	}

	protected HandlerRegistration keyTypedReg;

	public void setKeyTyped(JavaScriptObject aValue) {
		if (keyTyped != aValue) {
			if (keyTypedReg != null)
				keyTypedReg.removeHandler();
			keyTyped = aValue;
			if (keyTyped != null && component instanceof HasKeyPressHandlers) {
				keyTypedReg = ((HasKeyPressHandlers) component).addKeyPressHandler(this);
			}
		}
	}

	protected HandlerRegistration keyDownReg;

	public void setKeyPressed(JavaScriptObject aValue) {
		if (keyPressed != aValue) {
			if (keyDownReg != null)
				keyDownReg.removeHandler();
			keyPressed = aValue;
			if (keyPressed != null && component instanceof HasKeyDownHandlers) {
				keyDownReg = ((HasKeyDownHandlers) component).addKeyDownHandler(this);
			}
		}
	}

	protected HandlerRegistration keyUpReg;

	public void setKeyReleased(JavaScriptObject aValue) {
		if (keyReleased != aValue) {
			if (keyUpReg != null)
				keyUpReg.removeHandler();
			keyReleased = aValue;
			if (keyReleased != null && component instanceof HasKeyUpHandlers) {
				keyUpReg = ((HasKeyUpHandlers) component).addKeyUpHandler(this);
			}
		}
	}

	protected void executeEvent(JavaScriptObject aModule, JavaScriptObject aHandler, JavaScriptObject aEvent) {
		try {
			Utils.executeScriptEventVoid(eventThis, aHandler, aEvent);
		} catch (Exception e) {
			Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		executeEvent(eventThis, actionPerformed, JsEvents.publishChangeEvent(event));
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (mouseEntered != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseEntered, JsEvents.publishMouseOverEvent(event));
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (mouseExited != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseExited, JsEvents.publishMouseOutEvent(event));
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (mousePressed != null) {
			event.stopPropagation();
			// Event.setCapture(event.getRelativeElement());
			mouseState = MOUSE.PRESSED;
			executeEvent(eventThis, mousePressed, JsEvents.publishMouseDownEvent(event));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (mouseMoved != null || mouseDragged != null) {
			event.stopPropagation();
			if (mouseState == MOUSE.NULL || mouseState == MOUSE.MOVED) {
				mouseState = MOUSE.MOVED;
				executeEvent(eventThis, mouseMoved, null);
			} else if (mouseState == MOUSE.PRESSED || mouseState == MOUSE.DRAGGED) {
				mouseState = MOUSE.DRAGGED;
				executeEvent(eventThis, mouseDragged, JsEvents.publishMouseMoveEvent(event));
			}
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// if (mouseState == MOUSE.PRESSED)
		// Event.releaseCapture(event.getRelativeElement());
		if (mouseReleased != null) {
			event.stopPropagation();
			mouseState = MOUSE.NULL;
			executeEvent(eventThis, mouseReleased, JsEvents.publishMouseUpEvent(event));
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (mouseClicked != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseClicked, JsEvents.publishClickEvent(event));
		}
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if (mouseClicked != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseClicked, JsEvents.publishDoubleClickEvent(event));
		}
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if (mouseWheelMoved != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseWheelMoved, JsEvents.publishMouseWheelEvent(event));
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (keyTyped != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyTyped, JsEvents.publishKeyPressEvent(event));
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (keyReleased != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyReleased, JsEvents.publishKeyUpEvent(event));
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (keyPressed != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyPressed, JsEvents.publishKeyDownEvent(event));
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		if (focusGained != null) {
			executeEvent(eventThis, focusGained, JsEvents.publishFocusEvent(event));
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (focusLost != null) {
			executeEvent(eventThis, focusLost, JsEvents.publishBlurEvent(event));
		}
		mouseState = MOUSE.NULL;
	}

	@Override
	public void onShow(ShowEvent event) {
		if (componentShown != null) {
			executeEvent(eventThis, componentShown, JsEvents.publishShowEvent(event));
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (componentResized != null) {
			executeEvent(eventThis, componentResized, JsEvents.publishResizeEvent(event));
		}
	}

	@Override
	public void onHide(HideEvent event) {
		if (componentHidden != null) {
			executeEvent(eventThis, componentHidden, JsEvents.publishHideEvent(event));
		}
	}

	@Override
	public void onRemove(RemoveEvent event) {
		if (componentRemoved != null) {
			executeEvent(eventThis, componentRemoved, JsEvents.publishRemoveEvent(event));
		}
	}

	@Override
	public void onAdd(AddEvent event) {
		if (componentAdded != null) {
			executeEvent(eventThis, componentAdded, JsEvents.publishAddEvent(event));
		}
	}

	@Override
	public void onMove(MoveEvent<WindowUI> event) {
		if (componentMoved != null) {
			executeEvent(eventThis, componentMoved, JsEvents.publishMoveEvent(event));
		}
	}

	/**
	 * Intended to implement active tabs change event.
	 */
	@Override
	public void onSelection(SelectionEvent<Widget> event) {
			if (stateChanged != null) {
				JavaScriptObject publishedEvent = JsEvents.publishChangeEvent(event);
				executeEvent(eventThis, stateChanged, publishedEvent);
			}
	}
}

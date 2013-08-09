package com.eas.client.gxtcontrols.events;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.Utils;
import com.eas.client.form.api.JSEvents;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.AddEvent.AddHandler;
import com.sencha.gxt.widget.core.client.event.AddEvent.HasAddHandlers;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.BlurEvent.HasBlurHandlers;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent.HasFocusHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HasHideHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent.HasMoveHandlers;
import com.sencha.gxt.widget.core.client.event.MoveEvent.MoveHandler;
import com.sencha.gxt.widget.core.client.event.RemoveEvent;
import com.sencha.gxt.widget.core.client.event.RemoveEvent.HasRemoveHandlers;
import com.sencha.gxt.widget.core.client.event.RemoveEvent.RemoveHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.HasSelectHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.HasShowHandlers;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class GxtEventsExecutor implements SelectHandler, MouseOutHandler, MouseOverHandler, MouseDownHandler, MouseUpHandler, MouseWheelHandler, MouseMoveHandler, KeyDownHandler, KeyUpHandler,
        KeyPressHandler, FocusHandler, ShowHandler, ResizeHandler, HideHandler, RemoveHandler, MoveHandler, AddHandler, BlurHandler, SelectionHandler<Widget>, ClickHandler, DoubleClickHandler,
        ChangeHandler {

	private static final String HANDLER_DATA_NAME = "handler";
	private JavaScriptObject actionPerformed;
	private JavaScriptObject mouseExited;
	private JavaScriptObject mouseClicked;
	private JavaScriptObject mousePressed;
	private JavaScriptObject mouseReleased;
	private JavaScriptObject mouseEntered;
	private JavaScriptObject mouseWheelMoved;
	private JavaScriptObject mouseDragged;
	private JavaScriptObject mouseMoved;
	private JavaScriptObject stateChanged;
	private JavaScriptObject componentResized;
	private JavaScriptObject componentMoved;
	private JavaScriptObject componentShown;
	private JavaScriptObject componentHidden;
	private JavaScriptObject componentAdded;
	private JavaScriptObject componentRemoved;
	// private JavaScriptObject itemStateChanged;
	private JavaScriptObject focusGained;
	private JavaScriptObject focusLost;
	// private JavaScriptObject propertyChange;
	private JavaScriptObject keyTyped;
	private JavaScriptObject keyPressed;
	private JavaScriptObject keyReleased;

	private static enum MOUSE {
		NULL, PRESSED, MOVED, DRAGGED
	}

	public static GxtEventsExecutor get(Component aComponent) {
		return aComponent.getData(HANDLER_DATA_NAME);
	}

	public static GxtEventsExecutor createExecutor(Component aComponent, JavaScriptObject aEventsThis) throws Exception {
		final GxtEventsExecutor executor = new GxtEventsExecutor(aComponent, aEventsThis);
		aComponent.setData(HANDLER_DATA_NAME, executor);

		if (aComponent instanceof HasSelectHandlers)
			((HasSelectHandlers) aComponent).addSelectHandler(executor);

		if (aComponent instanceof PlatypusCheckBox) {
			final PlatypusCheckBox pcheck = (PlatypusCheckBox) aComponent;
			pcheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					PlatypusButtonGroup pgroup = pcheck.getButtonGroup();
					// Check boxes can process actionPerfomed events only when
					// they are standalone within no groups.
					if (pgroup == null)
						executor.onSelect(new SurrogateSelectEvent(event.getSource()));
				}
			});
		}
		/*
		if (aComponent instanceof Field<?>)
		{
			FieldCell<?> fc = ((Field<?>)aComponent).getCell();
			if(fc instanceof ValueBaseInputCell<?>){
				ValueBaseInputCell<?> vc = (ValueBaseInputCell<?>)fc;
				InputElement ie = vc.getInputElement(aComponent.getElement());
				
			}
		}
		*/

		aComponent.addDomHandler(executor, MouseOverEvent.getType());
		aComponent.addDomHandler(executor, MouseOutEvent.getType());
		aComponent.addDomHandler(executor, MouseDownEvent.getType());
		aComponent.addDomHandler(executor, MouseUpEvent.getType());
		aComponent.addDomHandler(executor, MouseWheelEvent.getType());
		aComponent.addDomHandler(executor, MouseMoveEvent.getType());
		aComponent.addDomHandler(executor, ClickEvent.getType());
		aComponent.addDomHandler(executor, DoubleClickEvent.getType());

		if (aComponent instanceof HasKeyDownHandlers)
			((HasKeyDownHandlers) aComponent).addKeyDownHandler(executor);

		if (aComponent instanceof HasKeyUpHandlers)
			((HasKeyUpHandlers) aComponent).addKeyUpHandler(executor);

		if (aComponent instanceof HasKeyPressHandlers)
			((HasKeyPressHandlers) aComponent).addKeyPressHandler(executor);

		if (aComponent instanceof HasFocusHandlers)
			((HasFocusHandlers) aComponent).addFocusHandler(executor);

		if (aComponent instanceof HasBlurHandlers)
			((HasBlurHandlers) aComponent).addBlurHandler(executor);

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

		if (aComponent instanceof HasMoveHandlers)
			((HasMoveHandlers) aComponent).addMoveHandler(executor);

		if (aComponent instanceof HasSelectionHandlers<?>)
			((HasSelectionHandlers<Widget>) aComponent).addSelectionHandler(executor);

		return executor;
	}

	private MOUSE mouseState = MOUSE.NULL;

	private Component component;
	private JavaScriptObject eventThis;

	public GxtEventsExecutor(Component aComponent, JavaScriptObject aEventThis) {
		super();
		component = aComponent;
		eventThis = aEventThis;
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

	public void setMouseExited(JavaScriptObject aValue) {
		mouseExited = aValue;
	}

	public void setActionPerformed(JavaScriptObject aValue) {
		actionPerformed = aValue;
	}

	public void setMouseClicked(JavaScriptObject aValue) {
		mouseClicked = aValue;
	}

	public void setMousePressed(JavaScriptObject aValue) {
		mousePressed = aValue;
	}

	public void setMouseReleased(JavaScriptObject aValue) {
		mouseReleased = aValue;
	}

	public void setMouseEntered(JavaScriptObject aValue) {
		mouseEntered = aValue;
	}

	public void setMouseWheelMoved(JavaScriptObject aValue) {
		mouseWheelMoved = aValue;
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

	public void setMouseMoved(JavaScriptObject aValue) {
		mouseMoved = aValue;
	}

	/*
	 * public void setItemStateChanged(JavaScriptObject aValue) {
	 * itemStateChanged = aValue; }
	 */

	public void setFocusGained(JavaScriptObject aValue) {
		focusGained = aValue;
	}

	public void setFocusLost(JavaScriptObject aValue) {
		focusLost = aValue;
	}

	/*
	 * public void setPropertyChange(JavaScriptObject aValue) { propertyChange =
	 * aValue; }
	 */

	public void setKeyTyped(JavaScriptObject aValue) {
		keyTyped = aValue;
	}

	public void setKeyPressed(JavaScriptObject aValue) {
		keyPressed = aValue;
	}

	public void setKeyReleased(JavaScriptObject aValue) {
		keyReleased = aValue;
	}

	protected void executeEvent(JavaScriptObject aModule, JavaScriptObject aHandler, JavaScriptObject aEvent) {
		try {
			Utils.executeScriptEventVoid(eventThis, aHandler, aEvent);
		} catch (Exception e) {
			Logger.getLogger(GxtEventsExecutor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	public void onSelect(SelectEvent event) {
		executeEvent(eventThis, actionPerformed, JSEvents.publishSelectEvent(event));
	}

	@Override
	public void onChange(ChangeEvent event) {
		executeEvent(eventThis, actionPerformed, JSEvents.publishChangeEvent(event));
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (mouseEntered != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseEntered, JSEvents.publishMouseOverEvent(event));
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (mouseExited != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseExited, JSEvents.publishMouseOutEvent(event));
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (mousePressed != null) {
			event.stopPropagation();
			Event.setCapture(event.getRelativeElement());
			mouseState = MOUSE.PRESSED;
			executeEvent(eventThis, mousePressed, JSEvents.publishMouseDownEvent(event));
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
				executeEvent(eventThis, mouseDragged, JSEvents.publishMouseMoveEvent(event));
			}
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		Event.releaseCapture(event.getRelativeElement());
		if (mouseReleased != null) {
			event.stopPropagation();
			mouseState = MOUSE.NULL;
			executeEvent(eventThis, mouseReleased, JSEvents.publishMouseUpEvent(event));
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (mouseClicked != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseClicked, JSEvents.publishClickEvent(event));
		}
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if (mouseClicked != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseClicked, JSEvents.publishDoubleClickEvent(event));
		}
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if (mouseWheelMoved != null) {
			event.stopPropagation();
			executeEvent(eventThis, mouseWheelMoved, JSEvents.publishMouseWheelEvent(event));
		}
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (keyTyped != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyTyped, JSEvents.publishKeyPressEvent(event));
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (keyReleased != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyReleased, JSEvents.publishKeyUpEvent(event));
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (keyPressed != null) {
			event.stopPropagation();
			executeEvent(eventThis, keyPressed, JSEvents.publishKeyDownEvent(event));
		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		if (focusGained != null) {
			executeEvent(eventThis, focusGained, JSEvents.publishFocusEvent(event));
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (focusLost != null) {
			executeEvent(eventThis, focusLost, JSEvents.publishBlurEvent(event));
		}
		mouseState = MOUSE.NULL;
	}

	@Override
	public void onShow(ShowEvent event) {
		if (componentShown != null) {
			executeEvent(eventThis, componentShown, JSEvents.publishShowEvent(event));
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (componentResized != null) {
			executeEvent(eventThis, componentResized, JSEvents.publishResizeEvent(event));
		}
	}

	@Override
	public void onHide(HideEvent event) {
		if (componentHidden != null) {
			executeEvent(eventThis, componentHidden, JSEvents.publishHideEvent(event));
		}
	}

	@Override
	public void onRemove(RemoveEvent event) {
		if (componentRemoved != null) {
			executeEvent(eventThis, componentRemoved, JSEvents.publishRemoveEvent(event));
		}
	}

	@Override
	public void onAdd(AddEvent event) {
		if (componentAdded != null) {
			executeEvent(eventThis, componentAdded, JSEvents.publishAddEvent(event));
		}
	}

	@Override
	public void onMove(MoveEvent event) {
		if (componentMoved != null) {
			executeEvent(eventThis, componentMoved, JSEvents.publishMoveEvent(event));
		}
	}

	public static class SurrogateSelectEvent extends SelectEvent {

		public SurrogateSelectEvent(Object aSource) {
			super();
			setSource(aSource);
		}
	}

	/**
	 * Intended to implement active tabs change event and to regularize menu
	 * items selection.
	 */
	@Override
	public void onSelection(SelectionEvent<Widget> event) {
		if (event.getSource() instanceof MenuItem) {
			onSelect(new SurrogateSelectEvent(event.getSource()));
		} else {
			if (stateChanged != null) {
				JavaScriptObject publishedEvent = JSEvents.publishChangeEvent(event);
				executeEvent(eventThis, stateChanged, publishedEvent);
			}
		}

	}
}

package com.eas.client.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.containers.window.events.HasMoveHandlers;
import com.bearsoft.gwt.ui.containers.window.events.MoveEvent;
import com.bearsoft.gwt.ui.containers.window.events.MoveHandler;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.events.AddEvent;
import com.eas.client.form.events.AddHandler;
import com.eas.client.form.events.HasAddHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasRemoveHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.RemoveEvent;
import com.eas.client.form.events.RemoveHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.js.JsEvents;
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public class EventsExecutor {

	private JavaScriptObject actionPerformed;
	private JavaScriptObject mouseExited;
	private JavaScriptObject mouseClicked;
	private JavaScriptObject mousePressed;
	private JavaScriptObject mouseReleased;
	private JavaScriptObject mouseEntered;
	private JavaScriptObject mouseWheelMoved;
	private JavaScriptObject mouseDragged;
	private JavaScriptObject mouseMoved;
	private JavaScriptObject stateChanged;// TODO: Make it clear if it is about
	                                      // tabs, radio group or a combo items.
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

	public JavaScriptObject getFocusGained() {
		return focusGained;
	}

	public JavaScriptObject getFocusLost() {
		return focusLost;
	}

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
			if (mouseOutReg != null) {
				mouseOutReg.removeHandler();
				mouseOutReg = null;
			}
			mouseExited = aValue;
			if (mouseExited != null)
				mouseOutReg = component.addDomHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						if (mouseExited != null) {
							event.stopPropagation();
							executeEvent(mouseExited, JsEvents.publish(event));
						}
					}
				}, MouseOutEvent.getType());
		}
	}

	public void setActionPerformed(JavaScriptObject aValue) {
		actionPerformed = aValue;
	}

	protected HandlerRegistration mouseClickedReg;
	protected HandlerRegistration mouseDblClickedReg;

	public void setMouseClicked(JavaScriptObject aValue) {
		if (mouseClicked != aValue) {
			if (mouseClickedReg != null) {
				mouseClickedReg.removeHandler();
				mouseClickedReg = null;
			}
			if (mouseDblClickedReg != null) {
				mouseDblClickedReg.removeHandler();
				mouseDblClickedReg = null;
			}
			mouseClicked = aValue;
			if (mouseClicked != null) {
				mouseClickedReg = component.addDomHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (mouseClicked != null) {
							event.stopPropagation();
							executeEvent(mouseClicked, JsEvents.publish(event));
						}
					}
				}, ClickEvent.getType());
				mouseDblClickedReg = component.addDomHandler(new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						if (mouseClicked != null) {
							event.stopPropagation();
							executeEvent(mouseClicked, JsEvents.publish(event));
						}
					}
				}, DoubleClickEvent.getType());
			}
		}
	}

	protected HandlerRegistration mouseDownReg;

	public void setMousePressed(JavaScriptObject aValue) {
		if (mousePressed != aValue) {
			if (mouseDownReg != null) {
				mouseDownReg.removeHandler();
				mouseDownReg = null;
			}
			mousePressed = aValue;
			if (mousePressed != null)
				mouseDownReg = component.addDomHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						if (mousePressed != null) {
							event.stopPropagation();
							// Event.setCapture(event.getRelativeElement());
							mouseState = MOUSE.PRESSED;
							executeEvent(mousePressed, JsEvents.publish(event));
						}
					}
				}, MouseDownEvent.getType());
		}
	}

	protected HandlerRegistration mouseUpReg;

	public void setMouseReleased(JavaScriptObject aValue) {
		if (mouseReleased != aValue) {
			if (mouseUpReg != null) {
				mouseUpReg.removeHandler();
				mouseUpReg = null;
			}
			mouseReleased = aValue;
			if (mouseReleased != null)
				mouseUpReg = component.addDomHandler(new MouseUpHandler() {
					@Override
					public void onMouseUp(MouseUpEvent event) {
						// if (mouseState == MOUSE.PRESSED)
						// Event.releaseCapture(event.getRelativeElement());
						if (mouseReleased != null) {
							event.stopPropagation();
							mouseState = MOUSE.NULL;
							executeEvent(mouseReleased, JsEvents.publish(event));
						}
					}
				}, MouseUpEvent.getType());
		}
	}

	protected HandlerRegistration mouseOverReg;

	public void setMouseEntered(JavaScriptObject aValue) {
		if (mouseEntered != aValue) {
			if (mouseOverReg != null) {
				mouseOverReg.removeHandler();
				mouseOverReg = null;
			}
			mouseEntered = aValue;
			if (mouseEntered != null)
				mouseOverReg = component.addDomHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						if (mouseEntered != null) {
							event.stopPropagation();
							executeEvent(mouseEntered, JsEvents.publish(event));
						}
					}
				}, MouseOverEvent.getType());
		}
	}

	protected HandlerRegistration mouseWheelReg;

	public void setMouseWheelMoved(JavaScriptObject aValue) {
		if (mouseWheelMoved != aValue) {
			if (mouseWheelReg != null) {
				mouseWheelReg.removeHandler();
				mouseWheelReg = null;
			}
			mouseWheelMoved = aValue;
			if (mouseWheelMoved != null)
				mouseWheelReg = component.addDomHandler(new MouseWheelHandler() {
					@Override
					public void onMouseWheel(MouseWheelEvent event) {
						if (mouseWheelMoved != null) {
							event.stopPropagation();
							executeEvent(mouseWheelMoved, JsEvents.publish(event));
						}
					}
				}, MouseWheelEvent.getType());
		}
	}

	protected HandlerRegistration mouseMoveReg;

	public void setMouseMoved(JavaScriptObject aValue) {
		if (mouseMoved != aValue) {
			if (mouseMoveReg != null) {
				mouseMoveReg.removeHandler();
				mouseMoveReg = null;
			}
			mouseMoved = aValue;
			if (mouseMoved != null)
				mouseMoveReg = component.addDomHandler(new MouseMoveHandler() {
					@Override
					public void onMouseMove(MouseMoveEvent event) {
						if (mouseMoved != null || mouseDragged != null) {
							event.stopPropagation();
							if (mouseState == MOUSE.NULL || mouseState == MOUSE.MOVED) {
								mouseState = MOUSE.MOVED;
								executeEvent(mouseMoved, null);
							} else if (mouseState == MOUSE.PRESSED || mouseState == MOUSE.DRAGGED) {
								mouseState = MOUSE.DRAGGED;
								executeEvent(mouseDragged, JsEvents.publish(event));
							}
						}
					}

				}, MouseMoveEvent.getType());
		}
	}

	protected HandlerRegistration stateChangedReg;
	protected HandlerRegistration selectionChangedReg;

	public void setStateChanged(JavaScriptObject aValue) {
		if (stateChanged != aValue) {
			if (stateChangedReg != null) {
				stateChangedReg.removeHandler();
				stateChangedReg = null;
			}
			if (selectionChangedReg != null) {
				selectionChangedReg.removeHandler();
				selectionChangedReg = null;
			}
			stateChanged = aValue;
			if (stateChanged != null) {
				if (component instanceof HasChangeHandlers)
					stateChangedReg = ((HasChangeHandlers) component).addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							executeEvent(actionPerformed, JsEvents.publish(event));
						}

					});
				if (component instanceof HasSelectionHandlers<?>)
					selectionChangedReg = ((HasSelectionHandlers<Object>) component).addSelectionHandler(new SelectionHandler<Object>() {
						@Override
						public void onSelection(SelectionEvent<Object> event) {
							if (stateChanged != null) {
								JavaScriptObject publishedEvent = JsEvents.publish(event);
								executeEvent(stateChanged, publishedEvent);
							}
						}
					});
			}
		}
	}

	protected HandlerRegistration componentResizedReg;

	public void setComponentResized(JavaScriptObject aValue) {
		if (componentResized != aValue) {
			if (componentResizedReg != null) {
				componentResizedReg.removeHandler();
				componentResizedReg = null;
			}
			componentResized = aValue;
			if (componentResized != null && component instanceof HasResizeHandlers)
				componentResizedReg = ((HasResizeHandlers) component).addResizeHandler(new ResizeHandler() {
					@Override
					public void onResize(ResizeEvent event) {
						if (componentResized != null) {
							executeEvent(componentResized, JsEvents.publish(event));
						}
					}
				});
		}
	}

	protected HandlerRegistration componentMovedReg;

	public void setComponentMoved(JavaScriptObject aValue) {
		if (componentMoved != aValue) {
			if (componentMovedReg != null) {
				componentMovedReg.removeHandler();
				componentMovedReg = null;
			}
			componentMoved = aValue;
			if (componentMoved != null && component instanceof HasMoveHandlers<?>)
				componentMovedReg = ((HasMoveHandlers<Object>) component).addMoveHandler(new MoveHandler<Object>() {
					@Override
					public void onMove(MoveEvent<Object> event) {
						if (componentMoved != null) {
							executeEvent(componentMoved, JsEvents.publish(event));
						}
					}
				});
		}
	}

	protected HandlerRegistration componentShownReg;

	public void setComponentShown(JavaScriptObject aValue) {
		if (componentShown != aValue) {
			if (componentShownReg != null) {
				componentShownReg.removeHandler();
				componentShownReg = null;
			}
			componentShown = aValue;
			if (componentShown != null && component instanceof HasShowHandlers)
				componentShownReg = ((HasShowHandlers) component).addShowHandler(new ShowHandler() {
					@Override
					public void onShow(ShowEvent event) {
						if (componentShown != null) {
							executeEvent(componentShown, JsEvents.publish(event));
						}
					}
				});
		}
	}

	protected HandlerRegistration componentHiddenReg;

	public void setComponentHidden(JavaScriptObject aValue) {
		if (componentHidden != aValue) {
			if (componentHiddenReg != null) {
				componentHiddenReg.removeHandler();
				componentHiddenReg = null;
			}
			componentHidden = aValue;
			if (componentHidden != null && component instanceof HasHideHandlers)
				componentHiddenReg = ((HasHideHandlers) component).addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						if (componentHidden != null) {
							executeEvent(componentHidden, JsEvents.publish(event));
						}
					}
				});
		}
	}

	protected HandlerRegistration componentAddedReg;

	public void setComponentAdded(JavaScriptObject aValue) {
		if (componentAdded != aValue) {
			if (componentAddedReg != null) {
				componentAddedReg.removeHandler();
				componentAddedReg = null;
			}
			componentAdded = aValue;
			if (componentAdded != null && component instanceof HasAddHandlers)
				componentAddedReg = ((HasAddHandlers) component).addAddHandler(new AddHandler() {
					@Override
					public void onAdd(AddEvent event) {
						if (componentAdded != null) {
							executeEvent(componentAdded, JsEvents.publish(event));
						}
					}
				});
		}
	}

	protected HandlerRegistration componentRemovedReg;

	public void setComponentRemoved(JavaScriptObject aValue) {
		if (componentRemoved != aValue) {
			if (componentRemovedReg != null) {
				componentRemovedReg.removeHandler();
				componentRemovedReg = null;
			}
			componentRemoved = aValue;
			if (componentRemoved != null && component instanceof HasRemoveHandlers)
				componentRemovedReg = ((HasRemoveHandlers) component).addRemoveHandler(new RemoveHandler() {
					@Override
					public void onRemove(RemoveEvent event) {
						if (componentRemoved != null) {
							executeEvent(componentRemoved, JsEvents.publish(event));
						}
					}
				});
		}
	}

	public void setMouseDragged(JavaScriptObject aValue) {
		mouseDragged = aValue;
	}

	protected HandlerRegistration focusReg;

	public void setFocusGained(JavaScriptObject aValue) {
		if (focusGained != aValue) {
			if (focusReg != null) {
				focusReg.removeHandler();
				focusReg = null;
			}
			focusGained = aValue;
			if (focusGained != null && component instanceof HasFocusHandlers) {
				focusReg = ((HasFocusHandlers) component).addFocusHandler(new FocusHandler() {
					@Override
					public void onFocus(FocusEvent event) {
						if (focusGained != null) {
							executeEvent(focusGained, JsEvents.publish(event));
						}
					}
				});
			}
		}
	}

	protected HandlerRegistration blurReg;

	public void setFocusLost(JavaScriptObject aValue) {
		if (focusLost != aValue) {
			if (blurReg != null) {
				blurReg.removeHandler();
				blurReg = null;
			}
			focusLost = aValue;
			if (focusLost != null && component instanceof HasBlurHandlers) {
				blurReg = ((HasBlurHandlers) component).addBlurHandler(new BlurHandler() {
					@Override
					public void onBlur(BlurEvent event) {
						if (focusLost != null) {
							executeEvent(focusLost, JsEvents.publish(event));
						}
						mouseState = MOUSE.NULL;
					}
				});
			}
		}
	}

	protected HandlerRegistration keyTypedReg;

	public void setKeyTyped(JavaScriptObject aValue) {
		if (keyTyped != aValue) {
			if (keyTypedReg != null) {
				keyTypedReg.removeHandler();
				keyTypedReg = null;
			}
			keyTyped = aValue;
			if (keyTyped != null && component instanceof HasKeyPressHandlers) {
				keyTypedReg = ((HasKeyPressHandlers) component).addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						if (keyTyped != null) {
							event.stopPropagation();
							executeEvent(keyTyped, JsEvents.publish(event));
						}
					}
				});
			}
		}
	}

	protected HandlerRegistration keyDownReg;

	public void setKeyPressed(JavaScriptObject aValue) {
		if (keyPressed != aValue) {
			if (keyDownReg != null) {
				keyDownReg.removeHandler();
				keyDownReg = null;
			}
			keyPressed = aValue;
			if (keyPressed != null && component instanceof HasKeyDownHandlers) {
				keyDownReg = ((HasKeyDownHandlers) component).addKeyDownHandler(new KeyDownHandler() {
					@Override
					public void onKeyDown(KeyDownEvent event) {
						if (keyPressed != null) {
							event.stopPropagation();
							executeEvent(keyPressed, JsEvents.publish(event));
						}
					}
				});
			}
		}
	}

	protected HandlerRegistration keyUpReg;

	public void setKeyReleased(JavaScriptObject aValue) {
		if (keyReleased != aValue) {
			if (keyUpReg != null) {
				keyUpReg.removeHandler();
				keyUpReg = null;
			}
			keyReleased = aValue;
			if (keyReleased != null && component instanceof HasKeyUpHandlers) {
				keyUpReg = ((HasKeyUpHandlers) component).addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (keyReleased != null) {
							event.stopPropagation();
							executeEvent(keyReleased, JsEvents.publish(event));
						}
					}

				});
			}
		}
	}

	protected void executeEvent(JavaScriptObject aHandler, JavaScriptObject aEvent) {
		try {
			Utils.executeScriptEventVoid(eventThis, aHandler, aEvent);
		} catch (Exception e) {
			Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
		}
	}

}

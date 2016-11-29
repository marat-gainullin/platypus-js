package com.eas.ui.events;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.ui.EventsPublisher;
import com.eas.window.events.HasMoveHandlers;
import com.eas.window.events.MoveEvent;
import com.eas.window.events.MoveHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.UIObject;
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
	private JavaScriptObject itemSelected;
	private JavaScriptObject valueChanged;

	private static enum MOUSE {
		NULL, PRESSED, MOVED, DRAGGED
	}

	private MOUSE mouseState = MOUSE.NULL;

	private UIObject component;
	private JavaScriptObject eventThis;

	public EventsExecutor(UIObject aComponent, JavaScriptObject aEventsThis) {
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

	public JavaScriptObject getValueChanged() {
		return valueChanged;
	}

	protected HandlerRegistration mouseOutReg;

	public void setMouseExited(JavaScriptObject aValue) {
		if (mouseExited != aValue) {
			if (mouseOutReg != null) {
				mouseOutReg.removeHandler();
				mouseOutReg = null;
			}
			mouseExited = aValue;
			if (mouseExited != null) {
				MouseOutHandler handler = new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						if (mouseExited != null) {
							event.stopPropagation();
							executeEvent(mouseExited, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget) {
					mouseOutReg = ((Widget) component).addDomHandler(handler, MouseOutEvent.getType());
				} else if (component instanceof HasMouseOutHandlers) {
					mouseOutReg = ((HasMouseOutHandlers) component).addMouseOutHandler(handler);
				}
			}
		}
	}

	protected HandlerRegistration actionPerformedReg;

	public void setActionPerformed(JavaScriptObject aValue) {
		if (actionPerformed != aValue) {
			if (actionPerformedReg != null) {
				actionPerformedReg.removeHandler();
				actionPerformedReg = null;
			}
			actionPerformed = aValue;
			if (actionPerformed != null && component instanceof HasActionHandlers) {
				actionPerformedReg = ((HasActionHandlers) component).addActionHandler(new ActionHandler() {

					@Override
					public void onAction(ActionEvent event) {
						executeEvent(actionPerformed, EventsPublisher.publish(event));
					}

				});
			}
		}
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
				ClickHandler cHandler = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (mouseClicked != null) {
							event.stopPropagation();
							executeEvent(mouseClicked, EventsPublisher.publish(event));
						}
					}
				};
				DoubleClickHandler dcHandler = new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						if (mouseClicked != null) {
							event.stopPropagation();
							executeEvent(mouseClicked, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget) {
					mouseClickedReg = ((Widget) component).addDomHandler(cHandler, ClickEvent.getType());
					mouseDblClickedReg = ((Widget) component).addDomHandler(dcHandler, DoubleClickEvent.getType());
				} else {
					if (component instanceof HasClickHandlers) {
						mouseClickedReg = ((HasClickHandlers) component).addClickHandler(cHandler);
					}
					if (component instanceof HasDoubleClickHandlers) {
						mouseDblClickedReg = ((HasDoubleClickHandlers) component).addDoubleClickHandler(dcHandler);
					}
				}
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
			if (mousePressed != null) {
				MouseDownHandler handler = new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						if (mousePressed != null) {
							event.stopPropagation();
							// Event.setCapture(event.getRelativeElement());
							mouseState = MOUSE.PRESSED;
							executeEvent(mousePressed, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget) {
					mouseDownReg = ((Widget) component).addDomHandler(handler, MouseDownEvent.getType());
				} else if (component instanceof HasMouseDownHandlers) {
					mouseDownReg = ((HasMouseDownHandlers) component).addMouseDownHandler(handler);
				}
			}
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
			if (mouseReleased != null) {
				MouseUpHandler handler = new MouseUpHandler() {
					@Override
					public void onMouseUp(MouseUpEvent event) {
						// if (mouseState == MOUSE.PRESSED)
						// Event.releaseCapture(event.getRelativeElement());
						if (mouseReleased != null) {
							event.stopPropagation();
							mouseState = MOUSE.NULL;
							executeEvent(mouseReleased, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget)
					mouseUpReg = ((Widget) component).addDomHandler(handler, MouseUpEvent.getType());
				else if (component instanceof HasMouseUpHandlers)
					mouseUpReg = ((HasMouseUpHandlers) component).addMouseUpHandler(handler);
			}
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
			if (mouseEntered != null) {
				MouseOverHandler handler = new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						if (mouseEntered != null) {
							event.stopPropagation();
							executeEvent(mouseEntered, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget) {
					mouseOverReg = ((Widget) component).addDomHandler(handler, MouseOverEvent.getType());
				} else if (component instanceof HasMouseOverHandlers) {
					mouseOverReg = ((HasMouseOverHandlers) component).addMouseOverHandler(handler);
				}
			}
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
			if (mouseWheelMoved != null) {
				MouseWheelHandler handler = new MouseWheelHandler() {
					@Override
					public void onMouseWheel(MouseWheelEvent event) {
						if (mouseWheelMoved != null) {
							event.stopPropagation();
							executeEvent(mouseWheelMoved, EventsPublisher.publish(event));
						}
					}
				};
				if (component instanceof Widget) {
					mouseWheelReg = ((Widget) component).addDomHandler(handler, MouseWheelEvent.getType());
				} else if (component instanceof HasMouseWheelHandlers) {
					mouseWheelReg = ((HasMouseWheelHandlers) component).addMouseWheelHandler(handler);
				}
			}
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
			if (mouseMoved != null) {
				MouseMoveHandler handler = new MouseMoveHandler() {
					@Override
					public void onMouseMove(MouseMoveEvent event) {
						if (mouseMoved != null || mouseDragged != null) {
							event.stopPropagation();
							if (mouseState == MOUSE.NULL || mouseState == MOUSE.MOVED) {
								mouseState = MOUSE.MOVED;
								executeEvent(mouseMoved, EventsPublisher.publish(event));
							} else if (mouseState == MOUSE.PRESSED || mouseState == MOUSE.DRAGGED) {
								mouseState = MOUSE.DRAGGED;
								executeEvent(mouseDragged, EventsPublisher.publish(event));
							}
						}
					}

				};
				if (component instanceof Widget) {
					mouseMoveReg = ((Widget) component).addDomHandler(handler, MouseMoveEvent.getType());
				} else if (component instanceof HasMouseMoveHandlers) {
					mouseMoveReg = ((HasMouseMoveHandlers) component).addMouseMoveHandler(handler);
				}
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
							executeEvent(componentResized, EventsPublisher.publish(event));
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
							executeEvent(componentMoved, EventsPublisher.publish(event));
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
							executeEvent(componentShown, EventsPublisher.publish(event));
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
							executeEvent(componentHidden, EventsPublisher.publish(event));
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
							executeEvent(componentAdded, EventsPublisher.publish(event));
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
							executeEvent(componentRemoved, EventsPublisher.publish(event));
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
							executeEvent(focusGained, EventsPublisher.publish(event));
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
							executeEvent(focusLost, EventsPublisher.publish(event));
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
							executeEvent(keyTyped, EventsPublisher.publish(event));
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
							executeEvent(keyPressed, EventsPublisher.publish(event));
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
							executeEvent(keyReleased, EventsPublisher.publish(event));
						}
					}

				});
			}
		}
	}

	public JavaScriptObject getItemSelected() {
		return itemSelected;
	}

	protected HandlerRegistration selectedItemReg;

	public void setItemSelected(JavaScriptObject aValue) {
		if (itemSelected != aValue) {
			if (selectedItemReg != null) {
				selectedItemReg.removeHandler();
				selectedItemReg = null;
			}
			itemSelected = aValue;
			if (component instanceof HasSelectionHandlers<?>) {
				selectedItemReg = ((HasSelectionHandlers<Object>) component).addSelectionHandler(new SelectionHandler<Object>() {

					@Override
					public void onSelection(SelectionEvent<Object> event) {
						JavaScriptObject published = ((HasPublished) event.getSource()).getPublished();
						Object oItem = event.getSelectedItem();
						if (oItem instanceof HasPublished)
							oItem = ((HasPublished) oItem).getPublished();
						executeEvent(itemSelected, EventsPublisher.publishItemEvent(published, oItem instanceof JavaScriptObject ? (JavaScriptObject) oItem : null));
					}

				});
			}
		}
	}

	protected HandlerRegistration valueChangedReg;

	public void setValueChanged(JavaScriptObject aValue) {
		if (valueChanged != aValue) {
			if (valueChangedReg != null) {
				valueChangedReg.removeHandler();
				valueChangedReg = null;
			}
			valueChanged = aValue;
			if (component instanceof HasValueChangeHandlers<?>) {
				valueChangedReg = ((HasValueChangeHandlers<Object>) component).addValueChangeHandler(new ValueChangeHandler<Object>() {

					@Override
					public void onValueChange(ValueChangeEvent<Object> event) {
						JavaScriptObject published = ((HasPublished) event.getSource()).getPublished();
						executeEvent(valueChanged, EventsPublisher.publishSourcedEvent(published));
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

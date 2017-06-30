package com.eas.ui;

import com.eas.ui.events.BlurEvent;
import com.eas.core.HasPublished;
import com.eas.core.Logger;
import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.Menu;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.BlurHandler;
import com.eas.ui.events.FocusHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.ui.events.HasBlurHandlers;
import com.eas.ui.events.HasFocusHandlers;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasKeyDownHandlers;
import com.eas.ui.events.HasKeyPressHandlers;
import com.eas.ui.events.HasKeyUpHandlers;
import com.eas.ui.events.HasSelectionHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HasValueChangeHandlers;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.KeyDownHandler;
import com.eas.ui.events.KeyPressHandler;
import com.eas.ui.events.KeyUpHandler;
import com.eas.ui.events.SelectionEvent;
import com.eas.ui.events.SelectionHandler;
import com.eas.ui.events.ComponentEvent;
import com.eas.ui.events.KeyDownEvent;
import com.eas.ui.events.KeyPressEvent;
import com.eas.ui.events.KeyUpEvent;
import com.eas.ui.events.MouseClickHandler;
import com.eas.ui.events.MouseDoubleClickHandler;
import com.eas.ui.events.MouseUpHandler;
import com.eas.ui.events.MouseDownHandler;
import com.eas.ui.events.MouseEnterHandler;
import com.eas.ui.events.MouseMoveHandler;
import com.eas.ui.events.MouseEvent;
import com.eas.ui.events.MouseExitHandler;
import com.eas.ui.events.MouseWheelHandler;
import com.eas.ui.events.ShowHandler;
import com.eas.ui.events.ValueChangeEvent;
import com.eas.ui.events.ValueChangeHandler;
import com.eas.widgets.containers.Container;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasEnabled;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mgainullin
 */
public abstract class Widget implements HasPublished, HasName, HasEnabled, HasComponentPopupMenu,
        HasShowHandlers, HasHideHandlers, HasActionHandlers {

    protected class EmptyHandlerRegistration implements HandlerRegistration {

        public EmptyHandlerRegistration() {
        }

        @Override
        public void removeHandler() {
        }

    }

    protected JavaScriptObject published;
    protected Element element;
    protected Container parent;
    private Style.Display visibleDisplay = Style.Display.INLINE_BLOCK;
    protected Menu menu;
    protected boolean enabled = true;
    protected String name;

    public Widget() {
        this(Document.get().createDivElement());
    }

    public Widget(Element element) {
        super();
        this.element = element;
        element.setPropertyObject("p-widget", this);
    }

    public String getTitle() {
        return element.getTitle();
    }

    public void setTitle(String aValue) {
        element.setTitle(aValue);
    }

    @Override
    public Menu getPlatypusPopupMenu() {
        return menu;
    }

    protected HandlerRegistration menuTriggerReg;

    @Override
    public void setPlatypusPopupMenu(Menu aMenu) {
        if (menu != aMenu) {
            if (menuTriggerReg != null) {
                menuTriggerReg.removeHandler();
            }
            menu = aMenu;
            if (menu != null) {
                menuTriggerReg = element.<XElement>cast().addEventListener(BrowserEvents.CONTEXTMENU, new XElement.NativeHandler() {

                    @Override
                    public void on(NativeEvent event) {
                        event.preventDefault();
                        event.stopPropagation();
                        menu.show(element.getAbsoluteLeft(), element.getAbsoluteTop(), element.getOffsetWidth(), element.getOffsetHeight());
                    }
                });
            }
        }
    }

    protected Set<ActionHandler> actionHandlers = new Set();

    @Override
    public HandlerRegistration addActionHandler(ActionHandler handler) {
        actionHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                actionHandlers.remove(handler);
            }

        };
    }

    protected void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this);
        for (ActionHandler h : actionHandlers) {
            h.onAction(event);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean aValue) {
        boolean oldValue = enabled;
        enabled = aValue;
        if (!oldValue && enabled) {
            getElement().<XElement>cast().unmask();
        } else if (oldValue && !enabled) {
            getElement().<XElement>cast().disabledMask();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String aValue) {
        name = aValue;
    }

    public Style.Display getVisibleDisplay() {
        return visibleDisplay;
    }

    public void setVisibleDisplay(Style.Display aValue) {
        this.visibleDisplay = aValue;
    }

    public Container getParent() {
        return parent;
    }

    public void setParent(Container aValue) {
        parent = aValue;
    }

    public Element getElement() {
        return element;
    }

    public boolean isVisible() {
        return !Style.Display.NONE.name().equals(element.getStyle().getDisplay());
    }

    public void setVisible(boolean aValue) {
        boolean oldValue = isVisible();
        if (oldValue != aValue) {
            if (aValue) {
                element.getStyle().setDisplay(visibleDisplay);
            } else {
                element.getStyle().setDisplay(Style.Display.NONE);
            }
            if (aValue) {
                ComponentEvent event = new ComponentEvent(this);
                for (ShowHandler h : showHandlers) {
                    h.onShow(event);
                }
            } else {
                ComponentEvent event = new ComponentEvent(this);
                for (HideHandler h : hideHandlers) {
                    h.onHide(event);
                }
            }
        }
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
                publish(aValue);
            }
        }
    }

    protected abstract void publish(JavaScriptObject aValue);

    private final Set<HideHandler> hideHandlers = new Set();

    @Override
    public HandlerRegistration addHideHandler(HideHandler handler) {
        hideHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                hideHandlers.remove(handler);
            }
        };
    }

    private final Set<ShowHandler> showHandlers = new Set();

    @Override
    public HandlerRegistration addShowHandler(ShowHandler handler) {
        showHandlers.add(handler);
        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                showHandlers.remove(handler);
            }
        };
    }

    public boolean isAttached() {
        Element cursor = element;
        while (cursor != null && element != Document.get().getBody()) {
            cursor = cursor.getParentElement();
        }
        return cursor != null;
    }

    private JavaScriptObject actionPerformed;
    private JavaScriptObject mouseExited;
    private JavaScriptObject mouseClicked;
    private JavaScriptObject mousePressed;
    private JavaScriptObject mouseReleased;
    private JavaScriptObject mouseEntered;
    private JavaScriptObject mouseWheelMoved;
    private JavaScriptObject mouseDragged;
    private JavaScriptObject mouseMoved;
    private JavaScriptObject componentShown;
    private JavaScriptObject componentHidden;
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

    public JavaScriptObject getComponentShown() {
        return componentShown;
    }

    public JavaScriptObject getComponentHidden() {
        return componentHidden;
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
                mouseOutReg = addMouseExitHandler(new MouseExitHandler() {
                    @Override
                    public void onMouseExit(MouseEvent evt) {
                        if (mouseExited != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseExited, EventsPublisher.publishMouseEvent(evt));
                        }
                    }

                });
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
            if (actionPerformed != null && this instanceof HasActionHandlers) {
                actionPerformedReg = ((HasActionHandlers) this).addActionHandler(new ActionHandler() {

                    @Override
                    public void onAction(ActionEvent event) {
                        executeEvent(actionPerformed, EventsPublisher.publishActionEvent(event));
                    }

                });
            }
        }
    }

    public HandlerRegistration addMouseClickHandler(MouseClickHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseClick(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseDoubleClickHandler(MouseDoubleClickHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.DBLCLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseDoubleClick(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseDown(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEUP, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseUp(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEMOVE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseMove(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseEnterHandler(MouseEnterHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEOVER, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseEnter(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseExitHandler(MouseExitHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEOUT, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseExit(new MouseEvent(Widget.this, evt));
            }
        });
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return element.<XElement>cast().addEventListener(BrowserEvents.MOUSEWHEEL, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent evt) {
                handler.onMouseWheel(new MouseEvent(Widget.this, evt));
            }
        });
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
                mouseClickedReg = addMouseClickHandler(new MouseClickHandler() {
                    @Override
                    public void onMouseClick(MouseEvent evt) {
                        if (mouseClicked != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseClicked, EventsPublisher.publishMouseEvent(evt));
                        }
                    }

                });
                mouseDblClickedReg = addMouseDoubleClickHandler(new MouseDoubleClickHandler() {
                    @Override
                    public void onMouseDoubleClick(MouseEvent evt) {
                        if (mouseClicked != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseClicked, EventsPublisher.publishMouseEvent(evt, 2));
                        }
                    }

                });
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
                mouseDownReg = addMouseDownHandler(new MouseDownHandler() {
                    @Override
                    public void onMouseDown(MouseEvent evt) {
                        if (mousePressed != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mousePressed, EventsPublisher.publishMouseEvent(evt));
                        }
                    }
                });
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
                mouseUpReg = addMouseUpHandler(new MouseUpHandler() {
                    @Override
                    public void onMouseUp(MouseEvent evt) {
                        if (mouseReleased != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseReleased, EventsPublisher.publishMouseEvent(evt));
                        }
                    }
                });
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
                mouseMoveReg = addMouseMoveHandler(new MouseMoveHandler() {
                    @Override
                    public void onMouseMove(MouseEvent evt) {
                        if (mouseMoved != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseDragged, EventsPublisher.publishMouseEvent(evt));
                        }
                    }

                });
            }
        }
    }

    protected HandlerRegistration mouseDownForDragReg;
    protected HandlerRegistration mouseUpForDragReg;
    protected HandlerRegistration mouseMoveForDragReg;

    public void setMouseDragged(JavaScriptObject aValue) {
        if (mouseDragged != aValue) {
            if (mouseDownForDragReg != null) {
                mouseDownForDragReg.removeHandler();
                mouseDownForDragReg = null;
            }
            if (mouseUpForDragReg != null) {
                mouseUpForDragReg.removeHandler();
                mouseUpForDragReg = null;
            }
            if (mouseMoveForDragReg != null) {
                mouseMoveForDragReg.removeHandler();
                mouseMoveForDragReg = null;
            }
            mouseDragged = aValue;
            if (mouseDragged != null) {
                mouseDownForDragReg = addMouseDownHandler(new MouseDownHandler() {
                    @Override
                    public void onMouseDown(MouseEvent evt) {
                        DOM.setCapture(element);
                        mouseState = MOUSE.PRESSED;
                        executeEvent(mousePressed, EventsPublisher.publishMouseEvent(evt));
                    }
                });
                mouseUpForDragReg = addMouseUpHandler(new MouseUpHandler() {
                    @Override
                    public void onMouseUp(MouseEvent evt) {
                        if (element == DOM.getCaptureElement()) {
                            DOM.releaseCapture(element);
                        }
                        evt.getEvent().stopPropagation();
                        mouseState = MOUSE.NULL;
                    }
                });
                mouseMoveForDragReg = addMouseMoveHandler(new MouseMoveHandler() {
                    @Override
                    public void onMouseMove(MouseEvent evt) {
                        if (mouseDragged != null) {
                            evt.getEvent().stopPropagation();
                            if (mouseState == MOUSE.PRESSED || mouseState == MOUSE.DRAGGED) {
                                mouseState = MOUSE.DRAGGED;
                                executeEvent(mouseDragged, EventsPublisher.publishMouseEvent(evt));
                            }
                        }
                    }

                });
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
                mouseOverReg = addMouseEnterHandler(new MouseEnterHandler() {
                    @Override
                    public void onMouseEnter(MouseEvent evt) {
                        if (mouseEntered != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseEntered, EventsPublisher.publishMouseEvent(evt));
                        }
                    }

                });
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
                mouseWheelReg = addMouseWheelHandler(new MouseWheelHandler() {
                    @Override
                    public void onMouseWheel(MouseEvent evt) {
                        if (mouseWheelMoved != null) {
                            evt.getEvent().stopPropagation();
                            executeEvent(mouseWheelMoved, EventsPublisher.publishMouseEvent(evt));
                        }
                    }

                });
            }
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
            if (componentShown != null) {
                componentShownReg = addShowHandler(new ShowHandler() {
                    @Override
                    public void onShow(ComponentEvent event) {
                        if (componentShown != null) {
                            executeEvent(componentShown, EventsPublisher.publishComponentEvent(event));
                        }
                    }
                });
            }
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
            if (componentHidden != null) {
                componentHiddenReg = addHideHandler(new HideHandler() {
                    @Override
                    public void onHide(ComponentEvent event) {
                        if (componentHidden != null) {
                            executeEvent(componentHidden, EventsPublisher.publishComponentEvent(event));
                        }
                    }
                });
            }
        }
    }

    protected HandlerRegistration focusReg;

    public void setFocusGained(JavaScriptObject aValue) {
        if (focusGained != aValue) {
            if (focusReg != null) {
                focusReg.removeHandler();
                focusReg = null;
            }
            focusGained = aValue;
            if (focusGained != null && this instanceof HasFocusHandlers) {
                focusReg = ((HasFocusHandlers) this).addFocusHandler(new FocusHandler() {
                    @Override
                    public void onFocus(FocusEvent event) {
                        if (focusGained != null) {
                            executeEvent(focusGained, EventsPublisher.publishFocusEvent(event));
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
            if (focusLost != null && this instanceof HasBlurHandlers) {
                blurReg = ((HasBlurHandlers) this).addBlurHandler(new BlurHandler() {
                    @Override
                    public void onBlur(BlurEvent event) {
                        if (focusLost != null) {
                            executeEvent(focusLost, EventsPublisher.publishBlurEvent(event));
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
            if (keyTyped != null && this instanceof HasKeyPressHandlers) {
                keyTypedReg = ((HasKeyPressHandlers) this).addKeyPressHandler(new KeyPressHandler() {
                    @Override
                    public void onKeyPress(KeyPressEvent event) {
                        if (keyTyped != null) {
                            event.getEvent().stopPropagation();
                            executeEvent(keyTyped, EventsPublisher.publishKeyEvent(event));
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
            if (keyPressed != null && this instanceof HasKeyDownHandlers) {
                keyDownReg = ((HasKeyDownHandlers) this).addKeyDownHandler(new KeyDownHandler() {
                    @Override
                    public void onKeyDown(KeyDownEvent event) {
                        if (keyPressed != null) {
                            event.getEvent().stopPropagation();
                            executeEvent(keyPressed, EventsPublisher.publishKeyEvent(event));
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
            if (keyReleased != null && this instanceof HasKeyUpHandlers) {
                keyUpReg = ((HasKeyUpHandlers) this).addKeyUpHandler(new KeyUpHandler() {
                    @Override
                    public void onKeyUp(KeyUpEvent event) {
                        if (keyReleased != null) {
                            event.getEvent().stopPropagation();
                            executeEvent(keyReleased, EventsPublisher.publishKeyEvent(event));
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
            if (this instanceof HasSelectionHandlers<?>) {
                selectedItemReg = ((HasSelectionHandlers<Object>) this).addSelectionHandler(new SelectionHandler<Object>() {

                    @Override
                    public void onSelection(SelectionEvent<Object> event) {
                        JavaScriptObject published = ((HasPublished) event.getSource()).getPublished();
                        Object oItem = event.getSelectedItem();
                        if (oItem instanceof HasPublished) {
                            oItem = ((HasPublished) oItem).getPublished();
                        }
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
            if (this instanceof HasValueChangeHandlers) {
                valueChangedReg = ((HasValueChangeHandlers) this).addValueChangeHandler(new ValueChangeHandler() {

                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        JavaScriptObject published = ((HasPublished) event.getSource()).getPublished();
                        executeEvent(valueChanged, EventsPublisher.publishSourcedEvent(published));
                    }
                });

            }
        }
    }

    protected void executeEvent(JavaScriptObject aHandler, JavaScriptObject aEvent) {
        try {
            Utils.executeScriptEventVoid(published, aHandler, aEvent);
        } catch (Exception e) {
            Logger.severe(e);
        }
    }
}

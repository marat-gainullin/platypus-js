/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasBlurHandlers;
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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <T>
 */
public abstract class DecoratorBox<T> extends Composite implements RequiresResize, HasValue<T>, HasValueChangeHandlers<T>, Focusable, HasEnabled, HasAllKeyHandlers, HasFocusHandlers, HasBlurHandlers {

	private static final String DECORATOR_FOCUSED_CLASS_NAME = "decorator-focused";
	protected FlowPanel container = new FlowPanel();
	protected HasValue<T> decorated;
	protected boolean enabled = true;
	protected boolean nullable = true;
	protected HandlerRegistration changeValueHandler;
	protected HandlerRegistration keyDownHandler;
	protected HandlerRegistration keyUpHandler;
	protected HandlerRegistration keyPressHandler;
	protected HandlerRegistration focusHandler;
	protected HandlerRegistration blurHandler;
	protected SimplePanel contentWrapper = new SimplePanel();
	protected SimplePanel selectButton = new SimplePanel();
	protected SimplePanel clearButton = new SimplePanel();

	public DecoratorBox(HasValue<T> aDecorated) {
		this();
		setWidget(aDecorated);
	}

	public DecoratorBox() {
		super();
		initWidget(container);
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		container.getElement().addClassName("decorator");
		contentWrapper.getElement().addClassName("decorator-content");
		contentWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		contentWrapper.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		contentWrapper.getElement().getStyle().setLeft(0, Style.Unit.PX);
		contentWrapper.getElement().getStyle().setTop(0, Style.Unit.PX);
		contentWrapper.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		contentWrapper.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());

		selectButton.getElement().addClassName("decorator-select");
		selectButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		selectButton.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		selectButton.getElement().getStyle().setTop(0, Style.Unit.PX);
		selectButton.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		clearButton.getElement().addClassName("decorator-clear");
		clearButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		clearButton.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		clearButton.getElement().getStyle().setTop(0, Style.Unit.PX);
		clearButton.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		container.add(contentWrapper);
		container.add(selectButton);
		container.add(clearButton);
		selectButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectValue();
			}
		}, ClickEvent.getType());
		clearButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clearValue();
				setFocus(true);
			}
		}, ClickEvent.getType());
		organizeButtonsContent();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public Widget getWidget() {
		return decorated instanceof Widget ? (Widget) decorated : null;
	}

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return super.addHandler(handler, KeyDownEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return super.addHandler(handler, KeyPressEvent.getType());
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return super.addHandler(handler, KeyUpEvent.getType());
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
			getElement().<XElement> cast().unmask();
		} else if (oldValue && !enabled) {
			getElement().<XElement> cast().disabledMask();
		}
	}
	
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean aValue) {
		if(nullable != aValue){
			nullable = aValue;
			setClearButtonVisible(nullable);
		}
    }
	
	@Override
	public void setFocus(boolean focused) {
		if (decorated instanceof Focusable) {
			((Focusable) decorated).setFocus(focused);
		}
	}

	@Override
	public void setAccessKey(char key) {
		if (decorated instanceof Focusable) {
			((Focusable) decorated).setAccessKey(key);
		}
	}

	@Override
	public int getTabIndex() {
		if (decorated instanceof Focusable) {
			return ((Focusable) decorated).getTabIndex();
		} else
			return -1;
	}

	@Override
	public void setTabIndex(int index) {
		if (decorated instanceof Focusable) {
			((Focusable) decorated).setTabIndex(index);
		}
	}

	protected abstract void selectValue();

	protected abstract void clearValue();

	public void setWidget(HasValue<T> w) {
		if (decorated != w) {
			if (changeValueHandler != null) {
				changeValueHandler.removeHandler();
			}
			if (keyDownHandler != null)
				keyDownHandler.removeHandler();
			if (keyUpHandler != null)
				keyUpHandler.removeHandler();
			if (keyPressHandler != null)
				keyPressHandler.removeHandler();
			if (focusHandler != null)
				focusHandler.removeHandler();
			if (blurHandler != null)
				blurHandler.removeHandler();
			if (decorated instanceof Widget) {
				((Widget) decorated).removeFromParent();
			}
			decorated = w;
			if (decorated != null) {
				changeValueHandler = decorated.addValueChangeHandler(new ValueChangeHandler<T>() {

					@Override
					public void onValueChange(ValueChangeEvent<T> event) {
						fireValueChangeEvent();
					}
				});
				if (decorated instanceof Widget) {
					CommonResources.INSTANCE.commons().ensureInjected();
					((Widget) decorated).getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
					Style style = ((Widget) decorated).getElement().getStyle();
					style.setBorderWidth(0, Style.Unit.PX);
					style.setPadding(0, Style.Unit.PX);
					style.setMargin(0, Style.Unit.PX);
					style.setPosition(Style.Position.ABSOLUTE);
					style.setDisplay(Style.Display.INLINE_BLOCK);
					style.setLeft(0, Style.Unit.PX);
					style.setTop(0, Style.Unit.PX);
					style.setHeight(100, Style.Unit.PCT);
					style.setWidth(100, Style.Unit.PCT);
					style.setOutlineStyle(Style.OutlineStyle.NONE);
					style.setBackgroundColor("inherit");
					style.setColor("inherit");
					contentWrapper.setWidget((Widget) decorated);
				}
				if (decorated instanceof HasKeyDownHandlers) {
					keyDownHandler = ((HasKeyDownHandlers) decorated).addKeyDownHandler(new KeyDownHandler() {

						@Override
						public void onKeyDown(KeyDownEvent event) {
							KeyDownEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
						}
					});
				}
				if (decorated instanceof HasKeyUpHandlers) {
					keyUpHandler = ((HasKeyUpHandlers) decorated).addKeyUpHandler(new KeyUpHandler() {

						@Override
						public void onKeyUp(KeyUpEvent event) {
							KeyUpEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
						}
					});
				}
				if (decorated instanceof HasKeyPressHandlers) {
					keyPressHandler = ((HasKeyPressHandlers) decorated).addKeyPressHandler(new KeyPressHandler() {

						@Override
						public void onKeyPress(KeyPressEvent event) {
							KeyPressEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
						}
					});
				}
				if (decorated instanceof HasFocusHandlers) {
					focusHandler = ((HasFocusHandlers) decorated).addFocusHandler(new FocusHandler() {

						@Override
						public void onFocus(FocusEvent event) {
							DecoratorBox.this.getElement().addClassName(DECORATOR_FOCUSED_CLASS_NAME);
							FocusEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
						}

					});
				}
				if (decorated instanceof HasBlurHandlers) {
					blurHandler = ((HasBlurHandlers) decorated).addBlurHandler(new BlurHandler() {

						@Override
						public void onBlur(BlurEvent event) {
							DecoratorBox.this.getElement().removeClassName(DECORATOR_FOCUSED_CLASS_NAME);
							BlurEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
						}

					});
				}
			}
		}
	}

	protected void fireValueChangeEvent() {
		ValueChangeEvent.fire(DecoratorBox.this, getValue());
	}

	protected void organizeButtonsContent() {
		int right = 0;
		if (isClearButtonVisible()) {
			clearButton.getElement().getStyle().setRight(right, Style.Unit.PX);
			right += clearButton.getElement().getOffsetWidth();
		}
		if (isSelectButtonVisible()) {
			selectButton.getElement().getStyle().setRight(right, Style.Unit.PX);
			right += selectButton.getElement().getOffsetWidth();
		}
		contentWrapper.getElement().getStyle().setRight(right, Style.Unit.PX);
	}

	public boolean isSelectButtonVisible() {
		return !Style.Display.NONE.getCssName().equalsIgnoreCase(selectButton.getElement().getStyle().getDisplay());
	}

	public void setSelectButtonVisible(boolean aValue) {
		if (isSelectButtonVisible() != aValue) {
			if (aValue) {
				selectButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			} else {
				selectButton.getElement().getStyle().setDisplay(Style.Display.NONE);
			}
			organizeButtonsContent();
		}
	}

	public boolean isClearButtonVisible() {
		return !Style.Display.NONE.getCssName().equalsIgnoreCase(clearButton.getElement().getStyle().getDisplay());
	}

	public void setClearButtonVisible(boolean aValue) {
		if (isClearButtonVisible() != aValue) {
			if (aValue) {
				clearButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			} else {
				clearButton.getElement().getStyle().setDisplay(Style.Display.NONE);
			}
			organizeButtonsContent();
		}
	}

	@Override
	public T getValue() {
		return decorated.getValue();
	}

	@Override
	public void setValue(T value) {
		setValue(value, false);
	}

	@Override
	public void setValue(T value, boolean fireEvents) {
		decorated.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	@Override
	public void onResize() {
		if (decorated instanceof RequiresResize) {
			((RequiresResize) decorated).onResize();
		}
		organizeButtonsContent();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organizeButtonsContent();
	}
}

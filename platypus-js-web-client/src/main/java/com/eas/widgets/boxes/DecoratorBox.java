package com.eas.widgets.boxes;

import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.HasDecorations;
import com.eas.ui.HasDecorationsWidth;
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
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <T>
 */
public abstract class DecoratorBox<T> extends Composite implements RequiresResize, HasValue<T>, HasValueChangeHandlers<T>, Focusable, HasEnabled, HasAllKeyHandlers, HasFocusHandlers, HasBlurHandlers {

	protected HasValue<T> decorated;
	protected boolean enabled = true;
	protected boolean nullable = true;
	protected HandlerRegistration changeValueHandler;
	protected HandlerRegistration keyDownHandler;
	protected HandlerRegistration keyUpHandler;
	protected HandlerRegistration keyPressHandler;
	protected HandlerRegistration focusHandler;
	protected HandlerRegistration blurHandler;
	protected SimplePanel selectButton = new SimplePanel();
	protected SimplePanel clearButton = new SimplePanel();
	protected boolean selectButtonNeeded;

	public DecoratorBox(HasValue<T> aDecorated) {
		super();
		decorated = aDecorated;
		if (decorated instanceof HasValue<?>) {
			decorated.addValueChangeHandler(new ValueChangeHandler<T>() {

				@Override
				public void onValueChange(ValueChangeEvent<T> event) {
					setClearButtonVisible(nullable && event.getValue() != null);
				}
			});
		}
		if (decorated instanceof HasDecorations) {
			HasWidgets container = ((HasDecorations) decorated).getContainer();
			((Widget) container).addStyleName("decorator");
			container.add(selectButton);
			container.add(clearButton);
			initWidget((Widget) decorated);
		} else {
			CommonResources.INSTANCE.commons().ensureInjected();
			((Widget) decorated).getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
			Style style = ((Widget) decorated).getElement().getStyle();
			style.setMargin(0, Style.Unit.PX);
			style.setPosition(Style.Position.ABSOLUTE);
			style.setDisplay(Style.Display.INLINE_BLOCK);
			style.setLeft(0, Style.Unit.PX);
			style.setTop(0, Style.Unit.PX);
			style.setHeight(100, Style.Unit.PCT);
			style.setWidth(100, Style.Unit.PCT);
			style.setOutlineStyle(Style.OutlineStyle.NONE);
			FlowPanel panel = new FlowPanel();
                        panel.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			panel.addStyleName("decorator");
			initWidget(panel);
			panel.add(selectButton);
			panel.add(clearButton);
			panel.add((Widget) decorated);
		}

		((Widget) decorated).addStyleName("decorator-content");

		selectButton.getElement().addClassName("decorator-select");
		selectButton.getElement().getStyle().setDisplay(Style.Display.NONE);
		selectButton.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		selectButton.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		selectButton.getElement().getStyle().setZIndex(1); // FireFox hides this in ModelCombo without such setting
		clearButton.getElement().addClassName("decorator-clear");
		clearButton.getElement().getStyle().setDisplay(Style.Display.NONE);
		clearButton.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		clearButton.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		clearButton.getElement().getStyle().setZIndex(1); // FireFox hides this in ModelCombo without such setting

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

		if (decorated instanceof HasValue<?>) {
			changeValueHandler = decorated.addValueChangeHandler(new ValueChangeHandler<T>() {

				@Override
				public void onValueChange(ValueChangeEvent<T> event) {
					fireValueChangeEvent();
				}
			});
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
					FocusEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
				}

			});
		}
		if (decorated instanceof HasBlurHandlers) {
			blurHandler = ((HasBlurHandlers) decorated).addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					BlurEvent.fireNativeEvent(event.getNativeEvent(), DecoratorBox.this);
				}

			});
		}

	}

	public Widget getWidget() {
		return (Widget) decorated;
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
		if (nullable != aValue) {
			nullable = aValue;
			setClearButtonVisible(nullable && getValue() != null);
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

	protected void fireValueChangeEvent() {
		ValueChangeEvent.fire(DecoratorBox.this, getValue());
	}

	protected int organizeButtonsContent() {
		int right = 0;
		if (decorated != null) {
			if (isSelectButtonVisible()) {
				right += selectButton.getElement().getOffsetWidth();
			}
			if (isClearButtonVisible()) {
				right += clearButton.getElement().getOffsetWidth();
			}
			if (decorated instanceof HasDecorationsWidth) {
				((HasDecorationsWidth) decorated).setDecorationsWidth(right);
			}
		}
		return right;
	}

	protected boolean isSelectButtonVisible() {
		return !Style.Display.NONE.getCssName().equalsIgnoreCase(selectButton.getElement().getStyle().getDisplay());
	}

	protected void setSelectButtonVisible(boolean aValue) {
		if (isSelectButtonVisible() != aValue) {
			if (aValue) {
				selectButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			} else {
				selectButton.getElement().getStyle().setDisplay(Style.Display.NONE);
			}
			organizeButtonsContent();
		}
	}

	protected boolean isClearButtonVisible() {
		return !Style.Display.NONE.getCssName().equalsIgnoreCase(clearButton.getElement().getStyle().getDisplay());
	}

	protected void setClearButtonVisible(boolean aValue) {
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
		if (!fireEvents) {
			setClearButtonVisible(nullable && decorated.getValue() != null);
		}
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

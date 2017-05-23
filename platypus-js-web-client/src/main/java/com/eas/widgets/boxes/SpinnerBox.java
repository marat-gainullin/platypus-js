package com.eas.widgets.boxes;

import com.eas.core.XElement;
import com.eas.ui.CommonResources;
import com.eas.ui.HasDecorations;
import com.eas.ui.HasDecorationsWidth;
import com.google.gwt.dom.client.Style;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
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
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ValueBox;

/**
 * 
 * @author mg
 * @param <T>
 */
public abstract class SpinnerBox<T> extends Composite implements RequiresResize, HasValue<T>, HasText, HasValueChangeHandlers<T>, IsEditor<LeafValueEditor<T>>, Focusable, HasAllKeyHandlers,
        HasFocusHandlers, HasBlurHandlers, HasDecorations, HasDecorationsWidth, HasName {

	protected FlowPanel container = new FlowPanel();
	protected SimplePanel left = new SimplePanel();
	protected ValueBox<T> field;
	protected SimplePanel right = new SimplePanel();
	protected int decorationsWidth;

	public SpinnerBox(ValueBox<T> aField) {
		initWidget(container);
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		container.getElement().addClassName("spin-field");
		field = aField;
		field.setStyleName("form-control");
		field.addValueChangeHandler(new ValueChangeHandler<T>() {

			@Override
			public void onValueChange(ValueChangeEvent<T> event) {
				ValueChangeEvent.fire(SpinnerBox.this, getValue());
			}
		});
		if (field instanceof HasKeyDownHandlers) {
			((HasKeyDownHandlers) field).addKeyDownHandler(new KeyDownHandler() {

				@Override
				public void onKeyDown(KeyDownEvent event) {
					KeyDownEvent.fireNativeEvent(event.getNativeEvent(), SpinnerBox.this);
				}
			});
		}
		if (field instanceof HasKeyUpHandlers) {
			((HasKeyUpHandlers) field).addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					KeyUpEvent.fireNativeEvent(event.getNativeEvent(), SpinnerBox.this);
				}
			});
		}
		if (field instanceof HasKeyPressHandlers) {
			((HasKeyPressHandlers) field).addKeyPressHandler(new KeyPressHandler() {

				@Override
				public void onKeyPress(KeyPressEvent event) {
					KeyPressEvent.fireNativeEvent(event.getNativeEvent(), SpinnerBox.this);
				}
			});
		}
		if (field instanceof HasFocusHandlers) {
			((HasFocusHandlers) field).addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					FocusEvent.fireNativeEvent(event.getNativeEvent(), SpinnerBox.this);
				}

			});
		}
		if (field instanceof HasBlurHandlers) {
			((HasBlurHandlers) field).addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					BlurEvent.fireNativeEvent(event.getNativeEvent(), SpinnerBox.this);
				}

			});
		}
		left.getElement().addClassName("spin-left");
		left.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		left.getElement().getStyle().setTop(0, Style.Unit.PX);
		left.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		left.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                // FireFox hides this without such setting because of place in DOM.
                // Place in DOM is after input element because of FireFox's float elements behaviour.
		left.getElement().getStyle().setZIndex(1);

		CommonResources.INSTANCE.commons().ensureInjected();

		field.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		field.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		field.getElement().getStyle().setTop(0, Style.Unit.PX);
		field.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		field.getElement().getStyle().setLeft(0, Style.Unit.PX);
		field.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		field.getElement().getStyle().setMargin(0, Style.Unit.PX);
		field.getElement().getStyle().setBackgroundColor("inherit");
		field.getElement().getStyle().setColor("inherit");
		field.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());

		right.getElement().addClassName("spin-right");
		right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		right.getElement().getStyle().setTop(0, Style.Unit.PX);
		right.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		right.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                // FireFox hides this without such setting because of place in DOM.
                // Place in DOM is after input element because of FireFox's float elements behaviour.
		right.getElement().getStyle().setZIndex(1);

		CommonResources.INSTANCE.commons().ensureInjected();
		left.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());
		right.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());

		container.add(right);
		container.add(left);
		container.add(field);
		left.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isReadonly()) {
					decrement();
				}
			}
		}, ClickEvent.getType());
		right.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isReadonly()) {
					increment();
				}
			}
		}, ClickEvent.getType());
		redecorate();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
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
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	@Override
	public void setDecorationsWidth(int aDecorationsWidth) {
		decorationsWidth = aDecorationsWidth;
		redecorate();
	}
	
	@Override
	public HasWidgets getContainer() {
	    return container;
	}
	
	protected void redecorate() {
		if (isAttached()) {
			field.getElement().getStyle().setPaddingLeft(left.getElement().getOffsetWidth(), Style.Unit.PX);
			int paddingRight = right.getElement().getOffsetWidth() + decorationsWidth;
			field.getElement().getStyle().setPaddingRight(paddingRight, Style.Unit.PX);
		}
	}

	protected abstract void increment();

	protected abstract void decrement();

	@Override
	public String getText() {
		return field.getText();
	}

	@Override
	public void setText(String text) {
		field.setText(text);
	}

	@Override
	public void setFocus(boolean focused) {
		field.setFocus(focused);
	}

	@Override
	public void setAccessKey(char key) {
		field.setAccessKey(key);
	}

	@Override
	public int getTabIndex() {
		return field.getTabIndex();
	}

	@Override
	public void setTabIndex(int index) {
		field.setTabIndex(index);
	}

	@Override
	public T getValue() {
		return field.getValue();
	}

	@Override
	public void setValue(T value) {
		field.setValue(value);
	}

	@Override
	public void setValue(T value, boolean fireEvents) {
		field.setValue(value, fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

        @Override
        public String getName(){
            return field.getName();
        }

        @Override
        public void setName(String name) {
            field.setName(name);
        }

        
	@Override
	public LeafValueEditor<T> asEditor() {
		return field.asEditor();
	}

	@Override
	public void onResize() {
		redecorate();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		redecorate();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}

	public void setReadonly(boolean aValue) {
		field.getElement().setPropertyBoolean("readOnly", aValue);
	}

	public boolean isReadonly() {
		return field.getElement().getPropertyBoolean("readOnly");
	}
}
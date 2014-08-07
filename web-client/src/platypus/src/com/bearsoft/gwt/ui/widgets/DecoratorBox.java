/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public abstract class DecoratorBox<T> extends Composite implements RequiresResize, HasValue<T>, HasValueChangeHandlers<T>, Focusable, HasEnabled {

	protected FlowPanel container = new FlowPanel();
	protected HasValue<T> decorated;
	protected boolean enabled = true;
	protected HandlerRegistration changeValueHandler;
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
			}
		}, ClickEvent.getType());
		organizeButtonsContent();
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}

	public Widget getWidget(){
		return decorated instanceof Widget ? (Widget)decorated : null;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		boolean oldValue = enabled;
		enabled = aValue;
		if(!oldValue && enabled){
			getElement().<XElement>cast().unmask();
		}else if(oldValue && !enabled){
			getElement().<XElement>cast().disabledMask();
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
					contentWrapper.setWidget((Widget) decorated);
				}
			}
		}
	}

	protected void fireValueChangeEvent() {
		ValueChangeEvent.fire(DecoratorBox.this, getValue());
	}

	protected void organizeButtonsContent(){
		int right = 0;
		if(isClearButtonVisible()){
			clearButton.getElement().getStyle().setRight(right, Style.Unit.PX);
			right += clearButton.getElement().getOffsetWidth();
		}
		if(isSelectButtonVisible()){
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
	public void onResize() {
		if (decorated instanceof RequiresResize) {
			((RequiresResize) decorated).onResize();
		}
	}
	
	@Override
	protected void onAttach() {
	    super.onAttach();
		organizeButtonsContent();
	}
}

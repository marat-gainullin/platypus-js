/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets;

import com.bearsoft.gwt.ui.CommonResources;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ValueBox;

/**
 * 
 * @author mg
 * @param <T>
 */
public abstract class SpinnerBox<T> extends Composite implements RequiresResize, HasValue<T>, HasValueChangeHandlers<T>, IsEditor<LeafValueEditor<T>> {

	protected FlowPanel container = new FlowPanel();
	protected SimplePanel left = new SimplePanel();
	protected SimplePanel fieldWrapper = new SimplePanel();
	protected ValueBox<T> field;
	protected SimplePanel right = new SimplePanel();

	public SpinnerBox(ValueBox<T> aField) {
		initWidget(container);
		container.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getElement().addClassName("spin-field");
		field = aField;
		field.addValueChangeHandler(new ValueChangeHandler<T>() {

			@Override
			public void onValueChange(ValueChangeEvent<T> event) {
				ValueChangeEvent.fire(SpinnerBox.this, getValue());
			}
		});
		left.getElement().addClassName("spin-left");
		left.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		left.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		left.getElement().getStyle().setTop(0, Style.Unit.PX);
		left.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		left.getElement().getStyle().setLeft(0, Style.Unit.PX);
		left.getElement().setInnerHTML("&nbsp;");

		fieldWrapper.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		fieldWrapper.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		fieldWrapper.getElement().getStyle().setTop(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		fieldWrapper.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setMargin(0, Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setPadding(0, Style.Unit.PX);

		field.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		field.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		field.getElement().getStyle().setTop(0, Style.Unit.PX);
		field.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		field.getElement().getStyle().setLeft(0, Style.Unit.PX);
		field.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		field.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
		field.getElement().getStyle().setMargin(0, Style.Unit.PX);
		field.getElement().getStyle().setPadding(0, Style.Unit.PX);
		fieldWrapper.setWidget(field);

		right.getElement().addClassName("spin-right");
		right.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		right.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		right.getElement().getStyle().setTop(0, Style.Unit.PX);
		right.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		right.getElement().getStyle().setRight(0, Style.Unit.PX);
		right.getElement().setInnerHTML("&nbsp;");

		CommonResources.INSTANCE.commons().ensureInjected();
		left.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());
		right.getElement().addClassName(CommonResources.INSTANCE.commons().unselectable());

		container.add(left);
		container.add(fieldWrapper);
		container.add(right);
		left.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				decrement();
			}
		}, ClickEvent.getType());
		right.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				increment();
			}
		}, ClickEvent.getType());
		organaizeFieldWrapperLeftRight();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	protected void organaizeFieldWrapperLeftRight() {
		fieldWrapper.getElement().getStyle().setLeft(left.getElement().getOffsetWidth(), Style.Unit.PX);
		fieldWrapper.getElement().getStyle().setRight(right.getElement().getOffsetWidth(), Style.Unit.PX);
	}

	protected abstract void increment();

	protected abstract void decrement();

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
	public LeafValueEditor<T> asEditor() {
		return field.asEditor();
	}

	@Override
	public void onResize() {
		organaizeFieldWrapperLeftRight();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organaizeFieldWrapperLeftRight();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}
}
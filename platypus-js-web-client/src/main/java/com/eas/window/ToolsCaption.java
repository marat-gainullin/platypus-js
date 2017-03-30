/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window;

import com.eas.window.events.MaximizeEvent;
import com.eas.window.events.MaximizeHandler;
import com.eas.window.events.MinimizeEvent;
import com.eas.window.events.MinimizeHandler;
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class ToolsCaption extends FlowPanel implements HasHTML {

	public static final String WINDOW_TOOL_CLASS_NAME = "window-tool";

	protected HTML label = new HTML();
	protected ImageResource icon;
	protected Panel tools = new FlowPanel();
	protected SimplePanel btnMinimize = new SimplePanel();
	protected SimplePanel btnMaximize = new SimplePanel();
	protected SimplePanel btnRestore = new SimplePanel();
	protected SimplePanel btnClose = new SimplePanel();
	protected WindowUI window;

	protected HandlerRegistration windowMinimize;
	protected HandlerRegistration windowMaximize;
	protected HandlerRegistration windowRestore;

	public ToolsCaption(WindowUI aWindow) {
		super();
		setStyleName("window-caption");
		btnMinimize.getElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-minimize");
		btnMaximize.getElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-maximize");
		btnRestore.getElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-restore");
		btnClose.getElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-close");
		for (Widget w : new Widget[] { btnMinimize, btnMaximize, btnRestore, btnClose }) {
			w.getElement().getStyle().setMargin(0, Style.Unit.PX);
			w.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			w.getElement().addClassName(WINDOW_TOOL_CLASS_NAME);
		}

		label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		label.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		label.setStyleName("window-caption-text");// to implicitly remove
		                                          // gwt-HTML class

		tools.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		tools.getElement().getStyle().setFloat(Style.Float.RIGHT);
		tools.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		tools.setStyleName("window-caption-tools");

		getElement().getStyle().setPosition(Style.Position.RELATIVE);

		add(tools);
		add(label);
		tools.add(btnMinimize);
		tools.add(btnMaximize);
		tools.add(btnRestore);
		tools.add(btnClose);
		btnMinimize.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				window.minimize();
			}
		}, ClickEvent.getType());
		btnMaximize.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				window.maximize();
			}
		}, ClickEvent.getType());
		btnRestore.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				window.restore();
			}
		}, ClickEvent.getType());
		btnClose.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				window.close();
			}
		}, ClickEvent.getType());

		addDomHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (window.isMaximized()) {
					window.restore();
				} else {
					window.maximize();
				}
			}

		}, DoubleClickEvent.getType());
		setWindow(aWindow);
	}

	public ToolsCaption() {
		this(null);
	}

	public ToolsCaption(WindowUI aWindow, String aCaptionHtml) {
		this(aWindow);
		label.setHTML(aCaptionHtml);
	}

	private void setWindow(WindowUI aWindow) {
		if (windowMinimize != null) {
			windowMinimize.removeHandler();
		}
		if (windowMaximize != null) {
			windowMaximize.removeHandler();
		}
		if (windowRestore != null) {
			windowRestore.removeHandler();
		}
		window = aWindow;
		if (window != null) {
			windowMinimize = window.addMinimizeHandler(new MinimizeHandler<WindowUI>() {

				@Override
				public void onMinimize(MinimizeEvent<WindowUI> anEvent) {
					updateToolsVisibility();
				}
			});
			windowMaximize = window.addMaximizeHandler(new MaximizeHandler<WindowUI>() {

				@Override
				public void onMaximize(MaximizeEvent<WindowUI> anEvent) {
					updateToolsVisibility();
				}
			});
			windowRestore = window.addRestoreHandler(new RestoreHandler<WindowUI>() {

				@Override
				public void onRestore(RestoreEvent<WindowUI> anEvent) {
					updateToolsVisibility();
				}
			});
		}
		updateToolsVisibility();
	}

	@Override
	public String getHTML() {
		return label.getHTML();
	}

	@Override
	public void setHTML(String html) {
		if (html != null && !html.isEmpty()) {
			label.setHTML(html);
		} else {
			label.setHTML("&nbsp;");
		}
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public void setText(String text) {
		if (text != null && !text.isEmpty()) {
			label.setText(text);
		} else {
			label.setHTML("&nbsp;");
		}
	}

	public ImageResource getIcon() {
		return icon;
	}

	public void setIcon(ImageResource aValue) {
		icon = aValue;
		if (icon != null) {
			label.getElement().getStyle().setPaddingLeft(icon.getWidth(), Style.Unit.PX);
			label.getElement().getStyle().setBackgroundImage("url(" + icon.getSafeUri().asString() + ")");
			label.getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");
		} else {
			label.getElement().getStyle().clearPaddingLeft();
			label.getElement().getStyle().clearBackgroundImage();
			label.getElement().getStyle().clearProperty("backgroundRepeat");
		}
	}

	public void updateToolsVisibility() {
		btnMinimize.getElement().getStyle().setDisplay(window.isMinimizable() && !window.isMinimized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
		btnMaximize.getElement().getStyle().setDisplay(window.isMaximizable() && !window.isMaximized() && !window.isMinimized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
		btnRestore.getElement().getStyle().setDisplay(window.isMinimized() || window.isMaximized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
		btnClose.getElement().getStyle().setDisplay(window.isClosable() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
	}
}

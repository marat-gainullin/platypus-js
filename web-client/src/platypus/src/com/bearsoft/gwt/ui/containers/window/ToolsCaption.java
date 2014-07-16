/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers.window;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Panel;
import com.bearsoft.gwt.ui.containers.window.events.MaximizeEvent;
import com.bearsoft.gwt.ui.containers.window.events.MaximizeHandler;
import com.bearsoft.gwt.ui.containers.window.events.MinimizeEvent;
import com.bearsoft.gwt.ui.containers.window.events.MinimizeHandler;
import com.bearsoft.gwt.ui.containers.window.events.RestoreEvent;
import com.bearsoft.gwt.ui.containers.window.events.RestoreHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class ToolsCaption extends FlowPanel implements HasHTML {

	public static final String WINDOW_TOOL_CLASS_NAME = "window-tool";

	public interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div class=\"{0}\"></div>")
		SafeHtml classedDiv(String aClasses);
	}

	private static final Template template = GWT.create(Template.class);

	protected HTML label = new HTML();
	protected ImageResource icon;
	protected Button anchor = new Button();
	protected Panel tools = new FlowPanel();
	protected Button btnMinimize = new Button(template.classedDiv(WINDOW_TOOL_CLASS_NAME), new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			window.minimize();
		}
	});
	protected Button btnMaximize = new Button(template.classedDiv(WINDOW_TOOL_CLASS_NAME), new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			window.maximize();
		}
	});
	protected Button btnRestore = new Button(template.classedDiv(WINDOW_TOOL_CLASS_NAME), new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			window.restore();
		}
	});
	protected Button btnClose = new Button(template.classedDiv(WINDOW_TOOL_CLASS_NAME), new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			window.close();
		}
	});
	protected WindowUI window;

	protected HandlerRegistration windowMinimize;
	protected HandlerRegistration windowMaximize;
	protected HandlerRegistration windowRestore;

	public ToolsCaption(WindowUI aWindow) {
		super();
		anchor.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
		setStyleName("window-caption");
		setWindow(aWindow);
		anchor.getElement().addClassName(WINDOW_TOOL_CLASS_NAME);		
		btnMinimize.getElement().getFirstChildElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-minimize");
		btnMaximize.getElement().getFirstChildElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-maximize");
		btnRestore.getElement().getFirstChildElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-restore");
		btnClose.getElement().getFirstChildElement().addClassName(WINDOW_TOOL_CLASS_NAME + "-close");
		for (Widget w : new Widget[] { btnMinimize, btnMaximize, btnRestore, btnClose }) {
			w.getElement().getStyle().setMargin(0, Style.Unit.PX);
			w.getElement().getStyle().setPadding(0, Style.Unit.PX);
			w.getElement().removeClassName("gwt-Button");
		}

		label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		label.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		anchor.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		anchor.getElement().getStyle().setWidth(0, Style.Unit.PX);
		anchor.getElement().getStyle().setPadding(0, Style.Unit.PX);
		anchor.getElement().getStyle().setMargin(0, Style.Unit.PX);
		anchor.getElement().getStyle().setProperty("boxSizing", "content-box");
		
		tools.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		tools.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		tools.getElement().getStyle().setRight(0, Style.Unit.PX);

		add(label);
		add(anchor);
		add(tools);
		tools.add(btnMinimize);
		tools.add(btnMaximize);
		tools.add(btnRestore);
		tools.add(btnClose);
		addDomHandler(new DoubleClickHandler(){

			@Override
            public void onDoubleClick(DoubleClickEvent event) {
				if(window.isMaximized()){
					window.restore();
				}else{
					window.maximize();
				}
            }
			
		}, DoubleClickEvent.getType());
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
		label.setHTML(html);
	}

	@Override
	public String getText() {
		return label.getText();
	}

	@Override
	public void setText(String text) {
		label.setText(text);
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

	private void updateToolsVisibility() {
		btnMinimize.setVisible(window.isMinimizable() && !window.isMinimized());
		btnMaximize.setVisible(window.isMaximizable() && !window.isMaximized() && !window.isMinimized());
		btnRestore.setVisible(window.isMinimized() || window.isMaximized());
		btnClose.setVisible(window.isClosable());
	}
}

package com.eas.window;

import com.eas.core.XElement;
import com.eas.window.events.MaximizeEvent;
import com.eas.window.events.MaximizeHandler;
import com.eas.window.events.MinimizeEvent;
import com.eas.window.events.MinimizeHandler;
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHTML;

/**
 *
 * @author mg
 */
public class Caption implements HasHTML {

    public static final String WINDOW_TOOL_CLASS_NAME = "window-tool";
    public static final String CAPTION_CLASS_NAME = "window-caption";

    protected String icon;
    protected Element element = Document.get().createDivElement();
    protected Element label = Document.get().createDivElement();
    protected Element tools = Document.get().createDivElement();
    protected Element btnMinimize = Document.get().createDivElement();
    protected Element btnMaximize = Document.get().createDivElement();
    protected Element btnRestore = Document.get().createDivElement();
    protected Element btnClose = Document.get().createDivElement();
    protected WindowPanel window;

    protected HandlerRegistration windowMinimize;
    protected HandlerRegistration windowMaximize;
    protected HandlerRegistration windowRestore;

    public Caption(WindowPanel aWindow) {
        super();
        element.setClassName(CAPTION_CLASS_NAME);
        btnMinimize.addClassName(WINDOW_TOOL_CLASS_NAME + "-minimize");
        btnMaximize.addClassName(WINDOW_TOOL_CLASS_NAME + "-maximize");
        btnRestore.addClassName(WINDOW_TOOL_CLASS_NAME + "-restore");
        btnClose.addClassName(WINDOW_TOOL_CLASS_NAME + "-close");
        for (Element e : new Element[]{btnMinimize, btnMaximize, btnRestore, btnClose}) {
            e.getStyle().setMargin(0, Style.Unit.PX);
            e.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            e.addClassName(WINDOW_TOOL_CLASS_NAME);
        }

        label.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.getStyle().setPosition(Style.Position.RELATIVE);
        label.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        label.setClassName("window-caption-text");// to implicitly remove
        // gwt-HTML class

        tools.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        tools.getStyle().setFloat(Style.Float.RIGHT);
        tools.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        tools.setClassName("window-caption-tools");

        element.getStyle().setPosition(Style.Position.RELATIVE);

        element.appendChild(tools);
        element.appendChild(label);
        tools.appendChild(btnMinimize);
        tools.appendChild(btnMaximize);
        tools.appendChild(btnRestore);
        tools.appendChild(btnClose);

        btnMinimize.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                window.minimize();
            }
        });
        btnMaximize.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                window.maximize();
            }
        });
        btnRestore.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                window.restore();
            }
        });
        btnClose.<XElement>cast().addEventListener(BrowserEvents.CLICK, new XElement.NativeHandler() {

            @Override
            public void on(NativeEvent event) {
                window.close();
            }
        });

        element.<XElement>cast().addEventListener(BrowserEvents.DBLCLICK, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (window.isMaximized()) {
                    window.restore();
                } else {
                    window.maximize();
                }
            }

        });
        setWindow(aWindow);
    }

    public Caption(WindowPanel aWindow, String aCaptionHtml) {
        this(aWindow);
        label.setInnerHTML(aCaptionHtml);
    }

    private void setWindow(WindowPanel aWindow) {
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
            windowMinimize = window.addMinimizeHandler(new MinimizeHandler() {

                @Override
                public void onMinimize(MinimizeEvent anEvent) {
                    updateToolsVisibility();
                }
            });
            windowMaximize = window.addMaximizeHandler(new MaximizeHandler() {

                @Override
                public void onMaximize(MaximizeEvent anEvent) {
                    updateToolsVisibility();
                }
            });
            windowRestore = window.addRestoreHandler(new RestoreHandler() {

                @Override
                public void onRestore(RestoreEvent anEvent) {
                    updateToolsVisibility();
                }
            });
        }
        updateToolsVisibility();
    }

    public Element getElement() {
        return element;
    }

    @Override
    public String getHTML() {
        return label.getInnerHTML();
    }

    @Override
    public void setHTML(String html) {
        if (html != null && !html.isEmpty()) {
            label.setInnerHTML(html);
        } else {
            label.setInnerHTML("&nbsp;");
        }
    }

    @Override
    public String getText() {
        return label.getInnerText();
    }

    @Override
    public void setText(String text) {
        if (text != null && !text.isEmpty()) {
            label.setInnerText(text);
        } else {
            label.setInnerHTML("&nbsp;");
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String aValue) {
        icon = aValue;
        Style ls = label.getStyle();
        if (icon != null) {
            ls.setBackgroundImage("url(" + icon + ")");
            ls.setProperty("backgroundRepeat", "no-repeat");
        } else {
            ls.clearPaddingLeft();
            ls.clearBackgroundImage();
            ls.clearProperty("backgroundRepeat");
        }
    }

    public void updateToolsVisibility() {
        btnMinimize.getStyle().setDisplay(window.isMinimizable() && !window.isMinimized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
        btnMaximize.getStyle().setDisplay(window.isMaximizable() && !window.isMaximized() && !window.isMinimized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
        btnRestore.getStyle().setDisplay(window.isMinimized() || window.isMaximized() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
        btnClose.getStyle().setDisplay(window.isClosable() ? Style.Display.INLINE_BLOCK : Style.Display.NONE);
    }
}

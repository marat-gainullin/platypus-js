/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers.window;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.bearsoft.gwt.ui.containers.window.events.ActivateEvent;
import com.bearsoft.gwt.ui.containers.window.events.ActivateHandler;
import com.bearsoft.gwt.ui.containers.window.events.BeforeCloseEvent;
import com.bearsoft.gwt.ui.containers.window.events.BeforeCloseHandler;
import com.bearsoft.gwt.ui.containers.window.events.ClosedEvent;
import com.bearsoft.gwt.ui.containers.window.events.ClosedHandler;
import com.bearsoft.gwt.ui.containers.window.events.DeactivateEvent;
import com.bearsoft.gwt.ui.containers.window.events.DeactivateHandler;
import com.bearsoft.gwt.ui.containers.window.events.MaximizeEvent;
import com.bearsoft.gwt.ui.containers.window.events.MaximizeHandler;
import com.bearsoft.gwt.ui.containers.window.events.MinimizeEvent;
import com.bearsoft.gwt.ui.containers.window.events.MinimizeHandler;
import com.bearsoft.gwt.ui.containers.window.events.RestoreEvent;
import com.bearsoft.gwt.ui.containers.window.events.RestoreHandler;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 *
 * @author mg
 */
public class WindowPopupPanel extends PopupPanel implements WindowUI, RequiresResize {

    protected final WindowPanel window = new WindowPanel() {

        @Override
        protected Widget getMovableTarget() {
            return WindowPopupPanel.this;
        }

        @Override
        protected void fireCloseEvent() {
            // no op here because of popup's event re-fire
        }

    };

    public WindowPopupPanel() {
        super();
        super.setWidget(window);
        initPopup();
    }

    public WindowPopupPanel(boolean autoHide) {
        super(autoHide);
        super.setWidget(window);
        initPopup();
    }

    public WindowPopupPanel(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        super.setWidget(window);
        initPopup();
    }

    protected final void initPopup() {
        // clearPopupDecorations
        getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        getElement().getStyle().setMargin(0, Style.Unit.PX);
        getElement().getStyle().setPadding(0, Style.Unit.PX);
        // close event re-fire
        addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                ClosedEvent.<WindowUI>fire(window, window);
            }
        });
    }

    @Override
    public void onResize() {
        window.onResize();
    }

    @Override
    public void setCaptionWidget(HasHTML aCaptionWidget) {
        window.setCaptionWidget(aCaptionWidget);
    }

    @Override
    public boolean isMovable() {
        return window.isMovable();
    }

    @Override
    public void setMovable(boolean aValue) {
        window.setMovable(aValue);
    }

    @Override
    public boolean isMinimized() {
        return window.isMinimized();
    }

    @Override
    public boolean isMaximized() {
        return window.isMaximized();
    }

    @Override
    public boolean isMinimizable() {
        return window.isMinimizable();
    }

    @Override
    public void setMinimizable(boolean minimizable) {
        window.setMinimizable(minimizable);
    }

    @Override
    public boolean isMaximizable() {
        return window.isMaximizable();
    }

    @Override
    public void setMaximizable(boolean maximizable) {
        window.setMaximizable(maximizable);
    }

    @Override
    public boolean isClosable() {
        return window.isClosable();
    }

    @Override
    public void setClosable(boolean Closable) {
        window.setClosable(Closable);
    }

    @Override
    public void maximize() {
        window.maximize();
    }

    /**
     * Minimizes the window. Minimize here is setting zero as height.
     * Descendants may decide to do something else. minimize does a snapshot of
     * all window's metrics. So subsequent restore or restoreSnapshot calls will
     * lead to correct restoration of the window.
     */
    @Override
    public void minimize() {
        window.minimize();
    }

    @Override
    public void restoreSnapshot() {
        window.restoreSnapshot();
    }

    @Override
    public void restore() {
        window.restore();
    }

    @Override
    public boolean isActive() {
        return window.isActive();
    }

    @Override
    public void setActive(boolean aValue) {
        window.setActive(aValue);
    }

    @Override
    public boolean isUndecorated() {
        return window.isUndecorated();
    }

    @Override
    public void setUndecorated(boolean aValue) {
        window.setUndecorated(aValue);
    }

    @Override
    public boolean isResizable() {
        return window.isResizable();
    }

    @Override
    public void setResizable(boolean aValue) {
        window.setResizable(aValue);
    }

    @Override
    public void setPosition(double aLeft, double aTop) {
        super.setPopupPosition((int) aLeft, (int) aTop);
    }

    @Override
    public void setSize(double aWidth, double aHeight) {
        setWidth(aWidth + "px");
        setHeight(aHeight + "px");
    }

    @Override
    public Widget getWidget() {
        return window.getWidget();
    }

    @Override
    public void setWidget(Widget w) {
        window.setWidget(w);
    }

    @Override
    public HasHTML getCaptionWidget() {
        return window.getCaptionWidget();
    }

    @Override
    public void close() {
        hide();
    }

    @Override
    public void hide(boolean autoClosed) {
        if (!BeforeCloseEvent.<WindowUI>fire(window, window)) {
            super.hide(autoClosed);
        }
    }

    @Override
    public HandlerRegistration addOpenHandler(OpenHandler<WindowUI> handler) {
        return window.addHandler(handler, OpenEvent.getType());
    }

    @Override
    public HandlerRegistration addClosedHandler(ClosedHandler<WindowUI> handler) {
        return window.addHandler(handler, ClosedEvent.getType());
    }

    @Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<WindowUI> handler) {
        return window.addHandler(handler, BeforeCloseEvent.getType());
    }

    @Override
    public HandlerRegistration addActivateHandler(ActivateHandler<WindowUI> handler) {
        return window.addHandler(handler, ActivateEvent.getType());
    }

    @Override
    public HandlerRegistration addDeactivateHandler(DeactivateHandler<WindowUI> handler) {
        return window.addHandler(handler, DeactivateEvent.getType());
    }

    @Override
    public HandlerRegistration addMinimizeHandler(MinimizeHandler<WindowUI> handler) {
        return window.addHandler(handler, MinimizeEvent.getType());
    }

    @Override
    public HandlerRegistration addMaximizeHandler(MaximizeHandler<WindowUI> handler) {
        return window.addHandler(handler, MaximizeEvent.getType());
    }

    @Override
    public HandlerRegistration addRestoreHandler(RestoreHandler<WindowUI> handler) {
        return window.addHandler(handler, RestoreEvent.getType());
    }
}

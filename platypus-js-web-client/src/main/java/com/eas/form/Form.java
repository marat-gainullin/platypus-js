package com.eas.form;

import java.util.HashMap;
import java.util.Map;

import com.eas.client.AppClient;
import com.eas.core.Logger;
import com.eas.core.HasPublished;
import com.eas.core.Predefine;
import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.eas.ui.EventsPublisher;
import com.eas.ui.Widget;
import com.eas.widgets.containers.Container;
import com.eas.widgets.containers.Anchors;
import com.eas.widgets.containers.Desktop;
import com.eas.widgets.WidgetsPublisher;
import com.eas.window.WindowPanel;
import com.eas.window.events.ActivateEvent;
import com.eas.window.events.ActivateHandler;
import com.eas.window.events.BeforeCloseEvent;
import com.eas.window.events.BeforeCloseHandler;
import com.eas.window.events.ClosedEvent;
import com.eas.window.events.ClosedHandler;
import com.eas.window.events.DeactivateEvent;
import com.eas.window.events.DeactivateHandler;
import com.eas.window.events.MaximizeEvent;
import com.eas.window.events.MaximizeHandler;
import com.eas.window.events.MinimizeEvent;
import com.eas.window.events.MinimizeHandler;
import com.eas.window.events.MoveEvent;
import com.eas.window.events.MoveHandler;
import com.eas.window.events.OpenEvent;
import com.eas.window.events.OpenHandler;
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class Form extends WindowPanel implements HasPublished {

    protected static final Map<String, Form> showingForms = new HashMap<String, Form>();

    public static JavaScriptObject getShownForms() {
        JsArray<JavaScriptObject> jsArray = JsArray.createArray().cast();
        for (Form f : showingForms.values()) {
            jsArray.push(f.getPublished());
        }
        return jsArray;
    }

    public static JavaScriptObject getShownForm(String aFormKey) {
        Form f = showingForms.get(aFormKey);
        return f != null ? f.getPublished() : null;
    }

    protected static JavaScriptObject onChange;

    public static JavaScriptObject getOnChange() {
        return onChange;
    }

    public static void setOnChange(JavaScriptObject aValue) {
        onChange = aValue;
    }

    protected static void shownFormsChanged(JavaScriptObject aSource) {
        if (onChange != null) {
            try {
                Utils.executeScriptEventVoid(Predefine.prerequire("forms/form"), onChange, EventsPublisher.publishSourcedEvent(aSource));
            } catch (Exception ex) {
                Logger.severe(ex);
            }
        }
    }

    protected Point location;
    protected Widget view;

    protected int defaultCloseOperation = 2;
    protected String icon;
    protected String title;
    protected float opacity = 1.0f;
    protected boolean alwaysOnTop;
    protected boolean locationByPlatform;
    protected JavaScriptObject windowOpened;
    protected JavaScriptObject windowClosing;
    protected JavaScriptObject windowClosed;
    protected JavaScriptObject windowMinimized;
    protected JavaScriptObject windowRestored;
    protected JavaScriptObject windowMaximized;
    protected JavaScriptObject windowActivated;
    protected JavaScriptObject windowDeactivated;

    protected Runnable handlersResolver;

    protected String formKey = "window-" + Document.get().createUniqueId();

    public Form() {
        this(new Anchors());
        WidgetsPublisher.publish((Anchors) view);
    }

    public Form(Widget aView) {
        super();
        view = aView;
        addMoveHandler(new MoveHandler() {

            @Override
            public void onMove(MoveEvent event) {
                Point p = new Point(event.getX(), event.getY());
                location = p;
            }
        });
        setClosable(true);
        setMaximizable(maximizable);
        setMinimizable(minimizable);
        setUndecorated(undecorated);
        setMovable(true);
        setResizable(resizable);
        getElement().getStyle().setOpacity(opacity);

        caption.setText(title);
        registerWindowListeners();
    }

    public Widget getView() {
        return view;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String aValue) throws Exception {
        showingForms.remove(formKey);
        formKey = aValue;
        if (isOpened()) {
            showingForms.put(formKey, this);
        }
        shownFormsChanged(published);
    }

    @Override
    public JavaScriptObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JavaScriptObject aValue) {
        if (published != aValue) {
            published = aValue;
            publishFormFacade(published, view, this);
        }
    }

    public void resolveHandlers() {
        if (handlersResolver != null) {
            handlersResolver.run();
            handlersResolver = null;
        }
    }

    public JavaScriptObject submit(final String aAction, final JavaScriptObject aOnSuccess, final JavaScriptObject aOnFailure) {
        Map<String, String> formData = new HashMap<String, String>();
        if (getView() instanceof Container) {
            gatherForm(formData, (Container) getView());
        }
        return Utils.publishCancellable(AppClient.getInstance().submitForm(aAction, RequestBuilder.POST, "application/x-www-form-urlencoded", formData, new Callback<XMLHttpRequest, XMLHttpRequest>() {
            @Override
            public void onSuccess(XMLHttpRequest aRequest) {
                try {
                    if (aOnSuccess != null) {
                        Utils.executeScriptEventVoid(aOnSuccess, aOnSuccess, aRequest);
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }

            @Override
            public void onFailure(XMLHttpRequest aRequest) {
                try {
                    if (aOnFailure != null) {
                        Utils.executeScriptEventVoid(aOnFailure, aOnFailure, aRequest);
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
            }
        }));
    }

    private void gatherForm(Map<String, String> aFormData, Container aContainer) {
        for (int i = 0; i < aContainer.getCount(); i++) {
            Widget w = aContainer.get(i);
            if (w instanceof HasValue<?>) {
                String name = w.getJsName();
                Object value = ((HasValue<Object>) w).getValue();
                if (name != null && !name.isEmpty() && (value == null || value instanceof String || value instanceof Number)) {
                    aFormData.put(name, value != null ? value.toString() : null);
                }
            }
            if (w instanceof Container) {
                gatherForm(aFormData, (Container) w);
            }
        }
    }

    public void show(boolean aModal, final JavaScriptObject aCallback, Desktop aDesktop) {
        //popup = new WindowPopupPanel(this, autoHide, aModal);
        //popup.setWidget(view);
        String sWidth = view.getElement().getStyle().getWidth();
        String sHeight = view.getElement().getStyle().getHeight();
        double width = sWidth != null && sWidth.endsWith("px") ? Double.parseDouble(sWidth.substring(0, sWidth.length() - 2)) : 0;
        double height = sHeight != null && sHeight.endsWith("px") ? Double.parseDouble(sHeight.substring(0, sHeight.length() - 2)) : 0;
        //popup.setSize(sWidth, sHeight);
        if (locationByPlatform) {
            if (aDesktop != null) {
                aDesktop.add(this);
                setPosition(aDesktop.getConsideredPosition().getX(), aDesktop.getConsideredPosition().getY());
            } else {
                int left = (Document.get().getClientWidth() - (int) width) / 2;
                int top = (Document.get().getClientHeight() - (int) height) / 2;
                setPosition(left, top);
                //popup.show();
            }
        } else {
            if (aDesktop != null) {
                aDesktop.add(this);
                if (location != null) {
                    setPosition(location.getX(), location.getY());
                } else {
                    int left = (aDesktop.getElement().getClientWidth() - (int) width) / 2;
                    int top = (aDesktop.getElement().getClientHeight() - (int) height) / 2;
                    setPosition(left, top);
                }
            } else {
                if (location != null) {
                    setPosition(location.getX(), location.getY());
                } else {
                    int left = (Document.get().getClientWidth() - (int) width) / 2;
                    int top = (Document.get().getClientHeight() - (int) height) / 2;
                    setPosition(left, top);
                }
                //popup.show();
            }
        }
    }

    private void registerWindowListeners() {
        addOpenHandler(new OpenHandler() {

            @Override
            public void onOpen(OpenEvent event) {
                showingForms.put(formKey, Form.this);
                if (windowOpened != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowOpened, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
                shownFormsChanged(published);
            }

        });
        addActivateHandler(new ActivateHandler() {

            @Override
            public void onActivate(ActivateEvent event) {
                for (Form w : showingForms.values()) {
                    if (w != event.getTarget() && !(w.getParent() instanceof Desktop)) {
                        w.deactivate();
                    }
                }
                if (windowActivated != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowActivated, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
        addDeactivateHandler(new DeactivateHandler() {

            @Override
            public void onDeactivate(DeactivateEvent event) {
                if (windowDeactivated != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowDeactivated, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
        addMinimizeHandler(new MinimizeHandler() {

            @Override
            public void onMinimize(MinimizeEvent event) {
                if (windowMinimized != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowMinimized, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
        addMaximizeHandler(new MaximizeHandler() {

            @Override
            public void onMaximize(MaximizeEvent event) {
                if (windowMaximized != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowMaximized, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
        addRestoreHandler(new RestoreHandler() {

            @Override
            public void onRestore(RestoreEvent event) {
                if (windowRestored != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowRestored, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }

        });
        addBeforeCloseHandler(new BeforeCloseHandler() {

            @Override
            public void onBeforeClose(BeforeCloseEvent anEvent) {
                if (windowClosing != null) {
                    try {
                        Boolean res = Utils.executeScriptEventBoolean(published, windowClosing, EventsPublisher.publishWindowEvent(anEvent, published));
                        if (Boolean.FALSE.equals(res)) {
                            anEvent.setCancelled(true);
                        }
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
            }
        });
        addClosedHandler(new ClosedHandler() {

            @Override
            public void onClosed(ClosedEvent event) {
                showingForms.remove(formKey);
                if (windowClosed != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowClosed, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.severe(ex);
                    }
                }
                shownFormsChanged(published);
                //popup = null;
            }
        });
    }

    protected boolean isOpened() {
        return false; //popup != null;
    }

    public void close(Object aSelected, JavaScriptObject aCallback) {
        if (isOpened()) {
            boolean wasModal = false;//popup.isModal();
            close();
            if (!isOpened() && wasModal && aCallback != null) {
                aCallback.<JsObject>cast().call(published, Utils.toJs(aSelected));
            }
        }
    }

    protected native static void publishFormFacade(JavaScriptObject aPublished, Widget aView, Form aForm);

    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    public void setDefaultCloseOperation(int aValue) {
        defaultCloseOperation = aValue;
    }

    public boolean isLocationByPlatform() {
        return locationByPlatform;
    }

    public void setLocationByPlatform(boolean aValue) {
        locationByPlatform = aValue;
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean aValue) {
        if (alwaysOnTop != aValue) {
            alwaysOnTop = aValue;
            if (alwaysOnTop) {
                getElement().getStyle().setZIndex(Integer.MAX_VALUE);
            } else {
                getElement().getStyle().setZIndex(0);
            }
        }
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float aValue) {
        opacity = aValue;
        getElement().getStyle().setOpacity(aValue);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aValue) {
        title = aValue;
        caption.setText(aValue);
    }

    public double getLeft() {
        return location != null ? location.getX() : 0;
    }

    public void setLeft(double aValue) {
        locationByPlatform = false;
        setPosition(aValue, getTop());
        if (location == null) {
            location = new Point(0, 0);
        }
        location = new Point(aValue, location.getY());// setX
    }

    public double getTop() {
        return location != null ? location.getY() : 0;
    }

    public void setTop(double aValue) {
        locationByPlatform = false;
        setPosition(getLeft(), aValue);
        if (location == null) {
            location = new Point(0, 0);
        }
        location = new Point(location.getX(), aValue);// setY
    }

    public double getWidth() {
        return view.getElement().getOffsetWidth();
    }

    public void setWidth(double aValue) {
        super.setWidth(aValue + "px");
    }

    public double getHeight() {
        return view.getElement().getOffsetHeight();
    }

    public void setHeight(double aValue) {
        super.setHeight(aValue + "px");
    }

    @Override
    public void setMinimizable(boolean aValue) {
        if (aValue != minimizable) {
            super.setMinimizable(aValue);
            caption.updateToolsVisibility();
        }
    }

    @Override
    public void setMaximizable(boolean aValue) {
        if (maximizable != aValue) {
            super.setMaximizable(aValue);
            caption.updateToolsVisibility();
        }
    }

    @Override
    public void setClosable(boolean aValue) {
        if (closable != aValue) {
            super.setClosable(aValue);
            caption.updateToolsVisibility();
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String aIcon) {
        icon = aIcon;
        caption.setIcon(aIcon);
    }

    public JavaScriptObject getWindowOpened() {
        return windowOpened;
    }

    public void setWindowOpened(JavaScriptObject aValue) {
        windowOpened = aValue;
    }

    public JavaScriptObject getWindowClosing() {
        return windowClosing;
    }

    public void setWindowClosing(JavaScriptObject aValue) {
        windowClosing = aValue;
    }

    public JavaScriptObject getWindowClosed() {
        return windowClosed;
    }

    public void setWindowClosed(JavaScriptObject aValue) {
        windowClosed = aValue;
    }

    public JavaScriptObject getWindowMinimized() {
        return windowMinimized;
    }

    public void setWindowMinimized(JavaScriptObject aValue) {
        windowMinimized = aValue;
    }

    public JavaScriptObject getWindowRestored() {
        return windowRestored;
    }

    public void setWindowRestored(JavaScriptObject aValue) {
        windowRestored = aValue;
    }

    public JavaScriptObject getWindowMaximized() {
        return windowMaximized;
    }

    public void setWindowMaximized(JavaScriptObject aValue) {
        windowMaximized = aValue;
    }

    public JavaScriptObject getWindowActivated() {
        return windowActivated;
    }

    public void setWindowActivated(JavaScriptObject aValue) {
        windowActivated = aValue;
    }

    public JavaScriptObject getWindowDeactivated() {
        return windowDeactivated;
    }

    public void setWindowDeactivated(JavaScriptObject aValue) {
        windowDeactivated = aValue;
    }
}

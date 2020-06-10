package com.eas.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.AppClient;
import com.eas.core.HasPublished;
import com.eas.core.Predefine;
import com.eas.core.Utils;
import com.eas.core.Utils.JsObject;
import com.eas.ui.HasJsName;
import com.eas.ui.EventsPublisher;
import com.eas.widgets.AnchorsPane;
import com.eas.widgets.DesktopPane;
import com.eas.widgets.WidgetsPublisher;
import com.eas.window.ToolsCaption;
import com.eas.window.WindowPanel;
import com.eas.window.WindowPopupPanel;
import com.eas.window.WindowUI;
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
import com.eas.window.events.RestoreEvent;
import com.eas.window.events.RestoreHandler;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
import java.util.ArrayList;
import java.util.List;

public class PlatypusWindow extends WindowPanel implements HasPublished {

    protected static final Map<String, PlatypusWindow> showingForms = new HashMap<String, PlatypusWindow>();

    public static JavaScriptObject getShownForms() {
        JsArray<JavaScriptObject> jsArray = JsArray.createArray().cast();
        for (PlatypusWindow f : showingForms.values()) {
            jsArray.push(f.getPublished());
        }
        return jsArray;
    }

    public static List<PlatypusWindow> getShownModalForms() {
        List<PlatypusWindow> modalForms = new ArrayList<>();
        for (PlatypusWindow f : showingForms.values()) {
            if (f.popup.isModal()) {
                modalForms.add(f);
            }
        }
        return modalForms;
    }

    public static JavaScriptObject getShownForm(String aFormKey) {
        PlatypusWindow f = showingForms.get(aFormKey);
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
                Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected JavaScriptObject published;

    protected ToolsCaption caption;
    protected Point location;
    protected Widget view;
    protected WindowPopupPanel popup;

    protected int defaultCloseOperation = 2;
    protected ImageResource icon;
    protected String title;
    protected float opacity = 1.0f;
    protected boolean alwaysOnTop;
    protected boolean locationByPlatform;
    protected boolean autoHide;
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

    public PlatypusWindow() {
        this(new AnchorsPane());
        WidgetsPublisher.publish((AnchorsPane) view);
    }

    public PlatypusWindow(Widget aView) {
        super();
        view = aView;
        caption = new ToolsCaption(this, "");
        setCaptionWidget(caption);
        addMoveHandler(new MoveHandler<WindowUI>() {

            @Override
            public void onMove(MoveEvent<WindowUI> event) {
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
        if (getView() instanceof HasWidgets) {
            gatherForm(formData, (HasWidgets) getView());
        }
        return Utils.publishCancellable(AppClient.getInstance().submitForm(aAction, RequestBuilder.POST, "application/x-www-form-urlencoded", formData, new Callback<XMLHttpRequest, XMLHttpRequest>() {
            @Override
            public void onSuccess(XMLHttpRequest aRequest) {
                try {
                    if (aOnSuccess != null) {
                        Utils.executeScriptEventVoid(aOnSuccess, aOnSuccess, aRequest);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onFailure(XMLHttpRequest aRequest) {
                try {
                    if (aOnFailure != null) {
                        Utils.executeScriptEventVoid(aOnFailure, aOnFailure, aRequest);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
    }

    private void gatherForm(Map<String, String> aFormData, HasWidgets aContainer) {
        Iterator<Widget> widgets = aContainer.iterator();
        while (widgets.hasNext()) {
            Widget w = widgets.next();
            if (w instanceof HasValue<?> && w instanceof HasJsName) {
                String name = ((HasJsName) w).getJsName();
                Object value = ((HasValue<Object>) w).getValue();
                if (name != null && !name.isEmpty() && (value == null || value instanceof String || value instanceof Number)) {
                    aFormData.put(name, value != null ? value.toString() : null);
                }
            }
            if (w instanceof HasWidgets) {
                gatherForm(aFormData, (HasWidgets) w);
            }
        }
    }

    @Override
    protected Widget getMovableTarget() {
        if (getParent() instanceof DesktopPane) {
            return this;
        } else {
            return popup != null ? popup : super.getMovableTarget();
        }
    }

    public void show(boolean aModal, final JavaScriptObject aCallback, DesktopPane aDesktop) {
        popup = new WindowPopupPanel(this, autoHide, aModal);
        popup.setWidget(view);
        String sWidth = view.getElement().getStyle().getWidth();
        String sHeight = view.getElement().getStyle().getHeight();
        double width = sWidth != null && sWidth.endsWith("px") ? Double.parseDouble(sWidth.substring(0, sWidth.length() - 2)) : 0;
        double height = sHeight != null && sHeight.endsWith("px") ? Double.parseDouble(sHeight.substring(0, sHeight.length() - 2)) : 0;
        popup.setSize(sWidth, sHeight);
        if (locationByPlatform) {
            if (aDesktop != null) {
                aDesktop.add(this);
                setPosition(aDesktop.getConsideredPosition().getX(), aDesktop.getConsideredPosition().getY());
            } else {
                int left = (Document.get().getClientWidth() - (int) width) / 2;
                int top = (Document.get().getClientHeight() - (int) height) / 2;
                setPosition(left, top);
                popup.show();
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
                popup.show();
            }
        }
    }

    private void registerWindowListeners() {
        addOpenHandler(new OpenHandler<WindowUI>() {

            @Override
            public void onOpen(OpenEvent<WindowUI> event) {
                showingForms.put(formKey, PlatypusWindow.this);
                if (windowOpened != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowOpened, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
                shownFormsChanged(published);
            }

        });
        addActivateHandler(new ActivateHandler<WindowUI>() {

            @Override
            public void onActivate(ActivateEvent<WindowUI> event) {
                for (WindowUI w : showingForms.values()) {
                    if (w != event.getTarget() && w instanceof PlatypusWindow) {
                        PlatypusWindow pw = (PlatypusWindow) w;
                        if (!(pw.getParent() instanceof DesktopPane)) {
                            w.deactivate();
                        }
                    }
                }
                if (windowActivated != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowActivated, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        });
        addDeactivateHandler(new DeactivateHandler<WindowUI>() {

            @Override
            public void onDeactivate(DeactivateEvent<WindowUI> event) {
                if (windowDeactivated != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowDeactivated, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        });
        addMinimizeHandler(new MinimizeHandler<WindowUI>() {

            @Override
            public void onMinimize(MinimizeEvent<WindowUI> event) {
                if (windowMinimized != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowMinimized, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        });
        addMaximizeHandler(new MaximizeHandler<WindowUI>() {

            @Override
            public void onMaximize(MaximizeEvent<WindowUI> event) {
                if (windowMaximized != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowMaximized, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        });
        addRestoreHandler(new RestoreHandler<WindowUI>() {

            @Override
            public void onRestore(RestoreEvent<WindowUI> event) {
                if (windowRestored != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowRestored, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }

        });
        addBeforeCloseHandler(new BeforeCloseHandler<WindowUI>() {

            @Override
            public void onBeforeClose(BeforeCloseEvent<WindowUI> anEvent) {
                if (windowClosing != null) {
                    try {
                        Boolean res = Utils.executeScriptEventBoolean(published, windowClosing, EventsPublisher.publishWindowEvent(anEvent, published));
                        if (Boolean.FALSE.equals(res)) {
                            anEvent.setCancelled(true);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        });
        addClosedHandler(new ClosedHandler<WindowUI>() {

            @Override
            public void onClosed(ClosedEvent<WindowUI> event) {
                showingForms.remove(formKey);
                if (windowClosed != null) {
                    try {
                        Utils.executeScriptEventVoid(published, windowClosed, EventsPublisher.publishWindowEvent(event, published));
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
                shownFormsChanged(published);
                popup = null;
            }
        });
    }

    protected boolean isOpened() {
        return popup != null;
    }

    public void close(Object aSelected, JavaScriptObject aCallback) {
        if (isOpened()) {
            if (popup != null) {
                boolean wasModal = popup.isModal();
                close();
                if (!isOpened() && wasModal && aCallback != null) {
                    aCallback.<JsObject>cast().call(published, Utils.toJs(aSelected));
                }
            } else {
                if (view.isAttached()) {
                    view.removeFromParent();
                }
            }
        }
    }

    protected native static void publishFormFacade(JavaScriptObject aPublished, Widget aView, PlatypusWindow aForm)/*-{
        Object.defineProperty(aPublished, "formKey", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getFormKey()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setFormKey(Ljava/lang/String;)(''+aValue);
	        } 
        });
        Object.defineProperty(aPublished, "defaultCloseOperation", {
        	get : function(){
        		return aForm.@com.eas.form.PlatypusWindow::getDefaultCloseOperation()();
        	},
        	set : function(aValue){
        		if(aValue == null)
        			aValue = 0;
        		aForm.@com.eas.form.PlatypusWindow::setDefaultCloseOperation(I)(aValue * 1);
        	}
        });
        Object.defineProperty(aPublished, "icon", {
        	get : function(){
        		return aForm.@com.eas.form.PlatypusWindow::getIcon()();
        	},
        	set : function(aValue){
      			aForm.@com.eas.form.PlatypusWindow::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
        	}
        });
        Object.defineProperty(aPublished, "title", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getTitle()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setTitle(Ljava/lang/String;)(''+aValue);
	        } 
        });
        Object.defineProperty(aPublished, "resizable", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isResizable()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setResizable(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "minimizable", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isMinimizable()();
	        },
	        set:function(aValue)
	        {
	        	aForm.@com.eas.form.PlatypusWindow::setMinimizable(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "minimized", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isMinimized()();
	        }
        });
        Object.defineProperty(aPublished, "maximizable", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isMaximizable()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setMaximizable(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "closable", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isClosable()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setClosable(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "maximized", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isMaximized()();
	        }
        });
        Object.defineProperty(aPublished, "undecorated", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isUndecorated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setUndecorated(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "opacity", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getOpacity()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.form.PlatypusWindow::setOpacity(F)(aValue * 1);
	        } 
        });
        Object.defineProperty(aPublished, "alwaysOnTop", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isAlwaysOnTop()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setAlwaysOnTop(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "locationByPlatform", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isLocationByPlatform()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setLocationByPlatform(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "left", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getLeft()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.form.PlatypusWindow::setLeft(D)(aValue * 1);
	        } 
        });
        Object.defineProperty(aPublished, "top", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getTop()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.form.PlatypusWindow::setTop(D)(aValue * 1);
	        } 
        });
        Object.defineProperty(aPublished, "width", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWidth()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.form.PlatypusWindow::setWidth(D)(aValue * 1);
	        } 
        });
        Object.defineProperty(aPublished, "height", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getHeight()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.form.PlatypusWindow::setHeight(D)(aValue * 1);
	        } 
        });
        Object.defineProperty(aPublished, "autoHide", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::isAutoHide()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setAutoHide(Z)(!!aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowOpened", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowOpened()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowOpened(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowClosing", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowClosing()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowClosing(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowClosed", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowClosed()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowClosed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowMinimized", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowMinimized()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowMinimized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowRestored", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowRestored()();
	        },
	        set:function(aValue)  {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowRestored(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowMaximized", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowMaximized()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowMaximized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowActivated", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowActivated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowActivated(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aPublished, "onWindowDeactivated", {
	        get:function() {
	        	return aForm.@com.eas.form.PlatypusWindow::getWindowDeactivated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.form.PlatypusWindow::setWindowDeactivated(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        
        (function() {
		var B = @com.eas.core.Predefine::boxing;
		var Logger = @com.eas.core.Predefine::logger;
	        var showedWnd = null;
	        var closeCallback = null;
	        aPublished.show = function() {
		        closeCallback = null;
		        showedWnd = aForm.@com.eas.form.PlatypusWindow::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/widgets/DesktopPane;)(false, null, null);
		        aForm.@com.eas.form.PlatypusWindow::activate()();
	        };
	        aPublished.showModal = function(aCallback) {
		        closeCallback = aCallback;
		        showedWnd = aForm.@com.eas.form.PlatypusWindow::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/widgets/DesktopPane;)(true, aCallback, null);
		        aForm.@com.eas.form.PlatypusWindow::activate()();
	        };
	        aPublished.showOnPanel = function(aPanel) {
	        	Logger.info("showOnPanel is unsupported. Use widget.showOn(...) method instead.");
	        };
	        aPublished.showInternalFrame = function(aPanel) {
	        	showedWnd = aForm.@com.eas.form.PlatypusWindow::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/widgets/DesktopPane;)(false, null, aPanel != null ? aPanel.unwrap() : null);
		        aForm.@com.eas.form.PlatypusWindow::activate()();
	        };
	        aPublished.minimize = function(){
	        	aForm.@com.eas.form.PlatypusWindow::minimize()();
	        };
	        aPublished.maximize = function(){
	        	aForm.@com.eas.form.PlatypusWindow::maximize()();
	        };
	        aPublished.toFront = function(){
	        	aForm.@com.eas.form.PlatypusWindow::toFront()();
		        aForm.@com.eas.form.PlatypusWindow::activate()();
	        };
	        aPublished.restore = function(){
	        	aForm.@com.eas.form.PlatypusWindow::restore()();
	        };
	        aPublished.close = function() {
		        if (arguments.length > 0)
		        	aForm.@com.eas.form.PlatypusWindow::close(Ljava/lang/Object;Lcom/google/gwt/core/client/JavaScriptObject;)(arguments[0] == null ? null : B.boxAsJava(arguments[0]), closeCallback);
		        else
		        	aForm.@com.eas.form.PlatypusWindow::close(Ljava/lang/Object;Lcom/google/gwt/core/client/JavaScriptObject;)(null, null);
	        };
	        aPublished.submit = function(aAction, aOnSuccess, aOnFailure) {
	        	aForm.@com.eas.form.PlatypusWindow::submit(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aAction, aOnSuccess, aOnFailure);
	        }
	        aPublished.unwrap = function(){
	        	return aForm;
	        };
        })();
    }-*/;

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
            if (popup != null) {
                if (alwaysOnTop) {
                    popup.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
                } else {
                    popup.getElement().getStyle().setZIndex(0);
                }
            } else {
                if (alwaysOnTop) {
                    getElement().getStyle().setZIndex(Integer.MAX_VALUE);
                } else {
                    getElement().getStyle().setZIndex(0);
                }
            }
        }
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public void setAutoHide(boolean aValue) {
        autoHide = aValue;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float aValue) {
        opacity = aValue;
        if (popup != null) {
            popup.getElement().getStyle().setOpacity(aValue);
        } else {
            getElement().getStyle().setOpacity(aValue);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String aValue) {
        title = aValue;
        caption.setText(aValue);
    }

    @Override
    public void setPosition(double aLeft, double aTop) {
        if (popup != null) {
            super.setPosition(aLeft, aTop);
            popup.setPosition(aLeft, aTop);
        }
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
        return view.getOffsetWidth();
    }

    public void setWidth(double aValue) {
        super.setWidth(aValue + "px");
    }

    public double getHeight() {
        return view.getOffsetHeight();
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

    public ImageResource getIcon() {
        return icon;
    }

    public void setIcon(ImageResource aIcon) {
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

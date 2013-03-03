package com.eas.client.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.Callback;
import com.eas.client.ImageResourceCallback;
import com.eas.client.Utils;
import com.eas.client.application.AppClient;
import com.eas.client.form.api.JSEvents;
import com.eas.client.gxtcontrols.MarginConstraints;
import com.eas.client.gxtcontrols.MarginConstraints.Margin;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusWindow;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.event.ActivateEvent;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent.MinimizeHandler;
import com.sencha.gxt.widget.core.client.event.MoveEvent;
import com.sencha.gxt.widget.core.client.event.MoveEvent.MoveHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.Field;

public class Form {
	protected static final Map<String, Form> showingForms = new HashMap();

	public static JavaScriptObject getShownForms() {
		JsArray<JavaScriptObject> jsArray = JsArray.createArray().cast();
		for (Form f : showingForms.values()) {
			jsArray.push(f.getModule());
		}
		return jsArray;
	}

	public static JavaScriptObject getShownForm(String aFormKey) {
		Form f = showingForms.get(aFormKey);
		return f != null ? f.getModule() : null;
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
				Utils.executeScriptEventVoid(JSEvents.getFormsClass(), onChange, JSEvents.publishScriptSourcedEvent(aSource));
			} catch (Exception ex) {
				Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	protected Window window;
	protected Container view;
	protected int viewPreferredWidth;
	protected int viewPreferredHeight;
	protected JavaScriptObject module;

	protected String iconImage;
	protected Point location;
	protected Point size;

	protected int defaultCloseOperation = 2;
	protected ImageResource icon;
	protected String title;
	protected boolean resizable;
	protected boolean minimizable = true;
	protected boolean maximizable = true;
	protected boolean undecorated;
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

	protected String formKey;

	public static final String PUBLISHED_DATA_KEY = "published";
	public static final String PID_DATA_KEY = "pId";

	public Form(Container aView, Runnable aHandlersResolver) {
		super();
		view = aView;
		handlersResolver = aHandlersResolver;
	}

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String aValue) throws Exception {
		showingForms.remove(formKey);
		formKey = aValue;
		if (window != null && window.isVisible())
			showingForms.put(formKey, this);
		shownFormsChanged(module);
	}

	public JavaScriptObject getModule() {
		return module;
	}

	public void resolveHandlers() {
		if (handlersResolver != null) {
			handlersResolver.run();
			handlersResolver = null;
		}
	}

	public Container getView() {
		return view;
	}

	public int getViewPreferredWidth() {
		return viewPreferredWidth;
	}

	public void setViewPreferredWidth(int aWidth) {
		viewPreferredWidth = aWidth;
	}

	public int getViewPreferredHeight() {
		return viewPreferredHeight;
	}

	public void setViewPreferredHeight(int aHeight) {
		viewPreferredHeight = aHeight;
	}

	public JavaScriptObject submit(String aAction, final JavaScriptObject aDoneCallback) {
		Map<String, String> fd = new HashMap<String, String>(); 
		gatherForm(fd, view);
		return Utils.publishCancellable(AppClient.getInstance().submitForm(aAction, fd, aDoneCallback == null ? null : new Callback<XMLHttpRequest>() {
			@Override
			public void run(XMLHttpRequest aRequest) throws Exception {
				Utils.executeScriptEventVoid(aDoneCallback, aDoneCallback, aRequest);
			}

			@Override
			public void cancel() {
			}
		}));
	}

	private void gatherForm(Map<String, String> aFormData, HasWidgets aContainer) {
		Iterator<Widget> widgets = aContainer.iterator();
		while (widgets.hasNext()) {
			Widget w = widgets.next();
			if (w instanceof Field<?>) {
				Field<Object> field = (Field<Object>) w;
				String name = (String) field.getData(PID_DATA_KEY);
				Object value = field.getValue();
				if (name != null && !name.isEmpty() && (value == null || value instanceof String || value instanceof Number)) {
					aFormData.put(name, value != null ? value.toString() : null);
				}
			}
			if (w instanceof HasWidgets)
				gatherForm(aFormData, (HasWidgets) w);
		}
	}

	public Window show(boolean aModal, final JavaScriptObject aCallback, PlatypusDesktopContainer aDesktop) {
		close(null, null);
		if (!isOpened()) {
			window = new PlatypusWindow(aDesktop, this);
			window.addMoveHandler(new MoveHandler() {

				@Override
				public void onMove(MoveEvent event) {
					location = new Point(event.getX(), event.getY());
				}
			});
			window.addResizeHandler(new ResizeHandler() {

				@Override
				public void onResize(ResizeEvent event) {
					size = new Point(event.getWidth(), event.getHeight());
				}
			});
			window.setClosable(true);
			window.setMaximizable(maximizable);
			window.setMinimizable(minimizable);
			window.setHeaderVisible(!undecorated);
			window.setShadow(!undecorated);
			window.setBorders(false);//(!undecorated);
			window.setDraggable(true);
			window.setResizable(resizable);
			window.setOnEsc(aModal);
			window.setHeadingText(title);
			window.setBodyBorder(false);
			window.getElement().getStyle().setOpacity(opacity);
			window.getBody().getStyle().setOpacity(opacity);
			if (iconImage != null && !iconImage.isEmpty())
				window.getHeader().setIcon(AppClient.getInstance().getImageResource(iconImage).addCallback(new ImageResourceCallback() {
					@Override
					public void run(ImageResource aResource) {
						setIcon(aResource);
						if (window != null)
							window.getHeader().setIcon(aResource);
					}
				}));
			window.setModal(aModal);
			window.setWidget(view);
			view.setVisible(true);
			registerWindowListeners(window);
			boolean wasSize = size != null;
			window.show();
			if (wasSize) {
				window.setPixelSize(size.getX(), size.getY());
			} else {
				int decorHeight = window.getElement().getHeight(false) - window.getBody().getHeight(false);
				int decorWidth = window.getElement().getWidth(false) - window.getBody().getWidth(false);
				window.setPixelSize(viewPreferredWidth + decorWidth, viewPreferredHeight + decorHeight);
			}
			if (locationByPlatform) {
				if (aDesktop != null)
					window.setPosition(aDesktop.getConsideredPosition().getX(), aDesktop.getConsideredPosition().getY());
				else
					window.center();
			} else {
				if (location != null)
					window.setPosition(location.getX(), location.getY());
				else
					window.center();
			}
		}
		return window;
	}

	protected HandlerRegistration showedOnPanelBeforeHideReg = null;
	protected HandlerRegistration showedOnPanelHideReg = null;

	public void showOnPanel(String aElementId) {
		showOnPanel(RootPanel.get(aElementId));
	}
	
	public void showOnPanel(HasWidgets aPanel) {
		close(null, null);
		if (!isOpened()) {
			view.setVisible(true);
			aPanel.clear();
			addToContainer(view, aPanel);
			final Component insertedComponent = view;
			showedOnPanelBeforeHideReg = view.addBeforeHideHandler(new BeforeHideHandler() {

				@Override
				public void onBeforeHide(BeforeHideEvent event) {
					if (windowClosing != null) {
						try {

							Boolean res = Utils.executeScriptEventBoolean(module, windowClosing, JSEvents.publishWindowEvent(event, module));
							if (Boolean.FALSE.equals(res)) {
								event.setCancelled(true);
								return;
							}
						} catch (Exception ex) {
							Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
						}
					}
					assert showedOnPanelBeforeHideReg != null;
					showedOnPanelBeforeHideReg.removeHandler();
					showedOnPanelBeforeHideReg = null;
				}

			});
			showedOnPanelHideReg = view.addHideHandler(new HideHandler() {

				@Override
				public void onHide(HideEvent event) {
					assert showedOnPanelHideReg != null;
					showedOnPanelHideReg.removeHandler();
					showedOnPanelHideReg = null;
					if (windowClosed != null) {
						try {
							Utils.executeScriptEventVoid(module, windowClosed, JSEvents.publishWindowEvent(event, module));
						} catch (Exception ex) {
							Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
						}
					}
					insertedComponent.removeFromParent();
				}

			});
		}
	}

	protected void addToContainer(Component toAdd, HasWidgets aPanel) {
		if (aPanel instanceof PlatypusBorderLayoutContainer) {
			((PlatypusBorderLayoutContainer) aPanel).setCenterWidget(toAdd);
		} else if (aPanel instanceof PlatypusMarginLayoutContainer) {
			MarginConstraints mc = new MarginConstraints();
			mc.setTop(new Margin(0, Style.Unit.PX));
			mc.setBottom(new Margin(0, Style.Unit.PX));
			mc.setLeft(new Margin(0, Style.Unit.PX));
			mc.setRight(new Margin(0, Style.Unit.PX));
			((PlatypusMarginLayoutContainer) aPanel).add(toAdd, mc);
		} else if (aPanel instanceof PlatypusSplitContainer) {
			((PlatypusSplitContainer) aPanel).setLeftComponent(toAdd);
		} else if (aPanel instanceof RootPanel) {
			aPanel.add(toAdd);
			/*
			toAdd.getElement().getStyle().clearLeft();
			toAdd.getElement().getStyle().clearTop();
			toAdd.getElement().getStyle().clearRight();
			toAdd.getElement().getStyle().clearBottom();
			toAdd.getElement().getStyle().setWidth(100, Style.Unit.PCT);
			toAdd.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			*/
		} else {
			aPanel.add(toAdd);
		}
		if (windowOpened != null) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					try {
						if (windowOpened != null) {
							Utils.executeScriptEventVoid(module, windowOpened, JSEvents.publishWindowEvent(new ShowEvent(), module));
						}
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			});
		}
	}

	private void registerWindowListeners(final Window w) {
		w.addShowHandler(new ShowHandler() {

			@Override
			public void onShow(ShowEvent event) {
				showingForms.put(formKey, Form.this);
				if (windowOpened != null) {
					try {
						Utils.executeScriptEventVoid(module, windowOpened, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
				shownFormsChanged(module);
			}

		});
		w.addActivateHandler(new ActivateHandler<Window>() {

			@Override
			public void onActivate(ActivateEvent<Window> event) {
				if (windowActivated != null) {
					try {
						Utils.executeScriptEventVoid(module, windowActivated, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}

		});
		w.addDeactivateHandler(new DeactivateHandler<Window>() {

			@Override
			public void onDeactivate(DeactivateEvent<Window> event) {
				if (windowDeactivated != null) {
					try {
						Utils.executeScriptEventVoid(module, windowDeactivated, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}

		});
		w.addMinimizeHandler(new MinimizeHandler() {

			@Override
			public void onMinimize(MinimizeEvent event) {
				if (windowMinimized != null) {
					try {
						Utils.executeScriptEventVoid(module, windowMinimized, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}

		});
		w.addMaximizeHandler(new MaximizeHandler() {

			@Override
			public void onMaximize(MaximizeEvent event) {
				if (windowMaximized != null) {
					try {
						Utils.executeScriptEventVoid(module, windowMaximized, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}

		});
		w.addRestoreHandler(new RestoreHandler() {

			@Override
			public void onRestore(RestoreEvent event) {
				if (windowRestored != null) {
					try {
						Utils.executeScriptEventVoid(module, windowRestored, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}

		});
		w.addBeforeHideHandler(new BeforeHideHandler() {
			@Override
			public void onBeforeHide(BeforeHideEvent event) {
				if (windowClosing != null) {
					try {
						Boolean res = Utils.executeScriptEventBoolean(module, windowClosing, JSEvents.publishWindowEvent(event, module));
						if (Boolean.FALSE.equals(res))
							event.setCancelled(true);
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
			}
		});
		w.addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent event) {
				showingForms.remove(formKey);
				view.removeFromParent();
				window = null;
				w.removeFromParent();
				if (windowClosed != null) {
					try {
						Utils.executeScriptEventVoid(module, windowClosed, JSEvents.publishWindowEvent(event, module));
					} catch (Exception ex) {
						Logger.getLogger(Form.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
					}
				}
				shownFormsChanged(module);
			}
		});
	}

	protected boolean isOpened() {
		Component surface = view;
		return window != null || (surface != null && surface.getParent() != null);
	}

	public void close(Object aSelected, JavaScriptObject aCallback) {
		if (isOpened()) {
			if (window != null) {
				Window windowClosed = window;
				window.hide();// window became null
				if (windowClosed.isModal() && !windowClosed.isVisible() && aCallback != null)
					invokeDialogCallback(aCallback, Utils.toJs(aSelected));
			} else {
				if (view.getParent() != null) {
					view.hide();
				}
			}
		}
	}

	protected native static void invokeDialogCallback(JavaScriptObject aCallback, Object aSelectedValue)/*-{
		aCallback($wnd.boxAsJs(aSelectedValue));
	}-*/;

	public void publish(JavaScriptObject aModule) {
		module = aModule;
		publishFormFacade(module, this, (JavaScriptObject) view.getData(PUBLISHED_DATA_KEY));
		publishComponentsFacades(view);
	}

	private void publishComponentsFacades(HasWidgets aView) {
		java.util.Iterator<Widget> wIt = aView.iterator();
		while (wIt.hasNext()) {
			Widget w = wIt.next();
			if (w instanceof Component) {
				Component c = (Component) w;
				JavaScriptObject published = (JavaScriptObject) c.getData(PUBLISHED_DATA_KEY);
				inject(module, (String) c.getData(PID_DATA_KEY), published);
			}
			if (w instanceof HasWidgets)
				publishComponentsFacades((HasWidgets) w);
		}
	}

	public native static void inject(JavaScriptObject aModule, String aName, JavaScriptObject aValue)/*-{
		if (aModule != null && aName != null) {
			Object.defineProperty(aModule, aName, {
				get : function() {
					return aValue;
				}
			});
		}
	}-*/;

	protected native static void publishFormFacade(JavaScriptObject aModule, Form aForm, JavaScriptObject aView)/*-{
        aModule["x-Form"] = aForm;
        Object.defineProperty(aModule, "handled", {
        	configurable : true,
        	set : function(aValue){
        		if(aValue) {
        			aForm.@com.eas.client.form.Form::resolveHandlers()();
        			delete aModule.handled;
        		}
        	}
        });  
              
        Object.defineProperty(aModule, "view", {
	        get:function() {
	        	return aView;
	        } 
        });
        Object.defineProperty(aModule, "formKey", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getFormKey()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setFormKey(Ljava/lang/String;)(''+aValue);
	        } 
        });
        aModule.formKey = aModule.applicationElementId; 
        Object.defineProperty(aModule, "defaultCloseOperation", {
        	get : function(){
        		return aForm.@com.eas.client.form.Form::getDefaultCloseOperation()();
        	},
        	set : function(aValue){
        		if(aValue == null)
        			aValue = 0;
        		aForm.@com.eas.client.form.Form::setDefaultCloseOperation(I)(aValue * 1);
        	}
        });
        Object.defineProperty(aModule, "icon", {
        	get : function(){
        		return aForm.@com.eas.client.form.Form::getIcon()();
        	},
        	set : function(aValue){
				var setterCallback = function(){
        			aForm.@com.eas.client.form.Form::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
        	}
        });
        Object.defineProperty(aModule, "title", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getTitle()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setTitle(Ljava/lang/String;)(''+aValue);
	        } 
        });
        Object.defineProperty(aModule, "resizable", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isResizable()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setResizable(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "minimizable", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isMinimizable()();
	        },
	        set:function(aValue)
	        {
	        	aForm.@com.eas.client.form.Form::setMinimizable(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "minimized", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isMinimized()();
	        }
        });
        Object.defineProperty(aModule, "maximizable", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isMaximizable()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setMaximizable(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "maximized", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isMaximized()();
	        }
        });
        Object.defineProperty(aModule, "undecorated", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isUndecorated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setUndecorated(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "opacity", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getOpacity()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.client.form.Form::setOpacity(F)(aValue * 1);
	        } 
        });
        Object.defineProperty(aModule, "alwaysOnTop", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isAlwaysOnTop()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setAlwaysOnTop(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "locationByPlatform", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::isLocationByPlatform()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setLocationByPlatform(Z)((false != aValue));
	        } 
        });
        Object.defineProperty(aModule, "left", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getLeft()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.client.form.Form::setLeft(I)(aValue * 1);
	        } 
        });
        Object.defineProperty(aModule, "top", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getTop()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.client.form.Form::setTop(I)(aValue * 1);
	        } 
        });
        Object.defineProperty(aModule, "width", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWidth()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.client.form.Form::setWidth(I)(aValue * 1);
	        } 
        });
        Object.defineProperty(aModule, "height", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getHeight()();
	        },
	        set:function(aValue) {
	        	if(aValue == null)
	        		aValue = 0;
	        	aForm.@com.eas.client.form.Form::setHeight(I)(aValue * 1);
	        } 
        });
        Object.defineProperty(aModule, "onWindowOpened", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowOpened()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowOpened(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowClosing", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowClosing()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowClosing(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowClosed", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowClosed()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowClosed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowMinimized", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowMinimized()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowMinimized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowRestored", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowRestored()();
	        },
	        set:function(aValue)  {
	        	aForm.@com.eas.client.form.Form::setWindowRestored(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowMaximized", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowMaximized()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowMaximized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowActivated", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowActivated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowActivated(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        Object.defineProperty(aModule, "onWindowDeactivated", {
	        get:function() {
	        	return aForm.@com.eas.client.form.Form::getWindowDeactivated()();
	        },
	        set:function(aValue) {
	        	aForm.@com.eas.client.form.Form::setWindowDeactivated(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
	        } 
        });
        
        (function() {
	        var showedWnd = null;
	        var closeCallback = null;
	        aModule.show = function() {
		        closeCallback = null;
		        showedWnd = aForm.@com.eas.client.form.Form::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/client/gxtcontrols/wrappers/container/PlatypusDesktopContainer;)(false, null, null);
	        };
	        aModule.showModal = function(aCallback) {
		        closeCallback = aCallback;
		        showedWnd = aForm.@com.eas.client.form.Form::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/client/gxtcontrols/wrappers/container/PlatypusDesktopContainer;)(true, aCallback, null);
	        };
	        aModule.showOnPanel = function(aPanel) {
	        	if(aPanel.unwrap)
	        		showedWnd = aForm.@com.eas.client.form.Form::showOnPanel(Lcom/google/gwt/user/client/ui/HasWidgets;)(aPanel.unwrap());
	        	else
	        		showedWnd = aForm.@com.eas.client.form.Form::showOnPanel(Ljava/lang/String;)(aPanel);
	        };
	        aModule.showInternalFrame = function(aPanel) {
	        	showedWnd = aForm.@com.eas.client.form.Form::show(ZLcom/google/gwt/core/client/JavaScriptObject;Lcom/eas/client/gxtcontrols/wrappers/container/PlatypusDesktopContainer;)(false, null, aPanel != null?aPanel.unwrap():null);
	        };
	        aModule.minimize = function(){
	        	aForm.@com.eas.client.form.Form::minimize()();
	        };
	        aModule.maximize = function(){
	        	aForm.@com.eas.client.form.Form::maximize()();
	        };
	        aModule.toFront = function(){
	        	aForm.@com.eas.client.form.Form::toFront()();
	        };
	        aModule.restore = function(){
	        	aForm.@com.eas.client.form.Form::restore()();
	        };
	        aModule.close = function() {
		        if (arguments.length > 0)
		        	aForm.@com.eas.client.form.Form::close(Ljava/lang/Object;Lcom/google/gwt/core/client/JavaScriptObject;)(arguments[0]==null?null:$wnd.boxAsJava(arguments[0]), closeCallback);
		        else
		        	aForm.@com.eas.client.form.Form::close(Ljava/lang/Object;Lcom/google/gwt/core/client/JavaScriptObject;)(null, null);
	        };
	        aModule.submit = function(aAction, aCallback) {
	        	aForm.@com.eas.client.form.Form::submit(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aAction, aCallback);
	        }
        })();
    }-*/;

	public String getIconImage() {
		return iconImage;
	}

	public void setIconImage(String aValue) {
		iconImage = aValue;
	}

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
		alwaysOnTop = aValue;
		if (window != null) {
			if (alwaysOnTop)
				window.setZIndex(Integer.MAX_VALUE);
			else
				window.setZIndex(0);
		}
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean aValue) {
		resizable = aValue;
		if (window != null)
			window.setResizable(resizable);
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public void setMinimizable(boolean aValue) {
		minimizable = aValue;
		if (window != null && !window.isModal())
			window.setMinimizable(minimizable);
	}

	public boolean isMinimized() {
		return window != null ? window.isCollapsed() : false;
	}

	public void minimize(){
		if(window != null)
			window.minimize();
	}
	
	public void maximize(){
		if(window != null)
			window.maximize();
	}
	
	public void toFront(){
		if(window != null)
			window.toFront();
	}
	
	public void restore(){
		if(window != null)
			window.restore();
	}
	
	public boolean isMaximizable() {
		return maximizable;
	}

	public void setMaximizable(boolean aValue) {
		maximizable = aValue;
		if (window != null)
			window.setMaximizable(maximizable);
	}

	public boolean isMaximized() {
		return window != null ? window.isMaximized() : false;
	}

	public boolean isUndecorated() {
		return undecorated;
	}

	public void setUndecorated(boolean aValue) {
		undecorated = aValue;
		if (window != null) {
			window.setHeaderVisible(!undecorated);
			window.setShadow(!undecorated);
			window.setBorders(!undecorated);
			window.setClosable(!undecorated);
			window.setMaximizable(!undecorated);
		}
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float aValue) {
		opacity = aValue;
		if (view != null)
			view.getElement().getStyle().setOpacity(aValue);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String aValue) {
		title = aValue;
		if (window != null)
			window.setHeadingText(title);
	}

	public int getLeft() {
		return location != null ? location.getX() : 0;
	}

	public void setLeft(int aValue) {
		locationByPlatform = false;
		if (window != null)
			window.setPosition(aValue, getTop());
		else {
			if (location == null)
				location = new Point(0, 0);
			location.setX(aValue);
		}
	}

	public int getTop() {
		return location != null ? location.getY() : 0;
	}

	public void setTop(int aValue) {
		locationByPlatform = false;
		if (window != null)
			window.setPosition(getLeft(), aValue);
		else {
			if (location == null)
				location = new Point(0, 0);
			location.setY(aValue);
		}
	}

	public int getWidth() {
		return size != null ? size.getX() : 0;
	}

	public void setWidth(int aValue) {
		if (window != null)
			window.setPixelSize(aValue, getHeight());
		else {
			if (size == null)
				size = new Point(0, 0);
			size.setX(aValue);
		}
	}

	public int getHeight() {
		return size != null ? size.getY() : 0;
	}

	public void setHeight(int aValue) {
		if (window != null)
			window.setPixelSize(getWidth(), aValue);
		else {
			if (size == null)
				size = new Point(0, 0);
			size.setY(aValue);
		}
	}

	public ImageResource getIcon() {
		return icon;
	}

	public void setIcon(ImageResource aIcon) {
		icon = aIcon;
		if (window != null)
			window.getHeader().setIcon(icon);
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

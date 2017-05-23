package com.eas.widgets;

import com.eas.ui.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class JsWidgets {
	
	private static JavaScriptObject TabbedPane;
	private static JavaScriptObject GridPane;
	private static JavaScriptObject CardPane;
	private static JavaScriptObject BoxPane;
	private static JavaScriptObject BorderPane;
	private static JavaScriptObject ToolBar;
	private static JavaScriptObject AnchorsPane;
	private static JavaScriptObject AbsolutePane;
	private static JavaScriptObject FlowPane;
	private static JavaScriptObject ScrollPane;
	private static JavaScriptObject SplitPane;
	
	public native static void publishComponentProperties(PublishedComponent aPublished)/*-{
                function isAttached(element){
                    while(element && element !== $doc.body)
                        element = element.parentElement;
                    return !!element;
                }
                
		var TabbedPane = @com.eas.widgets.JsWidgets::TabbedPane;
		var GridPane = @com.eas.widgets.JsWidgets::GridPane;
		var CardPane = @com.eas.widgets.JsWidgets::CardPane;
		var BoxPane = @com.eas.widgets.JsWidgets::BoxPane;
		var ToolBar = @com.eas.widgets.JsWidgets::ToolBar;
		var AnchorsPane = @com.eas.widgets.JsWidgets::AnchorsPane;
		var AbsolutePane = @com.eas.widgets.JsWidgets::AbsolutePane;
		var FlowPane = @com.eas.widgets.JsWidgets::FlowPane;
		var ScrollPane = @com.eas.widgets.JsWidgets::ScrollPane;
		var BorderPane = @com.eas.widgets.JsWidgets::BorderPane;
		var SplitPane = @com.eas.widgets.JsWidgets::SplitPane;
		var Orientation = @com.eas.ui.JsUi::Orientation; 
		var VerticalPosition = @com.eas.ui.JsUi::VerticalPosition; 
		var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition; 
		var FontStyle = @com.eas.ui.JsUi::FontStyle; 
		var ScrollBarPolicy = @com.eas.ui.JsUi::ScrollBarPolicy; 
		
		var comp = aPublished.unwrap();
		var _foreground = null;
		var _background = null;
		var _font = null;
		var _cursor = null;
		var _opaque = true;
	
	    Object.defineProperty(aPublished, "visible", {
		    get : function() {
			    return comp.@com.google.gwt.user.client.ui.UIObject::isVisible()();
		    },
		    set : function(aValue) {
			    comp.@com.google.gwt.user.client.ui.UIObject::setVisible(Z)(aValue);
		    	if(aPublished.parent instanceof BoxPane)
	    			aPublished.parent.unwrap().@com.eas.widgets.containers.BoxPanel::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof ToolBar)
	    			aPublished.parent.unwrap().@com.eas.widgets.containers.Toolbar::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof AnchorsPane)
	    			aPublished.parent.unwrap().@com.eas.widgets.containers.AnchorsPanel::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    }
		    });
	    Object.defineProperty(aPublished, "enabled", {
		    get : function() {
			    return comp.@com.google.gwt.user.client.ui.HasEnabled::isEnabled()();
		    },
		    set : function(aValue) {
		    	comp.@com.google.gwt.user.client.ui.HasEnabled::setEnabled(Z)(!!aValue);
		    }
		    });
	    Object.defineProperty(aPublished, "toolTipText", {
		    get : function() {
			    return comp.@com.google.gwt.user.client.ui.UIObject::getTitle()();
		    },
		    set : function(aValue) {
			    comp.@com.google.gwt.user.client.ui.UIObject::setTitle(Ljava/lang/String;)(aValue != null ? aValue + "" : null);
		    }
		    });
	    Object.defineProperty(aPublished, "background", {
		    get : function() {
		    	return _background;
		    },
		    set : function(aValue) {
		    	_background = aValue;
		    	//apply
		    	@com.eas.widgets.WidgetsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : ""); 
		    }
		    });
	    Object.defineProperty(aPublished, "backgroundSet", {get : function(){return _background != null;}});
	    Object.defineProperty(aPublished, "foreground", {
		    get : function() {
		    	return _foreground;
		    },
		    set : function(aValue) {
		    	_foreground = aValue;
		    	// apply
		    	@com.eas.widgets.WidgetsUtils::applyForeground(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/ui/PublishedColor;)(comp, _foreground); 
		    }
		    });
	    Object.defineProperty(aPublished, "foregroundSet", {get : function(){return _foreground != null;}});
	    Object.defineProperty(aPublished, "opaque", {
		    get : function() {
		    	return _opaque;
		    },
		    set : function(aValue) {
		    	_opaque = !!aValue;
		    	// apply
	    		@com.eas.widgets.WidgetsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : "");
		    }
		    });
	    Object.defineProperty(aPublished, "font", {
		    get : function() {
		    	return _font;
		    },
		    set : function(aValue) {
		    	_font = aValue;
		    	// apply
		    	@com.eas.widgets.WidgetsUtils::applyFont(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/ui/PublishedFont;)(comp, _font); 
		    }
		    });
	    Object.defineProperty(aPublished, "fontSet", { get : function(){return _font != null;}});
            Object.defineProperty(aPublished, "cursor", {
                get : function(){
                        return _cursor;
                },
                set : function(aValue){
                        _cursor = aValue; 
                        // apply	
                @com.eas.widgets.WidgetsUtils::applyCursor(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _cursor); 
                }

            });
	    Object.defineProperty(aPublished, "cursorSet", { get : function(){return _cursor != null;}});
	    Object.defineProperty(aPublished, "left", {
		    get : function() {
                        if (isAttached(aPublished.element)) {
                                if(aPublished.parent){
                                        return aPublished.parent.unwrap().@com.eas.widgets.HasChildrenPosition::getLeft(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
                                } else {
                                        return aPublished.element.offsetLeft;
                                }
                        } else {
                                return parseFloat(aPublished.element.style.left);
                        }
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof AbsolutePane || aPublished.parent instanceof AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.widgets.MarginsPane::ajustLeft(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
            });
	    Object.defineProperty(aPublished, "top", {
		    get : function() {
                        if (isAttached(aPublished.element)) {
                                if(aPublished.parent){
                                        return aPublished.parent.unwrap().@com.eas.widgets.HasChildrenPosition::getTop(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
                                } else {
                                        return aPublished.element.offsetTop;
                                }
                        } else {
                                return parseFloat(aPublished.element.style.top);
                        }
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof AbsolutePane || aPublished.parent instanceof AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.widgets.MarginsPane::ajustTop(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
            });
	    Object.defineProperty(aPublished, "width", {
		    get : function() {
                        if (isAttached(aPublished.element))
                            return aPublished.element.offsetWidth;
                        else {
                            return parseFloat(aPublished.element.style.width);
                        }
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof AbsolutePane || aPublished.parent instanceof AnchorsPane){
				if(aValue != null)
		    			aPublished.parent.unwrap().@com.eas.widgets.MarginsPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);		    		
		    	}else if(aPublished.parent instanceof BorderPane){
				if(aValue != null)
	    				aPublished.parent.unwrap().@com.eas.widgets.containers.BorderPanel::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	}else if(aPublished.parent instanceof ScrollPane){
				if(aValue != null)
                                        aPublished.parent.unwrap().@com.eas.widgets.containers.ScrollBoxPanel::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    		else
    					aPublished.element.style.width = null;
		    	}else if(aPublished.parent instanceof CardPane
		    	      || aPublished.parent instanceof TabbedPane
		    	      || aPublished.parent instanceof GridPane
		    	      || aPublished.parent instanceof SplitPane){
		    		// no op
		    	}else if(aPublished.parent instanceof BoxPane && aPublished.parent.orientation != Orientation.HORIZONTAL){
		    		// no op
		    	}else{
				if(aValue != null)
		    			aPublished.element.style.width = aValue + 'px';
		    		else
    					aPublished.element.style.width = null;
		    	}
		    }
		});
	    Object.defineProperty(aPublished, "height", {
		    get : function() {
                        if (isAttached(aPublished.element))
                            return aPublished.element.offsetHeight;
                        else {
                            return parseFloat(aPublished.element.style.height);
                        }
		    },
		    set : function(aValue) {
    			if(aPublished.parent instanceof AbsolutePane || aPublished.parent instanceof AnchorsPane){
				if(aValue != null)
		    			aPublished.parent.unwrap().@com.eas.widgets.MarginsPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	}else if(aPublished.parent instanceof BorderPane){
				if(aValue != null)
	    				aPublished.parent.unwrap().@com.eas.widgets.containers.BorderPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	}else if(aPublished.parent instanceof ScrollPane){
				if(aValue != null)
                                        aPublished.parent.unwrap().@com.eas.widgets.containers.ScrollBoxPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
	    			else
                                        aPublished.element.style.height = null;
		    	}else if(aPublished.parent instanceof CardPane
		    	      || aPublished.parent instanceof TabbedPane
		    	      || aPublished.parent instanceof GridPane
		    	      || aPublished.parent instanceof SplitPane){
		    		// no op
		    	}else if(aPublished.parent instanceof ToolBar){
		    		// no op
		    	}else if(aPublished.parent instanceof BoxPane && aPublished.parent.orientation != Orientation.VERTICAL){
		    		// no op
		    	}else{
				if(aValue != null)
	    				aPublished.element.style.height = aValue + 'px';
	    			else
                                        aPublished.element.style.height = null;
		    	}
		    }
		});
	    Object.defineProperty(aPublished, "componentPopupMenu", {
	    	get : function() {
	    		var menu = comp.@com.eas.menu.HasComponentPopupMenu::getPlatypusPopupMenu()();
			    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(menu);
	    	},
		    set : function(aValue) {
		    	if (aValue && aValue.unwrap) {
			    	comp.@com.eas.menu.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/menu/PlatypusPopupMenu;)(aValue.unwrap());
		    	} else {
			    	comp.@com.eas.menu.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/menu/PlatypusPopupMenu;)(null);
		    	}
		    }
		});
	    Object.defineProperty(aPublished, "parent", {
		    get : function() {
		    	return @com.eas.widgets.WidgetsUtils::lookupPublishedParent(Lcom/google/gwt/user/client/ui/UIObject;)(comp);
		    }
		});
	    Object.defineProperty(aPublished, "name", {
		    get : function() {
		    	return comp.@com.eas.ui.HasJsName::getJsName()();
		    },
		    set : function(aValue){
		    	comp.@com.eas.ui.HasJsName::setJsName(Ljava/lang/String;)("" + aValue);
		    }
		});
	    Object.defineProperty(aPublished, "focus", {
		    get : function() {
		    	return function(){
		    		@com.eas.widgets.WidgetsUtils::focus(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    	}
		    }
		});
	    // Events
	    @com.eas.widgets.JsWidgets::publishExecutor(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
	    // Native API
	    Object.defineProperty(aPublished, "element", {
	    	get : function() {
				return comp.@com.google.gwt.user.client.ui.UIObject::getElement()();
	    	}
	    });
	    Object.defineProperty(aPublished, "component", {
	    	get : function() {
				return null;
	    	}
	    });
	    Object.defineProperty(aPublished, "layout", {
	    	value : function() {
				@com.eas.widgets.WidgetsUtils::callOnResize(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
	    	}
	    });
	    Object.defineProperty(aPublished, "showOn", {
	    	value : function(aElement) {
	    		if(typeof aElement == "string")
	    			aElement = $doc.getElementById(aElement);
	    		if(aElement){
					@com.eas.widgets.WidgetsUtils::addWidgetTo(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/dom/client/Element;)(aPublished.unwrap(), aElement);
	    		}
	    	}
	    });
	    comp.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
	}-*/;
	
	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.eas.ui.HasEventsExecutor::getEventsExecutor()();
			if(executor == null){
				executor = @com.eas.ui.events.EventsExecutor::new(Lcom/google/gwt/user/client/ui/UIObject;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
				comp.@com.eas.ui.HasEventsExecutor::setEventsExecutor(Lcom/eas/ui/events/EventsExecutor;)(executor);
			} else {
				executor.@com.eas.ui.events.EventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
	
			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onItemSelected", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getItemSelected()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setItemSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onValueChange", {
				get : function() {
					return executor.@com.eas.ui.events.EventsExecutor::getValueChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.ui.events.EventsExecutor::setValueChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}
	}-*/;

	public native static void init()/*-{		

		function publishComponentProperties(aPublished) {
			@com.eas.widgets.JsWidgets::publishComponentProperties(Lcom/eas/ui/PublishedComponent;)(aPublished);
		}
		
		function publishChildrenOrdering(aPublished) {
			if (aPublished) {
				var comp = aPublished.unwrap();
				aPublished.toFront = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.widgets.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.widgets.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
		
				aPublished.toBack = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.widgets.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.widgets.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
			}
		}
			
		function publishIndexedPanel(aPublished) {
			var comp = aPublished.unwrap();
			Object.defineProperty(aPublished, "count", {
				get : function() {
					return comp.@com.google.gwt.user.client.ui.IndexedPanel::getWidgetCount()();
				}
			});
			aPublished.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
					var index = comp.@com.google.gwt.user.client.ui.IndexedPanel::getWidgetIndex(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					if(index != -1)
						comp.@com.google.gwt.user.client.ui.IndexedPanel::remove(I)(index);
				}
			};
			aPublished.clear = function() {
				for(var i = aPublished.count - 1; i >= 0; i--)
					comp.@com.google.gwt.user.client.ui.IndexedPanel::remove(I)(i);
			};
			if(!aPublished.child){
			    aPublished.child = function(aIndex) {
				    var widget = comp.@com.google.gwt.user.client.ui.IndexedPanel::getWidget(I)(aIndex);
				    return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
			    };
			}
			publishChildren(aPublished);
		}

		function publishChildren(aPublished){
			Object.defineProperty(aPublished, "children", {
				value : function(){
					var ch = [];
					for(var i=0; i < aPublished.count; i++)
						ch.push(aPublished.child(i));
					return ch;
				}
			});
		}	
			
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'forms/label', function(){
			function Label(aText, aIcon, aIconTextGap) {			
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof Label)) {
					throw  ' use  "new Label()" !';
				}
	
				var published = this;
				 
				aComponent = aComponent || @com.eas.widgets.PlatypusLabel::new()(); 	
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
				if (aIcon) {
					published.icon = aIcon;
				}
				if(aIconTextGap){
					published.iconTextGap = aIconTextGap;
				}
			}		
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('Label', Label);
			return Label;
		});
		
		predefine([], 'forms/button', function(){
			function Button(aText, aIcon, aGapOrCallback, aCallback) {
				var aIconTextGap = 4;
				if(!aCallback && aGapOrCallback && aGapOrCallback.call)
					aCallback = aGapOrCallback;
				
				var aComponent = arguments.length > 4 ? arguments[4] : null;
				
				if (!(this instanceof Button)) {
					throw  ' use  "new Button()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusButton::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				
				if (aText) {
					published.text = aText;
				} 	
				if (aIcon) {
					published.icon = aIcon;
				}
				if (aCallback) {
					published.onActionPerformed = aCallback; 
				}	 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('Button', Button);
			return Button;
		});	
		
		predefine([], 'forms/drop-down-button', function(){
			function DropDownButton(aText, aIcon, aGapOrCallback, aCallback) {			
				if (!(this instanceof DropDownButton)) {
					throw  ' use  "new DropDownButton()" !';
				}
				var aIconTextGap = 4;
				if(!aCallback && aGapOrCallback && aGapOrCallback.call)
					aCallback = aGapOrCallback;
				var aComponent = arguments.length > 4 ? arguments[4] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusSplitButton::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				
				if (aText) {
					published.text = aText;
				} 	
				if (aIcon) {
					published.icon = aIcon;
				}
				if (aCallback) {
					published.onActionPerformed = aCallback; 
				}	 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('DropDownButton', DropDownButton);
			return DropDownButton;	
		});
		
		predefine([], 'forms/toggle-button', function(){
			function ToggleButton(aText, aIcon, aSelected, aGapOrCallback, aCallback) {
				
				var aIconTextGap = 4;
				if(!aCallback && aGapOrCallback && aGapOrCallback.call)
					aCallback = aGapOrCallback;
	
				var aComponent = arguments.length > 5 ? arguments[5] : null;
				
				if (!(this instanceof ToggleButton)) {
					throw  ' use  "new ToggleButton()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusToggleButton::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if (aText) {
					published.text = aText;
				} 	
				if (aIcon) {
					published.icon = aIcon;
				}
				if (aSelected != undefined) {
					published.selected = aSelected;
				}
				if (aCallback) {
					published.onActionPerformed = aCallback; 
				}	 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ToggleButton', ToggleButton);
			return ToggleButton;
		});	
		
		predefine([], 'forms/radio-button', function(){
			function RadioButton(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof RadioButton)) {
					throw  ' use  "new RadioButton()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusRadioButton::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if (aText) {
					published.text = aText;
				} 	
				if (aSelected) {
					published.selected = aSelected;
				}
				if(aCallback){
					published.onActionPerformed = aCallback; 
				}
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('RadioButton', RadioButton);
			return RadioButton;	
		});	
		
		predefine([], 'forms/check-box', function(){
			function CheckBox(aText, aSelected, aCallback) {
				var aComponent = arguments.length>3?arguments[3]:null;
				
				if (!(this instanceof CheckBox)) {
					throw  ' use  "new CheckBox()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusCheckBox::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if (aText) {
					published.text = aText;
				} 	
				if (aSelected != undefined) {
					published.selected = aSelected;
				}
				if(aCallback){
					published.onActionPerformed = aCallback; 
				}
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('CheckBox', CheckBox);
			return CheckBox;
		});			
		
		predefine([], 'forms/password-field', function(){
			function PasswordField(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof PasswordField)) {
					throw  ' use  "new PasswordField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusPasswordField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);			
				if (aText) {
					published.text = aText;
				} 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('PasswordField', PasswordField);
			return PasswordField;
		});	
		
		predefine([], 'forms/text-field', function(){
			function TextField(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof TextField)) {
					throw  ' use  "new TextField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusTextField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('TextField', TextField);
			return TextField;
		});	

		predefine([], 'forms/formatted-field', function(){
			function FormattedField(aValue) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof FormattedField)) {
					throw  ' use  "new FormattedField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusFormattedTextField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aValue) {
					published.value = aValue;
				} 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('FormattedField', FormattedField);
			return FormattedField;
		});
		
		predefine([], 'forms/text-area', function(){
			function TextArea(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof TextArea)) {
					throw  ' use  "new TextArea()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusTextArea::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('TextArea', TextArea);
			return TextArea;
		});
		
		predefine([], 'forms/html-area', function(){
			function HtmlArea(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof HtmlArea)) {
					throw  ' use  "new HtmlArea()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusHtmlEditor::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('HtmlArea', HtmlArea);
			return HtmlArea;
		});
		
		predefine([], 'forms/slider', function(){
			var Orientation = @com.eas.ui.JsUi::Orientation; 
			function Slider() {
				var aOrientation = arguments.length == 1 || arguments.length == 4 ? arguments[0] : Orientation.HORIZONTAL;
				var aMinimum = null;
				if(arguments.length == 3)
					aMinimum = arguments[0];
				else if(arguments.length == 4)
					aMinimum = arguments[1];
				var aMaximum = null;
				if(arguments.length == 3)
					aMaximum = arguments[1];
				else if(arguments.length == 4)
					aMaximum = arguments[2];
				var aValue = null;
				if(arguments.length == 3)
					aValue = arguments[2];
				else if(arguments.length == 4)
					aValue = arguments[3];
				var aComponent = arguments.length > 4 ? arguments[4] : null;
				if (!(this instanceof Slider)) {
					throw  ' use  "new Slider()" !';
				}
				if (!aMinimum) {
					aMinimum = 0;
				} 	
				if (!aMaximum) {
					aMaximum = 100;
				} 	
				if (!aValue) {
					aValue = aMinimum;
				} 	
				
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusSlider::new(DD)(aMinimum, aMaximum);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aMinimum) {
					published.minimum = aMinimum;
				} 	
				if (aMaximum) {
					published.maximum = aMaximum;
				} 	
				if (aValue) {
					published.value = aValue;
				} 	
				return published;
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('Slider', Slider);
			return Slider;
		});
		
		predefine([], 'forms/progress-bar', function(){
			function ProgressBar(aMinimum, aMaximum) {
				var aComponent = arguments.length > 2 ? arguments[2] : null;			
				if (!(this instanceof ProgressBar)) {
					throw  ' use  "new ProgressBar()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.PlatypusProgressBar::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aMinimum) {
					published.minimum = aMinimum; 
				} 	
				if (aMaximum) {
					published.maximum = aMaximum; 
				} 	
				return published;
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ProgressBar', ProgressBar);
			return ProgressBar;
		});

		predefine([], 'forms/desktop-pane', function(){
			function DesktopPane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof DesktopPane)) {
					throw  ' use  "new DesktopPane()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.widgets.DesktopPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('DesktopPane', DesktopPane);
			return DesktopPane;
		});

		predefine([], 'forms/border-pane', function(){
			function BorderPane(aVGap, aHGap) {
				var aComponent = arguments.length > 2 ? arguments[2] : null;
				if(!aComponent){
					if(!aVGap)
						aVGap = 0;
					if(!aHGap)
						aHGap = 0;
				}
				if (!(this instanceof BorderPane)) {
					throw  ' use  "new BorderPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.BorderPane::new(II)(aVGap, aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			};
			@com.eas.widgets.JsWidgets::BorderPane = BorderPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('BorderPane', BorderPane);
			return BorderPane;
		});

		predefine([], 'forms/flow-pane', function(){
			function FlowPane(aVGap, aHGap) {
				var aComponent = arguments.length > 2 ? arguments[2] : null;
				if(!aComponent)
				{
					if(!aVGap)
						aVGap = 0;
					if(!aHGap)
						aHGap = 0;
				}
				if (!(this instanceof FlowPane)) {
					throw  ' use  "new FlowPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.FlowPane::new(II)(aVGap,aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			@com.eas.widgets.JsWidgets::FlowPane = FlowPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('FlowPane', FlowPane);
			return FlowPane;
		});
		
		predefine([], 'forms/grid-pane', function(){
			function GridPane(aRows, aCols, aVGap, aHGap) {
				if (!(this instanceof GridPane)) {
					throw  ' use  "new GridPane()" !';
				}
				var published = this;
				var aComponent;
				if(arguments.length > 4) {
					aComponent = arguments[4];
				} else {
					if (aRows == undefined) {
						throw "aRows argument is required!"
					}
					if (aCols == undefined) {
						throw "aCols argument is required!"
					}
					aVGap = aVGap || 0;
					aHGap = aHGap || 0;
					aComponent = @com.eas.widgets.GridPane::new(IIII)(+aRows, +aCols, +aVGap, +aHGap);
			    }
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
			}
			@com.eas.widgets.JsWidgets::GridPane = GridPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('GridPane', GridPane);
			return GridPane;
		});
		
		predefine([], 'forms/box-pane', function(){
			var Orientation = @com.eas.ui.JsUi::Orientation; 
			function BoxPane(aOrientation, aHGap, aVGap) {
				if (!(this instanceof BoxPane)) {
					throw  ' use  "new BoxPane()" !';
				}
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				if(!aComponent){
					if(arguments.length < 3)
						aVGap = 0;
					if(arguments.length < 2)
						aHGap = 0;
					if(arguments.length < 1)
						aOrientation = Orientation.HORIZONTAL;
					aComponent = @com.eas.widgets.BoxPane::new(III)(aOrientation, aHGap, aVGap);
				}
	
				var published = this;
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				
				published.add = function(toAdd){
					if(toAdd && toAdd.unwrap){
						if(toAdd.parent == published)
							throw 'A widget already added to this container';
						aComponent.@com.eas.widgets.BoxPane::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					}
				}
				
				Object.defineProperty(published, "orientation", {
					get : function(){
						return aComponent.@com.eas.widgets.containers.BoxPanel::getOrientation()();
					}
				});
			}
			@com.eas.widgets.JsWidgets::BoxPane = BoxPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('BoxPane', BoxPane);
			return BoxPane;
		});		

		predefine([], 'forms/card-pane', function(){
			function CardPane(aVGap, aHGap) {
				if (!(this instanceof CardPane)) {
					throw  ' use  "new CardPane()" !';
				}
				var aComponent = arguments.length > 2 ? arguments[2] : null;
				if(!aComponent)
				{
					if(!aVGap)
						aVGap = 0;
					if(!aHGap)
						aHGap = 0;
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.CardPane::new(II)(aVGap, aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			@com.eas.widgets.JsWidgets::CardPane = CardPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('CardPane', CardPane);
			return CardPane;
		});		

		predefine([], 'forms/tabbed-pane', function(){
			function TabbedPane() {
				var aComponent = arguments.length>0?arguments[0]:null;
				if (!(this instanceof TabbedPane)) {
					throw  ' use  "new TabbedPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.TabbedPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			@com.eas.widgets.JsWidgets::TabbedPane = TabbedPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('TabbedPane', TabbedPane);
			return TabbedPane;
		});

		predefine([], 'forms/scroll-pane', function(){
			function ScrollPane(aChild) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof ScrollPane)) {
					throw  ' use  "new ScrollPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.ScrollPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				if(aChild)
					published.add(aChild);
			}
			@com.eas.widgets.JsWidgets::ScrollPane = ScrollPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ScrollPane', ScrollPane);
			return ScrollPane;
		});		
		
		predefine([], 'forms/split-pane', function(){
			var Orientation = @com.eas.ui.JsUi::Orientation; 
			function SplitPane(aOrientation) {
				if (!(this instanceof SplitPane)) {
					throw  ' use  "new SplitPane()" !';
				}
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if(!aComponent)
				{
					if(!aOrientation)
						aOrientation = Orientation.HORIZONTAL;
					aComponent = @com.eas.widgets.SplitPane::new()();
					var orientation = (aOrientation === Orientation.VERTICAL ? @com.eas.widgets.SplitPane::VERTICAL_SPLIT : @com.eas.widgets.SplitPane::HORIZONTAL_SPLIT); 
					aComponent.@com.eas.widgets.SplitPane::setOrientation(I)(orientation);
				}
	
				var published = this; 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			@com.eas.widgets.JsWidgets::SplitPane = SplitPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('SplitPane', SplitPane);
			return SplitPane;
		});		
		
		predefine([], 'forms/tool-bar', function(){
			function ToolBar(floatable) {
				if (!(this instanceof ToolBar)) {
					throw  ' use  "new ToolBar()" !';
				}
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if(!aComponent)
				{
					if(floatable == undefined || floatable == null)
						floatable = false;
					aComponent = @com.eas.widgets.ToolBar::new()();
				}
	
				var published = this;
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			@com.eas.widgets.JsWidgets::ToolBar = ToolBar;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ToolBar', ToolBar);
			return ToolBar;
		});
		
		predefine([], 'forms/anchors-pane', function(){
			function AnchorsPane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof AnchorsPane)) {
					throw  ' use  "new AnchorsPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.AnchorsPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				publishChildrenOrdering(published);
			}
			@com.eas.widgets.JsWidgets::AnchorsPane = AnchorsPane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('AnchorsPane', AnchorsPane);
			return AnchorsPane;
		});

		predefine([], 'forms/absolute-pane', function(){
			function AbsolutePane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof AbsolutePane)) {
					throw  ' use  "new AbsolutePane()" !';
				}
				var published = this; 
				aComponent = aComponent || @com.eas.widgets.AbsolutePane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				publishChildrenOrdering(published);
			}
			@com.eas.widgets.JsWidgets::AbsolutePane = AbsolutePane;
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('AbsolutePane', AbsolutePane);
			return AbsolutePane;
		});
		
		predefine([], 'forms/button-group', function(){
			function ButtonGroup() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ButtonGroup)) {
					throw  ' use  "new ButtonGroup()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.ui.ButtonGroup::new()();
				published.unwrap = function() {
					return aComponent;
				};			
				aComponent.@com.eas.ui.ButtonGroup::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			@com.eas.widgets.WidgetsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ButtonGroup', ButtonGroup);
			return ButtonGroup;
		});
		
		predefine([], 'forms/form', function(Form){
			function Form(aView, aFormKey){
				var aComponent = arguments.length > 2 ? arguments[2] : null;
	
				var published = this;
				if(!aComponent){ 
					if(aView)
						aComponent = @com.eas.form.PlatypusWindow::new(Lcom/google/gwt/user/client/ui/Widget;)(aView.unwrap());
					else
						aComponent = @com.eas.form.PlatypusWindow::new()();
				} 	
				published.unwrap = function() {
					return aComponent;
				};
				if(aFormKey){
					aComponent.@com.eas.form.PlatypusWindow::setFormKey(Ljava/lang/String;)(aFormKey);
				}
				aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Form.getShownForm = function(aFormKey){
				return @com.eas.form.PlatypusWindow::getShownForm(Ljava/lang/String;)(aFormKey);
			};
			
			Object.defineProperty(Form, "shown", {
				get : function() {
					return @com.eas.form.PlatypusWindow::getShownForms()();
				}
			});
			Object.defineProperty(Form, "onChange", {
				get : function() {
					return @com.eas.form.PlatypusWindow::getOnChange()();
				},
				set : function(aValue) {
					@com.eas.form.PlatypusWindow::setOnChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				}
			});
			return Form;
		});				
	}-*/;
}

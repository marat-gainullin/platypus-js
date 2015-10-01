package com.eas.form;

import com.eas.form.published.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class JsUi {
	
	public static JavaScriptObject ui;
	
	public native static void publishComponentProperties(PublishedComponent aPublished)/*-{
		var Ui = @com.eas.form.JsUi::ui;
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
		    	if(aPublished.parent instanceof Ui.BoxPane)
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof Ui.ToolBar)
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.Toolbar::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof Ui.AnchorsPane)
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.AnchorsPanel::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
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
		    	@com.eas.form.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : ""); 
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
		    	@com.eas.form.ControlsUtils::applyForeground(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/form/published/PublishedColor;)(comp, _foreground); 
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
	    		@com.eas.form.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : "");
		    }
		    });
	    Object.defineProperty(aPublished, "font", {
		    get : function() {
		    	return _font;
		    },
		    set : function(aValue) {
		    	_font = aValue;
		    	// apply
		    	@com.eas.form.ControlsUtils::applyFont(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/form/published/PublishedFont;)(comp, _font); 
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
		    	@com.eas.form.ControlsUtils::applyCursor(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _cursor); 
		    	}
		    	
		    });
	    Object.defineProperty(aPublished, "cursorSet", { get : function(){return _cursor != null;}});
	    var _left = null;
	    Object.defineProperty(aPublished, "left", {
		    get : function() {
		    	if(aPublished.parent){
					_left = aPublished.parent.unwrap().@com.eas.form.published.containers.HasChildrenPosition::getLeft(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	}
				return _left;
		    },
		    set : function(aValue) {
		    	_left = aValue;
		    	if(aPublished.parent instanceof Ui.AbsolutePane || aPublished.parent instanceof Ui.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.form.published.containers.MarginsPane::ajustLeft(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
		    });
		    var _top = null;
	    Object.defineProperty(aPublished, "top", {
		    get : function() {
		    	if(aPublished.parent){
					_top = aPublished.parent.unwrap().@com.eas.form.published.containers.HasChildrenPosition::getTop(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	}
				return _top;
		    },
		    set : function(aValue) {
		    	_top = aValue;
		    	if(aPublished.parent instanceof Ui.AbsolutePane || aPublished.parent instanceof Ui.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.form.published.containers.MarginsPane::ajustTop(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
		    });
		    var _width = null;
	    Object.defineProperty(aPublished, "width", {
		    get : function() {
		    	if(_width == null)
		    		_width = aPublished.element.offsetWidth;
		    	return _width;
		    },
		    set : function(aValue) {
		    	_width = aValue;
		    	if(aPublished.parent instanceof Ui.AbsolutePane || aPublished.parent instanceof Ui.AnchorsPane){
		    		aPublished.parent.unwrap().@com.eas.form.published.containers.MarginsPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);		    		
		    	}else if(aPublished.parent instanceof Ui.ScrollPane){
		    		@com.eas.form.published.containers.ScrollPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof Ui.FlowPane){
		    		@com.eas.form.published.containers.FlowPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof Ui.BoxPane && aPublished.parent.orientation == Ui.Orientation.HORIZONTAL){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof Ui.ToolBar){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.Toolbar::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof Ui.BorderPane){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BorderPanel::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else{
		    		aPublished.element.style.width = aValue + 'px';
		    	}
		    }
		    });
		    var _height = null;
	    Object.defineProperty(aPublished, "height", {
		    get : function() {
		    	if(_height == null)
		    		_height = aPublished.element.offsetHeight;
		    	return _height;
		    },
		    set : function(aValue) {
		    	_height = aValue;
		    	if(aPublished.parent instanceof Ui.AbsolutePane || aPublished.parent instanceof Ui.AnchorsPane){
		    		aPublished.parent.unwrap().@com.eas.form.published.containers.MarginsPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else if(aPublished.parent instanceof Ui.ScrollPane){
		    		@com.eas.form.published.containers.ScrollPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
	    		}else if(aPublished.parent instanceof Ui.FlowPane){
		    		@com.eas.form.published.containers.FlowPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
				}else if(aPublished.parent instanceof Ui.BoxPane && aPublished.parent.orientation == Ui.Orientation.VERTICAL){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else if(aPublished.parent instanceof Ui.BorderPane){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BorderPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else{
		    		aPublished.element.style.height = aValue + 'px';
		    	}
		    }
		});
	    Object.defineProperty(aPublished, "componentPopupMenu", {
	    	get : function() {
	    		var menu = comp.@com.eas.form.published.HasComponentPopupMenu::getPlatypusPopupMenu()();
			    return @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(menu);
	    	},
		    set : function(aValue) {
		    	if (aValue && aValue.unwrap) {
			    	comp.@com.eas.form.published.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/form/published/menu/PlatypusPopupMenu;)(aValue.unwrap());
		    	} else {
			    	comp.@com.eas.form.published.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/form/published/menu/PlatypusPopupMenu;)(null);
		    	}
		    }
		});
	    Object.defineProperty(aPublished, "parent", {
		    get : function() {
		    	return @com.eas.form.ControlsUtils::lookupPublishedParent(Lcom/google/gwt/user/client/ui/UIObject;)(comp);
		    }
		});
	    Object.defineProperty(aPublished, "name", {
		    get : function() {
		    	return comp.@com.eas.form.published.HasJsName::getJsName()();
		    },
		    set : function(aValue){
		    	comp.@com.eas.form.published.HasJsName::setJsName(Ljava/lang/String;)("" + aValue);
		    }
		});
	    Object.defineProperty(aPublished, "focus", {
		    get : function() {
		    	return function(){
		    		@com.eas.form.ControlsUtils::focus(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    	}
		    }
		});
	    // Events
	    @com.eas.form.JsUi::publishExecutor(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
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
				@com.eas.form.ControlsUtils::callOnResize(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
	    	}
	    });
	    Object.defineProperty(aPublished, "showOn", {
	    	value : function(aElement) {
	    		if(typeof aElement == "string")
	    			aElement = $doc.getElementById(aElement);
	    		if(aElement){
						@com.eas.form.ControlsUtils::addWidgetTo(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/dom/client/Element;)(aPublished.unwrap(), aElement);
	    		}
	    	}
	    });
	    comp.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
	}-*/;
	
	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.eas.form.published.HasEventsExecutor::getEventsExecutor()();
			if(executor == null){
				executor = @com.eas.form.EventsExecutor::new(Lcom/google/gwt/user/client/ui/UIObject;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
				comp.@com.eas.form.published.HasEventsExecutor::setEventsExecutor(Lcom/eas/form/EventsExecutor;)(executor);
			} else {
				executor.@com.eas.form.EventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
	
			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onItemSelected", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getItemSelected()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setItemSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onValueChange", {
				get : function() {
					return executor.@com.eas.form.EventsExecutor::getValueChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.form.EventsExecutor::setValueChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}
	}-*/;

	public native static void init()/*-{
		
		// ***************************************************
		function publishChildrenOrdering(aPublished) {
			if (aPublished) {
				var comp = aPublished.unwrap();
				aPublished.toFront = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.form.published.containers.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.form.published.containers.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
		
				aPublished.toBack = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.form.published.containers.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.form.published.containers.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
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
			aPublished.child = function(aIndex) {
				var widget = comp.@com.google.gwt.user.client.ui.IndexedPanel::getWidget(I)(aIndex);
				return @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
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
			@com.eas.predefine.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
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
				aComponent.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
		
		predefine(['common-utils/color', 'common-utils/cursor', 'common-utils/font', 'forms/form'], 'ui', function(Color, Cursor, Font, Form){
			function selectFile(aCallback, aFileFilter) {
				@com.eas.form.ControlsUtils::jsSelectFile(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(aCallback, aFileFilter);
			}
			
			function selectColor(aCallback, aOldValue) {
				@com.eas.form.ControlsUtils::jsSelectColor(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aOldValue != null ? aOldValue + '' : '', aCallback);
			}
			
			function readFormDocument(aDocumnet, aModel, aTarget){
				var factory = @com.eas.form.store.XmlDom2Form::transform(Lcom/google/gwt/xml/client/Document;Lcom/google/gwt/core/client/JavaScriptObject;)(aDocumnet, aModel);
				var nativeForm = factory.@com.eas.form.FormFactory::getForm()(); 
				if(aTarget)
					Form.call(aTarget, null, null, nativeForm);
				else
					aTarget = new Form(null, null, nativeForm);
				var nwList = factory.@com.eas.form.FormFactory::getWidgetsList()();
				for(var i = 0; i < nwList.@java.util.List::size()(); i++){
					var nWidget = nwList.@java.util.List::get(I)(i);
					var pWidget = nWidget.@com.eas.client.HasPublished::getPublished()();
					if(pWidget.name)
						aTarget[pWidget.name] = pWidget;
				}
				return aTarget;
			}
			function loadForm(appElementName, aModel, aTarget) {
				var appElementDoc = aClient.@com.eas.client.AppClient::getFormDocument(Ljava/lang/String;)(appElementName);
				var form = readFormDocument(appElementDoc, aModel, aTarget);
	            if (!form.title)
	                form.title = appElementName;
	            form.formKey = appElementName;
	            return form;
			}
			function readForm(aFormContent, aModel, aTarget){
				var doc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aFormContent + "");
				return readFormDocument(doc, aModel, aTarget);
			}
			var module = {};
			
			function Icon() {
			}
			Object.defineProperty(Icon, "load", { 
				value: function(aIconName, aOnSuccess, aOnFailure) {
					@com.eas.form.PlatypusImageResource::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aIconName != null ? '' + aIconName : null, aOnSuccess, aOnFailure);
				} 
			});
			var Orientation = {HORIZONTAL: 0, VERTICAL: 1};
			var VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3};
			var HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4};
			var FontStyle = {NORMAL: 0, BOLD: 1, ITALIC: 2, BOLD_ITALIC: 3};
			var ScrollBarPolicy = {ALLWAYS: 32, NEVER: 31, AUTO: 30};
			
		    Object.defineProperty(module, 'Colors', {
		        enumerable: true,
		        value: Color
		    });
		    Object.defineProperty(module, 'Color', {
		        enumerable: true,
		        value: Color
		    });
		    Object.defineProperty(module, 'Cursor', {
		        enumerable: true,
		        value: Cursor
		    });
		    Object.defineProperty(module, 'Icon', {
		        enumerable: true,
		        value: Icon
		    });
		    Object.defineProperty(module, 'Icons', {
		        enumerable: true,
		        value: Icon
		    });
		    Object.defineProperty(module, 'Font', {
		        enumerable: true,
		        value: Font
		    });
			Object.defineProperty(module, 'VK_ALT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ALT 
			});
			Object.defineProperty(module, 'VK_BACKSPACE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE 
			});
			Object.defineProperty(module, 'VK_BACKSPACE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE 
			});
			Object.defineProperty(module, 'VK_DELETE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_DELETE 
			});
			Object.defineProperty(module, 'VK_DOWN', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_DOWN 
			});
			Object.defineProperty(module, 'VK_END', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_END 
			});
			Object.defineProperty(module, 'VK_ENTER', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ENTER 
			});
			Object.defineProperty(module, 'VK_ESCAPE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ESCAPE 
			});
			Object.defineProperty(module, 'VK_HOME', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_HOME 
			});
			Object.defineProperty(module, 'VK_LEFT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_LEFT
			});
			Object.defineProperty(module, 'VK_PAGEDOWN', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEDOWN 
			});
			Object.defineProperty(module, 'VK_PAGEUP', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEUP 
			});
			Object.defineProperty(module, 'VK_RIGHT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_RIGHT 
			});
			Object.defineProperty(module, 'VK_SHIFT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_SHIFT 
			});
			Object.defineProperty(module, 'VK_TAB', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_TAB 
			});
			Object.defineProperty(module, 'VK_UP', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_UP 
			});
			Object.defineProperty(module, 'selectFile', {
				enumerable: true,
				value : selectFile
			});
			Object.defineProperty(module, 'selectColor', {
				enumerable: true,
				value : selectColor
			});
		    Object.defineProperty(module, 'msgBox', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'error', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'warn', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'HorizontalPosition', {
		        enumerable: true,
		        value: HorizontalPosition
		    });
		    Object.defineProperty(module, 'VerticalPosition', {
		        enumerable: true,
		        value: VerticalPosition
		    });
		    Object.defineProperty(module, 'Orientation', {
		        enumerable: true,
		        value: Orientation
		    });
		    Object.defineProperty(module, 'ScrollBarPolicy', {
		        enumerable: true,
		        value: ScrollBarPolicy
		    });
		    Object.defineProperty(module, 'FontStyle', {
		        enumerable: true,
		        value: FontStyle
		    });
			Object.defineProperty(module, 'loadForm', {
				enumerable: true,
				value : loadForm
			});
			Object.defineProperty(module, 'readForm', {
				enumerable: true,
				value : readForm
			});
			@com.eas.form.JsUi::ui = module;
			return module;
		});
		
		predefine([], 'forms/cell-render-event', function(){
			function CellRenderEvent(aSource, aRowId, aColumnId, aRendered, aCell){
				Object.defineProperty(this, "source", {
					get : function(){
						return aSource;
					}
				});
				Object.defineProperty(this, "id", {
					get : function(){
						return aRowId;
					}
				});
				Object.defineProperty(this, "columnId", {
					get : function(){
						return aColumnId;
					}
				});
				Object.defineProperty(this, "object", {
					get : function(){
						return aRendered;
					}
				});
				Object.defineProperty(this, "cell", {
					get : function(){
						return aCell;
					}
				});
			}
			return CellRenderEvent;
		});
		
		predefine([], 'forms/window-event', function(){
			function WindowEvent(aSource){
				Object.defineProperty(this, "source", {
					get : function(){
						return aSource;
					}
				});
			}
			return WindowEvent;
		});
				
		predefine([], 'forms/mouse-event', function(){
			function MouseEvent(aEvent, aClickCount){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
				Object.defineProperty(this, "x", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::getX()();
					}
				});
				Object.defineProperty(this, "y", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::getY()();
					}
				});
				Object.defineProperty(this, "screenX", {
					get : function() {
						aEvent.@com.google.gwt.event.dom.client.MouseEvent::getScreenX()();
					}
				});
				Object.defineProperty(this, "screenY", {
					get : function() {
						aEvent.@com.google.gwt.event.dom.client.MouseEvent::getScreenY()();
					}
				});
				Object.defineProperty(this, "altDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isAltKeyDown()();
					}
				});
				Object.defineProperty(this, "controlDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isControlKeyDown()();
					}
				});
				Object.defineProperty(this, "shiftDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isShiftKeyDown()();
					}
				});
				Object.defineProperty(this, "metaDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.MouseEvent::isMetaKeyDown()();
					}
				});
				Object.defineProperty(this, "button", {
					get : function() {
						var button = aEvent.@com.google.gwt.event.dom.client.MouseEvent::getNativeButton()();
						switch (button) {
							case @com.google.gwt.dom.client.NativeEvent::BUTTON_LEFT : return 1; 
							case @com.google.gwt.dom.client.NativeEvent::BUTTON_RIGHT : return 2; 
							case @com.google.gwt.dom.client.NativeEvent::BUTTON_MIDDLE : return 3;
							default : return 0;
						} 
					}
				});
				Object.defineProperty(this, "clickCount", {
					get : function() {
						if(aClickCount)
							return aClickCount;
						else 
							return 0;
					}
				});
			}
			return MouseEvent;
		});
		
		predefine([], 'forms/key-event', function(){
			function KeyEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
				Object.defineProperty(this, "altDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isAltKeyDown()();
					}
				});
				Object.defineProperty(this, "controlDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isControlKeyDown()();
					}
				});
				Object.defineProperty(this, "shiftDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isShiftKeyDown()();
					}																																																																			
				});
				Object.defineProperty(this, "metaDown", {
					get : function() {
						return aEvent.@com.google.gwt.event.dom.client.KeyEvent::isMetaKeyDown()();
					}
				});
				Object.defineProperty(this, "key", {
					get : function() {																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																
						var ne = aEvent.@com.google.gwt.event.dom.client.KeyEvent::getNativeEvent()();
						return ne.keyCode || 0;																																																																																																																																																																																																																																																																																																																																																																																																																																																																													
					}
	            });
				Object.defineProperty(this, "char", {
					get : function() {																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																
						var ne = aEvent.@com.google.gwt.event.dom.client.KeyEvent::getNativeEvent()();
						return String.fromCharCode(ne.charCode || 0); 
					}
				});
			}
			return KeyEvent;
		});
		
		predefine([], 'forms/container-event', function(){
			function ContainerEvent(aEvent, isAdd){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
				Object.defineProperty(this, "child", {
					get : function(){
						var comp;
						if(isAdd)
							comp = aEvent.@com.eas.form.events.AddEvent::getWidget()();
						else
							comp = aEvent.@com.eas.form.events.RemoveEvent::getWidget()();
						return @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
					}
				});
			}
			return ContainerEvent;
		});
		
		predefine([], 'forms/item-event', function(){
			function ItemEvent(aSource, aItem){
				Object.defineProperty(this, "source", {
					get : function(){
						return aSource;
					}
				});
				Object.defineProperty(this, "item", {
					get : function(){
						return aItem;
					}
				});
			}
			return ItemEvent;
		});
		
		predefine([], 'forms/component-event', function(){
			function ComponentEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			return ComponentEvent;
		});
		
		predefine([], 'forms/focus-event', function(){
			function FocusEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			return FocusEvent;
		});
		
		predefine([], 'forms/action-event', function(){
			function ActionEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			return ActionEvent;
		});
		
		predefine([], 'forms/label', function(){
			function Label(aText, aIcon, aIconTextGap) {			
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof Label)) {
					throw  ' use  "new Label()" !';
				}
	
				var published = this;
				 
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusLabel::new()(); 	
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
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusButton::new()();
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
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusSplitButton::new()();
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
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusToggleButton::new()();
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
			return ToggleButton;
		});	
		
		predefine([], 'forms/radio-button', function(){
			function RadioButton(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof RadioButton)) {
					throw  ' use  "new RadioButton()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusRadioButton::new()();
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
			return RadioButton;	
		});	
		
		predefine([], 'forms/check-box', function(){
			function CheckBox(aText, aSelected, aCallback) {
				var aComponent = arguments.length>3?arguments[3]:null;
				
				if (!(this instanceof CheckBox)) {
					throw  ' use  "new CheckBox()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusCheckBox::new()();
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
			return CheckBox;
		});			
		
		predefine([], 'forms/password-field', function(){
			function PasswordField(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof PasswordField)) {
					throw  ' use  "new PasswordField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusPasswordField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);			
				if (aText) {
					published.text = aText;
				} 	
			}
			return PasswordField;
		});	
		
		predefine([], 'forms/text-field', function(){
			function TextField(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof TextField)) {
					throw  ' use  "new TextField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusTextField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			return TextField;
		});	

		predefine([], 'forms/formatted-field', function(){
			function FormattedField(aValue) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof FormattedField)) {
					throw  ' use  "new FormattedField()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusFormattedTextField::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aValue) {
					published.value = aValue;
				} 	
			}
			return FormattedField;
		});
		
		predefine([], 'forms/text-area', function(){
			function TextArea(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof TextArea)) {
					throw  ' use  "new TextArea()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusTextArea::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			return TextArea;
		});
		
		predefine([], 'forms/html-area', function(){
			function HtmlArea(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof HtmlArea)) {
					throw  ' use  "new HtmlArea()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusHtmlEditor::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				if (aText) {
					published.text = aText;
				} 	
			}
			return HtmlArea;
		});
		
		predefine(['ui'], 'forms/slider', function(Ui){
			function Slider() {
				var aOrientation = arguments.length == 1 || arguments.length == 4 ? arguments[0] : Ui.Orientation.HORIZONTAL;
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
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusSlider::new(DD)(aMinimum, aMaximum);
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
			return Slider;
		});
		
		predefine([], 'forms/progress-bar', function(){
			function ProgressBar(aMinimum, aMaximum) {
				var aComponent = arguments.length > 2 ? arguments[2] : null;			
				if (!(this instanceof ProgressBar)) {
					throw  ' use  "new ProgressBar()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.PlatypusProgressBar::new()();
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
			return ProgressBar;
		});

		predefine([], 'forms/desktop-pane', function(){
			function DesktopPane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof DesktopPane)) {
					throw  ' use  "new DesktopPane()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.widgets.DesktopPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return DesktopPane;
		});

		predefine([], 'forms/menu-bar', function(){
			function MenuBar() {
				if (!(this instanceof MenuBar)) {
					throw  ' use  "new MenuBar()" !';
				}
				var aComponent = arguments.length > 0 ? arguments[0] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenuBar::new(Z)(false);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
			}
			return MenuBar;
		});

		predefine([], 'forms/menu', function(){
			function Menu(aText) {
				if (!(this instanceof Menu)) {
					throw  ' use  "new Menu()" !';
				}
				var aComponent = arguments.length > 1 ? arguments[1] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenu::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if (aText) {
					published.text = aText;
				}
			}
			return Menu;
		});

		predefine([], 'forms/popup-menu', function(){
			function PopupMenu() {
				if (!(this instanceof PopupMenu)) {
					throw  ' use  "new PopupMenu()" !';
				}
				var aComponent = arguments.length > 0 ? arguments[0] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusPopupMenu::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
			}
			return PopupMenu;
		});
		
		predefine([], 'forms/menu-item', function(){
			function MenuItem(aText, aIcon, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof MenuItem)) {
					throw  ' use  "new MenuItem()" !';
				}
	
				var published = this;
				
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenuItemImageText::new()();
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
				return published;
			}
			return MenuItem;
		});	
		
		predefine([], 'forms/check-menu-item', function(){
			function CheckMenuItem(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof CheckMenuItem)) {
					throw  ' use  "new CheckMenuItem()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenuItemCheckBox::new()();
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
				return published;
			}
			return CheckMenuItem;
		});

		predefine([], 'forms/radio-menu-item', function(){
			function RadioMenuItem(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof RadioMenuItem)) {
					throw  ' use  "new RadioMenuItem()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenuItemRadioButton::new()();
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
				return published;
			}
		});
		
		predefine([], 'forms/menu-separator', function(){
			function MenuSeparator() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof MenuSeparator)) {
					throw  ' use  "new MenuSeparator()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.form.published.menu.PlatypusMenuItemSeparator::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return MenuSeparator;
		});
		
		predefine([], 'forms/model-grid', function(){
			function ModelGrid() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelGrid)) {
					throw  ' use  "new ModelGrid()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelGrid::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelGrid;
		});		

		predefine([], 'forms/check-grid-column', function(){
			function CheckGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof CheckGridColumn)) {
					throw  ' use  "new CheckGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.grid.columns.header.CheckHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return CheckGridColumn;
		});	
		
		predefine([], 'forms/radio-grid-column', function(){
			function RadioGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof RadioGridColumn)) {
					throw  ' use  "new RadioGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.grid.columns.header.RadioHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return RadioGridColumn;	
		});
				
		predefine([], 'forms/service-grid-column', function(){
			function ServiceGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ServiceGridColumn)) {
					throw  ' use  "new ServiceGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.grid.columns.header.ServiceHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return ServiceGridColumn;
		});	
		
		predefine([], 'forms/model-grid-column', function(){
			function ModelGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelGridColumn)) {
					throw  ' use  "new ModelGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.grid.columns.header.ModelHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return ModelGridColumn;
		});	
		
		predefine([], 'forms/model-check-box', function(){
			function ModelCheckBox(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				
				if (!(this instanceof ModelCheckBox)) {
					throw  ' use  "new ModelCheckBox()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelCheck::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if(!injected){
					published.text = aText;
				}
				return published;
			}
			return ModelCheckBox;	
		});
		
		predefine([], 'forms/model-formatted-field', function(){
			function ModelFormattedField() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelFormattedField)) {
					throw  ' use  "new ModelFormattedField()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelFormattedField::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelFormattedField;	
		});
		
		predefine([], 'forms/model-text-aArea', function(){
			function ModelTextArea() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelTextArea)) {
					throw  ' use  "new ModelTextArea()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelTextArea::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelTextArea;
		});	

		predefine([], 'forms/model-date', function(){
			function ModelDate() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelDate)) {
					throw  ' use  "new ModelDate()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelDate::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelDate;
		});	

		predefine([], 'forms/model-spin', function(){
			function ModelSpin() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelSpin)) {
					throw  ' use  "new ModelSpin()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelSpin::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelSpin;
		});	

		predefine([], 'forms/model-combo', function() {
			function ModelCombo() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelCombo)) {
					throw  ' use  "new ModelCombo()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.form.published.widgets.model.ModelCombo::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelCombo;
		});	

		predefine(['ui'], 'forms/border-pane', function(Ui){
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
				aComponent = aComponent || @com.eas.form.published.containers.BorderPane::new(II)(aVGap, aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			};
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
				aComponent = aComponent || @com.eas.form.published.containers.FlowPane::new(II)(aVGap,aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			return FlowPane;
		});
		
		predefine([], 'forms/grid-pane', function(){
			function GridPane(aRows, aCols, aVGap, aHGap) {
				var aComponent = arguments.length > 4 ? arguments[4] : null;
				if (!(this instanceof GridPane)) {
					throw  ' use  "new GridPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.GridPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				
				if(arguments.length <= 4){
					if (aRows == undefined) {
						throw "aRows argument is required!"
					}
					if (aCols == undefined) {
						throw "aCols argument is required!"
					}
					aComponent.@com.eas.form.published.containers.GridPane::resize(II)(aRows, aCols);
					if (aVGap) {
						aComponent.@com.eas.form.published.containers.GridPane::setVgap(I)(aVGap);
					}
					if (aHGap) {
						aComponent.@com.eas.form.published.containers.GridPane::setHgap(I)(aHGap);
					}
				}
			}
			return GridPane;
		});
		
		predefine(['ui'], 'forms/box-pane', function(Ui){
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
						aOrientation = Ui.Orientation.HORIZONTAL;
					aComponent = @com.eas.form.published.containers.BoxPane::new(III)(aOrientation, aHGap, aVGap);
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
						aComponent.@com.eas.form.published.containers.BoxPane::add(Lcom/google/gwt/user/client/ui/Widget;I)(toAdd.unwrap(), published.orientation == Ui.Orientation.VERTICAL ? toAdd.height : toAdd.width);
					}
				}
				
				Object.defineProperty(published, "orientation", {
					get : function(){
						return aComponent.@com.bearsoft.gwt.ui.containers.BoxPanel::getOrientation()();
					}
				});
			}
			return BoxPane;
		});		

		predefine([], 'forms/card-pane', function(){
			function CardPane(aVGap, aHGap) {
				if (!(this instanceof CardPane)) {
					throw  ' use  "new CardPane()" !';
				}
				var aComponent = arguments.length>2?arguments[2]:null;
				if(!aComponent)
				{
					if(!aVGap)
						aVGap = 0;
					if(!aHGap)
						aHGap = 0;
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.CardPane::new(II)(aVGap, aHGap);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			return CardPane;
		});		

		predefine([], 'forms/tabbed-pane', function(){
			function TabbedPane() {
				var aComponent = arguments.length>0?arguments[0]:null;
				if (!(this instanceof TabbedPane)) {
					throw  ' use  "new TabbedPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.TabbedPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			return TabbedPane;
		});

		predefine([], 'forms/scroll-pane', function(){
			function ScrollPane(aChild) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if (!(this instanceof ScrollPane)) {
					throw  ' use  "new ScrollPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.ScrollPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				if(aChild)
					published.add(aChild);
			}
			return ScrollPane;
		});		
		
		predefine(['ui'], 'forms/split-pane', function(Ui){
			function SplitPane(aOrientation) {
				if (!(this instanceof SplitPane)) {
					throw  ' use  "new SplitPane()" !';
				}
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				if(!aComponent)
				{
					if(!aOrientation)
						aOrientation = Ui.Orientation.HORIZONTAL;
					aComponent = @com.eas.form.published.containers.SplitPane::new()();
					var orientation = (aOrientation === Ui.Orientation.VERTICAL ? @com.eas.form.published.containers.SplitPane::VERTICAL_SPLIT : @com.eas.form.published.containers.SplitPane::HORIZONTAL_SPLIT); 
					aComponent.@com.eas.form.published.containers.SplitPane::setOrientation(I)(orientation);
				}
	
				var published = this; 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			return SplitPane
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
					aComponent = @com.eas.form.published.containers.ToolBar::new()();
				}
	
				var published = this;
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
			}
			return ToolBar;
		});
		
		predefine([], 'forms/anchors-pane', function(){
			function AnchorsPane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof AnchorsPane)) {
					throw  ' use  "new AnchorsPane()" !';
				}
	
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.AnchorsPane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				publishChildrenOrdering(published);
			}
			return AnchorsPane;
		});

		predefine([], 'forms/absolute-pane', function(){
			function AbsolutePane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof AbsolutePane)) {
					throw  ' use  "new AbsolutePane()" !';
				}
				var published = this; 
				aComponent = aComponent || @com.eas.form.published.containers.AbsolutePane::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishIndexedPanel(published);
				publishChildrenOrdering(published);
			}
			return AbsolutePane;
		});
		
		predefine([], 'forms/button-group', function(){
			function ButtonGroup() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ButtonGroup)) {
					throw  ' use  "new ButtonGroup()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.form.published.containers.ButtonGroup::new()();
				published.unwrap = function() {
					return aComponent;
				};			
				aComponent.@com.eas.form.published.containers.ButtonGroup::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return ButtonGroup;
		});

		predefine([], 'forms/anchors', function(){
			function Anchors(aLeft, aWidth, aRight, aTop, aHeight, aBottom) {
				function marginToString (aMargin) {
					if (aMargin != undefined && aMargin != null) {
						var unit = aMargin.@com.eas.form.MarginConstraints.Margin::unit;
						return aMargin.@com.eas.form.MarginConstraints.Margin::value + unit.@com.google.gwt.dom.client.Style.Unit::getType()();
					}
					return null;
				}
				
				if (!(this instanceof Anchors)) {
					throw  ' use  "new Anchors(...)" !';
				}
				var aConstraints = arguments.length>6?arguments[6]:null;
				if(!aConstraints){
					aConstraints = @com.eas.form.MarginConstraints::new()();
				}
				var published = this; 
				published.unwrap = function() {
					return aConstraints;
				};
				
				Object.defineProperty(published, "left", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getLeft()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setLeft(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "width", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getWidth()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setWidth(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "right", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getRight()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setRight(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "top", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getTop()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setTop(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "height", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getHeight()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setHeight(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "bottom", {
					get : function() {
						return marginToString(aConstraints.@com.eas.form.MarginConstraints::getBottom()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.form.MarginConstraints::setBottom(Lcom/eas/form/MarginConstraints$Margin;)(margin);
						}
					}
				});
				if (aLeft != null) {
					published.left = '' + aLeft;
				}
				if (aWidth != null) {
					published.width = '' + aWidth;
				}
				if (aRight != null) {
					published.right = '' + aRight;
				}
				if (aTop != null) {
					published.top = '' + aTop;
				}
				if (aHeight != null) {
					published.height = '' + aHeight;
				}
				if (aBottom != null) {
					published.bottom = '' + aBottom;
				}
			}
			return Anchors; 
		});
		
	}-*/;
}

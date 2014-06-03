package com.eas.client.form.js;

import com.eas.client.form.published.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class JsWidgets {

	public native static void init()/*-{
		
		$wnd.P.Orientation = {HORIZONTAL: 0, VERTICAL: 1};
		$wnd.P.VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3};
		$wnd.P.HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4};
		$wnd.P.FontStyle = {NORMAL: 0, BOLD: 1, ITALIC: 2, BOLD_ITALIC: 3};
		
		// ***************************************************
		$wnd.P.Label = function(aText, aIcon, aIconTextGap) {			
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.P.Label)) {
				throw  ' use  "new P.Label()" !';
			}

			var published = this;
			 
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusLabel::new()(); 	
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
		};		
		
		// **************************************************************************
		$wnd.P.Button = function (aText, aIcon, aGapOrCallback, aCallback) {
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;
			
			var aComponent = arguments.length > 4 ? arguments[4] : null;
			
			if (!(this instanceof $wnd.P.Button)) {
				throw  ' use  "new P.Button()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusButton::new()();
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
		};	
		
		// **************************************************************************
		$wnd.P.DropDownButton = function (aText, aIcon, aGapOrCallback, aCallback) {			
			if (!(this instanceof $wnd.P.DropDownButton)) {
				throw  ' use  "new P.DropDownButton()" !';
			}
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;
			var aComponent = arguments.length > 4 ? arguments[4] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusSplitButton::new()();
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
		};	
		
		// **************************************************************************
		$wnd.P.ToggleButton = function (aText, aIcon, aSelected, aGapOrCallback, aCallback) {
			
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;

			var aComponent = arguments.length > 5 ? arguments[5] : null;
			
			if (!(this instanceof $wnd.P.ToggleButton)) {
				throw  ' use  "new P.ToggleButton()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusToggleButton::new()();
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
		};	
		
		// **************************************************************************
		$wnd.P.RadioButton = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.P.RadioButton)) {
				throw  ' use  "new P.RadioButton()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusRadioButton::new()();
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
		};	
		
		// **************************************************************************
		$wnd.P.CheckBox = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.P.CheckBox)) {
				throw  ' use  "new P.CheckBox()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusCheckBox::new()();
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
		};			
		
		// **************************************************************************
		$wnd.P.PasswordField = function (aText) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.PasswordField)) {
				throw  ' use  "new P.PasswordField()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusPasswordField::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);			
			if (aText) {
				published.text = aText;
			} 	
		};	
		
		// **************************************************************************
		$wnd.P.TextField = function (aText) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.TextField)) {
				throw  ' use  "new P.TextField()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusTextField::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			if (aText) {
				published.text = aText;
			} 	
		};	

		// **************************************************************************
		$wnd.P.FormattedField = function (aValue) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.FormattedField)) {
				throw  ' use  "new P.FormattedField()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusFormattedTextField::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			if (aValue) {
				published.value = aValue;
			} 	
		};			
		
		// **************************************************************************
		$wnd.P.TextArea = function (aText) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.TextArea)) {
				throw  ' use  "new P.TextArea()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusTextArea::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			if (aText) {
				published.text = aText;
			} 	
		}
		
		// **************************************************************************
		$wnd.P.HtmlArea = function (aText) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.HtmlArea)) {
				throw  ' use  "new P.HtmlArea()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusHtmlEditor::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			if (aText) {
				published.text = aText;
			} 	
		}
		
		// **************************************************************************
		$wnd.P.Slider = function () {
			var aOrientation = arguments.length == 1 || arguments.length == 4 ? arguments[0] : $wnd.P.Orientation.HORIZONTAL;
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
			if (!(this instanceof $wnd.P.Slider)) {
				throw  ' use  "new P.Slider()" !';
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
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusSlider::new(DD)(aMinimum, aMaximum);
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
		
		// **************************************************************************
		$wnd.P.ProgressBar = function (aMinimum, aMaximum) {
			var aComponent = arguments.length > 2 ? arguments[2] : null;			
			if (!(this instanceof $wnd.P.ProgressBar)) {
				throw  ' use  "new P.ProgressBar()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusProgressBar::new()();
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

		// ***************************************************
		$wnd.P.DesktopPane = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.P.DesktopPane)) {
				throw  ' use  "new P.DesktopPane()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.widgets.DesktopPane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			return published;
		};

		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
	}-*/;

	public native static void publishComponentProperties(PublishedComponent aPublished)/*-{
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
		    	//if(_background == null) {
		    	//	var style = $wnd.P.getElementComputedStyle(comp.@com.google.gwt.user.client.ui.UIObject::getElement()());
		    	//	return @com.eas.client.form.ControlsUtils::parseColor(Ljava/lang/String;)(style.backgroundColor);
		    	//}
		    	return _background;
		    },
		    set : function(aValue) {
		    	_background = aValue;
		    	//apply
		    	@com.eas.client.form.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : ""); 
		    }
 	    });
	    Object.defineProperty(aPublished, "backgroundSet", {get : function(){return _background != null;}});
	    Object.defineProperty(aPublished, "foreground", {
		    get : function() {
		    	// if(_foreground == null){
		    	//	var style = $wnd.P.getElementComputedStyle(comp.@com.google.gwt.user.client.ui.Widget::getElement()());
		    	//	return @com.eas.client.form.ControlsUtils::parseColor(Ljava/lang/String;)(style.color);
		    	//}
		    	return _foreground;
		    },
		    set : function(aValue) {
		    	_foreground = aValue;
		    	// apply
		    	@com.eas.client.form.ControlsUtils::applyForeground(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/client/form/published/PublishedColor;)(comp, _foreground); 
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
	    		@com.eas.client.form.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : "");
		    }
 	    });
	    Object.defineProperty(aPublished, "font", {
		    get : function() {
		    	if(_font == null)
		    	{
		    		var style = $wnd.P.getElementComputedStyle(comp.@com.google.gwt.user.client.ui.UIObject::getElement()());
		    		var isItalic = style.fontStyle == "italic";
		    		var isBold = style.fontWeight == "bold" || style.fontWeight == "bolder"; 
		    		var platypusFontStyle = $wnd.P.FontStyle.NORMAL;
		    		if(isItalic)
		    		{
		    			if(isBold)
		    				platypusFontStyle = $wnd.P.FontStyle.BOLD_ITALIC;
		    			else
		    				platypusFontStyle = $wnd.P.FontStyle.ITALIC;
		    		}else if(isBold)
		    			platypusFontStyle = $wnd.P.FontStyle.BOLD;
		    		return new $wnd.P.Font(style.fontFamily, platypusFontStyle, parseInt(""+style.fontSize));
		    	}
		    	return _font;
		    },
		    set : function(aValue) {
		    	_font = aValue;
		    	// apply
		    	@com.eas.client.form.ControlsUtils::applyFont(Lcom/google/gwt/user/client/ui/UIObject;Lcom/eas/client/form/published/PublishedFont;)(comp, _font); 
		    }
 	    });
	    Object.defineProperty(aPublished, "fontSet", { get : function(){return _font != null;}});
 	    Object.defineProperty(aPublished, "cursor", {
 	    	get : function(){
 	    		if(_cursor == null)
 	    		{
		    		var style = $wnd.P.getElementComputedStyle(comp.@com.google.gwt.user.client.ui.UIObject::getElement()());
		    		return style.cursor;
 	    		}
 	    		return _cursor;
 	    	},
 	    	set : function(aValue)
 	    	{
 	    		_cursor = aValue; 
 	    		// apply	
		    	@com.eas.client.form.ControlsUtils::applyCursor(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _cursor); 
 	    	}
 	    	
 	    });
	    Object.defineProperty(aPublished, "cursorSet", { get : function(){return _cursor != null;}});
	     Object.defineProperty(aPublished, "left", {
		    get : function() {
    			return aPublished.element.offsetLeft;
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustLeft(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "top", {
		    get : function() {
    			return aPublished.element.offsetTop;
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustTop(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
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
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	else if(aPublished.parent instanceof $wnd.P.ScrollPane)
		    		@com.eas.client.form.published.containers.ScrollPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	else if(aPublished.parent instanceof $wnd.P.FlowPane)
		    		@com.eas.client.form.published.containers.FlowPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	else if(aPublished.parent instanceof $wnd.P.BoxPane && aPublished.parent.orientation == $wnd.P.Orientation.HORIZONTAL){
	    			aPublished.parent.unwrap().@com.eas.client.form.published.containers.HBoxPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
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
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	else if(aPublished.parent instanceof $wnd.P.ScrollPane)
		    		@com.eas.client.form.published.containers.ScrollPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	else if(aPublished.parent instanceof $wnd.P.FlowPane)
		    		@com.eas.client.form.published.containers.FlowPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	else if(aPublished.parent instanceof $wnd.P.BoxPane && aPublished.parent.orientation == $wnd.P.Orientation.VERTICAL){
	    			aPublished.parent.unwrap().@com.eas.client.form.published.containers.VBoxPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}
		    }
 	    });
	    Object.defineProperty(aPublished, "componentPopupMenu", {
	    	get : function() {
	    		var menu = comp.@com.eas.client.form.published.HasComponentPopupMenu::getPlatypusPopupMenu()();
			    return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(menu);
	    	},
		    set : function(aValue) {
		    	if (aValue && aValue.unwrap) {
			    	comp.@com.eas.client.form.published.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/client/form/published/menu/PlatypusPopupMenu;)(aValue.unwrap());
		    	} else {
			    	comp.@com.eas.client.form.published.HasComponentPopupMenu::setPlatypusPopupMenu(Lcom/eas/client/form/published/menu/PlatypusPopupMenu;)(null);
		    	}
		    }
 	    });
	    Object.defineProperty(aPublished, "parent", {
		    get : function() {
		    	return @com.eas.client.form.ControlsUtils::lookupPublishedParent(Lcom/google/gwt/user/client/ui/UIObject;)(comp);
		    }
 	    });
	    Object.defineProperty(aPublished, "name", {
		    get : function() {
		    	return comp.@com.eas.client.form.published.HasJsName::getJsName()();
		    }
 	    });
	    Object.defineProperty(aPublished, "focus", {
		    get : function() {
		    	return function(){aPublished.element.focus();}
		    }
 	    });
 	    // Events
 	    @com.eas.client.form.js.JsWidgets::publishExecutor(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
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
        comp.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
	}-*/;
	
	public native static JavaScriptObject publishExecutor(JavaScriptObject published)/*-{
		if (published && published.unwrap) {
			var comp = published.unwrap();
			var executor = comp.@com.eas.client.form.published.HasEventsExecutor::getEventsExecutor()();
			if(executor == null){
				executor = @com.eas.client.form.EventsExecutor::new(Lcom/google/gwt/user/client/ui/UIObject;Lcom/google/gwt/core/client/JavaScriptObject;)(comp, published);
				comp.@com.eas.client.form.published.HasEventsExecutor::setEventsExecutor(Lcom/eas/client/form/EventsExecutor;)(executor);
			} else {
				executor.@com.eas.client.form.EventsExecutor::setEventThis(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Object.defineProperty(published, "onActionPerformed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getActionPerformed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setActionPerformed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
	
			Object.defineProperty(published, "onMouseExited", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseExited()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseExited(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseClicked", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseClicked()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseClicked(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMousePressed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMousePressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMousePressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseReleased", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseEntered", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseEntered()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseEntered(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseWheelMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseWheelMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseWheelMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseDragged", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseDragged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseDragged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onMouseMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getMouseMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setMouseMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentResized", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentResized()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentResized(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentMoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentMoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentMoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentShown", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentShown()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentShown(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentHidden", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentHidden()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentHidden(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentAdded", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentAdded()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentAdded(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onComponentRemoved", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getComponentRemoved()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setComponentRemoved(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});			
			Object.defineProperty(published, "onFocusGained", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getFocusGained()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setFocusGained(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onFocusLost", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getFocusLost()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setFocusLost(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyTyped", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyTyped()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyTyped(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyPressed", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyPressed()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyPressed(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onKeyReleased", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getKeyReleased()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setKeyReleased(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onStateChanged", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getStateChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setStateChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}
	
	}-*/;
}

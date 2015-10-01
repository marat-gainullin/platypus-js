package com.eas.client.application.js;

import com.eas.client.form.published.PublishedComponent;
import com.google.gwt.core.client.JavaScriptObject;

public class JsWidgets {

	public native static void init()/*-{
		function predefine(aDeps, aName, aDefiner){
			var resolved = [];
			for(var d = 0; d < aDeps.length; d++){
				var module = @com.eas.client.application.Application::prerequire(Ljava/lang/String;)(aDeps[d]);
				resolved.push(module);
			}
			@com.eas.client.application.Application::predefine(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aName, aDefiner(resolved));
		}
		predefine([], 'forms/label', function(){
			function Label(aText, aIcon, aIconTextGap) {			
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof Label)) {
					throw  ' use  "new Label()" !';
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
				aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusPasswordField::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusTextField::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusFormattedTextField::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusTextArea::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.widgets.PlatypusHtmlEditor::new()();
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
			return Slider;
		});
		
		predefine([], 'forms/progress-bar', function(){
			function ProgressBar(aMinimum, aMaximum) {
				var aComponent = arguments.length > 2 ? arguments[2] : null;			
				if (!(this instanceof ProgressBar)) {
					throw  ' use  "new ProgressBar()" !';
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
			return ProgressBar;
		});

		predefine([], 'forms/desktop-pane', function(){
			function DesktopPane() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				if (!(this instanceof DesktopPane)) {
					throw  ' use  "new DesktopPane()" !';
				}
				var published = this;
				aComponent = aComponent || @com.eas.client.form.published.widgets.DesktopPane::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuBar::new(Z)(false);
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenu::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusPopupMenu::new()();
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
				
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemImageText::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemCheckBox::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemSeparator::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return MenuSeparator;
		});
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.application.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
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
		    	if(aPublished.parent instanceof $wnd.P.BoxPane)
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof $wnd.P.ToolBar)
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.Toolbar::ajustDisplay(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	else if(aPublished.parent instanceof $wnd.P.AnchorsPane)
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
		    	@com.eas.client.form.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _background != null && _opaque ? _background.toStyled() : ""); 
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
 	    		return _cursor;
 	    	},
 	    	set : function(aValue){
 	    		_cursor = aValue; 
 	    		// apply	
		    	@com.eas.client.form.ControlsUtils::applyCursor(Lcom/google/gwt/user/client/ui/UIObject;Ljava/lang/String;)(comp, _cursor); 
 	    	}
 	    	
 	    });
	    Object.defineProperty(aPublished, "cursorSet", { get : function(){return _cursor != null;}});
	    var _left = null;
	    Object.defineProperty(aPublished, "left", {
		    get : function() {
		    	if(aPublished.parent){
    				_left = aPublished.parent.unwrap().@com.eas.client.form.published.containers.HasChildrenPosition::getLeft(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	}
    			return _left;
		    },
		    set : function(aValue) {
		    	_left = aValue;
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustLeft(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
 	    });
 	    var _top = null;
	    Object.defineProperty(aPublished, "top", {
		    get : function() {
		    	if(aPublished.parent){
    				_top = aPublished.parent.unwrap().@com.eas.client.form.published.containers.HasChildrenPosition::getTop(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
		    	}
    			return _top;
		    },
		    set : function(aValue) {
		    	_top = aValue;
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
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane){
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);		    		
		    	}else if(aPublished.parent instanceof $wnd.P.ScrollPane){
		    		@com.eas.client.form.published.containers.ScrollPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof $wnd.P.FlowPane){
		    		@com.eas.client.form.published.containers.FlowPane::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof $wnd.P.BoxPane && aPublished.parent.orientation == $wnd.P.Orientation.HORIZONTAL){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof $wnd.P.ToolBar){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.Toolbar::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _width);
		    	}else if(aPublished.parent instanceof $wnd.P.BorderPane){
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
		    	if(aPublished.parent instanceof $wnd.P.AbsolutePane || aPublished.parent instanceof $wnd.P.AnchorsPane){
		    		aPublished.parent.unwrap().@com.eas.client.form.published.containers.MarginsPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else if(aPublished.parent instanceof $wnd.P.ScrollPane){
		    		@com.eas.client.form.published.containers.ScrollPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
	    		}else if(aPublished.parent instanceof $wnd.P.FlowPane){
		    		@com.eas.client.form.published.containers.FlowPane::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
				}else if(aPublished.parent instanceof $wnd.P.BoxPane && aPublished.parent.orientation == $wnd.P.Orientation.VERTICAL){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BoxPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else if(aPublished.parent instanceof $wnd.P.BorderPane){
	    			aPublished.parent.unwrap().@com.bearsoft.gwt.ui.containers.BorderPanel::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), _height);
		    	}else{
		    		aPublished.element.style.height = aValue + 'px';
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
		    },
		    set : function(aValue){
		    	comp.@com.eas.client.form.published.HasJsName::setJsName(Ljava/lang/String;)("" + aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "focus", {
		    get : function() {
		    	return function(){
		    		@com.eas.client.form.ControlsUtils::focus(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    	}
		    }
 	    });
 	    // Events
 	    @com.eas.client.application.js.JsWidgets::publishExecutor(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
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
    			@com.eas.client.form.ControlsUtils::callOnResize(Lcom/google/gwt/user/client/ui/Widget;)(aPublished.unwrap());
        	}
        });
        Object.defineProperty(aPublished, "showOn", {
        	value : function(aElement) {
        		if(typeof aElement == "string")
        			aElement = $doc.getElementById(aElement);
        		if(aElement){
   					@com.eas.client.form.ControlsUtils::addWidgetTo(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/dom/client/Element;)(aPublished.unwrap(), aElement);
        		}
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
			Object.defineProperty(published, "onItemSelected", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getItemSelected()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setItemSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
			Object.defineProperty(published, "onValueChange", {
				get : function() {
					return executor.@com.eas.client.form.EventsExecutor::getValueChanged()();
				},
				set : function(aValue) {
					executor.@com.eas.client.form.EventsExecutor::setValueChanged(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				},
				configurable : true
			});
		}
	
	}-*/;
}

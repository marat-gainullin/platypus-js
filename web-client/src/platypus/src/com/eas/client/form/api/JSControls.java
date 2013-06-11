package com.eas.client.form.api;

import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.sencha.gxt.widget.core.client.form.Field;

public class JSControls {

	public native static void initControls()/*-{
		
		$wnd.Orientation = {HORIZONTAL: 0, VERTICAL: 1};
		$wnd.VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3};
		$wnd.HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4};
		$wnd.FontStyle = {NORMAL: 0, BOLD: 1, ITALIC: 2, BOLD_ITALIC: 3};
		
		// ***************************************************
		$wnd.Label = function(aText, aIcon, aIconTextGap) {
			
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.Label)) {
				throw  ' use  "new Label()" !';
			}

			var published = this;
			 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::new()(); 	
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			published.opaque = false;

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::getText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			Object.defineProperty(published, "icon", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::getImage()();
				},
				set : function(aValue) {
					var setterCallback = function(){
						aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
					};
					if(aValue != null)
						aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
					setterCallback();
				}
			});
			Object.defineProperty(published, "iconTextGap", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::getIconTextGap()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setIconTextGap(I)(aValue);
				}
			});
			Object.defineProperty(published, "horizontalTextPosition", {
				get : function() {
					var position = aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::getHorizontalTextPosition()();
					switch(position) { 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::LEFT :	return $wnd.HorizontalPosition.LEFT; 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::CENTER :	return $wnd.HorizontalPosition.CENTER;
						default : return null; 
					}	
				},
				set : function(aValue) {
					switch (aValue) {
						case $wnd.HorizontalPosition.LEFT:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::LEFT);
							break;
						case $wnd.HorizontalPosition.RIGHT:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::RIGHT);
							break;
						case $wnd.HorizontalPosition.CENTER:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setHorizontalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::CENTER);
							break;
					}
				}
			});
			Object.defineProperty(published, "verticalTextPosition", {
				get : function() {
					var positon = aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::getVerticalTextPosition()();
					switch(position) { 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::TOP :	return $wnd.VerticalPosition.TOP; 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
						case @com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::CENTER :	return $wnd.VerticalPosition.CENTER;
						default : return null;
					} 
				},
				set : function(aValue) {
					switch (aValue) {
						case $wnd.VerticalPosition.TOP:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::TOP);
							break;
						case $wnd.VerticalPosition.BOTTOM:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::BOTTOM);
							break;
						case $wnd.VerticalPosition.CENTER:
							aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::setVerticalTextPosition(I)(@com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel::CENTER);
							break;
					}
				}
			});

			if (aText) {
				published.text = aText;
			} 	
			if (aIcon) {
				published.icon = aIcon;
			}
			if(aIconTextGap){
				published.iconTextGap = aIconTextGap;
			}
			return published;
		};		
		
		// **************************************************************************
		$wnd.Button = function (aText, aIcon, aGapOrCallback, aCallback) {
			
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;
			
			var aComponent = arguments.length > 4 ? arguments[4] : null;
			
			if (!(this instanceof $wnd.Button)) {
				throw  ' use  "new Button()" !';
			}

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.button.TextButton::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishButtonProperties(published);
			
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
		};	
		
		// **************************************************************************
		$wnd.DropDownButton = function (aText, aIcon, aGapOrCallback, aCallback) {			
			if (!(this instanceof $wnd.DropDownButton)) {
				throw  ' use  "new Button()" !';
			}
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;
			var aComponent = arguments.length > 4 ? arguments[4] : null;

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.button.SplitButton::new()();
			published.unwrap = function() {
				return aComponent;
			};
			Object.defineProperty(published, "dropDownMenu", {
				get : function(){
					var menu = aComponent.@com.sencha.gxt.widget.core.client.button.SplitButton::getMenu()();
					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(menu);
				}, 
				set : function(aValue){
					aComponent.@com.sencha.gxt.widget.core.client.button.SplitButton::setMenu(Lcom/sencha/gxt/widget/core/client/menu/Menu;)(aValue.unwrap());
				}
			});
			publishComponentProperties(published);
			publishButtonProperties(published);
			
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
		};	
		
		// **************************************************************************
		$wnd.ToggleButton = function (aText, aIcon, aSelected, aGapOrCallback, aCallback) {
			
			var aIconTextGap = 4;
			if(!aCallback && aGapOrCallback && aGapOrCallback.call)
				aCallback = aGapOrCallback;

			var aComponent = arguments.length>5?arguments[5]:null;
			
			if (!(this instanceof $wnd.ToggleButton)) {
				throw  ' use  "new ToggleButton()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusToggleButton::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishButtonProperties(published);
			publishButtonGroup(published);

			Object.defineProperty(published, "selected", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusToggleButton::getPlainValue()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusToggleButton::setPlainValue(Z)(aValue!=null?aValue:false);
				}
			});
			
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
			return published;
		};	
		
		// **************************************************************************
		$wnd.RadioButton = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.RadioButton)) {
				throw  ' use  "new RadioButton()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::new(Lcom/sencha/gxt/cell/core/client/form/CheckBoxCell;)(@com.sencha.gxt.cell.core.client.form.RadioCell::new()());
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishCheckBoxProperties(published);
			publishButtonGroup(published);

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
		};	
		
		// **************************************************************************
		$wnd.CheckBox = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.CheckBox)) {
				throw  ' use  "new CheckBox()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishCheckBoxProperties(published);

			if (aText) {
				published.text = aText;
			} 	
			if (aSelected != undefined) {
				published.selected = aSelected;
			}
			if(aCallback){
				published.onActionPerformed = aCallback; 
			}
			return published;
		};			
		
		// **************************************************************************
		$wnd.PasswordField = function (aText) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.PasswordField)) {
				throw  ' use  "new PasswordField()" !';
			}

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.form.PasswordField::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishEditorError(published);

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.sencha.gxt.widget.core.client.form.PasswordField::getText()();
				},
				set : function(aValue) {
					aComponent.@com.sencha.gxt.widget.core.client.form.PasswordField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			if (aText) {
				published.text = aText;
			} 	
			return published;
		};	
		
		// **************************************************************************
		$wnd.TextField = function (aText) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.TextField)) {
				throw  ' use  "new TextField()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishEditorError(published);

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::getText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});

			if (aText) {
				published.text = aText;
			} 	
			return published;
		};	

		// **************************************************************************
		$wnd.FormattedField = function (aValue) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.FormattedField)) {
				throw  ' use  "new FormattedField()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::new(Lcom/eas/client/gxtcontrols/wrappers/component/ObjectFormat;)(null);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishEditorError(published);

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::getText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});

			// FormattedField is plain non-model control.
			// But it has value property as an only case.
			// In other cases only model-controls have value property
			Object.defineProperty(published, "value", {
				get : function() {
					return $wnd.boxAsJs(aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::getJsValue()());
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
				}
			});
			Object.defineProperty(published, "format", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::getFormat()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField::setFormat(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			if (aValue) {
				published.value = aValue;
			} 	
			return published;
		};			
		
		// **************************************************************************
		$wnd.TextArea = function (aText) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.TextArea)) {
				throw  ' use  "new TextArea()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishEditorError(published);
		
			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::getText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			
			if (aText) {
				published.text = aText;
			} 	
			return published;
		}
		
		// **************************************************************************
		$wnd.HtmlArea = function (aText) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.HtmlArea)) {
				throw  ' use  "new HtmlArea()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
		
			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor::getValue()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor::setValue(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			
			if (aText) {
				published.text = aText;
			} 	
			return published;
		}
		
		// **************************************************************************
		$wnd.Slider = function () {
			var aOrientation = arguments.length == 1 || arguments.length == 4?arguments[0]:$wnd.Orientation.HORIZONTAL;
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
			var aComponent = arguments.length>4?arguments[4]:null;
			
			if (!(this instanceof $wnd.Slider)) {
				throw  ' use  "new Slider()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::new(Z)(aOrientation == $wnd.Orientation.VERTICAL);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			Object.defineProperty(published, "maximum", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::getMaxValue()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::setMaxValue(I)(aValue);
				}
			});
			Object.defineProperty(published, "minimum", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::getMinValue()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::setMinValue(I)(aValue);
				}
			});
			Object.defineProperty(published, "value", {
				get : function() {
					var value = aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::getValue()();
					return (value == null ? 0 :	value.@java.lang.Integer::intValue()());
				},
				set : function(aValue) {
					var value = @java.lang.Integer::new(Ljava/lang/String;)(''+aValue);
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider::setValue(Ljava/lang/Integer;)(value);
				}
			});
			
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
		$wnd.ProgressBar = function (aMinimum, aMaximum) {
			var aComponent = arguments.length>2?arguments[2]:null;
			
			if (!(this instanceof $wnd.ProgressBar)) {
				throw  ' use  "new ProgressBar()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			Object.defineProperty(published, "value", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::getIntValue()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::updateProgress(ILjava/lang/String;)(aValue, text);
				}
			});
			Object.defineProperty(published, "minimum", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::getMinimum()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::setMinimum(I)(aValue);
				}
			});
			Object.defineProperty(published, "maximum", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::getMaximum()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::setMaximum(I)(aValue);
				}
			});
			var text = null;
			Object.defineProperty(published, "text", {
				get : function() {
					return text;
				},
				set : function(aValue) {
					text = aValue != null ? ""+aValue : null;
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar::updateText(Ljava/lang/String;)(text);
				}
			});
			if (aMinimum) {
				published.minimum = aMinimum; 
			} 	
			if (aMaximum) {
				published.maximum = aMaximum; 
			} 	
			return published;
		}
		

		// **************************************************************************
		$wnd.MenuItem = function (aText, aIcon, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.MenuItem)) {
				throw  ' use  "new MenuItem()" !';
			}

			var published = this;
			
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.menu.MenuItem::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.sencha.gxt.widget.core.client.menu.MenuItem::getText()();
				},
				set : function(aValue) {
					aComponent.@com.sencha.gxt.widget.core.client.menu.MenuItem::setText(Ljava/lang/String;)(aValue);
				}
            });
			Object.defineProperty(published, "icon", {
				get : function() {
					aComponent.@com.sencha.gxt.widget.core.client.menu.MenuItem::getIcon()();
				},
				set : function(aValue) {
					var setterCallback = function()
					{
						aComponent.@com.sencha.gxt.widget.core.client.menu.MenuItem::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
					}
					if(aValue != null)
						aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
					setterCallback();
				}
			});
			
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
		};	
		
		// **************************************************************************
		$wnd.CheckMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.CheckMenuItem)) {
				throw  ' use  "new CheckMenuItem()" !';
			}

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.menu.CheckMenuItem::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishMenuCheckItemProperties(published);

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

		// **************************************************************************
		$wnd.RadioMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length>3?arguments[3]:null;
			
			if (!(this instanceof $wnd.RadioMenuItem)) {
				throw  ' use  "new RadioMenuItem()" !';
			}

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.menu.CheckMenuItem::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishMenuCheckItemProperties(published);
			publishButtonGroup(published);
			
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
		
		// **************************************************************************
		$wnd.MenuSeparator = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.MenuSeparator)) {
				throw  ' use  "new MenuSeparator()" !';
			}

			var published = this;
			aComponent = aComponent || @com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			return published;
		}
		
		// ***************************************************
		$wnd.DesktopPane = function() {
			var aComponent = arguments.length>0?arguments[0]:null;
			if (!(this instanceof $wnd.DesktopPane)) {
				throw  ' use  "new DesktopPane()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "forms", {
				get:function(){
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::getForms()();
				} 
			});
			published.closeAll = function()
			{
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::closeAll()();				
			}
			published.minimizeAll = function()
			{
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::minimizeAll()();				
			}
			published.maximizeAll = function()
			{
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::maximizeAll()();				
			}
			published.restoreAll = function()
			{
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer::restoreAll()();				
			}

			return published;
		};

		function publishComponentProperties(aPublished) {
			@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(aPublished);
		}
		
		function publishEditorError(aPublished) {
			@com.eas.client.form.api.JSControls::publishEditorError(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(aPublished);
		}
		
		// ******************************************************************
		function publishButtonProperties(aPublished) {
			var comp = aPublished.unwrap();
			
			Object.defineProperty(aPublished, "icon", {
				get : function() {
					return comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::getIcon()();
				},
				set : function(aValue) {
					var setterCallback = function()
					{
						comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
					}
					if(aValue != null)
						aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
					setterCallback();
				}
			});
			Object.defineProperty(aPublished, "text", {
				get : function() {
					return comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::getText()();
				},
				set : function(aValue) {
					comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setText(Ljava/lang/String;)(aValue!=null?(''+aValue):null);
				}
			});
			Object.defineProperty(aPublished, "horizontalTextPosition", {
				get : function() {
					var position = comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::getIconAlign()(); 
					switch (position) {
						case @com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::RIGHT : return $wnd.HorizontalPosition.LEFT; 
						case @com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::LEFT : return $wnd.HorizontalPosition.RIGHT;
						default : return  $wnd.HorizontalPosition.CENTER;
					}
				},
				set : function(aValue) {
					switch (aValue) {
						case $wnd.HorizontalPosition.LEFT:
							comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setIconAlign(Lcom/sencha/gxt/cell/core/client/ButtonCell$IconAlign;)(@com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::RIGHT);
						  	break;
						case $wnd.HorizontalPosition.RIGHT:
							comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setIconAlign(Lcom/sencha/gxt/cell/core/client/ButtonCell$IconAlign;)(@com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::LEFT);
							break;
					}	
				}
			});
			Object.defineProperty(aPublished, "verticalTextPosition", {
				get : function() {
					var position = comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::getIconAlign()(); 
					switch (position) {
						case @com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::TOP : return $wnd.VerticalPosition.BOTTOM; 
						case @com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::BOTTOM : return $wnd.VerticalPosition.TOP;
						default : return  $wnd.VerticalPosition.CENTER;
					}
				},
				set : function(aValue) {
					switch (aValue) {
						case $wnd.VerticalPosition.TOP:
							comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setIconAlign(Lcom/sencha/gxt/cell/core/client/ButtonCell$IconAlign;)(@com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::BOTTOM);
							break;
						case $wnd.VerticalPosition.BOTTOM:
							comp.@com.sencha.gxt.widget.core.client.button.CellButtonBase::setIconAlign(Lcom/sencha/gxt/cell/core/client/ButtonCell$IconAlign;)(@com.sencha.gxt.cell.core.client.ButtonCell.IconAlign::TOP);
							break;
					}
				}
			});
		};
		
		// ******************************************************************
		function publishButtonGroup(aPiblished)
		{
		}
					
		// ******************************************************************
		function publishCheckBoxProperties(aPublished) {
			var comp = aPublished.unwrap();
		
			Object.defineProperty(aPublished, "text", {
				get : function() {
					return comp.@com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::getBoxLabel()();
				},
				set : function(aValue) {
					comp.@com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::setBoxLabel(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			Object.defineProperty(aPublished, "selected", {
				get : function() {
					var value = comp.@com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::getValue()();
					if (value == null)
						return null;
					else
						return comp.@com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::getPlainValue()();
				},
				set : function(aValue) {
					comp.@com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox::setPlainValue(Z)(aValue!=null && (false != aValue));
				}
			});
		}
		
		// ******************************************************************
		function publishMenuCheckItemProperties(aPublished) {
			var comp = aPublished.unwrap();
	
			Object.defineProperty(aPublished, "text", {
				get : function() {
					return comp.@com.sencha.gxt.widget.core.client.menu.CheckMenuItem::getText()();
				},
				set : function(aValue) {
					comp.@com.sencha.gxt.widget.core.client.menu.CheckMenuItem::setText(Ljava/lang/String;)(aValue);
				}
			});
			Object.defineProperty(aPublished, "selected", {
				get : function() {
					return comp.@com.sencha.gxt.widget.core.client.menu.CheckMenuItem::isChecked()();
				},
				set : function(aValue) {
					comp.@com.sencha.gxt.widget.core.client.menu.CheckMenuItem::setChecked(Z)(aValue!=null && (false != aValue));
				}
			});
		}
	}-*/;

	public native static void publishEditorError(PublishedComponent aPublished)/*-{
		var aField = aPublished.unwrap();
		@com.eas.client.form.api.JSControls::publishEditorError(Lcom/sencha/gxt/widget/core/client/form/Field;Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(aField, aPublished);		
	}-*/;
	
	public native static void publishEditorError(Field aField, PublishedComponent aPublished)/*-{
		var errorMessage = null;
		Object.defineProperty(aPublished, "error", {
			get : function() {
				return errorMessage;
			},
			set : function(aValue) {
				errorMessage = aValue;
				if(aValue != null)
					aField.@com.sencha.gxt.widget.core.client.form.Field::forceInvalid(Ljava/lang/String;)(''+aValue);
				else
					aField.@com.sencha.gxt.widget.core.client.form.Field::clearInvalid()();
			}
		});			
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
			    return comp.@com.sencha.gxt.widget.core.client.Component::isVisible()();
		    },
		    set : function(aValue) {
			    comp.@com.sencha.gxt.widget.core.client.Component::setVisible(Z)(aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "enabled", {
		    get : function() {
			    return comp.@com.sencha.gxt.widget.core.client.Component::isEnabled()();
		    },
		    set : function(aValue) {
			    comp.@com.sencha.gxt.widget.core.client.Component::setEnabled(Z)(aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "toolTipText", {
		    get : function() {
			    return comp.@com.sencha.gxt.widget.core.client.Component::getTitle()();
		    },
		    set : function(aValue) {
			    comp.@com.sencha.gxt.widget.core.client.Component::setTitle(Ljava/lang/String;)(aValue != null ? aValue+"" : null);
		    }
 	    });
	    Object.defineProperty(aPublished, "background", {
		    get : function() {
		    	if(_background == null) {
		    		var style = $wnd.getElementComputedStyle(comp.@com.sencha.gxt.widget.core.client.Component::getElement()());
		    		return @com.eas.client.gxtcontrols.ControlsUtils::parseColor(Ljava/lang/String;)(style.backgroundColor);
		    	}
		    	return _background;
		    },
		    set : function(aValue) {
		    	_background = aValue;
		    	//apply
		    	@com.eas.client.gxtcontrols.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(comp, _background!=null?_background.toStyled():""); 
		    }
 	    });
	    Object.defineProperty(aPublished, "backgroundSet", {get : function(){return _background != null;}});
	    Object.defineProperty(aPublished, "foreground", {
		    get : function() {
		    	if(_foreground == null)
		    	{
		    		var style = $wnd.getElementComputedStyle(comp.@com.sencha.gxt.widget.core.client.Component::getElement()());
		    		return @com.eas.client.gxtcontrols.ControlsUtils::parseColor(Ljava/lang/String;)(style.color);
		    	}
		    	return _foreground;
		    },
		    set : function(aValue) {
		    	_foreground = aValue;
		    	// apply
		    	@com.eas.client.gxtcontrols.ControlsUtils::applyForeground(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/client/gxtcontrols/published/PublishedColor;)(comp, _foreground); 
		    }
 	    });
	    Object.defineProperty(aPublished, "foregroundSet", {get : function(){return _foreground != null;}});
	    Object.defineProperty(aPublished, "opaque", {
		    get : function() {
		    	return _opaque;
		    },
		    set : function(aValue) {
		    	_opaque = (false != aValue);
		    	// apply
	    		@com.eas.client.gxtcontrols.ControlsUtils::applyBackground(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(comp, _opaque?(_background!=null?_background.toStyled():""):"transparent");
		    }
 	    });
	    Object.defineProperty(aPublished, "font", {
		    get : function() {
		    	if(_font == null)
		    	{
		    		var style = $wnd.getElementComputedStyle(comp.@com.sencha.gxt.widget.core.client.Component::getElement()());
		    		var isItalic = style.fontStyle == "italic";
		    		var isBold = style.fontWeight == "bold" || style.fontWeight == "bolder"; 
		    		var platypusFontStyle = $wnd.FontStyle.NORMAL;
		    		if(isItalic)
		    		{
		    			if(isBold)
		    				platypusFontStyle = $wnd.FontStyle.BOLD_ITALIC;
		    			else
		    				platypusFontStyle = $wnd.FontStyle.ITALIC;
		    		}else if(isBold)
		    			platypusFontStyle = $wnd.FontStyle.BOLD;
		    		return new $wnd.Font(style.fontFamily, platypusFontStyle, parseInt(""+style.fontSize));
		    	}
		    	return _font;
		    },
		    set : function(aValue) {
		    	_font = aValue;
		    	// apply
		    	@com.eas.client.gxtcontrols.ControlsUtils::applyFont(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/client/gxtcontrols/published/PublishedFont;)(comp, _font); 
		    }
 	    });
	    Object.defineProperty(aPublished, "fontSet", { get : function(){return _font != null;}});
 	    Object.defineProperty(aPublished, "cursor", {
 	    	get : function(){
 	    		if(_cursor == null)
 	    		{
		    		var style = $wnd.getElementComputedStyle(comp.@com.sencha.gxt.widget.core.client.Component::getElement()());
		    		return style.cursor;
 	    		}
 	    		return _cursor;
 	    	},
 	    	set : function(aValue)
 	    	{
 	    		_cursor = aValue; 
 	    		// apply	
		    	@com.eas.client.gxtcontrols.ControlsUtils::applyCursor(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(comp, _cursor); 
 	    	}
 	    	
 	    });
	    Object.defineProperty(aPublished, "cursorSet", { get : function(){return _cursor != null;}});
	     Object.defineProperty(aPublished, "left", {
		    get : function() {
    			return @com.eas.client.gxtcontrols.Sizer::getWidgetLeft(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.AbsolutePane || aPublished.parent instanceof $wnd.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::ajustLeft(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "top", {
		    get : function() {
    			return @com.eas.client.gxtcontrols.Sizer::getWidgetTop(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.AbsolutePane || aPublished.parent instanceof $wnd.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::ajustTop(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    }
 	    });
	    Object.defineProperty(aPublished, "width", {
		    get : function() {
    			return @com.eas.client.gxtcontrols.Sizer::getWidgetWidth(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.AbsolutePane || aPublished.parent instanceof $wnd.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	else if(aPublished.parent instanceof $wnd.ScrollPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	else if(aPublished.parent instanceof $wnd.BoxPane)
		    	{
		    		if(aPublished.parent.orientation == $wnd.Orientation.HORIZONTAL)
		    			aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusHBoxLayoutContainer::ajustWidth(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	}
		    }
 	    });
	    Object.defineProperty(aPublished, "height", {
		    get : function() {
    			return @com.eas.client.gxtcontrols.Sizer::getWidgetHeight(Lcom/google/gwt/user/client/ui/Widget;)(comp);
		    },
		    set : function(aValue) {
		    	if(aPublished.parent instanceof $wnd.AbsolutePane || aPublished.parent instanceof $wnd.AnchorsPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	else if(aPublished.parent instanceof $wnd.ScrollPane)
		    		aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	else if(aPublished.parent instanceof $wnd.BoxPane)
		    	{
		    		if(aPublished.parent.orientation == $wnd.Orientation.VERTICAL)
		    			aPublished.parent.unwrap().@com.eas.client.gxtcontrols.wrappers.container.PlatypusVBoxLayoutContainer::ajustHeight(Lcom/google/gwt/user/client/ui/Widget;I)(aPublished.unwrap(), aValue);
		    	}
		    }
 	    });
	    Object.defineProperty(aPublished, "componentPopupMenu", {
	    	get : function() {
			    var nativeMenu = comp.@com.sencha.gxt.widget.core.client.Component::getData(Ljava/lang/String;)(@com.eas.client.gxtcontrols.ControlsUtils::CONTEXT_MENU);
			    return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(nativeMenu);
	    	},
		    set : function(aValue) {
		    	if (aValue != undefined && aValue != null && aValue.unwrap != undefined) {
			    	comp.@com.sencha.gxt.widget.core.client.Component::setContextMenu(Lcom/sencha/gxt/widget/core/client/menu/Menu;)(aValue.unwrap());
			    	comp.@com.sencha.gxt.widget.core.client.Component::setData(Ljava/lang/String;Ljava/lang/Object;)(@com.eas.client.gxtcontrols.ControlsUtils::CONTEXT_MENU, aValue.unwrap());
		    	} else {
			    	comp.@com.sencha.gxt.widget.core.client.Component::setContextMenu(Lcom/sencha/gxt/widget/core/client/menu/Menu;)(null);
			    	comp.@com.sencha.gxt.widget.core.client.Component::setData(Ljava/lang/String;Ljava/lang/Object;)(@com.eas.client.gxtcontrols.ControlsUtils::CONTEXT_MENU, null);
		    	}
		    }
 	    });
	    Object.defineProperty(aPublished, "parent", {
		    get : function() {
		    	return @com.eas.client.gxtcontrols.GxtControlsFactory::lookupPublishedParent(Lcom/sencha/gxt/widget/core/client/Component;)(comp);
		    }
 	    });
	    Object.defineProperty(aPublished, "name", {
		    get : function() {
		    	return comp.@com.sencha.gxt.widget.core.client.Component::getData(Ljava/lang/String;)(@com.eas.client.form.Form::PID_DATA_KEY);
		    }
 	    });
	    Object.defineProperty(aPublished, "focus", {
		    get : function() {
		    	return function(){comp.@com.sencha.gxt.widget.core.client.Component::focus()();}
		    }
 	    });
        @com.eas.client.gxtcontrols.Publisher::publishExecutor(Lcom/google/gwt/core/client/JavaScriptObject;)(aPublished);
        comp.@com.sencha.gxt.widget.core.client.Component::setData(Ljava/lang/String;Ljava/lang/Object;)(@com.eas.client.form.Form::PUBLISHED_DATA_KEY, aPublished);
 	    // Native API
        Object.defineProperty(aPublished, "element", {
        	get : function() {
    			return comp.@com.sencha.gxt.widget.core.client.Component::getElement()();
        	}
        });
        Object.defineProperty(aPublished, "component", {
        	get : function() {
    			return null;
        	}
        });
	}-*/;
}

package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class JsUi {
	
	private static JavaScriptObject Orientation;
	private static JavaScriptObject VerticalPosition;
	private static JavaScriptObject HorizontalPosition;
	private static JavaScriptObject FontStyle;
	private static JavaScriptObject ScrollBarPolicy;
	private static JavaScriptObject Color;	
	private static JavaScriptObject Font;	
	
	public native static void init()/*-{
			
		@com.eas.ui.JsUi::Orientation = {HORIZONTAL: 0, VERTICAL: 1}; 
		@com.eas.ui.JsUi::VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3}; 
		@com.eas.ui.JsUi::HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4}; 
		@com.eas.ui.JsUi::FontStyle = {NORMAL: 0, BOLD: 1, ITALIC: 2, BOLD_ITALIC: 3}; 
		@com.eas.ui.JsUi::ScrollBarPolicy = {ALLWAYS: 32, NEVER: 31, AUTO: 30}; 
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.predefine.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
				Object.defineProperty(this, "child", {
					get : function(){
						var comp;
						if(isAdd)
							comp = aEvent.@com.eas.ui.events.AddEvent::getWidget()();
						else
							comp = aEvent.@com.eas.ui.events.RemoveEvent::getWidget()();
						return @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
						var jsSource = @com.eas.predefine.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			return ActionEvent;
		});
		
		predefine([], 'forms/anchors', function(){
			function Anchors(aLeft, aWidth, aRight, aTop, aHeight, aBottom) {
				function marginToString (aMargin) {
					if (aMargin != undefined && aMargin != null) {
						var unit = aMargin.@com.eas.ui.MarginConstraints.Margin::unit;
						return aMargin.@com.eas.ui.MarginConstraints.Margin::value + unit.@com.google.gwt.dom.client.Style.Unit::getType()();
					}
					return null;
				}
				
				if (!(this instanceof Anchors)) {
					throw  ' use  "new Anchors(...)" !';
				}
				var aConstraints = arguments.length>6?arguments[6]:null;
				if(!aConstraints){
					aConstraints = @com.eas.ui.MarginConstraints::new()();
				}
				var published = this; 
				published.unwrap = function() {
					return aConstraints;
				};
				
				Object.defineProperty(published, "left", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getLeft()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setLeft(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "width", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getWidth()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setWidth(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "right", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getRight()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setRight(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "top", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getTop()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setTop(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "height", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getHeight()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setHeight(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
						}
					}
				});
				Object.defineProperty(published, "bottom", {
					get : function() {
						return marginToString(aConstraints.@com.eas.ui.MarginConstraints::getBottom()());
					},
					set : function(aValue) {
						if(aValue != null) {
							var margin = @com.eas.ui.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.ui.MarginConstraints::setBottom(Lcom/eas/ui/MarginConstraints$Margin;)(margin);
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
		
		predefine([], 'common-utils/cursor', function(){
			return {
			    CROSSHAIR : "crosshair",
			    DEFAULT : "default",
			    AUTO : "auto",
			    E_RESIZE : "e-resize",
			    // help ?
			    // progress ?
			    HAND : "pointer",
			    MOVE : "move",
			    NE_RESIZE : "ne-resize",
			    NW_RESIZE : "nw-resize",
			    N_RESIZE : "n-resize",
			    SE_RESIZE : "se-resize",
			    SW_RESIZE : "sw-resize",
			    S_RESIZE : "s-resize",
			    TEXT : "text",
			    WAIT : "wait",
			    W_RESIZE : "w-resize"
			};
		});
		
		predefine([], 'common-utils/font', function(){
			function Font(aFamily, aStyle, aSize){
				var _self = this;
				Object.defineProperty(_self, "family", { get:function(){ return aFamily;} });
				Object.defineProperty(_self, "style", { get:function(){ return aStyle;} });
				Object.defineProperty(_self, "size", { get:function(){ return aSize;} });			
			}; 
			@com.eas.ui.JsUi::Font = Font; 
			return Font;
		});

		predefine([], 'common-utils/color', function(){
			function Color(aRed, aGreen, aBlue, aAlpha){
				var _red = 0, _green = 0, _blue = 0, _alpha = 0xff;
				if(arguments.length == 1){
					var _color = @com.eas.ui.PublishedColor::parse(Ljava/lang/String;)(aRed + '');
					if(_color){
						_red = _color.red;
						_green = _color.green;
						_blue = _color.blue;
					}else
						throw "String like \"#cfcfcf\" is expected.";
				}else if(arguments.length >= 3){
					if(aRed)
						_red = aRed;
					if(aGreen)
						_green = aGreen;
					if(aBlue)
						_blue = aBlue;
					if(aAlpha)
						_alpha = aAlpha;
				}else{
					throw "String like \"#cfcfcf\" or three color components with optional alpha is expected.";
				}
				var _self = this;
				Object.defineProperty(_self, "red", { get:function(){ return _red;} });
				Object.defineProperty(_self, "green", { get:function(){ return _green;} });
				Object.defineProperty(_self, "blue", { get:function(){ return _blue;} });
				Object.defineProperty(_self, "alpha", { get:function(){ return _alpha;} });
				_self.toStyled = function(){
					return "rgba("+_self.red+","+_self.green+","+_self.blue+","+_self.alpha/255.0+")"; 
				}
				_self.toString = function(){
					var sred = (new Number(_self.red)).toString(16);
					if(sred.length == 1)
						sred = "0"+sred;
					var sgreen = (new Number(_self.green)).toString(16);
					if(sgreen.length == 1)
						sgreen = "0"+sgreen;
					var sblue = (new Number(_self.blue)).toString(16);
					if(sblue.length == 1)
						sblue = "0"+sblue;
					return "#"+sred+sgreen+sblue;
				}
			}; 
			Color.black = new Color(0,0,0);
			Color.BLACK = new Color(0,0,0);
			Color.blue = new Color(0,0,0xff);
			Color.BLUE = new Color(0,0,0xff);
			Color.cyan = new Color(0,0xff,0xff);
			Color.CYAN = new Color(0,0xff,0xff);
			Color.DARK_GRAY = new Color(0x40, 0x40, 0x40);
			Color.darkGray = new Color(0x40, 0x40, 0x40);
			Color.gray = new Color(0x80, 0x80, 0x80);
			Color.GRAY = new Color(0x80, 0x80, 0x80);
			Color.green = new Color(0, 0xff, 0);
			Color.GREEN = new Color(0, 0xff, 0);
			Color.LIGHT_GRAY = new Color(0xC0, 0xC0, 0xC0);
			Color.lightGray = new Color(0xC0, 0xC0, 0xC0);
			Color.magenta = new Color(0xff, 0, 0xff);
			Color.MAGENTA = new Color(0xff, 0, 0xff);
			Color.orange = new Color(0xff, 0xC8, 0);
			Color.ORANGE = new Color(0xff, 0xC8, 0);
			Color.pink = new Color(0xFF, 0xAF, 0xAF);
			Color.PINK = new Color(0xFF, 0xAF, 0xAF);
			Color.red = new Color(0xFF, 0, 0);
			Color.RED = new Color(0xFF, 0, 0);
			Color.white = new Color(0xFF, 0xff, 0xff);
			Color.WHITE = new Color(0xFF, 0xff, 0xff);
			Color.yellow = new Color(0xFF, 0xff, 0);
			Color.YELLOW = new Color(0xFF, 0xff, 0);
			@com.eas.ui.JsUi::Color = Color; 
			return Color;
		});
	}-*/;
}

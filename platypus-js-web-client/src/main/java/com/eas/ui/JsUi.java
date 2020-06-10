package com.eas.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.core.Utils;
import com.eas.core.XElement;
import com.eas.form.PlatypusWindow;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import java.util.List;

public class JsUi {

    private static JavaScriptObject Orientation;
    private static JavaScriptObject VerticalPosition;
    private static JavaScriptObject HorizontalPosition;
    private static JavaScriptObject FontStyle;
    private static JavaScriptObject ScrollBarPolicy;
    private static JavaScriptObject Color;
    private static JavaScriptObject Font;

    public static void jsSelectFile(final JavaScriptObject aCallback, final String aFileTypes) {
        if (aCallback != null) {
            selectFile(new Callback<JavaScriptObject, String>() {

                @Override
                public void onSuccess(JavaScriptObject result) {
                    try {
                        Utils.executeScriptEventVoid(aCallback, aCallback, result);
                    } catch (Exception ex) {
                        Logger.getLogger(JsUi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void onFailure(String reason) {
                }

            }, aFileTypes);
        }
    }

    public static void selectFile(final Callback<JavaScriptObject, String> aCallback, String aFileTypes) {
        final FileUpload fu = new FileUpload();
        fu.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        fu.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        fu.setWidth("10px");
        fu.setHeight("10px");
        fu.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Utils.JsObject jsFu = fu.getElement().cast();
                JavaScriptObject oFiles = jsFu.getJs("files");
                if (oFiles != null) {
                    JsArray<JavaScriptObject> jsFiles = oFiles.cast();
                    for (int i = 0; i < jsFiles.length(); i++) {
                        try {
                            aCallback.onSuccess(jsFiles.get(i));
                        } catch (Exception ex) {
                            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                fu.removeFromParent();
            }
        });
        List<PlatypusWindow> modalForms = PlatypusWindow.getShownModalForms();
        if (modalForms.size() > 0) {
            int i = modalForms.size() - 1;
            for (; i >= 0; i--) {
                PlatypusWindow f = modalForms.get(i);
                if (f.getView() instanceof HasWidgets) {
                    ((HasWidgets) f.getView()).add(fu);
                    break;
                }
            }
            if (i < 0) {
                RootPanel.get().add(fu);
            }
        } else {
            RootPanel.get().add(fu);
        }
        fu.click();
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                fu.removeFromParent();
                return false;
            }
        }, 1000 * 60 * 1);// 1 min
    }

    public static void jsSelectColor(String aOldValue, final JavaScriptObject aCallback) {
        if (aCallback != null) {
            selectColor(aOldValue, new Callback<String, String>() {

                @Override
                public void onSuccess(String result) {
                    try {
                        Utils.executeScriptEventVoid(aCallback, aCallback, result);
                    } catch (Exception ex) {
                        Logger.getLogger(JsUi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void onFailure(String reason) {
                }

            });
        }
    }

    public static void selectColor(String aOldValue, final Callback<String, String> aCallback) {
        final TextBox tb = new TextBox();
        tb.getElement().setAttribute("type", "color");
        tb.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        tb.setWidth("10px");
        tb.setHeight("10px");
        tb.setValue(aOldValue);
        tb.getElement().getStyle().setLeft(-100, Style.Unit.PX);
        tb.getElement().getStyle().setTop(-100, Style.Unit.PX);

        tb.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                try {
                    aCallback.onSuccess(tb.getValue());
                } finally {
                    tb.removeFromParent();
                }
            }

        });
        RootPanel.get().add(tb, -100, -100);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                tb.setFocus(true);
                tb.getElement().<XElement>cast().click();
                Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        tb.removeFromParent();
                        return false;
                    }
                }, 1000 * 60 * 1);// 1 min
            }
        });
    }

    public native static void init()/*-{
			
		@com.eas.ui.JsUi::Orientation = {HORIZONTAL: 0, VERTICAL: 1}; 
		@com.eas.ui.JsUi::VerticalPosition = {CENTER: 0, TOP: 1, BOTTOM: 3}; 
		@com.eas.ui.JsUi::HorizontalPosition = {CENTER: 0, LEFT: 2, RIGHT: 4}; 
		@com.eas.ui.JsUi::FontStyle = {NORMAL: 0, BOLD: 1, ITALIC: 2, BOLD_ITALIC: 3}; 
		@com.eas.ui.JsUi::ScrollBarPolicy = {ALLWAYS: 32, NEVER: 31, AUTO: 30}; 
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
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
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('CellRenderEvent', CellRenderEvent);
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
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('WindowEvent', WindowEvent);
			return WindowEvent;
		});
				
		predefine([], 'forms/mouse-event', function(){
			function MouseEvent(aEvent, aClickCount){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('MouseEvent', MouseEvent);
			return MouseEvent;
		});
		
		predefine([], 'forms/key-event', function(){
			function KeyEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('KeyEvent', KeyEvent);
			return KeyEvent;
		});
		
		predefine([], 'forms/container-event', function(){
			function ContainerEvent(aEvent, isAdd){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
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
						return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
					}
				});
			}
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ContainerEvent', ContainerEvent);
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
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ItemEvent', ItemEvent);
			return ItemEvent;
		});
		
		predefine([], 'forms/component-event', function(){
			function ComponentEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ComponentEvent', ComponentEvent);
			return ComponentEvent;
		});
		
		predefine([], 'forms/focus-event', function(){
			function FocusEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('FocusEvent', FocusEvent);
			return FocusEvent;
		});
		
		predefine([], 'forms/action-event', function(){
			function ActionEvent(aEvent){
				Object.defineProperty(this, "source", {
					get : function() {
						var source = aEvent.@com.google.web.bindery.event.shared.Event::getSource()();
						var jsSource = @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(source);
						return jsSource;
					}
				});
			}
			@com.eas.ui.EventsPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ActionEvent', ActionEvent);
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
			var FontStyle = @com.eas.ui.JsUi::FontStyle;
			function Font(aFamily, aStyle, aSize){
				var _self = this;
				Object.defineProperty(_self, "family", { get:function(){ return aFamily;} });
				Object.defineProperty(_self, "style", { get:function(){ return aStyle;} });
				Object.defineProperty(_self, "size", { get:function(){ return aSize;} });			
			}; 
			Font.prototype.toString = function(){
				return this.family + ' ' + (this.style == FontStyle.ITALIC ? 'Italic' : this.style == FontStyle.BOLD ? 'Bold' : this.style == FontStyle.BOLD_ITALIC ? 'Bold Italic' : 'Normal') + ' ' + this.size;
			}
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
					if(arguments.length > 3)
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
		
		predefine(['boxing', 'common-utils/color', 'common-utils/cursor', 'common-utils/font'], 'ui', function(B, Color, Cursor, Font){
			var Orientation = @com.eas.ui.JsUi::Orientation;
			var VerticalPosition = @com.eas.ui.JsUi::VerticalPosition;
			var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;
			var FontStyle = @com.eas.ui.JsUi::FontStyle;
			var ScrollBarPolicy = @com.eas.ui.JsUi::ScrollBarPolicy;
			
			function selectFile(aCallback, aFileFilter) {
				@com.eas.ui.JsUi::jsSelectFile(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(aCallback, aFileFilter);
			}
			
			function selectColor(aCallback, aOldValue) {
				@com.eas.ui.JsUi::jsSelectColor(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aOldValue != null ? aOldValue + '' : '', aCallback);
			}
			
			function Icon() {
			}
			Object.defineProperty(Icon, "load", { 
				value: function(aIconName, aOnSuccess, aOnFailure) {
					@com.eas.ui.PlatypusImageResource::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aIconName != null ? '' + aIconName : null, aOnSuccess, aOnFailure);
				} 
			});
			
			function readWidgetElement(widgetRootElement, aModel){
				var uiReader = @com.eas.ui.DefaultUiReader::new(Lcom/google/gwt/xml/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;)(widgetRootElement, aModel);
				uiReader.@com.eas.ui.DefaultUiReader::parse()();
				var nwList = uiReader.@com.eas.ui.DefaultUiReader::getWidgetsList()();
				var target = {};
				for(var i = 0; i < nwList.@java.util.List::size()(); i++){
					var nWidget = nwList.@java.util.List::get(I)(i);
					var pWidget = nWidget.@com.eas.core.HasPublished::getPublished()();
					if(pWidget.name){
						target[pWidget.name] = pWidget;
					}
				}
				return target;
			}
			
			function loadWidgets(aModuleName, aModel){
				var aClient = @com.eas.client.AppClient::getInstance()();
				var layoutDoc = aClient.@com.eas.client.AppClient::getFormDocument(Ljava/lang/String;)(aModuleName);
				if(layoutDoc){
					var rootElement = layoutDoc.@com.google.gwt.xml.client.Document::getDocumentElement()();
					var widgetRootElement = aModuleName ? @com.eas.ui.JsUi::findLayoutElementByBundleName(Lcom/google/gwt/xml/client/Element;Ljava/lang/String;)(rootElement, aModuleName) : rootElement;
					return readWidgetElement(widgetRootElement, aModel);
				} else {
					throw 'UI definition for module "' + aModuleName + '" is not found';
				}
			}
			
			function readWidgets(aContent, aModel){
				var layoutDoc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aContent + "");
				var rootElement = layoutDoc.@com.google.gwt.xml.client.Document::getDocumentElement()();
				return readWidgetElement(rootElement, aModel);
			}
			
			var module = {};
		    Object.defineProperty(module, 'loadWidgets', {
		        enumerable: true,
		        value: loadWidgets
		    });
		    Object.defineProperty(module, 'readWidgets', {
		        enumerable: true,
		        value: readWidgets
		    });
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
		    return module;
		});
	}-*/;

    public static Element findLayoutElementByBundleName(Element aElement, String aBundleName) {
        if (aElement.getTagName().equals("layout")) {
            return aElement;// the high level code had to do everything in the right way
        } else {
            Node child = aElement.getFirstChild();
            while (child != null) {
                if (child instanceof Element) {
                    Element el = (Element) child;
                    if (el.hasAttribute("bundle-name")) {
                        String bundleName = el.getAttribute("bundle-name");
                        if (bundleName.equals(aBundleName)) {
                            return el;
                        }
                    }
                }
                child = child.getNextSibling();
            }
        }
        return null;
    }
}

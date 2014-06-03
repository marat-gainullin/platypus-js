package com.eas.client.form.js;

public class JsContainers {

	public native static void init()/*-{

		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
				
		// ***************************************************
		$wnd.P.BorderPane = function(aVGap, aHGap) {
			var aComponent = arguments.length > 2 ? arguments[2] : null;
			if(!aComponent)
			{
				if(!aVGap)
					aVGap = 0;
				if(!aHGap)
					aHGap = 0;
			}
			if (!(this instanceof $wnd.P.BorderPane)) {
				throw  ' use  "new P.BorderPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.BorderPane::new(II)(aVGap, aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
		};		

		// ***************************************************
		$wnd.P.FlowPane = function(aVGap, aHGap) {
			var aComponent = arguments.length > 2 ? arguments[2] : null;
			if(!aComponent)
			{
				if(!aVGap)
					aVGap = 0;
				if(!aHGap)
					aHGap = 0;
			}
			if (!(this instanceof $wnd.P.FlowPane)) {
				throw  ' use  "new P.FlowPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.FlowPane::new(II)(aVGap,aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
		};
		
		// ***************************************************
		$wnd.P.GridPane = function(aRows, aCols, aVGap, aHGap) {
			var aComponent = arguments.length > 4 ? arguments[4] : null;
			if (!(this instanceof $wnd.P.GridPane)) {
				throw  ' use  "new P.GridPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.GridPane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			
			if(arguments.length <= 4)
			{
				if (aRows == undefined) {
					throw "aRows argument is required!"
				}
				if (aCols == undefined) {
					throw "aCols argument is required!"
				}
				aComponent.@com.eas.client.form.published.containers.GridPane::resize(II)(aRows, aCols);
				if (aVGap) {
					aComponent.@com.eas.client.form.published.containers.GridPane::setVgap(I)(aVGap);
				}
				if (aHGap) {
					aComponent.@com.eas.client.form.published.containers.GridPane::setHgap(I)(aHGap);
				}
			}
		};
		
		// ***************************************************
		$wnd.P.BoxPane = function(aOrientation) {
			if (!(this instanceof $wnd.P.BoxPane)) {
				throw  ' use  "new P.BoxPane()" !';
			}
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if(!aComponent)
			{
				if(!aOrientation)
					aOrientation = $wnd.P.Orientation.HORIZONTAL;
				aComponent = aOrientation == $wnd.P.Orientation.VERTICAL ? @com.eas.client.form.published.containers.VBoxPane::new()() : @com.eas.client.form.published.containers.HBoxPane::new()();
			}

			var published = this;
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			
			published.add = function(toAdd){
				if(toAdd && toAdd.unwrap){
					if (aOrientation == $wnd.P.Orientation.VERTICAL) {
						aComponent.@com.eas.client.form.published.containers.VBoxPane::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					} else { 
						aComponent.@com.eas.client.form.published.containers.HBoxPane::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					}
				}
			}
			
			Object.defineProperty(published, "orientation", {
				get : function()
				{
					return aOrientation;
				}
			});
			
			return published;
		};		

		// ***************************************************
		$wnd.P.CardPane = function(aVGap, aHGap) {
			if (!(this instanceof $wnd.P.CardPane)) {
				throw  ' use  "new P.CardPane()" !';
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
			aComponent = aComponent || @com.eas.client.form.published.containers.CardPane::new(II)(aVGap, aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
		};		

		// ***************************************************
		$wnd.P.TabbedPane = function() {
			var aComponent = arguments.length>0?arguments[0]:null;
			if (!(this instanceof $wnd.P.TabbedPane)) {
				throw  ' use  "new P.TabbedPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.TabbedPane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishChildren(published);
			return published;
		};

		// ***************************************************
		$wnd.P.ScrollPane = function(aChild) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if (!(this instanceof $wnd.P.ScrollPane)) {
				throw  ' use  "new P.ScrollPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.ScrollPane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			if(aChild)
				published.add(aChild);
			return published;
		};		
		
		// ***************************************************
		$wnd.P.SplitPane = function(aOrientation) {
			if (!(this instanceof $wnd.P.SplitPane)) {
				throw  ' use  "new P.SplitPane()" !';
			}
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if(!aComponent)
			{
				if(!aOrientation)
					aOrientation = $wnd.P.Orientation.HORIZONTAL;
				aComponent = @com.eas.client.form.published.containers.SplitPane::new()();
				var orientation = (aOrientation === $wnd.P.Orientation.VERTICAL ? @com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT : @com.eas.client.form.published.containers.SplitPane::HORIZONTAL_SPLIT); 
				aComponent.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(orientation);
			}

			var published = this; 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			return published;
		};		
		
		// ***************************************************
		$wnd.P.ToolBar = function(floatable) {
			if (!(this instanceof $wnd.P.ToolBar)) {
				throw  ' use  "new P.ToolBar()" !';
			}
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			if(!aComponent)
			{
				if(floatable == undefined || floatable == null)
					floatable = false;
				aComponent = @com.eas.client.form.published.containers.ToolBar::new()();
			}

			var published = this;
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			return published;
		};
		
		// ***************************************************
		$wnd.P.AnchorsPane = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.P.AnchorsPane)) {
				throw  ' use  "new P.AnchorsPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.AnchorsPane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			publishChildrenOrdering(published);
		}

		// ***************************************************
		$wnd.P.AbsolutePane = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.P.AbsolutePane)) {
				throw  ' use  "new P.AbsolutePane()" !';
			}
			var published = this; 
			aComponent = aComponent || @com.eas.client.form.published.containers.AbsolutePane::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishIndexedPanel(published);
			publishChildrenOrdering(published);
		}
		
		// **************************************************************************
		$wnd.P.ButtonGroup = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.ButtonGroup)) {
				throw  ' use  "new P.ButtonGroup()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.containers.ButtonGroup::new()();
			published.unwrap = function() {
				return aComponent;
			};			
			aComponent.@com.eas.client.form.published.containers.ButtonGroup::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		}

		// ***************************************************
		$wnd.P.Anchors = function(aLeft, aWidth, aRight, aTop, aHeight, aBottom) {
			function marginToString (aMargin) {
				if (aMargin != undefined && aMargin != null) {
					var unit = aMargin.@com.eas.client.form.MarginConstraints.Margin::unit;
					return aMargin.@com.eas.client.form.MarginConstraints.Margin::value + unit.@com.google.gwt.dom.client.Style.Unit::getType()();
				}
				return null;
			}
			
			if (!(this instanceof $wnd.P.Anchors)) {
				throw  ' use  "new P.Anchors(...)" !';
			}
			var aConstraints = arguments.length>6?arguments[6]:null;
			if(!aConstraints)
			{
				aConstraints = @com.eas.client.form.MarginConstraints::new()();
			}
			var published = this; 
			published.unwrap = function() {
				return aConstraints;
			};
			
			Object.defineProperty(published, "left", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getLeft()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setLeft(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "width", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getWidth()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setWidth(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "right", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getRight()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setRight(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "top", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getTop()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setTop(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "height", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getHeight()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setHeight(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "bottom", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.form.MarginConstraints::getBottom()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.form.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.form.MarginConstraints::setBottom(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
					}
				}
			});
			if (aLeft) {
				published.left = '' + aLeft;
			}
			if (aWidth) {
				published.width = '' + aWidth;
			}
			if (aRight) {
				published.right = '' + aRight;
			}
			if (aTop) {
				published.top = '' + aTop;
			}
			if (aHeight) {
				published.height = '' + aHeight;
			}
			if (aBottom) {
				published.bottom = '' + aBottom;
			}			
			return published;
		} 

		// ***************************************************
		function publishChildrenOrdering(aPublished) {
			if (aPublished) {
				var comp = aPublished.unwrap();
				aPublished.toFront = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.client.form.published.containers.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.client.form.published.containers.HasLayers::toFront(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
		
				aPublished.toBack = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.client.form.published.containers.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.client.form.published.containers.HasLayers::toBack(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
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
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			publishChildren(aPublished);
		}

		function publishChildren(aPublished){
			Object.defineProperty(aPublished, "children", {get : function(){
				var ch = [];
				for(var i=0; i < aPublished.count; i++)
					ch[ch.length] = aPublished.child(i);
				return ch;
			}});
		}		
	}-*/;
}

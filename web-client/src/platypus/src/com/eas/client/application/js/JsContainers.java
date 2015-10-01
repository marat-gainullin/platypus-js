package com.eas.client.application.js;

public class JsContainers {

	public native static void init()/*-{

		function publishComponentProperties(aPublished) {
			@com.eas.client.application.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
				
		function predefine(aDeps, aName, aDefiner){
			var resolved = [];
			for(var d = 0; d < aDeps.length; d++){
				var module = @com.eas.client.application.Application::prerequire(Ljava/lang/String;)(aDeps[d]);
				resolved.push(module);
			}
			@com.eas.client.application.Application::predefine(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aName, aDefiner(resolved));
		}
		
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
				aComponent = aComponent || @com.eas.client.form.published.containers.BorderPane::new(II)(aVGap, aHGap);
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
				aComponent = aComponent || @com.eas.client.form.published.containers.FlowPane::new(II)(aVGap,aHGap);
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
				aComponent = aComponent || @com.eas.client.form.published.containers.GridPane::new()();
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
					aComponent.@com.eas.client.form.published.containers.GridPane::resize(II)(aRows, aCols);
					if (aVGap) {
						aComponent.@com.eas.client.form.published.containers.GridPane::setVgap(I)(aVGap);
					}
					if (aHGap) {
						aComponent.@com.eas.client.form.published.containers.GridPane::setHgap(I)(aHGap);
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
					aComponent = @com.eas.client.form.published.containers.BoxPane::new(III)(aOrientation, aHGap, aVGap);
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
						aComponent.@com.eas.client.form.published.containers.BoxPane::add(Lcom/google/gwt/user/client/ui/Widget;I)(toAdd.unwrap(), published.orientation == Ui.Orientation.VERTICAL ? toAdd.height : toAdd.width);
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
				aComponent = aComponent || @com.eas.client.form.published.containers.CardPane::new(II)(aVGap, aHGap);
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
				aComponent = aComponent || @com.eas.client.form.published.containers.TabbedPane::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.containers.ScrollPane::new()();
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
					aComponent = @com.eas.client.form.published.containers.SplitPane::new()();
					var orientation = (aOrientation === Ui.Orientation.VERTICAL ? @com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT : @com.eas.client.form.published.containers.SplitPane::HORIZONTAL_SPLIT); 
					aComponent.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(orientation);
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
					aComponent = @com.eas.client.form.published.containers.ToolBar::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.containers.AnchorsPane::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.containers.AbsolutePane::new()();
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
				aComponent = aComponent || @com.eas.client.form.published.containers.ButtonGroup::new()();
				published.unwrap = function() {
					return aComponent;
				};			
				aComponent.@com.eas.client.form.published.containers.ButtonGroup::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return ButtonGroup;
		});

		predefine([], 'forms/anchors', function(){
			function Anchors(aLeft, aWidth, aRight, aTop, aHeight, aBottom) {
				function marginToString (aMargin) {
					if (aMargin != undefined && aMargin != null) {
						var unit = aMargin.@com.eas.client.form.MarginConstraints.Margin::unit;
						return aMargin.@com.eas.client.form.MarginConstraints.Margin::value + unit.@com.google.gwt.dom.client.Style.Unit::getType()();
					}
					return null;
				}
				
				if (!(this instanceof Anchors)) {
					throw  ' use  "new Anchors(...)" !';
				}
				var aConstraints = arguments.length>6?arguments[6]:null;
				if(!aConstraints){
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
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
							var margin = @com.eas.client.form.MarginConstraints.Margin::parse(Ljava/lang/String;)('' + aValue);
							aConstraints.@com.eas.client.form.MarginConstraints::setBottom(Lcom/eas/client/form/MarginConstraints$Margin;)(margin);
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
			Object.defineProperty(aPublished, "children", {
				value : function(){
					var ch = [];
					for(var i=0; i < aPublished.count; i++)
						ch.push(aPublished.child(i));
					return ch;
				}
			});
		}		
	}-*/;
}

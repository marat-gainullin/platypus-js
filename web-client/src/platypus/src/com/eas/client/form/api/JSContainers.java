package com.eas.client.form.api;

public class JSContainers {

	public native static void initContainers()/*-{

		function publishComponentProperties(aPublished) {
			@com.eas.client.form.api.JSControls::publishComponentProperties(Lcom/eas/client/gxtcontrols/published/PublishedComponent;)(aPublished);
		}
				
		// ***************************************************
		$wnd.BorderPane = function(aVGap, aHGap) {
			var aComponent = arguments.length>2?arguments[2]:null;
			if(!aComponent)
			{
				if(!aVGap)
					aVGap = 0;
				if(!aHGap)
					aHGap = 0;
			}
			if (!(this instanceof $wnd.BorderPane)) {
				throw  ' use  "new BorderPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::new(II)(aVGap, aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);

			Object.defineProperty(published, "leftComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::getWestWidget()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					if (aChild != null) {
					 	aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;D)(aChild.unwrap(),"WEST",32);
					}else
						published.remove(published.leftComponent);
				}
			});
			Object.defineProperty(published, "topComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::getNorthWidget()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					if (aChild != null) {
					 	aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;D)(aChild.unwrap(),"NORTH",32);
					}else
						published.remove(published.topComponent);
				}
			});
			Object.defineProperty(published, "rightComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::getEastWidget()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					if (aChild != null) {
					 	aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;D)(aChild.unwrap(),"EAST",32);
					}else
						published.remove(published.rightComponent);
				}
			});
			Object.defineProperty(published, "bottomComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::getSouthWidget()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					if (aChild != null) {
					 	aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;D)(aChild.unwrap(),"SOUTH",32);
					}else
						published.remove(published.bottomComponent);
				}
			});
			Object.defineProperty(published, "centerComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::getCenterWidget()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					if (aChild != null) {
					 	aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;D)(aChild.unwrap(),"CENTER",32);
					}else
						published.remove(published.centerComponent);
				}
			});
			published.add = function(toAdd, region, aSize) {
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined){
					if (aSize == undefined || aSize == null) {
						aSize = 32;
					}
					if(!region)
						region = $wnd.VerticalPosition.CENTER;
					switch (region) {
						case $wnd.VerticalPosition.CENTER:
						 	var place = @com.sencha.gxt.core.client.Style.LayoutRegion::CENTER; 
							aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/sencha/gxt/core/client/Style$LayoutRegion;D)(toAdd.unwrap(),place,0);
							break;  
						case $wnd.VerticalPosition.TOP: 
						 	var place = @com.sencha.gxt.core.client.Style.LayoutRegion::NORTH; 
							aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/sencha/gxt/core/client/Style$LayoutRegion;D)(toAdd.unwrap(),place,aSize);
							break;  
						case $wnd.VerticalPosition.BOTTOM: 
						 	var place = @com.sencha.gxt.core.client.Style.LayoutRegion::SOUTH; 
							aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/sencha/gxt/core/client/Style$LayoutRegion;D)(toAdd.unwrap(),place,aSize);
							break;  
						case $wnd.HorizontalPosition.LEFT: 
						 	var place = @com.sencha.gxt.core.client.Style.LayoutRegion::WEST; 
							aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/sencha/gxt/core/client/Style$LayoutRegion;D)(toAdd.unwrap(),place,aSize);
							break;  
						case $wnd.HorizontalPosition.RIGHT: 
						 	var place = @com.sencha.gxt.core.client.Style.LayoutRegion::EAST; 
							aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/sencha/gxt/core/client/Style$LayoutRegion;D)(toAdd.unwrap(),place,aSize);
							break;  
					}
				}
			}
						
			return published;
		};		

		// ***************************************************
		$wnd.FlowPane = function(aVGap, aHGap) {
			var aComponent = arguments.length>2?arguments[2]:null;
			if(!aComponent)
			{
				if(!aVGap)
					aVGap = 0;
				if(!aHGap)
					aHGap = 0;
			}
			if (!(this instanceof $wnd.FlowPane)) {
				throw  ' use  "new FlowPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusFlowLayoutContainer::new(II)(aVGap,aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			
			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusFlowLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
				}
			}
			
			return published;
		};
		
		// ***************************************************
		$wnd.GridPane = function(aRows, aCols, aVGap, aHGap) {
			var aComponent = arguments.length>4?arguments[4]:null;
			if (!(this instanceof $wnd.GridPane)) {
				throw  ' use  "new GridPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			
			published.add = function(toAdd, aRow, aCol){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					if (aRow != undefined && aRow != null && aCol != undefined && aCol != null) {
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::setWidget(Lcom/google/gwt/user/client/ui/Widget;II)(toAdd.unwrap(),aRow,aCol);
					} else { 
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					}
				}
			}
			published.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
				}
			};
			published.child = function(aIndex, aCol) {
				var widget;
				if (aCol != undefined && aCol != null) {
					widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::getWidget(II)(aIndex, aCol);
				} else {
					widget = aComponent.@com.sencha.gxt.widget.core.client.container.Container::getWidget(I)(aIndex);
				}
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			
			if(arguments.length<=4)
			{
				if (aRows == undefined) {
					throw "aRows argument is required!"
				}
				if (aCols == undefined) {
					throw "aCols argument is required!"
				}
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::setRows(I)(aRows);
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::setColumns(I)(aCols);
				if (aVGap) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::setVGap(I)(aVGap);
				}
				if (aHGap) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer::setHGap(I)(aHGap);
				}
			}
			
			return published;
		};
		
		// ***************************************************
		$wnd.BoxPane = function(aOrientation) {
			if (!(this instanceof $wnd.BoxPane)) {
				throw  ' use  "new BoxPane()" !';
			}
			var aComponent = arguments.length>1?arguments[1]:null;
			if(!aComponent)
			{
				if(!aOrientation)
					aOrientation = $wnd.Orientation.HORIZONTAL;
				aComponent = aOrientation == $wnd.Orientation.VERTICAL ? @com.eas.client.gxtcontrols.wrappers.container.PlatypusVBoxLayoutContainer::new()() : @com.eas.client.gxtcontrols.wrappers.container.PlatypusHBoxLayoutContainer::new()();
			}

			var published = this;
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			
			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					if (aOrientation == $wnd.Orientation.VERTICAL) {
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusVBoxLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					} else { 
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusHBoxLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
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
		$wnd.CardPane = function(aVGap, aHGap) {
			if (!(this instanceof $wnd.CardPane)) {
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
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer::new(II)(aVGap, aHGap);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			
			published.add = function(toAdd, aCardName){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Ljava/lang/String;)(toAdd.unwrap(), aCardName);
				}
			};
			published.child = function(aIndex_or_aCardName) {
				var widget;
				if (isNaN(aIndex_or_aCardName)) {
					widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer::getWidget(Ljava/lang/String;)(aIndex_or_aCardName);
				}else {
					var index = parseInt(aIndex_or_aCardName, 10);
					widget = aComponent.@com.sencha.gxt.widget.core.client.container.Container::getWidget(I)(index);
				}
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			published.show = function(aCardName) {
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer::show(Ljava/lang/String;)(aCardName);
			};
			
			return published;
		};		

		// ***************************************************
		$wnd.TabbedPane = function() {
			var aComponent = arguments.length>0?arguments[0]:null;
			if (!(this instanceof $wnd.TabbedPane)) {
				throw  ' use  "new TabbedPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			//publishContainer(published);
			Object.defineProperty(published, "selectedIndex", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::getSelectedIndex()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::setSelectedIndex(I)(aValue);
				}
			});
			Object.defineProperty(published, "selectedComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::getSelectedComponent()();
					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aValue) {
					if(aValue != null)
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::setSelectedComponent(Lcom/sencha/gxt/widget/core/client/Component;)(aValue.unwrap());
				}
			});
			Object.defineProperty(published, "count", {
				get : function(){
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::getTabsCount()();
				}
			});
			published.add = function(toAdd, aTabTitle, aTabIcon){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					var tabConfig = @com.sencha.gxt.widget.core.client.TabItemConfig::new(Ljava/lang/String;)(aTabTitle);
					if (aTabIcon) {
						tabConfig.@com.sencha.gxt.widget.core.client.TabItemConfig::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aTabIcon);
					}	
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::add(Lcom/sencha/gxt/widget/core/client/Component;Lcom/sencha/gxt/widget/core/client/TabItemConfig;)(toAdd.unwrap(), tabConfig);
				}
			};
			published.remove = function(aChild){
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {				
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::removeTabWidget(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
				}
			};
			published.clear = function() {
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::clear()();
			};
			published.child = function(aIndex){
				var widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer::getTabWidget(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			publishChildren(published);
			return published;
		};

		// ***************************************************
		$wnd.ScrollPane = function(aChild) {
			var aComponent = arguments.length>1?arguments[1]:null;
			if (!(this instanceof $wnd.ScrollPane)) {
				throw  ' use  "new ScrollPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);

			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
				}
			}
			Object.defineProperty(published, "view", {
				get : function()
				{
					var widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer::getView()();
					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
				},
				set : function(aValue)
				{
					if(aValue != null)
						published.add(aValue);
					else
						published.clear();
				}
			});
			
			if(aChild)
				published.add(aChild);
			return published;
		};		
		
		// ***************************************************
		$wnd.SplitPane = function(aOrientation) {
			if (!(this instanceof $wnd.SplitPane)) {
				throw  ' use  "new SplitPane()" !';
			}
			var aComponent = arguments.length>1?arguments[1]:null;
			if(!aComponent)
			{
				if(!aOrientation)
					aOrientation = $wnd.Orientation.HORIZONTAL;
				aComponent = @com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::new()();
				var orientation = (aOrientation === $wnd.Orientation.VERTICAL ? @com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::VERTICAL_SPLIT : @com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::HORIZONTAL_SPLIT); 
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setOrientation(I)(orientation);
			}

			var published = this; 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			
			Object.defineProperty(published, "orientation", {
				get : function() {
					var orientation = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::getOrientation()();
					if (orientation == @com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::VERTICAL_SPLIT) {
						return $wnd.Orientation.VERTICAL;
					} else {
						return $wnd.Orientation.HORIZONTAL;
					}
				},
				set : function(aOrientation) {
					if (aOrientation == $wnd.Orientation.VERTICAL) {
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setOrientation(I)(@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::VERTICAL_SPLIT);
					} else {
						aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setOrientation(I)(@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::HORIZONTAL_SPLIT);
					}
				}
			});
			Object.defineProperty(published, "firstComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::getLeftComponent()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					var child = (aChild == null ? null: aChild.unwrap());
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setLeftComponent(Lcom/sencha/gxt/widget/core/client/Component;)(child);
				}
			});
			Object.defineProperty(published, "secondComponent", {
				get : function() {
					var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::getRightComponent()();
 					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
				},
				set : function(aChild) {
					var child = (aChild == null ? null: aChild.unwrap());
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setRightComponent(Lcom/sencha/gxt/widget/core/client/Component;)(child);
				}
			});
			Object.defineProperty(published, "dividerLocation", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::getDividerLocation()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setDividerLocation(I)(aValue);
				}
			});
			Object.defineProperty(published, "oneTouchExpandable", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::isOneTouchExpandable()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::setOneTouchExpandable(Z)(false != aValue);
				}
			});
			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
					if (published.firstComponent == null) {
						published.firstComponent = toAdd;
					}else {
						published.secondComponent = toAdd;
					}
				}
			}
			published.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::removeChild(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
				}
			};
			published.child = function(aIndex) {
				var widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer::getWidget(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			
			return published;
		};		
		
		// ***************************************************
		$wnd.ToolBar = function(floatable) {
			if (!(this instanceof $wnd.ToolBar)) {
				throw  ' use  "new ToolBar()" !';
			}
			var aComponent = arguments.length>1?arguments[1]:null;
			if(!aComponent)
			{
				if(floatable == undefined || floatable == null)
					floatable = false;
				aComponent = @com.sencha.gxt.widget.core.client.toolbar.ToolBar::new()();
				if (floatable) {
					@com.sencha.gxt.fx.client.Draggable::new(Lcom/google/gwt/user/client/ui/Widget;)(aComponent);
				}
			}

			var published = this;
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);

			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					aComponent.@com.sencha.gxt.widget.core.client.toolbar.ToolBar::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
					aComponent.@com.sencha.gxt.widget.core.client.toolbar.ToolBar::doLayout()();					
				}
			}			
			return published;
		};
		
		// ***************************************************
		$wnd.MenuBar = function() {
			if (!(this instanceof $wnd.MenuBar)) {
				throw  ' use  "new MenuBar()" !';
			}
			var aComponent = arguments.length>0?arguments[0]:null;

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			//publishContainer(published);

			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined)
				{
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
				}
			};
			published.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
				}
			};
			published.clear = function() {
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::clear()();
			};
			published.child = function(aIndex) {
				var comp = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::getMenu(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			};
			Object.defineProperty(published, "count", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar::getWidgetCount()();
				}
			});
			publishChildren(published);
			return published;
		};

		// ***************************************************
		$wnd.Menu = function(aText) {
			if (!(this instanceof $wnd.Menu)) {
				throw  ' use  "new Menu()" !';
			}
			var aComponent = arguments.length>1?arguments[1]:null;
			if(!aComponent)
			{
				aComponent = @com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::new()();
			}
			var published = this;
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			publishMenu(published);

			Object.defineProperty(published, "text", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::getText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			
			if (aText) {
				published.text = aText;
			}
			return published;
		};

		// ***************************************************
		$wnd.PopupMenu = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.PopupMenu)) {
				throw  ' use  "new PopupMenu()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishContainer(published);
			publishMenu(published);
			return published;
		};

		// ***************************************************
		$wnd.AnchorsPane = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.AnchorsPane)) {
				throw  ' use  "new AnchorsPane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
				
			Object.defineProperty(published, "count", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::getCompsCount()();
				}
			});
			published.add = function(toAdd, aConstraints) {
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined && aConstraints != undefined && aConstraints != null) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/client/gxtcontrols/MarginJSConstraints;)(toAdd.unwrap(), aConstraints);
				}
			};
			published.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {				
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::removeComp(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
				}
			};
			published.clear = function() {
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::clear()();
			};
			published.child = function(aIndex) {
				var widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::getComp(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			publishChildren(published);
			publishChildrenOrdering(published);
						
			return published;
		}

		// ***************************************************
		$wnd.AbsolutePane = function() {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			if (!(this instanceof $wnd.AbsolutePane)) {
				throw  ' use  "new AbsolutePane()" !';
			}

			var published = this; 
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
				
			Object.defineProperty(published, "count", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::getCompsCount()();
				}
			});
			published.add = function(toAdd, aConstraints) {
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
					var c = aConstraints != undefined ? aConstraints : null; 
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/client/gxtcontrols/AbsoluteJSConstraints;)(toAdd.unwrap(), c);
				}
			};
			published.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {				
					aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::removeComp(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
				}
			};
			published.clear = function() {
				aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::clear()();
			};
			published.child = function(aIndex) {
				var widget = aComponent.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer::getComp(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			publishChildren(published);
			publishChildrenOrdering(published);
			
			return published;
		}

		// ***************************************************
		$wnd.Anchors = function(aLeft, aWidth, aRight, aTop, aHeight, aBottom) {
			if (!(this instanceof $wnd.Anchors)) {
				throw  ' use  "new Anchors(...)" !';
			}
			var aConstraints = arguments.length>6?arguments[6]:null;
			if(!aConstraints)
			{
				aConstraints = @com.eas.client.gxtcontrols.MarginConstraints::new()();
			}
			var published = this; 
			published.unwrap = function() {
				return aConstraints;
			};
			
			Object.defineProperty(published, "left", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getLeft()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setLeft(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "width", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getWidth()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setWidth(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "right", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getRight()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setRight(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "top", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getTop()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setTop(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "height", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getHeight()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setHeight(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
					}
				}
			});
			Object.defineProperty(published, "bottom", {
				get : function() {
					return marginToString(aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::getBottom()());
				},
				set : function(aValue) {
					if(aValue != null) {
						var margin = @com.eas.client.gxtcontrols.MarginConstraints.Margin::new(Ljava/lang/String;)('' + aValue);
						aConstraints.@com.eas.client.gxtcontrols.MarginConstraints::setBottom(Lcom/eas/client/gxtcontrols/MarginConstraints$Margin;)(margin);
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
			
			marginToString = function(aMargin) {
				if (aMargin != undefined && aMargin != null) {
					var unit = aMargin.@com.eas.client.gxtcontrols.MarginConstraints.Margin::unit;
					return aMargin.@com.eas.client.gxtcontrols.MarginConstraints.Margin::value + unit.@com.google.gwt.dom.client.Style.Unit::getType()();
				}
				return null;
			}
			return published;
		} 

		// ***************************************************
		function publishChildrenOrdering(aPublished) {
			if (aPublished) {
				var comp = aPublished.unwrap();
				aPublished.toFront = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.client.gxtcontrols.wrappers.container.OrderedContainer::toFront(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.client.gxtcontrols.wrappers.container.OrderedContainer::toFront(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
		
				aPublished.toBack = function(aChild, aCount) {
					if (arguments.length == 1) {
					    comp.@com.eas.client.gxtcontrols.wrappers.container.OrderedContainer::toBack(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
					} else {
					    comp.@com.eas.client.gxtcontrols.wrappers.container.OrderedContainer::toBack(Lcom/google/gwt/user/client/ui/Widget;I)(aChild.unwrap(), aCount);
					}
				}
			}
		}
			
		function publishContainer(aPublished) {
			var comp = aPublished.unwrap();
			Object.defineProperty(aPublished, "count", {
				get : function() {
					return comp.@com.sencha.gxt.widget.core.client.container.Container::getWidgetCount()();
				}
			});
			aPublished.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {				
					comp.@com.sencha.gxt.widget.core.client.container.Container::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());
				}
			};
			aPublished.clear = function() {
				comp.@com.sencha.gxt.widget.core.client.container.Container::clear()();
			};
			aPublished.child = function(aIndex) {
				var widget = comp.@com.sencha.gxt.widget.core.client.container.Container::getWidget(I)(aIndex);
				return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			};
			publishChildren(aPublished);
		}

		function publishChildren(aPublished){
			Object.defineProperty(aPublished, "children", {get : function(){
				var ch = [];
				for(var i=0;i<aPublished.count;i++)
					ch[ch.length] = aPublished.child(i);
				return ch;
			}});
		}
		
		// ***************************************************
		function publishMenu(aPublished) {
			var comp = aPublished.unwrap();

			aPublished.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
					comp.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
				}
			}
			aPublished.child = function(aIndex) {
				var c = comp.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::getWidget(I)(aIndex);
				var wrap = @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(c);
				if (wrap != null) {
					return wrap;
				} else {
					var menu = c.@com.sencha.gxt.widget.core.client.menu.MenuItem::getSubMenu()();
					return @com.eas.client.gxtcontrols.Publisher::checkPublishedComponent(Ljava/lang/Object;)(menu);
				}
			};
			aPublished.remove = function(aChild) {
				if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
					var c = aChild.unwrap(); 
					comp.@com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu::remove(Lcom/google/gwt/user/client/ui/Widget;)(c);
				}
			};
		}
		
		// **************************************************************************
		$wnd.ButtonGroup = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.ButtonGroup)) {
				throw  ' use  "new ButtonGroup()" !';
			}
			var published = this;
			aComponent = aComponent || @com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup::new()();
			published.unwrap = function() {
				return aComponent;
			};
			published.add = function(toAdd){
				if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup::add(Lcom/sencha/gxt/widget/core/client/Component;)(toAdd.unwrap());
				}
			}
			published.remove = function(toRemove) {
				if(toRemove != undefined && toRemove != null && toRemove.unwrap != undefined) {
					aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup::remove(Lcom/sencha/gxt/widget/core/client/Component;)(toRemove.unwrap());
				}
			}
			published.clear = function() {
				aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup::clear()();				
			}
			aComponent.@com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			return published;
		}

	}-*/;
}

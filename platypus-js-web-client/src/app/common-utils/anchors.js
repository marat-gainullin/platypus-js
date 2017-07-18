define(function(){
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
				var aConstraints = arguments.length > 6 ? arguments[6] : null;
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
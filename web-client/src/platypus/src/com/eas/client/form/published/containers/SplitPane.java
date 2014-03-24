package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.containers.SplittedPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;

public class SplitPane extends SplittedPanel implements HasPublished {

	public static int HORIZONTAL_SPLIT = 1;
	public static int VERTICAL_SPLIT = 0;

	protected JavaScriptObject published;

	protected Widget firstWidget;
	protected Widget secondWidget;

	protected boolean oneTouchExpandable;
	protected int orientation = HORIZONTAL_SPLIT;
	protected int dividerLocation = 84;
	protected int dividerSize = 8;

	public SplitPane() {
		super();
	}

	public Widget getFirstWidget() {
		return firstWidget;
	}

	public void setFirstWidget(Widget aFirstWidget) {
		if (firstWidget != aFirstWidget) {
			if (firstWidget != null)
				firstWidget.removeFromParent();
			firstWidget = aFirstWidget;
			if (firstWidget != null) {
				if (orientation == HORIZONTAL_SPLIT) {
					addWest(firstWidget, dividerLocation);
				} else
					addNorth(firstWidget, dividerLocation);
			}
		}
	}

	public Widget getSecondWidget() {
		return secondWidget;
	}

	public void setSecondWidget(Widget aSecondWidget) {
		if (secondWidget != aSecondWidget) {
			if (secondWidget != null)
				secondWidget.removeFromParent();
			secondWidget = aSecondWidget;
			if (secondWidget != null) {
				add(secondWidget);
			}
		}
	}

	public boolean isOneTouchExpandable() {
		return oneTouchExpandable;
	}

	public int getOrientation() {
		return orientation;
	}

	public int getDividerLocation() {
		return dividerLocation;
	}

	public void setOneTouchExpandable(boolean aValue) {
		if (oneTouchExpandable != aValue) {
			oneTouchExpandable = aValue;
			if (firstWidget != null)
				setWidgetToggleDisplayAllowed(firstWidget, oneTouchExpandable);
			if (secondWidget != null)
				setWidgetToggleDisplayAllowed(secondWidget, oneTouchExpandable);
		}
	}

	public void setOrientation(int aValue) {
		if (orientation != aValue) {
			orientation = aValue;
			if (firstWidget != null) {
				firstWidget.removeFromParent();
				if (orientation == HORIZONTAL_SPLIT) {
					addWest(firstWidget, dividerLocation);
				} else {
					addNorth(firstWidget, dividerLocation);
				}
			}
		}
	}

	public void setDividerLocation(int aValue) {
		if (dividerLocation != aValue) {
			dividerLocation = aValue;
			if (firstWidget != null)
				super.setWidgetSize(firstWidget, aValue);
		}
	}

	public int getDividerSize() {
		return dividerSize;
	}

	public void setDividerSize(int aValue) {
		if (dividerSize != aValue) {
			dividerSize = aValue;
			for (int i = 0; i < getWidgetCount(); i++) {
				Widget w = getWidget(i);
				if (w.getStyleName().contains("gwt-SplitLayoutPanel-HDragger") || w.getStyleName().contains("gwt-SplitLayoutPanel-VDragger")) {
					w.getElement().getStyle().setPropertyPx("width", dividerSize);
				}
			}
			forceLayout();
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "orientation", {
			get : function() {
				var orientation = aComponent.@com.eas.client.form.published.containers.SplitPane::getOrientation()();
				if (orientation == @com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT) {
					return $wnd.Orientation.VERTICAL;
				} else {
					return $wnd.Orientation.HORIZONTAL;
				}
			},
			set : function(aOrientation) {
				if (aOrientation == $wnd.Orientation.VERTICAL) {
					aComponent.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(@com.eas.client.form.published.containers.SplitPane::VERTICAL_SPLIT);
				} else {
					aComponent.@com.eas.client.form.published.containers.SplitPane::setOrientation(I)(@com.eas.client.form.published.containers.SplitPane::HORIZONTAL_SPLIT);
				}
			}
		});
		Object.defineProperty(published, "firstComponent", {
			get : function() {
				var comp = aComponent.@com.eas.client.form.published.containers.SplitPane::getFirstWidget()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aComponent.@com.eas.client.form.published.containers.SplitPane::setFirstWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "secondComponent", {
			get : function() {
				var comp = aComponent.@com.eas.client.form.published.containers.SplitPane::getSecondWidget()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);
			},
			set : function(aChild) {
				var child = (aChild == null ? null: aChild.unwrap());
				aComponent.@com.eas.client.form.published.containers.SplitPane::setSecondWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
			}
		});
		Object.defineProperty(published, "dividerLocation", {
			get : function() {
				return aComponent.@com.eas.client.form.published.containers.SplitPane::getDividerLocation()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.containers.SplitPane::setDividerLocation(I)(aValue);
			}
		});
		Object.defineProperty(published, "oneTouchExpandable", {
			get : function() {
				return aComponent.@com.eas.client.form.published.containers.SplitPane::isOneTouchExpandable()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.containers.SplitPane::setOneTouchExpandable(Z)(false != aValue);
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
				aComponent.@com.eas.client.form.published.containers.SplitPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
			}
		};
		published.child = function(aIndex) {
			var widget = aComponent.@com.eas.client.form.published.containers.SplitPane::getWidget(I)(aIndex);
			return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
		};
	}-*/;
}

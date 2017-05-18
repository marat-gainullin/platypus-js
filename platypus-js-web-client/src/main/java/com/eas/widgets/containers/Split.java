package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.HasChildrenPosition;
import com.eas.ui.Widget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;

/**
 *
 * @author mg
 */
public class Split extends Container implements HasChildrenPosition {

    public static int HORIZONTAL_SPLIT = 1;
    public static int VERTICAL_SPLIT = 0;

    protected Widget firstWidget;
    protected Widget secondWidget;

    protected boolean oneTouchExpandable;
    protected int orientation = HORIZONTAL_SPLIT;
    protected int dividerLocation = 84;
    protected int splitterSize = 10;

    public Split() {
        super();
        com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
    }

    public Split(int aSplitterSize) {
        this();
        splitterSize = aSplitterSize;
    }

    private void checkAdd(Widget w) {
        if (firstWidget == null) {
            firstWidget = w;
            formatFirst();
        } else {
            if (secondWidget != null) {
                super.remove(secondWidget);
            }
            secondWidget = w;
            formatSecond();
        }
    }

    @Override
    public void add(com.eas.ui.Widget w) {
        super.add(w);
        checkAdd(w);
    }

    @Override
    public void add(com.eas.ui.Widget w, int beforeIndex) {
        super.add(w, beforeIndex);
        checkAdd(w);
    }

    @Override
    public boolean remove(Widget w) {
        checkRemove(w);
        return super.remove(w);
    }

    private void checkRemove(Widget w) {
        if (w == firstWidget) {
            firstWidget = null;
        }
        if (w == secondWidget) {
            secondWidget = null;
        }
    }

    @Override
    public Widget remove(int index) {
        Widget w = super.remove(index);
        checkRemove(w);
        return w;
    }

    public Widget getFirstWidget() {
        return firstWidget;
    }

    public void setFirstWidget(Widget aFirstWidget) {
        if (firstWidget != aFirstWidget) {
            if (firstWidget != null) {
                super.remove(firstWidget);
            }
            firstWidget = aFirstWidget;
            if (firstWidget != null) {
                formatFirst();
                super.add(firstWidget);
            }
        }
    }

    public Widget getSecondWidget() {
        return secondWidget;
    }

    public void setSecondWidget(Widget aSecondWidget) {
        if (secondWidget != aSecondWidget) {
            if (secondWidget != null) {
                super.remove(secondWidget);
            }
            secondWidget = aSecondWidget;
            if (secondWidget != null) {
                formatSecond();
                super.add(secondWidget);
            }
        }
    }

    private void formatFirst() {
        if (firstWidget != null) {
            firstWidget.getElement().addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style fs = firstWidget.getElement().getStyle();
            if (orientation == HORIZONTAL_SPLIT) {
                fs.setLeft(0, Style.Unit.PX);
                fs.setTop(0, Style.Unit.PX);
                fs.setBottom(0, Style.Unit.PX);
                fs.setWidth(dividerLocation, Style.Unit.PX);
            } else {
                fs.setLeft(0, Style.Unit.PX);
                fs.setRight(0, Style.Unit.PX);
                fs.setTop(0, Style.Unit.PX);
                fs.setHeight(dividerLocation, Style.Unit.PX);
            }
        }
    }

    private void formatSecond() {
        if (secondWidget != null) {
            secondWidget.getElement().addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            Style fs = secondWidget.getElement().getStyle();
            if (orientation == HORIZONTAL_SPLIT) {
                fs.setRight(0, Style.Unit.PX);
                fs.setTop(0, Style.Unit.PX);
                fs.setBottom(0, Style.Unit.PX);
                fs.setLeft(dividerLocation + splitterSize, Style.Unit.PX);
            } else {
                fs.setLeft(0, Style.Unit.PX);
                fs.setRight(0, Style.Unit.PX);
                fs.setBottom(0, Style.Unit.PX);
                fs.setTop(dividerLocation + splitterSize, Style.Unit.PX);
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

    public int getSplitterSize() {
        return splitterSize;
    }

    public void setOneTouchExpandable(boolean aValue) {
        if (oneTouchExpandable != aValue) {
            oneTouchExpandable = aValue;
        }
    }

    public void setOrientation(int aValue) {
        if (orientation != aValue) {
            orientation = aValue;
            formatFirst();
            formatSecond();
        }
    }

    public void setDividerLocation(int aValue) {
        if (dividerLocation != aValue) {
            dividerLocation = aValue;
            formatFirst();
            formatSecond();
        }
    }

    public void setSplitterSize(int aValue) {
        if (splitterSize != aValue) {
            splitterSize = aValue;
            formatFirst();
            formatSecond();
        }
    }

    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetLeft();
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        var Orientation = @com.eas.ui.JsUi::Orientation;
        Object.defineProperty(published, "orientation", {
            get : function() {
                var orientation = aWidget.@com.eas.widgets.SplitPane::getOrientation()();
                if (orientation == @com.eas.widgets.SplitPane::VERTICAL_SPLIT) {
                    return Orientation.VERTICAL;
                } else {
                    return Orientation.HORIZONTAL;
                }
            },
            set : function(aOrientation) {
                if (aOrientation == Orientation.VERTICAL) {
                    aWidget.@com.eas.widgets.SplitPane::setOrientation(I)(@com.eas.widgets.SplitPane::VERTICAL_SPLIT);
                } else {
                    aWidget.@com.eas.widgets.SplitPane::setOrientation(I)(@com.eas.widgets.SplitPane::HORIZONTAL_SPLIT);
                }
            }
        });
        Object.defineProperty(published, "firstComponent", {
            get : function() {
                var comp = aWidget.@com.eas.widgets.SplitPane::getFirstWidget()();
                return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                var child = (aChild == null ? null: aChild.unwrap());
                aWidget.@com.eas.widgets.SplitPane::setFirstWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
            }
        });
        Object.defineProperty(published, "secondComponent", {
            get : function() {
                var comp = aWidget.@com.eas.widgets.SplitPane::getSecondWidget()();
                return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(comp);
            },
            set : function(aChild) {
                var child = (aChild == null ? null: aChild.unwrap());
                aWidget.@com.eas.widgets.SplitPane::setSecondWidget(Lcom/google/gwt/user/client/ui/Widget;)(child);
            }
        });
        Object.defineProperty(published, "dividerLocation", {
            get : function() {
                return aWidget.@com.eas.widgets.SplitPane::getDividerLocation()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.SplitPane::setDividerLocation(I)(aValue);
            }
        });
        Object.defineProperty(published, "oneTouchExpandable", {
            get : function() {
                return aWidget.@com.eas.widgets.SplitPane::isOneTouchExpandable()();
            },
            set : function(aValue) {
                aComponent.@com.eas.widgets.SplitPane::setOneTouchExpandable(Z)(false != aValue);
            }
        });
        published.add = function(toAdd){
            if(toAdd != undefined && toAdd != null && toAdd.unwrap != undefined) {
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                if (published.firstComponent == null) {
                    published.firstComponent = toAdd;
                }else {
                    published.secondComponent = toAdd;
                }
            }
        }
        published.remove = function(aChild) {
            if (aChild != undefined && aChild != null && aChild.unwrap != undefined) {
                aWidget.@com.eas.widgets.SplitPane::remove(Lcom/google/gwt/user/client/ui/Widget;)(aChild.unwrap());				
            }
        };
        published.child = function(aIndex) {
            var widget = aWidget.@com.eas.widgets.SplitPane::getWidget(I)(aIndex);
            return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(widget);
        };
    }-*/;

}

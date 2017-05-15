package com.eas.widgets.containers;

import com.eas.core.XElement;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author mg
 */
public class CardsPanel extends ComplexPanel implements InsertPanel.ForIsWidget, RequiresResize, ProvidesResize {

    protected Widget visibleWidget;

    private int hgap;
    private int vgap;

    /**
     * Creates an empty deck panel.
     */
    public CardsPanel() {
        setElement(Document.get().createDivElement());
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        getElement().getStyle().setPosition(Style.Position.RELATIVE);
	getElement().<XElement>cast().addResizingTransitionEnd(this);
    }

    public int getHgap() {
        return hgap;
    }

    public void setHgap(int aValue) {
        hgap = aValue;
        for (Widget w : getChildren()) {
            Element we = w.getElement();
            we.getStyle().setMarginLeft(hgap, Style.Unit.PX);
            we.getStyle().setMarginRight(hgap, Style.Unit.PX);
        }
        if (isAttached()) {
            onResize();
        }
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int aValue) {
        vgap = aValue;
        for (Widget w : getChildren()) {
            Element we = w.getElement();
            we.getStyle().setMarginTop(vgap, Style.Unit.PX);
            we.getStyle().setMarginBottom(vgap, Style.Unit.PX);
        }
        if (isAttached()) {
            onResize();
        }
    }

    @Override
    public void add(Widget w) {
        Element container = createWidgetContainer();
        DOM.appendChild(getElement(), container);

        // The order of these methods is very important. In order to preserve
        // backward compatibility, the offsetWidth and offsetHeight of the child
        // widget should be defined (greater than zero) when w.onLoad() is called.
        // As a result, we first initialize the container with a height of 0px, then
        // we attach the child widget to the container. See Issue 2321 for more
        // details.
        super.add(w, container);

        // After w.onLoad is called, it is safe to make the container invisible and
        // set the height of the container and widget to 100%.
        finishWidgetInitialization(container, w);
    }

    /**
     * Gets the index of the currently-visible widget, if any.
     *
     * @return the visible widget's index, or -1 if there is no such widget
     */
    public int getVisibleWidget() {
        return getWidgetIndex(visibleWidget);
    }

    @Override
    public void insert(IsWidget w, int beforeIndex) {
        insert(asWidgetOrNull(w), beforeIndex);
    }

    @Override
    public void insert(Widget w, int beforeIndex) {
        Element container = createWidgetContainer();
        DOM.insertChild(getElement(), container, beforeIndex);

        // See add(Widget) for important comments
        insert(w, container, beforeIndex, true);
        finishWidgetInitialization(container, w);
    }

    @Override
    public boolean remove(Widget w) {
        Element container = w.getElement().getParentElement();
        boolean removed = super.remove(w);
        if (removed) {
            getElement().removeChild(container);
            if (visibleWidget == w) {
                visibleWidget = null;
            }
        }
        return removed;
    }

    /**
     * Shows the widget at the specified index. This causes the currently-
     * visible widget to be hidden.
     *
     * @param toBeShown the widget to be shown
     */
    public void showWidget(Widget toBeShown) {
        Widget oldWidget = visibleWidget;
        visibleWidget = toBeShown;

        if (visibleWidget != oldWidget) {
            Element oldContainer = oldWidget != null ? oldWidget.getElement().getParentElement() : null;
            Element newContainer = visibleWidget.getElement().getParentElement();

            UIObject.setVisible(newContainer, true);
            newContainer.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            visibleWidget.setVisible(true);

            if (oldContainer != null) {
                oldContainer.removeClassName("card-shown");
                oldContainer.addClassName("card-hidden");
            }
            newContainer.removeClassName("card-hidden");
            newContainer.addClassName("card-shown");

            if (oldWidget != null) {
                UIObject.setVisible(oldContainer, false);
                oldWidget.setVisible(false);
            }
            //checkButtonWidth();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
		            if (visibleWidget instanceof RequiresResize) {
		                ((RequiresResize) visibleWidget).onResize();
		            }
				}

			});
        }
    }
/*
    protected void checkButtonWidth() {
        if (visibleWidget != null) {
            Element we = visibleWidget.getElement();
            Element wpe = we.getParentElement();
            //if (visibleWidget instanceof FocusWidget) {
                we.getStyle().clearRight();
                we.getStyle().clearBottom();
                we.getStyle().setWidth(wpe.getClientWidth() - hgap * 2, Style.Unit.PX);
                we.getStyle().setHeight(wpe.getClientHeight() - vgap * 2, Style.Unit.PX);
                com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
                visibleWidget.getElement().addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
            //}
        }
    }
*/
    /**
     * Setup the container around the widget.
     */
    private Element createWidgetContainer() {
        Element container = Document.get().createDivElement();
        container.addClassName("cards-item");
        container.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        container.getStyle().setPosition(Style.Position.RELATIVE);
        container.getStyle().setWidth(100, Style.Unit.PCT);
        container.getStyle().setHeight(100, Style.Unit.PCT);
        container.getStyle().setPadding(0, Style.Unit.PX);
        container.getStyle().setMargin(0, Style.Unit.PX);
        return container;
    }

    /**
     * Setup the container around the widget.
     */
    private void finishWidgetInitialization(Element container, Widget w) {
        UIObject.setVisible(container, false);

        // Set all anchors by default.
        Element we = w.getElement();
        we.getStyle().setPosition(Style.Position.ABSOLUTE);
        we.getStyle().clearWidth();
        we.getStyle().clearHeight();
        we.getStyle().setLeft(0, Style.Unit.PX);
        we.getStyle().setRight(0, Style.Unit.PX);
        we.getStyle().setTop(0, Style.Unit.PX);
        we.getStyle().setBottom(0, Style.Unit.PX);
        we.getStyle().setMarginLeft(hgap, Style.Unit.PX);
        we.getStyle().setMarginRight(hgap, Style.Unit.PX);
        we.getStyle().setMarginTop(vgap, Style.Unit.PX);
        we.getStyle().setMarginBottom(vgap, Style.Unit.PX);
        
        // Issue 2510 from GWT : Hiding the widget isn't necessary because we hide its
        // wrapper, but it's in here for legacy support.
        w.setVisible(false);
    }

    @Override
    public void onResize() {
        //checkButtonWidth();
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (getWidgetCount() > 0) {
            showWidget(getWidget(0));
        }
    }
}

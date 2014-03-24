/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;

/**
 * Toolbar is a container that is able to send invisible widgets into chevron
 * popup. Toolbar takes into account width, when a component is visible in
 * content area and sets it's height to 100%. Width and height are both taken
 * into account when a widget is not visible and added into chevron popup.
 *
 * @author mg
 */
public class Toolbar extends SimplePanel implements IndexedPanel, ProvidesResize, RequiresResize {

    protected HorizontalBoxPanel content = new HorizontalBoxPanel();
    protected VerticalPanel altContent = new VerticalPanel();
    protected SimplePanel chevron = new SimplePanel();

    public Toolbar() {
        super();
        getElement().addClassName("toolbar");
        chevron.getElement().addClassName("toolbar-chevron");
        content.setHgap(2);
        content.getElement().addClassName("toolbar-content");
        getElement().getStyle().setPosition(Style.Position.RELATIVE);
        content.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        content.getElement().getStyle().setHeight(100, Style.Unit.PCT);
        content.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        content.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        chevron.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        chevron.getElement().getStyle().setHeight(100, Style.Unit.PCT);
        setWidget(content);
        getElement().appendChild(chevron.getElement());
        chevron.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final Map<Widget, String> lefts = new HashMap<>();
                final Map<Widget, String> rights = new HashMap<>();
                final Map<Widget, String> widths = new HashMap<>();
                final Map<Widget, String> positions = new HashMap<>();
                final Map<Widget, String> margins = new HashMap<>();
                final VerticalPanel vp = new VerticalPanel();
                vp.getElement().addClassName("toolbar-chevron-menu");
                Widget[] widgets = new Widget[content.getWidgetCount()];
                for (int i = 0; i < widgets.length; i++) {
                    widgets[i] = content.getWidget(i);
                }
                for (int i = 0; i < widgets.length; i++) {
                    Widget iw = widgets[i];
                    int rightMost = iw.getElement().getOffsetLeft() + iw.getElement().getOffsetWidth();
                    int bottomMost = iw.getElement().getOffsetTop() + iw.getElement().getOffsetHeight();
                    int parentWidth = iw.getElement().getParentElement().getOffsetWidth();
                    int parentHeight = iw.getElement().getParentElement().getOffsetHeight();
                    if (rightMost <= 0 || iw.getElement().getOffsetLeft() >= parentWidth
                            || bottomMost <= 0 || iw.getElement().getOffsetTop() >= parentHeight) {
                        lefts.put(iw, iw.getElement().getStyle().getLeft());
                        rights.put(iw, iw.getElement().getStyle().getRight());
                        widths.put(iw, iw.getElement().getStyle().getWidth());
                        positions.put(iw, iw.getElement().getStyle().getPosition());
                        margins.put(iw, iw.getElement().getStyle().getMarginLeft());

                        SimplePanel sp = new SimplePanel();
                        sp.getElement().addClassName("toolbar-chevron-menu-item");
                        sp.getElement().getStyle().setPadding(0, Style.Unit.PX);
                        sp.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
                        sp.getElement().getStyle().setPosition(Style.Position.RELATIVE);
                        sp.setWidget(iw);
                        vp.add(sp);
                    }
                }
                if (vp.getWidgetCount() > 0) {
                    PopupPanel pp = new PopupPanel(true);
                    pp.getElement().getStyle().setPadding(0, Style.Unit.PX);
                    pp.setWidget(vp);
                    pp.setAnimationEnabled(false);
                    pp.addCloseHandler(new CloseHandler<PopupPanel>() {

                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            Widget[] widgets = new Widget[vp.getWidgetCount()];
                            for (int i = 0; i < widgets.length; i++) {
                                widgets[i] = vp.getWidget(i);
                            }
                            for (int i = 0; i < widgets.length; i++) {
                                Widget w = widgets[i];
                                assert w instanceof SimplePanel;
                                Widget iw = ((SimplePanel) w).getWidget();
                                String oldLeft = lefts.remove(iw);
                                if (oldLeft != null && !oldLeft.isEmpty()) {
                                    iw.getElement().getStyle().setProperty("left", oldLeft);
                                } else {
                                    iw.getElement().getStyle().clearLeft();
                                }
                                String oldRight = rights.remove(iw);
                                if (oldRight != null && !oldRight.isEmpty()) {
                                    iw.getElement().getStyle().setProperty("right", oldRight);
                                } else {
                                    iw.getElement().getStyle().clearRight();
                                }
                                String oldPosition = positions.remove(iw);
                                if (oldPosition != null && !oldPosition.isEmpty()) {
                                    iw.getElement().getStyle().setProperty("position", oldPosition);
                                } else {
                                    iw.getElement().getStyle().clearPosition();
                                }
                                String oldWidth = widths.remove(iw);
                                if (oldWidth != null && !oldWidth.isEmpty()) {
                                    iw.getElement().getStyle().setProperty("width", oldWidth);
                                } else {
                                    iw.getElement().getStyle().clearWidth();
                                }
                                String oldMargin = margins.remove(iw);
                                if (oldMargin != null && !oldMargin.isEmpty()) {
                                    iw.getElement().getStyle().setProperty("marginLeft", oldMargin);
                                } else {
                                    iw.getElement().getStyle().clearMarginLeft();
                                }
                                content.add(iw);
                            }
                            content.onResize();
                        }

                    });
                    pp.showRelativeTo(chevron);
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                            for (int i = 0; i < vp.getWidgetCount(); i++) {
                                Widget w = vp.getWidget(i);
                                assert w instanceof SimplePanel;
                                SimplePanel sp = (SimplePanel) w;
                                Widget iw = sp.getWidget();
                                int width = sp.getElement().getOffsetWidth();
                                int height = iw.getElement().getOffsetHeight();
                                sp.getElement().getStyle().setWidth(width, Style.Unit.PX);
                                sp.getElement().getStyle().setHeight(height, Style.Unit.PX);
                                iw.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
                                iw.getElement().getStyle().setLeft(0, Style.Unit.PX);
                                iw.getElement().getStyle().setRight(0, Style.Unit.PX);
                                iw.getElement().getStyle().clearWidth();
                                iw.getElement().getStyle().clearMarginLeft();
                                if ("button".equalsIgnoreCase(iw.getElement().getTagName())) {
                                    iw.getElement().getStyle().setWidth(100, Style.Unit.PCT);
                                }
                                if (iw instanceof RequiresResize) {
                                    ((RequiresResize) iw).onResize();
                                }
                            }
                        }
                    });
                }
            }
        }, ClickEvent.getType());
    }

    public int getHgap() {
        return content.getHgap();
    }

    public void setHgap(int aValue) {
        content.setHgap(aValue);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        adopt(chevron);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        orphan(chevron);
    }

    @Override
    public void add(Widget w) {
        content.add(w);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public boolean remove(Widget w) {
        return content.remove(w);
    }

    @Override
    public Widget getWidget(int index) {
        return content.getWidget(index);
    }

    @Override
    public int getWidgetCount() {
        return content.getWidgetCount();
    }

    @Override
    public int getWidgetIndex(Widget child) {
        return content.getWidgetIndex(child);
    }

    @Override
    public boolean remove(int index) {
        return content.remove(index);
    }

    @Override
    public void onResize() {
        content.onResize();
    }
}

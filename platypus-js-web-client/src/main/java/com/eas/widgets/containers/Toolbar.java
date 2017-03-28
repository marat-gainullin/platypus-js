package com.eas.widgets.containers;

import com.eas.core.XElement;
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

	protected BoxPanel content = new BoxPanel();
	protected VerticalPanel altContent = new VerticalPanel();
	protected SimplePanel chevron = new SimplePanel();

	public Toolbar() {
		super();
		getElement().addClassName("toolbar");
		chevron.getElement().addClassName("toolbar-chevron");
		chevron.getElement().getStyle().setDisplay(Style.Display.NONE);
		content.setHgap(0);
		content.getElement().addClassName("toolbar-content");
		content.getElement().addClassName("btn-group");
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
				toogleOverflowPopup();
			}
		}, ClickEvent.getType());
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}
	
	private PopupPanel overflowPopup;
	
	public void toogleOverflowPopup(){
		if(overflowPopup != null)
			hideOverflowPopup();
		else{
			showOverflowPopup();
		}
	}
	
	public void hideOverflowPopup(){
		if(overflowPopup != null){
			overflowPopup.hide();
		}
	}
	
	public void showOverflowPopup(){
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
			int parentWidth = iw.getElement().getParentElement().getClientWidth();
			int parentHeight = iw.getElement().getParentElement().getClientHeight();
			if (rightMost <= 0 || iw.getElement().getOffsetLeft() >= parentWidth || bottomMost <= 0 || iw.getElement().getOffsetTop() >= parentHeight) {
				lefts.put(iw, iw.getElement().getStyle().getLeft());
				rights.put(iw, iw.getElement().getStyle().getRight());
				widths.put(iw, iw.getElement().getStyle().getWidth());
				positions.put(iw, iw.getElement().getStyle().getPosition());
				margins.put(iw, iw.getElement().getStyle().getMarginLeft());
				iw.getElement().getStyle().setHeight(parentHeight, Style.Unit.PX);

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
			overflowPopup = new PopupPanel(true);
			overflowPopup.setStyleName("toolbar-chevron-popup");
			overflowPopup.setWidget(vp);
			overflowPopup.setAnimationEnabled(false);
			overflowPopup.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					overflowPopup = null;
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
						iw.getElement().getStyle().setHeight(100, Style.Unit.PCT);
						content.add(iw);
					}
					content.onResize();
				}

			});
			overflowPopup.setPopupPosition(chevron.getAbsoluteLeft(), chevron.getAbsoluteTop());
			overflowPopup.showRelativeTo(chevron);
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
						// if (iw instanceof FocusWidget) {
						iw.getElement().getStyle().clearRight();
						iw.getElement().getStyle().setWidth(100, Style.Unit.PCT);
						com.eas.ui.CommonResources.INSTANCE.commons().ensureInjected();
						iw.getElement().addClassName(com.eas.ui.CommonResources.INSTANCE.commons().borderSized());
						// }
						if (iw instanceof RequiresResize) {
							((RequiresResize) iw).onResize();
						}
					}
				}
			});
		}
	}
	
	public void updateChevronVisibility() {
		if (isAttached()) {
			int contentOffsetHeight = content.getElement().getOffsetHeight();
			int contentScrollHeight = content.getElement().getScrollHeight();
			if (contentScrollHeight <= contentOffsetHeight) {
				chevron.getElement().getStyle().setDisplay(Style.Display.NONE);
			} else {
				chevron.getElement().getStyle().clearDisplay();
			}
		}
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
		updateChevronVisibility();
	}

	@Override
	protected void onDetach() {
		orphan(chevron);
		super.onDetach();
	}

	@Override
	public void add(Widget w) {
		content.add(w);
		updateChevronVisibility();
	}

	public void ajustDisplay(Widget child) {
		content.ajustDisplay(child);
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
		boolean res = content.remove(index);
		updateChevronVisibility();
		return res;
	}

	@Override
	public void onResize() {
		content.onResize();
		updateChevronVisibility();
	}
}

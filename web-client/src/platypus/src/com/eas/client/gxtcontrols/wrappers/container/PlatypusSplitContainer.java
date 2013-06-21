package com.eas.client.gxtcontrols.wrappers.container;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.BeforeCollapseHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;

public class PlatypusSplitContainer extends BorderLayoutContainer {

	private class Handler implements BeforeCollapseHandler, BeforeExpandHandler {
		@Override
		public void onBeforeCollapse(BeforeCollapseEvent event) {
			/* Some bugs occur in BorderLayoutContainer when uncommented
			event.setCancelled(true);
			onCollapse((ContentPanel) event.getSource());
			*/
		}

		@Override
		public void onBeforeExpand(BeforeExpandEvent event) {
			/* Some bugs occur in BorderLayoutContainer when uncommented
			event.setCancelled(true);
			onExpand((ContentPanel) event.getSource());
			*/
		}

	}

	public static int HORIZONTAL_SPLIT = 1;
	public static int VERTICAL_SPLIT = 0;

	private Handler collapseHandler = new Handler();

	protected boolean oneTouchExpandable;
	protected int orientation = HORIZONTAL_SPLIT;
	protected int dividerLocation = 84;
	protected int dividerSize = 5;
	protected Component leftComponent;
	protected Component rightComponent;

	public PlatypusSplitContainer() {
		super();
	}

	protected void organizeLeft() {
		BorderLayoutData layoutData = new BorderLayoutData(dividerLocation);
		layoutData.setCollapsible(oneTouchExpandable);
		layoutData.setCollapseMini(oneTouchExpandable);
		layoutData.setCollapseHidden(oneTouchExpandable);
		layoutData.setSplit(true);
		setWestWidget(null);
		setNorthWidget(null);
		if (leftComponent != null) {
			ContentPanel cp = new ContentPanel();
			cp.setCollapsible(true);
			cp.setHideCollapseTool(true);
			cp.setHeaderVisible(false);
			cp.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					if (orientation == HORIZONTAL_SPLIT)
						dividerLocation = event.getWidth();
					else
						dividerLocation = event.getHeight();
				}
			});
			if (orientation == HORIZONTAL_SPLIT) {
				layoutData.setMargins(new Margins(0, dividerSize, 0, 0));
				setWestWidget(cp, layoutData);
			} else {
				layoutData.setMargins(new Margins(0, 0, dividerSize, 0));
				setNorthWidget(cp, layoutData);
			}
			cp.addBeforeCollapseHandler(collapseHandler);
			cp.addBeforeExpandHandler(collapseHandler);
			leftComponent.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
			cp.setWidget(leftComponent);
		}
		if (isAttached())
			forceLayout();
	}

	protected void organizeRight() {
		setCenterWidget(null);
		if (rightComponent != null) {
			rightComponent.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
			SimpleContainer c = new SimpleContainer();
			c.add(rightComponent, new MarginData(0, 0, 0, 0));
			setCenterWidget(c, new MarginData(0, 0, 0, 0));
		}
		if (isAttached()) {
			forceLayout();
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

	public Component getLeftComponent() {
		return leftComponent;
	}

	public Component getRightComponent() {
		return rightComponent;
	}

	public void setOneTouchExpandable(boolean aValue) {
		if (oneTouchExpandable != aValue) {
			oneTouchExpandable = aValue;
			organizeLeft();
		}
	}

	public void setOrientation(int aValue) {
		if (orientation != aValue) {
			orientation = aValue;
			organizeLeft();
		}
	}

	public void setDividerLocation(int aValue) {
		if (dividerLocation != aValue) {
			dividerLocation = aValue;
			organizeLeft();
		}
	}

	public void setLeftComponent(Component aComp) {
		if (leftComponent != aComp) {
			leftComponent = aComp;
			organizeLeft();
		}
	}

	public void setRightComponent(Component aComp) {
		if (rightComponent != aComp) {
			rightComponent = aComp;
			organizeRight();
		}
	}

	public boolean removeChild(Widget child) {
		if (child == leftComponent) {
			leftComponent = null;
			organizeLeft();
			return true;
		}
		if (child == rightComponent) {
			rightComponent = null;
			organizeRight();
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		leftComponent = null;
		rightComponent = null;
		super.clear();
	}

	@Override
	public Widget getWidget(int index) {
		index = getWidgetCount() - index - 1;
		Widget w = super.getWidget(index);
		if (w != null) {
			assert w instanceof SimpleContainer;
			return ((SimpleContainer) w).getWidget();
		}
		return null;
	}

}

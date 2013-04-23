package com.eas.client.gxtcontrols.wrappers.container;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.eas.client.gxtcontrols.Sizer;
import com.eas.client.gxtcontrols.model.ModelTextArea;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;

public class PlatypusScrollContainer extends FlowLayoutContainer {

	/**
	 * Used to set the vertical scroll bar policy so that vertical scrollbars
	 * are displayed only when needed.
	 */
	public static final int VERTICAL_SCROLLBAR_AS_NEEDED = 20;
	/**
	 * Used to set the vertical scroll bar policy so that vertical scrollbars
	 * are never displayed.
	 */
	public static final int VERTICAL_SCROLLBAR_NEVER = 21;
	/**
	 * Used to set the vertical scroll bar policy so that vertical scrollbars
	 * are always displayed.
	 */
	public static final int VERTICAL_SCROLLBAR_ALWAYS = 22;

	/**
	 * Used to set the horizontal scroll bar policy so that horizontal
	 * scrollbars are displayed only when needed.
	 */
	public static final int HORIZONTAL_SCROLLBAR_AS_NEEDED = 30;
	/**
	 * Used to set the horizontal scroll bar policy so that horizontal
	 * scrollbars are never displayed.
	 */
	public static final int HORIZONTAL_SCROLLBAR_NEVER = 31;
	/**
	 * Used to set the horizontal scroll bar policy so that horizontal
	 * scrollbars are always displayed.
	 */
	public static final int HORIZONTAL_SCROLLBAR_ALWAYS = 32;

	protected int verticalScrollBarPolicy;
	protected int horizontalScrollBarPolicy;
	protected Component view;
	protected HandlerRegistration viewResizeHandlerReg;

	public PlatypusScrollContainer() {
		super();
	}

	public int getVerticalScrollBarPolicy() {
		return verticalScrollBarPolicy;
	}

	public int getHorizontalScrollBarPolicy() {
		return horizontalScrollBarPolicy;
	}

	public void setVerticalScrollBarPolicy(int aValue) {
		verticalScrollBarPolicy = aValue;
		applyPolicies();
	}

	public void setHorizontalScrollBarPolicy(int aValue) {
		horizontalScrollBarPolicy = aValue;
		applyPolicies();
	}

	protected void applyPolicies() {
		if (isViewGreater() && !isTextArea(view)) {
			if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS)
				setScrollMode(ScrollMode.ALWAYS);
			else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_NEVER && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_NEVER)
				setScrollMode(ScrollMode.NONE);
			else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED && verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED)
				setScrollMode(ScrollMode.AUTO);
			else {
				if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED)
					setScrollMode(ScrollMode.AUTOX);
				if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED)
					setScrollMode(ScrollMode.AUTOY);
			}
		} else {
			setScrollMode(ScrollMode.NONE);
			if (isTextArea(view)) {
				// apply scroll policy to textarea (somehow)
			}
		}
	}

	protected boolean isViewGreater() {
		if (view != null) {
			int viewWidth = Sizer.getWidgetWidth(view);
			int viewHeight = Sizer.getWidgetHeight(view);
			int scrollWidth = Sizer.getWidgetWidth(this);
			int scrollHeight = Sizer.getWidgetHeight(this);
			return viewWidth > scrollWidth || viewHeight > scrollHeight;
		}
		return false;
	}

	public Component getView() {
		return view;
	}

	public void setView(Component aView) {
		if (view != aView) {
			if (viewResizeHandlerReg != null)
				viewResizeHandlerReg.removeHandler();
			if (view != null) {
				if (view instanceof HBoxLayoutContainer)
					((HBoxLayoutContainer) view).setEnableOverflow(true);
				super.remove(view);
			}
			view = aView;
			if (view != null) {
				if (view instanceof HBoxLayoutContainer)
					((HBoxLayoutContainer) view).setEnableOverflow(false);
				super.add(view);
				if (!isTextArea(view)) {
					viewResizeHandlerReg = view.addResizeHandler(new ResizeHandler() {

						@Override
						public void onResize(ResizeEvent event) {
							applyPolicies();
						}
					});
				}
			}
			applyPolicies();
		}
	}

	@Override
	public void add(Widget aWidget) {
		if (getWidgetCount() > 0) {
			clear();
		}
		if (aWidget instanceof Component)
			setView((Component) aWidget);
	}

	@Override
	public void clear() {
		super.clear();
		setView(null);
	}

	@Override
	public boolean remove(Widget child) {
		setView(null);
		return getChildren().size() <= 0;
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		if (isTextArea(view)) {
			view.setPixelSize(width, height);
			setScrollMode(ScrollMode.NONE);
		} else if(view instanceof PlatypusVBoxLayoutContainer)
		{
			((PlatypusVBoxLayoutContainer)view).ajustSize();
		} else if(view instanceof PlatypusHBoxLayoutContainer)
		{
			((PlatypusHBoxLayoutContainer)view).ajustSize();
		}
		applyPolicies();
	}

	protected boolean isTextArea(Widget aCandidate) {
		return aCandidate instanceof com.sencha.gxt.widget.core.client.form.TextArea
			|| aCandidate instanceof com.google.gwt.user.client.ui.TextArea
			|| aCandidate instanceof PlatypusHtmlEditor
			|| aCandidate instanceof ModelTextArea;
	}

	public void ajustWidth(Widget aChild, int aValue) {
		if (aChild != null && !isTextArea(aChild))
			aChild.setPixelSize(aValue, Sizer.getWidgetHeight(aChild));
	}

	public void ajustHeight(Widget aChild, int aValue) {
		if (aChild != null && !isTextArea(aChild))
			aChild.setPixelSize(Sizer.getWidgetWidth(aChild), aValue);
	}
}

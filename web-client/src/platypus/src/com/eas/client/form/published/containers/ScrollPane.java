package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.ScrollBoxPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public class ScrollPane extends ScrollBoxPanel implements HasPublished {

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

	protected JavaScriptObject published;

	protected int verticalScrollBarPolicy;
	protected int horizontalScrollBarPolicy;

	public ScrollPane() {
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
		switch (horizontalScrollBarPolicy) {
		case HORIZONTAL_SCROLLBAR_ALWAYS:
			setHorizontalScrollPolicy(ScrollPolicy.ALLWAYS);
			break;
		case HORIZONTAL_SCROLLBAR_AS_NEEDED:
			setHorizontalScrollPolicy(ScrollPolicy.AUTO);
			break;
		case HORIZONTAL_SCROLLBAR_NEVER:
			setHorizontalScrollPolicy(ScrollPolicy.NEVER);
			break;
		default:
			setHorizontalScrollPolicy(ScrollPolicy.AUTO);
		}
		switch (verticalScrollBarPolicy) {
		case VERTICAL_SCROLLBAR_ALWAYS:
			setVerticalScrollPolicy(ScrollPolicy.ALLWAYS);
			break;
		case VERTICAL_SCROLLBAR_AS_NEEDED:
			setVerticalScrollPolicy(ScrollPolicy.AUTO);
			break;
		case VERTICAL_SCROLLBAR_NEVER:
			setVerticalScrollPolicy(ScrollPolicy.NEVER);
			break;
		default:
			setVerticalScrollPolicy(ScrollPolicy.AUTO);
		}
	}

	public static void ajustWidth(Widget aChild, int aValue) {
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement>cast();
			int hDelta = xwe.getOffsetWidth() - xwe.getContentWidth();
			xwe.getStyle().setWidth(aValue - hDelta, Style.Unit.PX);
		}
	}

	public static void ajustHeight(Widget aChild, int aValue) {
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement>cast();
			int hDelta = xwe.getOffsetHeight() - xwe.getContentHeight();
			xwe.getStyle().setHeight(aValue - hDelta, Style.Unit.PX);
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
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap){
				aComponent.@com.eas.client.form.published.containers.ScrollPane::setWidget(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
			}
		}
		Object.defineProperty(published, "view", {
			get : function()
			{
				var widget = aComponent.@com.eas.client.form.published.containers.ScrollPane::getWidget()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(widget);
			},
			set : function(aValue)
			{
				if(aValue != null)
					published.add(aValue);
				else
					published.clear();
			}
		});
	}-*/;
}

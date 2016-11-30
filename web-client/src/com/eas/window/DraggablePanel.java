/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class DraggablePanel extends SimplePanel implements RequiresResize, ProvidesResize {

	protected interface Resources extends ClientBundle {

		static final Resources instance = GWT.create(Resources.class);

		public ResizableStyles resizable();
	}

	protected interface ResizableStyles extends CssResource {

		public String contentDecor();

		public String horizontalDecor();

		public String northDecor();

		public String southDecor();

		public String verticalDecor();

		public String westDecor();

		public String eastDecor();

		public String cornerDecor();

		public String northWestDecor();

		public String northEastDecor();

		public String southWestDecor();

		public String southEastDecor();
	}

	protected static ResizableStyles styles = Resources.instance.resizable();

	protected abstract class DraggableWidgetDecoration extends DraggableDecoration {

		private boolean mouseDragged;
		
		public DraggableWidgetDecoration(String... aClasses) {
			super(aClasses);
		}

		@Override
		protected void mousePressed() {
			focus();
		}
		
		@Override
		protected void mouseDragged() {
			if(!mouseDragged){
				mouseDragged = true;
				beginResizing();
			}
		}

		@Override
		protected void mouseReleased() {
			mouseDragged = false;
			endResizing();
		}

		@Override
		protected Widget getMovableTarget() {
			return DraggablePanel.this.getMovableTarget();
		}

		@Override
		protected Widget getResizableTarget() {
			return DraggablePanel.this.getWidget();
		}
	}

	protected DraggableDecoration n = new DraggableWidgetDecoration(styles.horizontalDecor(), styles.northDecor(), "horizontal-decor", "north-decor") {

		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			if (isNresizable()) {
				double newTop = targetScrollTop + dy;
				aMovableTarget.getElement().getStyle().setTop(newTop >= 0 ? newTop : 0, Style.Unit.PX);
				if (newTop >= 0) {
					aResizableTarget.getElement().getStyle().setHeight(targetHeight - dy, Style.Unit.PX);
					/*
					if (aResizableTarget instanceof RequiresResize) {
						((RequiresResize) aResizableTarget).onResize();
					}
					*/
				}
				return true;
			} else {
				return false;
			}
		}

	};
	protected DraggableDecoration s = new DraggableWidgetDecoration(styles.horizontalDecor(), styles.southDecor(), "horizontal-decor", "south-decor") {

		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			if (isSresizable()) {
				aResizableTarget.getElement().getStyle().setHeight(targetHeight + dy, Style.Unit.PX);
				/*
				if (aResizableTarget instanceof RequiresResize) {
					((RequiresResize) aResizableTarget).onResize();
				}
				*/
				return true;
			} else {
				return false;
			}
		}

	};
	protected DraggableDecoration w = new DraggableWidgetDecoration(styles.verticalDecor(), styles.westDecor(), "vertical-decor", "west-decor") {

		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			if (isWresizable()) {
				double newLeft = targetScrollLeft + dx;
				aMovableTarget.getElement().getStyle().setLeft(newLeft >= 0 ? newLeft : 0, Style.Unit.PX);
				if (newLeft >= 0) {
					aResizableTarget.getElement().getStyle().setWidth(targetWidth - dx, Style.Unit.PX);
					/*
					if (aResizableTarget instanceof RequiresResize) {
						((RequiresResize) aResizableTarget).onResize();
					}
					*/
				}
				return true;
			} else {
				return false;
			}
		}

	};
	protected DraggableDecoration e = new DraggableWidgetDecoration(styles.verticalDecor(), styles.eastDecor(), "vertical-decor", "east-decor") {
		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			if (isEresizable()) {
				aResizableTarget.getElement().getStyle().setWidth(targetWidth + dx, Style.Unit.PX);
				/*
				if (aResizableTarget instanceof RequiresResize) {
					((RequiresResize) aResizableTarget).onResize();
				}
				*/
				return true;
			} else {
				return false;
			}
		}
	};
	protected DraggableDecoration nw = new DraggableWidgetDecoration(styles.cornerDecor(), styles.northWestDecor(), "corner-decor", "north-west-decor") {

		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			n.assignMouseState(nw);
			n.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			w.assignMouseState(nw);
			w.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			return true;
		}
	};
	protected DraggableDecoration ne = new DraggableWidgetDecoration(styles.cornerDecor(), styles.northEastDecor(), "corner-decor", "north-east-decor") {
		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			n.assignMouseState(ne);
			n.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			e.assignMouseState(ne);
			e.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			return true;
		}
	};
	protected DraggableDecoration sw = new DraggableWidgetDecoration(styles.cornerDecor(), styles.southWestDecor(), "corner-decor", "south-west-decor") {
		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			s.assignMouseState(sw);
			s.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			w.assignMouseState(sw);
			w.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			return true;
		}
	};
	protected DraggableDecoration se = new DraggableWidgetDecoration(styles.cornerDecor(), styles.southEastDecor(), "corner-decor", "south-east-decor") {
		@Override
		protected boolean changeTarget(Widget aMovableTarget, Widget aResizableTarget, int dx, int dy, MouseEvent<?> aEvent) {
			s.assignMouseState(se);
			s.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			e.assignMouseState(se);
			e.changeTarget(aMovableTarget, aResizableTarget, dx, dy, aEvent);
			return true;
		}
	};

	protected final SimplePanel content = new SimplePanel();

	protected boolean undecorated;
	protected boolean resizable = true;

	public DraggablePanel() {
		super();
		init();
	}

	public DraggablePanel(boolean undecorated) {
		super();
		this.undecorated = undecorated;
		init();
	}

	protected void init() {
		styles.ensureInjected();		
		super.setWidget(content);
	}
	
	protected void beginResizing() {
	}

	protected void endResizing() {
		onResize();
	}

	@Override
	public Widget getWidget() {
		return content.getWidget();
	}

	@Override
	public void setWidget(Widget w) {
		content.setWidget(w);
		onResize();
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && !"hidden".equals(getElement().getStyle().getProperty("visibility"));
	}

	public boolean isUndecorated() {
		return undecorated;
	}

	public void setUndecorated(boolean aValue) {
		boolean oldValue = undecorated;
		undecorated = aValue;
		if (oldValue && !undecorated) {
			decorate();
		} else if (!oldValue && undecorated) {
			undecorate();
		}
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean aValue) {
		resizable = aValue;
		updateDecorCursors();
	}

	public void setPosition(double aLeft, double aTop) {
		Element elem = getMovableTarget().getElement();
		elem.getStyle().setLeft(aLeft, Style.Unit.PX);
		elem.getStyle().setTop(aTop, Style.Unit.PX);
		elem.getStyle().setPosition(Style.Position.ABSOLUTE);
	}

	public void setSize(double aWidth, double aHeight) {
		if (getWidget() != null) {
			Element elem = getWidget().getElement();
			elem.getStyle().setWidth(aWidth, Style.Unit.PX);
			elem.getStyle().setHeight(aHeight, Style.Unit.PX);
			onResize();
		}
	}

	@Override
	public void setWidth(String width) {
		if (getWidget() != null) {
			getWidget().setWidth(width);
			if (isAttached()) {
				onResize();
			}
		}
	}

	@Override
	public void setHeight(String height) {
		if (getWidget() != null) {
			getWidget().setHeight(height);
			if (isAttached()) {
				onResize();
			}
		}
	}

	protected void decorate() {
		content.getElement().addClassName(styles.contentDecor());

		Element topElement = getElement();
		// Physical attach
		topElement.appendChild(n.getElement());
		topElement.appendChild(s.getElement());
		topElement.appendChild(w.getElement());
		topElement.appendChild(e.getElement());
		topElement.appendChild(nw.getElement());
		topElement.appendChild(ne.getElement());
		topElement.appendChild(sw.getElement());
		topElement.appendChild(se.getElement());
		// Logical attach
		adopt(e);
		adopt(w);
		adopt(n);
		adopt(s);
		adopt(nw);
		adopt(ne);
		adopt(sw);
		adopt(se);
	}

	protected void undecorate() {
		// Logical detach
		orphan(e);
		orphan(w);
		orphan(n);
		orphan(s);
		orphan(nw);
		orphan(ne);
		orphan(sw);
		orphan(se);
		// Physical detach
		e.getElement().removeFromParent();
		w.getElement().removeFromParent();
		n.getElement().removeFromParent();
		s.getElement().removeFromParent();
		nw.getElement().removeFromParent();
		ne.getElement().removeFromParent();
		sw.getElement().removeFromParent();
		se.getElement().removeFromParent();
		content.getElement().removeClassName(styles.contentDecor());
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (!n.isAttached()) {
			n.onAttach();
		}
		if (!s.isAttached()) {
			s.onAttach();
		}
		if (!e.isAttached()) {
			e.onAttach();
		}
		if (!w.isAttached()) {
			w.onAttach();
		}
		if (!ne.isAttached()) {
			ne.onAttach();
		}
		if (!se.isAttached()) {
			se.onAttach();
		}
		if (!nw.isAttached()) {
			nw.onAttach();
		}
		if (!sw.isAttached()) {
			sw.onAttach();
		}
		onResize();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (n.isAttached()) {
			n.onDetach();
		}
		if (s.isAttached()) {
			s.onDetach();
		}
		if (e.isAttached()) {
			e.onDetach();
		}
		if (w.isAttached()) {
			w.onDetach();
		}
		if (ne.isAttached()) {
			ne.onDetach();
		}
		if (se.isAttached()) {
			se.onDetach();
		}
		if (nw.isAttached()) {
			nw.onDetach();
		}
		if (sw.isAttached()) {
			sw.onDetach();
		}
	}

	protected boolean isNresizable() {
		return resizable;
	}

	protected boolean isSresizable() {
		return resizable;
	}

	protected boolean isWresizable() {
		return resizable;
	}

	protected boolean isEresizable() {
		return resizable;
	}

	protected void updateDecorCursors() {
		if (isNresizable()) {
			n.getElement().getStyle().clearCursor();
		} else {
			n.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isSresizable()) {
			s.getElement().getStyle().clearCursor();
		} else {
			s.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isEresizable()) {
			e.getElement().getStyle().clearCursor();
		} else {
			e.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isWresizable()) {
			w.getElement().getStyle().clearCursor();
		} else {
			w.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isNresizable() && isWresizable()) {
			nw.getElement().getStyle().clearCursor();
		} else if (isNresizable()) {
			nw.getElement().getStyle().setCursor(Style.Cursor.N_RESIZE);
		} else if (isWresizable()) {
			nw.getElement().getStyle().setCursor(Style.Cursor.W_RESIZE);
		} else {
			nw.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isNresizable() && isEresizable()) {
			ne.getElement().getStyle().clearCursor();
		} else if (isNresizable()) {
			ne.getElement().getStyle().setCursor(Style.Cursor.N_RESIZE);
		} else if (isEresizable()) {
			ne.getElement().getStyle().setCursor(Style.Cursor.E_RESIZE);
		} else {
			ne.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isSresizable() && isEresizable()) {
			se.getElement().getStyle().clearCursor();
		} else if (isSresizable()) {
			se.getElement().getStyle().setCursor(Style.Cursor.S_RESIZE);
		} else if (isEresizable()) {
			se.getElement().getStyle().setCursor(Style.Cursor.E_RESIZE);
		} else {
			se.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
		if (isSresizable() && isWresizable()) {
			sw.getElement().getStyle().clearCursor();
		} else if (isSresizable()) {
			sw.getElement().getStyle().setCursor(Style.Cursor.S_RESIZE);
		} else if (isWresizable()) {
			sw.getElement().getStyle().setCursor(Style.Cursor.W_RESIZE);
		} else {
			sw.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
		}
	}

	protected Widget getMovableTarget() {
		return this;
	}

	protected void focus() {
		// no op here. Descendants may put some code here
	}

	@Override
	public void onResize() {
		if (getWidget() instanceof RequiresResize) {
			((RequiresResize) getWidget()).onResize();
		}
	}

}

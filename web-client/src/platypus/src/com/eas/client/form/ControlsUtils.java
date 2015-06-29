package com.eas.client.form;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.XElement;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.form.MarginConstraints.Margin;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedFont;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.MarginsPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.menu.PlatypusMenuBar;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ControlsUtils {

	public static final String DD = "DD";
	public static final String DD_MM = "DD_MM";
	public static final String DD_MM_YYYY = "DD_MM_YYYY";
	public static final String MM = "MM";
	public static final String MM_YYYY = "MM_YYYY";
	public static final String YYYY = "YYYY";
	public static final String MM_SS = "MM_SS";
	public static final String HH_MM = "HH_MM";
	public static final String HH_MM_SS = "HH_MM_SS";
	public static final String DD_MM_YYYY_HH_MM_SS = "DD_MM_YYYY_HH_MM_SS";
	public static final String CONTEXT_MENU = "contextMenu";

	public static String convertDateFormatString(String dateFormat) {
		if (DD.equals(dateFormat))
			return "dd";
		else if (MM.equals(dateFormat))
			return "MM";
		else if (MM_YYYY.equals(dateFormat))
			return "MM.yyyy";
		else if (YYYY.equals(dateFormat))
			return "yyyy";
		else if (DD_MM.equals(dateFormat))
			return "dd.MM";
		else if (DD_MM_YYYY.equals(dateFormat))
			return "dd.MM.yyyy";
		else if (MM_SS.equals(dateFormat))
			return "mm:ss";
		else if (HH_MM.equals(dateFormat))
			return "HH:mm";
		else if (HH_MM_SS.equals(dateFormat))
			return "HH:mm:ss";
		else if (DD_MM_YYYY_HH_MM_SS.equals(dateFormat))
			return "dd.MM.yyyy HH:mm:ss";
		else
			return dateFormat;
	}

	public static void jsSelectFile(final JavaScriptObject aCallback, final String aFileTypes) {
		if (aCallback != null) {
			selectFile(new Callback<JavaScriptObject, String>() {

				@Override
				public void onSuccess(JavaScriptObject result) {
					try {
						Utils.executeScriptEventVoid(aCallback, aCallback, result);
					} catch (Exception ex) {
						Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				@Override
				public void onFailure(String reason) {
				}

			}, aFileTypes);
		}
	}

	public static void selectFile(final Callback<JavaScriptObject, String> aCallback, String aFileTypes) {
		final FileUpload fu = new FileUpload();
		fu.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		fu.setWidth("10px");
		fu.setHeight("10px");
		fu.getElement().getStyle().setLeft(-100, Style.Unit.PX);
		fu.getElement().getStyle().setTop(-100, Style.Unit.PX);
		fu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				JsObject jsFu = fu.getElement().cast();
				JavaScriptObject oFiles = jsFu.getJs("files");
				if (oFiles != null) {
					JsArray<JavaScriptObject> jsFiles = oFiles.cast();
					for (int i = 0; i < jsFiles.length(); i++) {
						try {
							aCallback.onSuccess(jsFiles.get(i));
						} catch (Exception ex) {
							Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				fu.removeFromParent();
			}
		});
		RootPanel.get().add(fu, -100, -100);
		fu.click();
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				fu.removeFromParent();
				return false;
			}
		}, 1000 * 60 * 1);// 1 min
	}

	public static void jsSelectColor(String aOldValue, final JavaScriptObject aCallback) {
		if (aCallback != null) {
			selectColor(aOldValue, new Callback<String, String>() {

				@Override
				public void onSuccess(String result) {
					try {
						Utils.executeScriptEventVoid(aCallback, aCallback, result);
					} catch (Exception ex) {
						Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				@Override
				public void onFailure(String reason) {
				}

			});
		}
	}

	public static void selectColor(String aOldValue, final Callback<String, String> aCallback) {
		final TextBox tmpField = new TextBox();
		tmpField.getElement().setAttribute("type", "color");
		tmpField.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		tmpField.setWidth("10px");
		tmpField.setHeight("10px");
		tmpField.setValue(aOldValue);

		tmpField.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				try {
					aCallback.onSuccess(tmpField.getValue());
				} finally {
					tmpField.removeFromParent();
				}
			}

		});
		RootPanel.get().add(tmpField, 100, 100);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tmpField.setFocus(true);
				click(tmpField.getElement());
				Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
					@Override
					public boolean execute() {
						tmpField.removeFromParent();
						return false;
					}
				}, 1000 * 60 * 1);// 1 min
			}
		});
	}

	public static native void click(Element aElement)/*-{
		aElement.click();
	}-*/;

	protected static RegExp rgbPattern = RegExp.compile("rgb *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *\\)");
	protected static RegExp rgbaPattern = RegExp.compile("rgba *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *, *([0-9]*\\.?[0-9]+) *\\)");

	public static PublishedColor parseColor(String aInput) {
		if (aInput != null && !aInput.isEmpty()) {
			if (aInput.startsWith("#")) {
				Integer intval = Integer.decode(aInput);
				int i = intval.intValue();
				return PublishedColor.create((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, 0xFF);
			} else {
				MatchResult m = rgbPattern.exec(aInput);
				if (m != null) {
					return PublishedColor.create(Integer.valueOf(m.getGroup(1)), // r
					        Integer.valueOf(m.getGroup(2)), // g
					        Integer.valueOf(m.getGroup(3)), // b
					        0xFF); // a
				} else {
					MatchResult m1 = rgbaPattern.exec(aInput);
					if (m1 != null) {
						return PublishedColor.create(Integer.valueOf(m1.getGroup(1)), // r
						        Integer.valueOf(m1.getGroup(2)), // g
						        Integer.valueOf(m1.getGroup(3)), // b
						        Math.round(Float.valueOf(m1.getGroup(3)) * 255)); // a
					}
				}
			}
		}
		return null;
	}

	public static PublishedColor getStyledForeground(XElement aElement) {
		String c = aElement.getStyle().getColor();
		return c != null ? parseColor(c) : null;
	}

	public static PublishedColor getStyledBackground(XElement aElement) {
		String c = aElement.getStyle().getBackgroundColor();
		return c != null ? parseColor(c) : null;
	}

	public static String renderDecorated(SafeHtmlBuilder rendered, String aId, PublishedCell aCell, SafeHtmlBuilder sb) {
		return StyleIconDecorator.decorate(rendered.toSafeHtml(), aId, aCell, HasVerticalAlignment.ALIGN_MIDDLE, sb);
	}

	public static Runnable createScriptSelector(final JavaScriptObject aThis, final JavaScriptObject selectFunction, final JavaScriptObject aPublishedField) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					Utils.executeScriptEventVoid(aThis, selectFunction, aPublishedField);
				} catch (Exception ex) {
					Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		};
	}

	/**
	 * Calculates a published cell for stand-alone model-aware controls against
	 * row aRow.
	 * 
	 * @param aEventThis
	 * @param cellFunction
	 * @param aData
	 * @param aModelElement
	 * @return
	 * @throws Exception
	 */
	public static PublishedCell calcStandalonePublishedCell(JavaScriptObject aEventThis, JavaScriptObject cellFunction, JavaScriptObject aData, String aField, String aDisplay,
	        PublishedCell aAlreadyCell) throws Exception {
		if (aEventThis != null && aField != null && !aField.isEmpty() && cellFunction != null) {
			if (aData != null) {
				PublishedCell cell = aAlreadyCell != null ? aAlreadyCell : Publisher.publishCell(Utils.getPathData(aData, aField), aDisplay);
				Utils.executeScriptEventVoid(aEventThis, cellFunction, JsEvents.publishOnRenderEvent(aEventThis, null, null, aData, cell));
				return cell;
			}
		}
		return null;
	}

	public static PublishedCell calcValuedPublishedCell(JavaScriptObject aEventThis, JavaScriptObject cellFunction, Object aValue, String aDisplay, PublishedCell aAlreadyCell) {
		if (aEventThis != null && cellFunction != null) {
			PublishedCell cell = aAlreadyCell != null ? aAlreadyCell : Publisher.publishCell(Utils.toJs(aValue), aDisplay);
			try {
				Utils.executeScriptEventVoid(aEventThis, cellFunction, JsEvents.publishOnRenderEvent(aEventThis, null, null, null, cell));
			} catch (Exception ex) {
				Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, null, ex);
			}
			return cell;
		} else {
			return null;
		}
	}

	protected static void walkChildren(XElement aRoot, XElement.Observer aCallback) {
		List<Element> children = aRoot.select("*");
		for (int i = 0; i < children.size(); i++) {
			Element el = children.get(i);
			aCallback.observe(el);
		}
	}

	protected static void walkChildren(Element aRoot, Element aStop, XElement.Observer aCallback) {
		NodeList<Node> children = aRoot.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node el = children.getItem(i);
			if (el instanceof Element) {
				aCallback.observe((Element) el);
				if (el != aStop)
					walkChildren((Element) el, aStop, aCallback);
			}
		}
	}

	public static void applyBackground(UIObject aWidget, final PublishedColor aColor) {
		applyBackground(aWidget, aColor != null ? aColor.toStyled() : null);
	}

	public static void applyBackground(UIObject aWidget, final String aColorString) {
		Element aElement = aWidget.getElement();
		if (aElement != null) {
			if (aColorString != null && !aColorString.isEmpty()) {
				aElement.getStyle().setBackgroundColor(aColorString);
				aElement.getStyle().setBackgroundImage("none");
			} else {
				aElement.getStyle().clearBackgroundColor();
				aElement.getStyle().clearBackgroundImage();
			}
		}
	}

	public static void applyForeground(UIObject aWidget, final PublishedColor aColor) {
		Element aElement = aWidget.getElement();
		if (aColor != null)
			aElement.getStyle().setColor(aColor.toStyled());
		else
			aElement.getStyle().clearColor();
	}

	public static void applyFont(UIObject aWidget, final PublishedFont aFont) {
		Element aElement = aWidget.getElement();
		aElement.getStyle().setProperty("fontFamily", aFont != null ? aFont.getFamily() : "");
		if (aFont != null) {
			aElement.getStyle().setFontSize(aFont.getSize(), Style.Unit.PT);
			if (aFont.isBold())
				aElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
			// aElement.getStyle().setFontWeight(aFont.isBold() ?
			// FontWeight.BOLD : FontWeight.NORMAL);
			if (aFont.isItalic())
				aElement.getStyle().setFontStyle(Style.FontStyle.ITALIC);
			// aElement.getStyle().setFontStyle(aFont.isItalic() ?
			// FontStyle.ITALIC : FontStyle.NORMAL);
		} else {
			aElement.getStyle().clearFontSize();
			aElement.getStyle().clearFontWeight();
			aElement.getStyle().clearFontStyle();
		}
	}

	public static void applyCursor(UIObject aWidget, final String aCursor) {
		aWidget.getElement().getStyle().setProperty("cursor", aCursor != null ? aCursor : "");
	}

	public static void reapplyStyle(HasPublished aComponent) {
		if (aComponent instanceof UIObject && aComponent.getPublished() != null) {
			PublishedComponent published = aComponent.getPublished().cast();
			if (published.isBackgroundSet() && published.isOpaque())
				ControlsUtils.applyBackground((UIObject) aComponent, published.getBackground());
			if (published.isForegroundSet())
				ControlsUtils.applyForeground((UIObject) aComponent, published.getForeground());
			if (published.isFontSet())
				ControlsUtils.applyFont((UIObject) aComponent, published.getFont());
			if (published.isCursorSet())
				ControlsUtils.applyCursor((UIObject) aComponent, published.getCursor());
		}
	}

	public static JavaScriptObject lookupPublishedParent(UIObject aWidget) {
		assert aWidget != null;
		UIObject parent = aWidget;
		do {
			if (parent instanceof PlatypusMenuBar) {
				PlatypusMenuBar bar = (PlatypusMenuBar) parent;
				parent = bar.getParentItem();
				if (parent == null) {
					parent = bar.getParent();
				}
			} else if (parent instanceof Widget) {
				parent = ((Widget) parent).getParent();
			} else if (parent instanceof MenuItemSeparator) {
				MenuItemSeparator sep = (MenuItemSeparator) parent;
				parent = sep.getParentMenu();
			} else if (parent instanceof MenuItem) {
				MenuItem sep = (MenuItem) parent;
				parent = sep.getParentMenu();
			} else {
				parent = null;
			}
		} while (parent != null && (!(parent instanceof HasPublished) || (((HasPublished) parent).getPublished() == null)));
		return parent != null ? ((HasPublished) parent).getPublished() : null;
	}

	public static void addWidgetTo(Widget aWidet, String aElementId) {
		addWidgetTo(aWidet, RootPanel.get(aElementId));
	}

	public static void addWidgetTo(Widget aWidet, Element aElement) {
		addWidgetTo(aWidet, new StandaloneRootPanel(aElement));
	}

	public static void addWidgetTo(Widget aWidet, HasWidgets aContainer) {
		if (aContainer != null) {
			aWidet.setVisible(true);
			aContainer.clear();
			if (aContainer instanceof BorderPane) {
				((BorderPane) aContainer).add(aWidet);
			} else if (aContainer instanceof MarginsPane) {
				MarginConstraints mc = new MarginConstraints();
				mc.setTop(new Margin(0, Style.Unit.PX));
				mc.setBottom(new Margin(0, Style.Unit.PX));
				mc.setLeft(new Margin(0, Style.Unit.PX));
				mc.setRight(new Margin(0, Style.Unit.PX));
				((MarginsPane) aContainer).add(aWidet, mc);
			} else if (aContainer instanceof SplitPane) {
				((SplitPane) aContainer).setFirstWidget(aWidet);
			} else if (aContainer instanceof RootPanel) {
				aContainer.add(aWidet);
			} else {
				aContainer.add(aWidet);
			}
		}
	}

	public static void applyEmptyText(Element aElement, String aValue) {
		NodeList<Element> nodes = aElement.getElementsByTagName("input");
		for (int i = 0; i < nodes.getLength(); i++) {
			nodes.getItem(i).setAttribute("placeholder", aValue);
		}
		NodeList<Element> nodes1 = aElement.getElementsByTagName("textarea");
		for (int i = 0; i < nodes1.getLength(); i++) {
			nodes1.getItem(i).setAttribute("placeholder", aValue);
		}
		if ("input".equalsIgnoreCase(aElement.getTagName()) || "textarea".equalsIgnoreCase(aElement.getTagName())) {
			aElement.setAttribute("placeholder", aValue);
		}
	}

	public static void callOnResize(Widget aWidget) {
		if (aWidget instanceof RequiresResize) {
			((RequiresResize) aWidget).onResize();
		}
	}

	public static void walk(Widget aWidget, Callback<Widget, Widget> aObserver) {
		aObserver.onSuccess(aWidget);
		if (aWidget instanceof HasWidgets) {
			HasWidgets widgets = (HasWidgets) aWidget;
			Iterator<Widget> wIt = widgets.iterator();
			while (wIt.hasNext()) {
				walk(wIt.next(), aObserver);
			}
		}
	}

	public static void focus(Widget aWidget) {
		if (aWidget instanceof Focusable) {
			((Focusable) aWidget).setFocus(true);
		}
	}
}

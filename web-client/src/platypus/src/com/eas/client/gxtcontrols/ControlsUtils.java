package com.eas.client.gxtcontrols;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.Callback;
import com.eas.client.Utils;
import com.eas.client.form.Form;
import com.eas.client.form.api.JSEvents;
import com.eas.client.gxtcontrols.grid.ModelGrid;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.published.PublishedCell;
import com.eas.client.gxtcontrols.published.PublishedColor;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.gxtcontrols.published.PublishedFont;
import com.eas.client.gxtcontrols.published.PublishedStyle;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusFieldSet;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.form.AdapterField;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.grid.Grid;

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

	protected static class XFileUploadField extends FileUploadField {

		public XFileUploadField() {
			super();
		}

		public void scheduleSelect() {
			getFileInput().click();
		}

		public native JsArray<JavaScriptObject> getFiles()/*-{
			var element = this.@com.sencha.gxt.widget.core.client.form.FileUploadField::getFileInput()();
			return element.files;
		}-*/;

	}

	public static void jsSelectFile(final JavaScriptObject aCallback) {
		if (aCallback != null) {
			selectFile(new Callback<JavaScriptObject>() {
				@Override
				public void run(JavaScriptObject aResult) throws Exception {
					Utils.executeScriptEventVoid(aCallback, aCallback, aResult);
				}

				@Override
				public void cancel() {
				}
			});
		}
	}

	public static void selectFile(final Callback<JavaScriptObject> aCallback) {
		final XFileUploadField fu = new XFileUploadField();
		fu.getElement().setDisplayed(false);
		RootPanel.get().add(fu);
		fu.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (fu.getFiles() != null) {
					for (int i = 0; i < fu.getFiles().length(); i++) {
						try {
							aCallback.run(fu.getFiles().get(i));
						} catch (Exception ex) {
							Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				fu.removeFromParent();
			}
		});
		fu.scheduleSelect();
		/*
		 * Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		 * 
		 * @Override public void execute() { fu.removeFromParent(); } });
		 */
	}

	protected static RegExp rgbPattern = RegExp.compile("rgb *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *\\)");
	protected static RegExp rgbaPattern = RegExp.compile("rgba *\\( *([0-9]+) *, *([0-9]+) *, *([0-9]+) *, *([0-9]*\\.?[0-9]+) *\\)");

	public static PublishedColor parseColor(String aInput) {
		if (aInput != null && !aInput.isEmpty()) {
			if (aInput.startsWith("#")) {
				Integer intval = Integer.decode(aInput);
				int i = intval.intValue();
				return PublishedColor.create((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, 1);
			} else {
				MatchResult m = rgbPattern.exec(aInput);
				if (m != null) {
					return PublishedColor.create(Integer.valueOf(m.getGroup(1)), // r
					        Integer.valueOf(m.getGroup(2)), // g
					        Integer.valueOf(m.getGroup(3)), // b
					        1); // a
				} else {
					MatchResult m1 = rgbaPattern.exec(aInput);
					if (m1 != null) {
						return PublishedColor.create(Integer.valueOf(m1.getGroup(1)), // r
						        Integer.valueOf(m1.getGroup(2)), // g
						        Integer.valueOf(m1.getGroup(3)), // b
						        Float.valueOf(m1.getGroup(3))); // a
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

	public static void renderDecorated(SafeHtmlBuilder rendered, PublishedStyle aStyle, SafeHtmlBuilder sb) {
		if (aStyle != null) {
			StyleIconDecorator.decorate(rendered.toSafeHtml(), aStyle, HasVerticalAlignment.ALIGN_MIDDLE, sb);
		} else
			sb.append(rendered.toSafeHtml());
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
	 * Calculates a published cell for standalone model-aware controls against
	 * row aRow.
	 * 
	 * @param aEventThis
	 * @param cellFunction
	 * @param aRow
	 * @param aModelElement
	 * @return
	 * @throws Exception
	 */
	public static PublishedCell calcStandalonePublishedCell(JavaScriptObject aEventThis, JavaScriptObject cellFunction, Row aRow, String aDisplay, ModelElementRef aModelElement) throws Exception {
		if (aEventThis != null && aModelElement != null && cellFunction != null) {
			if (aRow != null) {
				PublishedCell cell = Publisher.publishCell(Utils.toJs(aRow.getColumnObject(aModelElement.getColIndex())), aDisplay);
				Object[] rowIds = aRow.getPKValues();
				if (rowIds != null) {
					for (int i = 0; i < rowIds.length; i++)
						rowIds[i] = Utils.toJs(rowIds[i]);
				}
				Boolean res = Utils.executeScriptEventBoolean(
				        aEventThis,
				        cellFunction,
				        JSEvents.publishOnRenderEvent(aEventThis, rowIds != null && rowIds.length > 0 ? (rowIds.length > 1 ? Utils.toJsArray(rowIds) : rowIds[0]) : null, null,
				                Entity.publishRowFacade(aRow, aModelElement.entity), cell));
				if (res != null && res) {
					return cell;
				}
			}
		}
		return null;
	}

	protected interface ElementCallback {
		public void run(Element aElement);
	}

	protected static void walkChildren(XElement aRoot, ElementCallback aCallback) {
		NodeList<Element> children = aRoot.select("*");
		for (int i = 0; i < children.getLength(); i++) {
			Element el = children.getItem(i);
			aCallback.run(el);
		}
	}

	protected static void walkChildren(Element aRoot, Element aStop, ElementCallback aCallback) {
		NodeList<Node> children = aRoot.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node el = children.getItem(i);
			if (el instanceof Element) {
				aCallback.run((Element) el);
				if (el != aStop)
					walkChildren((Element) el, aStop, aCallback);
			}
		}
	}

	public static void applyBackground(Widget aWidget, final PublishedColor aColor) {
		applyBackground(aWidget, aColor != null ? aColor.toStyled() : null);
	}

	public static void applyBackground(Widget aWidget, final String aColorString) {
		ElementCallback worker = new ElementCallback() {

			@Override
			public void run(Element aElement) {
				if (aElement != null && aElement.getStyle() != null) {
					if (aColorString != null && !aColorString.isEmpty()) {
						aElement.getStyle().setBackgroundColor(aColorString);
						aElement.getStyle().setBackgroundImage("none");
					} else {
						aElement.getStyle().clearBackgroundColor();
						aElement.getStyle().clearBackgroundImage();
					}
				}
			}

		};
		applyStylePart(aWidget, worker);
		if (aWidget.getParent() instanceof Window) {
			Window w = (Window) aWidget.getParent();
			if (w.getWidget() == aWidget) {
				Element stop = aWidget.getElement();
				if (aWidget instanceof PlatypusMarginLayoutContainer) {
					stop = ((PlatypusMarginLayoutContainer) aWidget).getWidget().getElement();
				}
				if (aWidget instanceof PlatypusFieldSet) {
					stop = ((PlatypusFieldSet) aWidget).getWidget().getElement();
				}
				if (aWidget instanceof PlatypusTabsContainer) {
					stop = ((PlatypusTabsContainer) aWidget).getWidget().getElement();
				}
				walkChildren(w.getElement(), stop, worker);
			}
		}
	}

	public static void applyForeground(Widget aWidget, final PublishedColor aColor) {
		applyStylePart(aWidget, new ElementCallback() {

			@Override
			public void run(Element aElement) {
				if (aColor != null)
					aElement.getStyle().setColor(aColor.toStyled());
				else
					aElement.getStyle().clearColor();
			}

		});
	}

	public static void applyFont(Widget aWidget, final PublishedFont aFont) {
		applyStylePart(aWidget, new ElementCallback() {

			@Override
			public void run(Element aElement) {
				aElement.getStyle().setProperty("fontFamily", aFont != null ? aFont.getFamily() : "");
				if (aFont != null) {
					aElement.getStyle().setFontSize(aFont.getSize(), Unit.PT);
					aElement.getStyle().setFontWeight(aFont.isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
					aElement.getStyle().setFontStyle(aFont.isItalic() ? FontStyle.ITALIC : FontStyle.NORMAL);
				} else {
					aElement.getStyle().clearFontSize();
					aElement.getStyle().clearFontWeight();
					aElement.getStyle().clearFontStyle();
				}
			}

		});
	}

	public static void applyCursor(Widget aWidget, final String aCursor) {
		applyStylePart(aWidget, new ElementCallback() {

			@Override
			public void run(Element aElement) {
				aElement.getStyle().setProperty("cursor", aCursor != null ? aCursor : "");
			}

		});
	}

	protected static void applyStylePart(Widget aWidget, ElementCallback aProcessor) {
		boolean force = false;
		if (aWidget instanceof AdapterField<?>) {
			force = true;
			aWidget = ((AdapterField<?>) aWidget).getWidget();
		} else if (aWidget instanceof ModelGrid) {
			aWidget = ((ModelGrid) aWidget).getWidget();
		}
		XElement rootElement = aWidget.getElement().<XElement> cast();
		if (aWidget instanceof Grid<?>) {
			Grid<?> grid = (Grid<?>) aWidget;
			rootElement = grid.getView().getBody();
		}
		if (rootElement != null) {
			aProcessor.run(rootElement);
			if (force || !(aWidget instanceof Container)) {
				walkChildren(rootElement, aProcessor);
			}
		}
	}

	public static void reapplyStyle(Component aComponent) {
		PublishedComponent published = aComponent.getData(Form.PUBLISHED_DATA_KEY);
		if (published.isBackgroundSet())
			ControlsUtils.applyBackground(aComponent, published.getBackground());
		if (published.isForegroundSet())
			ControlsUtils.applyForeground(aComponent, published.getForeground());
		if (published.isFontSet())
			ControlsUtils.applyFont(aComponent, published.getFont());
		if (published.isCursorSet())
			ControlsUtils.applyCursor(aComponent, published.getCursor());
    }
}

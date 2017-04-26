package com.eas.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.core.HasPublished;
import com.eas.core.Utils;
import com.eas.form.PlatypusWindow;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusMenu;
import com.eas.menu.PlatypusMenuBar;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.widgets.AnchorsPane;
import com.eas.widgets.BorderPane;
import com.eas.widgets.BoxPane;
import com.eas.widgets.CardPane;
import com.eas.widgets.FlowPane;
import com.eas.widgets.GridPane;
import com.eas.widgets.ScrollPane;
import com.eas.widgets.SplitPane;
import com.eas.widgets.TabbedPane;
import com.eas.widgets.ToolBar;
import com.eas.widgets.boxes.DropDownButton;
import com.eas.widgets.boxes.ImageButton;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;

public class DefaultUiReader extends UiReader {

	protected static class Dimension {
		public int width;
		public int height;
	}

	protected String rootContainerName;
	protected Element element;
	protected Map<String, UIObject> widgets = new HashMap<>();
	protected List<UIObject> widgetsList = new ArrayList<>();
	protected List<Runnable> resolvers = new ArrayList<>();
	protected UIObject viewWidget;
	protected Utils.JsObject model;

	public DefaultUiReader(Element anElement, JavaScriptObject aModel) {
		super();
		element = anElement;
		model = aModel != null ? aModel.<Utils.JsObject> cast() : null;
	}

        @Override
	public Map<String, UIObject> getWidgets() {
		return widgets;
	}

	public List<UIObject> getWidgetsList() {
		return widgetsList;
	}

	public void parse() throws Exception {
		if (!element.hasAttribute("view")) {
			throw new IllegalStateException("Old form format detected. Please open the form with a new form designer and re-save it.");
		} else {
			rootContainerName = element.getAttribute("view");
			Node childNode = element.getFirstChild();
			while (childNode != null) {
				if (childNode instanceof Element) {
					UIObject widget = readWidget((Element) childNode);
					if (widget != null) {
						String wName = ((HasJsName) widget).getJsName();
						assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
						widgets.put(wName, widget);
						widgetsList.add(widget);
					} else {
						Logger.getLogger(DefaultUiReader.class.getName()).log(Level.WARNING, "Unknown widget tag name: " + ((Element) childNode).getTagName() + ". skipping.");
					}
				}
				childNode = childNode.getNextSibling();
			}
		}
		for (int i = 0; i < resolvers.size(); i++) {
			Runnable r = resolvers.get(i);
			r.run();
		}
		viewWidget = widgets.get(rootContainerName);
		if (viewWidget == null) {
			viewWidget = new AnchorsPane();
			viewWidget.setSize(400 + "px", 300 + "px");
			Logger.getLogger(DefaultUiReader.class.getName()).log(Level.WARNING, "view widget missing. Falling back to AnchrosPane.");
		}
	}

	public UIObject getViewWidget() {
		return viewWidget;
	}

	protected Dimension readPrefSize(Element anElement) throws NumberFormatException {
		if (Utils.hasAttribute(anElement, "pw", "prefWidth") && Utils.hasAttribute(anElement, "ph", "prefHeight")) {
			Dimension prefSize = new Dimension();
			String prefWidth = Utils.getAttribute(anElement, "pw", "prefWidth", null);
			String prefHeight = Utils.getAttribute(anElement, "ph", "prefHeight", null);
			if (prefWidth.length() > 2 && prefWidth.endsWith("px")) {
				prefSize.width = Integer.parseInt(prefWidth.substring(0, prefWidth.length() - 2));
			}
			if (prefHeight.length() > 2 && prefHeight.endsWith("px")) {
				prefSize.height = Integer.parseInt(prefHeight.substring(0, prefHeight.length() - 2));
			}
			return prefSize;
		} else
			return null;
	}

        @Override
	public void addResolver(Runnable aResolver) {
		resolvers.add(aResolver);
	}

        @Override
	public UIObject readWidget(final Element anElement) throws Exception {
		for (UiWidgetReader reader : UiReader.getFactories()) {
			UIObject read = reader.readWidget(anElement, this);
			if (read != null) {
				return read;
			}
		}
		return null;
	}

        @Override
	public void readImageParagraph(Element anElement, final UIObject aImageParagraph) throws Exception {
		if (Utils.hasAttribute(anElement, "i", "icon") && aImageParagraph instanceof HasImageResource) {
			String iconImage = Utils.getAttribute(anElement, "i", "icon", null);
			PlatypusImageResource.load(iconImage, new CallbackAdapter<ImageResource, String>() {
				@Override
				protected void doWork(ImageResource aResult) throws Exception {
					((HasImageResource) aImageParagraph).setImageResource(aResult);
				}

				@Override
				public void onFailure(String reason) {
					Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, "Factory failed to load button icon. " + reason);
				}
			});
		}
		if (Utils.hasAttribute(anElement, "tx", "text") && aImageParagraph instanceof HasText) {
			((HasText) aImageParagraph).setText(Utils.getAttribute(anElement, "tx", "text", null));
		}
		if (aImageParagraph instanceof HasImageParagraph) {
			HasImageParagraph hip = (HasImageParagraph) aImageParagraph;
			hip.setHorizontalAlignment(Utils.getIntegerAttribute(anElement, "ha", "horizontalAlignment",
			        aImageParagraph instanceof ImageButton || aImageParagraph instanceof DropDownButton ? HasImageParagraph.CENTER : HasImageParagraph.LEFT));
			hip.setVerticalAlignment(Utils.getIntegerAttribute(anElement, "va", "verticalAlignment", HasImageParagraph.CENTER));
			hip.setIconTextGap(Utils.getIntegerAttribute(anElement, "itg", "iconTextGap", 4));
			hip.setHorizontalTextPosition(Utils.getIntegerAttribute(anElement, "htp", "horizontalTextPosition", HasImageParagraph.RIGHT));
			hip.setVerticalTextPosition(Utils.getIntegerAttribute(anElement, "vtp", "verticalTextPosition", HasImageParagraph.CENTER));
		}
	}

        @Override
	public void readGeneralProps(final Element anElement, final UIObject aTarget) throws Exception {
		String widgetName = "";
		if (Utils.hasAttribute(anElement, "n", "name") && aTarget instanceof HasJsName) {
			widgetName = Utils.getAttribute(anElement, "n", "name", null);
			((HasJsName) aTarget).setJsName(widgetName);
		}
		/*
		 * if (Utils.hasAttribute(anElement, "e", "editable") && aTarget
		 * instanceof HasEditable) { ((HasEditable)
		 * aTarget).setEditable(Utils.getBooleanAttribute(anElement, "e",
		 * "editable", Boolean.TRUE)); }
		 */
		if (Utils.hasAttribute(anElement, "et", "emptyText") && aTarget instanceof HasEmptyText) {
			((HasEmptyText) aTarget).setEmptyText(Utils.getAttribute(anElement, "et", "emptyText", null));
		}
		if (aTarget instanceof HasBinding) {
			if (Utils.hasAttribute(anElement, "f", "field")) {
				String fieldPath = Utils.getAttribute(anElement, "f", "field", null);
				try {
					((HasBinding) aTarget).setField(fieldPath);
				} catch (Exception ex) {
					Logger.getLogger(DefaultUiReader.class.getName()).log(Level.SEVERE, "While setting field (" + fieldPath + ") to widget " + widgetName + " exception occured: " + ex.getMessage());
				}
			}
			if (Utils.hasAttribute(anElement, "d", "data")) {
				String entityName = Utils.getAttribute(anElement, "d", "data", null);
				try {
					((HasBinding) aTarget).setData(resolveEntity(entityName));
				} catch (Exception ex) {
					Logger.getLogger(DefaultUiReader.class.getName()).log(Level.SEVERE,
					        "While setting data to named model's property (" + entityName + ") to widget " + widgetName + " exception occured: " + ex.getMessage());
				}
			}
		}
		if (aTarget instanceof HasEnabled)
			((HasEnabled) aTarget).setEnabled(Utils.getBooleanAttribute(anElement, "en", "enabled", Boolean.TRUE));
		// aTarget.setFocusable(Utils.getBooleanAttribute(anElement, "fc",
		// "focusable", Boolean.TRUE));
		if (aTarget instanceof Widget && aTarget instanceof HasPublished) {
			PublishedComponent pComp = ((HasPublished) aTarget).getPublished().cast();
			PublishedFont font = readFont(anElement);
			if (font != null) {
				pComp.setFont(font);
			}
			if (Utils.hasAttribute(anElement, "o", "opaque")) {
				pComp.setOpaque(Utils.getBooleanAttribute(anElement, "o", "opaque", Boolean.TRUE));
			}
			if (Utils.hasAttribute(anElement, "bg", "background")) {
				PublishedColor background = PublishedColor.parse(Utils.getAttribute(anElement, "bg", "background", null));
				pComp.setBackground(background);
			}
			if (Utils.hasAttribute(anElement, "fg", "foreground")) {
				PublishedColor foreground = PublishedColor.parse(Utils.getAttribute(anElement, "fg", "foreground", null));
				pComp.setForeground(foreground);
			}
			if (Utils.hasAttribute(anElement, "ttt", "toolTipText")) {
				pComp.setToolTipText(Utils.getAttribute(anElement, "ttt", "toolTipText", null));
			}
		}
		if (Utils.hasAttribute(anElement, "v", "visible")) {
			aTarget.setVisible(Utils.getBooleanAttribute(anElement, "v", "visible", Boolean.TRUE));
		}
		if (Utils.hasAttribute(anElement, "cpm", "componentPopupMenu") && aTarget instanceof HasComponentPopupMenu) {
			final String popupName = Utils.getAttribute(anElement, "cpm", "componentPopupMenu", null);
			if (popupName != null && !popupName.isEmpty()) {
				resolvers.add(new Runnable() {
                                        @Override
					public void run() {
						UIObject popup = widgets.get(popupName);
						if (popup instanceof PlatypusPopupMenu) {
							((HasComponentPopupMenu) aTarget).setPlatypusPopupMenu((PlatypusPopupMenu) popup);
						}
					}
				});
			}
		}
		if (Utils.hasAttribute(anElement, "bgr", "buttonGroup") && aTarget instanceof HasPlatypusButtonGroup) {
			final String buttonGroupName = Utils.getAttribute(anElement, "bgr", "buttonGroup", null);
			if (buttonGroupName != null && !buttonGroupName.isEmpty()) {
				resolvers.add(new Runnable() {
                                        @Override
					public void run() {
						UIObject buttonGroup = widgets.get(buttonGroupName);
						if (buttonGroup instanceof ButtonGroup) {
							ButtonGroup bg = (ButtonGroup) buttonGroup;
							bg.add((HasValue<Boolean>) aTarget);
						}
					}
				});
			}
		}
		if (Utils.hasAttribute(anElement, "p", "parent")) {
			final String parentName = Utils.getAttribute(anElement, "p", "parent", null);
			if (parentName != null && !parentName.isEmpty()) {
				resolvers.add(new Runnable() {
                                        @Override
					public void run() {
						UIObject parent = widgets.get(parentName);
						try {
							addToParent(anElement, aTarget, parent);
						} catch (Exception ex) {
							Logger.getLogger(DefaultUiReader.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});
			}
		}
		if (aTarget instanceof Widget && aTarget instanceof HasPublished && !(aTarget instanceof PlatypusMenu)) {
			Dimension prefSize = readPrefSize(anElement);
			if (prefSize != null) {
				PublishedComponent pComp = ((HasPublished) aTarget).getPublished().<PublishedComponent> cast();
                                pComp.setWidth(prefSize.width);
                                pComp.setHeight(prefSize.height);
			}
		}
	}

        @Override
	public PublishedFont readFont(Element anElement) throws Exception {
		PublishedFont font = readFontTag(anElement, "ft", "font");
		if (font != null) {
			return font;
		} else {
			return null;
		}
	}

	private PublishedFont readFontTag(Element anElement, String aShortName, String aLongName) throws Exception {
		Element easFontElement = Utils.scanForElementByTagName(anElement, aShortName, aLongName);
		if (easFontElement != null) {
			String name = Utils.getAttribute(easFontElement, "n", "name", null);
			if (name == null || name.isEmpty() || "null".equals(name)) {
				name = "Arial";
			}
			int style = Utils.getIntegerAttribute(easFontElement, "stl", "style", 0);
			int size = Utils.getIntegerAttribute(easFontElement, "sz", "size", 12);
			return PublishedFont.create(name, style, size);
		} else {
			return null;
		}
	}

	private void addToParent(Element anElement, UIObject aTarget, UIObject parent) throws Exception {
		if (parent instanceof PlatypusMenu) {
			((PlatypusMenu) parent).add(aTarget);
		} else if (parent instanceof PlatypusMenuBar) {
			((PlatypusMenuBar) parent).add(aTarget);
		} else if (parent instanceof PlatypusPopupMenu) {
			((PlatypusPopupMenu) parent).add(aTarget);
		} else if (parent instanceof ToolBar) {
			((ToolBar) parent).add((Widget) aTarget);
		} else if (parent instanceof TabbedPane) {
			Element constraintsElement = Utils.scanForElementByTagName(anElement, "tpc", "TabbedPaneConstraints");
			String tabTitle = Utils.getAttribute(constraintsElement, "tt", "tabTitle", null);
			String tabIconName = Utils.getAttribute(constraintsElement, "ti", "tabIcon", null);
			String tabTooltipText = Utils.getAttribute(constraintsElement, "ttp", "tabTooltipText", null);
			((TabbedPane) parent).add((Widget) aTarget, tabTitle, false, null);
			// ((TabbedPane) parent).add(aTarget, tabTitle,
			// resolveIcon(tabIconName));
		} else if (parent instanceof SplitPane) {
			// Split pane children are:
			// - left component
			// - right component
			// Theese children are setted while resolving component references
			// of a split pane.
		} else if (parent instanceof ScrollPane) {
			ScrollPane scroll = (ScrollPane) parent;
			// Dimension prefSize = readPrefSize(anElement);
			// aTarget.setSize(prefSize.width + "px", prefSize.height + "px");
			scroll.setWidget((Widget) aTarget);
		} else if (parent instanceof BorderPane) {
			Element constraintsElement = Utils.scanForElementByTagName(anElement, "bpc", "BorderPaneConstraints");
			Dimension prefSize = readPrefSize(anElement);
			Integer place = Utils.getIntegerAttribute(constraintsElement, "pl", "place", HorizontalPosition.CENTER);
			Integer size = 0;
			switch (place) {
			case HorizontalPosition.LEFT:
				size = prefSize.width;
				break;
			case HorizontalPosition.RIGHT:
				size = prefSize.width;
				break;
			case VerticalPosition.TOP:
				size = prefSize.height;
				break;
			case VerticalPosition.BOTTOM:
				size = prefSize.height;
				break;
			}
			BorderPane borderPane = (BorderPane) parent;
			borderPane.add((Widget) aTarget, place, size);
		} else if (parent instanceof BoxPane) {
			Dimension prefSize = readPrefSize(anElement);
			BoxPane box = (BoxPane) parent;
			if (box.getOrientation() == Orientation.HORIZONTAL) {
				box.add((Widget) aTarget, prefSize.width);
			} else {
				box.add((Widget) aTarget, prefSize.height);
			}
		} else if (parent instanceof CardPane) {
			Element constraintsElement = Utils.scanForElementByTagName(anElement, "cpc", "CardPaneConstraints");
			String cardName = Utils.getAttribute(constraintsElement, "cn", "cardName", null);
			((CardPane) parent).add((Widget) aTarget, cardName);
		} else if (parent instanceof FlowPane) {
			// Dimension prefSize = readPrefSize(anElement);
			// aTarget.setSize(prefSize.width + "px", prefSize.height + "px");
			((FlowPane) parent).add((Widget) aTarget);
		} else if (parent instanceof GridPane) {
			GridPane gridPane = (GridPane) parent;
			gridPane.addToFreeCell((Widget) aTarget);
		} else if (parent instanceof AnchorsPane) {
			Element constraintsElement = Utils.scanForElementByTagName(anElement, "apc", "AnchorsPaneConstraints");
			MarginConstraints constraints = readMarginConstraints(constraintsElement);
			((AnchorsPane) parent).add((Widget) aTarget, constraints);
		}
	}

        @Override
	public Utils.JsObject resolveEntity(String aEntityName) throws Exception {
		if (model.has(aEntityName)) {
			JavaScriptObject oEntity = model.getJs(aEntityName);
			if (oEntity != null) {
				return oEntity.cast();
			}
		}
		return null;
	}

	private static MarginConstraints readMarginConstraints(Element anElement) {
		MarginConstraints result = new MarginConstraints();
		if (Utils.hasAttribute(anElement, "l", "left")) {
			result.setLeft(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "l", "left", null)));
		}
		if (Utils.hasAttribute(anElement, "r", "right")) {
			result.setRight(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "r", "right", null)));
		}
		if (Utils.hasAttribute(anElement, "t", "top")) {
			result.setTop(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "t", "top", null)));
		}
		if (Utils.hasAttribute(anElement, "b", "bottom")) {
			result.setBottom(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "b", "bottom", null)));
		}
		if (Utils.hasAttribute(anElement, "w", "width")) {
			result.setWidth(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "w", "width", null)));
		}
		if (Utils.hasAttribute(anElement, "h", "height")) {
			result.setHeight(MarginConstraints.Margin.parse(Utils.getAttribute(anElement, "h", "height", null)));
		}
		return result;
	}
}

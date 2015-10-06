/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.CallbackAdapter;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusMenu;
import com.eas.menu.PlatypusMenuBar;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.predefine.HasPublished;
import com.eas.predefine.Utils;
import com.eas.ui.ButtonGroup;
import com.eas.ui.HasBinding;
import com.eas.ui.HasEmptyText;
import com.eas.ui.HasImageParagraph;
import com.eas.ui.HasImageResource;
import com.eas.ui.HasJsName;
import com.eas.ui.HasPlatypusButtonGroup;
import com.eas.ui.HorizontalPosition;
import com.eas.ui.MarginConstraints;
import com.eas.ui.Orientation;
import com.eas.ui.PlatypusImageResource;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedComponent;
import com.eas.ui.PublishedFont;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.ui.VerticalPosition;
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

/**
 * 
 * @author mg
 */
public class FormFactory extends UiReader {

	protected static class Dimension {
		public int width;
		public int height;
	}

	protected Element element;
	protected Utils.JsObject model;
	protected PlatypusWindow form;
	protected Map<String, UIObject> widgets = new HashMap<>();
	protected List<UIObject> widgetsList = new ArrayList<>();
	protected String rootContainerName;
	//
	protected List<Runnable> resolvers = new ArrayList<>();

	public FormFactory(Element anElement, JavaScriptObject aModel) {
		super();
		element = anElement;
		model = aModel != null ? aModel.<Utils.JsObject> cast() : null;
	}

	public Map<String, UIObject> getWidgets() {
		return widgets;
	}

	public List<UIObject> getWidgetsList() {
		return widgetsList;
	}

	public PlatypusWindow getForm() {
		return form;
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
						Logger.getLogger(FormFactory.class.getName()).log(Level.WARNING, "Unknown widget tag name: " + ((Element)childNode).getTagName() + ". skipping.");
					}
				}
				childNode = childNode.getNextSibling();
			}
		}
		UIObject viewWidget = widgets.get(rootContainerName);
		if (viewWidget == null) {
			viewWidget = new AnchorsPane();
			viewWidget.setSize(400 + "px", 300 + "px");
			Logger.getLogger(FormFactory.class.getName()).log(Level.WARNING, "view widget missing. Falling back to AnchrosPane.");
		}
		form = new PlatypusWindow((Widget) viewWidget);
		form.setDefaultCloseOperation(Utils.getIntegerAttribute(element, "defaultCloseOperation", 2));
		String iconImage = element.getAttribute("icon");
		if (iconImage != null && !iconImage.isEmpty()) {
			PlatypusImageResource.load(iconImage, new CallbackAdapter<ImageResource, String>() {
				@Override
				protected void doWork(ImageResource aResult) throws Exception {
					form.setIcon(aResult);
				}

				@Override
				public void onFailure(String reason) {
					Logger.getLogger(PlatypusWindow.class.getName()).log(Level.SEVERE, "Factory failed to load window title icon. " + reason);
				}
			});
		}
		form.setTitle(element.getAttribute("title"));
		form.setMaximizable(Utils.getBooleanAttribute(element, "maximizable", Boolean.TRUE));
		form.setMinimizable(Utils.getBooleanAttribute(element, "minimizable", Boolean.TRUE));
		form.setResizable(Utils.getBooleanAttribute(element, "resizable", Boolean.TRUE));
		form.setUndecorated(Utils.getBooleanAttribute(element, "undecorated", Boolean.FALSE));
		form.setOpacity(Utils.getFloatAttribute(element, "opacity", 1.0f));
		form.setAlwaysOnTop(Utils.getBooleanAttribute(element, "alwaysOnTop", Boolean.FALSE));
		form.setLocationByPlatform(Utils.getBooleanAttribute(element, "locationByPlatform", Boolean.TRUE));
		// form.setDesignedViewSize(viewWidget.getPreferredSize());
		for (int i = 0; i < resolvers.size(); i++) {
			Runnable r = resolvers.get(i);
			r.run();
		}
	}

	protected Dimension readPrefSize(Element anElement) throws NumberFormatException {
		if (anElement.hasAttribute("prefWidth") && anElement.hasAttribute("prefHeight")) {
			Dimension prefSize = new Dimension();
			String prefWidth = anElement.getAttribute("prefWidth");
			String prefHeight = anElement.getAttribute("prefHeight");
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

	public Utils.JsObject resolveEntity(String aEntityName) throws Exception {
		if (model.has(aEntityName)) {
			JavaScriptObject oEntity = model.getJs(aEntityName);
			if (oEntity != null) {
				return oEntity.cast();
			}
		}
		return null;
	}

	protected Utils.JsObject resolveEntity(long aEntityId) throws Exception {
		return null;
	}

	public void addResolver(Runnable aResolver) {
		resolvers.add(aResolver);
	}

	public UIObject readWidget(final Element anElement) throws Exception {
		for (UiWidgetReader reader : UiReader.getFactories()) {
			UIObject read = reader.readWidget(anElement, this);
			if (read != null) {
				return read;
			}
		}
		return null;
	}

	public void readImageParagraph(Element anElement, final UIObject aImageParagraph) throws Exception {
		if (anElement.hasAttribute("icon") && aImageParagraph instanceof HasImageResource) {
			String iconImage = anElement.getAttribute("icon");
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
		if (anElement.hasAttribute("text") && aImageParagraph instanceof HasText) {
			((HasText) aImageParagraph).setText(anElement.getAttribute("text"));
		}
		if (aImageParagraph instanceof HasImageParagraph) {
			HasImageParagraph hip = (HasImageParagraph) aImageParagraph;
			hip.setHorizontalAlignment(Utils.getIntegerAttribute(anElement, "horizontalAlignment",
			        aImageParagraph instanceof ImageButton || aImageParagraph instanceof DropDownButton ? HasImageParagraph.CENTER : HasImageParagraph.LEFT));
			hip.setVerticalAlignment(Utils.getIntegerAttribute(anElement, "verticalAlignment", HasImageParagraph.CENTER));
			hip.setIconTextGap(Utils.getIntegerAttribute(anElement, "iconTextGap", 4));
			hip.setHorizontalTextPosition(Utils.getIntegerAttribute(anElement, "horizontalTextPosition", HasImageParagraph.RIGHT));
			hip.setVerticalTextPosition(Utils.getIntegerAttribute(anElement, "verticalTextPosition", HasImageParagraph.CENTER));
		}
	}

	public void readGeneralProps(final Element anElement, final UIObject aTarget) throws Exception {
		String widgetName = "";
		if (anElement.hasAttribute("name") && aTarget instanceof HasJsName) {
			widgetName = anElement.getAttribute("name");
			((HasJsName) aTarget).setJsName(widgetName);
		}
		/*
		 * if (anElement.hasAttribute("editable") && aTarget instanceof
		 * HasEditable) { ((HasEditable)
		 * aTarget).setEditable(Utils.getBooleanAttribute(anElement, "editable",
		 * Boolean.TRUE)); }
		 */
		if (anElement.hasAttribute("emptyText") && aTarget instanceof HasEmptyText) {
			((HasEmptyText) aTarget).setEmptyText(anElement.getAttribute("emptyText"));
		}
		if (aTarget instanceof HasBinding) {
			if (anElement.hasAttribute("field")) {
				String fieldPath = anElement.getAttribute("field");
				try {
					((HasBinding) aTarget).setField(fieldPath);
				} catch (Exception ex) {
					Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting field (" + fieldPath + ") to widget " + widgetName + " exception occured: " + ex.getMessage());
				}
			}
			if (anElement.hasAttribute("data")) {
				String entityName = anElement.getAttribute("data");
				try {
					((HasBinding) aTarget).setData(resolveEntity(entityName));
				} catch (Exception ex) {
					Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE,
					        "While setting data to named model's property (" + entityName + ") to widget " + widgetName + " exception occured: " + ex.getMessage());
				}
			}
		}
		if (aTarget instanceof HasEnabled)
			((HasEnabled) aTarget).setEnabled(Utils.getBooleanAttribute(anElement, "enabled", Boolean.TRUE));
		// aTarget.setFocusable(Utils.getBooleanAttribute(anElement,
		// "focusable", Boolean.TRUE));
		if (aTarget instanceof Widget && aTarget instanceof HasPublished) {
			PublishedComponent pComp = ((HasPublished) aTarget).getPublished().cast();
			PublishedFont font = readFont(anElement);
			if (font != null) {
				pComp.setFont(font);
			}
			if (anElement.hasAttribute("opaque")) {
				pComp.setOpaque(Utils.getBooleanAttribute(anElement, "opaque", Boolean.TRUE));
			}
			if (anElement.hasAttribute("background")) {
				PublishedColor background = PublishedColor.parse(anElement.getAttribute("background"));
				pComp.setBackground(background);
			}
			if (anElement.hasAttribute("foreground")) {
				PublishedColor foreground = PublishedColor.parse(anElement.getAttribute("foreground"));
				pComp.setForeground(foreground);
			}
			if (anElement.hasAttribute("toolTipText")) {
				pComp.setToolTipText(anElement.getAttribute("toolTipText"));
			}
		}
		if (anElement.hasAttribute("visible")) {
			aTarget.setVisible(Utils.getBooleanAttribute(anElement, "visible", Boolean.TRUE));
		}
		if (anElement.hasAttribute("componentPopupMenu") && aTarget instanceof HasComponentPopupMenu) {
			final String popupName = anElement.getAttribute("componentPopupMenu");
			if (!popupName.isEmpty()) {
				resolvers.add(new Runnable() {
					public void run() {
						UIObject popup = widgets.get(popupName);
						if (popup instanceof PlatypusPopupMenu) {
							((HasComponentPopupMenu) aTarget).setPlatypusPopupMenu((PlatypusPopupMenu) popup);
						}
					}
				});
			}
		}
		if (anElement.hasAttribute("buttonGroup") && aTarget instanceof HasPlatypusButtonGroup) {
			final String buttonGroupName = anElement.getAttribute("buttonGroup");
			if (!buttonGroupName.isEmpty()) {
				resolvers.add(new Runnable() {
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
		if (anElement.hasAttribute("parent")) {
			final String parentName = anElement.getAttribute("parent");
			if (!parentName.isEmpty()) {
				resolvers.add(new Runnable() {
					public void run() {
						UIObject parent = widgets.get(parentName);
						try {
							addToParent(anElement, aTarget, parent);
						} catch (Exception ex) {
							ex.printStackTrace();
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

	public PublishedFont readFont(Element anElement) throws Exception {
		PublishedFont font = readFontTag(anElement, "font");
		if (font != null) {
			return font;
		} else {
			return null;
		}
	}

	private PublishedFont readFontTag(Element anElement, String aSubTagName) throws Exception {
		Element easFontElement = Utils.getElementByTagName(anElement, aSubTagName);
		if (easFontElement != null) {
			String name = easFontElement.getAttribute("name");
			if (name == null || name.isEmpty()) {
				name = "Arial";
			}
			int style = Utils.getIntegerAttribute(easFontElement, "style", 0);
			int size = Utils.getIntegerAttribute(easFontElement, "size", 12);
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
			Element constraintsElement = Utils.getElementByTagName(anElement, TabbedPane.class.getSimpleName() + "Constraints");
			String tabTitle = constraintsElement.getAttribute("tabTitle");
			String tabIconName = constraintsElement.getAttribute("tabIcon");
			String tabTooltipText = constraintsElement.getAttribute("tabTooltipText");
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
			Element constraintsElement = Utils.getElementByTagName(anElement, "BorderPaneConstraints");
			Dimension prefSize = readPrefSize(anElement);
			Integer place = Utils.getIntegerAttribute(constraintsElement, "place", HorizontalPosition.CENTER);
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
			Element constraintsElement = Utils.getElementByTagName(anElement, "CardPaneConstraints");
			String cardName = constraintsElement.getAttribute("cardName");
			((CardPane) parent).add((Widget) aTarget, cardName);
		} else if (parent instanceof FlowPane) {
			// Dimension prefSize = readPrefSize(anElement);
			// aTarget.setSize(prefSize.width + "px", prefSize.height + "px");
			((FlowPane) parent).add((Widget) aTarget);
		} else if (parent instanceof GridPane) {
			GridPane gridPane = (GridPane) parent;
			gridPane.addToFreeCell((Widget) aTarget);
		} else if (parent instanceof AnchorsPane) {
			Element constraintsElement = Utils.getElementByTagName(anElement, "AnchorsPaneConstraints");
			MarginConstraints constraints = readMarginConstraints(constraintsElement);
			((AnchorsPane) parent).add((Widget) aTarget, constraints);
		}
	}

	private static MarginConstraints readMarginConstraints(Element anElement) {
		MarginConstraints result = new MarginConstraints();
		if (anElement.hasAttribute("left")) {
			result.setLeft(MarginConstraints.Margin.parse(anElement.getAttribute("left")));
		}
		if (anElement.hasAttribute("right")) {
			result.setRight(MarginConstraints.Margin.parse(anElement.getAttribute("right")));
		}
		if (anElement.hasAttribute("top")) {
			result.setTop(MarginConstraints.Margin.parse(anElement.getAttribute("top")));
		}
		if (anElement.hasAttribute("bottom")) {
			result.setBottom(MarginConstraints.Margin.parse(anElement.getAttribute("bottom")));
		}
		if (anElement.hasAttribute("width")) {
			result.setWidth(MarginConstraints.Margin.parse(anElement.getAttribute("width")));
		}
		if (anElement.hasAttribute("height")) {
			result.setHeight(MarginConstraints.Margin.parse(anElement.getAttribute("height")));
		}
		return result;
	}

}

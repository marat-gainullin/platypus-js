/*
 * and open the template in the editor.
 */
package com.eas.client.form.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.ImageParagraph;
import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.rowset.Utils;
import com.eas.client.ImageResourceCallback;
import com.eas.client.application.AppClient;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.MarginConstraints;
import com.eas.client.form.PlatypusWindow;
import com.eas.client.form.Publisher;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedFont;
import com.eas.client.form.published.containers.AbsolutePane;
import com.eas.client.form.published.containers.AnchorsPane;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.containers.CardPane;
import com.eas.client.form.published.containers.FlowPane;
import com.eas.client.form.published.containers.GridPane;
import com.eas.client.form.published.containers.HBoxPane;
import com.eas.client.form.published.containers.MarginsPane;
import com.eas.client.form.published.containers.ScrollPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.containers.TabbedPane;
import com.eas.client.form.published.containers.ToolBar;
import com.eas.client.form.published.containers.VBoxPane;
import com.eas.client.form.published.menu.PlatypusMenu;
import com.eas.client.form.published.menu.PlatypusMenuBar;
import com.eas.client.form.published.menu.PlatypusMenuItemCheckBox;
import com.eas.client.form.published.menu.PlatypusMenuItemImageText;
import com.eas.client.form.published.menu.PlatypusMenuItemRadioButton;
import com.eas.client.form.published.menu.PlatypusMenuItemSeparator;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.form.published.widgets.DesktopPane;
import com.eas.client.form.published.widgets.PlatypusButton;
import com.eas.client.form.published.widgets.PlatypusCheckBox;
import com.eas.client.form.published.widgets.PlatypusFormattedTextField;
import com.eas.client.form.published.widgets.PlatypusHtmlEditor;
import com.eas.client.form.published.widgets.PlatypusLabel;
import com.eas.client.form.published.widgets.PlatypusPasswordField;
import com.eas.client.form.published.widgets.PlatypusProgressBar;
import com.eas.client.form.published.widgets.PlatypusRadioButton;
import com.eas.client.form.published.widgets.PlatypusSlider;
import com.eas.client.form.published.widgets.PlatypusSplitButton;
import com.eas.client.form.published.widgets.PlatypusTextArea;
import com.eas.client.form.published.widgets.PlatypusTextField;
import com.eas.client.form.published.widgets.PlatypusToggleButton;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author vy
 */
public class WidgetsFactory {

	protected static final String BORDER_CONSTRAINTS_TAG_NEEED = "BorderLayoutContainer children must have BorderLayout constraints tag";
	protected static final String CARD_CONSTRAINTS_TAG_NEEED = "CardLayoutContainer children must have CardLayout constraints tag";
	protected static final String ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED = "MarginLayoutContainer children must have Margin, Absolute or Layers constraints tag";
	protected static final String TABS_CONSTRAINTS_TAG_NEEED = "TabsContainer children must have tabs constraints tag";
	protected static final String UNKNOWN_CONTAINER_DETECTED = "Container of unknown type detected: ";
	protected static final String NULL_CONTAINER_DETECTED = "Container with the following name was not found: ";
	protected static final String NAME_ATTRIBUTE = "name";
	protected static final String PARENT_ATTRIBUTE = "parent";
	protected static final String TYPE_ATTRIBUTE = "type";
	protected static final String WIDGET_TAG = "widget";
	protected static final String NONVISUAL_TAG = "nonvisual";
	protected static final String LAYOUT_TAG = "layout";
	protected static final String CONSTRAINTS_TAG = "constraints";
	protected static final String EAS_LOCATION_TAG = "easLocation";
	protected static final String EAS_SIZE_TAG = "easSize";
	protected static final String BORDER_TAG = "border";
	protected static final String ROOT_WIDGET_NAME = "Form";

	protected JavaScriptObject target;
	protected PlatypusWindow form;
	protected HasWidgets rootWidget;
	protected boolean isRoot = true;

	private Element tag;
	private Map<String, UIObject> components = new HashMap<>();
	private Map<String, ButtonGroup> toggleGroups = new HashMap<>();
	// might be removed
	private Map<UIObject, Point> componentsPreferredSize = new HashMap<>();

	protected List<Runnable> postponedTasks = new ArrayList<>();
	protected List<Runnable> postponedTasks1 = new ArrayList<>();

	public WidgetsFactory(Element aFormElement, JavaScriptObject aModule) {
		super();
		tag = aFormElement;
		target = aModule;
	}

	/**
	 * Does all work for form construction.
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		String rootWidgetName = tag.getAttribute(NAME_ATTRIBUTE);
		assert ROOT_WIDGET_NAME.equalsIgnoreCase(rootWidgetName) : "Root form tag must be 'Form'";

		form = parseRoot(tag);
		isRoot = false;

		NodeList childNodes = tag.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof Element) {
				if (item.getNodeName().equalsIgnoreCase(WIDGET_TAG) || item.getNodeName().equalsIgnoreCase(NONVISUAL_TAG)) {
					parseWidget((Element) item);
				}
			}
		}
		for (Runnable r : postponedTasks)
			r.run();
		for (Runnable r : postponedTasks1)
			r.run();
	}

	private PlatypusWindow parseRoot(final Element aTag) throws Exception {
		UIObject w = parseWidget(aTag);
		assert w instanceof HasWidgets;
		rootWidget = (HasWidgets) w;
		Point size = componentsPreferredSize.get(rootWidget);
		((Widget) rootWidget).setSize(size.getX() + "px", size.getY() + "px");
		final PlatypusWindow form = new PlatypusWindow((Widget) rootWidget);
		form.setViewPreferredWidth(size.getX());
		form.setViewPreferredHeight(size.getY());
		if (aTag.hasAttribute("iconImage")) {
			form.setIconImage(aTag.getAttribute("iconImage"));
		}
		form.setDefaultCloseOperation(Utils.getIntegerAttribute(aTag, "defaultCloseOperation", 2));
		form.setLocationByPlatform(Utils.getBooleanAttribute(aTag, "locationByPlatform", false));
		form.setAlwaysOnTop(Utils.getBooleanAttribute(aTag, "alwaysOnTop", false));
		form.setResizable(Utils.getBooleanAttribute(aTag, "resizable", true));
		form.setUndecorated(Utils.getBooleanAttribute(aTag, "undecorated", false));
		form.setOpacity(Utils.getFloatAttribute(aTag, "opacity", 1.0f));
		if (aTag.hasAttribute("title")) {
			form.setTitle(aTag.getAttribute("title"));
		}
		return form;
	}

	private Element pickLayoutTag(Element aTag) {
		Node layoutCandidate = aTag.hasChildNodes() ? aTag.getLastChild() : null;
		if (layoutCandidate instanceof Element && LAYOUT_TAG.equalsIgnoreCase(layoutCandidate.getNodeName()))
			return (Element) layoutCandidate;
		else {
			if (isRoot)
				return Utils.scanForElementByTagName(aTag, LAYOUT_TAG);
			else
				return Utils.getElementByTagName(aTag, LAYOUT_TAG);
		}
	}

	private Element pickConstraintsTag(Element aTag) {
		if (!isRoot)
			return Utils.getElementByTagName(aTag, CONSTRAINTS_TAG);
		else
			return null;
	}

	/*
	 * private Element pickBorderTag(Element aTag) { if (!isRoot) return
	 * Utils.scanForElementByTagName(aTag, BORDER_TAG); else return null; }
	 */

	private UIObject parseWidget(Element aTag) {
		try {
			String nodeName = aTag.getNodeName();
			assert (nodeName.equalsIgnoreCase(LAYOUT_TAG) && isRoot) || nodeName.equalsIgnoreCase(WIDGET_TAG) || nodeName.equalsIgnoreCase(NONVISUAL_TAG) : "Form structure is broken. Form must be constructed of widget and nonvisual tags";
			UIObject component = createWidget(aTag);
			if (component != null)// There are might be toggle groups, that are
								  // non
			                      // visuals and so, not components
			{
				components.put(((HasJsName) component).getJsName(), component);
				String parentName = aTag.getAttribute(PARENT_ATTRIBUTE);
				if (!isRoot && parentName != null && !parentName.isEmpty()) {
					resolveParent(component, parentName, pickConstraintsTag(aTag));
				}
				return component;
			} else
				return null;
		} catch (Exception e) {
			Logger.getLogger(WidgetsFactory.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	protected UIObject createWidget(Element aTag) throws Exception {
		Element layoutTag = pickLayoutTag(aTag);
		String designInfoTypeName = aTag.getAttribute(TYPE_ATTRIBUTE);
		assert isRoot || designInfoTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";
		UIObject component = null;
		if (layoutTag != null) {
			String layoutTypeName = layoutTag.getAttribute(TYPE_ATTRIBUTE);
			assert layoutTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";
			if (layoutTypeName.equalsIgnoreCase("BorderLayoutDesignInfo")) {
				component = createBorderLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("BoxLayoutDesignInfo")) {
				int axis = Utils.getIntegerAttribute(layoutTag, "axis", 0);
				if (axis == 1 || axis == 3) {
					component = createVBox(aTag, layoutTag);
				} else {
					component = createHBox(aTag, layoutTag);
				}
			} else if (layoutTypeName.equalsIgnoreCase("CardLayoutDesignInfo")) {
				component = createCardLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("FlowLayoutDesignInfo")) {
				component = createFlowLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("GridLayoutDesignInfo")) {
				component = createGridLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("AbsoluteLayoutDesignInfo")) {
				component = createAbsoluteLayoutContainer(aTag);
			} else if (layoutTypeName.equalsIgnoreCase("MarginLayoutDesignInfo")) {
				component = createAnchorsLayoutContainer(aTag);
			} else
				component = createFlowLayoutContainer(aTag, layoutTag);
		} else {
			if (designInfoTypeName.equalsIgnoreCase("ButtonDesignInfo")) {
				component = createButton(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("DropDownButtonDesignInfo")) {
				component = createDropDownButton(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("LabelDesignInfo")) {
				component = createLabel(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("RadioDesignInfo")) {
				component = createRadio(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("CheckDesignInfo")) {
				component = createCheckBox(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("SliderDesignInfo")) {
				component = createSlider(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("ToggleButtonDesignInfo")) {
				component = createToggleButton(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("ButtonGroupDesignInfo")) {
				createToggleGroup(aTag);
				component = null;
			} else if (designInfoTypeName.equalsIgnoreCase("TextFieldDesignInfo")) {
				component = createTextField(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("FormattedFieldDesignInfo")) {
				component = createFormattedTextField(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("TextPaneDesignInfo")) {
				component = createTextArea(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("EditorPaneDesignInfo")) {
				component = createHtmlArea(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("ProgressBarDesignInfo")) {
				component = createProgressBar(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("PasswordFieldDesignInfo")) {
				component = createPasswordField(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenuSeparatorDesignInfo")) {
				component = createSeparatorMenuItem(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenubarDesignInfo")) {
				component = createMenuBar(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenuDesignInfo")) {
				component = createPlainMenu(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("PopupDesignInfo")) {
				component = createPopupMenu(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenuItemDesignInfo")) {
				component = createMenuItem(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenuCheckItemDesignInfo")) {
				component = createCheckMenuItem(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("MenuRadioItemDesignInfo")) {
				component = createRadioMenuItem(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("TabsDesignInfo")) {
				component = createTabsContainer(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("ToolbarDesignInfo")) {
				component = createToolBar(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("SplitDesignInfo")) {
				component = createSplitLayoutContainer(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("ScrollDesignInfo")) {
				component = createScrollContainer(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("DesktopDesignInfo")) {
				component = createDesktopContainer(aTag);
			} else if (designInfoTypeName.equalsIgnoreCase("LayersDesignInfo")) {
				component = createDesktopContainer(aTag);
			} else
				component = createStubLabel(aTag, "Type '" + designInfoTypeName + "' is unsupported.");
		}
		return component;
	}

	protected Widget createStubLabel(Element aTag, String aMessage) throws Exception {
		PlatypusLabel component = new PlatypusLabel(aMessage, false);
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private void setIconAndAlign(final ImageParagraph component, Element aTag) throws Exception {
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("iconTextGap"))
			component.setIconTextGap(Utils.getIntegerAttribute(aTag, "iconTextGap", 4));
		if (aTag.hasAttribute("horizontalTextPosition"))
			component.setHorizontalTextPosition(Utils.getIntegerAttribute(aTag, "horizontalTextPosition", PlatypusLabel.RIGHT));
		if (aTag.hasAttribute("verticalTextPosition"))
			component.setVerticalTextPosition(Utils.getIntegerAttribute(aTag, "verticalTextPosition", PlatypusLabel.RIGHT));
		if (aTag.hasAttribute("icon")) {
			component.setImage(AppClient.getInstance().getImageResource(aTag.getAttribute("icon")).addCallback(new ImageResourceCallback() {

				@Override
				public void run(PlatypusImageResource aResource) {
					component.setImage(aResource);
				}

			}));
		}
		if (aTag.hasAttribute("verticalAlignment"))
			component.setVerticalAlignment(Utils.getIntegerAttribute(aTag, "verticalAlignment", 0));
		if (aTag.hasAttribute("horizontalAlignment"))
			component.setHorizontalAlignment(Utils.getIntegerAttribute(aTag, "horizontalAlignment", 0));
	}

	private PlatypusButton createButton(Element aTag) throws Exception {
		final PlatypusButton component = new PlatypusButton();
		PublishedComponent publishedComp = Publisher.publish(component);
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component, aTag);
		return component;
	}

	private PlatypusSplitButton createDropDownButton(Element aTag) throws Exception {
		final PlatypusSplitButton component = new PlatypusSplitButton();
		PublishedComponent publishedComp = Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("dropDownMenu")) {
			final String dropDownMenu = aTag.getAttribute("dropDownMenu");
			postponedTasks.add(new Runnable() {

				@Override
				public void run() {
					UIObject comp = components.get(dropDownMenu);
					if (comp instanceof PlatypusPopupMenu)
						component.setMenu((PlatypusPopupMenu) comp);
					else
						Logger.getLogger(WidgetsFactory.class.getName()).log(Level.WARNING, "Some other widget than Menu is assigned as dropDownMenu in component: " + component.getElement().getId());
				}

			});
		}
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component.getContent(), aTag);
		return component;
	}

	private PlatypusLabel createLabel(Element aTag) throws Exception {
		final PlatypusLabel component = new PlatypusLabel();
		Publisher.publish(component);
		setIconAndAlign(component, aTag);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private PlatypusRadioButton createRadio(Element aTag) throws Exception {
		PlatypusRadioButton component = new PlatypusRadioButton();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		component.setEnabled(Utils.getBooleanAttribute(aTag, "enabled", true));
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private void addToToggleGroup(final HasValue<Boolean> item, final String groupName) {
		postponedTasks.add(new Runnable() {

			@Override
			public void run() {
				ButtonGroup group = toggleGroups.get(groupName);
				if (group != null) {
					if (group instanceof ButtonGroup)
						((ButtonGroup) group).add(item);
					else
						group.add(item);
				}
			}

		});
	}

	private PlatypusCheckBox createCheckBox(Element aTag) throws Exception {
		PlatypusCheckBox component = new PlatypusCheckBox();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		component.setEnabled(Utils.getBooleanAttribute(aTag, "enabled", true));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private PlatypusSlider createSlider(Element aTag) throws Exception {
		PlatypusSlider component = new PlatypusSlider();
		Publisher.publish(component);
		component.setMaxValue(Utils.getIntegerAttribute(aTag, "maximum", -Integer.MAX_VALUE));
		component.setMinValue(Utils.getIntegerAttribute(aTag, "minimum", Integer.MAX_VALUE));
		component.setValue((double) Utils.getIntegerAttribute(aTag, "value", (int) component.getMinValue()));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusToggleButton createToggleButton(Element aTag) throws Exception {
		PlatypusToggleButton component = new PlatypusToggleButton();
		PublishedComponent publishedComp = Publisher.publish(component);
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component, aTag);
		return component;
	}

	private void createToggleGroup(Element aTag) throws Exception {
		ButtonGroup buttonGroup = new ButtonGroup();
		final String widgetName = aTag.getAttribute(NAME_ATTRIBUTE);
		toggleGroups.put(widgetName, buttonGroup);
		buttonGroup.setPublished(Publisher.publish(buttonGroup));
		target.<Utils.JsObject> cast().inject(widgetName, buttonGroup.getPublished());
	}

	private PlatypusTextField createTextField(Element aTag) throws Exception {
		PlatypusTextField component = new PlatypusTextField();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setValue(aTag.getAttribute("text"));
		component.setReadOnly(!Utils.getBooleanAttribute(aTag, "editable", true));
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		// *********************************************************************
		// !!!??? This will only work when the widget is attached to the
		// document and not hidden.
		int selectionStart = Utils.getIntegerAttribute(aTag, "selectionStart", 0);
		int selectionEnd = Utils.getIntegerAttribute(aTag, "selectionEnd", 0);
		if (selectionStart > 0 && selectionEnd > 0) {
			component.setSelectionRange(selectionStart, selectionEnd - selectionStart);
		}
		if (aTag.hasAttribute("caretPosition"))
			component.setCursorPos(Utils.getIntegerAttribute(aTag, "caretPosition", component.getText().length()));
		// *********************************************************************
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusFormattedTextField createFormattedTextField(Element aTag) throws Exception {
		String formatPattern = aTag.getAttribute("format");
		int formatType = Utils.getIntegerAttribute(aTag, "valueType", ObjectFormat.MASK);

		PlatypusFormattedTextField component = new PlatypusFormattedTextField();
		component.setFormatType(formatType, formatPattern);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));

		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setReadOnly(!(Utils.getBooleanAttribute(aTag, "editable", true)));
		// *********************************************************************
		// !!!??? This will only work when the widget is attached to the
		// document and not hidden.
		int selectionStart = Utils.getIntegerAttribute(aTag, "selectionStart", 0);
		int selectionEnd = Utils.getIntegerAttribute(aTag, "selectionEnd", 0);
		if (selectionStart > 0 && selectionEnd > 0) {
			component.setSelectionRange(selectionStart, selectionEnd - selectionStart);
		}
		if (aTag.hasAttribute("caretPosition"))
			component.setCursorPos(Utils.getIntegerAttribute(aTag, "caretPosition", component.getText().length()));
		// *********************************************************************
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusTextArea createTextArea(Element aTag) throws Exception {
		PlatypusTextArea component = new PlatypusTextArea();
		Publisher.publish(component);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setReadOnly(!(Utils.getBooleanAttribute(aTag, "editable", true)));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusHtmlEditor createHtmlArea(Element aTag) throws Exception {
		PlatypusHtmlEditor component = new PlatypusHtmlEditor();
		Publisher.publish(component);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		// component.setReadOnly(!Utils.getBooleanAttribute(aTag, "editable",
		// true));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusProgressBar createProgressBar(Element aTag) throws Exception {
		PlatypusProgressBar component = new PlatypusProgressBar();
		Publisher.publish(component);
		int minimum = Utils.getIntegerAttribute(aTag, "minimum", 0);
		int maximum = Utils.getIntegerAttribute(aTag, "maximum", 100);
		component.setMinProgress(minimum);
		component.setMaxProgress(maximum);
		int value = Utils.getIntegerAttribute(aTag, "value", minimum);
		String string = aTag.hasAttribute("string") ? aTag.getAttribute("string") : "";
		component.setValue((double) value);
		if (string != null)
			component.setText(string);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private PlatypusPasswordField createPasswordField(Element aTag) throws Exception {
		PlatypusPasswordField component = new PlatypusPasswordField();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setReadOnly(!(Utils.getBooleanAttribute(aTag, "editable", true)));
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusMenuItemSeparator createSeparatorMenuItem(Element aTag) throws Exception {
		PlatypusMenuItemSeparator component = new PlatypusMenuItemSeparator();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private PlatypusMenuBar createMenuBar(Element aTag) throws Exception {
		PlatypusMenuBar component = new PlatypusMenuBar();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private PlatypusMenu createPlainMenu(Element aTag) throws Exception {
		PlatypusMenu component = new PlatypusMenu();
		Publisher.publish(component);
		if (aTag.hasAttribute("text")) {
			component.setText(aTag.getAttribute("text"));
		}
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		target.<Utils.JsObject> cast().inject(component.getJsName(), publishedComp);
		return component;
	}

	private PlatypusPopupMenu createPopupMenu(Element aTag) throws Exception {
		PlatypusPopupMenu component = new PlatypusPopupMenu();
		Publisher.publishPopup(component);
		if (aTag.hasAttribute("text")) {
			component.setText(aTag.getAttribute("text"));
		}
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		target.<Utils.JsObject> cast().inject(component.getJsName(), publishedComp);
		return component;
	}

	private PlatypusMenuItemImageText createMenuItem(Element aTag) throws Exception {
		final PlatypusMenuItemImageText component = new PlatypusMenuItemImageText();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("icon")) {
			component.setIcon(AppClient.getInstance().getImageResource(aTag.getAttribute("icon")).addCallback(new ImageResourceCallback() {
				@Override
				public void run(PlatypusImageResource aResource) {
					component.setIcon(aResource);
				}
			}));
		}
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		target.<Utils.JsObject> cast().inject(component.getJsName(), publishedComp);
		return component;
	}

	private PlatypusMenuItemCheckBox createCheckMenuItem(Element aTag) throws Exception {
		PlatypusMenuItemCheckBox component = new PlatypusMenuItemCheckBox();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		target.<Utils.JsObject> cast().inject(component.getJsName(), publishedComp);
		return component;
	}

	private PlatypusMenuItemRadioButton createRadioMenuItem(Element aTag) throws Exception {
		PlatypusMenuItemRadioButton component = new PlatypusMenuItemRadioButton();
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		target.<Utils.JsObject> cast().inject(component.getJsName(), publishedComp);
		return component;
	}

	protected void processGeneralProperties(final UIObject aComponent, Element aTag, PublishedComponent aPublished) throws Exception {
		processGeneralProperties(aComponent, aTag, true, aPublished);
	}

	protected static int idCounter = 0;

	protected void processGeneralProperties(final UIObject aComponent, Element aTag, boolean aDefaultOpaque, PublishedComponent aPublished) throws Exception {
		final String widgetName = aTag.getAttribute(NAME_ATTRIBUTE);
		if (widgetName != null && !widgetName.isEmpty())
			((HasJsName) aComponent).setJsName(widgetName);
		aComponent.getElement().setId("pw-" + (++idCounter));

		boolean visible = Utils.getBooleanAttribute(aTag, "visible", true);
		if (!visible)
			aComponent.setVisible(visible);
		boolean enabled = Utils.getBooleanAttribute(aTag, "enabled", true);
		if (!enabled && aComponent instanceof HasEnabled)
			((HasEnabled) aComponent).setEnabled(enabled);

		if (aTag.hasAttribute("toolTipText")) {
			String toolTipText = aTag.getAttribute("toolTipText");
			aComponent.setTitle(toolTipText);
		}

		if (aTag.hasAttribute("prefWidth") && aTag.hasAttribute("prefHeight")) {
			Point size = new Point(Utils.getPxAttribute(aTag, "prefWidth", 0), Utils.getPxAttribute(aTag, "prefHeight", 0));
			componentsPreferredSize.put(aComponent, size);
		}
		if (aTag.hasAttribute("componentPopupMenu")) {
			final String componentPopupMenu = aTag.getAttribute("componentPopupMenu");
			postponedTasks.add(new Runnable() {

				@Override
				public void run() {
					if (aComponent instanceof HasComponentPopupMenu) {
						UIObject menuComp = components.get(componentPopupMenu);
						if (menuComp instanceof PlatypusPopupMenu) {
							((HasComponentPopupMenu) aComponent).setPlatypusPopupMenu((PlatypusPopupMenu) menuComp);
						} else
							Logger.getLogger(WidgetsFactory.class.getName()).log(Level.WARNING, "Some other widget than Menu is assigned as componentPopupMenu in component: " + widgetName);
					} else
						Logger.getLogger(WidgetsFactory.class.getName())
						        .log(Level.WARNING, "Widget of type: " + widgetName + " with assigned componentPopupMenu doesn't support HasComponentPopupMenu");
				}

			});
		}
		// Not supported yet
		final boolean inheritsPopupMenu = Utils.getBooleanAttribute(aTag, "inheritsPopupMenu", false);
		//
		// Inline style
		// Style style = aComponent.getElement().getStyle();
		if (aTag.hasAttribute("backgroundColor")) {
			aPublished.setBackground(ControlsUtils.parseColor(aTag.getAttribute("backgroundColor")));
		}
		if (aTag.hasAttribute("foregroundColor")) {
			aPublished.setForeground(ControlsUtils.parseColor(aTag.getAttribute("foregroundColor")));// force
			// value
		}
		boolean opaque = Utils.getBooleanAttribute(aTag, "opaque", aDefaultOpaque);
		if (!opaque) // opaque == true is the default value, so we avoid to set
		             // it, because of speed
			aPublished.setOpaque(opaque);
		if (!isRoot) {
			Element fontTag = Utils.getElementByTagName(aTag, "easFont");
			PublishedFont font =  parseFont(fontTag);
			if (font != null) {
				aPublished.setFont(font);
			}
		}
		if (aTag.hasAttribute("cursor")) {
			aPublished.setCursor(convertCursor(Utils.getIntegerAttribute(aTag, "cursor", 0)));
		}
	}

	public static PublishedFont parseFont(Element fontTag) throws Exception{
		if(fontTag != null){
		String fontFamily = null;
		if (fontTag.hasAttribute("name")) {
			fontFamily = fontTag.getAttribute("name");
		}
		int fontSize = 0;
		if (fontTag.hasAttribute("size")) {
			fontSize = Utils.getIntegerAttribute(fontTag, "size", 10);
		}
		int fontStyle = 0;
		if (fontTag.hasAttribute("style")) {
			fontStyle = Utils.getIntegerAttribute(fontTag, "style", 0);
		}
		return PublishedFont.create(fontFamily, fontStyle, fontSize);
		}else{
			return null;
		}
	}
	
	protected static String convertCursor(int aValue) {
		switch (aValue) {
		case 1/* CROSSHAIR */:
			return "crosshair";
		case 0/* DEFAULT */:
			return "default";
		case 11/* E_RESIZE */:
			return "e-resize";
			// help ?
			// progress ?
		case 12/* HAND */:
			return "pointer";
		case 13/* MOVE */:
			return "move";
		case 7/* NE_RESIZE */:
			return "ne-resize";
		case 6/* NW_RESIZE */:
			return "nw-resize";
		case 8/* N_RESIZE */:
			return "n-resize";
		case 5/* SE_RESIZE */:
			return "se-resize";
		case 4/* SW_RESIZE */:
			return "sw-resize";
		case 9/* S_RESIZE */:
			return "s-resize";
		case 2/* TEXT */:
			return "text";
		case 3/* WAIT */:
			return "wait";
		case 10/* W_RESIZE */:
			return "w-resize";

		default:
			return "default";

		}
	}

	private void resolveParent(final UIObject aComponent, final String aParentName, final Element aConstraintsTag) throws Exception {
		postponedTasks.add(new Runnable() {
			@Override
			public void run() {
				try {
					Widget parentComp = (Widget) components.get(aParentName);
					if (parentComp instanceof BorderPane) {
						BorderPane container = (BorderPane) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("BorderLayoutConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								Point size = componentsPreferredSize.get(aComponent);
								container.add((Widget) aComponent, parseBorderConstraints(aConstraintsTag), size);
							} else
								throw new IllegalStateException(BORDER_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(BORDER_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof CardPane) {
						CardPane container = (CardPane) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("CardLayoutConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								String cardName = "";
								if (aConstraintsTag.hasAttribute("cardName"))
									cardName = aConstraintsTag.getAttribute("cardName");
								container.add((Widget) aComponent, cardName);
							} else
								throw new IllegalStateException(CARD_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(CARD_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof FlowPane) {
						FlowPane container = (FlowPane) parentComp;
						Point prefSize = componentsPreferredSize.get(aComponent);
						if (prefSize != null)
							aComponent.setSize(prefSize.getX() + "px", prefSize.getY() + "px");
						container.add((Widget) aComponent);
					} else if (parentComp instanceof GridPane) {
						GridPane container = (GridPane) parentComp;
						container.add((Widget) aComponent);
					} else if (parentComp instanceof MarginsPane) {
						MarginsPane container = (MarginsPane) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("AbsoluteConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName) || "MarginConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)
							        || "LayersLayoutConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								MarginConstraints constraints = null;
								if ("MarginConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
									constraints = parseMarginConstraints(aConstraintsTag);
								} else// AbsoluteConstraintsDesignInfo or
								      // LayersLayoutConstraintsDesignInfo
								{
									Point prefSize = componentsPreferredSize.get(aComponent);
									constraints = parseAbsoluteConstraints(aConstraintsTag);
									if (prefSize != null) {
										if (constraints.getWidth() != null && constraints.getWidth().value == -1) {
											constraints.getWidth().value = (int) Math.round(prefSize.getX());
											constraints.getWidth().unit = Style.Unit.PX;
										}
										if (constraints.getHeight() != null && constraints.getHeight().value == -1) {
											constraints.getHeight().value = (int) Math.round(prefSize.getY());
											constraints.getHeight().unit = Style.Unit.PX;
										}
									}
								}
								if (aConstraintsTag.hasAttribute("layer")) {
									int layer = Utils.getIntegerAttribute(aConstraintsTag, "layer", 0);
								}
								container.add((Widget) aComponent, constraints);
							} else
								throw new IllegalStateException(ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof ScrollPane) {
						Point prefSize = componentsPreferredSize.get(aComponent);
						ScrollPane container = (ScrollPane) parentComp;
						container.setWidget((Widget) aComponent);
						aComponent.setSize(prefSize.getX() + "px", prefSize.getY() + "px");
					} else if (parentComp instanceof DesktopPane) {
						DesktopPane container = (DesktopPane) parentComp;
						container.add((Widget) aComponent);
					} else if (parentComp instanceof SplitPane) {
						// left & right components were processed in
						// PlatypusSplitContainer's properties' closure.
					} else if (parentComp instanceof TabbedPane) {
						final TabbedPane container = (TabbedPane) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("TabsConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								String textOrHtml = "";
								boolean html = false;
								PlatypusImageResource imRes = null;
								if (aConstraintsTag.hasAttribute("tabTitle")) {
									textOrHtml = aConstraintsTag.getAttribute("tabTitle");
									if (textOrHtml != null && textOrHtml.startsWith("<html>")) {
										html = true;
										textOrHtml = textOrHtml.substring("<html>".length());
									} else {
										html = false;
									}
								}
								/*
								 * if
								 * (aConstraintsTag.hasAttribute("tabTooltipText"
								 * )) tabTooltipText =
								 * aTag.getAttribute("tabTooltipText");
								 */
								if (aConstraintsTag.hasAttribute("icon")) {
									imRes = AppClient.getInstance().getImageResource(aConstraintsTag.getAttribute("icon"));
								}
								container.add((Widget) aComponent, textOrHtml, html, imRes);
							} else
								throw new IllegalStateException(TABS_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " tag.");
						} else
							throw new IllegalStateException(TABS_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof ToolBar) {
						ToolBar container = (ToolBar) parentComp;
						container.add((Widget) aComponent);
					} else if (parentComp instanceof HBoxPane) {
						Point prefSize = componentsPreferredSize.get(aComponent);
						if (prefSize != null)
							aComponent.setSize(prefSize.getX() + "px", prefSize.getY() + "px");
						HBoxPane container = (HBoxPane) parentComp;
						container.add((Widget) aComponent);
					} else if (parentComp instanceof VBoxPane) {
						Point prefSize = componentsPreferredSize.get(aComponent);
						if (prefSize != null)
							aComponent.setSize(prefSize.getX() + "px", prefSize.getY() + "px");
						VBoxPane container = (VBoxPane) parentComp;
						container.add((Widget) aComponent);
					} else if (parentComp instanceof PlatypusMenuBar) {
						PlatypusMenuBar container = (PlatypusMenuBar) parentComp;
						container.add(aComponent);
					} else if (parentComp != null) {
						throw new IllegalStateException(UNKNOWN_CONTAINER_DETECTED + parentComp.getClass().getName());
					} else {
						throw new IllegalStateException(NULL_CONTAINER_DETECTED + aParentName);
					}
				} catch (Exception ex) {
					Logger.getLogger(WidgetsFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		});
	}

	private BorderPane createBorderLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		BorderPane component = new BorderPane(Utils.getIntegerAttribute(aLayoutTag, "vgap", 0), Utils.getIntegerAttribute(aLayoutTag, "hgap", 0));
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private ScrollPane createScrollContainer(Element aTag) throws Exception {
		ScrollPane component = new ScrollPane();
		Publisher.publish(component);
		int vScrollPolicy = Utils.getIntegerAttribute(aTag, "verticalScrollBarPolicy", ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		int hScrollPolicy = Utils.getIntegerAttribute(aTag, "horizontalScrollBarPolicy", ScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		component.setVerticalScrollBarPolicy(vScrollPolicy);
		component.setHorizontalScrollBarPolicy(hScrollPolicy);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private SplitPane createSplitLayoutContainer(Element aTag) throws Exception {
		final SplitPane component = new SplitPane();
		Publisher.publish(component);
		component.setOneTouchExpandable(Utils.getBooleanAttribute(aTag, "oneTouchExpandable", false));
		component.setOrientation(Utils.getIntegerAttribute(aTag, "orientation", SplitPane.HORIZONTAL_SPLIT));
		component.setDividerLocation(Utils.getIntegerAttribute(aTag, "dividerLocation", 84));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		final String leftComponent = aTag.getAttribute("leftComponent");
		final String rightComponent = aTag.getAttribute("rightComponent");
		postponedTasks.add(new Runnable() {

			@Override
			public void run() {
				component.setFirstWidget((Widget) components.get(leftComponent));
				component.setSecondWidget((Widget) components.get(rightComponent));
			}

		});
		return component;
	}

	private VBoxPane createVBox(Element aTag, Element aLayoutTag) throws Exception {
		VBoxPane vbox = new VBoxPane();
		Publisher.publish(vbox);
		processGeneralProperties(vbox, aTag, vbox.getPublished().<PublishedComponent> cast());
		return vbox;
	}

	private HBoxPane createHBox(Element aTag, Element aLayoutTag) throws Exception {
		HBoxPane hbox = new HBoxPane();
		Publisher.publish(hbox);
		processGeneralProperties(hbox, aTag, hbox.getPublished().<PublishedComponent> cast());
		return hbox;
	}

	private CardPane createCardLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		int hgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "hgap", 0) : 0;
		int vgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "vgap", 0) : 0;
		CardPane component = new CardPane(vgap, hgap);
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private AbsolutePane createAbsoluteLayoutContainer(Element aTag) throws Exception {
		AbsolutePane component = new AbsolutePane();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private MarginsPane createAnchorsLayoutContainer(Element aTag) throws Exception {
		AnchorsPane component = new AnchorsPane();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private DesktopPane createDesktopContainer(Element aTag) throws Exception {
		DesktopPane component = new DesktopPane();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private TabbedPane createTabsContainer(Element aTag) throws Exception {
		final TabbedPane component = new TabbedPane();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		if (aTag.hasAttribute("selectedComponent")) {
			final String selectedComponent = aTag.getAttribute("selectedComponent");
			postponedTasks1.add(new Runnable() {

				@Override
				public void run() {
					if (components.containsKey(selectedComponent))
						component.setSelected((Widget) components.get(selectedComponent));
				}
			});
		}
		return component;
	}

	private ToolBar createToolBar(Element aTag) throws Exception {
		ToolBar component = new ToolBar();
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private FlowPane createFlowLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		int hgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "hgap", 0) : 0;
		int vgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "vgap", 0) : 0;
		FlowPane component = new FlowPane(vgap, hgap);
		Publisher.publish(component);
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private GridPane createGridLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		GridPane component = new GridPane();
		Publisher.publish(component);
		component.setHgap(Utils.getIntegerAttribute(aLayoutTag, "hgap", 0));
		component.setVgap(Utils.getIntegerAttribute(aLayoutTag, "vgap", 0));
		component.resize(Utils.getIntegerAttribute(aLayoutTag, "rows", 1), Utils.getIntegerAttribute(aLayoutTag, "columns", 1));
		PublishedComponent publishedComp = component.getPublished().cast();
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private static Direction parseBorderConstraints(Element aTag) {
		String place = aTag.getAttribute("place");
		if ("north".equalsIgnoreCase(place) || "first".equalsIgnoreCase(place)) {
			return Direction.NORTH;
		} else if ("west".equalsIgnoreCase(place) || "before".equalsIgnoreCase(place)) {
			return Direction.WEST;
		} else if ("east".equalsIgnoreCase(place) || "after".equalsIgnoreCase(place)) {
			return Direction.EAST;
		} else if ("south".equalsIgnoreCase(place) || "last".equalsIgnoreCase(place)) {
			return Direction.SOUTH;
		}
		return Direction.CENTER;
	}

	private static MarginConstraints parseAbsoluteConstraints(Element aTag) throws Exception {
		MarginConstraints constraints = new MarginConstraints();
		MarginConstraints.Margin left = new MarginConstraints.Margin();
		MarginConstraints.Margin top = new MarginConstraints.Margin();
		Element locationTag = Utils.getElementByTagName(aTag, EAS_LOCATION_TAG);
		if (locationTag != null) {
			if (locationTag.hasAttribute("x")) {
				left = new MarginConstraints.Margin(Utils.getIntegerAttribute(locationTag, "x", 0), Style.Unit.PX);
			}
			if (locationTag.hasAttribute("y")) {
				top = new MarginConstraints.Margin(Utils.getIntegerAttribute(locationTag, "y", 0), Style.Unit.PX);
			}
		}
		constraints.setLeft(left);
		constraints.setTop(top);
		MarginConstraints.Margin width = new MarginConstraints.Margin();
		MarginConstraints.Margin height = new MarginConstraints.Margin();
		Element sizeTag = Utils.getElementByTagName(aTag, EAS_SIZE_TAG);
		if (sizeTag != null) {
			if (sizeTag.hasAttribute("width")) {
				width = new MarginConstraints.Margin(Utils.getIntegerAttribute(sizeTag, "width", -1), Style.Unit.PX);
			}
			if (sizeTag.hasAttribute("height")) {
				height = new MarginConstraints.Margin(Utils.getIntegerAttribute(sizeTag, "height", -1), Style.Unit.PX);
			}
		}
		constraints.setWidth(width);
		constraints.setHeight(height);
		return constraints;
	}

	private static MarginConstraints parseMarginConstraints(Element aTag) throws Exception {
		MarginConstraints constraints = new MarginConstraints();
		if (aTag.hasAttribute("left"))
			constraints.setLeft(new MarginConstraints.Margin(aTag.getAttribute("left")));
		if (aTag.hasAttribute("right"))
			constraints.setRight(new MarginConstraints.Margin(aTag.getAttribute("right")));
		if (aTag.hasAttribute("top"))
			constraints.setTop(new MarginConstraints.Margin(aTag.getAttribute("top")));
		if (aTag.hasAttribute("bottom"))
			constraints.setBottom(new MarginConstraints.Margin(aTag.getAttribute("bottom")));
		if (aTag.hasAttribute("width"))
			constraints.setWidth(new MarginConstraints.Margin(aTag.getAttribute("width")));
		if (aTag.hasAttribute("height"))
			constraints.setHeight(new MarginConstraints.Margin(aTag.getAttribute("height")));
		return constraints;
	}

	public PlatypusWindow getForm() {
		return form;
	}
}

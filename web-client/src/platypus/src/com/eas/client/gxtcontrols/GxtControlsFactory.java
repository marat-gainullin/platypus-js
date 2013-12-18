/*
 * and open the template in the editor.
 */
package com.eas.client.gxtcontrols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.ImageResourceCallback;
import com.eas.client.Utils;
import com.eas.client.application.AppClient;
import com.eas.client.form.Form;
import com.eas.client.gxtcontrols.events.GxtEventsExecutor;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.gxtcontrols.published.PublishedFont;
import com.eas.client.gxtcontrols.wrappers.component.ObjectFormat;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusButtonGroup;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckBox;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusCheckRadioMenuItem;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusFormattedTextField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusHtmlEditor;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusLabel;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusPasswordField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusProgressBar;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusSlider;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusSplitButton;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusTextArea;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusTextButton;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusTextField;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusToggleButton;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusBorderLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusCardLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusDesktopContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusFieldSet;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusFlowLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusGridLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusHBoxLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMarginLayoutContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMenu;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusMenuBar;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusScrollContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusSplitContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusTabsContainer;
import com.eas.client.gxtcontrols.wrappers.container.PlatypusVBoxLayoutContainer;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonScale;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.RadioCell;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.button.CellButtonBase;
import com.sencha.gxt.widget.core.client.button.SplitButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author vy
 */
public class GxtControlsFactory {

	protected static final String BORDER_CONSTRAINTS_TAG_NEEED = "BorderLayoutContainer children must have BorderLayout constraints tag";
	protected static final String CARD_CONSTRAINTS_TAG_NEEED = "CardLayoutContainer children must have CardLayout constraints tag";
	protected static final String ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED = "MarginLayoutContainer children must have Margin, Absolute or Layers constraints tag";
	protected static final String TABS_CONSTRAINTS_TAG_NEEED = "TabsContainer children must have tabs constraints tag";
	protected static final String UNKNOWN_CONTAINER_DETECTED = "Container of unknown type detected: ";
	protected static final String NULL_CONTAINER_DETECTED = "Container with the folowing name was not found: ";
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

	protected JavaScriptObject module;
	protected Form form;
	protected Container rootWidget;
	protected boolean isRoot = true;
	protected List<Runnable> handlersResolvers = new ArrayList<Runnable>();

	private Element tag;
	private Map<String, Component> components = new HashMap<String, Component>();
	private Map<String, ToggleGroup> toggleGroups = new HashMap<String, ToggleGroup>();
	// might be removed
	private Map<String, Size> componentsPreferredSize = new HashMap<String, Size>();

	protected List<Runnable> postponedTasks = new ArrayList<Runnable>();
	protected List<Runnable> postponedTasks1 = new ArrayList<Runnable>();

	public GxtControlsFactory(Element aFormElement, JavaScriptObject aModule) {
		super();
		tag = aFormElement;
		module = aModule;
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

	private Form parseRoot(final Element aTag) throws Exception {
		Component w = parseWidget(aTag);
		assert w instanceof Container;
		rootWidget = (Container) w;
		Size size = componentsPreferredSize.get(ROOT_WIDGET_NAME);
		rootWidget.setSize(size.getWidth() + "px", size.getHeight() + "px");
		final Form form = new Form(rootWidget, new Runnable() {
			@Override
			public void run() {
				for (Runnable hResolver : handlersResolvers)
					hResolver.run();
			}
		});
		form.setViewPreferredWidth(size.getWidth());
		form.setViewPreferredHeight(size.getHeight());

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
		handlersResolvers.add(new Runnable() {
			@Override
			public void run() {
				if (aTag.hasAttribute("windowOpened")) {
					form.setWindowOpened(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowOpened")));
				}
				if (aTag.hasAttribute("windowClosing")) {
					form.setWindowClosing(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowClosing")));
				}
				if (aTag.hasAttribute("windowClosed")) {
					form.setWindowClosed(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowClosed")));
				}
				if (aTag.hasAttribute("windowMinimized")) {
					form.setWindowMinimized(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowMinimized")));
				}
				if (aTag.hasAttribute("windowRestored")) {
					form.setWindowRestored(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowRestored")));
				}
				if (aTag.hasAttribute("windowMaximized")) {
					form.setWindowMaximized(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowMaximized")));
				}
				if (aTag.hasAttribute("windowActivated")) {
					form.setWindowActivated(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowActivated")));
				}
				if (aTag.hasAttribute("windowDeactivated")) {
					form.setWindowDeactivated(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("windowDeactivated")));
				}
			}
		});
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

	private Element pickBorderTag(Element aTag) {
		if (!isRoot)
			return Utils.scanForElementByTagName(aTag, BORDER_TAG);
		else
			return null;
	}

	protected void checkBorders(Component aComp, Element aTag) {
		Element titledBorderTag = pickBorderTag(aTag);
		if (titledBorderTag != null) {
			if (titledBorderTag.hasAttribute("title")) {
				PlatypusFieldSet fs = new PlatypusFieldSet();
				fs.setHeadingText(titledBorderTag.getAttribute("title"));
				fs.setWidget(aComp);
				fs.setCollapsible(false);

				JavaScriptObject published = aComp.getData(Form.PUBLISHED_DATA_KEY);
				fs.setData(Form.PUBLISHED_DATA_KEY, published);
				if (published != null) {
					Publisher.publishTextBorder(published, fs);
				}
			} else {
				aComp.setBorders(true);
			}
		}
	}

	protected Component top(Component aComponent) {
		if (aComponent == null) {
			return null;
		}
		Widget currComp = aComponent;
		while (currComp.getParent() != null)
			currComp = currComp.getParent();
		if (currComp instanceof Component)
			return (Component) currComp;
		else
			return null;
	}

	private Component parseWidget(Element aTag) throws Exception {
		String nodeName = aTag.getNodeName();
		assert (nodeName.equalsIgnoreCase(LAYOUT_TAG) && isRoot) || nodeName.equalsIgnoreCase(WIDGET_TAG) || nodeName.equalsIgnoreCase(NONVISUAL_TAG) : "Form structure is broken. Form must be constructed of widget and nonvisual tags";

		Component component = createComponent(aTag);
		if (component != null)// There are might be toggle groups, that are non
		                      // visuals and so, not components
		{
			components.put((String) component.getData(Form.PID_DATA_KEY), component);

			String parentName = aTag.getAttribute(PARENT_ATTRIBUTE);
			if (!isRoot && parentName != null && !parentName.isEmpty()) {
				resolveParent(component, parentName, pickConstraintsTag(aTag));
			}
			return component;
		} else
			return null;
	}

	protected Component createComponent(Element aTag) throws Exception {
		Element layoutTag = pickLayoutTag(aTag);
		String designInfoTypeName = aTag.getAttribute(TYPE_ATTRIBUTE);
		assert isRoot || designInfoTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";
		Component component = null;
		if (layoutTag != null) {
			String layoutTypeName = layoutTag.getAttribute(TYPE_ATTRIBUTE);
			assert layoutTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";
			if (layoutTypeName.equalsIgnoreCase("BorderLayoutDesignInfo")) {
				component = createBorderLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("BoxLayoutDesignInfo")) {
				component = createBoxLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("CardLayoutDesignInfo")) {
				component = createCardLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("FlowLayoutDesignInfo")) {
				component = createFlowLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("GridLayoutDesignInfo")) {
				component = createGridLayoutContainer(aTag, layoutTag);
			} else if (layoutTypeName.equalsIgnoreCase("AbsoluteLayoutDesignInfo")) {
				component = createAbsoluteLayoutContainer(aTag);
			} else if (layoutTypeName.equalsIgnoreCase("MarginLayoutDesignInfo")) {
				component = createMarginLayoutContainer(aTag);
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
				component = createLayersLayoutContainer(aTag);
			} else
				component = createStubLabel(aTag, "Type '" + designInfoTypeName + "' is unsupported.");
		}
		return component;
	}

	protected Component createStubLabel(Element aTag, String aMessage) throws Exception {
		PlatypusLabel component = new PlatypusLabel(aMessage);
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private void setIconAndAlign(final CellButtonBase<?> btn, Element aTag, final PublishedComponent aPublished) throws Exception {
		btn.setIconAlign(IconAlign.LEFT);// default value
		int horizontalTextPosition = 11;// TRAILING
		if (aTag.hasAttribute("horizontalTextPosition")) {
			horizontalTextPosition = Utils.getIntegerAttribute(aTag, "horizontalTextPosition", 11);// TRAILING
		}
		int verticalTextPosition = 0;// CENTER
		if (aTag.hasAttribute("verticalTextPosition")) {
			verticalTextPosition = Utils.getIntegerAttribute(aTag, "verticalTextPosition", 0);// CENTER
		}
		switch (horizontalTextPosition) {
		case 4:// text RIGHT, so icon is to the LEFT
			btn.setIconAlign(IconAlign.LEFT);
			break;
		case 2:// LEFT
			btn.setIconAlign(IconAlign.RIGHT);
			break;
		case 0:// CENTER
			switch (verticalTextPosition) {
			case 0:// CENTER
				btn.setIconAlign(IconAlign.TOP);
				break;
			case 1:// TOP
				btn.setIconAlign(IconAlign.BOTTOM);
				break;
			case 3:// BOTTOM
				btn.setIconAlign(IconAlign.TOP);
				break;
			default:
				btn.setIconAlign(IconAlign.TOP);
				break;
			}
			break;
		case 10:// LEADING
			btn.setIconAlign(IconAlign.RIGHT);
			break;
		case 11:// TRAILING
			btn.setIconAlign(IconAlign.LEFT);
			break;
		default:
			btn.setIconAlign(IconAlign.LEFT);
			break;
		}
		
		btn.setScale(ButtonScale.SMALL);
		if (aTag.hasAttribute("icon")) {
			btn.setIcon(AppClient.getInstance().getImageResource(aTag.getAttribute("icon")).addCallback(new ImageResourceCallback() {
				@Override
				public void run(ImageResource aResource) {
					btn.setIcon(aResource);
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							if (btn.isAttached()) {
								if (btn.getParent() instanceof ResizeContainer) {// fix for sencha ResizeContainer.onResize bug
									ResizeContainer c = (ResizeContainer)btn.getParent();
									Size s = XElement.as(c.getElement()).getSize(false);
									c.clearSizeCache();
									c.setPixelSize(s.getWidth(), s.getHeight());
								} else if (btn.getParent() instanceof RequiresResize) {
									((RequiresResize) btn.getParent()).onResize();
								}
							}
						}
					});
				}

			}));
		}
	}

	private Component createButton(Element aTag) throws Exception {
		final PlatypusTextButton component = new PlatypusTextButton();
		processEvents(component, aTag);
		PublishedComponent publishedComp = Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("iconTextGap"))
			component.setIconTextGap(Utils.getIntegerAttribute(aTag, "iconTextGap", 4));
		checkBorders(component, aTag);
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component, aTag, publishedComp);
		
		return component;
	}

	private Component createDropDownButton(Element aTag) throws Exception {
		final SplitButton component = new PlatypusSplitButton();
		processEvents(component, aTag);
		PublishedComponent publishedComp = Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("dropDownMenu")) {
			final String dropDownMenu = aTag.getAttribute("dropDownMenu");
			postponedTasks.add(new Runnable() {

				@Override
				public void run() {
					Component comp = components.get(dropDownMenu);
					if (comp instanceof Menu)
						component.setMenu((Menu) comp);
					else
						Logger.getLogger(GxtControlsFactory.class.getName()).log(Level.WARNING,
						        "Some other widget than Menu is assigned as dropDownMenu in component: " + component.getData(Form.PID_DATA_KEY));
				}

			});
		}
		checkBorders(component, aTag);
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component, aTag, publishedComp);
		return component;
	}

	private Component createLabel(Element aTag) throws Exception {
		final PlatypusLabel component = new PlatypusLabel();
		processEvents(component, aTag);
		Publisher.publish(component);
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
				public void run(ImageResource aResource) {
					component.setImage(aResource);
					if (component.isAttached()) {
						if (component.getParent() instanceof ResizeContainer) {// fix for sencha ResizeContainer.onResize bug
							ResizeContainer c = (ResizeContainer)component.getParent();
							Size s = XElement.as(c.getElement()).getSize(false);
							c.clearSizeCache();
							c.setPixelSize(s.getWidth(), s.getHeight());
						} else if (component.getParent() instanceof RequiresResize) {
							((RequiresResize) component.getParent()).onResize();
						}
					}
				}

			}));
		}
		if (aTag.hasAttribute("verticalAlignment"))
			component.setVerticalAlignment(Utils.getIntegerAttribute(aTag, "verticalAlignment", 0));
		if (aTag.hasAttribute("horizontalAlignment"))
			component.setHorizontalAlignment(Utils.getIntegerAttribute(aTag, "horizontalAlignment", 0));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private void processCheckBoxProperties(PlatypusCheckBox aComponent, Element aTag) throws Exception {
		if (aTag.hasAttribute("text"))
			aComponent.setBoxLabel(aTag.getAttribute("text"));
		aComponent.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		aComponent.setReadOnly(!Utils.getBooleanAttribute(aTag, "enabled", true));
	}

	private Component createRadio(Element aTag) throws Exception {
		PlatypusCheckBox component = new PlatypusCheckBox(new RadioCell());
		processEvents(component, aTag);
		Publisher.publishRadio(component);
		processCheckBoxProperties(component, aTag);
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private void addToToggleGroup(final HasValue<Boolean> item, final String groupName) {
		postponedTasks.add(new Runnable() {

			@Override
			public void run() {
				ToggleGroup group = toggleGroups.get(groupName);
				if (group != null) {
					if (group instanceof PlatypusButtonGroup && item instanceof Component)
						((PlatypusButtonGroup) group).add((Component) item);
					else
						group.add(item);
				}
			}

		});
	}

	private Component createCheckBox(Element aTag) throws Exception {
		PlatypusCheckBox component = new PlatypusCheckBox();
		processEvents(component, aTag);
		Publisher.publish(component);
		processCheckBoxProperties(component, aTag);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private Component createSlider(Element aTag) throws Exception {
		PlatypusSlider component = new PlatypusSlider("1".equals(aTag.getAttribute("orientation")));
		processEvents(component, aTag);
		Publisher.publish(component);
		component.setMaxValue(Utils.getIntegerAttribute(aTag, "maximum", component.getMaxValue()));
		component.setMinValue(Utils.getIntegerAttribute(aTag, "minimum", component.getMinValue()));
		component.setValue(Utils.getIntegerAttribute(aTag, "value", component.getMinValue()));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createToggleButton(Element aTag) throws Exception {
		PlatypusToggleButton component = new PlatypusToggleButton();
		processEvents(component, aTag);
		PublishedComponent publishedComp = Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		component.setValue(Utils.getBooleanAttribute(aTag, "selected", false));
		processGeneralProperties(component, aTag, publishedComp);
		setIconAndAlign(component, aTag, publishedComp);
		checkBorders(component, aTag);
		return component;
	}

	private void createToggleGroup(Element aTag) throws Exception {
		PlatypusButtonGroup buttonGroup = new PlatypusButtonGroup();
		final String widgetName = aTag.getAttribute(NAME_ATTRIBUTE);
		toggleGroups.put(widgetName, buttonGroup);
		buttonGroup.setPublished(Publisher.publish(buttonGroup));
		Form.inject(module, widgetName, buttonGroup.getPublished());
	}

	private void setText(ValueBaseField<?> aComponent, Element aTag) throws Exception {
		if (aTag.hasAttribute("text"))
			aComponent.setText(aTag.getAttribute("text"));
		aComponent.setReadOnly(!(Utils.getBooleanAttribute(aTag, "editable", true)));
	}

	private Component createTextField(Element aTag) throws Exception {
		PlatypusTextField component = new PlatypusTextField();
		processEvents(component, aTag);
		Publisher.publish(component);
		setText(component, aTag);
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
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createFormattedTextField(Element aTag) throws Exception {
		String formatPattern = aTag.getAttribute("format");
		int formatType = Utils.getIntegerAttribute(aTag, "valueType", ObjectFormat.MASK);

		PlatypusFormattedTextField component = new PlatypusFormattedTextField(new ObjectFormat(formatType, formatPattern));
		processEvents(component, aTag);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		
		Publisher.publish(component);
		setText(component, aTag);
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
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createTextArea(Element aTag) throws Exception {
		PlatypusTextArea component = new PlatypusTextArea();
		processEvents(component, aTag);
		Publisher.publish(component);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		setText(component, aTag);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createHtmlArea(Element aTag) throws Exception {
		PlatypusHtmlEditor component = new PlatypusHtmlEditor();
		processEvents(component, aTag);
		Publisher.publish(component);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		if (aTag.hasAttribute("text"))
			component.setValue(aTag.getAttribute("text"));
		// component.setReadOnly(!(Utils.getBooleanAttribute(aTag, "editable",
		// true)));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createProgressBar(Element aTag) throws Exception {
		PlatypusProgressBar component = new PlatypusProgressBar();
		processEvents(component, aTag);
		Publisher.publish(component);
		int minimum = Utils.getIntegerAttribute(aTag, "minimum", 0);
		int maximum = Utils.getIntegerAttribute(aTag, "maximum", 100);
		component.setRange(minimum, maximum);
		int value = Utils.getIntegerAttribute(aTag, "value", minimum);
		String string = aTag.hasAttribute("string") ? aTag.getAttribute("string") : "";
		component.setValue(value);
		component.updateProgress(value, string);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private Component createPasswordField(Element aTag) throws Exception {
		PlatypusPasswordField component = new PlatypusPasswordField();
		processEvents(component, aTag);
		Publisher.publish(component);
		setText(component, aTag);
		if (aTag.hasAttribute("emptyText"))
			component.setEmptyText(aTag.getAttribute("emptyText"));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createSeparatorMenuItem(Element aTag) throws Exception {
		SeparatorMenuItem component = new SeparatorMenuItem();
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private Component createMenuBar(Element aTag) throws Exception {
		MenuBar component = new PlatypusMenuBar();
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Component createPlainMenu(Element aTag) throws Exception {
		return createMenu(aTag, true);
	}

	private PlatypusMenu createPopupMenu(Element aTag) throws Exception {
		PlatypusMenu menuComp = createMenu(aTag, false);
		return menuComp;
	}

	private PlatypusMenu createMenu(Element aTag, boolean isPlain) throws Exception {
		PlatypusMenu component = new PlatypusMenu();
		processEvents(component, aTag);
		if (isPlain)
			Publisher.publish(component);
		else
			Publisher.publishPopup(component);
		if (aTag.hasAttribute("text")) {
			component.setText(aTag.getAttribute("text"));
		}
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		Form.inject(module, (String) component.getData(Form.PID_DATA_KEY), publishedComp);
		return component;
	}

	private Component createMenuItem(Element aTag) throws Exception {
		final MenuItem component = new MenuItem();
		processEvents(component, aTag);
		Publisher.publish(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		if (aTag.hasAttribute("icon")) {
			component.setIcon(AppClient.getInstance().getImageResource(aTag.getAttribute("icon")).addCallback(new ImageResourceCallback() {
				@Override
				public void run(ImageResource aResource) {
					component.setIcon(aResource);
				}
			}));
		}
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		Form.inject(module, (String) component.getData(Form.PID_DATA_KEY), publishedComp);
		return component;
	}

	private Component createCheckMenuItem(Element aTag) throws Exception {
		return createCheckRadioMenuItem(aTag, true);
	}

	private Component createRadioMenuItem(Element aTag) throws Exception {
		return createCheckRadioMenuItem(aTag, false);
	}

	private Component createCheckRadioMenuItem(Element aTag, boolean aCheck) throws Exception {
		PlatypusCheckRadioMenuItem component = new PlatypusCheckRadioMenuItem();
		processEvents(component, aTag);
		if (aCheck)
			Publisher.publish(component);
		else
			Publisher.publishRadio(component);
		if (aTag.hasAttribute("text"))
			component.setText(aTag.getAttribute("text"));
		component.setChecked(Utils.getBooleanAttribute(aTag, "selected", component.isChecked()));
		if (aTag.hasAttribute("buttonGroup"))
			addToToggleGroup(component, aTag.getAttribute("buttonGroup"));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		Form.inject(module, (String) component.getData(Form.PID_DATA_KEY), publishedComp);
		return component;
	}

	protected Component processGeneralProperties(final Component aComponent, Element aTag, PublishedComponent aPublished) throws Exception {
		return processGeneralProperties(aComponent, aTag, true, aPublished);
	}

	protected static int idCounter = 0;

	protected Component processGeneralProperties(final Component aComponent, Element aTag, boolean aDefaultOpaque, PublishedComponent aPublished) throws Exception {
		final String widgetName = aTag.getAttribute(NAME_ATTRIBUTE);
		if (widgetName != null)
			aComponent.setData(Form.PID_DATA_KEY, widgetName);

		if (widgetName != null && !widgetName.isEmpty())
			aComponent.setId(widgetName);
		else
			aComponent.setId("pw-" + (++idCounter));

		boolean visible = Utils.getBooleanAttribute(aTag, "visible", true);
		if (!visible)
			aComponent.setVisible(visible);
		boolean enabled = Utils.getBooleanAttribute(aTag, "enabled", true);
		if (!enabled)
			aComponent.setEnabled(enabled);

		if (aTag.hasAttribute("toolTipText")) {
			String toolTipText = aTag.getAttribute("toolTipText");
			aComponent.setTitle(toolTipText);
			// aComponent.setToolTip(toolTipText);
		}

		String prefWidth = aTag.getAttribute("prefWidth");
		String prefHeight = aTag.getAttribute("prefHeight");

		if (prefWidth != null && prefHeight != null) {
			Size size = new Size(Util.parseInt(prefWidth, 0), Util.parseInt(prefHeight, 0));
			componentsPreferredSize.put(widgetName, size);
		}
		if (aTag.hasAttribute("componentPopupMenu")) {
			final String componentPopupMenu = aTag.getAttribute("componentPopupMenu");
			postponedTasks.add(new Runnable() {

				@Override
				public void run() {
					Component comp = components.get(componentPopupMenu);
					if (comp instanceof Menu) {
						aComponent.setContextMenu((Menu) comp);
						aComponent.setData(ControlsUtils.CONTEXT_MENU, comp);
					} else
						Logger.getLogger(GxtControlsFactory.class.getName()).log(Level.WARNING, "Some other widget than Menu is assigned as componentPopupMenu in component: " + widgetName);
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
			if (fontTag != null) {
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
				aPublished.setFont(PublishedFont.create(fontFamily, fontStyle, fontSize));
			}
		}
		if (aTag.hasAttribute("cursor")) {
			aPublished.setCursor(convertCursor(Utils.getIntegerAttribute(aTag, "cursor", 0)));
		}
		return aComponent;
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

	protected void processEvents(Component aComponent, final Element aTag) throws Exception {
		// module is only default value of "this"
		final GxtEventsExecutor executor = GxtEventsExecutor.createExecutor(aComponent, module);

		handlersResolvers.add(new Runnable() {
			@Override
			public void run() {
				if (aTag.hasAttribute("actionPerformed")) {
					executor.setActionPerformed(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("actionPerformed")));
				}
				if (aTag.hasAttribute("mouseEntered")) {
					executor.setMouseEntered(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseEntered")));
				}
				if (aTag.hasAttribute("mouseExited")) {
					executor.setMouseExited(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseExited")));
				}
				if (aTag.hasAttribute("mousePressed")) {
					executor.setMousePressed(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mousePressed")));
				}
				if (aTag.hasAttribute("mouseReleased")) {
					executor.setMouseReleased(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseReleased")));
				}
				if (aTag.hasAttribute("mouseWheelMoved")) {
					executor.setMouseWheelMoved(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseWheelMoved")));
				}
				if (aTag.hasAttribute("mouseMoved")) {
					executor.setMouseMoved(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseMoved")));
				}

				if (aTag.hasAttribute("mouseClicked")) {
					executor.setMouseClicked(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseClicked")));
				}
				if (aTag.hasAttribute("mouseDragged")) {
					executor.setMouseDragged(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("mouseDragged")));
				}

				if (aTag.hasAttribute("keyTyped")) {
					executor.setKeyTyped(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("keyTyped")));
				}
				if (aTag.hasAttribute("keyPressed")) {
					executor.setKeyPressed(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("keyPressed")));
				}
				if (aTag.hasAttribute("keyReleased")) {
					executor.setKeyReleased(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("keyReleased")));
				}
				if (aTag.hasAttribute("focusGained")) {
					executor.setFocusGained(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("focusGained")));
				}
				if (aTag.hasAttribute("focusLost")) {
					executor.setFocusLost(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("focusLost")));
				}
				if (aTag.hasAttribute("componentShown")) {
					executor.setComponentShown(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentShown")));
				}
				if (aTag.hasAttribute("componentResized")) {
					executor.setComponentResized(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentResized")));
				}
				if (aTag.hasAttribute("componentHidden")) {
					executor.setComponentHidden(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentHidden")));
				}
				if (aTag.hasAttribute("componentRemoved")) {
					executor.setComponentRemoved(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentRemoved")));
				}
				if (aTag.hasAttribute("componentAdded")) {
					executor.setComponentAdded(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentAdded")));
				}

				if (aTag.hasAttribute("componentMoved")) {
					executor.setComponentMoved(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("componentMoved")));
				}
				// if (aTag.hasAttribute("itemStateChanged")) {
				// executor.setItemStateChanged(Utils.lookupProperty(module,
				// aTag.getAttribute("itemStateChanged")));
				// }
				if (aTag.hasAttribute("stateChanged")) {
					executor.setStateChanged(module.<Utils.JsModule> cast().getHandler(aTag.getAttribute("stateChanged")));
				}
				// if (aTag.hasAttribute("propertyChange")) {
				// executor.setPropertyChange(Utils.lookupProperty(module,
				// aTag.getAttribute("propertyChange")));
				// }
			}
		});
	}

	private void resolveParent(final Component aComponent, final String aParentName, final Element aConstraintsTag) throws Exception {
		postponedTasks.add(new Runnable() {
			@Override
			public void run() {
				try {
					Component parentComp = components.get(aParentName);
					if (parentComp instanceof PlatypusBorderLayoutContainer) {
						PlatypusBorderLayoutContainer container = (PlatypusBorderLayoutContainer) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("BorderLayoutConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								Size size = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
								container.add(top(aComponent), parseBorderConstraints(aConstraintsTag), size);
							} else
								throw new IllegalStateException(BORDER_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(BORDER_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof PlatypusCardLayoutContainer) {
						PlatypusCardLayoutContainer container = (PlatypusCardLayoutContainer) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("CardLayoutConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								container.add(top(aComponent), parseCardConstraints(aConstraintsTag));
							} else
								throw new IllegalStateException(CARD_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(CARD_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof PlatypusFlowLayoutContainer) {
						PlatypusFlowLayoutContainer container = (PlatypusFlowLayoutContainer) parentComp;
						Component target = top(aComponent);
						Size prefSize = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
						if (prefSize != null)
							target.setPixelSize(prefSize.getWidth(), prefSize.getHeight());
						container.add(target);
					} else if (parentComp instanceof PlatypusGridLayoutContainer) {
						PlatypusGridLayoutContainer container = (PlatypusGridLayoutContainer) parentComp;
						container.add(top(aComponent));
					} else if (parentComp instanceof PlatypusMarginLayoutContainer) {
						PlatypusMarginLayoutContainer container = (PlatypusMarginLayoutContainer) parentComp;
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
									Size prefSize = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
									constraints = parseAbsoluteConstraints(aConstraintsTag);
									if (prefSize != null) {
										if (constraints.getWidth() != null && constraints.getWidth().value == -1) {
											constraints.getWidth().value = prefSize.getWidth();
											constraints.getWidth().unit = Style.Unit.PX;
										}
										if (constraints.getHeight() != null && constraints.getHeight().value == -1) {
											constraints.getHeight().value = prefSize.getHeight();
											constraints.getHeight().unit = Style.Unit.PX;
										}
									}
								}
								if (aConstraintsTag.hasAttribute("layer")) {
									int layer = Utils.getIntegerAttribute(aConstraintsTag, "layer", 0);
								}
								container.add(top(aComponent), constraints);
							} else
								throw new IllegalStateException(ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " type.");
						} else
							throw new IllegalStateException(ABSOLUTE_OR_MARGIN_OR_LAYERS_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof PlatypusScrollContainer) {
						Size prefSize = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
						PlatypusScrollContainer container = (PlatypusScrollContainer) parentComp;
						container.setView(top(aComponent));
						container.getView().setPixelSize(prefSize.getWidth(), prefSize.getHeight());
					} else if (parentComp instanceof PlatypusDesktopContainer) {
						PlatypusDesktopContainer container = (PlatypusDesktopContainer) parentComp;
						container.add(top(aComponent));
					} else if (parentComp instanceof PlatypusSplitContainer) {
						// left & right components were processed in
						// PlatypusSplitContainer's properties' closure.
					} else if (parentComp instanceof PlatypusTabsContainer) {
						final PlatypusTabsContainer container = (PlatypusTabsContainer) parentComp;
						if (aConstraintsTag != null && aConstraintsTag.hasAttribute(TYPE_ATTRIBUTE)) {
							String contraintsTypeName = aConstraintsTag.getAttribute(TYPE_ATTRIBUTE);
							if ("TabsConstraintsDesignInfo".equalsIgnoreCase(contraintsTypeName)) {
								container.add(top(aComponent), parseTabItemConfig(aConstraintsTag, new ImageResourceCallback() {
									@Override
									public void run(ImageResource aResource) {
										container.forceTabsLayout();
									}
								}));
							} else
								throw new IllegalStateException(TABS_CONSTRAINTS_TAG_NEEED + ", but not " + contraintsTypeName + " tag.");
						} else
							throw new IllegalStateException(TABS_CONSTRAINTS_TAG_NEEED);
					} else if (parentComp instanceof ToolBar) {
						ToolBar container = (ToolBar) parentComp;
						container.add(top(aComponent));
					} else if (parentComp instanceof PlatypusHBoxLayoutContainer) {
						Component target = top(aComponent);
						Size prefSize = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
						if (prefSize != null)
							target.setPixelSize(prefSize.getWidth(), prefSize.getHeight());
						PlatypusHBoxLayoutContainer container = (PlatypusHBoxLayoutContainer) parentComp;
						container.add(target);
					} else if (parentComp instanceof PlatypusVBoxLayoutContainer) {
						Component target = top(aComponent);
						Size prefSize = componentsPreferredSize.get((String) aComponent.getData(Form.PID_DATA_KEY));
						if (prefSize != null)
							target.setPixelSize(prefSize.getWidth(), prefSize.getHeight());
						PlatypusVBoxLayoutContainer container = (PlatypusVBoxLayoutContainer) parentComp;
						container.add(target);
					} else if (parentComp instanceof MenuBar) {
						MenuBar container = (MenuBar) parentComp;
						Component top = top(aComponent);
						container.add(top);
					} else if (parentComp instanceof Menu) {
						Menu container = (Menu) parentComp;
						if (aComponent instanceof PlatypusMenu) {
							PlatypusMenu child = (PlatypusMenu) aComponent;
							MenuItem mi = new MenuItem(child.getText());
							mi.setSubMenu(child);
							container.add(mi);
						} else
							container.add(top(aComponent));
					} else if (parentComp != null)
						throw new IllegalStateException(UNKNOWN_CONTAINER_DETECTED + parentComp.getClass().getName());
					else
						throw new IllegalStateException(NULL_CONTAINER_DETECTED + aParentName);
				} catch (Exception ex) {
					Logger.getLogger(GxtControlsFactory.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		});
	}

	private Container createBorderLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		PlatypusBorderLayoutContainer component = new PlatypusBorderLayoutContainer(Utils.getIntegerAttribute(aLayoutTag, "vgap", 0), Utils.getIntegerAttribute(aLayoutTag, "hgap", 0));
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createScrollContainer(Element aTag) throws Exception {
		PlatypusScrollContainer component = new PlatypusScrollContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		int vScrollPolicy = Utils.getIntegerAttribute(aTag, "verticalScrollBarPolicy", PlatypusScrollContainer.VERTICAL_SCROLLBAR_AS_NEEDED);
		int hScrollPolicy = Utils.getIntegerAttribute(aTag, "horizontalScrollBarPolicy", PlatypusScrollContainer.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		component.setVerticalScrollBarPolicy(vScrollPolicy);
		component.setHorizontalScrollBarPolicy(hScrollPolicy);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createSplitLayoutContainer(Element aTag) throws Exception {
		final PlatypusSplitContainer component = new PlatypusSplitContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		component.setOneTouchExpandable(Utils.getBooleanAttribute(aTag, "oneTouchExpandable", false));
		component.setOrientation(Utils.getIntegerAttribute(aTag, "orientation", PlatypusSplitContainer.HORIZONTAL_SPLIT));
		component.setDividerLocation(Utils.getIntegerAttribute(aTag, "dividerLocation", 84));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		final String leftComponent = aTag.getAttribute("leftComponent");
		final String rightComponent = aTag.getAttribute("rightComponent");
		postponedTasks.add(new Runnable() {

			@Override
			public void run() {
				component.setLeftComponent(top(components.get(leftComponent)));
				component.setRightComponent(top(components.get(rightComponent)));
			}

		});
		return component;
	}

	private Container createBoxLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		Container component;
		int axis = Utils.getIntegerAttribute(aLayoutTag, "axis", 0);
		if (axis == 1 || axis == 3) {
			component = new PlatypusVBoxLayoutContainer();
			processEvents(component, aTag);
			Publisher.publish((PlatypusVBoxLayoutContainer) component);
			((VBoxLayoutContainer) component).setVBoxLayoutAlign(VBoxLayoutContainer.VBoxLayoutAlign.STRETCH);
		} else {
			component = new PlatypusHBoxLayoutContainer();
			processEvents(component, aTag);
			Publisher.publish((PlatypusHBoxLayoutContainer) component);
			((HBoxLayoutContainer) component).setHBoxLayoutAlign(HBoxLayoutContainer.HBoxLayoutAlign.STRETCH);
		}
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createCardLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		int hgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "hgap", 0) : 0;
		int vgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "vgap", 0) : 0;
		PlatypusCardLayoutContainer component = new PlatypusCardLayoutContainer(vgap, hgap);
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createAbsoluteLayoutContainer(Element aTag) throws Exception {
		return createMarginAbsoluteLayoutContainer(aTag, true);
	}

	private Container createMarginLayoutContainer(Element aTag) throws Exception {
		return createMarginAbsoluteLayoutContainer(aTag, false);
	}

	private Container createMarginAbsoluteLayoutContainer(Element aTag, boolean aAbsolute) throws Exception {
		PlatypusMarginLayoutContainer component = new PlatypusMarginLayoutContainer();
		processEvents(component, aTag);
		if (aAbsolute)
			Publisher.publishAbsolute(component);
		else
			Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createLayersLayoutContainer(Element aTag) throws Exception {
		PlatypusMarginLayoutContainer component = new PlatypusMarginLayoutContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private Container createDesktopContainer(Element aTag) throws Exception {
		PlatypusDesktopContainer component = new PlatypusDesktopContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, false, publishedComp);
		return component;
	}

	private Container createTabsContainer(Element aTag) throws Exception {
		final PlatypusTabsContainer component = new PlatypusTabsContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		if (aTag.hasAttribute("selectedComponent")) {
			final String selectedComponent = aTag.getAttribute("selectedComponent");
			postponedTasks1.add(new Runnable() {

				@Override
				public void run() {
					if (components.containsKey(selectedComponent))
						component.setSelectedComponent(components.get(selectedComponent));
				}
			});
		}
		return component;
	}

	private Container createToolBar(Element aTag) throws Exception {
		ToolBar component = new ToolBar();
		processEvents(component, aTag);
		Publisher.publish(component);
		if (Utils.getBooleanAttribute(aTag, "floatable", true)) {
			Draggable draggable = new Draggable(component);
		}
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createFlowLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		int hgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "hgap", 0) : 0;
		int vgap = aLayoutTag != null ? Utils.getIntegerAttribute(aLayoutTag, "vgap", 0) : 0;
		PlatypusFlowLayoutContainer component = new PlatypusFlowLayoutContainer(vgap, hgap);
		processEvents(component, aTag);
		Publisher.publish(component);
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private Container createGridLayoutContainer(Element aTag, Element aLayoutTag) throws Exception {
		PlatypusGridLayoutContainer component = new PlatypusGridLayoutContainer();
		processEvents(component, aTag);
		Publisher.publish(component);
		component.setHGap(Utils.getIntegerAttribute(aLayoutTag, "hgap", 0));
		component.setVGap(Utils.getIntegerAttribute(aLayoutTag, "vgap", 0));
		component.setColumns(Utils.getIntegerAttribute(aLayoutTag, "columns", 1));
		component.setRows(Utils.getIntegerAttribute(aLayoutTag, "rows", 1));
		checkBorders(component, aTag);
		PublishedComponent publishedComp = (PublishedComponent) component.getData(Form.PUBLISHED_DATA_KEY);
		processGeneralProperties(component, aTag, publishedComp);
		return component;
	}

	private static LayoutRegion parseBorderConstraints(Element aTag) {
		String place = aTag.getAttribute("place");
		if ("north".equalsIgnoreCase(place) || "first".equalsIgnoreCase(place)) {
			return LayoutRegion.NORTH;
		} else if ("west".equalsIgnoreCase(place) || "before".equalsIgnoreCase(place)) {
			return LayoutRegion.WEST;
		} else if ("east".equalsIgnoreCase(place) || "after".equalsIgnoreCase(place)) {
			return LayoutRegion.EAST;
		} else if ("south".equalsIgnoreCase(place) || "last".equalsIgnoreCase(place)) {
			return LayoutRegion.SOUTH;
		}
		return LayoutRegion.CENTER;
	}

	private static String parseCardConstraints(Element aTag) {
		if (aTag.hasAttribute("cardName"))
			return aTag.getAttribute("cardName");
		else
			return "";
	}

	private static TabItemConfig parseTabItemConfig(Element aTag, final ImageResourceCallback aImageLoadedCallback) {
		final TabItemConfig config = new TabItemConfig();
		if (aTag.hasAttribute("tabTitle")) {
			String value = aTag.getAttribute("tabTitle");
			if (value != null && value.startsWith(PlatypusLabel.HTML_SWING_PREFIX))
				config.setHTML(value.substring(PlatypusLabel.HTML_SWING_PREFIX.length()));
			else
				config.setText(value);
		}
		/*
		 * if (aTag.hasAttribute("tabTooltipText"))
		 * config.setText(aTag.getAttribute("tabTooltipText"));
		 */
		if (aTag.hasAttribute("icon")) {
			config.setIcon(AppClient.getInstance().getImageResource(aTag.getAttribute("icon")).addCallback(new ImageResourceCallback() {
				@Override
				public void run(ImageResource aResource) {
					config.setIcon(aResource);
					if (aImageLoadedCallback != null)
						aImageLoadedCallback.run(aResource);
				}
			}));
		}
		config.setClosable(false);
		return config;
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

	public Form getForm() {
		return form;
	}

	public static JavaScriptObject lookupPublishedParent(Component aComponent) {
		assert aComponent != null;
		Widget parent = aComponent;
		while (parent != null) {
			if (parent instanceof PlatypusMenu) {
				PlatypusMenu menu = (PlatypusMenu) parent;
				parent = menu.getParentItem();
				if (parent == null) {
					parent = menu.getParentMenuBarItem();
				}
			} else {
				parent = parent.getParent();
			}
			if (parent instanceof Component) {
				Object published = ((Component) parent).getData(Form.PUBLISHED_DATA_KEY);
				if (published != null && !(parent instanceof FieldSet)) {
					assert published instanceof JavaScriptObject;
					return (JavaScriptObject) published;
				}
			}
		}
		return null;
	}
}

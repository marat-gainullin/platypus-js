/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.HasImageParagraph;
import com.bearsoft.gwt.ui.HasImageResource;
import com.bearsoft.gwt.ui.Orientation;
import com.bearsoft.gwt.ui.widgets.DropDownButton;
import com.bearsoft.gwt.ui.widgets.ImageButton;
import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.eas.client.CallbackAdapter;
import com.eas.client.Utils;
import com.eas.client.Utils.JsObject;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.grid.columns.header.CheckHeaderNode;
import com.eas.client.form.grid.columns.header.ModelHeaderNode;
import com.eas.client.form.grid.columns.header.RadioHeaderNode;
import com.eas.client.form.grid.columns.header.ServiceHeaderNode;
import com.eas.client.form.published.containers.AnchorsPane;
import com.eas.client.form.published.containers.BorderPane;
import com.eas.client.form.published.containers.BoxPane;
import com.eas.client.form.published.containers.ButtonGroup;
import com.eas.client.form.published.containers.CardPane;
import com.eas.client.form.published.containers.FlowPane;
import com.eas.client.form.published.containers.GridPane;
import com.eas.client.form.published.containers.ScrollPane;
import com.eas.client.form.published.containers.SplitPane;
import com.eas.client.form.published.containers.TabbedPane;
import com.eas.client.form.published.containers.ToolBar;
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
import com.eas.client.form.published.widgets.model.ModelCheck;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.ModelDecoratorBox;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.eas.client.form.published.HasBinding;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.HasJsName;
import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.PublishedFont;
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
public class FormFactory {

	protected static class Dimension {
		public int width;
		public int height;
	}

	protected Element element;
	protected JsObject model;
	protected PlatypusWindow form;
	protected Map<String, UIObject> widgets = new HashMap<>();
	protected List<UIObject> widgetsList = new ArrayList<>();
	protected String rootContainerName;
	//
	protected List<Runnable> resolvers = new ArrayList<>();

	public FormFactory(Element anElement, JavaScriptObject aModel) {
		super();
		element = anElement;
		model = aModel != null ? aModel.<JsObject> cast() : null;
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
					String wName = ((HasJsName) widget).getJsName();
					assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
					widgets.put(wName, widget);
					widgetsList.add(widget);
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

	protected JsObject resolveEntity(String aEntityName) throws Exception {
		if (model.has(aEntityName)) {
			JavaScriptObject oEntity = model.getJs(aEntityName);
			if (oEntity != null) {
				return oEntity.cast();
			}
		}
		return null;
	}

	protected JsObject resolveEntity(long aEntityId) throws Exception {
		return null;
	}

	private UIObject readWidget(Element anElement) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		// widgets
		case "Label":
			final PlatypusLabel label = new PlatypusLabel();
			Publisher.publish(label);
			readGeneralProps(anElement, label);
			readImageParagraph(anElement, label);
			return label;
		case "Button":
			PlatypusButton button = new PlatypusButton();
			Publisher.publish(button);
			readGeneralProps(anElement, button);
			readImageParagraph(anElement, button);
			return button;
		case "DropDownButton":
			final PlatypusSplitButton dropDownButton = new PlatypusSplitButton();
			Publisher.publish(dropDownButton);
			readGeneralProps(anElement, dropDownButton);
			readImageParagraph(anElement, dropDownButton);
			if (anElement.hasAttribute("dropDownMenu")) {
				final String dropDownMenuName = anElement.getAttribute("dropDownMenu");
				resolvers.add(new Runnable() {
					public void run() {
						if (widgets.containsKey(dropDownMenuName)) {
							UIObject compMenu = widgets.get(dropDownMenuName);
							if (compMenu instanceof PlatypusPopupMenu) {
								dropDownButton.setMenu((PlatypusPopupMenu) compMenu);
							}
						}
					}
				});
			}
			return dropDownButton;
		case "ButtonGroup":
			ButtonGroup buttonGroup = new ButtonGroup();
			Publisher.publish(buttonGroup);
			if (anElement.hasAttribute("name")) {
				buttonGroup.setJsName(anElement.getAttribute("name"));
			}
			return buttonGroup;
		case "CheckBox":
			PlatypusCheckBox checkBox = new PlatypusCheckBox();
			Publisher.publish(checkBox);
			readGeneralProps(anElement, checkBox);
			readImageParagraph(anElement, checkBox);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				checkBox.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				checkBox.setText(anElement.getAttribute("text"));
			}
			return checkBox;
		case "TextArea":
			PlatypusTextArea textArea = new PlatypusTextArea();
			Publisher.publish(textArea);
			readGeneralProps(anElement, textArea);
			if (anElement.hasAttribute("text")) {
				textArea.setText(anElement.getAttribute("text"));
			}
			return textArea;
		case "HtmlArea":
			PlatypusHtmlEditor htmlArea = new PlatypusHtmlEditor();
			Publisher.publish(htmlArea);
			readGeneralProps(anElement, htmlArea);
			if (anElement.hasAttribute("text")) {
				String text = anElement.getAttribute("text");
				htmlArea.setValue(text); 
			}
			return htmlArea;
		case "FormattedField": {
			PlatypusFormattedTextField formattedField = new PlatypusFormattedTextField();
			Publisher.publish(formattedField);
			readGeneralProps(anElement, formattedField);
			String format = anElement.getAttribute("format");
			int valueType = Utils.getIntegerAttribute(anElement, "valueType", ObjectFormat.REGEXP);
			formattedField.setValueType(valueType);
			formattedField.setFormat(format);
			if (anElement.hasAttribute("text")) {
				formattedField.setText(anElement.getAttribute("text"));
			}
			return formattedField;
		}
		case "PasswordField":
			PlatypusPasswordField passwordField = new PlatypusPasswordField();
			Publisher.publish(passwordField);
			readGeneralProps(anElement, passwordField);
			if (anElement.hasAttribute("text")) {
				passwordField.setText(anElement.getAttribute("text"));
			}
			return passwordField;
		case "ProgressBar": {
			PlatypusProgressBar progressBar = new PlatypusProgressBar();
			Publisher.publish(progressBar);
			readGeneralProps(anElement, progressBar);
			int minimum = Utils.getIntegerAttribute(anElement, "minimum", 0);
			int value = Utils.getIntegerAttribute(anElement, "value", 0);
			int maximum = Utils.getIntegerAttribute(anElement, "maximum", 100);
			progressBar.setMinProgress(minimum);
			progressBar.setMaxProgress(maximum);
			progressBar.setValue((double) value);
			if (anElement.hasAttribute("string")) {
				progressBar.setText(anElement.getAttribute("string"));
			}
			return progressBar;
		}
		case "RadioButton":
			PlatypusRadioButton radio = new PlatypusRadioButton();
			Publisher.publish(radio);
			readGeneralProps(anElement, radio);
			readImageParagraph(anElement, radio);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				radio.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				radio.setText(anElement.getAttribute("text"));
			}
			return radio;
		case "Slider":
			PlatypusSlider slider = new PlatypusSlider();
			Publisher.publish(slider);
			readGeneralProps(anElement, slider);
			int minimum = Utils.getIntegerAttribute(anElement, "minimum", 0);
			int value = Utils.getIntegerAttribute(anElement, "value", 0);
			int maximum = Utils.getIntegerAttribute(anElement, "maximum", 100);
			slider.setMinValue(minimum);
			slider.setMaxValue(maximum);
			slider.setValue((double) value);
			return slider;
		case "TextField":
			PlatypusTextField textField = new PlatypusTextField();
			Publisher.publish(textField);
			readGeneralProps(anElement, textField);
			if (anElement.hasAttribute("text")) {
				textField.setText(anElement.getAttribute("text"));
			}
			return textField;
		case "ToggleButton":
			PlatypusToggleButton toggle = new PlatypusToggleButton();
			Publisher.publish(toggle);
			readGeneralProps(anElement, toggle);
			readImageParagraph(anElement, toggle);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				toggle.setValue(selected);
			}
			return toggle;
		case "DesktopPane":
			DesktopPane desktop = new DesktopPane();
			Publisher.publish(desktop);
			readGeneralProps(anElement, desktop);
			return desktop;
			// model widgets
		case "ModelCheckBox":
			ModelCheck modelCheckBox = new ModelCheck();
			Publisher.publish(modelCheckBox);
			readGeneralProps(anElement, modelCheckBox);
			if (anElement.hasAttribute("text")) {
				modelCheckBox.setText(anElement.getAttribute("text"));
			}
			return modelCheckBox;
		case "ModelCombo":
			ModelCombo modelCombo = new ModelCombo();
			Publisher.publish(modelCombo);
			readGeneralProps(anElement, modelCombo);
			boolean list = Utils.getBooleanAttribute(anElement, "list", Boolean.TRUE);
			modelCombo.setList(list);
			if (anElement.hasAttribute("displayList")) {
				String displayList = anElement.getAttribute("displayList");
				modelCombo.setDisplayList(resolveEntity(displayList));
			}
			if (anElement.hasAttribute("displayField")) {
				String displayField = anElement.getAttribute("displayField");
				modelCombo.setDisplayField(displayField);
			}
			return modelCombo;
		case "ModelDate":
			ModelDate modelDate = new ModelDate();
			Publisher.publish(modelDate);
			readGeneralProps(anElement, modelDate);
			if (anElement.hasAttribute("dateFormat")) {
				String dateFormat = anElement.getAttribute("dateFormat");
				try {
					modelDate.setFormat(dateFormat);
				} catch (Exception ex) {
					Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (anElement.hasAttribute("datePicker")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "datePicker", Boolean.FALSE);
				modelDate.setDateShown(selected);
			}
			if (anElement.hasAttribute("timePicker")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "timePicker", Boolean.FALSE);
				modelDate.setTimeShown(selected);
			}
			return modelDate;
		case "ModelFormattedField":
			ModelFormattedField modelFormattedField = new ModelFormattedField();
			Publisher.publish(modelFormattedField);
			readGeneralProps(anElement, modelFormattedField);
			try {
				String format = anElement.getAttribute("format");
				int valueType = Utils.getIntegerAttribute(anElement, "valueType", ObjectFormat.REGEXP);
				modelFormattedField.setValueType(valueType);
				modelFormattedField.setFormat(format);
				if (anElement.hasAttribute("text")) {
					modelFormattedField.setText(anElement.getAttribute("text"));
				}
			} catch (Exception ex) {
				Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			return modelFormattedField;
		case "ModelSpin":
			ModelSpin modelSpin = new ModelSpin();
			Publisher.publish(modelSpin);
			readGeneralProps(anElement, modelSpin);
			Double min = null;
			if (anElement.hasAttribute("min"))
				min = Utils.getDoubleAttribute(anElement, "min", -Double.MAX_VALUE);
			double step = Utils.getDoubleAttribute(anElement, "step", 1.0d);
			Double max = null;
			if (anElement.hasAttribute("max"))
				max = Utils.getDoubleAttribute(anElement, "max", Double.MAX_VALUE);
			try {
				modelSpin.setMin(min);
				modelSpin.setMax(max);
				modelSpin.setStep(step);
			} catch (Exception ex) {
				Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			return modelSpin;
		case "ModelTextArea":
			ModelTextArea modelTextArea = new ModelTextArea();
			Publisher.publish(modelTextArea);
			readGeneralProps(anElement, modelTextArea);
			if (anElement.hasAttribute("text")) {
				modelTextArea.setValue(anElement.getAttribute("text"));
			}
			return modelTextArea;
		case "ModelGrid": {
			ModelGrid grid = new ModelGrid();
			Publisher.publish(grid);
			readGeneralProps(anElement, grid);
			int frozenColumns = Utils.getIntegerAttribute(anElement, "frozenColumns", 0);
			int frozenRows = Utils.getIntegerAttribute(anElement, "frozenRows", 0);
			boolean insertable = Utils.getBooleanAttribute(anElement, "insertable", Boolean.TRUE);
			boolean deletable = Utils.getBooleanAttribute(anElement, "deletable", Boolean.TRUE);
			boolean editable = Utils.getBooleanAttribute(anElement, "editable", Boolean.TRUE);
			boolean showHorizontalLines = Utils.getBooleanAttribute(anElement, "showHorizontalLines", Boolean.TRUE);
			boolean showVerticalLines = Utils.getBooleanAttribute(anElement, "showVerticalLines", Boolean.TRUE);
			boolean showOddRowsInOtherColor = Utils.getBooleanAttribute(anElement, "showOddRowsInOtherColor", Boolean.TRUE);
			int rowsHeight = Utils.getIntegerAttribute(anElement, "rowsHeight", 20);
			grid.setRowsHeight(rowsHeight);
			grid.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
			grid.setShowVerticalLines(showVerticalLines);
			grid.setShowHorizontalLines(showHorizontalLines);
			grid.setEditable(editable);
			grid.setDeletable(deletable);
			grid.setInsertable(insertable);
			grid.setFrozenColumns(frozenColumns);
			grid.setFrozenRows(frozenRows);
			if (anElement.hasAttribute("oddRowsColor")) {
				String oddRowsColorDesc = anElement.getAttribute("oddRowsColor");
				grid.setOddRowsColor(ControlsUtils.parseColor(oddRowsColorDesc));
			}
			if (anElement.hasAttribute("gridColor")) {
				String gridColorDesc = anElement.getAttribute("gridColor");
				grid.setGridColor(ControlsUtils.parseColor(gridColorDesc));
			}
			if (anElement.hasAttribute("parentField")) {
				String parentFieldPath = anElement.getAttribute("parentField");
				grid.setParentField(parentFieldPath);
			}
			if (anElement.hasAttribute("childrenField")) {
				String childrenFieldPath = anElement.getAttribute("childrenField");
				grid.setChildrenField(childrenFieldPath);
			}
			List<HeaderNode<JavaScriptObject>> roots = readColumns(anElement);
			grid.setHeader(roots);
			if (anElement.hasAttribute("data")) {
				String entityName = anElement.getAttribute("data");
				try {
					grid.setData(resolveEntity(entityName));
				} catch (Exception ex) {
					Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE,
					        "While setting data to named model's property " + entityName + " to widget " + grid.getJsName() + " exception occured: " + ex.getMessage());
				}
			}
			if (anElement.hasAttribute("field")) {
				String dataPropertyPath = anElement.getAttribute("field");
				grid.setField(dataPropertyPath);
			}
			return grid;
		}
		// containers
		case "AnchorsPane":
			AnchorsPane anchorsPane = new AnchorsPane();
			Publisher.publish(anchorsPane);
			readGeneralProps(anElement, anchorsPane);
			return anchorsPane;
		case "BorderPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			BorderPane borderPane = new BorderPane(hgap, vgap);
			Publisher.publish(borderPane);
			readGeneralProps(anElement, borderPane);
			return borderPane;
		}
		case "BoxPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "orientation", Orientation.HORIZONTAL);
			BoxPane boxPane = new BoxPane(orientation, hgap, vgap);
			Publisher.publish(boxPane);
			readGeneralProps(anElement, boxPane);
			return boxPane;
		}
		case "CardPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			CardPane cardPane = new CardPane(hgap, vgap);
			Publisher.publish(cardPane);
			readGeneralProps(anElement, cardPane);
			return cardPane;
		}
		case "FlowPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			FlowPane flowPane = new FlowPane(hgap, vgap);
			Publisher.publish(flowPane);
			readGeneralProps(anElement, flowPane);
			return flowPane;
		}
		case "GridPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			int rows = Utils.getIntegerAttribute(anElement, "rows", 0);
			int columns = Utils.getIntegerAttribute(anElement, "columns", 0);
			GridPane gridPane = new GridPane(rows, columns, hgap, vgap);
			Publisher.publish(gridPane);
			readGeneralProps(anElement, gridPane);
			return gridPane;
		}
		// predefined layout containers
		case "ScrollPane":
			ScrollPane scroll = new ScrollPane();
			Publisher.publish(scroll);
			readGeneralProps(anElement, scroll);
			boolean wheelScrollingEnabled = Utils.getBooleanAttribute(anElement, "wheelScrollingEnabled", Boolean.TRUE);
			int horizontalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "horizontalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			int verticalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "verticalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
			scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
			return scroll;
		case "SplitPane":
			final SplitPane split = new SplitPane();
			Publisher.publish(split);
			readGeneralProps(anElement, split);
			boolean oneTouchExpandable = Utils.getBooleanAttribute(anElement, "oneTouchExpandable", true);
			int dividerLocation = Utils.getIntegerAttribute(anElement, "dividerLocation", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "orientation", Orientation.VERTICAL);
			split.setDividerLocation(dividerLocation);
			split.setOrientation(orientation);
			split.setOneTouchExpandable(oneTouchExpandable);
			if (anElement.hasAttribute("leftComponent")) {
				final String leftComponentName = anElement.getAttribute("leftComponent");
				resolvers.add(new Runnable() {
					@Override
					public void run() {
						UIObject leftComponent = widgets.get(leftComponentName);
						split.setFirstWidget((Widget) leftComponent);
					}
				});
			}
			if (anElement.hasAttribute("rightComponent")) {
				final String rightComponentName = anElement.getAttribute("rightComponent");
				resolvers.add(new Runnable() {
					@Override
					public void run() {
						UIObject rightComponent = widgets.get(rightComponentName);
						split.setSecondWidget((Widget) rightComponent);
					}
				});
			}
			return split;
		case "TabbedPane":
			TabbedPane tabs = new TabbedPane();
			Publisher.publish(tabs);
			readGeneralProps(anElement, tabs);
			/*
			 * int tabPlacement = Utils.getIntegerAttribute(anElement,
			 * "tabPlacement", TabbedPane.TOP);
			 * tabs.setTabPlacement(tabPlacement);
			 */
			return tabs;
		case "ToolBar":
			ToolBar toolbar = new ToolBar();
			Publisher.publish(toolbar);
			readGeneralProps(anElement, toolbar);
			return toolbar;
			// menus
		case "Menu":
			PlatypusMenu menu = new PlatypusMenu();
			Publisher.publish(menu);
			readGeneralProps(anElement, menu);
			if (anElement.hasAttribute("text")) {
				menu.setText(anElement.getAttribute("text"));
			}
			return menu;
		case "MenuItem":
			PlatypusMenuItemImageText menuitem = new PlatypusMenuItemImageText();
			Publisher.publish(menuitem);
			readGeneralProps(anElement, menuitem);
			readImageParagraph(anElement, menuitem);
			if (anElement.hasAttribute("text")) {
				menuitem.setText(anElement.getAttribute("text"));
			}
			return menuitem;
		case "CheckMenuItem":
			PlatypusMenuItemCheckBox checkMenuItem = new PlatypusMenuItemCheckBox();
			Publisher.publish(checkMenuItem);
			readGeneralProps(anElement, checkMenuItem);
			readImageParagraph(anElement, checkMenuItem);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				checkMenuItem.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				checkMenuItem.setText(anElement.getAttribute("text"));
			}
			return checkMenuItem;
		case "RadioMenuItem":
			PlatypusMenuItemRadioButton radioMenuItem = new PlatypusMenuItemRadioButton();
			Publisher.publish(radioMenuItem);
			readGeneralProps(anElement, radioMenuItem);
			readImageParagraph(anElement, radioMenuItem);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				radioMenuItem.setValue(selected);
			}
			if (anElement.hasAttribute("text")) {
				radioMenuItem.setText(anElement.getAttribute("text"));
			}
			return radioMenuItem;
		case "MenuSeparator":
			PlatypusMenuItemSeparator menuSeparator = new PlatypusMenuItemSeparator();
			Publisher.publish(menuSeparator);
			readGeneralProps(anElement, menuSeparator);
			return menuSeparator;
		case "MenuBar":
			PlatypusMenuBar menuBar = new PlatypusMenuBar();
			Publisher.publish(menuBar);
			readGeneralProps(anElement, menuBar);
			return menuBar;
		case "PopupMenu":
			PlatypusPopupMenu popupMenu = new PlatypusPopupMenu();
			Publisher.publish(popupMenu);
			readGeneralProps(anElement, popupMenu);
			return popupMenu;
		default:
			return null;
		}
	}

	protected void readImageParagraph(Element anElement, final UIObject aImageParagraph) throws Exception {
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
			hip.setHorizontalAlignment(Utils.getIntegerAttribute(anElement, "horizontalAlignment", aImageParagraph instanceof ImageButton || aImageParagraph instanceof DropDownButton ? HasImageParagraph.CENTER
			        : HasImageParagraph.LEFT));
			hip.setVerticalAlignment(Utils.getIntegerAttribute(anElement, "verticalAlignment", HasImageParagraph.CENTER));
			hip.setIconTextGap(Utils.getIntegerAttribute(anElement, "iconTextGap", 4));
			hip.setHorizontalTextPosition(Utils.getIntegerAttribute(anElement, "horizontalTextPosition", HasImageParagraph.RIGHT));
			hip.setVerticalTextPosition(Utils.getIntegerAttribute(anElement, "verticalTextPosition", HasImageParagraph.CENTER));
		}
	}

	private void readGeneralProps(final Element anElement, final UIObject aTarget) throws Exception {
		String widgetName = "";
		if (anElement.hasAttribute("name") && aTarget instanceof HasJsName) {
			widgetName = anElement.getAttribute("name");
			((HasJsName) aTarget).setJsName(widgetName);
		}
		if (anElement.hasAttribute("nullable") && aTarget instanceof ModelDecoratorBox<?>) {
			((ModelDecoratorBox<?>) aTarget).setNullable(Utils.getBooleanAttribute(anElement, "nullable", true));
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
				PublishedColor background = ControlsUtils.parseColor(anElement.getAttribute("background"));
				pComp.setBackground(background);
			}
			if (anElement.hasAttribute("foreground")) {
				PublishedColor foreground = ControlsUtils.parseColor(anElement.getAttribute("foreground"));
				pComp.setForeground(foreground);
			}
			if (anElement.hasAttribute("toolTipText")) {
				pComp.setToolTipText(anElement.getAttribute("toolTipText"));
			}
		}
		/*
		 * int cursorId = Utils.getIntegerAttribute(anElement, "cursor",
		 * com.eas.gui.Cursor.DEFAULT_CURSOR); aTarget.setCursor(new
		 * com.eas.gui.Cursor(cursorId));
		 */
		if (anElement.hasAttribute("visible")) {
			aTarget.setVisible(Utils.getBooleanAttribute(anElement, "visible", Boolean.TRUE));
		}
		/*
		 * if (anElement.hasAttribute("nextFocusableComponent")) { final String
		 * nextFocusableName = anElement.getAttribute("nextFocusableComponent");
		 * if (!nextFocusableName.isEmpty()) { resolvers.add(new Runnable() {
		 * 
		 * @Override public void run() { UIObject nextFocusable =
		 * widgets.get(nextFocusableName);
		 * aTarget.setNextFocusableComponent(nextFocusable); } }); } }
		 */
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

	protected PublishedFont readFont(Element anElement) throws Exception {
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

	private List<HeaderNode<JavaScriptObject>> readColumns(Element aColumnsElement) throws Exception {
		List<HeaderNode<JavaScriptObject>> nodes = new ArrayList<>();
		Node childNode = aColumnsElement.getFirstChild();
		while (childNode != null) {
			if (childNode instanceof Element) {
				Element childTag = (Element) childNode;
				String columnType = childTag.getTagName();
				switch (columnType) {
				case "CheckGridColumn": {
					CheckHeaderNode column = new CheckHeaderNode();
					Publisher.publish(column);
					readColumnNode(column, childTag);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "RadioGridColumn": {
					RadioHeaderNode column = new RadioHeaderNode();
					Publisher.publish(column);
					readColumnNode(column, childTag);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "ServiceGridColumn": {
					ServiceHeaderNode column = new ServiceHeaderNode();
					Publisher.publish(column);
					readColumnNode(column, childTag);
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				case "ModelGridColumn": {
					ModelHeaderNode column = new ModelHeaderNode();
					Publisher.publish(column);
					readColumnNode(column, childTag);
					if (childTag.hasAttribute("field")) {
						column.setField(childTag.getAttribute("field"));
					}
					if (childTag.hasAttribute("sortField")) {
						column.setSortField(childTag.getAttribute("sortField"));
					}
					Node _childNode = childTag.getFirstChild();
					while (_childNode != null) {
						if (_childNode instanceof Element) {
							Element _childTag = (Element) _childNode;
							UIObject editorComp = readWidget(_childTag);
							if (editorComp instanceof ModelDecoratorBox<?>) {
								ModelColumn col = (ModelColumn) column.getColumn();
								col.setEditor((ModelDecoratorBox<Object>) editorComp);
								// ModelWidget viewComp = (ModelWidget)
								// readWidget((Element) _childNode);
								// col.setView(viewComp);
								break;
							}
						}
						_childNode = _childNode.getNextSibling();
					}
					nodes.add(column);
					List<HeaderNode<JavaScriptObject>> children = readColumns(childTag);
					for (int i = 0; i < children.size(); i++) {
						column.addColumnNode(children.get(i));
					}
					break;
				}
				}
			}
			childNode = childNode.getNextSibling();
		}
		return nodes;
	}

	private void readColumnNode(ModelHeaderNode aNode, Element anElement) throws Exception {
		aNode.setJsName(anElement.getAttribute("name"));
		if (anElement.hasAttribute("title")) {
			aNode.setTitle(anElement.getAttribute("title"));
		}
		if (anElement.hasAttribute("background")) {
			PublishedColor background = ControlsUtils.parseColor(anElement.getAttribute("background"));
			aNode.setBackground(background);
		}
		if (anElement.hasAttribute("foreground")) {
			PublishedColor foreground = ControlsUtils.parseColor(anElement.getAttribute("foreground"));
			aNode.setForeground(foreground);
		}
		aNode.setReadonly(Utils.getBooleanAttribute(anElement, "readonly", Boolean.FALSE));
		// aNode.setEnabled(Utils.getBooleanAttribute(anElement, "enabled",
		// Boolean.TRUE));
		PublishedFont font = readFont(anElement);
		if (font != null) {
			aNode.setFont(font);
		}
		if (anElement.hasAttribute("minWidth")) {
			String minWidth = anElement.getAttribute("minWidth");
			if (minWidth.length() > 2 && minWidth.endsWith("px")) {
				aNode.setMinWidth(Integer.parseInt(minWidth.substring(0, minWidth.length() - 2)));
			}
		}
		if (anElement.hasAttribute("maxWidth")) {
			String maxWidth = anElement.getAttribute("maxWidth");
			if (maxWidth.length() > 2 && maxWidth.endsWith("px")) {
				aNode.setMaxWidth(Integer.parseInt(maxWidth.substring(0, maxWidth.length() - 2)));
			}
		}
		if (anElement.hasAttribute("preferredWidth")) {
			String preferredWidth = anElement.getAttribute("preferredWidth");
			if (preferredWidth.length() > 2 && preferredWidth.endsWith("px")) {
				aNode.setPreferredWidth(Integer.parseInt(preferredWidth.substring(0, preferredWidth.length() - 2)));
			}
		}
		aNode.setMoveable(Utils.getBooleanAttribute(anElement, "movable", Boolean.TRUE));
		aNode.setResizable(Utils.getBooleanAttribute(anElement, "resizable", aNode instanceof CheckHeaderNode || aNode instanceof RadioHeaderNode || aNode instanceof ServiceHeaderNode ? Boolean.FALSE
		        : Boolean.TRUE));
		// aNode.setSelectOnly(Utils.getBooleanAttribute(anElement,
		// "selectOnly", Boolean.FALSE));
		aNode.setSortable(Utils.getBooleanAttribute(anElement, "sortable", Boolean.TRUE));
		aNode.setVisible(Utils.getBooleanAttribute(anElement, "visible", Boolean.TRUE));
	}
}

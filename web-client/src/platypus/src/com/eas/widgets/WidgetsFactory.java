package com.eas.widgets;

import com.eas.core.Utils;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.ButtonGroup;
import com.eas.ui.Orientation;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.widgets.boxes.ObjectFormat;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;

public class WidgetsFactory implements UiWidgetReader{

	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		// widgets
		case "lb":
		case "Label":
			final PlatypusLabel label = new PlatypusLabel();
			WidgetsPublisher.publish(label);
			aFactory.readGeneralProps(anElement, label);
			aFactory.readImageParagraph(anElement, label);
			return label;
		case "bt":
		case "Button":
			PlatypusButton button = new PlatypusButton();
			WidgetsPublisher.publish(button);
			aFactory.readGeneralProps(anElement, button);
			aFactory.readImageParagraph(anElement, button);
			return button;
		case "ddb":
		case "DropDownButton":
			final PlatypusSplitButton dropDownButton = new PlatypusSplitButton();
			WidgetsPublisher.publish(dropDownButton);
			aFactory.readGeneralProps(anElement, dropDownButton);
			aFactory.readImageParagraph(anElement, dropDownButton);
			if (Utils.hasAttribute(anElement, "ddm", "dropDownMenu")) {
				final String dropDownMenuName = Utils.getAttribute(anElement, "ddm", "dropDownMenu", null);
				aFactory.addResolver(new Runnable() {
					public void run() {
						if (aFactory.getWidgets().containsKey(dropDownMenuName)) {
							UIObject compMenu = aFactory.getWidgets().get(dropDownMenuName);
							if (compMenu instanceof PlatypusPopupMenu) {
								dropDownButton.setMenu((PlatypusPopupMenu) compMenu);
							}
						}
					}
				});
			}
			return dropDownButton;
		case "bg":
		case "ButtonGroup":
			ButtonGroup buttonGroup = new ButtonGroup();
			WidgetsPublisher.publish(buttonGroup);
			if (Utils.hasAttribute(anElement, "n", "name")) {
				buttonGroup.setJsName(Utils.getAttribute(anElement, "n", "name", null));
			}
			return buttonGroup;
		case "cb":
		case "CheckBox":
			PlatypusCheckBox checkBox = new PlatypusCheckBox();
			WidgetsPublisher.publish(checkBox);
			aFactory.readGeneralProps(anElement, checkBox);
			aFactory.readImageParagraph(anElement, checkBox);
			if (Utils.hasAttribute(anElement, "st", "selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
				checkBox.setValue(selected);
			}
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				checkBox.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return checkBox;
		case "ta":
		case "TextArea":
			PlatypusTextArea textArea = new PlatypusTextArea();
			WidgetsPublisher.publish(textArea);
			aFactory.readGeneralProps(anElement, textArea);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				textArea.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return textArea;
		case "ha":
		case "HtmlArea":
			PlatypusHtmlEditor htmlArea = new PlatypusHtmlEditor();
			WidgetsPublisher.publish(htmlArea);
			aFactory.readGeneralProps(anElement, htmlArea);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				String text = Utils.getAttribute(anElement, "tx", "text", null);
				htmlArea.setValue(text); 
			}
			return htmlArea;
		case "ff":
		case "FormattedField": {
			PlatypusFormattedTextField formattedField = new PlatypusFormattedTextField();
			WidgetsPublisher.publish(formattedField);
			aFactory.readGeneralProps(anElement, formattedField);
			String format = Utils.getAttribute(anElement, "fr", "format", null);
			int valueType = Utils.getIntegerAttribute(anElement, "vt", "valueType", ObjectFormat.REGEXP);
			formattedField.setValueType(valueType);
			formattedField.setFormat(format);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				formattedField.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return formattedField;
		}
		case "pf":
		case "PasswordField":
			PlatypusPasswordField passwordField = new PlatypusPasswordField();
			WidgetsPublisher.publish(passwordField);
			aFactory.readGeneralProps(anElement, passwordField);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				passwordField.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return passwordField;
		case "pb":
		case "ProgressBar": {
			PlatypusProgressBar progressBar = new PlatypusProgressBar();
			WidgetsPublisher.publish(progressBar);
			aFactory.readGeneralProps(anElement, progressBar);
			int minimum = Utils.getIntegerAttribute(anElement, "mm", "minimum", 0);
			int value = Utils.getIntegerAttribute(anElement, "vl", "value", 0);
			int maximum = Utils.getIntegerAttribute(anElement, "mx", "maximum", 100);
			progressBar.setMinProgress(minimum);
			progressBar.setMaxProgress(maximum);
			progressBar.setValue((double) value);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				progressBar.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return progressBar;
		}
		case "rb":
		case "RadioButton":
			PlatypusRadioButton radio = new PlatypusRadioButton();
			WidgetsPublisher.publish(radio);
			aFactory.readGeneralProps(anElement, radio);
			aFactory.readImageParagraph(anElement, radio);
			if (Utils.hasAttribute(anElement, "st", "selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
				radio.setValue(selected);
			}
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				radio.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return radio;
		case "s":
		case "Slider":
			PlatypusSlider slider = new PlatypusSlider();
			WidgetsPublisher.publish(slider);
			aFactory.readGeneralProps(anElement, slider);
			int minimum = Utils.getIntegerAttribute(anElement, "mm", "minimum", 0);
			int value = Utils.getIntegerAttribute(anElement, "vl", "value", 0);
			int maximum = Utils.getIntegerAttribute(anElement, "mx", "maximum", 100);
			slider.setMinValue(minimum);
			slider.setMaxValue(maximum);
			slider.setValue((double) value);
			return slider;
		case "tf":
		case "TextField":
			PlatypusTextField textField = new PlatypusTextField();
			WidgetsPublisher.publish(textField);
			aFactory.readGeneralProps(anElement, textField);
			if (Utils.hasAttribute(anElement, "tx", "text")) {
				textField.setText(Utils.getAttribute(anElement, "tx", "text", null));
			}
			return textField;
		case "tb":
		case "ToggleButton":
			PlatypusToggleButton toggle = new PlatypusToggleButton();
			WidgetsPublisher.publish(toggle);
			aFactory.readGeneralProps(anElement, toggle);
			aFactory.readImageParagraph(anElement, toggle);
			if (Utils.hasAttribute(anElement, "st", "selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
				toggle.setValue(selected);
			}
			return toggle;
		case "dp":
		case "DesktopPane":
			DesktopPane desktop = new DesktopPane();
			WidgetsPublisher.publish(desktop);
			aFactory.readGeneralProps(anElement, desktop);
			return desktop;
		case "ap":
		case "AnchorsPane":
			AnchorsPane anchorsPane = new AnchorsPane();
			WidgetsPublisher.publish(anchorsPane);
			aFactory.readGeneralProps(anElement, anchorsPane);
			return anchorsPane;
		case "bp":
		case "BorderPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
			BorderPane borderPane = new BorderPane(hgap, vgap);
			WidgetsPublisher.publish(borderPane);
			aFactory.readGeneralProps(anElement, borderPane);
			return borderPane;
		}
		case "bx":
		case "BoxPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "on", "orientation", Orientation.HORIZONTAL);
			BoxPane boxPane = new BoxPane(orientation, hgap, vgap);
			WidgetsPublisher.publish(boxPane);
			aFactory.readGeneralProps(anElement, boxPane);
			return boxPane;
		}
		case "cp":
		case "CardPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
			CardPane cardPane = new CardPane(hgap, vgap);
			WidgetsPublisher.publish(cardPane);
			aFactory.readGeneralProps(anElement, cardPane);
			return cardPane;
		}
		case "fp":
		case "FlowPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
			FlowPane flowPane = new FlowPane(hgap, vgap);
			WidgetsPublisher.publish(flowPane);
			aFactory.readGeneralProps(anElement, flowPane);
			return flowPane;
		}
		case "gp":
		case "GridPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
			int rows = Utils.getIntegerAttribute(anElement, "rows", "rows", 0);
			int columns = Utils.getIntegerAttribute(anElement, "columns", "columns", 0);
			GridPane gridPane = new GridPane(rows, columns, hgap, vgap);
			WidgetsPublisher.publish(gridPane);
			aFactory.readGeneralProps(anElement, gridPane);
			return gridPane;
		}
		// predefined layout containers
		case "sp":
		case "ScrollPane":
			ScrollPane scroll = new ScrollPane();
			WidgetsPublisher.publish(scroll);
			aFactory.readGeneralProps(anElement, scroll);
			boolean wheelScrollingEnabled = Utils.getBooleanAttribute(anElement, "wse", "wheelScrollingEnabled", Boolean.TRUE);
			int horizontalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "hsp", "horizontalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			int verticalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "vsp", "verticalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
			scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
			return scroll;
		case "spl":
		case "SplitPane":
			final SplitPane split = new SplitPane();
			WidgetsPublisher.publish(split);
			aFactory.readGeneralProps(anElement, split);
			boolean oneTouchExpandable = Utils.getBooleanAttribute(anElement, "ote", "oneTouchExpandable", true);
			int dividerLocation = Utils.getIntegerAttribute(anElement, "dvl", "dividerLocation", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "on", "orientation", Orientation.VERTICAL);
			split.setDividerLocation(dividerLocation);
			split.setOrientation(orientation);
			split.setOneTouchExpandable(oneTouchExpandable);
			if (Utils.hasAttribute(anElement, "lc", "leftComponent")) {
				final String leftComponentName = Utils.getAttribute(anElement, "lc", "leftComponent", null);
				aFactory.addResolver(new Runnable() {
					@Override
					public void run() {
						UIObject leftComponent = aFactory.getWidgets().get(leftComponentName);
						split.setFirstWidget((Widget) leftComponent);
					}
				});
			}
			if (Utils.hasAttribute(anElement, "rc", "rightComponent")) {
				final String rightComponentName = Utils.getAttribute(anElement, "rc", "rightComponent", null);
				aFactory.addResolver(new Runnable() {
					@Override
					public void run() {
						UIObject rightComponent = aFactory.getWidgets().get(rightComponentName);
						split.setSecondWidget((Widget) rightComponent);
					}
				});
			}
			return split;
		case "tp":
		case "TabbedPane":
			TabbedPane tabs = new TabbedPane();
			WidgetsPublisher.publish(tabs);
			aFactory.readGeneralProps(anElement, tabs);
			/*
			 * int tabPlacement = Utils.getIntegerAttribute(anElement, "tp",
			 * "tabPlacement", TabbedPane.TOP);
			 * tabs.setTabPlacement(tabPlacement);
			 */
			return tabs;
		case "tl":
		case "ToolBar":
			ToolBar toolbar = new ToolBar();
			WidgetsPublisher.publish(toolbar);
			aFactory.readGeneralProps(anElement, toolbar);
			return toolbar;
		default:
			return null;
		}
	}
	
}

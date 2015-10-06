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
		case "Label":
			final PlatypusLabel label = new PlatypusLabel();
			WidgetsPublisher.publish(label);
			aFactory.readGeneralProps(anElement, label);
			aFactory.readImageParagraph(anElement, label);
			return label;
		case "Button":
			PlatypusButton button = new PlatypusButton();
			WidgetsPublisher.publish(button);
			aFactory.readGeneralProps(anElement, button);
			aFactory.readImageParagraph(anElement, button);
			return button;
		case "DropDownButton":
			final PlatypusSplitButton dropDownButton = new PlatypusSplitButton();
			WidgetsPublisher.publish(dropDownButton);
			aFactory.readGeneralProps(anElement, dropDownButton);
			aFactory.readImageParagraph(anElement, dropDownButton);
			if (anElement.hasAttribute("dropDownMenu")) {
				final String dropDownMenuName = anElement.getAttribute("dropDownMenu");
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
		case "ButtonGroup":
			ButtonGroup buttonGroup = new ButtonGroup();
			WidgetsPublisher.publish(buttonGroup);
			if (anElement.hasAttribute("name")) {
				buttonGroup.setJsName(anElement.getAttribute("name"));
			}
			return buttonGroup;
		case "CheckBox":
			PlatypusCheckBox checkBox = new PlatypusCheckBox();
			WidgetsPublisher.publish(checkBox);
			aFactory.readGeneralProps(anElement, checkBox);
			aFactory.readImageParagraph(anElement, checkBox);
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
			WidgetsPublisher.publish(textArea);
			aFactory.readGeneralProps(anElement, textArea);
			if (anElement.hasAttribute("text")) {
				textArea.setText(anElement.getAttribute("text"));
			}
			return textArea;
		case "HtmlArea":
			PlatypusHtmlEditor htmlArea = new PlatypusHtmlEditor();
			WidgetsPublisher.publish(htmlArea);
			aFactory.readGeneralProps(anElement, htmlArea);
			if (anElement.hasAttribute("text")) {
				String text = anElement.getAttribute("text");
				htmlArea.setValue(text); 
			}
			return htmlArea;
		case "FormattedField": {
			PlatypusFormattedTextField formattedField = new PlatypusFormattedTextField();
			WidgetsPublisher.publish(formattedField);
			aFactory.readGeneralProps(anElement, formattedField);
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
			WidgetsPublisher.publish(passwordField);
			aFactory.readGeneralProps(anElement, passwordField);
			if (anElement.hasAttribute("text")) {
				passwordField.setText(anElement.getAttribute("text"));
			}
			return passwordField;
		case "ProgressBar": {
			PlatypusProgressBar progressBar = new PlatypusProgressBar();
			WidgetsPublisher.publish(progressBar);
			aFactory.readGeneralProps(anElement, progressBar);
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
			WidgetsPublisher.publish(radio);
			aFactory.readGeneralProps(anElement, radio);
			aFactory.readImageParagraph(anElement, radio);
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
			WidgetsPublisher.publish(slider);
			aFactory.readGeneralProps(anElement, slider);
			int minimum = Utils.getIntegerAttribute(anElement, "minimum", 0);
			int value = Utils.getIntegerAttribute(anElement, "value", 0);
			int maximum = Utils.getIntegerAttribute(anElement, "maximum", 100);
			slider.setMinValue(minimum);
			slider.setMaxValue(maximum);
			slider.setValue((double) value);
			return slider;
		case "TextField":
			PlatypusTextField textField = new PlatypusTextField();
			WidgetsPublisher.publish(textField);
			aFactory.readGeneralProps(anElement, textField);
			if (anElement.hasAttribute("text")) {
				textField.setText(anElement.getAttribute("text"));
			}
			return textField;
		case "ToggleButton":
			PlatypusToggleButton toggle = new PlatypusToggleButton();
			WidgetsPublisher.publish(toggle);
			aFactory.readGeneralProps(anElement, toggle);
			aFactory.readImageParagraph(anElement, toggle);
			if (anElement.hasAttribute("selected")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "selected", Boolean.FALSE);
				toggle.setValue(selected);
			}
			return toggle;
		case "DesktopPane":
			DesktopPane desktop = new DesktopPane();
			WidgetsPublisher.publish(desktop);
			aFactory.readGeneralProps(anElement, desktop);
			return desktop;
		case "AnchorsPane":
			AnchorsPane anchorsPane = new AnchorsPane();
			WidgetsPublisher.publish(anchorsPane);
			aFactory.readGeneralProps(anElement, anchorsPane);
			return anchorsPane;
		case "BorderPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			BorderPane borderPane = new BorderPane(hgap, vgap);
			WidgetsPublisher.publish(borderPane);
			aFactory.readGeneralProps(anElement, borderPane);
			return borderPane;
		}
		case "BoxPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "orientation", Orientation.HORIZONTAL);
			BoxPane boxPane = new BoxPane(orientation, hgap, vgap);
			WidgetsPublisher.publish(boxPane);
			aFactory.readGeneralProps(anElement, boxPane);
			return boxPane;
		}
		case "CardPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			CardPane cardPane = new CardPane(hgap, vgap);
			WidgetsPublisher.publish(cardPane);
			aFactory.readGeneralProps(anElement, cardPane);
			return cardPane;
		}
		case "FlowPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			FlowPane flowPane = new FlowPane(hgap, vgap);
			WidgetsPublisher.publish(flowPane);
			aFactory.readGeneralProps(anElement, flowPane);
			return flowPane;
		}
		case "GridPane": {
			int hgap = Utils.getIntegerAttribute(anElement, "hgap", 0);
			int vgap = Utils.getIntegerAttribute(anElement, "vgap", 0);
			int rows = Utils.getIntegerAttribute(anElement, "rows", 0);
			int columns = Utils.getIntegerAttribute(anElement, "columns", 0);
			GridPane gridPane = new GridPane(rows, columns, hgap, vgap);
			WidgetsPublisher.publish(gridPane);
			aFactory.readGeneralProps(anElement, gridPane);
			return gridPane;
		}
		// predefined layout containers
		case "ScrollPane":
			ScrollPane scroll = new ScrollPane();
			WidgetsPublisher.publish(scroll);
			aFactory.readGeneralProps(anElement, scroll);
			boolean wheelScrollingEnabled = Utils.getBooleanAttribute(anElement, "wheelScrollingEnabled", Boolean.TRUE);
			int horizontalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "horizontalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			int verticalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "verticalScrollBarPolicy", ScrollPane.SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
			scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
			return scroll;
		case "SplitPane":
			final SplitPane split = new SplitPane();
			WidgetsPublisher.publish(split);
			aFactory.readGeneralProps(anElement, split);
			boolean oneTouchExpandable = Utils.getBooleanAttribute(anElement, "oneTouchExpandable", true);
			int dividerLocation = Utils.getIntegerAttribute(anElement, "dividerLocation", 0);
			int orientation = Utils.getIntegerAttribute(anElement, "orientation", Orientation.VERTICAL);
			split.setDividerLocation(dividerLocation);
			split.setOrientation(orientation);
			split.setOneTouchExpandable(oneTouchExpandable);
			if (anElement.hasAttribute("leftComponent")) {
				final String leftComponentName = anElement.getAttribute("leftComponent");
				aFactory.addResolver(new Runnable() {
					@Override
					public void run() {
						UIObject leftComponent = aFactory.getWidgets().get(leftComponentName);
						split.setFirstWidget((Widget) leftComponent);
					}
				});
			}
			if (anElement.hasAttribute("rightComponent")) {
				final String rightComponentName = anElement.getAttribute("rightComponent");
				aFactory.addResolver(new Runnable() {
					@Override
					public void run() {
						UIObject rightComponent = aFactory.getWidgets().get(rightComponentName);
						split.setSecondWidget((Widget) rightComponent);
					}
				});
			}
			return split;
		case "TabbedPane":
			TabbedPane tabs = new TabbedPane();
			WidgetsPublisher.publish(tabs);
			aFactory.readGeneralProps(anElement, tabs);
			/*
			 * int tabPlacement = Utils.getIntegerAttribute(anElement,
			 * "tabPlacement", TabbedPane.TOP);
			 * tabs.setTabPlacement(tabPlacement);
			 */
			return tabs;
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

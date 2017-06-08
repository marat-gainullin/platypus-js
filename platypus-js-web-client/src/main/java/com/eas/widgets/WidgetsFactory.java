package com.eas.widgets;

import com.eas.widgets.boxes.RichTextArea;
import com.eas.widgets.boxes.TextArea;
import com.eas.widgets.boxes.TextField;
import com.eas.widgets.boxes.CheckBox;
import com.eas.widgets.containers.Desktop;
import com.eas.widgets.containers.Anchors;
import com.eas.core.Utils;
import com.eas.menu.PopupMenu;
import com.eas.ui.ButtonGroup;
import com.eas.ui.Orientation;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.widgets.boxes.ImageLabel;
import com.eas.widgets.boxes.ImageButton;
import com.eas.widgets.boxes.DropDownButton;
import com.eas.widgets.containers.Tabs;
import com.eas.widgets.containers.Toolbar;
import com.eas.ui.Widget;
import com.eas.widgets.boxes.FormattedField;
import com.eas.widgets.boxes.ImageToggleButton;
import com.eas.widgets.boxes.PasswordField;
import com.eas.widgets.boxes.RadioButton;
import com.eas.widgets.boxes.Slider;
import com.eas.widgets.boxes.ProgressBar;
import com.eas.widgets.containers.Flow;
import com.eas.widgets.containers.Split;
import com.eas.widgets.containers.Scroll;
import com.eas.widgets.containers.Cards;
import com.eas.widgets.containers.Cells;
import com.eas.widgets.containers.Box;
import com.eas.widgets.containers.Borders;
import com.google.gwt.xml.client.Element;

public class WidgetsFactory implements UiWidgetReader {

    public Widget readWidget(Element anElement, final UiReader aFactory) throws Exception {
        String type = anElement.getTagName();
        switch (type) {
            // widgets
            case "lb":
            case "Label":
                final ImageLabel label = new ImageLabel();
                WidgetsPublisher.publish(label);
                aFactory.readGeneralProps(anElement, label);
                aFactory.readImageParagraph(anElement, label);
                return label;
            case "bt":
            case "Button":
                ImageButton button = new ImageButton();
                WidgetsPublisher.publish(button);
                aFactory.readGeneralProps(anElement, button);
                aFactory.readImageParagraph(anElement, button);
                return button;
            case "ddb":
            case "DropDownButton":
                final DropDownButton dropDownButton = new DropDownButton();
                WidgetsPublisher.publish(dropDownButton);
                aFactory.readGeneralProps(anElement, dropDownButton);
                aFactory.readImageParagraph(anElement, dropDownButton);
                if (Utils.hasAttribute(anElement, "ddm", "dropDownMenu")) {
                    final String dropDownMenuName = Utils.getAttribute(anElement, "ddm", "dropDownMenu", null);
                    aFactory.addResolver(new Runnable() {
                        public void run() {
                            if (aFactory.getWidgets().containsKey(dropDownMenuName)) {
                                Widget compMenu = aFactory.getWidgets().get(dropDownMenuName);
                                if (compMenu instanceof PopupMenu) {
                                    dropDownButton.setMenu((PopupMenu) compMenu);
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
                    aFactory.getButtonGroups().put(buttonGroup.getJsName(), buttonGroup);
                }
                return null;
            case "cb":
            case "CheckBox":
                CheckBox checkBox = new CheckBox();
                WidgetsPublisher.publish(checkBox);
                aFactory.readGeneralProps(anElement, checkBox);
                aFactory.readImageParagraph(anElement, checkBox);
                if (Utils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    checkBox.setJsValue(selected);
                }
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    checkBox.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return checkBox;
            case "ta":
            case "TextArea":
                TextArea textArea = new TextArea();
                WidgetsPublisher.publish(textArea);
                aFactory.readGeneralProps(anElement, textArea);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    textArea.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return textArea;
            case "ha":
            case "HtmlArea":
                RichTextArea htmlArea = new RichTextArea();
                WidgetsPublisher.publish(htmlArea);
                aFactory.readGeneralProps(anElement, htmlArea);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    String text = Utils.getAttribute(anElement, "tx", "text", null);
                    htmlArea.setJsValue(text);
                }
                return htmlArea;
            case "ff":
            case "FormattedField": {
                FormattedField formattedField = new FormattedField();
                WidgetsPublisher.publish(formattedField);
                aFactory.readGeneralProps(anElement, formattedField);
                String format = Utils.getAttribute(anElement, "fr", "format", null);
                int valueType = Utils.getIntegerAttribute(anElement, "vt", "valueType", FormattedField.REGEXP);
                formattedField.setValueType(valueType);
                formattedField.setPattern(format);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    formattedField.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return formattedField;
            }
            case "pf":
            case "PasswordField":
                PasswordField passwordField = new PasswordField();
                WidgetsPublisher.publish(passwordField);
                aFactory.readGeneralProps(anElement, passwordField);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    passwordField.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return passwordField;
            case "pb":
            case "ProgressBar": {
                ProgressBar progressBar = new ProgressBar();
                WidgetsPublisher.publish(progressBar);
                aFactory.readGeneralProps(anElement, progressBar);
                int minimum = Utils.getIntegerAttribute(anElement, "mm", "minimum", 0);
                int value = Utils.getIntegerAttribute(anElement, "vl", "value", 0);
                int maximum = Utils.getIntegerAttribute(anElement, "mx", "maximum", 100);
                progressBar.setMinProgress(minimum);
                progressBar.setMaxProgress(maximum);
                progressBar.setJsValue((double) value);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    progressBar.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return progressBar;
            }
            case "rb":
            case "RadioButton":
                RadioButton radio = new RadioButton();
                WidgetsPublisher.publish(radio);
                aFactory.readGeneralProps(anElement, radio);
                aFactory.readImageParagraph(anElement, radio);
                if (Utils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    radio.setJsValue(selected);
                }
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    radio.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return radio;
            case "s":
            case "Slider":
                Slider slider = new Slider();
                WidgetsPublisher.publish(slider);
                aFactory.readGeneralProps(anElement, slider);
                int minimum = Utils.getIntegerAttribute(anElement, "mm", "minimum", 0);
                int value = Utils.getIntegerAttribute(anElement, "vl", "value", 0);
                int maximum = Utils.getIntegerAttribute(anElement, "mx", "maximum", 100);
                slider.setMinValue(minimum);
                slider.setMaxValue(maximum);
                slider.setJsValue((double) value);
                return slider;
            case "tf":
            case "TextField":
                TextField textField = new TextField();
                WidgetsPublisher.publish(textField);
                aFactory.readGeneralProps(anElement, textField);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    textField.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return textField;
            case "tb":
            case "ToggleButton":
                ImageToggleButton toggle = new ImageToggleButton();
                WidgetsPublisher.publish(toggle);
                aFactory.readGeneralProps(anElement, toggle);
                aFactory.readImageParagraph(anElement, toggle);
                if (Utils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    toggle.setJsValue(selected);
                }
                return toggle;
            case "dp":
            case "DesktopPane":
                Desktop desktop = new Desktop();
                WidgetsPublisher.publish(desktop);
                aFactory.readGeneralProps(anElement, desktop);
                return desktop;
            case "ap":
            case "AnchorsPane":
                Anchors anchorsPane = new Anchors();
                WidgetsPublisher.publish(anchorsPane);
                aFactory.readGeneralProps(anElement, anchorsPane);
                return anchorsPane;
            case "bp":
            case "BorderPane": {
                int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
                int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
                Borders borderPane = new Borders(hgap, vgap);
                WidgetsPublisher.publish(borderPane);
                aFactory.readGeneralProps(anElement, borderPane);
                return borderPane;
            }
            case "bx":
            case "BoxPane": {
                int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
                int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
                int orientation = Utils.getIntegerAttribute(anElement, "on", "orientation", Orientation.HORIZONTAL);
                Box boxPane = new Box(orientation, hgap, vgap);
                WidgetsPublisher.publish(boxPane);
                aFactory.readGeneralProps(anElement, boxPane);
                return boxPane;
            }
            case "cp":
            case "CardPane": {
                int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
                int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
                Cards cardPane = new Cards(hgap, vgap);
                WidgetsPublisher.publish(cardPane);
                aFactory.readGeneralProps(anElement, cardPane);
                return cardPane;
            }
            case "fp":
            case "FlowPane": {
                int hgap = Utils.getIntegerAttribute(anElement, "hgap", "hgap", 0);
                int vgap = Utils.getIntegerAttribute(anElement, "vgap", "vgap", 0);
                Flow flowPane = new Flow(hgap, vgap);
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
                Cells gridPane = new Cells(rows, columns, hgap, vgap);
                WidgetsPublisher.publish(gridPane);
                aFactory.readGeneralProps(anElement, gridPane);
                return gridPane;
            }
            // predefined layout containers
            case "sp":
            case "ScrollPane":
                Scroll scroll = new Scroll();
                WidgetsPublisher.publish(scroll);
                aFactory.readGeneralProps(anElement, scroll);
                boolean wheelScrollingEnabled = Utils.getBooleanAttribute(anElement, "wse", "wheelScrollingEnabled", Boolean.TRUE);
                int horizontalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "hsp", "horizontalScrollBarPolicy", Scroll.SCROLLBAR_AS_NEEDED);
                int verticalScrollBarPolicy = Utils.getIntegerAttribute(anElement, "vsp", "verticalScrollBarPolicy", Scroll.SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
                scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
                return scroll;
            case "spl":
            case "SplitPane":
                final Split split = new Split();
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
                            Widget leftComponent = aFactory.getWidgets().get(leftComponentName);
                            split.setFirstWidget((Widget) leftComponent);
                        }
                    });
                }
                if (Utils.hasAttribute(anElement, "rc", "rightComponent")) {
                    final String rightComponentName = Utils.getAttribute(anElement, "rc", "rightComponent", null);
                    aFactory.addResolver(new Runnable() {
                        @Override
                        public void run() {
                            Widget rightComponent = aFactory.getWidgets().get(rightComponentName);
                            split.setSecondWidget((Widget) rightComponent);
                        }
                    });
                }
                return split;
            case "tp":
            case "TabbedPane":
                Tabs tabs = new Tabs();
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
                Toolbar toolbar = new Toolbar();
                WidgetsPublisher.publish(toolbar);
                aFactory.readGeneralProps(anElement, toolbar);
                return toolbar;
            default:
                return null;
        }
    }

}

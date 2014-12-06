/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.eas.client.forms.components.Button;
import com.eas.client.forms.components.CheckBox;
import com.eas.client.forms.components.DesktopPane;
import com.eas.client.forms.components.DropDownButton;
import com.eas.client.forms.components.FormattedField;
import com.eas.client.forms.components.HtmlArea;
import com.eas.client.forms.components.Label;
import com.eas.client.forms.components.PasswordField;
import com.eas.client.forms.components.ProgressBar;
import com.eas.client.forms.components.RadioButton;
import com.eas.client.forms.components.Slider;
import com.eas.client.forms.components.TextArea;
import com.eas.client.forms.components.TextField;
import com.eas.client.forms.components.ToggleButton;
import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelCombo;
import com.eas.client.forms.components.model.ModelDate;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.ModelSpin;
import com.eas.client.forms.components.model.ModelTextArea;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.rt.ButtonGroupWrapper;
import com.eas.client.forms.components.rt.FormatsUtils;
import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.HasGroup;
import com.eas.client.forms.containers.AnchorsPane;
import com.eas.client.forms.containers.BorderPane;
import com.eas.client.forms.containers.BoxPane;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.CardPane;
import com.eas.client.forms.containers.FlowPane;
import com.eas.client.forms.containers.GridPane;
import com.eas.client.forms.containers.ScrollPane;
import com.eas.client.forms.containers.SplitPane;
import com.eas.client.forms.containers.TabbedPane;
import com.eas.client.forms.containers.ToolBar;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.Margin;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.client.forms.menu.CheckMenuItem;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.MenuItem;
import com.eas.client.forms.menu.MenuSeparator;
import com.eas.client.forms.menu.PopupMenu;
import com.eas.client.forms.menu.RadioMenuItem;
import com.eas.gui.ScriptColor;
import com.eas.xml.dom.XmlDomUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class FormFactory {

    protected Element element;
    protected JSObject model;
    protected Form form;
    protected boolean forceColumns;
    //
    protected List<Consumer<Map<String, JComponent>>> resolvers = new ArrayList<>();

    public FormFactory(Element anElement, JSObject aModel) {
        super();
        model = aModel;
    }

    public boolean isForceColumns() {
        return forceColumns;
    }

    public void setForceColumns(boolean aValue) {
        forceColumns = aValue;
    }

    public Form getForm() {
        return form;
    }

    public void parse() throws Exception {
        Element layoutTag = XmlDomUtils.getElementByTagName(element, "layout");
        assert layoutTag != null : "tag layout is required for panel containers.";

        form = new Form();
        form.setDefaultCloseOperation(XmlDomUtils.readIntegerAttribute(element, "defaultCloseOperation", JFrame.DISPOSE_ON_CLOSE));
        form.setIcon(resolveIcon(element.getAttribute("icon")));
        form.setTitle(element.getAttribute("title"));
        form.setResizable(XmlDomUtils.readBooleanAttribute(element, "resizable", Boolean.TRUE));
        form.setUndecorated(XmlDomUtils.readBooleanAttribute(element, "undecorated", Boolean.FALSE));
        form.setOpacity(XmlDomUtils.readFloatAttribute(element, "opacity", 1.0f));
        form.setAlwaysOnTop(XmlDomUtils.readBooleanAttribute(element, "alwaysOnTop", Boolean.FALSE));
        form.setLocationByPlatform(XmlDomUtils.readBooleanAttribute(element, "locationByPlatform", Boolean.TRUE));
        Dimension prefSize = readPrefSize(element);
        form.setDesignedViewSize(prefSize);
        List<Element> widgetsElements = XmlDomUtils.elementsByTagName(element, "widget");
        Map<String, JComponent> widgets = new HashMap<>();
        widgetsElements.stream().forEach((Element aElement) -> {
            JComponent widget = readWidget(aElement);
            String wName = widget.getName();
            assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
            widgets.put(wName, widget);
        });
        resolvers.stream().forEach((Consumer<Map<String, JComponent>> aResolver) -> {
            aResolver.accept(widgets);
        });
    }

    protected Dimension readPrefSize(Element anElement) throws NumberFormatException {
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
    }

    private ImageIcon resolveIcon(String aIconName) {
        if (aIconName != null) {
            try {
                return IconResources.load(aIconName, null, null);
            } catch (Exception ex) {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private JComponent readWidget(Element anElement) {
        String type = anElement.getAttribute("type");
        assert type != null && !type.isEmpty() : "type attribute is required for widgets to be read from a file";
        switch (type) {
            // widgets
            case "LabelDesignInfo":
                Label label = new Label();
                readGeneralProps(anElement, label);
                if (anElement.hasAttribute("icon")) {
                    label.setIcon(resolveIcon(anElement.getAttribute("icon")));
                }
                if (anElement.hasAttribute("text")) {
                    label.setText(anElement.getAttribute("text"));
                }
                label.setHorizontalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "horizontalAlignment", Label.LEADING));
                label.setVerticalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "verticalAlignment", Label.CENTER));
                label.setIconTextGap(XmlDomUtils.readIntegerAttribute(anElement, "iconTextGap", 4));
                label.setHorizontalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "horizontalTextPosition", Label.TRAILING));
                label.setVerticalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "verticalTextPosition", Label.CENTER));
                if (anElement.hasAttribute("labelFor")) {
                    String labelForName = anElement.getAttribute("labelFor");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        if (aWidgets.containsKey(labelForName)) {
                            label.setLabelFor(aWidgets.get(labelForName));
                        }
                    });
                }
                return label;
            case "ButtonDesignInfo":
                Button button = new Button();
                readGeneralProps(anElement, button);
                readButton(anElement, button);
                return button;
            case "DropDownButtonDesignInfo":
                DropDownButton dropDownButton = new DropDownButton();
                readGeneralProps(anElement, dropDownButton);
                readButton(anElement, dropDownButton);
                if (anElement.hasAttribute("dropDownMenu")) {
                    String dropDownMenuName = anElement.getAttribute("dropDownMenu");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        if (aWidgets.containsKey(dropDownMenuName)) {
                            JComponent compMenu = aWidgets.get(dropDownMenuName);
                            if (compMenu instanceof JPopupMenu) {
                                dropDownButton.setDropDownMenu((JPopupMenu) compMenu);
                            }
                        }
                    });
                }
                return dropDownButton;
            case "ButtonGroupDesignInfo":
                ButtonGroup buttonGroup = new ButtonGroup();
                return buttonGroup;
            case "CheckDesignInfo":
                CheckBox checkBox = new CheckBox();
                readGeneralProps(anElement, checkBox);
                readButton(anElement, checkBox);
                if (anElement.hasAttribute("selected")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "selected", Boolean.FALSE);
                    checkBox.setSelected(selected);
                }
                if (anElement.hasAttribute("text")) {
                    checkBox.setText(anElement.getAttribute("text"));
                }
                return checkBox;
            case "TextPaneDesignInfo":
                TextArea textArea = new TextArea();
                readGeneralProps(anElement, textArea);
                if (anElement.hasAttribute("text")) {
                    textArea.setText(anElement.getAttribute("text"));
                }
                return textArea;
            case "EditorPaneDesignInfo":
                HtmlArea htmlArea = new HtmlArea();
                readGeneralProps(anElement, htmlArea);
                if (anElement.hasAttribute("text")) {
                    htmlArea.setText(anElement.getAttribute("text"));
                }
                return htmlArea;
            case "FormattedFieldDesignInfo": {
                FormattedField formattedField = new FormattedField();
                readGeneralProps(anElement, formattedField);
                String format = anElement.getAttribute("format");
                int valueType = XmlDomUtils.readIntegerAttribute(anElement, "valueType", FormatsUtils.MASK);
                formattedField.setValueType(valueType);
                formattedField.setFormat(format);
                if (anElement.hasAttribute("text")) {
                    formattedField.setText(anElement.getAttribute("text"));
                }
                return formattedField;
            }
            case "PasswordFieldDesignInfo":
                PasswordField passwordField = new PasswordField();
                readGeneralProps(anElement, passwordField);
                if (anElement.hasAttribute("text")) {
                    passwordField.setText(anElement.getAttribute("text"));
                }
                return passwordField;
            case "ProgressBarDesignInfo": {
                ProgressBar progressBar = new ProgressBar();
                readGeneralProps(anElement, progressBar);
                int minimum = XmlDomUtils.readIntegerAttribute(anElement, "minimum", 0);
                int value = XmlDomUtils.readIntegerAttribute(anElement, "value", 0);
                int maximum = XmlDomUtils.readIntegerAttribute(anElement, "maximum", 100);
                progressBar.setMinimum(minimum);
                progressBar.setMaximum(maximum);
                progressBar.setValue(value);
                if (anElement.hasAttribute("string")) {
                    progressBar.setText(anElement.getAttribute("string"));
                }
                return progressBar;
            }
            case "RadioDesignInfo":
                RadioButton radio = new RadioButton();
                readGeneralProps(anElement, radio);
                readButton(anElement, radio);
                if (anElement.hasAttribute("selected")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "selected", Boolean.FALSE);
                    radio.setSelected(selected);
                }
                if (anElement.hasAttribute("text")) {
                    radio.setText(anElement.getAttribute("text"));
                }
                return radio;
            case "SliderDesignInfo":
                Slider slider = new Slider();
                readGeneralProps(anElement, slider);
                int minimum = XmlDomUtils.readIntegerAttribute(anElement, "minimum", 0);
                int value = XmlDomUtils.readIntegerAttribute(anElement, "value", 0);
                int maximum = XmlDomUtils.readIntegerAttribute(anElement, "maximum", 100);
                slider.setMinimum(minimum);
                slider.setMaximum(maximum);
                slider.setValue(value);
                return slider;
            case "TextFieldDesignInfo":
                TextField textField = new TextField();
                readGeneralProps(anElement, textField);
                if (anElement.hasAttribute("text")) {
                    textField.setText(anElement.getAttribute("text"));
                }
                return textField;
            case "ToggleButtonDesignInfo":
                ToggleButton toggle = new ToggleButton();
                readGeneralProps(anElement, toggle);
                readButton(anElement, toggle);
                return toggle;
            case "DesktopDesignInfo":
                DesktopPane desktop = new DesktopPane();
                readGeneralProps(anElement, desktop);
                return desktop;
            // model widgets
            case "DbCheckDesignInfo":
                ModelCheckBox modelCheckBox = new ModelCheckBox();
                readGeneralProps(anElement, modelCheckBox);
                if (anElement.hasAttribute("text")) {
                    modelCheckBox.setText(anElement.getAttribute("text"));
                }
                return modelCheckBox;
            case "DbComboDesignInfo":
                ModelCombo modelCombo = new ModelCombo();
                readGeneralProps(anElement, modelCombo);
                boolean list = XmlDomUtils.readBooleanAttribute(anElement, "list", Boolean.TRUE);
                if (anElement.hasAttribute("valueField")) {
                    String valueField = anElement.getAttribute("valueField");
                }
                if (anElement.hasAttribute("displayField")) {
                    String displayField = anElement.getAttribute("displayField");
                }
                return modelCombo;
            case "DbDateDesignInfo":
                ModelDate modelDate = new ModelDate();
                readGeneralProps(anElement, modelDate);
                if (anElement.hasAttribute("dateFormat")) {
                    String dateFormat = anElement.getAttribute("dateFormat");
                    try {
                        modelDate.setDateFormat(dateFormat);
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return modelDate;
            case "DbLabelDesignInfo":
                ModelFormattedField modelFormattedField = new ModelFormattedField();
                readGeneralProps(anElement, modelFormattedField);
                try {
                    String format = anElement.getAttribute("format");
                    int valueType = XmlDomUtils.readIntegerAttribute(anElement, "valueType", FormatsUtils.MASK);
                    modelFormattedField.setValueType(valueType);
                    modelFormattedField.setFormat(format);
                    if (anElement.hasAttribute("text")) {
                        modelFormattedField.setText(anElement.getAttribute("text"));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                return modelFormattedField;
            case "DbSpinDesignInfo":
                ModelSpin modelSpin = new ModelSpin();
                readGeneralProps(anElement, modelSpin);
                double min = XmlDomUtils.readDoubleAttribute(anElement, "min", 0.0d);
                double step = XmlDomUtils.readDoubleAttribute(anElement, "step", 0.0d);
                double max = XmlDomUtils.readDoubleAttribute(anElement, "max", 100.0d);
                try {
                    modelSpin.setMin(min);
                    modelSpin.setMax(max);
                    modelSpin.setStep(step);
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                return modelSpin;
            case "DbTextDesignInfo":
                ModelTextArea textarea = new ModelTextArea();
                readGeneralProps(anElement, textarea);
                if (anElement.hasAttribute("text")) {
                    try {
                        textarea.setText(anElement.getAttribute("text"));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return textarea;
            case "DbGridDesignInfo":
                ModelGrid grid = new ModelGrid();
                readGeneralProps(anElement, grid);
                int fixedColumns = XmlDomUtils.readIntegerAttribute(anElement, "fixedColumns", 0);
                int fixedRows = XmlDomUtils.readIntegerAttribute(anElement, "fixedRows", 0);
                boolean insertable = XmlDomUtils.readBooleanAttribute(anElement, "insertable", Boolean.TRUE);
                boolean deletable = XmlDomUtils.readBooleanAttribute(anElement, "deletable", Boolean.TRUE);
                boolean editable = XmlDomUtils.readBooleanAttribute(anElement, "editable", Boolean.TRUE);
                boolean showHorizontalLines = XmlDomUtils.readBooleanAttribute(anElement, "showHorizontalLines", Boolean.TRUE);
                boolean showVerticalLines = XmlDomUtils.readBooleanAttribute(anElement, "showVerticalLines", Boolean.TRUE);
                boolean showOddRowsInOtherColor = XmlDomUtils.readBooleanAttribute(anElement, "showOddRowsInOtherColor", Boolean.TRUE);
                int rowsHeight = XmlDomUtils.readIntegerAttribute(anElement, "rowsHeight", 20);
                grid.setRowsHeight(rowsHeight);
                grid.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
                grid.setShowVerticalLines(showVerticalLines);
                grid.setShowHorizontalLines(showHorizontalLines);
                grid.setEditable(editable);
                grid.setDeletable(deletable);
                grid.setInsertable(insertable);
                grid.setFrozenColumns(fixedColumns);
                grid.setFrozenRows(fixedRows);
                if (anElement.hasAttribute("oddRowsColor")) {
                    String oddRowsColorDesc = anElement.getAttribute("oddRowsColor");
                    grid.setOddRowsColor(new ScriptColor(oddRowsColorDesc));
                }
                if (anElement.hasAttribute("gridColor")) {
                    String gridColorDesc = anElement.getAttribute("gridColor");
                    grid.setGridColor(new ScriptColor(gridColorDesc));
                }
                if (anElement.hasAttribute("parentField")) {
                    String parentFieldPath = anElement.getAttribute("parentField");
                    grid.setParentField(parentFieldPath);
                }
                if (anElement.hasAttribute("childrenField")) {
                    String childrenFieldPath = anElement.getAttribute("childrenField");
                    grid.setChildrenField(childrenFieldPath);
                }
                List<GridColumnsGroup> columnsRoots = readColumns(grid, XmlDomUtils.elementsByTagName(anElement, "column"));
                grid.setHeader(columnsRoots);
                return grid;
            // containers   
            // layouted containers
            case "PanelDesignInfo":
                Element layoutTag = XmlDomUtils.getElementByTagName(anElement, "layout");
                assert layoutTag != null : "tag layout is required for panel containers.";
                JComponent container = readLayoutedContainer(layoutTag);
                readGeneralProps(anElement, container);
                return container;
            // predefined layout containers
            case "ScrollDesignInfo":
                ScrollPane scroll = new ScrollPane();
                readGeneralProps(anElement, scroll);
                boolean wheelScrollingEnabled = XmlDomUtils.readBooleanAttribute(anElement, "wheelScrollingEnabled", Boolean.TRUE);
                int horizontalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "horizontalScrollBarPolicy", ScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                int verticalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "verticalScrollBarPolicy", ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
                scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
                return scroll;
            case "SplitDesignInfo":
                SplitPane split = new SplitPane();
                readGeneralProps(anElement, split);
                boolean oneTouchExpandable = XmlDomUtils.readBooleanAttribute(anElement, "oneTouchExpandable", true);
                int dividerLocation = XmlDomUtils.readIntegerAttribute(anElement, "dividerLocation", 0);
                int dividerSize = XmlDomUtils.readIntegerAttribute(anElement, "dividerSize", 5);
                int orientation = XmlDomUtils.readIntegerAttribute(anElement, "orientation", Orientation.VERTICAL);
                split.setDividerLocation(dividerLocation);
                split.setDividerSize(dividerSize);
                split.setOrientation(orientation);
                split.setOneTouchExpandable(oneTouchExpandable);
                if (anElement.hasAttribute("leftComponent")) {
                    String leftComponentName = anElement.getAttribute("leftComponent");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        JComponent leftComponent = aWidgets.get(leftComponentName);
                        split.setLeftComponent(leftComponent);
                    });
                }
                if (anElement.hasAttribute("rightComponent")) {
                    String rightComponentName = anElement.getAttribute("rightComponent");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        JComponent rightComponent = aWidgets.get(rightComponentName);
                        split.setRightComponent(rightComponent);
                    });
                }
                return split;
            case "TabsDesignInfo":
                TabbedPane tabs = new TabbedPane();
                readGeneralProps(anElement, tabs);
                int tabPlacement = XmlDomUtils.readIntegerAttribute(anElement, "tabPlacement", TabbedPane.TOP);
                tabs.setTabPlacement(tabPlacement);
                return tabs;
            case "ToolbarDesignInfo":
                ToolBar toolbar = new ToolBar();
                readGeneralProps(anElement, toolbar);
                return toolbar;
            // menus
            case "MenuCheckItemDesignInfo":
                CheckMenuItem checkMenuItem = new CheckMenuItem();
                readGeneralProps(anElement, checkMenuItem);
                readButton(anElement, checkMenuItem);
                if (anElement.hasAttribute("selected")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "selected", Boolean.FALSE);
                    checkMenuItem.setSelected(selected);
                }
                if (anElement.hasAttribute("text")) {
                    checkMenuItem.setText(anElement.getAttribute("text"));
                }
                return checkMenuItem;
            case "MenuDesignInfo":
                Menu menu = new Menu();
                readGeneralProps(anElement, menu);
                return menu;
            case "MenuItemDesignInfo":
                MenuItem menuitem = new MenuItem();
                readGeneralProps(anElement, menuitem);
                readButton(anElement, menuitem);
                if (anElement.hasAttribute("text")) {
                    menuitem.setText(anElement.getAttribute("text"));
                }
                return menuitem;
            case "MenuRadioItemDesignInfo":
                RadioMenuItem radioMenuItem = new RadioMenuItem();
                readGeneralProps(anElement, radioMenuItem);
                readButton(anElement, radioMenuItem);
                if (anElement.hasAttribute("selected")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "selected", Boolean.FALSE);
                    radioMenuItem.setSelected(selected);
                }
                if (anElement.hasAttribute("text")) {
                    radioMenuItem.setText(anElement.getAttribute("text"));
                }
                return radioMenuItem;
            case "MenuSeparatorDesignInfo":
                MenuSeparator menuSeparator = new MenuSeparator();
                readGeneralProps(anElement, menuSeparator);
                return menuSeparator;
            case "MenubarDesignInfo":
                MenuBar menuBar = new MenuBar();
                readGeneralProps(anElement, menuBar);
                return menuBar;
            case "PopupDesignInfo":
                PopupMenu popupMenu = new PopupMenu();
                readGeneralProps(anElement, popupMenu);
                return popupMenu;
            default:
                return null;
        }
    }

    protected void readButton(Element anElement, AbstractButton button) {
        if (anElement.hasAttribute("icon")) {
            button.setIcon(resolveIcon(anElement.getAttribute("icon")));
        }
        if (anElement.hasAttribute("text")) {
            button.setText(anElement.getAttribute("text"));
        }
        button.setHorizontalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "horizontalAlignment", Button.LEADING));
        button.setVerticalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "verticalAlignment", Button.CENTER));
        button.setIconTextGap(XmlDomUtils.readIntegerAttribute(anElement, "iconTextGap", 4));
        button.setHorizontalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "horizontalTextPosition", Button.TRAILING));
        button.setVerticalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "verticalTextPosition", Button.CENTER));
    }

    private JComponent readLayoutedContainer(Element aLayoutElement) {
        String type = aLayoutElement.getAttribute("type");
        assert type != null && !type.isEmpty() : "type attribute is required for layouts to be read from a file";
        int hgap = XmlDomUtils.readIntegerAttribute(aLayoutElement, "hgap", 0);
        int vgap = XmlDomUtils.readIntegerAttribute(aLayoutElement, "vgap", 0);
        switch (type) {
            case "BorderLayoutDesignInfo": {
                return new BorderPane(hgap, vgap);
            }
            case "BoxLayoutDesignInfo": {
                int axis = XmlDomUtils.readIntegerAttribute(aLayoutElement, "axis", BoxLayout.LINE_AXIS);
                return new BoxPane(axis, hgap, vgap);
            }
            case "CardLayoutDesignInfo": {
                return new CardPane(hgap, vgap);
            }
            case "FlowLayoutDesignInfo": {
                int alignment = XmlDomUtils.readIntegerAttribute(aLayoutElement, "alignment", 0);
                return new FlowPane(hgap, vgap);
            }
            case "GridLayoutDesignInfo": {
                int rows = XmlDomUtils.readIntegerAttribute(aLayoutElement, "rows", 0);
                int columns = XmlDomUtils.readIntegerAttribute(aLayoutElement, "columns", 0);
                return new GridPane(rows, columns, hgap, vgap);
            }
            case "AbsoluteLayoutDesignInfo":
            case "MarginLayoutDesignInfo":
                return new AnchorsPane();
            default:
                return null;
        }
    }

    private void readGeneralProps(Element anElement, JComponent aTarget) {
        if (anElement.hasAttribute("name")) {
            aTarget.setName(anElement.getAttribute("name"));
        }
        if (anElement.hasAttribute("editable") && aTarget instanceof HasEditable) {
            ((HasEditable) aTarget).setEditable(XmlDomUtils.readBooleanAttribute(anElement, "editable", Boolean.TRUE));
        }
        if (anElement.hasAttribute("emptyText") && aTarget instanceof HasEmptyText) {
            ((HasEmptyText) aTarget).setEmptyText(anElement.getAttribute("emptyText"));
        }
        if (anElement.hasAttribute("field") && aTarget instanceof ModelWidget) {
            String fieldPath = anElement.getAttribute("field");
            try {
                ((ModelWidget) aTarget).setField(fieldPath);
            } catch (Exception ex) {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting field ({0}) to widget {1} exception occured: {2}", new Object[]{fieldPath, aTarget.getName(), ex.getMessage()});
            }
        }

        if (anElement.hasAttribute("background")) {
            ScriptColor background = new ScriptColor(anElement.getAttribute("background"));
            aTarget.setBackground(background);
        }
        if (anElement.hasAttribute("foreground")) {
            ScriptColor foreground = new ScriptColor(anElement.getAttribute("foreground"));
            aTarget.setForeground(foreground);
        }
        aTarget.setEnabled(XmlDomUtils.readBooleanAttribute(anElement, "enabled", Boolean.TRUE));
        aTarget.setFocusable(XmlDomUtils.readBooleanAttribute(anElement, "focusable", Boolean.TRUE));
        com.eas.gui.Font font = readFontTag(anElement, "font");
        if (font != null) {
            aTarget.setFont(font);
        } else {
            com.eas.gui.Font easfont = readFontTag(anElement, "easFont");
            if (easfont != null) {
                aTarget.setFont(easfont);
            }
        }
        if (anElement.hasAttribute("opaque")) {
            aTarget.setOpaque(XmlDomUtils.readBooleanAttribute(anElement, "opaque", Boolean.TRUE));
        }
        if (anElement.hasAttribute("toolTipText")) {
            aTarget.setToolTipText(anElement.getAttribute("toolTipText"));
        }
        int cursorId = XmlDomUtils.readIntegerAttribute(anElement, "cursor", com.eas.gui.Cursor.DEFAULT_CURSOR);
        aTarget.setCursor(new com.eas.gui.Cursor(cursorId));
        if (anElement.hasAttribute("visible")) {
            aTarget.setVisible(XmlDomUtils.readBooleanAttribute(anElement, "visible", Boolean.TRUE));
        }
        if (anElement.hasAttribute("nextFocusableComponent")) {
            String nextFocusableName = anElement.getAttribute("nextFocusableComponent");
            if (!nextFocusableName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent nextFocusable = aWidgets.get(nextFocusableName);
                    aTarget.setNextFocusableComponent(nextFocusable);
                });
            }
        }
        if (anElement.hasAttribute("componentPopupMenu")) {
            String popupName = anElement.getAttribute("componentPopupMenu");
            if (!popupName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent popup = aWidgets.get(popupName);
                    if (popup instanceof JPopupMenu) {
                        aTarget.setComponentPopupMenu((JPopupMenu) popup);
                    }
                });
            }
        }
        if (anElement.hasAttribute("buttonGroup") && aTarget instanceof HasGroup) {
            String buttonGroupName = anElement.getAttribute("buttonGroup");
            if (!buttonGroupName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent buttonGroup = aWidgets.get(buttonGroupName);
                    if (buttonGroup instanceof ButtonGroupWrapper) {
                        ButtonGroupWrapper bgw = (ButtonGroupWrapper) buttonGroup;
                        bgw.add(aTarget);
                    }
                });
            }
        }
        if (anElement.hasAttribute("parent")) {
            String parentName = anElement.getAttribute("parent");
            if (!parentName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent parent = aWidgets.get(parentName);
                    addToParent(anElement, aTarget, parent);
                });
            }
        }
    }

    private com.eas.gui.Font readFontTag(Element anElement, String aSubTagName) {
        Element easFontElement = XmlDomUtils.getElementByTagName(anElement, aSubTagName);
        if (easFontElement != null) {
            String name = easFontElement.getAttribute("name");
            if (name == null || name.isEmpty()) {
                name = Font.MONOSPACED;
            }
            int style = XmlDomUtils.readIntegerAttribute(easFontElement, "style", 0);
            int size = XmlDomUtils.readIntegerAttribute(easFontElement, "size", 12);
            return new com.eas.gui.Font(name, style, size);
        } else {
            return null;
        }
    }

    private void addToParent(Element anElement, JComponent aTarget, JComponent parent) {
        Element constraintsElement = XmlDomUtils.getElementByTagName(anElement, "constraints");
        if (parent instanceof MenuBar) {
            ((MenuBar) parent).add(aTarget);
        } else if (parent instanceof PopupMenu) {
            ((PopupMenu) parent).add(aTarget);
        } else if (parent instanceof Menu) {
            ((Menu) parent).add(aTarget);
        } else if (parent instanceof ToolBar) {
            ((ToolBar) parent).add(aTarget);
        } else if (parent instanceof TabbedPane) {
            String tabTitle = constraintsElement.getAttribute("tabTitle");
            String tabIconName = constraintsElement.getAttribute("tabIcon");
            String tabTooltipText = constraintsElement.getAttribute("tabTooltipText");
            ((TabbedPane) parent).add(aTarget, tabTitle, resolveIcon(tabIconName));
        } else if (parent instanceof SplitPane) {
            // Split pane children are:
            // - left component
            // - right component
            // Theese children are setted while resolving component references of a split pane.
        } else if (parent instanceof ScrollPane) {
            ScrollPane scroll = (ScrollPane) parent;
            Dimension prefSize = readPrefSize(anElement);
            aTarget.setPreferredSize(prefSize);
            scroll.setView(aTarget);
        } else if (parent instanceof BorderPane) {
            Dimension prefSize = readPrefSize(anElement);
            Integer place = HorizontalPosition.CENTER;
            Integer size = null;
            if (constraintsElement.hasAttribute("place")) {
                String placeName = constraintsElement.getAttribute("place");
                switch (placeName) {
                    case BorderLayout.WEST:
                        place = HorizontalPosition.LEFT;
                        size = prefSize.width;
                        break;
                    case BorderLayout.EAST:
                        place = HorizontalPosition.RIGHT;
                        size = prefSize.width;
                        break;
                    case BorderLayout.NORTH:
                        place = VerticalPosition.TOP;
                        size = prefSize.height;
                        break;
                    case BorderLayout.SOUTH:
                        place = VerticalPosition.BOTTOM;
                        size = prefSize.height;
                        break;
                    case BorderLayout.CENTER:
                        place = HorizontalPosition.CENTER;
                        break;
                    default:
                        place = HorizontalPosition.CENTER;
                }
            }
            ((BorderPane) parent).add(aTarget, place, size);
        } else if (parent instanceof BoxPane) {
            Dimension prefSize = readPrefSize(anElement);
            BoxPane box = (BoxPane) parent;
            if (box.getOrientation() == Orientation.HORIZONTAL) {
                box.add(aTarget, prefSize.width);
            } else {
                box.add(aTarget, prefSize.height);
            }
        } else if (parent instanceof CardPane) {
            String cardName = constraintsElement.getAttribute("cardName");
            ((CardPane) parent).add(aTarget, cardName);
        } else if (parent instanceof FlowPane) {
            Dimension prefSize = readPrefSize(anElement);
            aTarget.setPreferredSize(prefSize);
            ((FlowPane) parent).add(aTarget);
        } else if (parent instanceof GridPane) {
            ((GridPane) parent).add(aTarget);
        } else if (parent instanceof AnchorsPane) {
            AnchorsPane anchors = (AnchorsPane) parent;
            MarginConstraints constraints = readMarginConstraints(constraintsElement);
            anchors.add(aTarget, constraints);
        }
    }

    private static MarginConstraints readMarginConstraints(Element anElement) {
        MarginConstraints result = new MarginConstraints();
        if (anElement.hasAttribute("left")) {
            result.setLeft(Margin.parse(anElement.getAttribute("left")));
        }
        if (anElement.hasAttribute("right")) {
            result.setRight(Margin.parse(anElement.getAttribute("right")));
        }
        if (anElement.hasAttribute("top")) {
            result.setTop(Margin.parse(anElement.getAttribute("top")));
        }
        if (anElement.hasAttribute("bottom")) {
            result.setBottom(Margin.parse(anElement.getAttribute("bottom")));
        }
        if (anElement.hasAttribute("width")) {
            result.setWidth(Margin.parse(anElement.getAttribute("width")));
        }
        if (anElement.hasAttribute("height")) {
            result.setHeight(Margin.parse(anElement.getAttribute("height")));
        }
        return result;
    }

    private List<GridColumnsGroup> readColumns(ModelGrid grid, List<Element> columnsElements) {
        List<GridColumnsGroup> groups = new ArrayList<>();
        columnsElements.stream().sequential().forEach((columnElement) -> {
            GridColumnsGroup group = new GridColumnsGroup();
            groups.add(group);
            List<Element> subColumnsElements = XmlDomUtils.elementsByTagName(columnElement, "column");
            if (subColumnsElements.isEmpty() || forceColumns) {
                // Leaf or design -> Both GridColumnsGroup and ModelColumn should be created
                ModelColumn col = new ModelColumn();
                group.setTableColumn(col);
                grid.addColumn(col);
            }
            if(!subColumnsElements.isEmpty()){
                List<GridColumnsGroup> subGroups = readColumns(grid, subColumnsElements);
                subGroups.stream().sequential().forEach((GridColumnsGroup aGroup) -> {
                    group.addChild(aGroup);
                });
            }
        });
        return groups;
    }
}

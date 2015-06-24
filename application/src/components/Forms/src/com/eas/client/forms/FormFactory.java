/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
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
import com.eas.client.forms.components.model.grid.header.CheckGridColumn;
import com.eas.client.forms.components.model.grid.header.ModelGridColumn;
import com.eas.client.forms.components.model.grid.header.RadioGridColumn;
import com.eas.client.forms.components.model.grid.header.ServiceGridColumn;
import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.HasGroup;
import com.eas.client.forms.components.rt.VFormattedField;
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
import com.eas.client.forms.layouts.CardLayout;
import com.eas.client.forms.layouts.Margin;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.menu.CheckMenuItem;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.MenuItem;
import com.eas.client.forms.menu.MenuSeparator;
import com.eas.client.forms.menu.PopupMenu;
import com.eas.client.forms.menu.RadioMenuItem;
import com.eas.gui.ScriptColor;
import com.eas.script.HasPublished;
import com.eas.script.Scripts;
import com.eas.xml.dom.XmlDomUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import jdk.nashorn.api.scripting.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 */
public class FormFactory {

    public static final String OLD_FORM_ROOT_CONTAINER_NAME = "Form";
    protected Element element;
    protected JSObject model;
    protected Form form;
    protected Map<String, JComponent> widgets = new HashMap<>();
    protected List<JComponent> widgetsList = new ArrayList<>();
    protected boolean oldFormat;
    protected String rootContainerName;
    //
    protected List<Consumer<Map<String, JComponent>>> resolvers = new ArrayList<>();

    public FormFactory(Element anElement, JSObject aModel) {
        super();
        element = anElement;
        model = aModel;
    }

    public Map<String, JComponent> getWidgets() {
        return widgets;
    }

    public List<JComponent> getWidgetsList() {
        return widgetsList;
    }

    public Form getForm() {
        return form;
    }

    public void parse() throws Exception {
        oldFormat = !element.hasAttribute(Form.VIEW_SCRIPT_NAME);
        if (oldFormat) {
            List<Element> widgetsElements = XmlDomUtils.elementsByTagName(element, "widget");
            List<Element> legacyNonVisualElements = XmlDomUtils.elementsByTagName(element, "nonvisual");
            widgetsElements.addAll(legacyNonVisualElements);
            widgetsElements.stream().sequential().forEach((Element aElement) -> {
                try {
                    JComponent widget = readWidget(aElement);
                    if (widget != null) {
                        String wName = widget.getName();
                        assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
                        widgets.put(wName, widget);
                        widgetsList.add(widget);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } else {
            rootContainerName = element.getAttribute(Form.VIEW_SCRIPT_NAME);
            Node childNode = element.getFirstChild();
            while (childNode != null) {
                if (childNode instanceof Element) {
                    JComponent widget = readWidget((Element) childNode);
                    String wName = widget.getName();
                    assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
                    widgets.put(wName, widget);
                    widgetsList.add(widget);
                }
                childNode = childNode.getNextSibling();
            }
        }
        JComponent viewWidget;
        if (oldFormat) {
            element.setAttribute("type", "PanelDesignInfo");
            viewWidget = readWidget(element);
            Dimension rootPrefSize = readPrefSize(element);
            viewWidget.setName(Form.VIEW_SCRIPT_NAME);
            viewWidget.setPreferredSize(rootPrefSize);
        } else {
            viewWidget = widgets.get(rootContainerName);
        }
        if (viewWidget == null) {
            viewWidget = new AnchorsPane();
            viewWidget.setPreferredSize(new Dimension(400, 300));
            Logger.getLogger(FormFactory.class.getName()).log(Level.WARNING, "view widget missing. Falling back to AnchrosPane.");
        }
        viewWidget.setSize(viewWidget.getPreferredSize());
        form = new Form(viewWidget);
        form.setDefaultCloseOperation(XmlDomUtils.readIntegerAttribute(element, "defaultCloseOperation", JFrame.DISPOSE_ON_CLOSE));
        resolveIcon(element.getAttribute("icon"), (ImageIcon aLoaded) -> {
            form.setIcon(aLoaded);
        }, (Exception ex) -> {
            Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
        });

        form.setTitle(element.getAttribute("title"));
        form.setMaximizable(XmlDomUtils.readBooleanAttribute(element, "maximizable", Boolean.TRUE));
        form.setMinimizable(XmlDomUtils.readBooleanAttribute(element, "minimizable", Boolean.TRUE));
        form.setResizable(XmlDomUtils.readBooleanAttribute(element, "resizable", Boolean.TRUE));
        form.setUndecorated(XmlDomUtils.readBooleanAttribute(element, "undecorated", Boolean.FALSE));
        form.setOpacity(XmlDomUtils.readFloatAttribute(element, "opacity", 1.0f));
        form.setAlwaysOnTop(XmlDomUtils.readBooleanAttribute(element, "alwaysOnTop", Boolean.FALSE));
        form.setLocationByPlatform(XmlDomUtils.readBooleanAttribute(element, "locationByPlatform", Boolean.TRUE));
        form.setDesignedViewSize(viewWidget.getPreferredSize());
        //
        resolvers.stream().sequential().forEach((Consumer<Map<String, JComponent>> aResolver) -> {
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

    protected void resolveIcon(String aIconName, Consumer<ImageIcon> onLoad, Consumer<Exception> onFailure) {
        if (aIconName != null && !aIconName.isEmpty()) {
            Scripts.Space space = Scripts.getSpace();
            try {
                IconResources.load(aIconName, space, onLoad, onFailure);
            } catch (Exception ex) {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected JSObject resolveEntity(String aEntityName) throws Exception {
        if (model.hasMember(aEntityName)) {
            Object oEntity = model.getMember(aEntityName);
            if (oEntity instanceof JSObject) {
                return (JSObject) oEntity;
            }
        }
        return null;
    }

    protected JSObject resolveEntity(long aEntityId) throws Exception {
        return null;
    }

    private JComponent readWidget(Element anElement) throws Exception {
        String type;
        if (oldFormat) {
            if (anElement.getTagName().equals("controlInfo")) {
                type = anElement.getAttribute("classHint");
            } else {
                type = anElement.getAttribute("type");
            }
            if (type == null || type.isEmpty()) {
                return null;
            }
        } else {
            type = anElement.getTagName();
        }
        switch (type) {
            // widgets
            case "Label":
            case "LabelDesignInfo":
                Label label = new Label();
                readGeneralProps(anElement, label);
                if (anElement.hasAttribute("icon")) {
                    resolveIcon(anElement.getAttribute("icon"), (ImageIcon aLoaded) -> {
                        label.setIcon(aLoaded);
                    }, (Exception ex) -> {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
                    });
                }
                if (anElement.hasAttribute("text")) {
                    label.setText(anElement.getAttribute("text"));
                }
                label.setHorizontalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "horizontalAlignment", Label.LEFT));
                label.setVerticalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "verticalAlignment", Label.CENTER));
                label.setIconTextGap(XmlDomUtils.readIntegerAttribute(anElement, "iconTextGap", 4));
                label.setHorizontalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "horizontalTextPosition", Label.RIGHT));
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
            case "Button":
            case "ButtonDesignInfo":
                Button button = new Button();
                readGeneralProps(anElement, button);
                readButton(anElement, button);
                return button;
            case "DropDownButton":
            case "DropDownButtonDesignInfo":
                DropDownButton dropDownButton = new DropDownButton();
                readGeneralProps(anElement, dropDownButton);
                readButton(anElement, dropDownButton);
                if (anElement.hasAttribute("dropDownMenu")) {
                    String dropDownMenuName = anElement.getAttribute("dropDownMenu");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        if (aWidgets.containsKey(dropDownMenuName)) {
                            JComponent compMenu = aWidgets.get(dropDownMenuName);
                            if (compMenu instanceof PopupMenu) {
                                dropDownButton.setDropDownMenu((PopupMenu) compMenu);
                            }
                        }
                    });
                }
                return dropDownButton;
            case "ButtonGroup":
            case "ButtonGroupDesignInfo":
                ButtonGroup buttonGroup = new ButtonGroup();
                if (anElement.hasAttribute("name")) {
                    buttonGroup.setName(anElement.getAttribute("name"));
                }
                return buttonGroup;
            case "CheckBox":
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
            case "TextArea":
            case "TextPaneDesignInfo":
                TextArea textArea = new TextArea();
                readGeneralProps(anElement, textArea);
                if (anElement.hasAttribute("text")) {
                    textArea.setText(anElement.getAttribute("text"));
                }
                return textArea;
            case "HtmlArea":
            case "EditorPaneDesignInfo":
                HtmlArea htmlArea = new HtmlArea();
                readGeneralProps(anElement, htmlArea);
                if (anElement.hasAttribute("text")) {
                    htmlArea.setText(anElement.getAttribute("text"));
                }
                return htmlArea;
            case "FormattedField":
            case "FormattedFieldDesignInfo": {
                FormattedField formattedField = new FormattedField();
                readGeneralProps(anElement, formattedField);
                String format = anElement.getAttribute("format");
                int valueType = XmlDomUtils.readIntegerAttribute(anElement, "valueType", VFormattedField.REGEXP);
                formattedField.setValueType(valueType);
                formattedField.setFormat(format);
                if (anElement.hasAttribute("text")) {
                    formattedField.setText(anElement.getAttribute("text"));
                }
                return formattedField;
            }
            case "PasswordField":
            case "PasswordFieldDesignInfo":
                PasswordField passwordField = new PasswordField();
                readGeneralProps(anElement, passwordField);
                if (anElement.hasAttribute("text")) {
                    passwordField.setText(anElement.getAttribute("text"));
                }
                return passwordField;
            case "ProgressBar":
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
            case "RadioButton":
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
            case "Slider":
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
            case "TextField":
            case "TextFieldDesignInfo":
                TextField textField = new TextField();
                readGeneralProps(anElement, textField);
                if (anElement.hasAttribute("text")) {
                    textField.setText(anElement.getAttribute("text"));
                }
                return textField;
            case "ToggleButton":
            case "ToggleButtonDesignInfo":
                ToggleButton toggle = new ToggleButton();
                readGeneralProps(anElement, toggle);
                readButton(anElement, toggle);
                if (anElement.hasAttribute("selected")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "selected", Boolean.FALSE);
                    toggle.setSelected(selected);
                }
                return toggle;
            case "DesktopPane":
            case "DesktopDesignInfo":
                DesktopPane desktop = new DesktopPane();
                readGeneralProps(anElement, desktop);
                return desktop;
            // model widgets
            case "ModelCheckBox":
            case "DbCheckDesignInfo":
                ModelCheckBox modelCheckBox = new ModelCheckBox();
                readGeneralProps(anElement, modelCheckBox);
                if (anElement.hasAttribute("text")) {
                    modelCheckBox.setText(anElement.getAttribute("text"));
                }
                if(anElement.hasAttribute("nullable")) {
                    modelCheckBox.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelCheckBox;
            case "ModelCombo":
            case "DbComboDesignInfo":
                ModelCombo modelCombo = new ModelCombo();
                readGeneralProps(anElement, modelCombo);
                boolean list = XmlDomUtils.readBooleanAttribute(anElement, "list", Boolean.TRUE);
                modelCombo.setList(list);
                if (oldFormat) {
                    Element displayField = XmlDomUtils.getElementByTagName(anElement, "displayField");
                    if (displayField != null && displayField.hasAttribute("fieldName")) {
                        modelCombo.setDisplayField(displayField.getAttribute("fieldName"));
                    }
                    Element valueField = XmlDomUtils.getElementByTagName(anElement, "valueField");
                    if (valueField != null && valueField.hasAttribute("entityId")) {
                        String entityId = valueField.getAttribute("entityId");
                        modelCombo.setDisplayList(resolveEntity(Long.valueOf(entityId)));
                    }
                } else {
                    if (anElement.hasAttribute("displayList")) {
                        String displayList = anElement.getAttribute("displayList");
                        modelCombo.setDisplayList(resolveEntity(displayList));
                    }
                    if (anElement.hasAttribute("displayField")) {
                        String displayField = anElement.getAttribute("displayField");
                        modelCombo.setDisplayField(displayField);
                    }
                }
                if(anElement.hasAttribute("nullable")) {
                    modelCombo.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelCombo;
            case "ModelDate":
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
                if (anElement.hasAttribute("dateField")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "dateField", Boolean.FALSE);
                    modelDate.setDatePicker(selected);
                }
                if (anElement.hasAttribute("timeField")) {
                    boolean selected = XmlDomUtils.readBooleanAttribute(anElement, "timeField", Boolean.FALSE);
                    modelDate.setTimePicker(selected);
                }
                if(anElement.hasAttribute("nullable")) {
                    modelDate.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelDate;
            case "ModelFormattedField":
            case "DbLabelDesignInfo":
                ModelFormattedField modelFormattedField = new ModelFormattedField();
                readGeneralProps(anElement, modelFormattedField);
                try {
                    String format = anElement.getAttribute("format");
                    int valueType = XmlDomUtils.readIntegerAttribute(anElement, "valueType", VFormattedField.REGEXP);
                    modelFormattedField.setValueType(valueType);
                    modelFormattedField.setFormat(format);
                    if (anElement.hasAttribute("text")) {
                        modelFormattedField.setText(anElement.getAttribute("text"));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(anElement.hasAttribute("nullable")) {
                    modelFormattedField.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelFormattedField;
            case "ModelSpin":
            case "DbSpinDesignInfo":
                ModelSpin modelSpin = new ModelSpin();
                readGeneralProps(anElement, modelSpin);
                Double min = null;
                if (anElement.hasAttribute("min")) {
                    min = XmlDomUtils.readDoubleAttribute(anElement, "min", -Double.MAX_VALUE);
                }
                double step = XmlDomUtils.readDoubleAttribute(anElement, "step", 1.0d);
                Double max = null;
                if (anElement.hasAttribute("max")) {
                    max = XmlDomUtils.readDoubleAttribute(anElement, "max", Double.MAX_VALUE);
                }
                try {
                    modelSpin.setMin(min);
                    modelSpin.setMax(max);
                    modelSpin.setStep(step);
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(anElement.hasAttribute("nullable")) {
                    modelSpin.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelSpin;
            case "ModelTextArea":
            case "DbTextDesignInfo":
                ModelTextArea modelTextArea = new ModelTextArea();
                readGeneralProps(anElement, modelTextArea);
                if (anElement.hasAttribute("text")) {
                    try {
                        modelTextArea.setText(anElement.getAttribute("text"));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(anElement.hasAttribute("nullable")) {
                    modelTextArea.setNullable(XmlDomUtils.readBooleanAttribute(anElement, "nullable", true));
                }
                return modelTextArea;
            case "ModelGrid":
            case "DbGridDesignInfo": {
                ModelGrid grid = new ModelGrid();
                readGeneralProps(anElement, grid);
                GridColumnsNode oldFormatRowsHeader = null;
                if (oldFormat) {
                    Element rowsColumns = XmlDomUtils.getElementByTagName(anElement, "rowsColumnsDesignInfo");
                    if (rowsColumns != null) {
                        Element rowsDatasource = XmlDomUtils.getElementByTagName(rowsColumns, "rowsDatasource");
                        if (rowsDatasource != null) {
                            String entityId = rowsDatasource.getAttribute("entityId");
                            try {
                                grid.setData(resolveEntity(Long.valueOf(entityId)));
                            } catch (Exception ex) {
                                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to named model's property ({0}) in old format to grid {1} exception occured: {2}", new Object[]{entityId, grid.getName(), ex.getMessage()});
                            }
                        }
                        int rowsHeaderType = 1;// Usual
                        if (rowsColumns.hasAttribute("rowsHeaderType")) {
                            rowsHeaderType = Integer.valueOf(rowsColumns.getAttribute("rowsHeaderType"));
                        }
                        switch (rowsHeaderType) {
                            case 1:// Usual
                                oldFormatRowsHeader = new ServiceGridColumn();
                                break;
                            case 2:// Check
                                oldFormatRowsHeader = new CheckGridColumn();
                                break;
                            case 3:// Radio
                                oldFormatRowsHeader = new RadioGridColumn();
                                break;
                        }
                    }
                    Element tree = XmlDomUtils.getElementByTagName(anElement, "treeDesignInfo");
                }
                int frozenColumns = XmlDomUtils.readIntegerAttribute(anElement, "frozenColumns", 0);
                int frozenRows = XmlDomUtils.readIntegerAttribute(anElement, "frozenRows", 0);
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
                grid.setFrozenColumns(frozenColumns);
                grid.setFrozenRows(frozenRows);
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
                List<GridColumnsNode> roots = readColumns(anElement);
                List<ModelColumn> columns = new ArrayList<>();
                List<GridColumnsNode> leaves = new ArrayList<>();
                MultiLevelHeader.achieveLeaves(roots, leaves);
                leaves.stream().sequential().forEach((leaf) -> {
                    columns.add((ModelColumn) leaf.getTableColumn());
                });
                if (oldFormatRowsHeader != null) {
                    roots.add(0, oldFormatRowsHeader);
                    columns.add(0, (ModelColumn) oldFormatRowsHeader.getTableColumn());
                }
                grid.setColumns(columns.toArray(new ModelColumn[]{}));
                grid.setHeader(roots);
                if (anElement.hasAttribute("data")) {
                    String entityName = anElement.getAttribute("data");
                    try {
                        grid.setData(resolveEntity(entityName));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to property ({0}) of widget {1} exception occured: {2}", new Object[]{entityName, grid.getName(), ex.getMessage()});
                    }
                }
                if (anElement.hasAttribute("field")) {
                    String dataPropertyPath = anElement.getAttribute("field");
                    grid.setField(dataPropertyPath);
                }
                if (Scripts.isInitialized()) {
                    injectColumns(grid, roots);
                }
                return grid;
            }
            // containers   
            // layouted containers                
            case "PanelDesignInfo":// oldFormat
                Element layoutTag = XmlDomUtils.getElementByTagName(anElement, "layout");
                assert layoutTag != null : "tag layout is required for panel containers.";
                JComponent container = readOldStyleLayoutedContainer(layoutTag);
                readGeneralProps(anElement, container);
                return container;
            case "AnchorsPane":
                JComponent anchorsPane = createAnchorsPane();
                readGeneralProps(anElement, anchorsPane);
                return anchorsPane;
            case "BorderPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent borderPane = createBorderPane(hgap, vgap);
                readGeneralProps(anElement, borderPane);
                return borderPane;
            }
            case "BoxPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                int orientation = XmlDomUtils.readIntegerAttribute(anElement, "orientation", Orientation.HORIZONTAL);
                JComponent boxPane = createBoxPane(orientation, hgap, vgap);
                readGeneralProps(anElement, boxPane);
                return boxPane;
            }
            case "CardPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent cardPane = createCardPane(hgap, vgap);
                readGeneralProps(anElement, cardPane);
                return cardPane;
            }
            case "FlowPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent flowPane = createFlowPane(hgap, vgap);
                readGeneralProps(anElement, flowPane);
                return flowPane;
            }
            case "GridPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                int rows = XmlDomUtils.readIntegerAttribute(anElement, "rows", 0);
                int columns = XmlDomUtils.readIntegerAttribute(anElement, "columns", 0);
                JComponent gridPane = createGridPane(rows, columns, hgap, vgap);
                readGeneralProps(anElement, gridPane);
                return gridPane;
            }
            // predefined layout containers
            case "ScrollPane":
            case "ScrollDesignInfo":
                ScrollPane scroll = new ScrollPane();
                readGeneralProps(anElement, scroll);
                boolean wheelScrollingEnabled = XmlDomUtils.readBooleanAttribute(anElement, "wheelScrollingEnabled", Boolean.TRUE);
                int horizontalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "horizontalScrollBarPolicy", ScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                int verticalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "verticalScrollBarPolicy", ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
                scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
                return scroll;
            case "SplitPane":
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
            case "TabbedPane":
            case "TabsDesignInfo":
                TabbedPane tabs = new TabbedPane();
                readGeneralProps(anElement, tabs);
                int tabPlacement = XmlDomUtils.readIntegerAttribute(anElement, "tabPlacement", TabbedPane.TOP);
                tabs.setTabPlacement(tabPlacement);
                return tabs;
            case "ToolBar":
            case "ToolbarDesignInfo":
                ToolBar toolbar = new ToolBar();
                readGeneralProps(anElement, toolbar);
                return toolbar;
            // menus
            case "Menu":
            case "MenuDesignInfo":
                Menu menu = new Menu();
                readGeneralProps(anElement, menu);
                if (anElement.hasAttribute("text")) {
                    menu.setText(anElement.getAttribute("text"));
                }
                return menu;
            case "MenuItem":
            case "MenuItemDesignInfo":
                MenuItem menuitem = new MenuItem();
                readGeneralProps(anElement, menuitem);
                readButton(anElement, menuitem);
                if (anElement.hasAttribute("text")) {
                    menuitem.setText(anElement.getAttribute("text"));
                }
                return menuitem;
            case "CheckMenuItem":
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
            case "RadioMenuItem":
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
            case "MenuSeparator":
            case "MenuSeparatorDesignInfo":
                MenuSeparator menuSeparator = new MenuSeparator();
                readGeneralProps(anElement, menuSeparator);
                return menuSeparator;
            case "MenuBar":
            case "MenubarDesignInfo":
                MenuBar menuBar = new MenuBar();
                readGeneralProps(anElement, menuBar);
                return menuBar;
            case "PopupMenu":
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
            resolveIcon(anElement.getAttribute("icon"), (ImageIcon aLoaded) -> {
                button.setIcon(aLoaded);
            }, (Exception ex) -> {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
            });
        }
        if (anElement.hasAttribute("text")) {
            button.setText(anElement.getAttribute("text"));
        }
        button.setHorizontalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "horizontalAlignment", Button.CENTER));
        button.setVerticalAlignment(XmlDomUtils.readIntegerAttribute(anElement, "verticalAlignment", Button.CENTER));
        button.setIconTextGap(XmlDomUtils.readIntegerAttribute(anElement, "iconTextGap", 4));
        button.setHorizontalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "horizontalTextPosition", Button.RIGHT));
        button.setVerticalTextPosition(XmlDomUtils.readIntegerAttribute(anElement, "verticalTextPosition", Button.CENTER));
    }

    protected JComponent readOldStyleLayoutedContainer(Element aLayoutElement) {
        String type = aLayoutElement.getAttribute("type");
        assert type != null && !type.isEmpty() : "type attribute is required for layouts to be read from a file";
        int hgap = XmlDomUtils.readIntegerAttribute(aLayoutElement, "hgap", 0);
        int vgap = XmlDomUtils.readIntegerAttribute(aLayoutElement, "vgap", 0);
        switch (type) {
            case "BorderLayoutDesignInfo": {
                return createBorderPane(hgap, vgap);
            }
            case "BoxLayoutDesignInfo": {
                int axis = XmlDomUtils.readIntegerAttribute(aLayoutElement, "axis", BoxLayout.LINE_AXIS);
                return createBoxPane(axis, hgap, vgap);
            }
            case "CardLayoutDesignInfo": {
                return createCardPane(hgap, vgap);
            }
            case "FlowLayoutDesignInfo": {
                int alignment = XmlDomUtils.readIntegerAttribute(aLayoutElement, "alignment", 0);
                return createFlowPane(hgap, vgap);
            }
            case "GridLayoutDesignInfo": {
                int rows = XmlDomUtils.readIntegerAttribute(aLayoutElement, "rows", 0);
                int columns = XmlDomUtils.readIntegerAttribute(aLayoutElement, "columns", 0);
                return createGridPane(rows, columns, hgap, vgap);
            }
            case "AbsoluteLayoutDesignInfo":
            case "MarginLayoutDesignInfo":
                return createAnchorsPane();
            default:
                return null;
        }
    }

    protected JComponent createAnchorsPane() {
        return new AnchorsPane();
    }

    protected JComponent createGridPane(int rows, int columns, int hgap, int vgap) {
        return new GridPane(rows, columns, hgap, vgap);
    }

    protected JComponent createFlowPane(int hgap, int vgap) {
        return new FlowPane(hgap, vgap);
    }

    protected JComponent createCardPane(int hgap, int vgap) {
        return new CardPane(hgap, vgap);
    }

    protected JComponent createBoxPane(int orientation, int hgap, int vgap) {
        return new BoxPane(orientation, hgap, vgap);
    }

    protected JComponent createBorderPane(int hgap, int vgap) {
        return new BorderPane(hgap, vgap);
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
        if (oldFormat) {
            if (aTarget instanceof ModelWidget) {
                Element datamodelElement = XmlDomUtils.getElementByTagName(anElement, "datamodelElement");
                if (datamodelElement != null) {
                    String entityId = datamodelElement.getAttribute("entityId");
                    try {
                        ((ModelWidget) aTarget).setData(resolveEntity(Long.valueOf(entityId)));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to named model's property ({0}) in old format to widget {1} exception occured: {2}", new Object[]{entityId, aTarget.getName(), ex.getMessage()});
                    }
                    String fieldName = datamodelElement.getAttribute("fieldName");
                    try {
                        ((ModelWidget) aTarget).setField("cursor." + fieldName);
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to named model's property ({0}) in old format to widget {1} exception occured: {2}", new Object[]{entityId, aTarget.getName(), ex.getMessage()});
                    }
                }
            }
            if (anElement.hasAttribute("backgroundColor")) {
                ScriptColor background = new ScriptColor(anElement.getAttribute("backgroundColor"));
                aTarget.setBackground(background);
            }
            if (anElement.hasAttribute("foregroundColor")) {
                ScriptColor foreground = new ScriptColor(anElement.getAttribute("foregroundColor"));
                aTarget.setForeground(foreground);
            }
        } else {
            if (anElement.hasAttribute("background")) {
                ScriptColor background = new ScriptColor(anElement.getAttribute("background"));
                aTarget.setBackground(background);
            }
            if (anElement.hasAttribute("foreground")) {
                ScriptColor foreground = new ScriptColor(anElement.getAttribute("foreground"));
                aTarget.setForeground(foreground);
            }
            if (anElement.hasAttribute("data") && aTarget instanceof ModelWidget) {
                String entityName = anElement.getAttribute("data");
                try {
                    ((ModelWidget) aTarget).setData(resolveEntity(entityName));
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to named model's property ({0}) to widget {1} exception occured: {2}", new Object[]{entityName, aTarget.getName(), ex.getMessage()});
                }
            }
        }
        aTarget.setEnabled(XmlDomUtils.readBooleanAttribute(anElement, "enabled", Boolean.TRUE));
        aTarget.setFocusable(XmlDomUtils.readBooleanAttribute(anElement, "focusable", Boolean.TRUE));
        Font font = readFont(anElement);
        if (font != null) {
            aTarget.setFont(font);
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
                    if (popup instanceof PopupMenu) {
                        aTarget.setComponentPopupMenu((PopupMenu) popup);
                    }
                });
            }
        }
        if (anElement.hasAttribute("buttonGroup") && aTarget instanceof HasGroup) {
            String buttonGroupName = anElement.getAttribute("buttonGroup");
            if (!buttonGroupName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent buttonGroup = aWidgets.get(buttonGroupName);
                    if (buttonGroup instanceof ButtonGroup) {
                        ButtonGroup bg = (ButtonGroup) buttonGroup;
                        ((HasGroup) aTarget).setButtonGroup(bg);
                    }
                });
            }
        }
        if (anElement.hasAttribute("parent")) {
            String parentName = anElement.getAttribute("parent");
            if (!parentName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent parent = oldFormat && OLD_FORM_ROOT_CONTAINER_NAME.equalsIgnoreCase(parentName) ? form.getViewWidget() : aWidgets.get(parentName);
                    addToParent(anElement, aTarget, parent);
                });
            }
        }
        if (!oldFormat && rootContainerName.equals(aTarget.getName())) {
            aTarget.setPreferredSize(readPrefSize(anElement));
        }
    }

    protected com.eas.gui.Font readFont(Element anElement) {
        com.eas.gui.Font font = readFontTag(anElement, "font");
        if (font != null) {
            return font;
        } else if (oldFormat) {
            return readFontTag(anElement, "easFont");
        } else {
            return null;
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
            Dimension prefSize = readPrefSize(anElement);
            aTarget.setPreferredSize(prefSize);
            aTarget.setSize(prefSize);
            ((ToolBar) parent).add(aTarget);
        } else if (parent instanceof TabbedPane) {
            if (constraintsElement == null) {// new format
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, TabbedPane.class.getSimpleName() + "Constraints");
            }
            String tabTitle = constraintsElement.getAttribute("tabTitle");
            String tabIconName = constraintsElement.getAttribute("tabIcon");
            String tabTooltipText = oldFormat ? constraintsElement.getAttribute("tabToolTip") : constraintsElement.getAttribute("tabTooltipText");
            TabbedPane tabs = (TabbedPane) parent;
            tabs.add(aTarget, tabTitle);
            int tabIndex = tabs.getTabCount() - 1;
            tabs.setToolTipTextAt(tabIndex, tabTooltipText);
            resolveIcon(tabIconName, (ImageIcon aLoaded) -> {
                if (tabIndex >= 0 && tabIndex < tabs.getTabCount()) {
                    tabs.setIconAt(tabIndex, aLoaded);
                }
            }, (Exception ex) -> {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
            });
        } else if (parent instanceof SplitPane) {
            // Split pane children are:
            // - left component
            // - right component
            // Theese children are setted while resolving component references of a split pane.
        } else if (parent instanceof ScrollPane) {
            ScrollPane scroll = (ScrollPane) parent;
            Dimension prefSize = readPrefSize(anElement);
            aTarget.setPreferredSize(prefSize);
            aTarget.setSize(prefSize);
            scroll.setView(aTarget);
        } else if (parent != null && parent.getLayout() instanceof BorderLayout) {
            if (constraintsElement == null) {// new format
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "BorderPaneConstraints");
            }
            Dimension prefSize = readPrefSize(anElement);
            Integer place = HorizontalPosition.CENTER;
            if (oldFormat) {
                String sPlace = constraintsElement.getAttribute("place");
                if (sPlace != null && !sPlace.isEmpty()) {
                    switch (sPlace) {
                        case BorderLayout.LINE_START:
                        case BorderLayout.WEST:
                            place = HorizontalPosition.LEFT;
                            break;
                        case BorderLayout.LINE_END:
                        case BorderLayout.EAST:
                            place = HorizontalPosition.RIGHT;
                            break;
                        case BorderLayout.PAGE_START:
                        case BorderLayout.NORTH:
                            place = VerticalPosition.TOP;
                            break;
                        case BorderLayout.PAGE_END:
                        case BorderLayout.SOUTH:
                            place = VerticalPosition.BOTTOM;
                            break;
                        default:
                            place = HorizontalPosition.CENTER;
                    }
                }
            } else {
                place = XmlDomUtils.readIntegerAttribute(constraintsElement, "place", HorizontalPosition.CENTER);
            }
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
            addToBorderPane(parent, aTarget, place, size);
        } else if (parent != null && parent.getLayout() instanceof BoxLayout) {
            Dimension prefSize = readPrefSize(anElement);
            addToBoxPane(parent, aTarget, prefSize);
        } else if (parent != null && parent.getLayout() instanceof CardLayout) {
            if (constraintsElement == null) {// new format
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "CardPaneConstraints");
            }
            String cardName = constraintsElement.getAttribute("cardName");
            addToCardPane(parent, aTarget, cardName);
        } else if (parent != null && parent.getLayout() instanceof FlowLayout) {
            Dimension prefSize = readPrefSize(anElement);
            addToFlowPane(parent, aTarget, prefSize);
        } else if (parent != null && parent.getLayout() instanceof GridLayout) {
            addToGridPane(parent, aTarget);
        } else if (parent != null && parent.getLayout() instanceof MarginLayout) {
            if (constraintsElement == null) {// new format
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "AnchorsPaneConstraints");
            }
            MarginConstraints constraints = readMarginConstraints(constraintsElement);
            addToAnchorsPane(parent, aTarget, constraints);
        }
    }

    protected void addToAnchorsPane(JComponent parent, JComponent aTarget, MarginConstraints constraints) {
        AnchorsPane anchors = (AnchorsPane) parent;
        anchors.add(aTarget, constraints);
    }

    protected void addToGridPane(JComponent parent, JComponent aTarget) {
        ((GridPane) parent).add(aTarget);
    }

    protected void addToFlowPane(JComponent parent, JComponent aTarget, Dimension prefSize) {
        ((FlowPane) parent).add(aTarget);
        aTarget.setPreferredSize(prefSize);
    }

    protected void addToCardPane(JComponent parent, JComponent aTarget, String cardName) {
        ((CardPane) parent).add(aTarget, cardName);
    }

    protected void addToBoxPane(JComponent parent, JComponent aTarget, Dimension prefSize) {
        BoxPane box = (BoxPane) parent;
        if (box.getOrientation() == Orientation.HORIZONTAL) {
            box.add(aTarget, prefSize.width);
        } else {
            box.add(aTarget, prefSize.height);
        }
    }

    protected void addToBorderPane(JComponent parent, JComponent aTarget, Integer place, Integer size) {
        BorderPane borderPane = (BorderPane) parent;
        borderPane.add(aTarget, place, size);
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

    private List<GridColumnsNode> readColumns(Element aColumnsElement) throws Exception {
        List<GridColumnsNode> nodes = new ArrayList<>();
        Node childNode = aColumnsElement.getFirstChild();
        while (childNode != null) {
            if (childNode instanceof Element) {
                Element childTag = (Element) childNode;
                String columnType;
                if (oldFormat) {
                    if ("column".equals(childTag.getTagName())) {
                        columnType = "ModelGridColumn";
                    } else {
                        childNode = childNode.getNextSibling();
                        continue;
                    }
                } else {
                    columnType = childTag.getTagName();
                }
                switch (columnType) {
                    case "CheckGridColumn": {
                        CheckGridColumn columnn = new CheckGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "RadioGridColumn": {
                        RadioGridColumn columnn = new RadioGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "ServiceGridColumn": {
                        ServiceGridColumn columnn = new ServiceGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "ModelGridColumn": {
                        ModelGridColumn columnn = new ModelGridColumn();
                        readColumnNode(columnn, childTag);
                        if (childTag.hasAttribute("field")) {
                            columnn.setField(childTag.getAttribute("field"));
                        }
                        if (childTag.hasAttribute("sortField")) {
                            columnn.setSortField(childTag.getAttribute("sortField"));
                        }
                        Node _childNode = childTag.getFirstChild();
                        while (_childNode != null) {
                            if (_childNode instanceof Element) {
                                Element _childTag = (Element) _childNode;
                                if (oldFormat) {
                                    if (_childTag.getTagName().equals("datamodelElement")) {
                                        if (_childTag.hasAttribute("fieldName")) {
                                            columnn.setField(_childTag.getAttribute("fieldName"));
                                        }
                                    }
                                }
                                JComponent editorComp = readWidget(_childTag);
                                if (editorComp instanceof ModelWidget) {
                                    ModelColumn col = (ModelColumn) columnn.getTableColumn();
                                    col.setEditor((ModelWidget) editorComp);
                                    ModelWidget viewComp = (ModelWidget) readWidget((Element) _childNode);
                                    col.setView(viewComp);
                                    if (!oldFormat) {
                                        break;
                                    }
                                }
                            }
                            _childNode = _childNode.getNextSibling();
                        }
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                }
            }
            childNode = childNode.getNextSibling();
        }
        return nodes;
    }

    private void readColumnNode(GridColumnsNode aNode, Element anElement) throws Exception {
        ((ModelColumn) aNode.getTableColumn()).setName(anElement.getAttribute("name"));
        if (anElement.hasAttribute("title")) {
            aNode.setTitle(anElement.getAttribute("title"));
        }
        if (anElement.hasAttribute("background")) {
            ScriptColor background = new ScriptColor(anElement.getAttribute("background"));
            aNode.setBackground(background);
        }
        if (anElement.hasAttribute("foreground")) {
            ScriptColor foreground = new ScriptColor(anElement.getAttribute("foreground"));
            aNode.setForeground(foreground);
        }
        aNode.setReadonly(XmlDomUtils.readBooleanAttribute(anElement, "readonly", Boolean.FALSE));
        aNode.setEnabled(XmlDomUtils.readBooleanAttribute(anElement, "enabled", Boolean.TRUE));
        Font font = readFont(anElement);
        if (font != null) {
            aNode.setFont(font);
        }
        if (anElement.hasAttribute("width")) {
            String width = anElement.getAttribute("width");
            if (width.length() > 2 && width.endsWith("px")) {
                aNode.setWidth(Integer.parseInt(width.substring(0, width.length() - 2)));
            }
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
        aNode.setMovable(XmlDomUtils.readBooleanAttribute(anElement, "movable", Boolean.TRUE));
        aNode.setResizable(XmlDomUtils.readBooleanAttribute(anElement, "resizable", Boolean.TRUE));
        aNode.setSelectOnly(XmlDomUtils.readBooleanAttribute(anElement, "selectOnly", Boolean.FALSE));
        aNode.setSortable(XmlDomUtils.readBooleanAttribute(anElement, "sortable", Boolean.TRUE));
        aNode.setVisible(XmlDomUtils.readBooleanAttribute(anElement, "visible", Boolean.TRUE));
    }

    private void injectColumns(ModelGrid grid, List<GridColumnsNode> roots) {
        JSObject publishedGrid = grid.getPublished();
        roots.stream().forEach((node) -> {
            publishedGrid.setMember(((ModelColumn) node.getTableColumn()).getName(), ((HasPublished) node).getPublished());
            injectColumns(grid, node.getChildren());
        });
    }
}

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
    protected JComponent viewWidget;
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

    public Form getForm() throws Exception {
        if (form == null) {
            form = new Form(viewWidget);
            form.setDefaultCloseOperation(readIntegerAttribute(element, "dco", "defaultCloseOperation", JFrame.DISPOSE_ON_CLOSE));
            resolveIcon(XmlDomUtils.getAttribute(element, "i", "icon"), (ImageIcon aLoaded) -> {
                form.setIcon(aLoaded);
            }, (Exception ex) -> {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
            });

            form.setTitle(XmlDomUtils.getAttribute(element, "tl", "title"));
            form.setClosable(readBooleanAttribute(element, "cle", "closable", Boolean.TRUE));
            form.setMaximizable(readBooleanAttribute(element, "mxe", "maximizable", Boolean.TRUE));
            form.setMinimizable(readBooleanAttribute(element, "mne", "minimizable", Boolean.TRUE));
            form.setResizable(readBooleanAttribute(element, "rs", "resizable", Boolean.TRUE));
            form.setUndecorated(readBooleanAttribute(element, "udr", "undecorated", Boolean.FALSE));
            form.setOpacity(readFloatAttribute(element, "opc", "opacity", 1.0f));
            form.setAlwaysOnTop(readBooleanAttribute(element, "aot", "alwaysOnTop", Boolean.FALSE));
            form.setLocationByPlatform(readBooleanAttribute(element, "lbp", "locationByPlatform", Boolean.TRUE));
            form.setDesignedViewSize(viewWidget.getPreferredSize());
        }
        return form;
    }

    public void parse() throws Exception {
        oldFormat = !element.hasAttribute(Form.VIEW_SCRIPT_NAME);
        if (oldFormat) {
            List<Element> widgetsElements = XmlDomUtils.elementsByTagName(element, "widget", "widget");
            List<Element> legacyNonVisualElements = XmlDomUtils.elementsByTagName(element, "nonvisual", "nonvisual");
            widgetsElements.addAll(legacyNonVisualElements);
            widgetsElements.stream().sequential().forEach((Element aElement) -> {
                try {
                    JComponent widget = readWidget(aElement);
                    if (widget != null) {
                        String wName = widget.getName();
                        assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
                        widgets.put(wName, widget);
                        widgetsList.add(widget);
                    } else {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.WARNING, "Unknown widget tag name: {0}. skipping.", aElement.getTagName());
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
        //
        resolvers.stream().sequential().forEach((Consumer<Map<String, JComponent>> aResolver) -> {
            aResolver.accept(widgets);
        });
    }

    protected Dimension readPrefSize(Element anElement) throws NumberFormatException {
        Dimension prefSize = new Dimension();
        String prefWidth = XmlDomUtils.getAttribute(anElement, "pw", "prefWidth");
        String prefHeight = XmlDomUtils.getAttribute(anElement, "ph", "prefHeight");
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
                IconResources.load(aIconName, null, space, onLoad, onFailure);
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
            case "lb":
            case "Label":
            case "LabelDesignInfo":
                Label label = new Label();
                readGeneralProps(anElement, label);
                if (XmlDomUtils.hasAttribute(anElement, "i", "icon")) {
                    resolveIcon(XmlDomUtils.getAttribute(anElement, "i", "icon"), (ImageIcon aLoaded) -> {
                        label.setIcon(aLoaded);
                    }, (Exception ex) -> {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
                    });
                }
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    label.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                label.setHorizontalAlignment(readIntegerAttribute(anElement, "ha", "horizontalAlignment", Label.LEFT));
                label.setVerticalAlignment(readIntegerAttribute(anElement, "va", "verticalAlignment", Label.CENTER));
                label.setIconTextGap(readIntegerAttribute(anElement, "itg", "iconTextGap", 4));
                label.setHorizontalTextPosition(readIntegerAttribute(anElement, "htp", "horizontalTextPosition", Label.RIGHT));
                label.setVerticalTextPosition(readIntegerAttribute(anElement, "vtp", "verticalTextPosition", Label.CENTER));
                if (XmlDomUtils.hasAttribute(anElement, "lf", "labelFor")) {
                    String labelForName = XmlDomUtils.getAttribute(anElement, "lf", "labelFor");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        if (aWidgets.containsKey(labelForName)) {
                            label.setLabelFor(aWidgets.get(labelForName));
                        }
                    });
                }
                return label;
            case "bt":
            case "Button":
            case "ButtonDesignInfo":
                Button button = new Button();
                readGeneralProps(anElement, button);
                readButton(anElement, button);
                return button;
            case "ddb":
            case "DropDownButton":
            case "DropDownButtonDesignInfo":
                DropDownButton dropDownButton = new DropDownButton();
                readGeneralProps(anElement, dropDownButton);
                readButton(anElement, dropDownButton);
                if (XmlDomUtils.hasAttribute(anElement, "ddm", "dropDownMenu")) {
                    String dropDownMenuName = XmlDomUtils.getAttribute(anElement, "ddm", "dropDownMenu");
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
            case "bg":
            case "ButtonGroup":
            case "ButtonGroupDesignInfo":
                ButtonGroup buttonGroup = new ButtonGroup();
                if (XmlDomUtils.hasAttribute(anElement, "n", "name")) {
                    buttonGroup.setName(XmlDomUtils.getAttribute(anElement, "n", "name"));
                }
                return buttonGroup;
            case "cb":
            case "CheckBox":
            case "CheckDesignInfo":
                CheckBox checkBox = new CheckBox();
                readGeneralProps(anElement, checkBox);
                readButton(anElement, checkBox);
                if (XmlDomUtils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = readBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    checkBox.setSelected(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    checkBox.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return checkBox;
            case "ta":
            case "TextArea":
            case "TextPaneDesignInfo":
                TextArea textArea = new TextArea();
                readGeneralProps(anElement, textArea);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    textArea.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return textArea;
            case "ha":
            case "HtmlArea":
            case "EditorPaneDesignInfo":
                HtmlArea htmlArea = new HtmlArea();
                readGeneralProps(anElement, htmlArea);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    htmlArea.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return htmlArea;
            case "ff":
            case "FormattedField":
            case "FormattedFieldDesignInfo": {
                FormattedField formattedField = new FormattedField();
                readGeneralProps(anElement, formattedField);
                String format = XmlDomUtils.getAttribute(anElement, "fr", "format");
                int valueType = readIntegerAttribute(anElement, "vt", "valueType", VFormattedField.REGEXP);
                formattedField.setValueType(valueType);
                formattedField.setFormat(format);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    formattedField.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return formattedField;
            }
            case "pf":
            case "PasswordField":
            case "PasswordFieldDesignInfo":
                PasswordField passwordField = new PasswordField();
                readGeneralProps(anElement, passwordField);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    passwordField.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return passwordField;
            case "pb":
            case "ProgressBar":
            case "ProgressBarDesignInfo": {
                ProgressBar progressBar = new ProgressBar();
                readGeneralProps(anElement, progressBar);
                int minimum = readIntegerAttribute(anElement, "mm", "minimum", 0);
                int value = readIntegerAttribute(anElement, "vl", "value", 0);
                int maximum = readIntegerAttribute(anElement, "mx", "maximum", 100);
                progressBar.setMinimum(minimum);
                progressBar.setMaximum(maximum);
                progressBar.setValue(value);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    progressBar.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return progressBar;
            }
            case "rb":
            case "RadioButton":
            case "RadioDesignInfo":
                RadioButton radio = new RadioButton();
                readGeneralProps(anElement, radio);
                readButton(anElement, radio);
                if (XmlDomUtils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = readBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    radio.setSelected(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    radio.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return radio;
            case "s":
            case "Slider":
            case "SliderDesignInfo":
                Slider slider = new Slider();
                readGeneralProps(anElement, slider);
                int minimum = readIntegerAttribute(anElement, "mn", "minimum", 0);
                int value = readIntegerAttribute(anElement, "vl", "value", 0);
                int maximum = readIntegerAttribute(anElement, "mx", "maximum", 100);
                slider.setMinimum(minimum);
                slider.setMaximum(maximum);
                slider.setValue(value);
                return slider;
            case "tf":
            case "TextField":
            case "TextFieldDesignInfo":
                TextField textField = new TextField();
                readGeneralProps(anElement, textField);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    textField.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return textField;
            case "tb":
            case "ToggleButton":
            case "ToggleButtonDesignInfo":
                ToggleButton toggle = new ToggleButton();
                readGeneralProps(anElement, toggle);
                readButton(anElement, toggle);
                if (XmlDomUtils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = readBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    toggle.setSelected(selected);
                }
                return toggle;
            case "dp":
            case "DesktopPane":
            case "DesktopDesignInfo":
                DesktopPane desktop = new DesktopPane();
                readGeneralProps(anElement, desktop);
                return desktop;
            // model widgets
            case "mcb":
            case "ModelCheckBox":
            case "DbCheckDesignInfo":
                ModelCheckBox modelCheckBox = new ModelCheckBox();
                readGeneralProps(anElement, modelCheckBox);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    modelCheckBox.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelCheckBox.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelCheckBox;
            case "mc":
            case "ModelCombo":
            case "DbComboDesignInfo":
                ModelCombo modelCombo = new ModelCombo();
                readGeneralProps(anElement, modelCombo);
                boolean list = readBooleanAttribute(anElement, "ls", "list", Boolean.TRUE);
                modelCombo.setList(list);
                if (oldFormat) {
                    Element displayField = XmlDomUtils.getElementByTagName(anElement, null, "displayField");
                    if (displayField != null && displayField.hasAttribute("fieldName")) {
                        modelCombo.setDisplayField(displayField.getAttribute("fieldName"));
                    }
                    Element valueField = XmlDomUtils.getElementByTagName(anElement, null, "valueField");
                    if (valueField != null && valueField.hasAttribute("entityId")) {
                        String entityId = valueField.getAttribute("entityId");
                        modelCombo.setDisplayList(resolveEntity(Long.valueOf(entityId)));
                    }
                } else {
                    if (XmlDomUtils.hasAttribute(anElement, "dl", "displayList")) {
                        String displayList = XmlDomUtils.getAttribute(anElement, "dl", "displayList");
                        modelCombo.setDisplayList(resolveEntity(displayList));
                    }
                    if (XmlDomUtils.hasAttribute(anElement, "df", "displayField")) {
                        String displayField = XmlDomUtils.getAttribute(anElement, "df", "displayField");
                        modelCombo.setDisplayField(displayField);
                    }
                }
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelCombo.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelCombo;
            case "md":
            case "ModelDate":
            case "DbDateDesignInfo":
                ModelDate modelDate = new ModelDate();
                readGeneralProps(anElement, modelDate);
                if (XmlDomUtils.hasAttribute(anElement, "fr", "format")) {
                    String dateFormat = XmlDomUtils.getAttribute(anElement, "fr", "format");
                    try {
                        modelDate.setFormat(dateFormat);
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (XmlDomUtils.hasAttribute(anElement, "dtp", "datePicker")) {
                    boolean selected = readBooleanAttribute(anElement, "dtp", "datePicker", Boolean.FALSE);
                    modelDate.setDatePicker(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "tmp", "timePicker")) {
                    boolean selected = readBooleanAttribute(anElement, "tmp", "timePicker", Boolean.FALSE);
                    modelDate.setTimePicker(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelDate.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelDate;
            case "mff":
            case "ModelFormattedField":
            case "DbLabelDesignInfo":
                ModelFormattedField modelFormattedField = new ModelFormattedField();
                readGeneralProps(anElement, modelFormattedField);
                try {
                    String format = XmlDomUtils.getAttribute(anElement, "fr", "format");
                    int valueType = readIntegerAttribute(anElement, "vt", "valueType", VFormattedField.REGEXP);
                    modelFormattedField.setValueType(valueType);
                    modelFormattedField.setFormat(format);
                    if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                        modelFormattedField.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelFormattedField.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelFormattedField;
            case "msp":
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
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelSpin.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelSpin;
            case "mta":
            case "ModelTextArea":
            case "DbTextDesignInfo":
                ModelTextArea modelTextArea = new ModelTextArea();
                readGeneralProps(anElement, modelTextArea);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    try {
                        modelTextArea.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (XmlDomUtils.hasAttribute(anElement, "nl", "nullable")) {
                    modelTextArea.setNullable(readBooleanAttribute(anElement, "nl", "nullable", true));
                }
                return modelTextArea;
            case "mg":
            case "ModelGrid":
            case "DbGridDesignInfo": {
                ModelGrid grid = new ModelGrid();
                readGeneralProps(anElement, grid);
                GridColumnsNode oldFormatRowsHeader = null;
                if (oldFormat) {
                    Element rowsColumns = XmlDomUtils.getElementByTagName(anElement, null, "rowsColumnsDesignInfo");
                    if (rowsColumns != null) {
                        Element rowsDatasource = XmlDomUtils.getElementByTagName(rowsColumns, null, "rowsDatasource");
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
                    Element tree = XmlDomUtils.getElementByTagName(anElement, null, "treeDesignInfo");
                }
                int frozenColumns = readIntegerAttribute(anElement, "frc", "frozenColumns", 0);
                int frozenRows = readIntegerAttribute(anElement, "frr", "frozenRows", 0);
                boolean insertable = readBooleanAttribute(anElement, "ie", "insertable", Boolean.TRUE);
                boolean deletable = readBooleanAttribute(anElement, "de", "deletable", Boolean.TRUE);
                boolean editable = readBooleanAttribute(anElement, "e", "editable", Boolean.TRUE);
                boolean headerVisible = readBooleanAttribute(anElement, "hv", "headerVisible", Boolean.TRUE);
                boolean draggableRows = readBooleanAttribute(anElement, "dr", "draggableRows", Boolean.FALSE);
                boolean showHorizontalLines = readBooleanAttribute(anElement, "shl", "showHorizontalLines", Boolean.TRUE);
                boolean showVerticalLines = readBooleanAttribute(anElement, "svl", "showVerticalLines", Boolean.TRUE);
                boolean showOddRowsInOtherColor = readBooleanAttribute(anElement, "soc", "showOddRowsInOtherColor", Boolean.TRUE);
                int rowsHeight = readIntegerAttribute(anElement, "rh", "rowsHeight", 30);
                grid.setHeaderVisible(headerVisible);
                grid.setDraggableRows(draggableRows);
                grid.setRowsHeight(rowsHeight);
                grid.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
                grid.setShowVerticalLines(showVerticalLines);
                grid.setShowHorizontalLines(showHorizontalLines);
                grid.setEditable(editable);
                grid.setDeletable(deletable);
                grid.setInsertable(insertable);
                grid.setFrozenColumns(frozenColumns);
                grid.setFrozenRows(frozenRows);
                if (XmlDomUtils.hasAttribute(anElement, "orc", "oddRowsColor")) {
                    String oddRowsColorDesc = XmlDomUtils.getAttribute(anElement, "orc", "oddRowsColor");
                    grid.setOddRowsColor(new ScriptColor(oddRowsColorDesc));
                }
                if (XmlDomUtils.hasAttribute(anElement, "gc", "gridColor")) {
                    String gridColorDesc = XmlDomUtils.getAttribute(anElement, "gc", "gridColor");
                    grid.setGridColor(new ScriptColor(gridColorDesc));
                }
                if (XmlDomUtils.hasAttribute(anElement, "pf", "parentField")) {
                    String parentFieldPath = XmlDomUtils.getAttribute(anElement, "pf", "parentField");
                    grid.setParentField(parentFieldPath);
                }
                if (XmlDomUtils.hasAttribute(anElement, "cf", "childrenField")) {
                    String childrenFieldPath = XmlDomUtils.getAttribute(anElement, "cf", "childrenField");
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
                if (XmlDomUtils.hasAttribute(anElement, "d", "data")) {
                    String entityName = XmlDomUtils.getAttribute(anElement, "d", "data");
                    try {
                        grid.setData(resolveEntity(entityName));
                    } catch (Exception ex) {
                        Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to property ({0}) of widget {1} exception occured: {2}", new Object[]{entityName, grid.getName(), ex.getMessage()});
                    }
                }
                if (XmlDomUtils.hasAttribute(anElement, "f", "field")) {
                    String dataPropertyPath = XmlDomUtils.getAttribute(anElement, "f", "field");
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
                Element layoutTag = XmlDomUtils.getElementByTagName(anElement, null, "layout");
                assert layoutTag != null : "tag layout is required for panel containers.";
                JComponent container = readOldStyleLayoutedContainer(layoutTag);
                readGeneralProps(anElement, container);
                return container;
            case "ap":
            case "AnchorsPane":
                JComponent anchorsPane = createAnchorsPane();
                readGeneralProps(anElement, anchorsPane);
                return anchorsPane;
            case "bp":
            case "BorderPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent borderPane = createBorderPane(hgap, vgap);
                readGeneralProps(anElement, borderPane);
                return borderPane;
            }
            case "bx":
            case "BoxPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                int orientation = readIntegerAttribute(anElement, "on", "orientation", Orientation.HORIZONTAL);
                JComponent boxPane = createBoxPane(orientation, hgap, vgap);
                readGeneralProps(anElement, boxPane);
                return boxPane;
            }
            case "cp":
            case "CardPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent cardPane = createCardPane(hgap, vgap);
                readGeneralProps(anElement, cardPane);
                return cardPane;
            }
            case "fp":
            case "FlowPane": {
                int hgap = XmlDomUtils.readIntegerAttribute(anElement, "hgap", 0);
                int vgap = XmlDomUtils.readIntegerAttribute(anElement, "vgap", 0);
                JComponent flowPane = createFlowPane(hgap, vgap);
                readGeneralProps(anElement, flowPane);
                return flowPane;
            }
            case "gp":
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
            case "sp":
            case "ScrollPane":
            case "ScrollDesignInfo":
                ScrollPane scroll = new ScrollPane();
                readGeneralProps(anElement, scroll);
                boolean wheelScrollingEnabled = readBooleanAttribute(anElement, "wse", "wheelScrollingEnabled", Boolean.TRUE);
                int horizontalScrollBarPolicy = readIntegerAttribute(anElement, "hsp", "horizontalScrollBarPolicy", ScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                int verticalScrollBarPolicy = readIntegerAttribute(anElement, "vsp", "verticalScrollBarPolicy", ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
                scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
                return scroll;
            case "spl":
            case "SplitPane":
            case "SplitDesignInfo":
                SplitPane split = new SplitPane();
                readGeneralProps(anElement, split);
                boolean oneTouchExpandable = readBooleanAttribute(anElement, "ote", "oneTouchExpandable", true);
                int dividerLocation = readIntegerAttribute(anElement, "dvl", "dividerLocation", 0);
                int dividerSize = readIntegerAttribute(anElement, "ds", "dividerSize", 5);
                int orientation = readIntegerAttribute(anElement, "on", "orientation", Orientation.VERTICAL);
                split.setDividerLocation(dividerLocation);
                split.setDividerSize(dividerSize);
                split.setOrientation(orientation);
                split.setOneTouchExpandable(oneTouchExpandable);
                if (XmlDomUtils.hasAttribute(anElement, "lc", "leftComponent")) {
                    String leftComponentName = XmlDomUtils.getAttribute(anElement, "lc", "leftComponent");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        JComponent leftComponent = aWidgets.get(leftComponentName);
                        split.setLeftComponent(leftComponent);
                    });
                }
                if (XmlDomUtils.hasAttribute(anElement, "rc", "rightComponent")) {
                    String rightComponentName = XmlDomUtils.getAttribute(anElement, "rc", "rightComponent");
                    resolvers.add((Map<String, JComponent> aWidgets) -> {
                        JComponent rightComponent = aWidgets.get(rightComponentName);
                        split.setRightComponent(rightComponent);
                    });
                }
                return split;
            case "tp":
            case "TabbedPane":
            case "TabsDesignInfo":
                TabbedPane tabs = new TabbedPane();
                readGeneralProps(anElement, tabs);
                int tabPlacement = readIntegerAttribute(anElement, "tp", "tabPlacement", TabbedPane.TOP);
                tabs.setTabPlacement(tabPlacement);
                return tabs;
            case "tl":
            case "ToolBar":
            case "ToolbarDesignInfo":
                ToolBar toolbar = new ToolBar();
                readGeneralProps(anElement, toolbar);
                return toolbar;
            // menus
            case "m":
            case "Menu":
            case "MenuDesignInfo":
                Menu menu = new Menu();
                readGeneralProps(anElement, menu);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    menu.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return menu;
            case "mi":
            case "MenuItem":
            case "MenuItemDesignInfo":
                MenuItem menuitem = new MenuItem();
                readGeneralProps(anElement, menuitem);
                readButton(anElement, menuitem);
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    menuitem.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return menuitem;
            case "cmi":
            case "CheckMenuItem":
            case "MenuCheckItemDesignInfo":
                CheckMenuItem checkMenuItem = new CheckMenuItem();
                readGeneralProps(anElement, checkMenuItem);
                readButton(anElement, checkMenuItem);
                if (XmlDomUtils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = readBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    checkMenuItem.setSelected(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    checkMenuItem.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return checkMenuItem;
            case "rmi":
            case "RadioMenuItem":
            case "MenuRadioItemDesignInfo":
                RadioMenuItem radioMenuItem = new RadioMenuItem();
                readGeneralProps(anElement, radioMenuItem);
                readButton(anElement, radioMenuItem);
                if (XmlDomUtils.hasAttribute(anElement, "st", "selected")) {
                    boolean selected = readBooleanAttribute(anElement, "st", "selected", Boolean.FALSE);
                    radioMenuItem.setSelected(selected);
                }
                if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
                    radioMenuItem.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
                }
                return radioMenuItem;
            case "ms":
            case "MenuSeparator":
            case "MenuSeparatorDesignInfo":
                MenuSeparator menuSeparator = new MenuSeparator();
                readGeneralProps(anElement, menuSeparator);
                return menuSeparator;
            case "mb":
            case "MenuBar":
            case "MenubarDesignInfo":
                MenuBar menuBar = new MenuBar();
                readGeneralProps(anElement, menuBar);
                return menuBar;
            case "pm":
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
        if (XmlDomUtils.hasAttribute(anElement, "i", "icon")) {
            resolveIcon(XmlDomUtils.getAttribute(anElement, "i", "icon"), (ImageIcon aLoaded) -> {
                button.setIcon(aLoaded);
            }, (Exception ex) -> {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, ex.getMessage());
            });
        }
        if (XmlDomUtils.hasAttribute(anElement, "tx", "text")) {
            button.setText(XmlDomUtils.getAttribute(anElement, "tx", "text"));
        }
        button.setHorizontalAlignment(readIntegerAttribute(anElement, "ha", "horizontalAlignment", Button.CENTER));
        button.setVerticalAlignment(readIntegerAttribute(anElement, "va", "verticalAlignment", Button.CENTER));
        button.setIconTextGap(readIntegerAttribute(anElement, "itg", "iconTextGap", 4));
        button.setHorizontalTextPosition(readIntegerAttribute(anElement, "htp", "horizontalTextPosition", Button.RIGHT));
        button.setVerticalTextPosition(readIntegerAttribute(anElement, "vtp", "verticalTextPosition", Button.CENTER));
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
        if (XmlDomUtils.hasAttribute(anElement, "n", "name")) {
            aTarget.setName(XmlDomUtils.getAttribute(anElement, "n", "name"));
        }
        if (XmlDomUtils.hasAttribute(anElement, "e", "editable") && aTarget instanceof HasEditable) {
            ((HasEditable) aTarget).setEditable(readBooleanAttribute(anElement, "e", "editable", Boolean.TRUE));
        }
        if (XmlDomUtils.hasAttribute(anElement, "et", "emptyText") && aTarget instanceof HasEmptyText) {
            ((HasEmptyText) aTarget).setEmptyText(XmlDomUtils.getAttribute(anElement, "et", "emptyText"));
        }
        if (XmlDomUtils.hasAttribute(anElement, "f", "field") && aTarget instanceof ModelWidget) {
            String fieldPath = XmlDomUtils.getAttribute(anElement, "f", "field");
            try {
                ((ModelWidget) aTarget).setField(fieldPath);
            } catch (Exception ex) {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting field ({0}) to widget {1} exception occured: {2}", new Object[]{fieldPath, aTarget.getName(), ex.getMessage()});
            }
        }
        if (oldFormat) {
            if (aTarget instanceof ModelWidget) {
                Element datamodelElement = XmlDomUtils.getElementByTagName(anElement, null, "datamodelElement");
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
            if (XmlDomUtils.hasAttribute(anElement, "bg", "background")) {
                ScriptColor background = new ScriptColor(XmlDomUtils.getAttribute(anElement, "bg", "background"));
                aTarget.setBackground(background);
            }
            if (XmlDomUtils.hasAttribute(anElement, "fg", "foreground")) {
                ScriptColor foreground = new ScriptColor(XmlDomUtils.getAttribute(anElement, "fg", "foreground"));
                aTarget.setForeground(foreground);
            }
            if (XmlDomUtils.hasAttribute(anElement, "d", "data") && aTarget instanceof ModelWidget) {
                String entityName = XmlDomUtils.getAttribute(anElement, "d", "data");
                try {
                    ((ModelWidget) aTarget).setData(resolveEntity(entityName));
                } catch (Exception ex) {
                    Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, "While setting data to named model's property ({0}) to widget {1} exception occured: {2}", new Object[]{entityName, aTarget.getName(), ex.getMessage()});
                }
            }
        }
        aTarget.setEnabled(readBooleanAttribute(anElement, "en", "enabled", Boolean.TRUE));
        aTarget.setFocusable(readBooleanAttribute(anElement, "fc", "focusable", Boolean.TRUE));
        Font font = readFont(anElement);
        if (font != null) {
            aTarget.setFont(font);
        }
        if (XmlDomUtils.hasAttribute(anElement, "o", "opaque")) {
            aTarget.setOpaque(readBooleanAttribute(anElement, "o", "opaque", Boolean.TRUE));
        }
        if (XmlDomUtils.hasAttribute(anElement, "ttt", "toolTipText")) {
            aTarget.setToolTipText(XmlDomUtils.getAttribute(anElement, "ttt", "toolTipText"));
        }
        int cursorId = readIntegerAttribute(anElement, "cr", "cursor", com.eas.gui.Cursor.DEFAULT_CURSOR);
        aTarget.setCursor(new com.eas.gui.Cursor(cursorId));
        if (XmlDomUtils.hasAttribute(anElement, "v", "visible")) {
            aTarget.setVisible(readBooleanAttribute(anElement, "v", "visible", Boolean.TRUE));
        }
        if (XmlDomUtils.hasAttribute(anElement, "nfc", "nextFocusableComponent")) {
            String nextFocusableName = XmlDomUtils.getAttribute(anElement, "nfc", "nextFocusableComponent");
            if (!nextFocusableName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent nextFocusable = aWidgets.get(nextFocusableName);
                    aTarget.setNextFocusableComponent(nextFocusable);
                });
            }
        }
        if (XmlDomUtils.hasAttribute(anElement, "cpm", "componentPopupMenu")) {
            String popupName = XmlDomUtils.getAttribute(anElement, "cpm", "componentPopupMenu");
            if (!popupName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent popup = aWidgets.get(popupName);
                    if (popup instanceof PopupMenu) {
                        aTarget.setComponentPopupMenu((PopupMenu) popup);
                    }
                });
            }
        }
        if (XmlDomUtils.hasAttribute(anElement, "bgr", "buttonGroup") && aTarget instanceof HasGroup) {
            String buttonGroupName = XmlDomUtils.getAttribute(anElement, "bgr", "buttonGroup");
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
        if (XmlDomUtils.hasAttribute(anElement, "p", "parent")) {
            String parentName = XmlDomUtils.getAttribute(anElement, "p", "parent");
            if (!parentName.isEmpty()) {
                resolvers.add((Map<String, JComponent> aWidgets) -> {
                    JComponent parent = oldFormat && OLD_FORM_ROOT_CONTAINER_NAME.equalsIgnoreCase(parentName) ? viewWidget : aWidgets.get(parentName);
                    addToParent(anElement, aTarget, parent);
                });
            }
        }
        if (!oldFormat && rootContainerName.equals(aTarget.getName())) {
            aTarget.setPreferredSize(readPrefSize(anElement));
        }
    }

    protected com.eas.gui.Font readFont(Element anElement) {
        com.eas.gui.Font font = readFontTag(anElement, "ft", "font");
        if (font != null) {
            return font;
        } else if (oldFormat) {
            return readFontTag(anElement, "easFont", "easFont");
        } else {
            return null;
        }
    }

    private com.eas.gui.Font readFontTag(Element anElement, String aSubTagShortName, String aSubTagLongName) {
        Element easFontElement = XmlDomUtils.getElementByTagName(anElement, aSubTagShortName, aSubTagLongName);
        if (easFontElement != null) {
            String name = XmlDomUtils.getAttribute(easFontElement, "n", "name");
            if (name == null || name.isEmpty() || "null".equals(name)) {
                name = "Arial";
            }
            int style = readIntegerAttribute(easFontElement, "stl", "style", 0);
            int size = readIntegerAttribute(easFontElement, "sz", "size", 12);
            return new com.eas.gui.Font(name, style, size);
        } else {
            return null;
        }
    }

    private void addToParent(Element anElement, JComponent aTarget, JComponent parent) {
        Element constraintsElement = XmlDomUtils.getElementByTagName(anElement, null, "constraints");
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
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "tpc", "TabbedPaneConstraints");
            }
            String tabTitle = XmlDomUtils.getAttribute(constraintsElement, "tt", "tabTitle");
            String tabIconName = XmlDomUtils.getAttribute(constraintsElement, "ti", "tabIcon");
            String tabTooltipText;
            if (oldFormat) {
                tabTooltipText = constraintsElement.getAttribute("tabToolTip");
            } else {
                tabTooltipText = XmlDomUtils.getAttribute(constraintsElement, "ttp", "tabTooltipText");
            }
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
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "bpc", "BorderPaneConstraints");
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
                place = readIntegerAttribute(constraintsElement, "pl", "place", HorizontalPosition.CENTER);
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
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "cpc", "CardPaneConstraints");
            }
            String cardName = XmlDomUtils.getAttribute(constraintsElement, "cn", "cardName");
            addToCardPane(parent, aTarget, cardName);
        } else if (parent != null && parent.getLayout() instanceof FlowLayout) {
            Dimension prefSize = readPrefSize(anElement);
            addToFlowPane(parent, aTarget, prefSize);
        } else if (parent != null && parent.getLayout() instanceof GridLayout) {
            addToGridPane(parent, aTarget);
        } else if (parent != null && parent.getLayout() instanceof MarginLayout) {
            if (constraintsElement == null) {// new format
                constraintsElement = XmlDomUtils.getElementByTagName(anElement, "apc", "AnchorsPaneConstraints");
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

    private static boolean readBooleanAttribute(Element anElement, String aShortName, String aLongName, boolean aDefaultValue) {
        if (anElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readBooleanAttribute(anElement, aShortName, aDefaultValue);
        } else {
            return XmlDomUtils.readBooleanAttribute(anElement, aLongName, aDefaultValue);
        }
    }

    private static float readFloatAttribute(Element anElement, String aShortName, String aLongName, float aDefaultValue) {
        if (anElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readFloatAttribute(anElement, aShortName, aDefaultValue);
        } else {
            return XmlDomUtils.readFloatAttribute(anElement, aLongName, aDefaultValue);
        }
    }

    private static int readIntegerAttribute(Element anElement, String aShortName, String aLongName, int aDefaultValue) {
        if (anElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readIntegerAttribute(anElement, aShortName, aDefaultValue);
        } else {
            return XmlDomUtils.readIntegerAttribute(anElement, aLongName, aDefaultValue);
        }
    }

    private static MarginConstraints readMarginConstraints(Element anElement) {
        MarginConstraints result = new MarginConstraints();
        if (anElement.hasAttribute("l")) {
            result.setLeft(Margin.parse(anElement.getAttribute("l")));
        } else if (anElement.hasAttribute("left")) {
            result.setLeft(Margin.parse(anElement.getAttribute("left")));
        }
        if (anElement.hasAttribute("r")) {
            result.setRight(Margin.parse(anElement.getAttribute("r")));
        } else if (anElement.hasAttribute("right")) {
            result.setRight(Margin.parse(anElement.getAttribute("right")));
        }
        if (anElement.hasAttribute("t")) {
            result.setTop(Margin.parse(anElement.getAttribute("t")));
        } else if (anElement.hasAttribute("top")) {
            result.setTop(Margin.parse(anElement.getAttribute("top")));
        }
        if (anElement.hasAttribute("b")) {
            result.setBottom(Margin.parse(anElement.getAttribute("b")));
        } else if (anElement.hasAttribute("bottom")) {
            result.setBottom(Margin.parse(anElement.getAttribute("bottom")));
        }
        if (anElement.hasAttribute("w")) {
            result.setWidth(Margin.parse(anElement.getAttribute("w")));
        } else if (anElement.hasAttribute("width")) {
            result.setWidth(Margin.parse(anElement.getAttribute("width")));
        }
        if (anElement.hasAttribute("h")) {
            result.setHeight(Margin.parse(anElement.getAttribute("h")));
        } else if (anElement.hasAttribute("height")) {
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
                    case "cgc":
                    case "CheckGridColumn": {
                        CheckGridColumn columnn = new CheckGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "rgc":
                    case "RadioGridColumn": {
                        RadioGridColumn columnn = new RadioGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "sgc":
                    case "ServiceGridColumn": {
                        ServiceGridColumn columnn = new ServiceGridColumn();
                        readColumnNode(columnn, childTag);
                        nodes.add(columnn);
                        List<GridColumnsNode> children = readColumns(childTag);
                        columnn.getChildren().addAll(children);
                        break;
                    }
                    case "mgc":
                    case "ModelGridColumn": {
                        ModelGridColumn columnn = new ModelGridColumn();
                        readColumnNode(columnn, childTag);
                        if (XmlDomUtils.hasAttribute(childTag, "f", "field")) {
                            columnn.setField(XmlDomUtils.getAttribute(childTag, "f", "field"));
                        }
                        if (XmlDomUtils.hasAttribute(childTag, "sf", "sortField")) {
                            columnn.setSortField(XmlDomUtils.getAttribute(childTag, "sf", "sortField"));
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
        ((ModelColumn) aNode.getTableColumn()).setName(XmlDomUtils.getAttribute(anElement, "n", "name"));
        if (XmlDomUtils.hasAttribute(anElement, "tl", "title")) {
            aNode.setTitle(XmlDomUtils.getAttribute(anElement, "tl", "title"));
        }
        if (XmlDomUtils.hasAttribute(anElement, "bg", "background")) {
            ScriptColor background = new ScriptColor(XmlDomUtils.getAttribute(anElement, "bg", "background"));
            aNode.setBackground(background);
        }
        if (XmlDomUtils.hasAttribute(anElement, "fg", "foreground")) {
            ScriptColor foreground = new ScriptColor(XmlDomUtils.getAttribute(anElement, "fg", "foreground"));
            aNode.setForeground(foreground);
        }
        aNode.setReadonly(readBooleanAttribute(anElement, "ro", "readonly", Boolean.FALSE));
        aNode.setEnabled(readBooleanAttribute(anElement, "en", "enabled", Boolean.TRUE));
        Font font = readFont(anElement);
        if (font != null) {
            aNode.setFont(font);
        }
        if (XmlDomUtils.hasAttribute(anElement, "w", "width")) {
            String width = XmlDomUtils.getAttribute(anElement, "w", "width");
            if (width.length() > 2 && width.endsWith("px")) {
                aNode.setWidth(Integer.parseInt(width.substring(0, width.length() - 2)));
            }
        }
        if (XmlDomUtils.hasAttribute(anElement, "mw", "minWidth")) {
            String minWidth = XmlDomUtils.getAttribute(anElement, "mw", "minWidth");
            if (minWidth.length() > 2 && minWidth.endsWith("px")) {
                aNode.setMinWidth(Integer.parseInt(minWidth.substring(0, minWidth.length() - 2)));
            }
        }
        if (XmlDomUtils.hasAttribute(anElement, "mxw", "maxWidth")) {
            String maxWidth = XmlDomUtils.getAttribute(anElement, "mxw", "maxWidth");
            if (maxWidth.length() > 2 && maxWidth.endsWith("px")) {
                aNode.setMaxWidth(Integer.parseInt(maxWidth.substring(0, maxWidth.length() - 2)));
            }
        }
        if (XmlDomUtils.hasAttribute(anElement, "prw", "preferredWidth")) {
            String preferredWidth = XmlDomUtils.getAttribute(anElement, "prw", "preferredWidth");
            if (preferredWidth.length() > 2 && preferredWidth.endsWith("px")) {
                aNode.setPreferredWidth(Integer.parseInt(preferredWidth.substring(0, preferredWidth.length() - 2)));
            }
        }
        aNode.setMovable(readBooleanAttribute(anElement, "m", "movable", Boolean.TRUE));
        aNode.setResizable(readBooleanAttribute(anElement, "rs", "resizable", Boolean.TRUE));
        aNode.setSelectOnly(readBooleanAttribute(anElement, "so", "selectOnly", Boolean.FALSE));
        aNode.setSortable(readBooleanAttribute(anElement, "s", "sortable", Boolean.TRUE));
        aNode.setVisible(readBooleanAttribute(anElement, "v", "visible", Boolean.TRUE));
    }

    private void injectColumns(ModelGrid grid, List<GridColumnsNode> roots) {
        JSObject publishedGrid = grid.getPublished();
        roots.stream().forEach((node) -> {
            publishedGrid.setMember(((ModelColumn) node.getTableColumn()).getName(), ((HasPublished) node).getPublished());
            injectColumns(grid, node.getChildren());
        });
    }
}

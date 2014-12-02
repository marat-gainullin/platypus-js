/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events.factories;

import com.eas.client.forms.Form;
import com.eas.client.forms.IconResources;
import com.eas.client.forms.Orientation;
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
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.containers.AbsolutePane;
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
import com.eas.client.forms.menu.CheckMenuItem;
import com.eas.client.forms.menu.Menu;
import com.eas.client.forms.menu.MenuBar;
import com.eas.client.forms.menu.MenuItem;
import com.eas.client.forms.menu.MenuSeparator;
import com.eas.client.forms.menu.PopupMenu;
import com.eas.client.forms.menu.RadioMenuItem;
import com.eas.xml.dom.XmlDomUtils;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
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
    //
    protected List<Runnable> resolvers = new ArrayList<>();

    public FormFactory(Element anElement, JSObject aModel) {
        super();
        model = aModel;
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
        Dimension prefSize = new Dimension();
        String prefWidth = element.getAttribute("prefWidth");
        String prefHeight = element.getAttribute("prefHeight");
        if (prefWidth.length() > 2 && prefWidth.endsWith("px")) {
            prefSize.width = Integer.parseInt(prefWidth.substring(0, prefWidth.length() - 2));
        }
        if (prefHeight.length() > 2 && prefHeight.endsWith("px")) {
            prefSize.height = Integer.parseInt(prefHeight.substring(0, prefHeight.length() - 2));
        }
        form.setDesignedViewSize(prefSize);
        List<Element> widgetsElements = XmlDomUtils.elementsByTagName(element, "widget");
        Map<String, JComponent> widgets = new HashMap<>();
        widgetsElements.stream().forEach((Element aElement) -> {
            JComponent widget = readWidget(aElement);
            String wName = widget.getName();
            assert wName != null && !wName.isEmpty() : "A widget is expected to be a named item.";
            widgets.put(wName, widget);
        });
        resolvers.stream().forEach((Runnable aResolver) -> {
            aResolver.run();
        });
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
                return new Label();
            case "ButtonDesignInfo":
                return new Button();
            case "DropDownButtonDesignInfo":
                return new DropDownButton();
            case "ButtonGroupDesignInfo":
                return new ButtonGroup();
            case "CheckDesignInfo":
                return new CheckBox();
            case "TextPaneDesignInfo":
                return new TextArea();
            case "EditorPaneDesignInfo":
                return new HtmlArea();
            case "FormattedFieldDesignInfo":
                return new FormattedField();
            case "PasswordFieldDesignInfo":
                return new PasswordField();
            case "ProgressBarDesignInfo":
                return new ProgressBar();
            case "RadioDesignInfo":
                return new RadioButton();
            case "SliderDesignInfo":
                return new Slider();
            case "TextFieldDesignInfo":
                return new TextField();
            case "ToggleButtonDesignInfo":
                return new ToggleButton();
            case "DesktopDesignInfo":
                return new DesktopPane();
            // model widgets
            case "DbCheckDesignInfo":
                return new ModelCheckBox();
            case "DbComboDesignInfo":
                return new ModelCombo();
            case "DbDateDesignInfo":
                return new ModelDate();
            case "DbLabelDesignInfo":
                return new ModelFormattedField();
            case "DbSpinDesignInfo":
                return new ModelSpin();
            case "DbTextDesignInfo":
                return new ModelTextArea();
            case "DbGridDesignInfo":
                return new ModelGrid();
            // containers   
            // layouted containers
            case "PanelDesignInfo":
                Element layoutTag = XmlDomUtils.getElementByTagName(anElement, "layout");
                assert layoutTag != null : "tag layout is required for panel containers.";
                return createLayoutedContainer(layoutTag);
            // predefined layout containers
            case "ScrollDesignInfo":
                boolean wheelScrollingEnabled = XmlDomUtils.readBooleanAttribute(anElement, "wheelScrollingEnabled", Boolean.TRUE);
                int horizontalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "horizontalScrollBarPolicy", ScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                int verticalScrollBarPolicy = XmlDomUtils.readIntegerAttribute(anElement, "verticalScrollBarPolicy", ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ScrollPane scroll = new ScrollPane();
                scroll.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
                scroll.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
                return scroll;
            case "SplitDesignInfo":
                boolean oneTouchExpandable = XmlDomUtils.readBooleanAttribute(anElement, "oneTouchExpandable", true);
                int dividerLocation = XmlDomUtils.readIntegerAttribute(anElement, "dividerLocation", 0);
                int dividerSize = XmlDomUtils.readIntegerAttribute(anElement, "dividerSize", 5);
                int orientation = XmlDomUtils.readIntegerAttribute(anElement, "orientation", Orientation.VERTICAL);
                SplitPane split = new SplitPane();
                split.setDividerLocation(dividerLocation);
                split.setDividerSize(dividerSize);
                split.setOrientation(orientation);
                split.setOneTouchExpandable(oneTouchExpandable);
                return split;
            case "TabsDesignInfo":
                int tabPlacement = XmlDomUtils.readIntegerAttribute(anElement, "tabPlacement", TabbedPane.TOP);
                TabbedPane tabs = new TabbedPane();
                tabs.setTabPlacement(tabPlacement);
                return tabs;
            case "ToolbarDesignInfo":
                return new ToolBar();
            // menus
            case "MenuCheckItemDesignInfo":
                return new CheckMenuItem();
            case "MenuDesignInfo":
                return new Menu();
            case "MenuItemDesignInfo":
                return new MenuItem();
            case "MenuRadioItemDesignInfo":
                return new RadioMenuItem();
            case "MenuSeparatorDesignInfo":
                return new MenuSeparator();
            case "MenubarDesignInfo":
                return new MenuBar();
            case "PopupDesignInfo":
                return new PopupMenu();
            default:
                return null;
        }
    }

    private JComponent createLayoutedContainer(Element aLayoutElement) {
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
                return new AbsolutePane();
            case "MarginLayoutDesignInfo":
                return new AnchorsPane();
            default:
                return null;
        }
    }
}

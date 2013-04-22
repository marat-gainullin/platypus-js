/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.visitors;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.containers.DesktopDesignInfo;
import com.eas.controls.containers.LayersDesignInfo;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.containers.ScrollDesignInfo;
import com.eas.controls.containers.SplitDesignInfo;
import com.eas.controls.containers.TabsDesignInfo;
import com.eas.controls.containers.ToolbarDesignInfo;
import com.eas.controls.menus.MenuCheckItemDesignInfo;
import com.eas.controls.menus.MenuDesignInfo;
import com.eas.controls.menus.MenuItemDesignInfo;
import com.eas.controls.menus.MenuRadioItemDesignInfo;
import com.eas.controls.menus.MenuSeparatorDesignInfo;
import com.eas.controls.menus.MenubarDesignInfo;
import com.eas.controls.menus.PopupDesignInfo;
import com.eas.controls.plain.ButtonDesignInfo;
import com.eas.controls.plain.ButtonGroupDesignInfo;
import com.eas.controls.plain.CheckDesignInfo;
import com.eas.controls.plain.DropDownButtonDesignInfo;
import com.eas.controls.plain.EditorPaneDesignInfo;
import com.eas.controls.plain.FormattedFieldDesignInfo;
import com.eas.controls.plain.LabelDesignInfo;
import com.eas.controls.plain.PasswordFieldDesignInfo;
import com.eas.controls.plain.ProgressBarDesignInfo;
import com.eas.controls.plain.RadioDesignInfo;
import com.eas.controls.plain.SliderDesignInfo;
import com.eas.controls.plain.TextFieldDesignInfo;
import com.eas.controls.plain.TextPaneDesignInfo;
import com.eas.controls.plain.ToggleButtonDesignInfo;
import com.eas.gui.JDropDownButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 *
 * @author mg
 */
public class ControlClassFinder implements ControlsDesignInfoVisitor {

    protected Class result = null;

    public Class getResult() {
        return result;
    }

    @Override
    public void visit(LabelDesignInfo aInfo) {
        result = JLabel.class;
    }

    @Override
    public void visit(ButtonDesignInfo aInfo) {
        result = JButton.class;
    }

    @Override
    public void visit(DropDownButtonDesignInfo aInfo) {
        result = JDropDownButton.class;
    }

    @Override
    public void visit(ButtonGroupDesignInfo aInfo) {
        result = ButtonGroup.class;
    }

    @Override
    public void visit(CheckDesignInfo aInfo) {
        result = JCheckBox.class;
    }

    @Override
    public void visit(TextPaneDesignInfo aInfo) {
        result = JTextPane.class;
    }

    @Override
    public void visit(EditorPaneDesignInfo aInfo) {
        result = JEditorPane.class;
    }

    @Override
    public void visit(FormattedFieldDesignInfo aInfo) {
        result = JFormattedTextField.class;
    }

    @Override
    public void visit(PasswordFieldDesignInfo aInfo) {
        result = JPasswordField.class;
    }

    @Override
    public void visit(ProgressBarDesignInfo aInfo) {
        result = JProgressBar.class;
    }

    @Override
    public void visit(RadioDesignInfo aInfo) {
        result = JRadioButton.class;
    }

    @Override
    public void visit(SliderDesignInfo aInfo) {
        result = JSlider.class;
    }

    @Override
    public void visit(TextFieldDesignInfo aInfo) {
        result = JTextField.class;
    }

    @Override
    public void visit(ToggleButtonDesignInfo aInfo) {
        result = JToggleButton.class;
    }

    @Override
    public void visit(DesktopDesignInfo aInfo) {
        result = JDesktopPane.class;
    }

    @Override
    public void visit(LayersDesignInfo aInfo) {
        result = JLayeredPane.class;
    }

    @Override
    public void visit(PanelDesignInfo aInfo) {
        result = JPanel.class;
    }

    @Override
    public void visit(ScrollDesignInfo aInfo) {
        result = JScrollPane.class;
    }

    @Override
    public void visit(SplitDesignInfo aInfo) {
        result = JSplitPane.class;
    }

    @Override
    public void visit(TabsDesignInfo aInfo) {
        result = JTabbedPane.class;
    }

    @Override
    public void visit(ToolbarDesignInfo aInfo) {
        result = JToolBar.class;
    }

    @Override
    public void visit(MenuCheckItemDesignInfo aInfo) {
        result = JCheckBoxMenuItem.class;
    }

    @Override
    public void visit(MenuDesignInfo aInfo) {
        result = JMenu.class;
    }

    @Override
    public void visit(MenuItemDesignInfo aInfo) {
        result = JMenuItem.class;
    }

    @Override
    public void visit(MenuRadioItemDesignInfo aInfo) {
        result = JRadioButtonMenuItem.class;
    }

    @Override
    public void visit(MenuSeparatorDesignInfo aInfo) {
        result = JSeparator.class;
    }

    @Override
    public void visit(MenubarDesignInfo aInfo) {
        result = JMenuBar.class;
    }

    @Override
    public void visit(PopupDesignInfo aInfo) {
        result = JPopupMenu.class;
    }

    @Override
    public void visit(FormDesignInfo aInfo) {
        result = JFrame.class;
    }
}

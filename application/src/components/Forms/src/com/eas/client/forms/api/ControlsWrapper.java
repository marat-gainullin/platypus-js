/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.client.forms.api.components.ComponentWrapper;
import com.eas.client.forms.api.components.model.ModelComponentWrapper;
import com.eas.client.forms.api.containers.ContainerWrapper;
import com.eas.client.forms.api.menu.MenuWrapper;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.containers.DesktopDesignInfo;
import com.eas.controls.containers.LayersDesignInfo;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.containers.ScrollDesignInfo;
import com.eas.controls.containers.SplitDesignInfo;
import com.eas.controls.containers.TabsDesignInfo;
import com.eas.controls.containers.ToolbarDesignInfo;
import com.eas.controls.events.ControlEventsIProxy;
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
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import com.eas.gui.JDropDownButton;
import com.eas.script.NativeJavaHostObject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
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
public class ControlsWrapper implements DbControlsDesignInfoVisitor {

    protected JComponent target;
    protected Component<?> result;

    public ControlsWrapper(JComponent aTarget) {
        target = aTarget;
    }

    public Component<?> getResult() {
        return result;
    }

    @Override
    public void visit(DbCheckDesignInfo dcdi) {
        result = ModelComponentWrapper.wrap((DbCheck) target);
    }

    @Override
    public void visit(DbComboDesignInfo dcdi) {
        result = ModelComponentWrapper.wrap((DbCombo) target);
    }

    @Override
    public void visit(DbDateDesignInfo dddi) {
        result = ModelComponentWrapper.wrap((DbDate) target);
    }

    @Override
    public void visit(DbImageDesignInfo didi) {
        result = ModelComponentWrapper.wrap((DbImage) target);
    }

    @Override
    public void visit(DbLabelDesignInfo dldi) {
        result = ModelComponentWrapper.wrap((DbLabel) target);
    }

    @Override
    public void visit(DbSchemeDesignInfo dsdi) {
        result = ModelComponentWrapper.wrap((DbScheme) target);
    }

    @Override
    public void visit(DbSpinDesignInfo dsdi) {
        result = ModelComponentWrapper.wrap((DbSpin) target);
    }

    @Override
    public void visit(DbTextDesignInfo dtdi) {
        result = ModelComponentWrapper.wrap((DbText) target);
    }

    @Override
    public void visit(DbGridDesignInfo dgdi) {
        result = ModelComponentWrapper.wrap((DbGrid) target);
    }

    @Override
    public void visit(DbMapDesignInfo dmdi) {
        result = ModelComponentWrapper.wrap((DbMap) target);
    }

    @Override
    public void visit(LabelDesignInfo ldi) {
        result = ComponentWrapper.wrap((JLabel) target);
    }

    @Override
    public void visit(ButtonDesignInfo bdi) {
        result = ComponentWrapper.wrap((JButton) target);
    }

    @Override
    public void visit(DropDownButtonDesignInfo ddbdi) {
        result = ComponentWrapper.wrap((JDropDownButton) target);
    }

    @Override
    public void visit(ButtonGroupDesignInfo bgdi) {
        result = ContainerWrapper.wrap((ButtonGroupWrapper) target);
    }

    @Override
    public void visit(CheckDesignInfo cdi) {
        result = ComponentWrapper.wrap((JCheckBox) target);
    }

    @Override
    public void visit(TextPaneDesignInfo epdi) {
        result = ComponentWrapper.wrap((JTextPane) target);
    }
    @Override
    public void visit(EditorPaneDesignInfo epdi) {
        result = ComponentWrapper.wrap((JEditorPane) target);
    }

    @Override
    public void visit(FormattedFieldDesignInfo ffdi) {
        result = ComponentWrapper.wrap((JFormattedTextField) target);
    }

    @Override
    public void visit(PasswordFieldDesignInfo pfdi) {
        result = ComponentWrapper.wrap((JPasswordField) target);
    }

    @Override
    public void visit(ProgressBarDesignInfo pbdi) {
        result = ComponentWrapper.wrap((JProgressBar) target);
    }

    @Override
    public void visit(RadioDesignInfo rdi) {
        result = ComponentWrapper.wrap((JRadioButton) target);
    }

    @Override
    public void visit(SliderDesignInfo sdi) {
        result = ComponentWrapper.wrap((JSlider) target);
    }

    @Override
    public void visit(TextFieldDesignInfo tfdi) {
        result = ComponentWrapper.wrap((JTextField) target);
    }

    @Override
    public void visit(ToggleButtonDesignInfo tbdi) {
        result = ComponentWrapper.wrap((JToggleButton) target);
    }

    @Override
    public void visit(FormDesignInfo fdi) {
        throw new UnsupportedOperationException("Form shouldn't be wrapped");
    }

    @Override
    public void visit(DesktopDesignInfo ddi) {
        result = ComponentWrapper.wrap((JDesktopPane) target);
    }

    @Override
    public void visit(LayersDesignInfo ldi) {
        //result = ComponentWrapper.wrap((JLayeredPane) target);
    }

    @Override
    public void visit(PanelDesignInfo pdi) {
        result = ContainerWrapper.wrap((JPanel) target, target.getLayout());
    }

    @Override
    public void visit(ScrollDesignInfo sdi) {
        result = ContainerWrapper.wrap((JScrollPane) target);
    }

    @Override
    public void visit(SplitDesignInfo sdi) {
        result = ContainerWrapper.wrap((JSplitPane) target);
    }

    @Override
    public void visit(TabsDesignInfo tdi) {
        result = ContainerWrapper.wrap((JTabbedPane) target);
    }

    @Override
    public void visit(ToolbarDesignInfo tdi) {
        result = ContainerWrapper.wrap((JToolBar) target);
    }

    @Override
    public void visit(MenuCheckItemDesignInfo mcidi) {
        result = MenuWrapper.wrap((JCheckBoxMenuItem) target);
    }

    @Override
    public void visit(MenuDesignInfo mdi) {
        result = MenuWrapper.wrap((JMenu) target);
    }

    @Override
    public void visit(MenuItemDesignInfo midi) {
        result = MenuWrapper.wrap((JMenuItem) target);
    }

    @Override
    public void visit(MenuRadioItemDesignInfo mridi) {
        result = MenuWrapper.wrap((JRadioButtonMenuItem) target);
    }

    @Override
    public void visit(MenuSeparatorDesignInfo msdi) {
        result = MenuWrapper.wrap((JSeparator) target);
    }

    @Override
    public void visit(MenubarDesignInfo mdi) {
        result = MenuWrapper.wrap((JMenuBar) target);
    }

    @Override
    public void visit(PopupDesignInfo pdi) {
        result = MenuWrapper.wrap((JPopupMenu) target);
    }

    public static NativeJavaHostObject getJsWrapper(Component<?> aComp) {
        return aComp.getJsWrapper();
    }

    public static void setJsWrapper(Component<?> aComp, NativeJavaHostObject aWrapper) {
        aComp.setJsWrapper(aWrapper);
    }

    public static ControlEventsIProxy getEventsProxy(Component<?> aComp) {
        return Component.getEventsProxy(unwrap(aComp));
    }

    public static JComponent unwrap(Component<?> aComp) {
        return Component.unwrap(aComp);
    }

    public static void clearPreferredSize(Component<?> aComp) {
        //if (aComp != null && aComp.clearPrefSize) {
        JComponent delegate = Component.unwrap(aComp);
        if (delegate != null) {
            delegate.setPreferredSize(null);
        }
        //}
    }

    public static void clearMaximumSize(Component<?> aComp) {
        //if (aComp != null && aComp.clearPrefSize) {
        JComponent delegate = Component.unwrap(aComp);
        if (delegate != null) {
            delegate.setMaximumSize(null);
        }
        //}
    }
}

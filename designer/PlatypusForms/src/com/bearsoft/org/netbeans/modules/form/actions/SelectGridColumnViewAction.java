/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.actions;

import com.bearsoft.org.netbeans.modules.form.FormInspector;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADComponentCookie;
import com.bearsoft.org.netbeans.modules.form.bound.RADColumnView;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteItem;
import com.bearsoft.org.netbeans.modules.form.palette.PaletteUtils;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.header.ModelGridColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class SelectGridColumnViewAction extends CookieAction {

    public static boolean isEditableComponent(RADComponent<?> aComponent) {
        return aComponent instanceof RADModelGridColumn && ((RADModelGridColumn) aComponent).getBeanInstance() instanceof ModelGridColumn;
    }

    public SelectGridColumnViewAction() {
        super();
    }

    @Override
    protected int mode() {
        return MODE_ONE;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class<?>[]{RADComponentCookie.class};
    }

    @Override
    protected void performAction(Node[] aNodes) {
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SelectGridColumnViewAction.class, "CTL_SelectGridColumnViewAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return getPopupPresenter();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return getColumnsViewsMenu();
    }

    protected JMenu getColumnsViewsMenu() {
        JMenu viewsMenu = new ColumnsViewsMenu(getName());
        viewsMenu.setEnabled(isEnabled());
        HelpCtx.setHelpIDString(viewsMenu, FillGridColumnsAction.class.getName());
        return viewsMenu;
    }

    private static PaletteItem[] getAllColumnsViews() {
        PaletteItem[] allItems = PaletteUtils.getAllItems();
        java.util.List<PaletteItem> layoutsList = new ArrayList<>();
        for (int i = 0; i < allItems.length; i++) {
            if (allItems[i] != null && allItems[i].getComponentClass() != null && ModelWidget.class.isAssignableFrom(allItems[i].getComponentClass())) {
                assert allItems[i].isVisual();
                layoutsList.add(allItems[i]);
            }
        }
        PaletteItem[] layouts = new PaletteItem[layoutsList.size()];
        layoutsList.toArray(layouts);
        return layouts;
    }

    private static class ColumnsViewsMenu extends JMenu {

        private boolean initialized = false;

        private ColumnsViewsMenu(String name) {
            super(name);
        }

        //Заполнение меню выбора RADModelGridColumn'a
        @Override
        public JPopupMenu getPopupMenu() {
            JPopupMenu popup = super.getPopupMenu();
            RADModelGridColumn radColumn = getSelectedColumn();
            if (radColumn != null && !initialized) {
                popup.removeAll();
                PaletteItem[] views = getAllColumnsViews();
                for (int i = 0; i < views.length; i++) {
                    JMenuItem mi = new JMenuItem(views[i].getNode().getDisplayName());
                    if (radColumn.getViewControl() != null && radColumn.getViewControl().getBeanClass() == views[i].getComponentClass()) {
                        setBoldFontForMenuText(mi);
                    }
                    mi.setIcon(new ImageIcon(views[i].getNode().getIcon(BeanInfo.ICON_COLOR_16x16)));
                    HelpCtx.setHelpIDString(mi, SelectGridColumnViewAction.class.getName());
                    popup.add(mi);
                    mi.addActionListener(new ViewActionListener(views[i]));
                }
                initialized = true;
            }
            return popup;
        }

        private static void setBoldFontForMenuText(JMenuItem mi) {
            java.awt.Font font = mi.getFont();
            mi.setFont(font.deriveFont(font.getStyle() | java.awt.Font.BOLD));
        }
    }

    private static RADModelGridColumn getSelectedColumn() {
        // using NodeAction and global activated nodes is not reliable
        // (activated nodes are set with a delay after selection in
        // FormInspector)
        Node[] nodes = FormInspector.getInstance().getExplorerManager().getSelectedNodes();
        if (nodes != null && nodes.length == 1 && nodes[0].getLookup().lookup(RADComponentCookie.class) != null
                && nodes[0].getLookup().lookup(RADComponentCookie.class).getRADComponent() instanceof RADModelGridColumn) {
            try {
                return (RADModelGridColumn) nodes[0].getLookup().lookup(RADComponentCookie.class).getRADComponent();
            } catch (Exception ex) {
                Logger.getLogger(SelectGridColumnViewAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private static class ViewActionListener implements ActionListener {

        private PaletteItem paletteItem;

        ViewActionListener(PaletteItem aPaletteItem) {
            paletteItem = aPaletteItem;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            RADModelGridColumn radColumn = getSelectedColumn();
            if (radColumn != null && radColumn.getViewControl() != null) {
                try {
                    RADColumnView<? super ModelComponentDecorator> viewControl = new RADColumnView<>();
                    viewControl.initialize(radColumn.getFormModel());
                    viewControl.setInstance((ModelComponentDecorator) paletteItem.getComponentClass().newInstance());
                    radColumn.getFormModel().setColumnViewImpl(radColumn, viewControl);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(SelectGridColumnViewAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

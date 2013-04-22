/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.open.i18n.I18N;
import com.jeta.open.resources.AppResourceLoader;
import com.jeta.open.resources.RtIcons;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author Mg
 */
public class MenuItemsProperty extends ToolButtonsProperty {

    static final long serialVersionUID = 2375434415791274626L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    protected static String m_PropName = "menuItems";

    public MenuItemsProperty() {
        super(m_PropName);
    }

    @Override
    public String toString() {
        return I18N.getLocalizedMessage(m_PropName);
    }

    @Override
    protected String getPropName() {
        return m_PropName;
    }

    @Override
    public void setValue(Object obj) {
        if (obj != null && obj instanceof MenuItemsProperty) {
            assignButtons((MenuItemsProperty) obj);
        }
    }

    private void assignButtonsFromList(MenuItemsItem aParent, LinkedList<ToolButtonsItem> aParentChildren, LinkedList<ToolButtonsItem> abtns) {
        if (aParentChildren != null && abtns != null) {
            for (int i = 0; i < abtns.size(); i++) {
                ToolButtonsItem loldButton = abtns.get(i);
                MenuItemsItem lnewButton = null;
                if (loldButton != null && !(loldButton instanceof MenuSeparatorItem)) {
                    lnewButton = new MenuItemsItem(aParent, loldButton.m_Name, loldButton.m_Caption,
                            loldButton.m_Hint, loldButton.m_PictureFile, loldButton.m_ScriptAction, loldButton.m_Action,
                            loldButton.m_Toggle, loldButton.m_GroupNumber, loldButton.m_Mnemonic, loldButton.m_MnemonicModifiers, loldButton.m_Visible);
                    if (loldButton instanceof MenuItemsItem) {
                        MenuItemsItem l_oldItem = (MenuItemsItem) loldButton;
                        assignButtonsFromList(lnewButton, lnewButton.m_Children, l_oldItem.m_Children);
                    }
                }
                aParentChildren.add(lnewButton);
            }
        }
    }

    @Override
    public void assignButtons(ToolButtonsProperty aSourcebuttons) {
        if (aSourcebuttons != null && aSourcebuttons instanceof MenuItemsProperty) {
            MenuItemsProperty l_Sourcebuttons = (MenuItemsProperty) aSourcebuttons;
            m_buttons.clear();
            assignButtonsFromList(null, m_buttons, l_Sourcebuttons.m_buttons);
        }
    }

    protected void addRecursive2Bean(JComponent parentComp, LinkedList<ToolButtonsItem> aButtons, JETABean jbean) {
        HashMap<Integer, ButtonGroup> lGroups = new HashMap<Integer, ButtonGroup>();
        for (int i = 0; i < aButtons.size(); i++) {
            if (aButtons.get(i) != null && aButtons.get(i) instanceof MenuItemsItem) {
                MenuItemsItem l_MenuItem = (MenuItemsItem) aButtons.get(i);
                if (l_MenuItem.m_Visible) {
                    JMenuItem lnewMenuItem = null;
                    if (l_MenuItem.m_Children.size() > 0) {
                        JMenu lsubMenu = new JMenu();
                        lsubMenu.setAction(l_MenuItem.m_Action);
                        lsubMenu.setText(l_MenuItem.m_Caption);
                        lsubMenu.setName(l_MenuItem.m_Name);

                        if (l_MenuItem.m_Mnemonic > 0 && l_MenuItem.m_MnemonicModifiers > 0) {
                            lsubMenu.setMnemonic(l_MenuItem.m_Mnemonic);
                        }
                        if (l_MenuItem.m_PictureFile != null && !l_MenuItem.m_PictureFile.isEmpty()) {
                            lsubMenu.setIcon(AppResourceLoader.getImage(RtIcons.m_iconsPrefix + l_MenuItem.m_PictureFile));
                        }
                        if (l_MenuItem.m_Hint != null && l_MenuItem.m_Hint.equals("")) {
                            lsubMenu.setToolTipText(null);
                        } else {
                            lsubMenu.setToolTipText(l_MenuItem.m_Hint);
                        }
                        lsubMenu.addActionListener(jbean.getEventsHandler());

                        addRecursive2Bean(lsubMenu, l_MenuItem.m_Children, jbean);
                        parentComp.add(lsubMenu);
                    } else {
                        if (l_MenuItem.m_Toggle) {
                            if (l_MenuItem.m_GroupNumber <= 0) {
                                lnewMenuItem = new JCheckBoxMenuItem();
                            } else {
                                lnewMenuItem = new JRadioButtonMenuItem();
                                ButtonGroup lGroup = lGroups.get(l_MenuItem.m_GroupNumber);
                                if (lGroup == null) {
                                    lGroup = new ButtonGroup();
                                    lGroups.put(l_MenuItem.m_GroupNumber, lGroup);
                                }
                                lGroup.add(lnewMenuItem);
                            }
                        } else {
                            lnewMenuItem = new JMenuItem();
                        }
                        lnewMenuItem.setAction(l_MenuItem.m_Action);
                        lnewMenuItem.setName(l_MenuItem.m_Name);
                        lnewMenuItem.setText(l_MenuItem.m_Caption);

                        if (l_MenuItem.m_Hint != null && l_MenuItem.m_Hint.equals("")) {
                            lnewMenuItem.setToolTipText(null);
                        } else {
                            lnewMenuItem.setToolTipText(l_MenuItem.m_Hint);
                        }

                        if (l_MenuItem.m_Mnemonic > 0 && l_MenuItem.m_MnemonicModifiers > 0) {
                            lnewMenuItem.setAccelerator(KeyStroke.getKeyStroke(l_MenuItem.m_Mnemonic, l_MenuItem.m_MnemonicModifiers, true));
                        }
                        if (l_MenuItem.m_PictureFile != null && !l_MenuItem.m_PictureFile.isEmpty()) {
                            lnewMenuItem.setIcon(AppResourceLoader.getImage(RtIcons.m_iconsPrefix + l_MenuItem.m_PictureFile));
                        }
                        lnewMenuItem.putClientProperty(FormUtils.SCRIPT_ACTION_NAME, l_MenuItem.m_ScriptAction);
                        lnewMenuItem.addActionListener(jbean.getEventsHandler());
                        //lnewMenuItem.setActionCommand(l_Button.m_ScriptAction);

                        parentComp.add(lnewMenuItem);
                    }
                }
            } else if (aButtons.get(i) == null) {
                Separator lSep = new Separator();
                parentComp.add(lSep);
            }

        }
    }

    @Override
    public void updateBean(JETABean jbean) {
        if (jbean != null) {
            if (jbean.getDelegate() != null && jbean.getDelegate() instanceof JPopupMenu) {
                JComponent lMenu = (JComponent) jbean.getDelegate();
                jbean.remove(lMenu);
                lMenu.removeAll();
                addRecursive2Bean(lMenu, m_buttons, jbean);
                if (FormUtils.isDesignMode() && lMenu instanceof JPopupMenu) {
                    JPopupMenu ppm = (JPopupMenu) lMenu;
                    ppm.setInvoker(jbean);
                }
            }
        }
    }
}

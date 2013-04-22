/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import com.jeta.open.resources.AppResourceLoader;
import com.jeta.open.resources.RtIcons;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 *
 * @author Mg
 */
public class ToolButtonsProperty extends JETAProperty {

    static final long serialVersionUID = 2375434415661274626L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 1;
    public static final LinkedList<ToolButtonsItem> EMPTY_TOOL_BUTTON_LIST = new LinkedList<ToolButtonsItem>();
    private JToolBar m_Toolbar = null;
    protected LinkedList<ToolButtonsItem> m_buttons = null;
    private static String m_PropName = "buttons";

    public ToolButtonsProperty() {
        super(m_PropName);
        m_buttons = new LinkedList<ToolButtonsItem>();
    }

    public ToolButtonsProperty(String aPropName) {
        super(aPropName);
        m_buttons = new LinkedList<ToolButtonsItem>();
    }

    protected String getPropName() {
        return m_PropName;
    }

    public void assignButtons(ToolButtonsProperty aSourcebuttons) {
        m_buttons.clear();
        for (int i = 0; i < aSourcebuttons.m_buttons.size(); i++) {
            ToolButtonsItem loldButton = aSourcebuttons.m_buttons.get(i);
            ToolButtonsItem lnewButton = null;
            if (loldButton != null) {
                lnewButton = new ToolButtonsItem(loldButton.m_Name, loldButton.m_Caption,
                        loldButton.m_Hint, loldButton.m_PictureFile, loldButton.m_ScriptAction, loldButton.m_Action,
                        loldButton.m_Toggle, loldButton.m_GroupNumber, loldButton.m_Mnemonic,
                        loldButton.m_MnemonicModifiers, loldButton.m_Visible);
            }
            m_buttons.add(lnewButton);
        }
    }

    public LinkedList<ToolButtonsItem> getButtons() {
        return m_buttons;
    }

    @Override
    public void setValue(Object obj) {
        if (obj != null && obj instanceof ToolButtonsProperty) {
            assignButtons((ToolButtonsProperty) obj);
        }
    }

    @Override
    public void updateBean(JETABean jbean) {
        if (jbean != null) {
            if (jbean.getDelegate() != null && jbean.getDelegate() instanceof JToolBar) {
                m_Toolbar = (JToolBar) jbean.getDelegate();
                m_Toolbar.removeAll();
                HashMap<Integer, ButtonGroup> lGroups = new HashMap<Integer, ButtonGroup>();
                for (int i = 0; i < m_buttons.size(); i++) {
                    ToolButtonsItem lButton = m_buttons.get(i);
                    if (lButton != null) {
                        if (lButton.m_Visible) {
                            AbstractButton lbutton = null;
                            if (lButton.m_Toggle) {
                                lbutton = new JToggleButton();
                            } else {
                                lbutton = new JButton();
                            }
                            if (lButton.m_GroupNumber > 0) {
                                ButtonGroup lGroup = lGroups.get(lButton.m_GroupNumber);
                                if (lGroup == null) {
                                    lGroup = new ButtonGroup();
                                    lGroups.put(lButton.m_GroupNumber, lGroup);
                                }
                                lGroup.add(lbutton);
                            }
                            lbutton.setAction(lButton.m_Action);
                            lbutton.setName(lButton.m_Name);
                            lbutton.setText(lButton.m_Caption);

                            if (lButton.m_Hint != null && lButton.m_Hint.equals("")) {
                                lbutton.setToolTipText(null);
                            } else {
                                lbutton.setToolTipText(lButton.m_Hint);
                            }
                            if (lButton.m_PictureFile != null && !lButton.m_PictureFile.isEmpty()) {
                                lbutton.setIcon(AppResourceLoader.getImage(RtIcons.m_iconsPrefix + lButton.m_PictureFile));
                            }
                            lbutton.putClientProperty(FormUtils.SCRIPT_ACTION_NAME, lButton.m_ScriptAction);
                            lbutton.addActionListener(jbean.getEventsHandler());
                            //lbutton.setActionCommand(l_Button.m_ScriptAction);
                            if (lButton.m_Mnemonic > 0) {
                                lbutton.setMnemonic(lButton.m_Mnemonic);
                            }
                            m_Toolbar.add(lbutton);
                        }
                    } else {
                        m_Toolbar.addSeparator();
                    }
                }
            }
        }
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        m_buttons = (LinkedList<ToolButtonsItem>) in.readObject(getPropName(), EMPTY_TOOL_BUTTON_LIST);
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        out.writeObject(getPropName(), m_buttons);
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        for (int i = 0; i < m_buttons.size(); i++) {
            ToolButtonsItem tbi = m_buttons.get(i);
            if (tbi != null) {
                tbi.resolveReferences(jbean, aAllComps, propName);
            }
        }
    }
}

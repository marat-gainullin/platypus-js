/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Mg
 */
public class ToolButtonsItem extends JETAProperty {

    static final long serialVersionUID = 2375974406561274626L;
    /**
     * The current version number of this class
     */
    public static final int VERSION = 4;
    private static final String id_Name_Property = "Name";
    private static final String id_Caption_Property = "Caption";
    private static final String id_Hint_Property = "Hint";
    private static final String id_PictureFile_Property = "PictureFile";
    private static final String id_Action_Property = "ComponentAction";
    private static final String id_ScriptAction_Property = "ScriptAction";
    private static final String id_Toggle_Property = "Toggle";
    private static final String id_ButtonGroup_Property = "ButtonGroup";
    private static final String id_Mnemonic_Property = "Mnemonic";
    private static final String id_MnemonicModifiers_Property = "MnemonicModifiers";
    private static final String id_Visible_Property = "ItemVisible";
    public String m_Name = "btn";
    public String m_Caption = "Button";
    public String m_Hint = null;
    public String m_PictureFile = null;
    public ComponentActionRefProperty m_Action = null;
    public String m_ScriptAction = null;
    public Boolean m_Toggle = false;
    public Integer m_GroupNumber = 0;
    public Integer m_Mnemonic = 0;
    public Integer m_MnemonicModifiers = 0;
    public boolean m_Visible = true;

    public ToolButtonsItem() {
        super();
    }

    public ToolButtonsItem(String aName, String aCaption, String aHint, String aPictureFile,
            String aScriptAction, ComponentActionRefProperty aAction, Boolean aToggle, Integer aGroupNumber,
            Integer aMnemonic, Integer aMnemonicModifiers, boolean aVisible) {
        super();
        m_Name = aName;
        m_Caption = aCaption;
        m_Hint = aHint;
        m_PictureFile = aPictureFile;
        m_Action = new ComponentActionRefProperty();
        m_Action.setValue(aAction);
        m_ScriptAction = aScriptAction;
        m_Toggle = aToggle;
        m_GroupNumber = aGroupNumber;
        m_Mnemonic = aMnemonic;
        m_MnemonicModifiers = aMnemonicModifiers;
        m_Visible = aVisible;
    }

    @Override
    public void setValue(Object obj) {
    }

    @Override
    public void updateBean(JETABean jbean) {
    }

    public ComponentActionRefProperty getComponentActionRef() {
        return m_Action;
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
        super.read(in.getSuperClassInput());
        int version = in.readVersion();
        if (version > 2) {
            m_Name = in.readString(id_Name_Property);
            m_Caption = in.readString(id_Caption_Property);
            m_Hint = in.readString(id_Hint_Property);
            m_PictureFile = in.readString(id_PictureFile_Property);
            m_ScriptAction = in.readString(id_ScriptAction_Property);
            m_Toggle = in.readBoolean(id_Toggle_Property, false);
            m_GroupNumber = in.readInt(id_ButtonGroup_Property, 0);
            m_Mnemonic = in.readInt(id_Mnemonic_Property, 0);
            m_MnemonicModifiers = in.readInt(id_MnemonicModifiers_Property, 0);
            m_Action = (ComponentActionRefProperty) in.readObject(id_Action_Property, ComponentActionRefProperty.EMPTY_ACTION_REF);
            if (version > 3) {
                m_Visible = in.readBoolean(id_Visible_Property, true);
            }
        } else {
            m_Name = (String) in.readObject(id_Name_Property, "");
            m_Caption = (String) in.readObject(id_Caption_Property, "");
            m_Hint = (String) in.readObject(id_Hint_Property, "");
            m_PictureFile = (String) in.readObject(id_PictureFile_Property, "");
            m_ScriptAction = (String) in.readObject(id_ScriptAction_Property, "");
            m_Toggle = (Boolean) in.readObject(id_Toggle_Property, false);
            m_GroupNumber = (Integer) in.readObject(id_ButtonGroup_Property, 0);
            m_Mnemonic = (Integer) in.readObject(id_Mnemonic_Property, 0);
            m_MnemonicModifiers = (Integer) in.readObject(id_MnemonicModifiers_Property, 0);
            if (version > 1) {
                m_Action = (ComponentActionRefProperty) in.readObject(id_Action_Property, ComponentActionRefProperty.EMPTY_ACTION_REF);
            }
        }
    }

    /**
     * JETAPersistable Implementation
     */
    @Override
    public void write(JETAObjectOutput out) throws IOException {
        super.write(out.getSuperClassOutput(JETAProperty.class));
        out.writeVersion(VERSION);
        out.writeString(id_Name_Property, m_Name);
        out.writeString(id_Caption_Property, m_Caption);
        out.writeString(id_Hint_Property, m_Hint);
        out.writeString(id_PictureFile_Property, m_PictureFile);
        out.writeString(id_ScriptAction_Property, m_ScriptAction);
        out.writeBoolean(id_Toggle_Property, m_Toggle);
        out.writeInt(id_ButtonGroup_Property, m_GroupNumber);
        out.writeInt(id_Mnemonic_Property, m_Mnemonic);
        out.writeInt(id_MnemonicModifiers_Property, m_MnemonicModifiers);
        if (VERSION > 1) {
            out.writeObject(id_Action_Property, m_Action);
        }
        if (VERSION > 3) {
            out.writeBoolean(id_Visible_Property, m_Visible);
        }
    }

    @Override
    public void resolveReferences(JETABean jbean, HashMap<Long, GridComponent> aAllComps, String propName) {
        if (m_Action != null) {
            m_Action.resolveReferences(jbean, aAllComps, propName);
        }
    }
}

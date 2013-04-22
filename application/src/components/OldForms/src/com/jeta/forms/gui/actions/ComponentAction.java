/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import com.jeta.forms.store.properties.IDGenerator;
import com.jeta.open.resources.AppResourceLoader;
import com.jeta.open.resources.RtIcons;
import javax.swing.Icon;


/**
 *
 * @author Mg
 */
public class ComponentAction extends AbstractAction{

    Long ID = IDGenerator.genID();
    String Name = "";
    String Caption = "";
    
    public ComponentAction(long aID, String aName, String aCaption)
    {
        super();
        ID = aID;
        Name = aName;
        Caption = aCaption;
    }

    public Icon getIcon() {
        return AppResourceLoader.getImage(RtIcons.m_iconsPrefix + RtIcons.TOOLBAR_16);
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String Caption) {
        this.Caption = Caption;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
      if(Caption != null && Caption.length() > 0)
        return Caption;
      else
        return Name;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {  
        
    }

}

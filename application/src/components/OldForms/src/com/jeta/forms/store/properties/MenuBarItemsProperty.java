/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.properties;

import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormUtils;
import com.jeta.open.resources.AppResourceLoader;
import com.jeta.open.resources.RtIcons;
import java.awt.Dimension;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;

/**
 *
 * @author Marat
 */
public class MenuBarItemsProperty extends MenuItemsProperty{
    
   static final long serialVersionUID = 2375434415791274626L;
   /**
    * The current version number of this class
    */
   public static final int VERSION = 1;
   
   public MenuBarItemsProperty()
   {
       super();
   }
   
   
    @Override
    public void updateBean(JETABean jbean) {
        if(jbean != null)
        {
            if (jbean.getDelegate() != null && jbean.getDelegate() instanceof JMenuBar)
            {
                JMenuBar lMenu = (JMenuBar)jbean.getDelegate();
                lMenu.removeAll();  
                for(int i=0;i<m_buttons.size();i++)
                {
                    MenuItemsItem lMenuItem = (MenuItemsItem)m_buttons.get(i);
                    if(lMenuItem != null)
                    {
                        JMenu lMenubarItem = new JMenu();
                        lMenubarItem.setAction(lMenuItem.m_Action);
                        lMenubarItem.setName(lMenuItem.m_Name);
                        if(lMenuItem.m_Caption != null && !lMenuItem.m_Caption.isEmpty() &&
                           lMenuItem.m_PictureFile != null && !lMenuItem.m_PictureFile.isEmpty())
                            lMenubarItem.setText(lMenuItem.m_Caption+"      ");
                        else
                            lMenubarItem.setText(lMenuItem.m_Caption);
                        if(lMenuItem.m_Hint != null && lMenuItem.m_Hint.equals(""))
                            lMenubarItem.setToolTipText(null);
                        else
                            lMenubarItem.setToolTipText(lMenuItem.m_Hint);
                        if(lMenuItem.m_PictureFile != null && !lMenuItem.m_PictureFile.isEmpty())
                            lMenubarItem.setIcon(AppResourceLoader.getImage(RtIcons.m_iconsPrefix + lMenuItem.m_PictureFile));
                        lMenubarItem.putClientProperty(FormUtils.SCRIPT_ACTION_NAME, lMenuItem.m_ScriptAction);
                        lMenubarItem.addActionListener(jbean.getEventsHandler());
                        lMenu.add(lMenubarItem);
                        addRecursive2Bean(lMenubarItem, lMenuItem.m_Children, jbean);
                    }else
                    {
                        JSeparator lMenubarItem = new JSeparator();
                        lMenubarItem.setFocusable(false);
                        lMenubarItem.setEnabled(false);
                        //if(lMenu.getOrientation() == 0)
                        //{
                        lMenubarItem.setOrientation(1);
                        Dimension d = new Dimension(3, lMenubarItem.getPreferredSize().width);
                        lMenubarItem.setSize(d);
                        lMenubarItem.setPreferredSize(d);
                        lMenubarItem.setMinimumSize(d);
                        //}else
                        //{
                        //lMenubarItem.setOrientation(0);
//                        Dimension d = new Dimension(3, lMenubarItem.getPreferredSize().width);
//                        lMenubarItem.setSize(d);
//                        lMenubarItem.setPreferredSize(d);
//                        lMenubarItem.setMinimumSize(d);
                        //}
                        lMenu.add(lMenubarItem);
                    }
                }
            }
        }
    }

}

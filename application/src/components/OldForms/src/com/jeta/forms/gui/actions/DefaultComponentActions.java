/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jeta.forms.gui.actions;

import com.jeta.forms.gui.form.GridComponent;
import com.jeta.open.i18n.I18N;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class PopupRunAction extends ComponentAction
{

    PopupRunAction(long aID, String aName, String aCaption)
    {
        super(aID, aName, aCaption);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e != null && e.getSource() != null && e instanceof ComponentActionEvent)
        {
            ComponentActionEvent ca = (ComponentActionEvent) e;
            GridComponent gc = (GridComponent) ca.getActionSubject();
            if (gc != null && gc.getBeanDelegate() != null &&
                    gc.getBeanDelegate() instanceof JPopupMenu)
            {
                JPopupMenu ppm = (JPopupMenu) gc.getBeanDelegate();
                if (ca.getSource() != null && ca.getSource() instanceof Component)
                {
                    Component lcomp = (Component) ca.getSource();
                    Point mousePos = lcomp.getMousePosition();
                    if(!(lcomp instanceof JMenuItem))
                    {
                        if (mousePos != null && lcomp.isShowing())
                        {
                            ppm.show(lcomp, mousePos.x, mousePos.y);
                        }
                        else
                        {
                            mousePos = MouseInfo.getPointerInfo().getLocation();
                            ppm.show(null, mousePos.x, mousePos.y);
                        }
                    }
                }
            }
        }
    }
}

/**
 *
 * @author Marat
 */
public class DefaultComponentActions
{

    static HashMap<Class, LinkedList<Action>> defaulActions = new HashMap<Class, LinkedList<Action>>();


    static
    {
        LinkedList<Action> pa = new LinkedList<Action>();
        pa.add(new PopupRunAction(650183170575743L, "run", I18N.getLocalizedMessage("run")));
        //pa.add(new PopupRunAction(650183170570043L, "run1",I18N.getLocalizedMessage("run1")));
        //ll.add(new ...
        defaulActions.put(JPopupMenu.class, pa);
    // ...
    //defaulActions.put(JPopupMenu.class, other actions);
    }

    static public LinkedList<Action> getActions(Class aClass)
    {
        return defaulActions.get(aClass);
    }
}

package com.eas.client.utils.scalableui;

import java.awt.Component;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 *
 * @author Marat
 */
public class ScalablePopupFactory extends PopupFactory{

    protected ScalablePopup sharedPopup = new ScalablePopup();
    protected PopupFactory previousFactory;
    
    public ScalablePopupFactory(PopupFactory aPrevFactory)
    {
        super();
        previousFactory = aPrevFactory;
    }
    
    public static JScalablePanel iifScalable(Component aComp)
    {
        Component lParent = aComp;
        while(lParent != null && !(lParent instanceof JScalablePanel))
            lParent = lParent.getParent();
        if(lParent != null && lParent instanceof JScalablePanel)
            return (JScalablePanel)lParent;
        return null;
    }
    
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
        JScalablePanel sp = iifScalable(owner);
        if(sp != null)
        {
            if(contents instanceof ScalableComboPopup)
            {
                sharedPopup.init4Combo(owner, (ScalableComboPopup)contents, x, y);
                return sharedPopup;
            /*
            }else if(contents instanceof JPopupMenu)
            {
                sharedPopup.init4Menu(owner, (JPopupMenu)contents, x, y);
                return sharedPopup;
             */
            }else
            {
                if(previousFactory != null)
                    return previousFactory.getPopup(owner, contents, x, y);
                else
                    return super.getPopup(owner, contents, x, y);
            }
        }else
        {
            if(previousFactory != null)
                return previousFactory.getPopup(owner, contents, x, y);
            else
                return super.getPopup(owner, contents, x, y);
        }
    }

}

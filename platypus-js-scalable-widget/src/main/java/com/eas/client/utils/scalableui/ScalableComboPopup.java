package com.eas.client.utils.scalableui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 *
 * @author Marat
 */
public class ScalableComboPopup extends BasicComboPopup {
    
    JScalablePanel scalablePane = null;
    protected boolean oldOpaque = false;
    
    public ScalableComboPopup(JComboBox aCombo)
    {
        super(aCombo);
        scalablePane = ZoomRepaintManager.getPanelContainer(aCombo);
    }

    @Override
    public void setVisible(boolean b) {
        if(b)
            super.setVisible(b);
        else
        {
            AWTEvent evt = EventQueue.getCurrentEvent();
            if(evt == null || !(evt.getSource() instanceof DrawWallPanel) ||
                    ((JScalablePanel)((DrawWallPanel)evt.getSource()).getParent()).isForceHidePopups() )
                super.setVisible(b);
        }
    }

    public void onInstall()
    {
        JViewport viewport = scroller.getViewport();
        if(viewport != null)
        {
            Component lview = viewport.getView();
            if(lview != null && lview instanceof JComponent)
            {
                JComponent ljcview = (JComponent)lview;
                oldOpaque = ljcview.isOpaque();
                ljcview.setOpaque(false);
            }
        }
    }
        
    public void onUninstall()
    {
        JViewport viewport = scroller.getViewport();
        if(viewport != null)
        {
            Component lview = viewport.getView();
            if(lview != null && lview instanceof JComponent)
            {
                JComponent ljcview = (JComponent)lview;
                oldOpaque = ljcview.isOpaque();
                ljcview.setOpaque(oldOpaque);
            }
        }
    }

}

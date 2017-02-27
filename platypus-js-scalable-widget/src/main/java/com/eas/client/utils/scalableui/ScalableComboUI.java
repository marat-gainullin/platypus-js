package com.eas.client.utils.scalableui;

import javax.swing.JComponent;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

/**
 *
 * @author Marat
 */
public class ScalableComboUI extends BasicComboBoxUI{
    
    ComboBoxUI delegate = null;
    boolean isOpaque = false;
    
    public ScalableComboUI(ComboBoxUI aDelegate)
    {
        super();
        delegate = aDelegate;
    }

    @Override
    public void installUI(JComponent a) {
        super.installUI(a);
        if(popup instanceof ScalableComboPopup)
        {
            ((ScalableComboPopup)popup).onInstall();
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        if(popup instanceof ScalableComboPopup)
        {
            ((ScalableComboPopup)popup).onUninstall();
        }
    }

    @Override
    protected ComboPopup createPopup() {
        return new ScalableComboPopup(comboBox);
    }
   

}

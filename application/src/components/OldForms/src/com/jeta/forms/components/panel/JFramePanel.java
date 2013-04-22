/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.components.panel;

import com.jeta.forms.gui.common.FormUtils;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.open.i18n.I18N;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author mg
 */
public class JFramePanel extends JPanel{

    public static final String FORM_MISSING_LABEL = I18N.getLocalizedMessage("frameFormMissing");

    public JFramePanel()
    {
        super();
    }

    public FormComponent getFormComponent() {
        if(getComponentCount() > 0)
        {
            Component comp = getComponent(0);
            if(comp != null && comp instanceof FormComponent)
                return (FormComponent)comp;
        }
        return null;
    }

    @Override
    protected void paintChildren(Graphics g) {
        if(getComponentCount() == 0 && FormUtils.isDesignMode())
        {
            Color bk = UIManager.getColor("control");
            if(bk == null)
                bk = Color.gray;
            else
                bk = bk.darker();
            g.setColor(bk);
            Dimension ls = getSize();
            g.drawRect(0, 0, ls.width-1, ls.height-1);
            g.fillRect(5, 5, ls.width-10, ls.height-10);
            g.setColor(bk.brighter());
            g.drawString(FORM_MISSING_LABEL, 10, ls.height/2);
        }
        super.paintChildren(g);
    }

}

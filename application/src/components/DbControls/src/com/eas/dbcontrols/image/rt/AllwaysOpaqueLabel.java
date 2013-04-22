/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.image.rt;

import javax.swing.JLabel;

/**
 *
 * @author Марат
 */
public class AllwaysOpaqueLabel extends JLabel{

    public AllwaysOpaqueLabel() {
        super();
        super.setOpaque(true);
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(true);
    }

}

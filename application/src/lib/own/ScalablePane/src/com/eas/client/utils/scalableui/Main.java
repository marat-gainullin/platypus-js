/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.scalableui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Mg
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        //UIManager.setLookAndFeel("org.fife.plaf.OfficeXP.OfficeXPLookAndFeel");
        //UIManager.setLookAndFeel("com.pagosoft.plaf.PgsLookAndFeel");
        //UIManager.setLookAndFeel("net.beeger.squareness.SquarenessLookAndFeel");
        //UIManager.setLookAndFeel("com.easynth.lookandfeel.EaSynthLookAndFeel");
        //UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
        DemoFrame frm = new DemoFrame();
        frm.setVisible(true);
    }

}

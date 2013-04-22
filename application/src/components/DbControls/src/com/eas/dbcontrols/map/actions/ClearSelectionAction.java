/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.actions;

import com.eas.client.controls.geopane.actions.GeoPaneAction;
import com.eas.dbcontrols.map.DbMap;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class ClearSelectionAction extends GeoPaneAction {

    protected DbMap map;

    public ClearSelectionAction(DbMap aMap) {
        super(aMap.getPane());
        map = aMap;
        putValue(Action.NAME, getClass().getSimpleName());
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    }

    public void actionPerformed(ActionEvent e) {
        map.getSelection().clear();
    }
}

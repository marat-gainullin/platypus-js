/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.open.support;

import javax.swing.JComponent;

/**
 *
 * @author Marat
 */
/* has only one function "doWork", that performs any work on 
   iterated object and return if that object has inner
   structure and has it to be iterated through*/
public abstract class IteratedAction {
    public boolean doWork(JComponent comp)
    {
        return true;
    }
}

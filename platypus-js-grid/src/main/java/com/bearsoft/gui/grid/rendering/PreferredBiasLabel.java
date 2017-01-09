/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import java.awt.Dimension;

/**
 *
 * @author Gala
 */
public class PreferredBiasLabel extends NonRepaintableLabel {

    protected int bias = 0;

    public PreferredBiasLabel(boolean aRepaintable)
    {
        super(aRepaintable);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension ps = super.getPreferredSize();
        ps.width += bias;
        return ps;
    }

    public int getBias() {
        return bias;
    }

    public void setBias(int aValue) {
        bias = aValue;
        invalidate();
    }
}

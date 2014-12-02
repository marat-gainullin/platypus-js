/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.forms.events.factories;

import com.eas.controls.layouts.constraints.*;
import com.eas.client.forms.layouts.MarginConstraints;
import java.awt.GridBagConstraints;

/**
 *
 * @author mg
 */
public class LayoutConstraintsFactory implements ConstraintsDesignInfoVisitor{

    private Object result;

    public Object getResult() {
        return result;
    }

    @Override
    public void visit(BorderLayoutConstraintsDesignInfo aInfo) {
        result = aInfo.getPlace();
    }

    @Override
    public void visit(CardLayoutConstraintsDesignInfo aInfo) {
        result = aInfo.getCardName();
    }

    @Override
    public void visit(GridBagLayoutConstraintsDesignInfo aInfo) {
        result = new GridBagConstraints(
                aInfo.getGridx(), aInfo.getGridy(),
                aInfo.getGridwidth(), aInfo.getGridheight(),
                aInfo.getWeightx(), aInfo.getWeighty(),
                aInfo.getAnchor(),
                aInfo.getFill(),
                aInfo.getInsets(),
                aInfo.getIpadx(), aInfo.getIpady());
    }

    @Override
    public void visit(TabsConstraintsDesignInfo aInfo) {
    }

    @Override
    public void visit(AbsoluteConstraintsDesignInfo aInfo) {
        result = null;
    }

    @Override
    public void visit(LayersLayoutConstraintsDesignInfo aInfo) {
        result = aInfo.getLayer();
    }
    
    @Override
    public void visit(MarginConstraintsDesignInfo aInfo) {
        result = new MarginConstraints(
                aInfo.getMarginLeft(),
                aInfo.getMarginTop(),
                aInfo.getMarginRight(),
                aInfo.getMarginBottom(),
                aInfo.getMarginWidth(),
                aInfo.getMarginHeight()
                );
    }

}

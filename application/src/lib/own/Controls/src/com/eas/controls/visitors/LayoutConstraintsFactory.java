/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls.visitors;

import com.eas.controls.layouts.constraints.*;
import com.eas.controls.layouts.margin.MarginConstraints;
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

    public void visit(BorderLayoutConstraintsDesignInfo aInfo) {
        result = aInfo.getPlace();
    }

    public void visit(CardLayoutConstraintsDesignInfo aInfo) {
        result = aInfo.getCardName();
    }

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

    public void visit(TabsConstraintsDesignInfo aInfo) {
    }

    public void visit(AbsoluteConstraintsDesignInfo aInfo) {
        result = null;
    }

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

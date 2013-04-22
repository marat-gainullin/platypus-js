/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts.constraints;

/**
 *
 * @author mg
 */
public interface ConstraintsDesignInfoVisitor {

    // layout constraints
    public void visit(BorderLayoutConstraintsDesignInfo aInfo);

    public void visit(CardLayoutConstraintsDesignInfo aInfo);

    public void visit(GridBagLayoutConstraintsDesignInfo aInfo);

    // non-layout control's constraints
    public void visit(TabsConstraintsDesignInfo aInfo);

    public void visit(AbsoluteConstraintsDesignInfo aInfo);

    public void visit(LayersLayoutConstraintsDesignInfo aInfo);
    
    //пользовательские constraints
    public void visit(MarginConstraintsDesignInfo aInfo);
}

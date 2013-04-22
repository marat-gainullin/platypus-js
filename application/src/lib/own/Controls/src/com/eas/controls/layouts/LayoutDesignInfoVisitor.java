/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

/**
 *
 * @author mg
 */
public interface LayoutDesignInfoVisitor {

    // layouts
    public void visit(BorderLayoutDesignInfo aInfo);

    public void visit(BoxLayoutDesignInfo aInfo);

    public void visit(CardLayoutDesignInfo aInfo);

    public void visit(FlowLayoutDesignInfo aInfo);

    public void visit(GridBagLayoutDesignInfo aInfo);

    public void visit(GridLayoutDesignInfo aInfo);

    public void visit(GroupLayoutDesignInfo aInfo);

    public void visit(GroupLayoutGroupDesignInfo aInfo);

    public void visit(AbsoluteLayoutDesignInfo aInfo);
    
    //пользовательские Layouts
    public void visit(MarginLayoutDesignInfo aInfo);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

/**
 *
 * @author mg
 */
public interface BorderDesignInfoVisitor {
    public void visit(BevelBorderDesignInfo aInfo);
    public void visit(CompoundBorderDesignInfo aInfo);    
    public void visit(EmptyBorderDesignInfo aInfo);
    public void visit(EtchedBorderDesignInfo aInfo);
    public void visit(LineBorderDesignInfo aInfo);
    public void visit(MatteBorderDesignInfo aInfo);
    public void visit(SoftBevelBorderDesignInfo aInfo);
    public void visit(TitledBorderDesignInfo aInfo);
}

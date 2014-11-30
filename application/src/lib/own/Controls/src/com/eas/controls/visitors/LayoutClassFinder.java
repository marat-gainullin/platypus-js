/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.visitors;

import com.eas.controls.layouts.*;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.layouts.PlatypusCardLayout;
import java.awt.*;
import javax.swing.GroupLayout;

/**
 *
 * @author mg
 */
public class LayoutClassFinder implements LayoutDesignInfoVisitor {

    protected Class result = null;

    public static Class find(LayoutDesignInfo aInfo) {
        if (aInfo != null) {
            LayoutClassFinder finder = new LayoutClassFinder();
            aInfo.accept(finder);
            return finder.result;
        } else {
            return null;
        }
    }

    @Override
    public void visit(BorderLayoutDesignInfo aInfo) {
        result = BorderLayout.class;
    }

    @Override
    public void visit(BoxLayoutDesignInfo aInfo) {
        result = BoxLayout.class;
    }

    @Override
    public void visit(CardLayoutDesignInfo aInfo) {
        result = PlatypusCardLayout.class;
    }

    @Override
    public void visit(FlowLayoutDesignInfo aInfo) {
        result = FlowLayout.class;
    }

    @Override
    public void visit(GridBagLayoutDesignInfo aInfo) {
        result = GridBagLayout.class;
    }

    @Override
    public void visit(GridLayoutDesignInfo aInfo) {
        result = GridLayout.class;
    }

    @Override
    public void visit(GroupLayoutDesignInfo aInfo) {
        result = GroupLayout.class;
    }

    @Override
    public void visit(GroupLayoutGroupDesignInfo aInfo) {
    }

    @Override
    public void visit(AbsoluteLayoutDesignInfo aInfo) {
        result = null;
    }
    
    @Override
    public void visit(MarginLayoutDesignInfo aInfo) {
        result = MarginLayout.class;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.designer;

import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.*;
import com.eas.controls.layouts.*;

/**
 *
 * @author mg
 */
public class SimpleLayoutFactory implements LayoutDesignInfoVisitor {

    protected LayoutSupportDelegate result;

    public LayoutSupportDelegate getResult() {
        return result;
    }

    @Override
    public void visit(BorderLayoutDesignInfo bldi) {
        result = new BorderLayoutSupport();
    }

    @Override
    public void visit(BoxLayoutDesignInfo bldi) {
        result = new BoxLayoutSupport();
    }

    @Override
    public void visit(CardLayoutDesignInfo cldi) {
        result = new CardLayoutSupport();
    }

    @Override
    public void visit(FlowLayoutDesignInfo fldi) {
        result = new FlowLayoutSupport();
    }

    @Override
    public void visit(GridBagLayoutDesignInfo gbldi) {
    }

    @Override
    public void visit(GridLayoutDesignInfo gldi) {
        result = new GridLayoutSupport();
    }

    @Override
    public void visit(GroupLayoutDesignInfo gldi) {
        throw new UnsupportedOperationException("Group layout must be processed in special way");
    }

    @Override
    public void visit(GroupLayoutGroupDesignInfo glgdi) {
        throw new UnsupportedOperationException("Group layout groups must be processed in special way");
    }

    @Override
    public void visit(AbsoluteLayoutDesignInfo aldi) {
        result = new AbsoluteLayoutSupport();
    }

    @Override
    public void visit(MarginLayoutDesignInfo aldi) {
        result = new MarginLayoutSupport();
    }
}

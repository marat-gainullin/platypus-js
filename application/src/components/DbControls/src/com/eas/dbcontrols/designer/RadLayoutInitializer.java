/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.controls.layouts.*;
import java.util.Map;

/**
 *
 * @author mg
 */
public class RadLayoutInitializer extends RadInitializer implements LayoutDesignInfoVisitor {

    public RadLayoutInitializer(RADComponent<?> aComponent, Map<String, RADComponent<?>> aComponents) {
        super(aComponent, aComponents);
    }

    @Override
    public void visit(BorderLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(BoxLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(CardLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(FlowLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(GridBagLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(GridLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(GroupLayoutDesignInfo di) {
        initializeProperties(di);
    }

    @Override
    public void visit(GroupLayoutGroupDesignInfo glgdi) {
    }

    @Override
    public void visit(AbsoluteLayoutDesignInfo aldi) {
    }

    @Override
    public void visit(MarginLayoutDesignInfo aldi) {
    }
}

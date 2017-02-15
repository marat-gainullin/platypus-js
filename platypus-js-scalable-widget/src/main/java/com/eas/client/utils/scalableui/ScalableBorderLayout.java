package com.eas.client.utils.scalableui;

import java.awt.BorderLayout;
import java.awt.Component;

/**
 *
 * @author Marat
 */
public class ScalableBorderLayout extends BorderLayout{

    public ScalableBorderLayout() {
        super();
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if(!(comp instanceof ScalablePopup.ScalablePopupPanel))
            super.addLayoutComponent(comp, constraints);
    }

    
}

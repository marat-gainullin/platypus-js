/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */

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

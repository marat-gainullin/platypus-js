/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.store.PropertiesSimpleFactory;

/**
 *
 * @author mg
 */
public class BorderPropertyFactory implements PropertiesSimpleFactory {

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        if (BevelBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new BevelBorderDesignInfo();
        } else if (CompoundBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new CompoundBorderDesignInfo();
        } else if (EmptyBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new EmptyBorderDesignInfo();
        } else if (EtchedBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new EtchedBorderDesignInfo();
        } else if (LineBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new LineBorderDesignInfo();
        } else if (MatteBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MatteBorderDesignInfo();
        } else if (SoftBevelBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new SoftBevelBorderDesignInfo();
        } else if (TitledBorderDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new TitledBorderDesignInfo();
        } else {
            return null;
        }
    }
}

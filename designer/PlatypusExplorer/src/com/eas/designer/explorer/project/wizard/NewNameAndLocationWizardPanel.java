/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

/**
 *
 * @author mg
 */
public class NewNameAndLocationWizardPanel extends NameAndLocationWizardPanel{

    @Override
    protected NameAndLocationWizardPanelVisual createComponent() {
        return new NewNameAndLocationWizardPanelVisual(this);
    }
    
}

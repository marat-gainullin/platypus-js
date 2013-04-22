/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.controls.containers.*;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;

/**
 *
 * @author mg
 */
public interface ControlsDesignInfoVisitor {

    // plain controls
    public void visit(LabelDesignInfo aInfo);

    public void visit(ButtonDesignInfo aInfo);

    public void visit(DropDownButtonDesignInfo aInfo);
    
    public void visit(ButtonGroupDesignInfo aInfo);

    public void visit(CheckDesignInfo aInfo);

    public void visit(EditorPaneDesignInfo aInfo);

    public void visit(TextPaneDesignInfo aInfo);
    
    public void visit(FormattedFieldDesignInfo aInfo);

    public void visit(PasswordFieldDesignInfo aInfo);

    public void visit(ProgressBarDesignInfo aInfo);

    public void visit(RadioDesignInfo aInfo);

    public void visit(SliderDesignInfo aInfo);

    public void visit(TextFieldDesignInfo aInfo);

    public void visit(ToggleButtonDesignInfo aInfo);

    // form - top-level container
    public void visit(FormDesignInfo aInfo);

    // containers
    public void visit(DesktopDesignInfo aInfo);

    public void visit(LayersDesignInfo aInfo);

    public void visit(PanelDesignInfo aInfo);

    public void visit(ScrollDesignInfo aInfo);

    public void visit(SplitDesignInfo aInfo);

    public void visit(TabsDesignInfo aInfo);

    public void visit(ToolbarDesignInfo aInfo);

    // menus
    public void visit(MenuCheckItemDesignInfo aInfo);

    public void visit(MenuDesignInfo aInfo);

    public void visit(MenuItemDesignInfo aInfo);

    public void visit(MenuRadioItemDesignInfo aInfo);

    public void visit(MenuSeparatorDesignInfo aInfo);

    public void visit(MenubarDesignInfo aInfo);

    public void visit(PopupDesignInfo aInfo);
}

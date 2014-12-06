/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.*;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.containers.*;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;

/**
 *
 * @author mg
 */
public class HiddenLayoutFactory implements ControlsDesignInfoVisitor {
    public static final String ONLY_CONTAINERS_ARE_SUPPORTED_MSG = "Only containers are supported.";

    protected LayoutSupportDelegate result;

    public LayoutSupportDelegate getResult() {
        return result;
    }

    @Override
    public void visit(LabelDesignInfo ldi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(ButtonDesignInfo bdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(DropDownButtonDesignInfo ddbdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(ButtonGroupDesignInfo bgdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(CheckDesignInfo cdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(TextPaneDesignInfo epdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(EditorPaneDesignInfo epdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(FormattedFieldDesignInfo ffdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(PasswordFieldDesignInfo pfdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(ProgressBarDesignInfo pbdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(RadioDesignInfo rdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(SliderDesignInfo sdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(TextFieldDesignInfo tfdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(ToggleButtonDesignInfo tbdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(FormDesignInfo fdi) {
    }

    @Override
    public void visit(PanelDesignInfo pdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(DesktopDesignInfo ddi) {
        result = new JLayeredPaneSupport();
    }

    @Override
    public void visit(LayersDesignInfo ldi) {
        result = new JLayeredPaneSupport();
    }

    @Override
    public void visit(ScrollDesignInfo sdi) {
        result = new JScrollPaneSupport();
    }

    @Override
    public void visit(SplitDesignInfo sdi) {
        result = new JSplitPaneSupport();
    }

    @Override
    public void visit(TabsDesignInfo tdi) {
        result = new JTabbedPaneSupport();
    }

    @Override
    public void visit(ToolbarDesignInfo tdi) {
        result = new JToolBarSupport();
    }

    @Override
    public void visit(MenuCheckItemDesignInfo mcidi) {
    }

    @Override
    public void visit(MenuDesignInfo mdi) {
    }

    @Override
    public void visit(MenuItemDesignInfo midi) {
    }

    @Override
    public void visit(MenuRadioItemDesignInfo mridi) {
    }

    @Override
    public void visit(MenuSeparatorDesignInfo msdi) {
    }

    @Override
    public void visit(MenubarDesignInfo mdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }

    @Override
    public void visit(PopupDesignInfo pdi) {
        throw new UnsupportedOperationException(ONLY_CONTAINERS_ARE_SUPPORTED_MSG);
    }
}

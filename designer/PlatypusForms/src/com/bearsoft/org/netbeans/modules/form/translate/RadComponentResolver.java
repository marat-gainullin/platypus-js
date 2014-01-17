/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.RADVisualFormContainer;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.JSplitPaneSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.JSplitPaneSupport.SplitLayoutConstraints;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.HtmlContentEditorKit;
import com.eas.controls.containers.*;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

/**
 *
 * @author mg
 */
public class RadComponentResolver extends RadInitializer implements DbControlsDesignInfoVisitor {

    protected DesignInfo designInfo;
    protected Map<String, ControlDesignInfo> designInfos;

    public RadComponentResolver(RADComponent<?> aComponent, ControlDesignInfo aDesignInfo, Map<String, RADComponent<?>> aComponents, Map<String, ControlDesignInfo> aDesignInfos) {
        super(aComponent, aComponents);
        designInfo = aDesignInfo;
        designInfos = aDesignInfos;
    }

    @Override
    public void visit(DbCheckDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    protected RADComponent<?> findParent(ControlDesignInfo cdi) {
        if (cdi instanceof ControlDesignInfo) {
            return components.get(cdi.getParent());
        } else {
            return null;
        }
    }

    public void resolveParent(ControlDesignInfo cdi) {
        assert designInfo == cdi;
        RADComponent<?> parentComp = findParent(cdi);
        if (parentComp instanceof RADVisualContainer<?>) {
            RADVisualContainer<?> parent = (RADVisualContainer<?>) parentComp;
            DesignInfo parentCdi = designInfos.get(parent.getName());
            if (JSplitPane.class.isAssignableFrom(parentComp.getBeanClass())) {
                assert parentCdi instanceof SplitDesignInfo;
                SplitDesignInfo di = (SplitDesignInfo) parentCdi;
                // Let's construct split layout constraints

                RADComponent<?> leftcomp = components.get(di.getLeftComponent());
                RADComponent<?> rightcomp = components.get(di.getRightComponent());

                if (component == leftcomp) {
                    assert leftcomp instanceof RADVisualComponent<?>;
                    RADVisualComponent<?> leftVisualComp = (RADVisualComponent<?>) leftcomp;
                    SplitLayoutConstraints leftConstraints = null;
                    if (di.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
                        leftConstraints = new SplitLayoutConstraints(JSplitPane.TOP);
                    } else {
                        leftConstraints = new SplitLayoutConstraints(JSplitPane.LEFT);
                    }
                    leftVisualComp.setLayoutConstraints(JSplitPaneSupport.class, leftConstraints);
                } else if (component == rightcomp) {
                    assert rightcomp instanceof RADVisualComponent<?>;
                    RADVisualComponent<?> rightVisualComp = (RADVisualComponent<?>) rightcomp;
                    SplitLayoutConstraints rightConstraints = null;
                    if (di.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
                        rightConstraints = new SplitLayoutConstraints(JSplitPane.BOTTOM);
                    } else {
                        rightConstraints = new SplitLayoutConstraints(JSplitPane.RIGHT);
                    }
                    rightVisualComp.setLayoutConstraints(JSplitPaneSupport.class, rightConstraints);
                }
            } else if (JScrollPane.class.isAssignableFrom(parentComp.getBeanClass())) {
                assert component instanceof RADVisualComponent<?>;
                RADVisualComponent<?> visualComp = (RADVisualComponent<?>) component;
                visualComp.getBeanInstance().setSize(cdi.getDesignedPreferredSize());
            }
            if (component instanceof RADVisualComponent<?>) {
                if (cdi.getDesignedPreferredSize() != null) {
                    ((RADVisualComponent<?>) component).getBeanInstance().setSize(cdi.getDesignedPreferredSize());
                }
                if (parent.getLayoutSupport() != null
                        && parent.getLayoutSupport().getLayoutDelegate() != null) {
                    parent.getFormModel().addVisualComponent((RADVisualComponent<?>) component, parent, -1, ((RADVisualComponent<?>) component).getLayoutConstraints(parent.getLayoutSupport().getLayoutDelegate().getClass()), false);
                } else {
                    component.setParent(parent);
                    parent.add((RADVisualComponent<?>) component);
                }
            }
        }
    }

    @Override
    public void visit(DbComboDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbDateDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbImageDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbLabelDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbSchemeDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbSpinDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbTextDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbGridDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DbMapDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ButtonDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(DropDownButtonDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(LabelDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ButtonGroupDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
    }

    @Override
    public void visit(CheckDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(TextPaneDesignInfo di) {
        ((JTextPane) component.getBeanInstance()).setContentType("text/plain");
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(EditorPaneDesignInfo di) {
        ((JEditorPane) component.getBeanInstance()).setEditorKitForContentType("text/html", new HtmlContentEditorKit());
        ((JEditorPane) component.getBeanInstance()).setContentType("text/html");
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(FormattedFieldDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(PasswordFieldDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ProgressBarDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(RadioDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(SliderDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(TextFieldDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ToggleButtonDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(FormDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        RADComponent<?> resolved = components.get(di.getJMenuBar());
        if (resolved != null && component instanceof RADVisualFormContainer) {
            RADVisualFormContainer formComp = (RADVisualFormContainer) component;
            // TODO: inspect jmenubar properties of RADComponent
            // May be this is not neccesary, because Matisse threats some subcomponent as
            // container's menu
        }
    }

    @Override
    public void visit(DesktopDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(LayersDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(PanelDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ScrollDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(SplitDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
        // chldren constraints resolving is in resolve parent.
    }

    @Override
    public void visit(TabsDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(ToolbarDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenuCheckItemDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenuDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenuItemDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenuRadioItemDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenuSeparatorDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(MenubarDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
        resolveParent(di);
    }

    @Override
    public void visit(PopupDesignInfo di) {
        initializeProperties(di);
        initializeEvents(di);
    }
}

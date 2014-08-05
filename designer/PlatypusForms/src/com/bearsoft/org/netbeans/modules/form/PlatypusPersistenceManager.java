/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MenuFakeSupport;
import com.bearsoft.org.netbeans.modules.form.translate.*;
import com.eas.client.forms.DbFormDesignInfo;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.layouts.GridBagLayoutDesignInfo;
import com.eas.controls.layouts.GroupLayoutDesignInfo;
import com.eas.controls.layouts.LayoutDesignInfo;
import com.eas.controls.layouts.MarginLayoutDesignInfo;
import com.eas.controls.menus.MenubarDesignInfo;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.design.Designable;
import com.eas.designer.application.PlatypusUtils;
import com.eas.store.Object2Dom;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.Image;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import org.openide.ErrorManager;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class PlatypusPersistenceManager extends PersistenceManager {

    static final String XML_FORM = "layout"; // NOI18N

    public void saveRadComponentLayout(ContainerDesignInfo aContainerDi, RADVisualContainer<?> aContainer) throws Exception {
        assert aContainer instanceof RADVisualContainer<?>;
        RADVisualContainer<?> radContainer = (RADVisualContainer<?>) aContainer;
        DesignInfo layoutDesignInfo = null;
        // Case of simple swing layout
        if (radContainer.getLayoutSupport() != null && radContainer.getLayoutSupport().getLayoutDelegate() != null && radContainer.getLayoutSupport().getLayoutDelegate().getSupportedClass() != null) {
            layoutDesignInfo = DesignInfoFactory.create(radContainer.getLayoutSupport().getLayoutDelegate().getSupportedClass());
            DesignInfoFactory.initWithProperties(layoutDesignInfo, radContainer.getLayoutSupport().getAllProperties());
        }
        // There is possible situation when layout is defined implicitly.
        // E.g Layout is absent in swing, but it's very handy to
        // have layout-like constraints.
        // Such cases are: JSplitPane, JTabbedPane (each tab's icon, title, etc), etc.
        if (layoutDesignInfo instanceof LayoutDesignInfo) {
            aContainerDi.setLayout((LayoutDesignInfo) layoutDesignInfo);
        }
    }

    @Override
    public boolean canLoadForm(PlatypusFormDataObject formObject) throws PersistenceException {
        return true;
    }

    public static class PlatypusFrame extends JFrame {

        protected boolean minimizable = true;
        protected boolean maximizable = true;

        public PlatypusFrame() {
            super();
        }

        @Override
        @Designable(displayName = "icon", description = "Window's icon")
        public Image getIconImage() {
            return super.getIconImage();
        }

        public boolean isMinimizable() {
            return minimizable;
        }

        public void setMinimizable(boolean aValue) {
            minimizable = aValue;
        }

        public boolean isMaximizable() {
            return maximizable;
        }

        public void setMaximizable(boolean aValue) {
            maximizable = aValue;
        }
    }

    @Override
    public void loadForm(PlatypusFormDataObject formObject, FormModel formModel, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            formModel.setFormBaseClass(PlatypusFrame.class); //Only for design purposes. At runtime it will be any of swing top-level containers
            formModel.setName(formObject.getName());
            RADComponent<?> topComp = formModel.getTopRADComponent();
            assert topComp instanceof RADVisualFormContainer;
            RADVisualFormContainer formComp = (RADVisualFormContainer) topComp;
            // Let's load form structure
            String formContent = formObject.getFormFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
            Document doc = Source2XmlDom.transform(formContent);
            FormDesignInfo formInfo = new DbFormDesignInfo();
            Object2Dom.transform(formInfo, doc);

            // We must preserve components order to save structure of layouts such as grid layout and etc.
            List<RADComponent<?>> createdComponents = new ArrayList<>();
            Map<String, ControlDesignInfo> fiComponents = new HashMap<>();
            Map<String, RADComponent<?>> radComponents = new HashMap<>();
            // visual components
            for (ControlDesignInfo cInfo : formInfo.getChildren()) {
                // Factory will take care of properly classes creation,
                // properties and events
                RadComponentFactory rFactory = new RadComponentFactory(formModel);
                cInfo.accept(rFactory);
                radComponents.put(cInfo.getName(), rFactory.getResult());
                fiComponents.put(cInfo.getName(), cInfo);
                createdComponents.add(rFactory.getResult());
                if (rFactory.getResult().getBeanInstance() instanceof DbControl) {
                    DbControl dbControl = (DbControl) rFactory.getResult().getBeanInstance();
                    if (formObject.getClient() != null) {
                        dbControl.setModel(formObject.getModel());
                    }
                }
            }
            // non-visual components
            for (DesignInfo di : formInfo.getNonvisuals()) {
                if (di instanceof ControlDesignInfo) {
                    ControlDesignInfo cInfo = (ControlDesignInfo) di;
                    // Factory will take care of properly classes creation,
                    // properties and events
                    RadComponentFactory rFactory = new RadComponentFactory(formModel);
                    cInfo.accept(rFactory);
                    radComponents.put(cInfo.getName(), rFactory.getResult());
                    fiComponents.put(cInfo.getName(), cInfo);
                    createdComponents.add(rFactory.getResult());
                    // final action - add to the form
                    formModel.addComponent(rFactory.getResult(), null, false);
                } else // parentless grid column or map layer
                {
                    if (di instanceof RowsetFeatureDescriptor) {
                        RadComponentFactory.initMapLayer(formModel, (RowsetFeatureDescriptor) di, null);
                    } else if (di instanceof DbGridColumn) {
                        RadComponentFactory.initGridColumn(formModel, (DbGridColumn) di, null, null);
                    }
                }
            }
            radComponents.put(formComp.getName(), formComp);
            fiComponents.put(formComp.getName(), formInfo);
            // Top-level container is added to createdComponents here
            // because it's have a layout.
            createdComponents.add(formComp);

            // Let's organize containers's layouts, except group layout.
            for (RADComponent<?> radComp : createdComponents) {
                if (radComp instanceof RADVisualContainer<?>) {
                    RADVisualContainer<?> radContainer = (RADVisualContainer<?>) radComp;
                    DesignInfo di = fiComponents.get(radComp.getName());
                    if (!(radComp.getBeanInstance() instanceof JPopupMenu)
                            && !(radComp.getBeanInstance() instanceof JMenu)
                            && !(radComp.getBeanInstance() instanceof JMenuBar)) {
                        assert di instanceof ContainerDesignInfo;
                        ContainerDesignInfo cdi = (ContainerDesignInfo) di;
                        if (cdi.getLayout() != null) {
                            // Explicit layout
                            if (!(cdi.getLayout() instanceof GroupLayoutDesignInfo)) {
                                // Simple layout of the container
                                if (cdi.getLayout() instanceof GridBagLayoutDesignInfo) {
                                    cdi.setLayout(new MarginLayoutDesignInfo());
                                }
                                SimpleLayoutFactory lf = new SimpleLayoutFactory();
                                cdi.getLayout().accept(lf);
                                formModel.setContainerLayout(radContainer, lf.getResult());
                                RadInitializer.initializeProperties(cdi.getLayout(), radContainer.getLayoutSupport().getAllProperties());
                            }
                        } else {
                            // Hidden layout of the container (scroll, split, toolbar, etc)
                            HiddenLayoutFactory lf = new HiddenLayoutFactory();
                            try {
                                cdi.accept(lf);
                                if (lf.getResult() != null) {
                                    formModel.setContainerLayout(radContainer, lf.getResult());
                                }
                            } catch (UnsupportedOperationException ex) {
                                ErrorManager.getDefault().notify(new UnsupportedOperationException(String.format("Plain container without a layout found ( %s ).", cdi.getName())));
                            }
                        }
                    } else {
                        formModel.setContainerLayout(radContainer, new MenuFakeSupport());
                    }
                }
            }
            // Let's resolve parents, references and constraints
            // Additional resolving is to set FormAwareEditor's context (FormModel)
            for (RADComponent<?> comp : createdComponents) {
                ControlDesignInfo cInfo = fiComponents.get(comp.getName());
                // constraints
                if (comp instanceof RADVisualComponent<?>) {
                    LayoutConstraints<?> lc = LayoutConstraintsProcessor.processFromDesignInfo(cInfo);
                    if (lc != null) {
                        ((RADVisualComponent<?>) comp).setLayoutConstraints(LayoutConstraintsProcessor.resolveDelegateClass(lc), lc);
                    }
                }
                // parent
                RadComponentResolver resolver = new RadComponentResolver(comp, cInfo, radComponents, fiComponents);
                cInfo.accept(resolver);
            }
            // designed size
            if (formInfo.getDesignedPreferredSize() != null) {
                formComp.setDesignerSize(formInfo.getDesignedPreferredSize());
            }
        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public void saveForm(PlatypusFormDataObject formObject, FormModel formModel, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            Map<String, ControlDesignInfo> involvedDesignInfos = new HashMap<>();
            List<RADVisualComponent<?>> needToResolve = new ArrayList<>();

            FormDesignInfo fdi = new FormDesignInfo();
            RADVisualFormContainer formContainer = null;
            for (RADComponent<?> comp : formModel.getOrderedComponentList()) {
                if (comp instanceof RADVisualFormContainer) {
                    formContainer = (RADVisualFormContainer) comp;
                    continue;
                }
                // Factory will take care of applying right classes.
                DesignInfo di = DesignInfoFactory.create(comp.getBeanClass());
                if (di == null) {// columns and layers
                    if (comp.getBeanInstance() instanceof DbGridColumn) {
                        assert comp instanceof RADModelGridColumn;
                        RADModelGridColumn radColumn = (RADModelGridColumn) comp;
                        DbGridColumn column = (DbGridColumn) comp.getBeanInstance();
                        DesignInfoFactory.initColumnsContainerWithEvents(radColumn);
                        di = column;
                        DesignInfo viewDi = DesignInfoFactory.create(radColumn.getViewControl().getBeanClass());
                        assert viewDi instanceof DbControlDesignInfo;
                        DesignInfoFactory.initWithComponent(viewDi, radColumn.getViewControl());
                        column.setControlInfo(null);
                        column.getCellDesignInfo().setCellControlInfo(null);
                        if (column.isVeer()) {
                            column.getCellDesignInfo().setCellControlInfo((DbControlDesignInfo) viewDi);
                        } else {
                            column.setControlInfo((DbControlDesignInfo) viewDi);
                        }
                    } else if (comp.getBeanInstance() instanceof RowsetFeatureDescriptor) {
                        di = (RowsetFeatureDescriptor) comp.getBeanInstance();
                    }
                }
                if (di instanceof ControlDesignInfo) {
                    ControlDesignInfo controlDi = (ControlDesignInfo) di;
                    involvedDesignInfos.put(comp.getName(), controlDi);
                    // Let's take care of properties and events.
                    // Factory initWithComponent method will take care about
                    // properties, events and simple component references.
                    DesignInfoFactory.initWithComponent(controlDi, comp);
                    // Toss design info by visual/non-visual basis
                    if (comp.getParentComponent() != null) {
                        // Some checks
                        assert comp instanceof RADVisualComponent<?>;
                        assert comp.getParentComponent() instanceof RADVisualContainer<?>;
                        RADVisualComponent<?> visualComp = (RADVisualComponent<?>) comp;
                        RADVisualContainer<?> parent = (RADVisualContainer<?>) comp.getParentComponent();
                        controlDi.setParent(parent.getName());
                        // Let's take care of layout constraints
                        // We convert them into layout properties if layout is
                        // real layout and some inter-component references in case of
                        // implicit layout.
                        if (parent.getLayoutSupport() != null) {
                            LayoutConstraints<?> radContraints = parent.getLayoutSupport().getConstraints(visualComp);
                            if (radContraints != null) {
                                boolean processed = LayoutConstraintsProcessor.processToDesignInfo(controlDi, visualComp, radContraints);
                                if (!processed) {
                                    needToResolve.add(visualComp);
                                }
                            }
                        }
                        fdi.getChildren().add(controlDi);
                        // Let's take care of container's layout.
                        if (controlDi instanceof ContainerDesignInfo
                                && comp instanceof RADVisualContainer
                                && !(controlDi instanceof MenubarDesignInfo)) {
                            //assert comp instanceof RADVisualContainer<?>;
                            ContainerDesignInfo containerDi = (ContainerDesignInfo) controlDi;
                            saveRadComponentLayout(containerDi, (RADVisualContainer<?>) comp);
                        }
                    } else {// parentless controls including button groups
                        fdi.getNonvisuals().add(controlDi);
                    }
                } else if (comp.getParent() == null) {// parentless grid columns and map layers
                    fdi.getNonvisuals().add(di);
                }
            }
            // Let's resolve complex component references.
            // Such references may occur in SplitPaneConstraints, for example.
            // There are might be other cases.
            for (RADVisualComponent<?> visualComp : needToResolve) {
                RADVisualContainer<?> parent = (RADVisualContainer<?>) visualComp.getParentComponent();
                if (parent.getLayoutSupport() != null) {
                    LayoutConstraints<?> radContraints = parent.getLayoutSupport().getConstraints(visualComp);
                    LayoutConstraintsProcessor.resolveToDesignInfo(involvedDesignInfos, visualComp, radContraints);
                }
            }
            assert formContainer != null : "Any form must have a top-level container";
            DesignInfoFactory.initWithComponent(fdi, formContainer);
            saveRadComponentLayout(fdi, formContainer);
            Document doc = Object2Dom.transform(fdi, XML_FORM, false);
            String content = XmlDom2String.transform(doc);
            try (OutputStream out = formObject.getFormFile().getOutputStream()) {
                out.write(content.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusPersistenceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

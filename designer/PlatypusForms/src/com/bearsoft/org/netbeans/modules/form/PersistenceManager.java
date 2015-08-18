/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.org.netbeans.modules.form.bound.RADColumnView;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelScalarComponent;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportManager;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.BorderLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.CardLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.SplitPaneSupport;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.TabbedPaneSupport;
import com.eas.client.forms.Form;
import com.eas.client.forms.FormFactory;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.HorizontalPosition;
import com.eas.client.forms.Orientation;
import com.eas.client.forms.VerticalPosition;
import com.eas.client.forms.Widget;
import com.eas.client.forms.components.model.ModelComponentDecorator;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.client.forms.components.model.grid.columns.ServiceColumn;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.containers.SplitPane;
import com.eas.client.forms.containers.TabbedPane;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.CardLayout;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.client.forms.menu.PopupMenu;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.settings.SettingsConstants;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.module.EntityJSObject;
import com.eas.designer.explorer.PlatypusDataObject;
import com.eas.gui.ScriptColor;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.nashorn.api.scripting.JSObject;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An abstract class which defines interface for persistence managers (being
 * responsible for loading and saving forms) and provides a basic registration
 * facility. PersistenceManager implementations should be able to deal with
 * multiple forms being saved and loaded by one instance of persistence manager
 * (but not concurrently).
 *
 * @author Ian Formanek, Tomas Pavek
 */
public class PersistenceManager {

    protected static DocumentBuilderFactory docsBuidlersFactory = DocumentBuilderFactory.newInstance();

    public FormModel loadForm(FileObject formFileObject, PlatypusDataObject formDataObject, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            String formContent = formFileObject.asText(PlatypusUtils.COMMON_ENCODING_NAME);
            Document doc = Source2XmlDom.transform(formContent);
            FormFactory formFactory = new FormFactory(doc.getDocumentElement(), formDataObject instanceof PlatypusFormDataObject ? ((PlatypusFormDataObject) formDataObject).getModel().getPublished() : null) {

                @Override
                protected void resolveIcon(String aIconName, Consumer<ImageIcon> onLoad, Consumer<Exception> onFailure) {
                    try {
                        NbImageIcon loaded = IconEditor.iconFromResourceName(formDataObject, aIconName);
                        onLoad.accept(loaded);
                    } catch (Exception ex) {
                        nonfatalErrors.add(ex);
                    }
                }

                @Override
                protected JSObject resolveEntity(String aEntityName) throws Exception {
                    if (formDataObject instanceof PlatypusFormDataObject) {
                        ApplicationDbEntity entity = ((PlatypusFormDataObject) formDataObject).getModel().getEntityByName(aEntityName);
                        return entity != null ? entity.getPublished() : null;
                    } else {
                        return new EntityJSObject(aEntityName);
                    }
                }

                @Override
                protected JSObject resolveEntity(long aEntityId) throws Exception {
                    if (formDataObject instanceof PlatypusFormDataObject) {
                        ApplicationDbEntity entity = ((PlatypusFormDataObject) formDataObject).getModel().getEntityById(aEntityId);
                        return entity != null ? entity.getPublished() : null;
                    } else {
                        return null;
                    }
                }

                @Override
                protected JComponent createAnchorsPane() {
                    return new FormUtils.Panel(new MarginLayout());
                }

                @Override
                protected JComponent createBorderPane(int hgap, int vgap) {
                    return new FormUtils.Panel(new BorderLayout(hgap, vgap));
                }

                @Override
                protected JComponent createBoxPane(int orientation, int hgap, int vgap) {
                    JComponent res = new FormUtils.Panel();
                    int axis = BoxLayout.X_AXIS;
                    if (orientation == Orientation.HORIZONTAL) {
                        axis = BoxLayout.X_AXIS;
                    } else if (orientation == Orientation.VERTICAL) {
                        axis = BoxLayout.Y_AXIS;
                    }
                    res.setLayout(new BoxLayout(res, axis, hgap, vgap));
                    return res;
                }

                @Override
                protected JComponent createCardPane(int hgap, int vgap) {
                    return new FormUtils.Panel(new CardLayout(hgap, vgap));
                }

                @Override
                protected JComponent createFlowPane(int hgap, int vgap) {
                    return new FormUtils.Panel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
                }

                @Override
                protected JComponent createGridPane(int rows, int columns, int hgap, int vgap) {
                    return new FormUtils.Panel(new GridLayout(rows, columns, hgap, vgap));
                }

                @Override
                protected void addToAnchorsPane(JComponent parent, JComponent aTarget, MarginConstraints constraints) {
                    parent.add(aTarget, constraints);
                }

                @Override
                protected void addToBorderPane(JComponent parent, JComponent aComp, Integer aPlace, Integer aSize) {
                    aPlace = aPlace != null ? aPlace : HorizontalPosition.CENTER;
                    if (aPlace != HorizontalPosition.CENTER && aSize != null) {
                        Dimension prefSize = aComp.getPreferredSize();
                        if (aPlace == HorizontalPosition.LEFT || aPlace == HorizontalPosition.RIGHT) {
                            aComp.setPreferredSize(new Dimension(aSize, prefSize.height));
                        } else {
                            aComp.setPreferredSize(new Dimension(prefSize.width, aSize));
                        }
                    }
                    String place;
                    switch (aPlace) {
                        case HorizontalPosition.LEFT:
                            place = BorderLayout.WEST;
                            break;
                        case HorizontalPosition.CENTER:
                            place = BorderLayout.CENTER;
                            break;
                        case HorizontalPosition.RIGHT:
                            place = BorderLayout.EAST;
                            break;
                        case VerticalPosition.TOP:
                            place = BorderLayout.NORTH;
                            break;
                        case VerticalPosition.BOTTOM:
                            place = BorderLayout.SOUTH;
                            break;
                        default:
                            place = BorderLayout.CENTER;
                            break;
                    }
                    parent.add(aComp, place);
                }

                @Override
                protected void addToBoxPane(JComponent parent, JComponent aTarget, Dimension prefSize) {
                    aTarget.setPreferredSize(prefSize);
                    aTarget.setSize(prefSize);
                    parent.add(aTarget);
                }

                @Override
                protected void addToCardPane(JComponent parent, JComponent aTarget, String cardName) {
                    parent.add(aTarget, cardName);
                }

                @Override
                protected void addToFlowPane(JComponent parent, JComponent aTarget, Dimension prefSize) {
                    aTarget.setPreferredSize(prefSize);
                    parent.add(aTarget);
                }

                @Override
                protected void addToGridPane(JComponent parent, JComponent aTarget) {
                    parent.add(aTarget);
                }

            };
            formFactory.parse();
            Form form = formFactory.getForm();
            FormModel formModel = new FormModel(formDataObject, form);
            formModel.setName(formDataObject.getName());

            // Let's take care of top level container
            RADVisualFormContainer formComp = new RADVisualFormContainer();
            formComp.initialize(formModel);
            formModel.initFormComponent(formComp);
            formComp.setStoredName(form.getViewWidget().getName());
            formComp.setBeanInstance(form.getViewWidget());

            formComp.checkLayoutSupport();
            LayoutSupportManager laysup = formComp.getLayoutSupport();
            laysup.prepareLayoutDelegate(false);
            formComp.setInModel(true);

            Map<String, RADComponent<?>> radComps = new HashMap<>();
            radComps.put(formComp.getName(), formComp);
            formFactory.getWidgetsList().stream().sequential().filter((JComponent aWidget) -> {
                return aWidget != form.getViewWidget();
            }).forEach((JComponent aWidget) -> {
                try {
                    RADComponent<?> radComp = radComponentByWidget(formModel, aWidget);
                    radComps.put(radComp.getName(), radComp);
                } catch (Exception ex) {
                    nonfatalErrors.add(ex);
                }
            });
            formFactory.getWidgetsList().stream().sequential().forEach((JComponent aWidget) -> {
                RADComponent<?> radComp = radComps.get(aWidget.getName());
                if (aWidget instanceof Widget) {
                    Widget w = (Widget) aWidget;
                    Widget p = w.getParentWidget();
                    if (p != null) {
                        String parentName = p.getComponent().getName();
                        RADComponent<?> radParent = radComps.get(parentName);
                        if (radParent instanceof ComponentContainer) {
                            ComponentContainer cont = (ComponentContainer) radParent;
                            radComp.setParent(cont);
                            cont.add(radComp);
                            RADVisualContainer<?> visCont = radComp.getParentComponent();
                            if (visCont != null && visCont.getLayoutSupport() != null) {
                                LayoutSupportManager layoutSupportManager = visCont.getLayoutSupport();
                                LayoutSupportDelegate layoutDelegate = layoutSupportManager.getLayoutDelegate();
                                if (layoutDelegate instanceof BorderLayoutSupport) {
                                    BorderLayout borderLayout = (BorderLayout) visCont.getBeanInstance().getLayout();
                                    LayoutConstraints constriants = new BorderLayoutSupport.BorderLayoutConstraints((String) borderLayout.getConstraints((Component) radComp.getBeanInstance()));
                                    layoutSupportManager.injectComponents(new RADVisualComponent<?>[]{(RADVisualComponent<?>) radComp}, new LayoutConstraints[]{constriants}, layoutSupportManager.getComponentCount());
                                } else if (layoutDelegate instanceof CardLayoutSupport) {
                                    CardLayout cardLayout = (CardLayout) visCont.getBeanInstance().getLayout();
                                    LayoutConstraints constriants = new CardLayoutSupport.CardLayoutConstraints(cardLayout.getCard((Component) radComp.getBeanInstance()));
                                    layoutSupportManager.injectComponents(new RADVisualComponent<?>[]{(RADVisualComponent<?>) radComp}, new LayoutConstraints[]{constriants}, layoutSupportManager.getComponentCount());
                                } else if (layoutDelegate instanceof MarginLayoutSupport) {
                                    MarginLayout marginLayout = (MarginLayout) visCont.getBeanInstance().getLayout();
                                    LayoutConstraints constriants = new MarginLayoutSupport.MarginLayoutConstraints(marginLayout.getLayoutConstraints((Component) radComp.getBeanInstance()));
                                    layoutSupportManager.injectComponents(new RADVisualComponent<?>[]{(RADVisualComponent<?>) radComp}, new LayoutConstraints[]{constriants}, layoutSupportManager.getComponentCount());
                                } else if (layoutDelegate instanceof TabbedPaneSupport) {
                                    TabbedPane tabbedPane = (TabbedPane) visCont.getBeanInstance();
                                    int index = layoutSupportManager.getComponentCount();
                                    String tooltip = tabbedPane.getToolTipTextAt(index);
                                    NbImageIcon icon = (NbImageIcon) tabbedPane.getIconAt(index);
                                    String title = tabbedPane.getTitleAt(index);
                                    LayoutConstraints constriants = new TabbedPaneSupport.TabLayoutConstraints(title, icon, tooltip);
                                    layoutSupportManager.injectComponents(new RADVisualComponent<?>[]{(RADVisualComponent<?>) radComp}, new LayoutConstraints[]{constriants}, index);
                                } else if (layoutDelegate instanceof SplitPaneSupport) {
                                    SplitPane splitPane = (SplitPane) visCont.getBeanInstance();
                                    String placement = splitPane.getOrientation() == Orientation.HORIZONTAL
                                            ? (splitPane.getLeftComponent() == radComp.getBeanInstance() ? SplitPane.LEFT : SplitPane.RIGHT)
                                            : (splitPane.getTopComponent() == radComp.getBeanInstance() ? SplitPane.TOP : SplitPane.BOTTOM);
                                    LayoutConstraints constriants = new SplitPaneSupport.SplitLayoutConstraints(placement);
                                    layoutSupportManager.injectComponents(new RADVisualComponent<?>[]{(RADVisualComponent<?>) radComp}, new LayoutConstraints[]{constriants}, layoutSupportManager.getComponentCount());
                                }
                            }
                        } else {
                            formModel.getModelContainer().add(radComp);
                        }
                    } else if (w != form.getViewWidget()) {
                        formModel.getModelContainer().add(radComp);
                    }
                }
                radComp.getProperties();// force properties creation
            });
            return formModel;
        } catch (Throwable ex) {
            throw new PersistenceException(ex);
        }
    }

    public void saveForm(FileObject formFileObject, FormEditor formEditor, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            DocumentBuilder builder = docsBuidlersFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.setXmlStandalone(true);
            Element root = doc.createElement("layout");
            doc.appendChild(root);
            writeProperties(formEditor.getFormRootNode().getFormProperties(), doc, root, true);
            root.setAttribute(Form.VIEW_SCRIPT_NAME, formEditor.getFormModel().getTopRADComponent().getName());
            //formEditor.getFormModel().getAllComponents().stream().sequential().filter((RADComponent<?> aComp) -> {
            formEditor.getFormModel().getOrderedComponentList().stream().sequential().filter((RADComponent<?> aComp) -> {
                return !(aComp instanceof RADModelGridColumn);// grid columns will be written by grid.
            }).forEach((RADComponent<?> aComp) -> {
                String widgetTagName = aComp.getBeanClass().getSimpleName();
                if (aComp.getBeanInstance() instanceof FormUtils.Panel
                        && aComp instanceof RADVisualContainer<?>) {
                    RADVisualContainer<?> radCont = (RADVisualContainer<?>) aComp;
                    LayoutSupportManager layoutSupportManager = radCont.getLayoutSupport();
                    if (layoutSupportManager != null && layoutSupportManager.getLayoutDelegate() != null) {
                        Class<?> layoutedContainerClass = FormUtils.getPlatypusConainerClass(layoutSupportManager.getLayoutDelegate().getSupportedClass());
                        widgetTagName = layoutedContainerClass.getSimpleName();
                    }
                }
                Element widgetElement = doc.createElement(widgetTagName);
                root.appendChild(widgetElement);
                writeProperties(aComp.getBeanProperties(), doc, widgetElement, true);
                widgetElement.setAttribute("name", aComp.getName());
                if (aComp.getBeanInstance() instanceof JComponent
                        && !(aComp.getBeanInstance() instanceof ButtonGroup)
                        && !(aComp.getBeanInstance() instanceof PopupMenu)) {
                    JComponent jComp = (JComponent) aComp.getBeanInstance();
                    widgetElement.setAttribute("prefWidth", jComp.getSize().width + "px");
                    widgetElement.setAttribute("prefHeight", jComp.getSize().height + "px");
                }
                if (aComp.getBeanInstance() instanceof SplitPane) {
                    RADVisualContainer<?> splitCont = (RADVisualContainer<?>) aComp;
                    for (RADVisualComponent<?> visComp : splitCont.getSubBeans()) {
                        SplitPaneSupport.SplitLayoutConstraints constraints = (SplitPaneSupport.SplitLayoutConstraints) splitCont.getLayoutSupport().getConstraints(visComp);
                        String position = constraints.getConstraintsObject();
                        switch (position) {
                            case SplitPane.TOP:
                            case SplitPane.LEFT:
                                widgetElement.setAttribute("leftComponent", visComp.getBeanInstance().getName());
                                break;
                            case SplitPane.BOTTOM:
                            case SplitPane.RIGHT:
                                widgetElement.setAttribute("rightComponent", visComp.getBeanInstance().getName());
                                break;
                        }
                    }
                }
                if (aComp instanceof RADVisualContainer<?> && aComp.getBeanInstance() instanceof FormUtils.Panel) {
                    RADVisualContainer<?> radCont = (RADVisualContainer<?>) aComp;
                    writeProperties(radCont.getLayoutSupport().getAllProperties(), doc, widgetElement, false);
                }
                if (aComp instanceof RADVisualComponent<?>) {
                    RADVisualComponent<?> visComp = (RADVisualComponent<?>) aComp;
                    if (visComp.getParentComponent() != null) {
                        if (visComp.getParentComponent().getBeanInstance() instanceof FormUtils.Panel) {
                            LayoutSupportManager parentLayoutSupport = visComp.getParentLayoutSupport();
                            LayoutConstraints constraints = parentLayoutSupport.getConstraints(visComp);
                            if (constraints != null) {
                                Class<?> layoutedContainerClass = FormUtils.getPlatypusConainerClass(parentLayoutSupport.getSupportedClass());
                                Element constraintsElement = doc.createElement(layoutedContainerClass.getSimpleName() + "Constraints");
                                widgetElement.appendChild(constraintsElement);
                                if (constraints instanceof BorderLayoutSupport.BorderLayoutConstraints) {
                                    BorderLayoutSupport.BorderLayoutConstraints borderConstraints = (BorderLayoutSupport.BorderLayoutConstraints) constraints;
                                    String place = (String) borderConstraints.getConstraintsObject();
                                    constraintsElement.setAttribute("place", awtBorderConstraintsToPlPositions(place) + "");
                                } else {
                                    writeProperties(constraints.getProperties(), doc, constraintsElement, false);
                                }
                            }
                        } else if (visComp.getParentComponent().getBeanInstance() instanceof TabbedPane) {
                            LayoutSupportManager parentLayoutSupport = visComp.getParentLayoutSupport();
                            LayoutConstraints contraints = parentLayoutSupport.getConstraints(visComp);
                            Element constraintsElement = doc.createElement(TabbedPane.class.getSimpleName() + "Constraints");
                            widgetElement.appendChild(constraintsElement);
                            writeProperties(contraints.getProperties(), doc, constraintsElement, false);
                        }
                    }
                    if (aComp instanceof RADModelGrid) {
                        try {
                            RADModelGrid grid = (RADModelGrid) aComp;
                            writeSubBeans(grid, doc, widgetElement);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                if (aComp.getParent() instanceof RADVisualComponent<?>) {
                    RADVisualComponent<?> visParent = (RADVisualComponent<?>) aComp.getParent();
                    widgetElement.setAttribute("parent", visParent.getName());
                }
            });
            String content = XmlDom2String.transform(doc);
            try (OutputStream out = formFileObject.getOutputStream()) {
                out.write(content.getBytes(SettingsConstants.COMMON_ENCODING));
            }
        } catch (ParserConfigurationException | DOMException | IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    private int awtBorderConstraintsToPlPositions(String aPlace) {
        int place;
        if (aPlace != null) {
            switch (aPlace) {
                case BorderLayout.LINE_START:
                case BorderLayout.WEST:
                    place = HorizontalPosition.LEFT;
                    break;
                case BorderLayout.CENTER:
                    place = HorizontalPosition.CENTER;
                    break;
                case BorderLayout.LINE_END:
                case BorderLayout.EAST:
                    place = HorizontalPosition.RIGHT;
                    break;
                case BorderLayout.PAGE_START:
                case BorderLayout.NORTH:
                    place = VerticalPosition.TOP;
                    break;
                case BorderLayout.PAGE_END:
                case BorderLayout.SOUTH:
                    place = VerticalPosition.BOTTOM;
                    break;
                default:
                    place = HorizontalPosition.CENTER;
                    break;
            }
            return place;
        } else {
            return HorizontalPosition.CENTER;
        }
    }

    private void writeSubBeans(ComponentContainer aColumnsContainer, Document doc, Element targetElement) throws Exception {
        for (RADComponent<?> subBean : aColumnsContainer.getSubBeans()) {
            Element columnElement = doc.createElement(subBean.getBeanClass().getSimpleName());
            targetElement.appendChild(columnElement);
            columnElement.setAttribute("name", subBean.getName());
            writeProperties(subBean.getBeanProperties(), doc, columnElement, false);
            if (subBean instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) subBean;
                RADProperty<Integer> radWidth = radColumn.getProperty("width");
                if (!radWidth.isDefaultValue()) {
                    columnElement.setAttribute(radWidth.getName(), radWidth.getValue() + "px");
                }
                RADProperty<Integer> radMinWidth = radColumn.getProperty("minWidth");
                if (!radMinWidth.isDefaultValue()) {
                    columnElement.setAttribute(radMinWidth.getName(), radMinWidth.getValue() + "px");
                }
                RADProperty<Integer> radMaxWidth = radColumn.getProperty("maxWidth");
                if (!radMaxWidth.isDefaultValue()) {
                    columnElement.setAttribute(radMaxWidth.getName(), radMaxWidth.getValue() + "px");
                }
                RADProperty<Integer> radPreferredWidth = radColumn.getProperty("preferredWidth");
                if (!radPreferredWidth.isDefaultValue()) {
                    columnElement.setAttribute(radPreferredWidth.getName(), radPreferredWidth.getValue() + "px");
                }
                if (!(radColumn.getBeanInstance().getTableColumn() instanceof ServiceColumn)) {
                    Element viewElement = doc.createElement(radColumn.getViewControl().getBeanClass().getSimpleName());
                    columnElement.appendChild(viewElement);
                    writeProperties(radColumn.getViewControl().getBeanProperties(), doc, viewElement, true);
                }
            }
            if (subBean instanceof ComponentContainer) {
                writeSubBeans((ComponentContainer) subBean, doc, columnElement);
            }
        }
    }

    private void writeProperties(FormProperty<?>[] aProperties, Document doc, Element targetElement, boolean skipPosition) throws DOMException {
        for (FormProperty<?> radProp : aProperties) {
            if (!radProp.isDefaultValue()) {
                try {
                    Object propValue = radProp.getValue();
                    if (propValue != null) {
                        if (propValue instanceof ComponentReference<?>) {
                            ComponentReference<?> compRef = (ComponentReference<?>) propValue;
                            if (compRef.getComponent() != null) {
                                targetElement.setAttribute(radProp.getName(), compRef.getComponent().getName());
                            }
                        } else if (propValue instanceof EntityJSObject) {
                            targetElement.setAttribute(radProp.getName(), ((EntityJSObject) propValue).getName());
                        } else if (propValue instanceof IconEditor.NbImageIcon) {
                            targetElement.setAttribute(radProp.getName(), ((IconEditor.NbImageIcon) propValue).getName());
                        } else if (propValue instanceof java.awt.Cursor) {
                            targetElement.setAttribute(radProp.getName(), String.valueOf(((java.awt.Cursor) propValue).getType()));
                        } else if (propValue instanceof Color) {
                            targetElement.setAttribute(radProp.getName(), ScriptColor.encode((Color) propValue));
                        } else if (propValue instanceof Font) {
                            Font fontValue = (Font) propValue;
                            Element fontElement = doc.createElement("font");
                            targetElement.appendChild(fontElement);
                            fontElement.setAttribute("family", fontValue.getFamily());
                            fontElement.setAttribute("size", String.valueOf(fontValue.getSize()));
                            fontElement.setAttribute("style", String.valueOf(fontValue.getStyle()));
                        } else {
                            String propName = radProp.getName();
                            if (skipPosition && ("left".equalsIgnoreCase(propName)
                                    || "top".equalsIgnoreCase(propName)
                                    || "width".equalsIgnoreCase(propName)
                                    || "height".equalsIgnoreCase(propName))) {
                                continue;
                            }
                            targetElement.setAttribute(propName, String.valueOf(propValue));
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private RADComponent<?> radComponentByWidget(FormModel aFormModel, JComponent aWidget) throws Exception {
        RADComponent<?> radComp;
        if (aWidget instanceof ModelGrid) {
            radComp = new RADModelGrid();
            ModelGrid modelGrid = (ModelGrid) aWidget;
            ((RADModelGrid) radComp).setFireRawColumnsChanges(false);
            adoptHeaderNodes(aFormModel, modelGrid.getHeader(), (RADModelGrid) radComp);
            ((RADModelGrid) radComp).setFireRawColumnsChanges(true);
        } else if (aWidget instanceof ModelWidget) {
            radComp = new RADModelScalarComponent<>();
        } else if (aWidget instanceof ButtonGroup) {
            radComp = new RADButtonGroup();
        } else if (aWidget instanceof HasChildren) {
            radComp = new RADVisualContainer<>();
        } else {
            radComp = new RADVisualComponent<>();
        }
        radComp.initialize(aFormModel);
        radComp.setStoredName(aWidget.getName());
        ((RADComponent<JComponent>) radComp).setBeanInstance(aWidget);
        if (radComp instanceof RADVisualContainer<?>) {
            RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp;
            radCont.checkLayoutSupport();
            LayoutSupportManager laysup = radCont.getLayoutSupport();
            laysup.prepareLayoutDelegate(false);
        }
        radComp.setInModel(true);
        return radComp;
    }

    private void adoptHeaderNodes(FormModel aFormModel, List<GridColumnsNode> aNodes, ComponentContainer aColumnsContainer) {
        aNodes.stream().sequential().forEach((GridColumnsNode aNode) -> {
            RADModelGridColumn radCol = new RADModelGridColumn();
            radCol.initialize(aFormModel);
            radCol.setStoredName(((ModelColumn) aNode.getTableColumn()).getName());
            radCol.setBeanInstance(aNode);
            radCol.setParent(aColumnsContainer);
            aColumnsContainer.add(radCol);
            radCol.setInModel(true);
            if (aNode.getTableColumn() instanceof ModelColumn) {
                ModelWidget editor = ((ModelColumn) aNode.getTableColumn()).getEditor();
                if (editor != null) {
                    try {
                        RADColumnView<ModelComponentDecorator> radEditor = new RADColumnView<>();
                        radEditor.initialize(aFormModel);
                        radEditor.setBeanInstance((ModelComponentDecorator) editor);
                        radCol.setViewControl(radEditor);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            adoptHeaderNodes(aFormModel, aNode.getChildren(), radCol);
        });
    }
}

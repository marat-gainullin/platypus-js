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

import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelScalarComponent;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportDelegate;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutSupportRegistry;
import com.eas.client.forms.Form;
import com.eas.client.forms.FormFactory;
import com.eas.client.forms.HasChildren;
import com.eas.client.forms.HorizontalPosition;
import com.eas.client.forms.Orientation;
import com.eas.client.forms.Widget;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.client.forms.containers.ButtonGroup;
import com.eas.client.forms.layouts.BoxLayout;
import com.eas.client.forms.layouts.CardLayout;
import com.eas.client.forms.layouts.MarginConstraints;
import com.eas.client.forms.layouts.MarginLayout;
import com.eas.designer.application.PlatypusUtils;
import com.eas.gui.ScriptColor;
import com.eas.xml.dom.Source2XmlDom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    static final String XML_FORM = "layout"; // NOI18N

    public boolean canLoadForm(PlatypusFormDataObject formObject) throws PersistenceException {
        return false;
    }

    public FormModel loadForm(PlatypusFormDataObject formDataObject, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            String formContent = formDataObject.getFormFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
            Document doc = Source2XmlDom.transform(formContent);
            FormFactory formFactory = new FormFactory(doc.getDocumentElement(), formDataObject.getModel().getPublished()) {

                @Override
                protected ImageIcon resolveIcon(String aIconName) {
                    try {
                        return IconEditor.iconFromResourceName(formDataObject, aIconName);
                    } catch (Exception ex) {
                        nonfatalErrors.add(ex);
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
                protected JComponent createBoxPane(int axis, int hgap, int vgap) {
                    JComponent res = new FormUtils.Panel();
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
                    parent.add(aComp, aPlace);
                }

                @Override
                protected void addToBoxPane(JComponent parent, JComponent aTarget, Dimension prefSize) {
                    if (((BoxLayout) parent.getLayout()).getAxis() == Orientation.HORIZONTAL) {
                        parent.add(aTarget, prefSize.width);
                    } else {
                        parent.add(aTarget, prefSize.height);
                    }
                }

                @Override
                protected void addToCardPane(JComponent parent, JComponent aTarget, String cardName) {
                    parent.add(aTarget, cardName);
                }

                @Override
                protected void addToFlowPane(JComponent parent, JComponent aTarget) {
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
            formComp.setBeanInstance(form.getViewWidget());
            for (RADProperty<?> radProp : formComp.getBeanProperties()) {
                radProp.setChanged(!radProp.isDefaultValue());
            }
            formComp.checkLayoutSupport();
            LayoutSupportDelegate layoutSupportDelegate = LayoutSupportRegistry.createSupportForLayout(((Container) formComp.getBeanInstance()).getLayout().getClass());
            formComp.getLayoutSupport().setLayoutDelegate(layoutSupportDelegate);
            formComp.setInModel(true);

            Map<String, RADComponent<?>> radComps = new HashMap<>();
            formFactory.getWidgets().entrySet().stream().forEach((Map.Entry<String, JComponent> aEntry) -> {
                try {
                    RADComponent<?> radComp = radComponentByWidget(formModel, aEntry.getValue());
                    radComps.put(radComp.getName(), radComp);
                    /*
                     if(radComp instanceof RADModelGrid){
                     RADModelGrid radGrid = (RADModelGrid)radComp;
                     ModelGrid grid = radGrid.getBeanInstance();
                     for(GridColumnsNode colNode : grid.getHeader()){
                     RADModelGridColumn radCol = new RADModelGridColumn();
                     radCol.initialize(formModel);
                     radCol.setStoredName(colNode.getName());
                     radCol.setBeanInstance(colNode);
                     radCol.setInModel(true);
                     for(RADProperty<?> radProp : radCol.getBeanProperties()){
                     radProp.setChanged(!radProp.isDefaultValue());
                     }
                     }
                     }
                     */
                } catch (Exception ex) {
                    nonfatalErrors.add(ex);
                }
            });

            RADComponent<?>[] toResolve = radComps.values().toArray(new RADComponent<?>[]{});
            for (RADComponent<?> radComp : toResolve) {
                Object oWidget = radComp.getBeanInstance();
                if (oWidget instanceof Widget) {
                    Widget w = (Widget) oWidget;
                    Widget p = w.getParentWidget();
                    if (p != null) {
                        if (p == form.getViewWidget()) {
                            radComp.setParent(formComp);
                            formComp.add(radComp);
                        } else {
                            String parentName = p.getComponent().getName();
                            RADComponent<?> radParent = radComps.get(parentName);
                            if (radParent instanceof ComponentContainer) {
                                ComponentContainer cont = (ComponentContainer) radParent;
                                radComp.setParent(cont);
                                cont.add(radComp);
                            } else {
                                formModel.getModelContainer().add(radComp);
                            }
                        }
                    } else {
                        formModel.getModelContainer().add(radComp);
                    }
                }
            }
            return formModel;
        } catch (Exception ex) {
            throw new PersistenceException(ex);
        }
    }

    public void saveForm(PlatypusFormDataObject formObject, FormModel formModel, List<Throwable> nonfatalErrors) throws PersistenceException {
        try {
            DocumentBuilder builder = docsBuidlersFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.setXmlStandalone(true);
            Element root = doc.createElement(XML_FORM);
            doc.appendChild(root);
            writeProperties(formModel.getTopRADComponent().getBeanProperties(), doc, root);
            formModel.getAllComponents().forEach((RADComponent<?> aComp) -> {
                Element widgetElement = doc.createElement("widget");
                root.appendChild(widgetElement);
                widgetElement.setAttribute("type", aComp.getBeanClass().getSimpleName());
                widgetElement.setAttribute("name", aComp.getName());
                writeProperties(aComp.getBeanProperties(), doc, widgetElement);
                if (aComp instanceof RADVisualContainer<?>) {
                    Element layoutElement = doc.createElement("layout");
                    widgetElement.appendChild(layoutElement);
                    RADVisualContainer<?> radCont = (RADVisualContainer<?>) aComp;
                    writeProperties(radCont.getLayoutSupport().getAllProperties(), doc, layoutElement);
                }
                if (aComp instanceof RADVisualComponent<?>) {
                    RADVisualComponent<?> visComp = (RADVisualComponent<?>) aComp;
                    Element constraintsElement = doc.createElement("constraints");
                    widgetElement.appendChild(constraintsElement);
                    writeProperties(visComp.getConstraintsProperties(), doc, constraintsElement);
                    if (aComp instanceof RADModelGrid) {
                        RADModelGrid grid = (RADModelGrid) aComp;
                        writeSubBeans(grid, doc, widgetElement);
                    }
                }
                if (aComp.getParent() instanceof RADVisualComponent<?>) {
                    RADVisualComponent<?> visParent = (RADVisualComponent<?>) aComp.getParent();
                    widgetElement.setAttribute("parent", visParent.getName());
                }
            });
        } catch (ParserConfigurationException ex) {
            throw new PersistenceException(ex);
        }
    }

    private void writeSubBeans(ComponentContainer aColumnsContainer, Document doc, Element targetElement) throws DOMException {
        for (RADComponent<?> subBean : aColumnsContainer.getSubBeans()) {
            Element columnElement = doc.createElement("column");
            targetElement.appendChild(columnElement);
            writeProperties(subBean.getBeanProperties(), doc, columnElement);
            if (subBean instanceof RADModelGridColumn) {
                RADModelGridColumn radColumn = (RADModelGridColumn) subBean;
                Element viewElement = doc.createElement("view");
                columnElement.appendChild(viewElement);
                writeProperties(radColumn.getViewControl().getBeanProperties(), doc, viewElement);
            }
            if (subBean instanceof ComponentContainer) {
                writeSubBeans((ComponentContainer) subBean, doc, columnElement);
            }
        }
    }

    private void writeProperties(FormProperty<?>[] aProperties, Document doc, Element targetElement) throws DOMException {
        for (FormProperty<?> radProp : aProperties) {
            if (!radProp.getName().equals("type")) {
                if (!radProp.isDefaultValue()) {
                    try {
                        Object propValue = radProp.getValue();
                        if (propValue != null) {
                            if (propValue instanceof ComponentReference<?>) {
                                ComponentReference<?> compRef = (ComponentReference<?>) propValue;
                                if (compRef.getComponent() != null) {
                                    targetElement.setAttribute(radProp.getName(), compRef.getComponent().getName());
                                }
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
                                targetElement.setAttribute(radProp.getName(), String.valueOf(propValue));
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } else {
                Logger.getLogger(PersistenceManager.class.getName()).log(Level.WARNING, "Prohibited property name \"type\" occured while saving a form.");
            }
        }
    }

    private RADComponent<?> radComponentByWidget(FormModel aFormModel, JComponent aWidget) throws Exception {
        RADComponent<?> radComp;
        if (aWidget instanceof ModelWidget) {
            if (aWidget instanceof ModelGrid) {
                radComp = new RADModelGrid();
            } else {
                radComp = new RADModelScalarComponent<>();
            }
        } else {
            if (aWidget instanceof ButtonGroup) {
                radComp = new RADButtonGroup();
            } else {
                if (aWidget instanceof HasChildren) {
                    radComp = new RADVisualContainer<>();
                } else {
                    radComp = new RADVisualComponent<>();
                }
            }
        }
        radComp.initialize(aFormModel);
        radComp.setStoredName(aWidget.getName());
        ((RADComponent<JComponent>) radComp).setBeanInstance(aWidget);
        for (RADProperty<?> radProp : radComp.getBeanProperties()) {
            radProp.setChanged(!radProp.isDefaultValue());
        }
        if (radComp instanceof RADVisualContainer<?>) {
            RADVisualContainer<?> radCont = (RADVisualContainer<?>) radComp;
            radCont.checkLayoutSupport();
            LayoutSupportDelegate layoutSupportDelegate = LayoutSupportRegistry.createSupportForLayout(radCont.getBeanInstance().getLayout().getClass());
            radCont.getLayoutSupport().setLayoutDelegate(layoutSupportDelegate);
        }
        radComp.setInModel(true);
        return radComp;
    }
}

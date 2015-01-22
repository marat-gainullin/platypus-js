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

import java.awt.Component;
import javax.swing.Action;
import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;

/**
 * A common superclass for nodes used in Form Editor.
 *
 * @author Tomas Pavek
 */
public class FormNode extends AbstractNode {

    protected FormModel formModel;
    
    private final FormCookie formCookie = new FormCookie() {
        @Override
        public FormModel getFormModel() {
            return formModel;
        }

        @Override
        public FormNode getOriginalNode() {
            return FormNode.this;
        }
    };
    protected Action[] actions;

    protected FormNode(Children children, FormModel aFormModel) {
        super(children);
        formModel = aFormModel;
        getCookieSet().add(formCookie);
        getCookieSet().add(aFormModel.getDataObject());
    }

    @Override
    public javax.swing.Action[] getActions(boolean context) {
        if (actions == null) {
            actions = new Action[]{SystemAction.get(PropertiesAction.class)};
        }
        return actions;
    }

    @Override
    public Component getCustomizer() {
        return createCustomizer();
    }

    // to be implemented in FormNode descendants (instead of getCustomizer)
    protected Component createCustomizer() {
        return null;
    }

    /**
     * Provides access for firing property changes
     *
     * @param name property name
     * @param oldValue old value of the property
     * @param newValue new value of the property
     */
    public void firePropertyChangeHelper(String name,
            Object oldValue, Object newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }

    // ----------
    // automatic children updates
    void updateChildren() {
        Children children = getChildren();
        if (children instanceof FormNodeChildren) {
            ((FormNodeChildren) children).updateKeys();
        }
    }

    // Special children class - to be implemented in FormNode descendants (if
    // they know their set of children nodes and can update them).
    protected abstract static class FormNodeChildren extends Children.Keys<RADComponent<?>> {

        protected abstract void updateKeys();
    }

    // ----------
    // Persistence hacks - for the case the node is selected in some
    // (standalone) properties window when IDE exits. We don't restore the
    // original node after IDE restarts (would require to load the form), but
    // provide a fake node which destroys itself immediately - closing the
    // properties window. [Would be nice to find some better solution...]
    @Override
    public Node.Handle getHandle() {
        return new Handle();
    }

    static class Handle implements Node.Handle {

        static final long serialVersionUID = 1;

        @Override
        public Node getNode() throws java.io.IOException {
            return new ClosingNode();
        }
    }

    static class ClosingNode extends AbstractNode implements Runnable {

        ClosingNode() {
            super(Children.LEAF);
        }

        @Override
        public String getName() {
            java.awt.EventQueue.invokeLater(this);
            return super.getName();
        }

        @Override
        public Node.PropertySet[] getPropertySets() {
            java.awt.EventQueue.invokeLater(this);
            return super.getPropertySets();
        }

        @Override
        public void run() {
            fireNodeDestroyed();
        }
    }
}

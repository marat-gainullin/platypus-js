/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2011 Sun
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

package com.eas.wizard.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.swing.NbiCheckBox;
import org.netbeans.installer.wizard.components.panels.DestinationPanel;
import org.netbeans.installer.wizard.containers.SwingContainer;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;

/**
 *
 * @author Dmitry Lipin
 */
public class PlatypusRuntimePanel extends DestinationPanel {

    public PlatypusRuntimePanel() {
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);        
        setProperty(ERROR_CONTAINS_NON_ASCII_CHARS,
                DEFAULT_ERROR_CONTAINS_NON_ASCII_CHARS);
    }

    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new PlatypusRuntimePanelUi(this);
        }

        return wizardUi;
    }

    @Override
    public void initialize() {
        super.initialize();
    }


    public static class PlatypusRuntimePanelUi extends DestinationPanelUi {

        protected PlatypusRuntimePanel panel;

        public PlatypusRuntimePanelUi(PlatypusRuntimePanel panel) {
            super(panel);


            this.panel = panel;
        }

        @Override
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new PlatypusRuntimeSwingUi(panel, container);
            }

            return super.getSwingUi(container);
        }
    }

    public static class PlatypusRuntimeSwingUi extends DestinationPanelSwingUi {

        protected PlatypusRuntimePanel panel;

        public PlatypusRuntimeSwingUi(
                final PlatypusRuntimePanel panel,
                final SwingContainer container) {
            super(panel, container);

            this.panel = panel;
        }

        // protected ////////////////////////////////////////////////////////////////
        @Override
        protected void initialize() {

            super.initialize();
        }

        @Override
        protected void saveInput() {
            super.saveInput();
        }

        @Override
        protected String validateInput() {
            String errorMessage = super.validateInput();
            
            if (errorMessage == null) {
                // #222846 - non-ascii characters in installation path
                File installationFolder = new File(getDestinationPath());
                CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
                if (!encoder.canEncode(installationFolder.getAbsolutePath())) {
                    return StringUtils.format(panel.getProperty(ERROR_CONTAINS_NON_ASCII_CHARS));
                }
            }
            
            return errorMessage;
        }

    }
    /////////////////////////////////////////////////////////////////////////////////
    // Constants   
    public static final String ERROR_CONTAINS_NON_ASCII_CHARS =
            "error.contains.non.ascii.chars"; // NOI18N
    
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(PlatypusRuntimePanel.class,
            "P.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(PlatypusRuntimePanel.class,
            "P.description"); // NOI18N
    public static final String DEFAULT_ERROR_CONTAINS_NON_ASCII_CHARS =
            ResourceUtils.getString(PlatypusRuntimePanel.class,
            "P.error.contains.non.ascii.chars"); // NOI18N   
}

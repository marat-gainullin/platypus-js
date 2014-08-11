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

package com.eas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.installer.utils.applications.NetBeansUtils;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.product.components.ProductConfigurationLogic;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.applications.JavaUtils;
import org.netbeans.installer.utils.exceptions.InitializationException;
import org.netbeans.installer.utils.exceptions.InstallationException;
import org.netbeans.installer.utils.exceptions.UninstallationException;
import org.netbeans.installer.utils.helper.Status;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.wizard.Wizard;
import org.netbeans.installer.wizard.components.WizardComponent;

public class ConfigurationLogic extends ProductConfigurationLogic {

    private List<WizardComponent> wizardComponents;

    // constructor //////////////////////////////////////////////////////////////////
    public ConfigurationLogic() throws InitializationException {
        wizardComponents = Wizard.loadWizardComponents(
                WIZARD_COMPONENTS_URI,
                getClass().getClassLoader());
    }

    public List<WizardComponent> getWizardComponents() {
        return wizardComponents;
    }

    @Override
    public boolean allowModifyMode() {
        return false;
    }

    private boolean isPlatypusRuntimeRegistred(File nbLocation) throws IOException {
        return new File (nbLocation, "platypusdesigner/config/Platypus/designer").exists();
    }

    @Override
    public void install(Progress progress) throws InstallationException {
        final File location = getProduct().getInstallationLocation();
        
        //get bundled registry to perform further runtime integration
        //http://wiki.netbeans.org/NetBeansInstallerIDEAndRuntimesIntegration
        Registry bundledRegistry = new Registry();
        try {
            final String bundledRegistryUri = System.getProperty(
                    Registry.BUNDLED_PRODUCT_REGISTRY_URI_PROPERTY);

            bundledRegistry.loadProductRegistry(
                    (bundledRegistryUri != null) ? bundledRegistryUri : Registry.DEFAULT_BUNDLED_PRODUCT_REGISTRY_URI);
        } catch (InitializationException e) {
            LogManager.log("Cannot load bundled registry", e);
        }

        try {
            progress.setDetail("Integrating Platypus.js Runtime..."); // NOI18N

            final List<Product> ides =
                    Registry.getInstance().getProducts("designer");
            List<Product> productsToIntegrate = new ArrayList<Product>();
            for (Product ide : ides) {
                if (ide.getStatus() == Status.INSTALLED) {
                    LogManager.log("... checking if " + getProduct().getDisplayName() + " can be integrated with " + ide.getDisplayName() + " at " + ide.getInstallationLocation());
                    final File ideLocation = ide.getInstallationLocation();
                    if (ideLocation != null && FileUtils.exists(ideLocation) && !FileUtils.isEmpty(ideLocation)) {
                        final Product bundledProduct = bundledRegistry.getProduct(ide.getUid(), ide.getVersion());
                        if (bundledProduct != null) {
                            //one of already installed IDEs is in the bundled registry as well - we need to integrate with it
                            productsToIntegrate.add(ide);
                            LogManager.log("... will be integrated since this produce is also bundled");
                        } else {
                            //check if this IDE is not integrated with any other GF instance - we need integrate with such IDE instance
                            try {
                                if(!isPlatypusRuntimeRegistred(location)) {
                                    LogManager.log("... will be integrated since there it is not yet integrated with any instance or such an instance does not exist");
                                    productsToIntegrate.add(ide);
                                } else {
                                    LogManager.log("... will not be integrated since it is already integrated with another instance");
                                }
                            } catch (IOException e) {
                                LogManager.log(e);
                            }
                        }
                    }
                }
            }

            for (Product productToIntegrate : productsToIntegrate) {
                final File ideLocation = productToIntegrate.getInstallationLocation();
                LogManager.log("... integrate " + getProduct().getDisplayName() + " with " + productToIntegrate.getDisplayName() + " installed at " + ideLocation);
                /////////////////////////////////////////////////////////////////////////////
                // Reference: http://wiki.netbeans.org/wiki/view/TomcatAutoRegistration
                if(!registerPlatypusRuntime(ideLocation, location)) {
                    continue;
                }
            }
        } catch (IOException e) {
            throw new InstallationException(
                    getString("CL.install.error.ide.integration"), // NOI18N
                    e);
        }

        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);

    }


private boolean registerPlatypusRuntime(File nbLocation, File platypusRuntimeLocation) throws IOException {
        File javaExe = JavaUtils.getExecutable(new File(System.getProperty("java.home")));
        String [] cp = {
            "platform/core/*",
            "platform/lib/*",
	    "platform/modules/*",
	    "ide/modules/*",
            "platypusdesigner/modules/com-eas-designer-application.jar"
        };
        String mainClass = "com.eas.designer.application.platform.AutomaticRegistration";
        List <String> commands = new ArrayList <String> ();
        File nbCluster = new File(nbLocation, "platypusdesigner");
        commands.add(javaExe.getAbsolutePath());
        commands.add("-cp");
        commands.add(StringUtils.asString(cp, File.pathSeparator));
        commands.add(mainClass);
        commands.add("--add");
        commands.add(nbCluster.getAbsolutePath());
        commands.add(platypusRuntimeLocation.getAbsolutePath());
        
        return SystemUtils.executeCommand(nbLocation, commands.toArray(new String[]{})).getErrorCode() == 0;
    }

    @Override
    public void uninstall(Progress progress) throws UninstallationException {

        /////////////////////////////////////////////////////////////////////////////
        progress.setPercentage(Progress.COMPLETE);
    }

    @Override
    public String getIcon() {
            return null;
    }

    @Override
    public boolean registerInSystem() {
        return true;
    }

    @Override
    public boolean requireLegalArtifactSaving() {
        return false;
    }

    @Override
    public boolean requireDotAppForMacOs() {
        return true;
    }

    @Override
    public boolean wrapForMacOs() {
        return true;
    }

    public static final String WIZARD_COMPONENTS_URI =
            "resource:" + // NOI18N
            "com/eas/wizard.xml"; // NOI18N

    private static final String ALL_USERS_PROPERTY_VALUE =
            "all.users"; // NOI18N
    private static final String CURRENT_USER_PROPERTY_VALUE =
            "current.user"; // NOI18N
}

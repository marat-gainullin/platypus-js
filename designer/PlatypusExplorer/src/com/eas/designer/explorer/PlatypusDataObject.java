/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer;

import com.eas.client.DatabasesClient;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.script.Scripts;
import com.eas.util.ListenerRegistration;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.RequestProcessor;

/**
 * Base class for Platypus data objects.
 *
 * @author vv
 */
public abstract class PlatypusDataObject extends MultiDataObject {

    private static final RequestProcessor RP = new RequestProcessor(PlatypusDataObject.class.getName(), 10);
    private final Set<PlatypusProject.ClientChangeListener> clientListeners = new HashSet<>();
    protected ListenerRegistration projectClientListener;

    public PlatypusDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        if (getProject() != null) {
            projectClientListener = getProject().addClientChangeListener(new PlatypusProject.ClientChangeListener() {

                @Override
                public void connected(String aDatasourceName) {
                    fireClientConnected(aDatasourceName);
                }

                @Override
                public void disconnected(String aDatasourceName) {
                    fireClientDisconnected(aDatasourceName);
                }

                @Override
                public void defaultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName) {
                    fireDeafultDatasourceNameChanged(aOldDatasourceName, aNewDatasourceName);
                }
            });
        }
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    /**
     * WARNING!!! This method is executed in a separate thread.
     *
     * @throws java.lang.Exception
     */
    protected abstract void validateModel() throws Exception;

    protected boolean validationStarted;

    public boolean isValidationStarted() {
        return validationStarted;
    }

    public void startModelValidating() {
        if (!isModelValid() && !validationStarted) {
            validationStarted = true;
            EventQueue.invokeLater(() -> {
                Scripts.LocalContext context = Scripts.getContext();
                RP.execute(() -> {
                    Scripts.setContext(context);
                    try {
                        validateModel();
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusDataObject.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                    } finally {
                        Scripts.setContext(null);
                        EventQueue.invokeLater(() -> {
                            validationStarted = false;
                            setModelValid(true);
                        });
                    }
                });
            });
        }
    }

    @Override
    protected void handleDelete() throws IOException {
        dispose();
        super.handleDelete();
    }

    @Override
    protected void dispose() {
        if (projectClientListener != null) {
            projectClientListener.remove();
            projectClientListener = null;
        }
        super.dispose();
    }

    public ListenerRegistration addClientChangeListener(final PlatypusProject.ClientChangeListener onChange) {
        clientListeners.add(onChange);
        return () -> {
            clientListeners.remove(onChange);
        };
    }

    private void fireClientConnected(final String aDatasourceName) {
        for (PlatypusProject.ClientChangeListener onChange : clientListeners.toArray(new PlatypusProject.ClientChangeListener[]{})) {
            onChange.connected(aDatasourceName);
        }
    }

    private void fireClientDisconnected(String aDatasourceName) {
        for (PlatypusProject.ClientChangeListener onChange : clientListeners.toArray(new PlatypusProject.ClientChangeListener[]{})) {
            onChange.disconnected(aDatasourceName);
        }
    }

    private void fireDeafultDatasourceNameChanged(String aOldDatasourceName, String aNewDatasourceName) {
        for (PlatypusProject.ClientChangeListener onChange : clientListeners.toArray(new PlatypusProject.ClientChangeListener[]{})) {
            onChange.defaultDatasourceNameChanged(aOldDatasourceName, aNewDatasourceName);
        }
    }

    public final PlatypusProject getProject() {
        Project pr = FileOwnerQuery.getOwner(getPrimaryFile());
        if (pr instanceof PlatypusProject) {
            return (PlatypusProject) pr;
        } else {
            return null;
        }
    }

    public DatabasesClient getBasesProxy() {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getBasesProxy();
        }
        return null;
    }
    private final Set<Runnable> modelValidListeners = new HashSet<>();
    protected boolean modelValid = true;

    public boolean isModelValid() {
        return modelValid;
    }

    public void setModelValid(boolean aValue) {
        boolean oldValue = modelValid;
        modelValid = aValue;
        if (oldValue != modelValid) {
            fireModelValidChanged();
        }
    }

    public ListenerRegistration addModelValidChangeListener(final Runnable onChange) {
        modelValidListeners.add(onChange);
        return () -> {
            modelValidListeners.remove(onChange);
        };
    }

    private void fireModelValidChanged() {
        for (Runnable onChange : modelValidListeners.toArray(new Runnable[]{})) {
            onChange.run();
        }
    }

    public FileObject getAppRoot() throws Exception {
        PlatypusProject project = getProject();
        if (project != null) {
            return project.getSrcRoot();
        } else {
            return null;
        }
    }
}

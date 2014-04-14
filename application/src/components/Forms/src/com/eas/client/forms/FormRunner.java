/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.Client;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.forms.api.ControlsWrapper;
import com.eas.client.forms.api.FormWindowEventsIProxy;
import com.eas.client.forms.api.components.DesktopPane;
import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.FormEventsExecutor;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.controls.events.WindowEventsIProxy;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import com.eas.resources.images.IconCache;
import com.eas.script.EventMethod;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import com.eas.util.exceptions.ClosedManageException;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.mozilla.javascript.*;

/**
 *
 * @author mg
 */
public class FormRunner extends ScriptRunner implements FormEventsExecutor {

    public static final String FORM_ID_AS_FIRST_REQUIRED_MSG = "First element of form key must be a valid form id.";
    public static final String FORM_KEY_REQUIRED_MSG = "Form key must be not null and must contain at least one element (form id).";
    public static final String VIEW_SCRIPT_NAME = "view";
    protected static ScriptableObject platypusGuiLibScope;
    protected static final Map<String, FormRunner> showingForms = new HashMap<>();
    protected static Function onChange = null;

    public static FormRunner[] getShownForms() {
        synchronized (FormRunner.class) {
            List<FormRunner> notNullForms = new ArrayList<>();
            for (FormRunner f : showingForms.values()) {
                if (f != null) {
                    notNullForms.add(f);
                }
            }
            return notNullForms.toArray(new FormRunner[]{});
        }
    }

    public static FormRunner getShownForm(String aFormKey) {
        synchronized (FormRunner.class) {
            return showingForms.get(aFormKey);
        }
    }

    public static Function getOnChange() {
        return onChange;
    }

    public static void setOnChange(Function aValue) {
        onChange = aValue;
    }

    protected static void initializePlatypusGuiLibScope() throws IOException {
        if (platypusGuiLibScope == null) {
            try {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        // Fix from rsp! Let's initialize library functions
                        // in top-level scope.
                        //platypusGuiLibScope = (ScriptableObject) context.newObject(platypusStandardLibScope);
                        //platypusGuiLibScope.setPrototype(platypusStandardLibScope);
                        platypusGuiLibScope = platypusStandardLibScope;
                        importScriptLibrary("/com/eas/client/forms/gui.js", "gui", cx, platypusGuiLibScope);
                        return null;
                    }
                });
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }

    private void checkUndecorated(RootPaneContainer topContainer) {
        if (UIManager.getLookAndFeel().getClass().getName().startsWith("com.jtattoo.plaf")
                || UIManager.getLookAndFeel().getClass().getName().startsWith("de.javasoft.plaf.synthetica")) {
            if (topContainer instanceof JFrame) {
                ((JFrame) topContainer).setUndecorated(true);
            } else if (topContainer instanceof JDialog) {
                ((JDialog) topContainer).setUndecorated(true);
            }
        }
    }

    protected void showingFormsChanged() {
        if (onChange != null) {
            try {
                ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        onChange.call(cx, FormRunner.this, FormRunnerPrototype.getInstance().getConstructor(), new Object[]{ScriptUtils.javaToJS(new ScriptSourcedEvent(FormRunner.this), FormRunner.this)});
                        return null;
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(FormRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class WindowClosingReflector extends WindowAdapter implements InternalFrameListener {

        @Override
        public void windowClosed(WindowEvent e) {
            surface = null;
            synchronized (FormRunner.class) {
                showingForms.remove(FormRunner.this.getFormKey());
                showingFormsChanged();
            }
        }

        @Override
        public void internalFrameOpened(InternalFrameEvent e) {
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
        }

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            surface = null;
            synchronized (FormRunner.class) {
                showingForms.remove(FormRunner.this.getFormKey());
                showingFormsChanged();
            }
        }

        @Override
        public void internalFrameIconified(InternalFrameEvent e) {
        }

        @Override
        public void internalFrameDeiconified(InternalFrameEvent e) {
        }

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {
        }

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {
        }
    }
    // From design info
    protected int defaultCloseOperation;
    protected ImageIcon icon;
    protected String title;
    protected boolean resizable;
    protected boolean minimizable;
    protected boolean maximizable;
    protected boolean undecorated;
    protected float opacity;
    protected boolean alwaysOnTop;
    protected boolean locationByPlatform;
    protected Point formLocation;
    protected Dimension designedViewSize;
    protected Dimension formSize;
    protected Dimension windowDecorSize = new Dimension();
    protected WindowEventsIProxy windowHandler;
    // Runtime 
    protected JPanel form;
    protected Map<String, JComponent> components;
    protected String formKey;
    // frequent runtime
    protected Container surface;
    protected Object closeCallbackParameter;

    static {
        try {
            initializePlatypusStandardLibScope();
            assert standardObjectsScope != null;
            assert platypusStandardLibScope != null;
            initializePlatypusGuiLibScope();
        } catch (Exception ex) {
            Logger.getLogger(ScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FormRunner(String aFormId, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, Object[] args) throws Exception {
        super(aFormId, aClient, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, args);
        setPrototype(FormRunnerPrototype.getInstance());
        formKey = aFormId;
    }
    
    // Script interface
    
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows the form as an ordinary window.\n"
            + " */";

    /**
     * Script method showing the form as ordinary frame
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        displayAsFrame();
    }

    public Object showDialog(Function onOkModalResult) throws Exception {
        return showModal(onOkModalResult);
    }

    private static final String SHOW_MODAL_JSDOC = ""
            + "/**\n"
            + " * Shows the form as a dialog (modal window).\n"
            + " * @param callback a callback handler function\n"
            + " */";

    /**
     * Script method showing the form as modal dialog
     *
     * @param onOkModalResult a callback handler function
     * @return value of the modalResult property
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(params = {"callback"}, jsDoc = SHOW_MODAL_JSDOC)
    public Object showModal(Function onOkModalResult) throws Exception {
        return displayAsDialog(onOkModalResult);
    }

    private static final String SHOW_INTERNAL_FRAME_JSDOC = ""
            + "/**\n"
            + " * Shows the form as an internal window in a desktop.\n"
            + " * @param desktop the parent desktop object\n"
            + " */";

    /**
     * Script method showing the form as an internal frame.
     *
     * @param desktopObject Should be an instance of JDesktopPane
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(params = {"desktop"}, jsDoc = SHOW_INTERNAL_FRAME_JSDOC)
    public void showInternalFrame(Object desktopObject) throws Exception {
        DesktopPane desktopPane;
        if (desktopObject instanceof NativeJavaObject) {
            desktopObject = Context.jsToJava(desktopObject, DesktopPane.class);
        }
        if (desktopObject instanceof DesktopPane) {
            desktopPane = (DesktopPane) desktopObject;
        } else {
            throw new IllegalArgumentException("argument should be instance of DesktopPane, not " + desktopObject);
        }
        displayAsInternalFrame(desktopPane);
    }

    private static final String SHOW_ON_PANEL_FRAME_JSDOC = ""
            + "/**\n"
            + " * Script method showing the form in embedded mode on the panel.\n"
            + " * @param panel the parent panel\n"
            + " */";

    @ScriptFunction(params = {"panel"}, jsDoc = SHOW_ON_PANEL_FRAME_JSDOC)
    public void showOnPanel(Object aParent) throws Exception {
        com.eas.client.forms.api.Container<?> hostPanel;
        if (aParent != null) {
            if (aParent instanceof NativeJavaObject) {
                aParent = Context.jsToJava(aParent, com.eas.client.forms.api.Container.class);
            }
            if (aParent instanceof com.eas.client.forms.api.Container<?>) {
                hostPanel = (com.eas.client.forms.api.Container<?>) aParent;
            } else {
                throw new IllegalArgumentException("argument should be instance of Container, not " + aParent.getClass().getName());
            }
        } else {
            throw new IllegalArgumentException("argument should be not null");
        }
        displayOnContainer(ControlsWrapper.unwrap(hostPanel));
    }

    //TODO is redundant method?
    public String getFormId() {
        return getApplicationElementId();
    }

    private static final String FORM_KEY_JSDOC = ""
            + "/**\n"
            + " * The form key. Used to identify a form instance. Initialy set to the form's application element name.\n"
            + " */";

    @ScriptFunction(jsDoc = FORM_KEY_JSDOC)
    public String getFormKey() {
        return formKey;
    }

    @ScriptFunction()
    public void setFormKey(String aValue) {
        if (formKey == null ? aValue != null : !formKey.equals(aValue)) {
            synchronized (FormRunner.class) {
                showingForms.remove(formKey);
                formKey = aValue;
                if (isInOpenedWindow()) {
                    showingForms.put(formKey, this);
                    showingFormsChanged();
                }
            }
        }
    }

    private static final String IS_VISIBLE_JSDOC = ""
            + "/**\n"
            + " * Checks if this form is visible.\n"
            + " */";

    @ScriptFunction(jsDoc = IS_VISIBLE_JSDOC)
    public boolean getVisible() {
        return isFrameVisible() || isDialogVisible() || isInternalFrameVisible();
    }

    public boolean isFrameVisible() {
        return surface instanceof JFrame && surface.isVisible();
    }

    public boolean isDialogVisible() {
        return surface instanceof JDialog && surface.isVisible();
    }

    public boolean isInternalFrameVisible() {
        return surface instanceof JInternalFrame && (surface.isVisible() || ((JInternalFrame) surface).getDesktopIcon().isVisible());
    }

    public boolean isInOpenedWindow() {
        return isFrameVisible() || isDialogVisible() || isInternalFrameVisible();
    }

    public JDialog getDialog(Function onOkModalResult) throws Exception {
        showDialog(onOkModalResult);
        assert surface instanceof JDialog;
        return (JDialog) surface;
    }

    public JInternalFrame getInternalFrame(Object desktopObject) throws Exception {
        showInternalFrame(desktopObject);
        assert surface instanceof JInternalFrame;
        return (JInternalFrame) surface;
    }

    public JFrame getFrame() throws Exception {
        show();
        assert surface instanceof JFrame;
        return (JFrame) surface;
    }

    public void displayAsFrame() throws Exception {
        if (surface == null) {
            close(null);
            execute();
            final JFrame frame = new JFrame() {
                @Override
                protected void processWindowEvent(WindowEvent e) {
                    try {
                        super.processWindowEvent(e);
                    } catch (ClosedManageException ex) {
                        // no op
                    }
                }
            };

            frame.addWindowListener(new WindowClosingReflector());
            frame.getContentPane().setLayout(new BorderLayout());
            // configure frame
            frame.setDefaultCloseOperation(defaultCloseOperation);
            frame.setIconImage(icon != null ? icon.getImage() : null);
            frame.setTitle(title);
            frame.setResizable(resizable);
            frame.setUndecorated(undecorated);
            if (frame.isUndecorated()) {
                frame.setOpacity(opacity);
            }
            checkUndecorated(frame);
            frame.setAlwaysOnTop(alwaysOnTop);
            frame.setLocationByPlatform(locationByPlatform);
            frame.getContentPane().add(form, BorderLayout.CENTER);
            // add window listener
            // for unknown reasons, control events are also working
            windowHandler.setHandlee(frame);
            //
            Function windowOpenedHandler = windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened);
            windowHandler.getHandlers().remove(WindowEventsIProxy.windowOpened);
            try {
                synchronized (FormRunner.class) {
                    showingForms.put(formKey, this);
                }
                frame.setVisible(true);
                if (formSize != null) {
                    frame.setSize(formSize);
                } else if (designedViewSize != null) {
                    Insets decorInsets = frame.getInsets();
                    windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                    frame.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                } else {
                    frame.pack();
                }
                formSize = frame.getSize();
                if (formLocation != null && !locationByPlatform) {
                    frame.setLocation(formLocation);
                }
            } finally {
                windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, windowOpenedHandler);
            }
            surface = frame;
            formLocation = surface.getLocation();
            surface.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    formLocation = surface.getLocation();
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    formSize = surface.getSize();
                }
            });
            surface.revalidate();
            surface.repaint();
            showingFormsChanged();
        } else {
            surface.setVisible(true);
        }
    }

    public void displayAsInternalFrame(DesktopPane aDesktop) throws Exception {
        if (surface == null) {
            close(null);
            execute();
            JInternalFrame internalFrame = new PlatypusInternalFrame(this) {
                @Override
                public void doDefaultCloseAction() {
                    try {
                        super.doDefaultCloseAction();
                    } catch (ClosedManageException ex) {
                        // no op
                    }
                }
            };
            internalFrame.addInternalFrameListener(new WindowClosingReflector());
            internalFrame.getContentPane().setLayout(new BorderLayout());
            // configure frame
            internalFrame.setDefaultCloseOperation(defaultCloseOperation);
            internalFrame.setFrameIcon(icon);
            internalFrame.setTitle(title);
            internalFrame.setResizable(resizable);

            internalFrame.setClosable(true);
            internalFrame.setIconifiable(true);
            internalFrame.setMaximizable(true);
            internalFrame.getContentPane().add(form, BorderLayout.CENTER);

            // add window listener
            // for unknown reasons, control events are also working
            windowHandler.setHandlee(internalFrame);
            //
            Function windowOpenedHandler = windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened);
            windowHandler.getHandlers().remove(WindowEventsIProxy.windowOpened);
            try {
                synchronized (FormRunner.class) {
                    showingForms.put(formKey, this);
                }
                ControlsWrapper.unwrap(aDesktop).add(internalFrame);
                internalFrame.setVisible(true);
                if (formSize != null) {
                    internalFrame.setSize(formSize);
                } else if (designedViewSize != null) {
                    Insets decorInsets = internalFrame.getInsets();
                    windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                    internalFrame.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                } else {
                    internalFrame.pack();
                }
                formSize = internalFrame.getSize();
                if (formLocation != null && !locationByPlatform) {
                    internalFrame.setLocation(formLocation);
                }
            } finally {
                windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, windowOpenedHandler);
            }
            surface = internalFrame;
            formLocation = surface.getLocation();
            surface.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    formLocation = surface.getLocation();
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    formSize = surface.getSize();
                }
            });
            windowHandler.windowOpened(null);
            showingFormsChanged();
        } else {
            surface.setVisible(true);
        }
    }

    public Object displayAsDialog(final Function onOkModalResult) throws Exception {
        if (surface == null) {
            close(null);
            execute();
            JDialog dialog = new JDialog() {
                @Override
                protected void processWindowEvent(WindowEvent e) {
                    try {
                        if (e.getID() == WindowEvent.WINDOW_OPENED) {
                            if (formSize != null) {
                                surface.setSize(formSize);
                            } else if (designedViewSize != null) {
                                Insets decorInsets = surface.getInsets();
                                windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                                surface.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                            } else {
                                ((JDialog) surface).pack();
                            }
                            formSize = surface.getSize();
                            if (formLocation != null && !locationByPlatform) {
                                surface.setLocation(formLocation);
                            }
                            formLocation = surface.getLocation();

                            surface.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentMoved(ComponentEvent e) {
                                    formLocation = surface.getLocation();
                                }

                                @Override
                                public void componentResized(ComponentEvent e) {
                                    if (surface != null && surface.isVisible()) {
                                        formSize = surface.getSize();
                                    }
                                }
                            });

                            synchronized (FormRunner.class) {
                                showingForms.put(formKey, FormRunner.this);
                            }
                            surface.revalidate();
                            surface.repaint();
                            showingFormsChanged();
                        }
                        super.processWindowEvent(e);
                    } catch (ClosedManageException ex) {
                        // no op
                    }
                }
            };
            //dialog.addWindowListener(new WindowClosingReflector());
            dialog.getContentPane().setLayout(new BorderLayout());
            // configure dialog
            dialog.setDefaultCloseOperation(defaultCloseOperation);
            dialog.setIconImage(icon != null ? icon.getImage() : null);
            dialog.setTitle(title);
            dialog.setResizable(resizable);
            dialog.setUndecorated(undecorated);
            if (dialog.isUndecorated()) {
                dialog.setOpacity(opacity);
            }
            checkUndecorated(dialog);
            dialog.setLocationByPlatform(locationByPlatform);
            dialog.getContentPane().add(form, BorderLayout.CENTER);
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);

            // add window listener
            // for unknown reasons, control events are also working
            windowHandler.setHandlee(dialog);
            //
            surface = dialog;
            dialog.setVisible(true);
            final Object selected = closeCallbackParameter;
            surface = null;
            closeCallbackParameter = null;
            if (onOkModalResult != null && selected != Context.getUndefinedValue()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ScriptUtils.inContext(new ScriptAction() {
                                @Override
                                public Object run(Context cx) throws Exception {
                                    onOkModalResult.call(cx, FormRunner.this, FormRunner.this, new Object[]{selected});
                                    return null;
                                }
                            });
                        } catch (Exception ex) {
                            Logger.getLogger(FormRunner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            return selected;
        } else {
            return null;
        }
    }

    public void displayOnContainer(java.awt.Container aTarget) throws Exception {
        if (surface == null) {
            close(null);
            execute();
            if (aTarget.getLayout() != null) {// TODO: remove this code when script moving/resizing will be clear.
                aTarget.setLayout(new BorderLayout());
            }
            if (aTarget.getLayout() instanceof BorderLayout) {
                aTarget.add(form, BorderLayout.CENTER);
            }
            surface = aTarget;
            windowDecorSize = new Dimension();
            windowHandler.windowOpened(new WindowEvent(new JFrame(), WindowEvent.WINDOW_OPENED));
            aTarget.invalidate();
            form.invalidate();
            aTarget.validate();
            aTarget.repaint();
            if (surface.getLayout() == null) {
                if (formSize != null) {
                    form.setSize(formSize);
                } else if (designedViewSize != null) {
                    form.setSize(designedViewSize);
                }
                formSize = form.getSize();
                if (formLocation != null) {
                    form.setLocation(formLocation);
                }
            }
            designedViewSize = form.getSize();
            formLocation = form.getLocation();
            surface.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    formLocation = surface.getLocation();
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    formSize = surface.getSize();
                    designedViewSize = surface.getSize();
                }
            });
        }
    }
    private static final String CLOSE_JSDOC = ""
            + "/**\n"
            + " * Closes this form.\n"
            + " * @param obj an object to be passed as a result of a selection into <code>showModal</code> callback handler function.\n"
            + " */";

    @ScriptFunction(params = {"obj"}, jsDoc = CLOSE_JSDOC)
    public void close(Object aSelected) {
        if (surface != null) {
            try {
                if (surface instanceof Window) {
                    windowHandler.windowClosing(new WindowEvent(((Window) surface), WindowEvent.WINDOW_CLOSING));
                    ((Window) surface).dispose();
                } else if (surface instanceof JInternalFrame) {
                    windowHandler.internalFrameClosing(new InternalFrameEvent(((JInternalFrame) surface), WindowEvent.WINDOW_CLOSING));
                    ((JInternalFrame) surface).dispose();
                } else {
                    windowHandler.windowClosing(new WindowEvent(new JFrame(), WindowEvent.WINDOW_CLOSING));
                    windowHandler.windowClosed(new WindowEvent(new JFrame(), WindowEvent.WINDOW_CLOSED));
                    form.invalidate();
                    surface.remove(form);
                    surface.validate();
                    surface.repaint();
                }
                surface = null;
                closeCallbackParameter = aSelected;
            } catch (ClosedManageException ex) {
                // no op
            }
        }
    }

    private static final String MINIMIZE_JSDOC = ""
            + "/**\n"
            + " * Minimizes this form.\n"
            + " */";

    @ScriptFunction(jsDoc = MINIMIZE_JSDOC)
    public void minimize() {
        if (surface != null) {
            if (surface instanceof JFrame) {
                ((JFrame) surface).setState(JFrame.ICONIFIED);
            } else if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                try {
                    aFrame.setIcon(true);
                } catch (Exception e) {
                }
            }
        }
    }

    private static final String MAXIMIZE_JSDOC = ""
            + "/**\n"
            + " * Maximizes this form.\n"
            + " */";

    @ScriptFunction(jsDoc = MAXIMIZE_JSDOC)
    public void maximize() {
        if (surface != null) {
            if (surface instanceof JFrame) {
                ((JFrame) surface).setState(JFrame.MAXIMIZED_BOTH);
            } else if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                try {
                    aFrame.setMaximum(true);
                    aFrame.toFront();
                } catch (Exception e) {
                }
            }
        }
    }
    private static final String RESTORE_JSDOC = ""
            + "/**\n"
            + " * Restores this form state.\n"
            + " */";

    @ScriptFunction(jsDoc = RESTORE_JSDOC)
    public void restore() {
        if (surface != null) {
            if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                try {
                    aFrame.setMaximum(false);
                    aFrame.setIcon(false);
                } catch (Exception e) {
                }
                aFrame.toFront();
            } else if (surface instanceof JFrame) {
                ((JFrame) surface).setExtendedState(JFrame.NORMAL);
                ((JFrame) surface).toFront();
            }
        }
    }

    private static final String TO_FRONT_JSDOC = ""
            + "/**\n"
            + " * Moves form to the front position.\n"
            + " */";

    @ScriptFunction(jsDoc = TO_FRONT_JSDOC)
    public void toFront() {
        if (surface != null) {
            if (surface instanceof JInternalFrame) {
                try {
                    ((JInternalFrame) surface).setIcon(false);
                } catch (Exception e) {
                }
                ((JInternalFrame) surface).toFront();
            } else if (surface instanceof JFrame) {
                ((JFrame) surface).toFront();
            }
        }
    }
    private static final String GET_MINIMIZED_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form is minimized.\n"
            + " */";

    @ScriptFunction(jsDoc = GET_MINIMIZED_JSDOC)
    public boolean getMinimized() {
        if (surface != null) {
            if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                return aFrame.isIcon();
            } else if (surface instanceof JFrame) {
                JFrame aFrame = (JFrame) surface;
                return aFrame.getExtendedState() == JFrame.ICONIFIED;
            }
        }
        return false;
    }

    private static final String IS_MAXIMIZED_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form is maximized.\n"
            + " */";

    @ScriptFunction(jsDoc = IS_MAXIMIZED_JSDOC)
    public boolean getMaximized() {
        if (surface != null) {
            if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                return aFrame.isMaximum() && !aFrame.isIcon();
            } else if (surface instanceof JFrame) {
                JFrame aFrame = (JFrame) surface;
                return aFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
            }
        }
        return false;
    }

    private static final String LEFT_JSDOC = ""
            + "/**\n"
            + " * The distance for this form to the parent container's left side.\n"
            + " */";

    @ScriptFunction(jsDoc = LEFT_JSDOC)
    public int getLeft() {
        if (surface != null) {
            return surface.getLocation().x;
        } else if (formLocation != null) {
            return formLocation.x;
        } else {
            return 0;
        }
    }

    @ScriptFunction
    public void setLeft(int aValue) {
        if (surface != null) {
            surface.setLocation(aValue, surface.getLocation().y);
        } else if (formLocation != null) {
            formLocation.x = aValue;
        }
        locationByPlatform = false;
    }

    private static final String TOP_JSDOC = ""
            + "/**\n"
            + " * The distance for this form to the parent container's top side.\n"
            + " */";

    @ScriptFunction(jsDoc = TOP_JSDOC)
    public int getTop() {
        if (surface != null) {
            return surface.getLocation().y;
        } else if (formLocation != null) {
            return formLocation.y;
        } else {
            return 0;
        }
    }

    @ScriptFunction
    public void setTop(int aValue) {
        if (surface != null) {
            surface.setLocation(surface.getLocation().x, aValue);
        } else if (formLocation != null) {
            formLocation.y = aValue;
        }
        locationByPlatform = false;
    }

    private static final String WIDTH_JSDOC = ""
            + "/**\n"
            + " * The form's width.\n"
            + " */";

    @ScriptFunction(jsDoc = WIDTH_JSDOC)
    public int getWidth() {
        if (surface != null) {
            return surface.getWidth();
        } else if (formSize != null) {
            return formSize.width;
        } else {
            return designedViewSize.width + windowDecorSize.width;
        }
    }

    @ScriptFunction
    public void setWidth(int aValue) {
        if (surface != null) {
            surface.setSize(aValue, surface.getHeight());
        } else {
            if (formSize == null) {
                formSize = new Dimension();
            }
            formSize.width = aValue;
        }
    }

    private static final String HEIGHT_JSDOC = ""
            + "/**\n"
            + " * The form's height.\n"
            + " */";

    @ScriptFunction(jsDoc = HEIGHT_JSDOC)
    public int getHeight() {
        if (surface != null) {
            return surface.getHeight();
        } else if (formSize != null) {
            return formSize.height;
        } else {
            return designedViewSize.height + windowDecorSize.height;
        }
    }

    @ScriptFunction
    public void setHeight(int aValue) {
        if (surface != null) {
            surface.setSize(surface.getWidth(), aValue);
        } else {
            if (formSize == null) {
                formSize = new Dimension();
            }
            formSize.height = aValue;
        }
    }

    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    public void setDefaultCloseOperation(int aValue) {
        defaultCloseOperation = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setDefaultCloseOperation(defaultCloseOperation);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setDefaultCloseOperation(defaultCloseOperation);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setDefaultCloseOperation(defaultCloseOperation);
        }
    }

    private static final String ICON_JSDOC = ""
            + "/**\n"
            + " * The form's icon.\n"
            + " */";

    @ScriptFunction(jsDoc = ICON_JSDOC)
    public Object getIcon() {
        return icon;
    }

    @ScriptFunction
    public void setIcon(Object aValue) {
        if (aValue instanceof Wrapper && ((Wrapper) aValue).unwrap() instanceof ImageIcon) {
            icon = (ImageIcon) ((Wrapper) aValue).unwrap();
            if (surface instanceof JDialog) {
                ((JDialog) surface).setIconImage(icon != null ? icon.getImage() : null);
            }
            if (surface instanceof JInternalFrame) {
                ((JInternalFrame) surface).setFrameIcon(icon);
            }
            if (surface instanceof JFrame) {
                ((JFrame) surface).setIconImage(icon != null ? icon.getImage() : null);
            }
        }
    }

    private static final String TITLE_JSDOC = ""
            + "/**\n"
            + " * The form's title text.\n"
            + " */";

    @ScriptFunction(jsDoc = TITLE_JSDOC)
    public String getTitle() {
        return title;
    }

    @ScriptFunction
    public void setTitle(String aValue) {
        title = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setTitle(title);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setTitle(title);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setTitle(title);
        }
    }

    private static final String RESIZABLE_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form resizable.\n"
            + " */";

    @ScriptFunction(jsDoc = RESIZABLE_JSDOC)
    public boolean getResizable() {
        return resizable;
    }

    @ScriptFunction
    public void setResizable(boolean aValue) {
        resizable = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setResizable(resizable);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setResizable(resizable);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setResizable(resizable);
        }
    }

    private static final String MAXIMIZABLE_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form maximizable.\n"
            + " */";

    @ScriptFunction(jsDoc = MAXIMIZABLE_JSDOC)
    public boolean getMaximizable() {
        return maximizable;
    }

    @ScriptFunction
    public void setMaximizable(boolean aValue) {
        maximizable = aValue;
        if (surface instanceof JDialog) {
            //((JDialog) surface).setMaximizable(maximizable);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setMaximizable(maximizable);
        }
        if (surface instanceof JFrame) {
            //((JFrame) surface).setMaximizable(maximizable);
        }
    }

    private static final String MINIMIZABLE_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form minimizable.\n"
            + " */";

    @ScriptFunction(jsDoc = MINIMIZABLE_JSDOC)
    public boolean getMinimizable() {
        return minimizable;
    }

    @ScriptFunction
    public void setMinimizable(boolean aValue) {
        minimizable = aValue;
        if (surface instanceof JDialog) {
            //((JDialog) surface).setMinimizable(minimizable);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setIconifiable(minimizable);
        }
        if (surface instanceof JFrame) {
            //((JFrame) surface).setMinimizable(minimizable);
        }
    }

    private static final String UNDECORATED_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if no decoration to be enabled for this form.\n"
            + " */";

    @ScriptFunction(jsDoc = UNDECORATED_JSDOC)
    public boolean getUndecorated() {
        return undecorated;
    }

    @ScriptFunction
    public void setUndecorated(boolean aValue) {
        undecorated = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setUndecorated(undecorated);
        }
        if (surface instanceof JInternalFrame) {
            //((JInternalFrame) surface).setUndecorated(undecorated);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setUndecorated(undecorated);
        }
    }

    private static final String OPACITY_JSDOC = ""
            + "/**\n"
            + " * The opacity of the form.\n"
            + " */";

    @ScriptFunction(jsDoc = OPACITY_JSDOC)
    public float getOpacity() {
        return opacity;
    }

    @ScriptFunction
    public void setOpacity(float aValue) {
        opacity = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setOpacity(opacity);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setOpaque(opacity > 0.5f);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setOpacity(opacity);
        }
    }
 
    private static final String ALWAYS_ON_TOP_JSDOC = ""
            + "/**\n"
            + " * Determines whether this window should always be above other windows.\n"
            + " */";

    @ScriptFunction(jsDoc = ALWAYS_ON_TOP_JSDOC)
    public boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean aValue) {
        alwaysOnTop = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setAlwaysOnTop(alwaysOnTop);
        }
        if (surface instanceof JInternalFrame) {
            //((JInternalFrame) surface).setAlwaysOnTop(alwaysOnTop);
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setAlwaysOnTop(alwaysOnTop);
        }
    }

    private static final String LOCATION_BY_PLATFORM_JSDOC = ""
            + "/**\n"
            + " * Determines whether this form should appear at the default location\n"
            + " * for the native windowing system or at the current location.\n"
            + " */";

    @ScriptFunction(jsDoc = LOCATION_BY_PLATFORM_JSDOC)
    public boolean getLocationByPlatform() {
        return locationByPlatform;
    }

    @ScriptFunction
    public void setLocationByPlatform(boolean aValue) {
        locationByPlatform = aValue;
        if (surface instanceof JDialog) {
            ((JDialog) surface).setLocationByPlatform(locationByPlatform);
        }
        if (surface instanceof JInternalFrame) {
            //((JInternalFrame) surface).setLocationByPlatform(locationByPlatform)
        }
        if (surface instanceof JFrame) {
            ((JFrame) surface).setLocationByPlatform(locationByPlatform);
        }
    }

    private static final String ON_WINDOW_OPENED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>before open</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_OPENED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowOpened() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened) : null;
    }

    @ScriptFunction
    public void setOnWindowOpened(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, aValue);
        }
    }

    private static final String ON_WINDOW_CLOSING_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>before close</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_CLOSING_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowClosing() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosing) : null;
    }

    @ScriptFunction()
    public void setOnWindowClosing(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosing, aValue);
        }
    }

    private static final String ON_WINDOW_CLOSED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after close</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_CLOSED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowClosed() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosed) : null;
    }

    @ScriptFunction
    public void setOnWindowClosed(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosed, aValue);
        }
    }

    private static final String ON_WINDOW_MINIMIZED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after minimize</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_MINIMIZED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowMinimized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowIconified) : null;
    }

    @ScriptFunction
    public void setOnWindowMinimized(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowIconified, aValue);
        }
    }

    private static final String ON_WINDOW_RESTORED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after restore</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_RESTORED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowRestored() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowRestored) : null;
    }

    @ScriptFunction
    public void setOnWindowRestored(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowRestored, aValue);
        }
    }

    private static final String ON_WINDOW_MAXIMIZED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after maximize</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_MAXIMIZED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowMaximized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowMaximized) : null;
    }

    @ScriptFunction
    public void setOnWindowMaximized(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowMaximized, aValue);
        }
    }

    private static final String ON_WINDOW_ACTIVATED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after activate</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_ACTIVATED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowActivated() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowActivated) : null;
    }

    @ScriptFunction
    public void setOnWindowActivated(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowActivated, aValue);
        }
    }

    private static final String ON_WINDOW_DEACTIVATED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after deactivate</i> event.\n"
            + " */";
    
    @ScriptFunction(jsDoc = ON_WINDOW_DEACTIVATED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.api.events.WindowEvent.class)
    public Function getOnWindowDeactivated() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowDeactivated) : null;
    }

    @ScriptFunction
    public void setOnWindowDeactivated(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowDeactivated, aValue);
        }
    }

    @Override
    protected void shrink() throws Exception {
        close(null);
        if (windowHandler != null) {
            windowHandler.setHandlee(null);
        }
        windowHandler = null;
        form = null;
        components = null;
        super.shrink();
    }

    @Override
    protected void prepare(ScriptDocument scriptDoc, Object[] args) throws Exception {
        prepareRoles(scriptDoc);
        prepareModel(scriptDoc);
        prepareForm(scriptDoc);
        prepareScript(scriptDoc, args);
    }

    protected void prepareForm(ScriptDocument scriptDoc) throws Exception {
        assert scriptDoc instanceof FormDocument;
        FormDocument formDoc = (FormDocument) scriptDoc;
        defaultCloseOperation = formDoc.getFormDesignInfo().getDefaultCloseOperation();
        icon = IconCache.getIcon(formDoc.getFormDesignInfo().getIconImage());
        title = formDoc.getFormDesignInfo().getTitle();
        if (title == null || title.isEmpty()) {
            title = formDoc.getTitle();
        }
        resizable = formDoc.getFormDesignInfo().isResizable();
        undecorated = formDoc.getFormDesignInfo().isUndecorated();
        opacity = formDoc.getFormDesignInfo().getOpacity();
        alwaysOnTop = formDoc.getFormDesignInfo().isAlwaysOnTop();
        locationByPlatform = formDoc.getFormDesignInfo().isLocationByPlatform();
        designedViewSize = formDoc.getFormDesignInfo().getDesignedPreferredSize();
        windowHandler = new FormWindowEventsIProxy(this);
        final DbSwingFactory factory = new FormFactory(this, model);
        final FormDesignInfo fdi = formDoc.getFormDesignInfo();//.copy();
        fdi.accept(factory);
        form = factory.getResult();
        components = new HashMap<>();
        components.putAll(factory.getNonvisuals());
        components.putAll(factory.getComponents());
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                for (Entry<String, JComponent> entry : components.entrySet()) {
                    if (form != entry.getValue() && !(entry.getValue() instanceof ButtonGroupWrapper)) {
                        defineProperty(entry.getKey(), publishComponent(entry.getValue(), FormRunner.this, factory.getControlDesignInfos().get(entry.getKey())), READONLY);
                    }
                }
                for (Entry<String, JComponent> entry : components.entrySet()) {
                    if (form != entry.getValue() && (entry.getValue() instanceof ButtonGroupWrapper)) {
                        defineProperty(entry.getKey(), publishComponent(entry.getValue(), FormRunner.this, factory.getControlDesignInfos().get(entry.getKey())), READONLY);
                    }
                }
                FormRunner.super.delete(VIEW_SCRIPT_NAME);
                ControlsWrapper viewWrapper = new ControlsWrapper(form);
                (new PanelDesignInfo()).accept(viewWrapper);
                defineProperty(VIEW_SCRIPT_NAME, ScriptUtils.javaToJS(viewWrapper.getResult(), FormRunner.this), READONLY);
                return null;
            }
        });
    }

    @Override
    public String getClassName() {
        return FormRunner.class.getName();
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
            "show",
            "showModal",
            "showOnPanel",
            "showInternalFrame",
            "close",
            "minimize",
            "maximize",
            "restore",
            "toFront"
        }, FormRunner.class, EMPTY);
        defineProperty("formKey", FormRunner.class, EMPTY);
        defineProperty("visible", FormRunner.class, READONLY);
        defineProperty("left", FormRunner.class, EMPTY);
        defineProperty("top", FormRunner.class, EMPTY);
        defineProperty("width", FormRunner.class, EMPTY);
        defineProperty("height", FormRunner.class, EMPTY);
        defineProperty("minimized", FormRunner.class, READONLY);
        defineProperty("maximized", FormRunner.class, READONLY);
        defineProperty("minimizable", FormRunner.class, EMPTY);
        defineProperty("maximizable", FormRunner.class, EMPTY);
        defineProperty("defaultCloseOperation", FormRunner.class, EMPTY);
        defineProperty("icon", FormRunner.class, EMPTY);
        defineProperty("title", FormRunner.class, EMPTY);
        defineProperty("resizable", FormRunner.class, EMPTY);
        defineProperty("undecorated", FormRunner.class, EMPTY);
        defineProperty("opacity", FormRunner.class, EMPTY);
        defineProperty("alwaysOnTop", FormRunner.class, EMPTY);
        defineProperty("locationByPlatform", FormRunner.class, EMPTY);
        defineProperty("onWindowOpened", FormRunner.class, EMPTY);
        defineProperty("onWindowClosing", FormRunner.class, EMPTY);
        defineProperty("onWindowClosed", FormRunner.class, EMPTY);
        defineProperty("onWindowMinimized", FormRunner.class, EMPTY);
        defineProperty("onWindowRestored", FormRunner.class, EMPTY);
        defineProperty("onWindowMaximized", FormRunner.class, EMPTY);
        defineProperty("onWindowActivated", FormRunner.class, EMPTY);
        defineProperty("onWindowDeactivated", FormRunner.class, EMPTY);
    }

    @Override
    public Object executeEvent(final Function aHandler, final Scriptable aEventThis, final Object anEvent) {
        // The components map must be filled before any event can occur.
        if (components != null && aHandler != null) {
            try {
                return ScriptUtils.inContext(new ScriptAction() {
                    @Override
                    public Object run(Context cx) throws Exception {
                        return ScriptUtils.js2Java(aHandler.call(cx, FormRunner.this, aEventThis, new Object[]{anEvent}));
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(FormRunner.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        return Context.getUndefinedValue();
    }

    protected static NativeJavaHostObject publishComponent(JComponent aComp, Scriptable aScope, ControlDesignInfo aDesignInfo) {
        ControlsWrapper apiWrapper = new ControlsWrapper(aComp);
        aDesignInfo.accept(apiWrapper);
        Object published = ScriptUtils.javaToJS(apiWrapper.getResult() != null ? apiWrapper.getResult() : aComp, aScope);
        NativeJavaHostObject res = (published instanceof NativeJavaHostObject) ? (NativeJavaHostObject) published : null;
        ControlEventsIProxy eventsProxy = ControlsWrapper.getEventsProxy(apiWrapper.getResult());
        if (eventsProxy != null) {
            eventsProxy.setEventThis(res);
        }
        if (aComp instanceof DbControlPanel) {
            ((DbControlPanel) aComp).setScriptScope(aScope);
            ((DbControlPanel) aComp).setEventsThis(res);
        }
        if (aComp instanceof DbGrid) {
            ((DbGrid) aComp).setScriptScope(aScope);
            ((DbGrid) aComp).setEventThis(res);
        }
        if (aComp instanceof DbMap) {
            ((DbMap) aComp).setScriptScope(aScope);
            ((DbMap) aComp).setEventThis(res);
        }
        return res;
    }
}

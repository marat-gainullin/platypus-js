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
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.FormEventsExecutor;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.controls.events.WindowEventsIProxy;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import com.eas.resources.images.IconCache;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
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
            Context context = ScriptUtils.enterContext();
            try {
                // Fix from rsp! Let's initialize library functions
                // in top-level scope.
                //platypusGuiLibScope = (ScriptableObject) context.newObject(platypusStandardLibScope);
                //platypusGuiLibScope.setPrototype(platypusStandardLibScope);
                platypusGuiLibScope = platypusStandardLibScope;
                importScriptLibrary("/com/eas/client/forms/gui.js", "gui", context, platypusGuiLibScope);
            } finally {
                Context.exit();
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
            Context cx = Context.getCurrentContext();
            boolean wasContext = cx != null;
            if (!wasContext) {
                cx = ScriptUtils.enterContext();
            }
            try {
                onChange.call(cx, FormRunner.this, FormRunnerPrototype.getInstance().getConstructor(), new Object[]{ScriptUtils.javaToJS(new ScriptSourcedEvent(FormRunner.this), FormRunner.this)});
            } finally {
                if (!wasContext) {
                    Context.exit();
                }
            }
            Context.enter();
            try {
            } finally {
                Context.exit();
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
        } catch (IOException ex) {
            Logger.getLogger(ScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FormRunner(String aFormId, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost) throws Exception {
        super(aFormId, aClient, aScope, aPrincipalHost, aCompiledScriptDocumentsHost, aScriptResolverHost);
        setPrototype(FormRunnerPrototype.getInstance());
        formKey = aFormId;
    }
    // Platypus script interface
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + "* Shows the form as an ordinary window.\n"
            + "*/";

    /**
     * Script method showing the form as ordinary frame
     *
     * @throws Exception
     */
    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        displayAsFrame();
    }

    /**
     * Same as showModal
     *
     * @return
     * @throws Exception
     * @see #showModal()
     */
    public Object showDialog(Function onOkModalResult) throws Exception {
        return showModal(onOkModalResult);
    }
    private static final String SHOW_MODAL_JSDOC = ""
            + "/**\n"
            + "* Shows the form as a dialog (modal window).\n"
            + "*/";

    /**
     * Script method showing the form as modal dialog
     *
     * @return Value of modalResult property.
     * @throws Exception
     */
    @ScriptFunction(jsDoc = SHOW_MODAL_JSDOC)
    public Object showModal(Function onOkModalResult) throws Exception {
        return displayAsDialog(onOkModalResult);
    }
    private static final String SHOW_INTERNAL_FRAME_JSDOC = ""
            + "/**\n"
            + "* Shows the form as internal window in a desktop.\n"
            + "*/";

    /**
     * Script method showing the form as internal frame.
     *
     * @param desktopObject Should be instanceof JDesktopPane
     * @throws Exception
     */
    @ScriptFunction(jsDoc = SHOW_INTERNAL_FRAME_JSDOC)
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
            + "* Script method showing the form on panel.\n"
            + "* @param panel Parent panel.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHOW_ON_PANEL_FRAME_JSDOC)
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
    private static final String GET_FORM_KEY_JSDOC = ""
            + "/**\n"
            + "* Gets form keys.\n"
            + "* @return keys List of form keys.\n"
            + "*/";

    @ScriptFunction(jsDoc = GET_FORM_KEY_JSDOC)
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
            + "* Checks if form visible\n"
            + "*/";

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
//            if (formSize != null) {
//                surface.setSize(formSize);
//            } else if (designedViewSize != null) {
//                Insets decorInsets = surface.getInsets();
//                windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
//                surface.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
//            } else {
//                dialog.pack();
//            }
//            formSize = surface.getSize();
//            if (formLocation != null && !locationByPlatform) {
//                surface.setLocation(formLocation);
//            }
//            formLocation = surface.getLocation();
//
//            surface.addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentMoved(ComponentEvent e) {
//                    formLocation = surface.getLocation();
//                }
//
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    if (surface != null && surface.isVisible()) {
//                        formSize = surface.getSize();
//                    }
//                }
//            });
//
//            synchronized (FormRunner.class) {
//                showingForms.put(formKey, this);
//            }
//            showingFormsChanged();
            dialog.setVisible(true);
            final Object selected = closeCallbackParameter;
            surface = null;
            closeCallbackParameter = null;
            if (onOkModalResult != null && selected != Context.getUndefinedValue()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Context cx = ScriptUtils.enterContext();
                        try {
                            onOkModalResult.call(cx, FormRunner.this, FormRunner.this, new Object[]{selected});
                        } finally {
                            Context.exit();
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
            + "* Closes form.\n"
            + "* @param aSelected Value to be passed as a result of a selection into showModal's callback.\n"
            + "*/";

    @ScriptFunction(jsDoc = CLOSE_JSDOC)
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
            + "* Minimizes form\n"
            + "*/";

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
            + "* Maximizes form\n"
            + "*/";

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
            + "* Restores form\n"
            + "*/";

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
            + "* Moves form to the front\n"
            + "*/";

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
    private static final String IS_MINIMIZED_JSDOC = ""
            + "/**\n"
            + " * Returns if form is minimized\n"
            + " * return minimized True if minimized\n"
            + " */";

    @ScriptFunction(jsDoc = IS_MINIMIZED_JSDOC)
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
            + "* Checks if form is maximized\n"
            + "* return maximized True if maximized\n"
            + "*/";

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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
    public boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    @ScriptFunction
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

    @ScriptFunction
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

    @ScriptFunction
    public Function getOnWindowOpened() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened) : null;
    }

    @ScriptFunction
    public void setOnWindowOpened(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowClosing() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosing) : null;
    }

    @ScriptFunction
    public void setOnWindowClosing(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosing, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowClosed() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosed) : null;
    }

    @ScriptFunction
    public void setOnWindowClosed(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosed, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowMinimized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowIconified) : null;
    }

    @ScriptFunction
    public void setOnWindowMinimized(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowIconified, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowRestored() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowRestored) : null;
    }

    @ScriptFunction
    public void setOnWindowRestored(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowRestored, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowMaximized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowMaximized) : null;
    }

    @ScriptFunction
    public void setOnWindowMaximized(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowMaximized, aValue);
        }
    }

    @ScriptFunction
    public Function getOnWindowActivated() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowActivated) : null;
    }

    @ScriptFunction
    public void setOnWindowActivated(Function aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowActivated, aValue);
        }
    }

    @ScriptFunction
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
    protected void prepare(ScriptDocument scriptDoc) throws Exception {
        prepareRoles(scriptDoc);
        prepareModel(scriptDoc);
        Runnable handlersResolver = prepareForm(scriptDoc);
        prepareScript(scriptDoc);
        handlersResolver.run();
    }

    protected Runnable prepareForm(ScriptDocument scriptDoc) throws Exception {
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
        Context cx = ScriptUtils.enterContext();
        try {
            for (Entry<String, JComponent> entry : components.entrySet()) {
                if (form != entry.getValue()) {
                    defineProperty(entry.getKey(), publishComponent(entry.getValue(), this, factory.getControlDesignInfos().get(entry.getKey())), READONLY);
                }
            }
            super.delete(VIEW_SCRIPT_NAME);
            ControlsWrapper viewWrapper = new ControlsWrapper(form);
            (new PanelDesignInfo()).accept(viewWrapper);
            defineProperty(VIEW_SCRIPT_NAME, ScriptUtils.javaToJS(viewWrapper.getResult(), this), READONLY);
        } finally {
            Context.exit();
        }
        return new Runnable() {
            @Override
            public void run() {
                // Resolve handlers
                for (Runnable resolver : factory.getHandlersResolvers()) {
                    resolver.run();
                }
                windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, getHandler(fdi.getWindowOpened()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowClosing, getHandler(fdi.getWindowClosing()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowClosed, getHandler(fdi.getWindowClosed()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowIconified, getHandler(fdi.getWindowMinimized()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowMaximized, getHandler(fdi.getWindowMaximized()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowRestored, getHandler(fdi.getWindowRestored()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowActivated, getHandler(fdi.getWindowActivated()));
                windowHandler.getHandlers().put(WindowEventsIProxy.windowDeactivated, getHandler(fdi.getWindowDeactivated()));
            }
        };
    }

    @Override
    public String getClassName() {
        return FormRunner.class.getName();
    }

    @Override
    protected void definePropertiesAndMethods() {
        super.definePropertiesAndMethods();
        defineFunctionProperties(new String[]{
                    "getDialog",
                    "getInternalFrame",
                    "getFormId",
                    "getFrame",
                    "show",
                    "showInternalFrame",
                    "showModal",
                    "showDialog",
                    "showOnPanel",
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
        defineProperty("frameVisible", FormRunner.class, READONLY);
        defineProperty("dialogVisible", FormRunner.class, READONLY);
        defineProperty("internalFrameVisible", FormRunner.class, READONLY);
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
    public Function getHandler(String aHandlerName) {
        Object oHandler = get(aHandlerName, this);
        if (oHandler instanceof Function) {
            return (Function) oHandler;
        } else {
            return null;
        }
    }

    @Override
    public Object executeEvent(Function aHandler, Scriptable aEventThis, Object anEvent) {
        // The components map must be filled before any event can occur.
        if (components != null && aHandler != null) {
            try {
                Context cx = Context.getCurrentContext();
                boolean wasContext = cx != null;
                if (!wasContext) {
                    cx = ScriptUtils.enterContext();
                }
                try {
                    return ScriptUtils.js2Java(aHandler.call(cx, this, aEventThis, new Object[]{anEvent}));
                } finally {
                    if (!wasContext) {
                        Context.exit();
                    }
                }
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

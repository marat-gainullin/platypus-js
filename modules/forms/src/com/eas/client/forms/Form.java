package com.eas.client.forms;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.forms.components.DesktopPane;
import com.eas.client.forms.containers.AnchorsPane;
import com.eas.client.forms.events.rt.WindowEventsIProxy;
import com.eas.design.Undesignable;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
import com.eas.util.exceptions.ClosedManageException;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(name = "Form", jsDoc = ""
        + "/**\n"
        + " * Application form.\n"
        + " */")
public class Form implements HasPublished {

    public static final String FORM_ID_AS_FIRST_REQUIRED_MSG = "First element of form key must be a valid form id.";
    public static final String FORM_KEY_REQUIRED_MSG = "Form key must be not null and must contain at least one element (form id).";
    public static final String VIEW_SCRIPT_NAME = "view";
    protected static final Map<String, Form> showingForms = new HashMap<>();
    protected static JSObject onChange;

    public static Form[] getShownForms() {
        List<Form> notNullForms = new ArrayList<>();
        showingForms.values().forEach((Form f) -> {
            if (f != null) {
                notNullForms.add(f);
            }
        });
        return notNullForms.toArray(new Form[]{});
    }

    public static Form getShownForm(String aFormKey) {
        return showingForms.get(aFormKey);
    }

    public static JSObject getOnChange() {
        return onChange;
    }

    public static void setOnChange(JSObject aValue) {
        onChange = aValue;
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
                PublishedSourcedEvent event = new PublishedSourcedEvent(this);
                onChange.call(published, new Object[]{event.getPublished()});
            } catch (Exception ex) {
                Logger.getLogger(Form.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected class WindowClosingReflector extends WindowAdapter implements InternalFrameListener {

        @Override
        public void windowClosed(WindowEvent e) {
            surface = null;
            showingForms.remove(Form.this.getFormKey());
            showingFormsChanged();
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
            showingForms.remove(Form.this.getFormKey());
            showingFormsChanged();
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
    protected int defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE;
    protected ImageIcon icon;
    protected String title;
    protected boolean resizable = true;
    protected boolean minimizable = true;
    protected boolean maximizable = true;
    protected boolean closable = true;
    protected boolean undecorated;
    protected float opacity = 1.0f;
    protected boolean alwaysOnTop;
    protected boolean locationByPlatform = true;
    protected Point formLocation;
    protected Dimension designedViewSize;
    protected Dimension formSize;
    protected Dimension windowDecorSize = new Dimension();
    protected WindowEventsIProxy windowHandler;
    // runtime 
    protected JComponent view;
    protected String formKey;
    //
    protected JSObject published;
    // frequent runtime
    protected Collection<JSObject> publishedComponents = new ArrayList<>();
    protected Container surface;
    protected Object closeCallbackParameter;

    public Form() throws Exception {
        this("form-" + IdGenerator.genId());
    }

    public Form(String aFormKey) throws Exception {
        this(null, aFormKey);
    }

    public Form(JComponent aView) throws Exception {
        this(aView, "form-" + IdGenerator.genId());
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Creates a form.\n"
            + " * @param aView Container instance to be used as view of created form. Optional. If it is omitted P.AnchorsPane will be created and used as view.\n"
            + " * @param aFormKey Form instance key for open windows accounting. Optional.\n"
            + " */",
            params = {"aView", "aFormKey"})
    public Form(JComponent aView, String aFormKey) throws Exception {
        super();
        formKey = aFormKey;
        windowHandler = new WindowEventsIProxy();
        view = aView != null ? aView : new AnchorsPane();
        view.setName(VIEW_SCRIPT_NAME);
    }

    @Override
    public final JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        injectPublished(aValue);
    }

    public void injectPublished(JSObject aValue) {
        published = aValue;
        windowHandler.setEventThis(published);
    }

    // Script interface
    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows the form as an ordinary window.\n"
            + " */";

    /**
     * Script method showing the view as ordinary frame
     *
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        displayAsFrame();
    }

    public Object showDialog(JSObject onOkModalResult) throws Exception {
        return showModal(onOkModalResult);
    }

    private static final String SHOW_MODAL_JSDOC = ""
            + "/**\n"
            + " * Shows the form as a dialog (modal window).\n"
            + " * @param callback a callback handler function\n"
            + " */";

    /**
     * Script method showing the view as modal dialog
     *
     * @param onOkModalResult a callback handler function
     * @return value of the modalResult property
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(jsDoc = SHOW_MODAL_JSDOC, params = {"callback"})
    public Object showModal(JSObject onOkModalResult) throws Exception {
        return displayAsDialog(onOkModalResult);
    }

    private static final String SHOW_INTERNAL_FRAME_JSDOC = ""
            + "/**\n"
            + " * Shows the form as an internal window in a desktop.\n"
            + " * @param desktop the parent desktop object\n"
            + " */";

    /**
     * Script method showing the view as an internal frame.
     *
     * @param aDesktop Should be an instance of JDesktopPane
     * @throws Exception an exception if something goes wrong
     */
    @ScriptFunction(params = {"desktop"}, jsDoc = SHOW_INTERNAL_FRAME_JSDOC)
    public void showInternalFrame(DesktopPane aDesktop) throws Exception {
        displayAsInternalFrame(aDesktop);
    }

    /*
     private static final String SHOW_ON_PANEL_FRAME_JSDOC = ""
     + "/**\n"
     + " * Script method showing the view in embedded mode on the panel.\n"
     + " * @param panel the parent panel\n"
     *///        + " */";
/*
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
     */
    private static final String FORM_KEY_JSDOC = ""
            + "/**\n"
            + " * The form key. Used to identify a form instance. Initialy set to the form's application element name.\n"
            + " */";

    @ScriptFunction(jsDoc = FORM_KEY_JSDOC)
    @Undesignable
    public String getFormKey() {
        return formKey;
    }

    @ScriptFunction
    public void setFormKey(String aValue) {
        if (formKey == null ? aValue != null : !formKey.equals(aValue)) {
            showingForms.remove(formKey);
            formKey = aValue;
            if (isInOpenedWindow()) {
                showingForms.put(formKey, this);
                showingFormsChanged();
            }
        }
    }

    public JComponent getViewWidget() {
        return view;
    }

    public JSObject getView() {
        return ((HasPublished) view).getPublished();
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

    public void displayAsFrame() throws Exception {
        if (surface == null) {
            close(null);
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
            frame.getContentPane().add(view, BorderLayout.CENTER);
            // add window listener
            // for unknown reasons, control events are also working
            windowHandler.setHandlee(frame);
            //
            JSObject windowOpenedHandler = windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened);
            windowHandler.getHandlers().remove(WindowEventsIProxy.windowOpened);
            try {
                showingForms.put(formKey, this);
                frame.setVisible(true);
                Insets decorInsets = frame.getInsets();
                windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                if (formSize != null) {
                    frame.setSize(formSize.width + windowDecorSize.width, formSize.height + windowDecorSize.height);
                } else if (designedViewSize != null) {
                    frame.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                } else {
                    frame.pack();
                }
                formSize = new Dimension(frame.getSize().width - windowDecorSize.width, frame.getSize().height - windowDecorSize.height);
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
                    formSize = new Dimension(surface.getSize().width - windowDecorSize.width, surface.getSize().height - windowDecorSize.height);
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
            internalFrame.getContentPane().add(view, BorderLayout.CENTER);

            // add window listener
            // for unknown reasons, control events are also working
            windowHandler.setHandlee(internalFrame);
            //
            JSObject windowOpenedHandler = windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened);
            windowHandler.getHandlers().remove(WindowEventsIProxy.windowOpened);
            try {
                showingForms.put(formKey, this);
                aDesktop.add(internalFrame);
                internalFrame.setVisible(true);
                Insets decorInsets = internalFrame.getInsets();
                windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                if (formSize != null) {
                    internalFrame.setSize(formSize.width + windowDecorSize.width, formSize.height + windowDecorSize.height);
                } else if (designedViewSize != null) {
                    internalFrame.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                } else {
                    internalFrame.pack();
                }
                formSize = new Dimension(internalFrame.getSize().width - windowDecorSize.width, internalFrame.getSize().height - windowDecorSize.height);
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
                    formSize = new Dimension(surface.getSize().width - windowDecorSize.width, surface.getSize().height - windowDecorSize.height);
                }
            });
            windowHandler.windowOpened(null);
            showingFormsChanged();
        } else {
            surface.setVisible(true);
        }
    }

    public Object displayAsDialog(final JSObject onOkModalResult) throws Exception {
        if (surface == null) {
            close(null);
            JDialog dialog = new JDialog() {

                @Override
                protected void processWindowEvent(WindowEvent e) {
                    try {
                        if (e.getID() == WindowEvent.WINDOW_OPENED) {
                            Insets decorInsets = surface.getInsets();
                            windowDecorSize = new Dimension(decorInsets.left + decorInsets.right, decorInsets.top + decorInsets.bottom);
                            if (formSize != null) {
                                surface.setSize(formSize.width + windowDecorSize.width, formSize.height + windowDecorSize.height);
                            } else if (designedViewSize != null) {
                                surface.setSize(designedViewSize.width + windowDecorSize.width, designedViewSize.height + windowDecorSize.height);
                            } else {
                                ((JDialog) surface).pack();
                            }
                            formSize = new Dimension(surface.getSize().width - windowDecorSize.width, surface.getSize().height - windowDecorSize.height);
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
                                        formSize = new Dimension(surface.getSize().width - windowDecorSize.width, surface.getSize().height - windowDecorSize.height);
                                    }
                                }
                            });

                            showingForms.put(formKey, Form.this);
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
            dialog.getContentPane().add(view, BorderLayout.CENTER);
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
            if (onOkModalResult != null) {
                try {
                    onOkModalResult.call(published, new Object[]{selected});

                } catch (Exception ex) {
                    Logger.getLogger(Form.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
            return selected;
        } else {
            return null;
        }
    }
    /*
     public void displayOnContainer(java.awt.Container aTarget) throws Exception {
     if (surface == null) {
     close(null);
     if (aTarget.getLayout() != null) {// TODO: remove this code when script moving/resizing will be clear.
     aTarget.setLayout(new BorderLayout());
     }
     if (aTarget.getLayout() instanceof BorderLayout) {
     aTarget.add(view, BorderLayout.CENTER);
     }
     surface = aTarget;
     windowDecorSize = new Dimension();
     windowHandler.windowOpened(new WindowEvent(new JFrame(), WindowEvent.WINDOW_OPENED));
     aTarget.invalidate();
     view.invalidate();
     aTarget.validate();
     aTarget.repaint();
     if (surface.getLayout() == null) {
     if (formSize != null) {
     view.setSize(formSize);
     } else if (designedViewSize != null) {
     view.setSize(designedViewSize);
     }
     formSize = view.getSize();
     if (formLocation != null) {
     view.setLocation(formLocation);
     }
     }
     designedViewSize = view.getSize();
     formLocation = view.getLocation();
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
     */

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
                    view.invalidate();
                    surface.remove(view);
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
                ((JFrame) surface).setExtendedState(JFrame.ICONIFIED);
            } else if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                try {
                    if (aFrame.isMaximum()) {
                        aFrame.setMaximum(false);
                    }
                    if (!aFrame.isIcon() && aFrame.isIconifiable()) {
                        aFrame.setIcon(true);
                    }
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
                ((JFrame) surface).setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else if (surface instanceof JInternalFrame) {
                JInternalFrame aFrame = (JInternalFrame) surface;
                try {
                    if (aFrame.isIcon()) {
                        aFrame.setIcon(false);
                    }
                    if (!aFrame.isMaximum() && aFrame.isMaximizable()) {
                        aFrame.setMaximum(true);
                        aFrame.toFront();
                    }
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
                    if (aFrame.isMaximum()) {
                        aFrame.setMaximum(false);
                    }
                    if (aFrame.isIcon()) {
                        aFrame.setIcon(false);
                    }
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
    @Undesignable
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
    @Undesignable
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
    @Undesignable
    public int getWidth() {
        if (surface != null) {
            return surface.getWidth() - windowDecorSize.width;
        } else if (formSize != null) {
            return formSize.width;
        } else {
            return designedViewSize.width;
        }
    }

    @ScriptFunction
    public void setWidth(int aValue) {
        if (surface != null) {
            surface.setSize(aValue + windowDecorSize.width, surface.getHeight());
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
    @Undesignable
    public int getHeight() {
        if (surface != null) {
            return surface.getHeight() - windowDecorSize.height;
        } else if (formSize != null) {
            return formSize.height;
        } else {
            return designedViewSize.height;
        }
    }

    @ScriptFunction
    public void setHeight(int aValue) {
        if (surface != null) {
            surface.setSize(surface.getWidth(), aValue + windowDecorSize.height);
        } else {
            if (formSize == null) {
                formSize = new Dimension();
            }
            formSize.height = aValue;
        }
    }

    public Dimension getDesignedViewSize() {
        return designedViewSize;
    }

    public void setDesignedViewSize(Dimension aValue) {
        designedViewSize = aValue;
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
    public ImageIcon getIcon() {
        return icon;
    }

    @ScriptFunction
    public void setIcon(ImageIcon aValue) {
        icon = aValue;
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
            + " * <code>true</code> if this form is resizable.\n"
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
            + " * <code>true</code> if this form is maximizable.\n"
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

    private static final String CLOSABLE_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form is closable.\n"
            + " */";

    @ScriptFunction(jsDoc = MAXIMIZABLE_JSDOC)
    public boolean getClosable() {
        return closable;
    }

    @ScriptFunction
    public void setClosable(boolean aValue) {
        closable = aValue;
        if (surface instanceof JDialog) {
            //((JDialog) surface).setClosable(closable);
        }
        if (surface instanceof JInternalFrame) {
            ((JInternalFrame) surface).setClosable(closable);
        }
        if (surface instanceof JFrame) {
            //((JFrame) surface).setClosable(closable);
        }
    }

    private static final String MINIMIZABLE_JSDOC = ""
            + "/**\n"
            + " * <code>true</code> if this form is minimizable.\n"
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
    @Undesignable
    public boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    @ScriptFunction()
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
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowOpened() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowOpened) : null;
    }

    @ScriptFunction
    public void setOnWindowOpened(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowOpened, aValue);
        }
    }

    private static final String ON_WINDOW_CLOSING_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>before close</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_CLOSING_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowClosing() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosing) : null;
    }

    @ScriptFunction()
    public void setOnWindowClosing(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosing, aValue);
        }
    }

    private static final String ON_WINDOW_CLOSED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after close</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_CLOSED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowClosed() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowClosed) : null;
    }

    @ScriptFunction
    public void setOnWindowClosed(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowClosed, aValue);
        }
    }

    private static final String ON_WINDOW_MINIMIZED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after minimize</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_MINIMIZED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowMinimized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowIconified) : null;
    }

    @ScriptFunction
    public void setOnWindowMinimized(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowIconified, aValue);
        }
    }

    private static final String ON_WINDOW_RESTORED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after restore</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_RESTORED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowRestored() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowRestored) : null;
    }

    @ScriptFunction
    public void setOnWindowRestored(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowRestored, aValue);
        }
    }

    private static final String ON_WINDOW_MAXIMIZED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after maximize</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_MAXIMIZED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowMaximized() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowMaximized) : null;
    }

    @ScriptFunction
    public void setOnWindowMaximized(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowMaximized, aValue);
        }
    }

    private static final String ON_WINDOW_ACTIVATED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after activate</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_ACTIVATED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowActivated() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowActivated) : null;
    }

    @ScriptFunction
    public void setOnWindowActivated(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowActivated, aValue);
        }
    }

    private static final String ON_WINDOW_DEACTIVATED_JSDOC = ""
            + "/**\n"
            + " * The handler function for the form's <i>after deactivate</i> event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_WINDOW_DEACTIVATED_JSDOC)
    @EventMethod(eventClass = com.eas.client.forms.events.WindowEvent.class)
    @Undesignable
    public JSObject getOnWindowDeactivated() {
        return windowHandler != null ? windowHandler.getHandlers().get(WindowEventsIProxy.windowDeactivated) : null;
    }

    @ScriptFunction
    public void setOnWindowDeactivated(JSObject aValue) {
        if (windowHandler != null) {
            windowHandler.getHandlers().put(WindowEventsIProxy.windowDeactivated, aValue);
        }
    }

    public JSObject[] getPublishedComponents() {
        Collection<JSObject> copy = new ArrayList<>();
        copy.addAll(publishedComponents);
        publishedComponents.clear();
        return copy.toArray(new JSObject[]{});
    }
}

package de.wannawork.jcalendar;

/*
 * Copyright (c) 2003, Bodo Tasche (http://www.wannawork.de) All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.PlainDocument;

/**
 * This Class creates a ComboBox for selecting the Date. If pressed, it shows a
 * Popup that contains a JCalendarPanel.
 *
 * You can add a ChangeListener to this ComboBox to receive change events.
 *
 * It is possible to change the Text on the ComboBox using the
 * DateFormat-Parameter.
 *
 * @author Bodo Tasche, David Freels
 */
public class JCalendarComboBox extends JPanel implements AncestorListener, ChangeListener, SwingConstants {

    public void setDateFormat(SimpleDateFormat aFormat) {
        _calendarPanel = null;
        panelDateFormat = aFormat;
        createGUI();
    }

    protected class CalendarPanelComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            if (e.getComponent() == _calendarPanel) {
                _changed = false;
                hideCalendar();
            }
        }
    }
    private static JCalendarComboBox comboWithPopupShown = null;
    private static AWTEventListener awtMonitor = new AWTEventListener() {
        @Override
        public void eventDispatched(AWTEvent event) {
            if (event instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) event;
                Component src = me.getComponent();
                switch (me.getID()) {
                    case MouseEvent.MOUSE_PRESSED:
                        if (!isInPopup(src, me.getPoint())) {
                            cancelPopupCalendar();
                        }
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        if (!isInPopup(src, me.getPoint())) {
                            cancelPopupCalendar();
                        }
                        break;
                    case MouseEvent.MOUSE_DRAGGED:
                        if (!isInPopup(src, me.getPoint())) {
                            cancelPopupCalendar();
                        }
                        break;
                    case MouseEvent.MOUSE_WHEEL:
                        if (!isInPopup(src, me.getPoint())) {
                            cancelPopupCalendar();
                        }
                        break;
                }
            }
        }

        boolean isInPopup(Component src, Point aPt) {
            for (Component c = src; c != null; c = c instanceof JPopupMenu ? ((JPopupMenu) c).getInvoker() : c.getParent()) {
                if (c instanceof Applet || (c instanceof Window && !(c instanceof JCalendarPopupWindow))) {
                    break;
                } else if (c instanceof JCalendarPopupWindow || c instanceof JCalendarInvokerButton) {
                    return true;
                }
            }
            if (comboWithPopupShown != null && comboWithPopupShown.isCalendarShown()) {
                Point converted = SwingUtilities.convertPoint(src, aPt, comboWithPopupShown.getCalendarPanel());
                Rectangle bounds = comboWithPopupShown.getCalendarPanel().getBounds();
                bounds.setLocation(0, 0);
                if (converted != null && bounds.contains(converted)) {
                    return true;
                }
                JCalendarInvokerButton lbtn = comboWithPopupShown.getDropDownButton();
                bounds = lbtn.getBounds();
                bounds.setLocation(0, 0);
                converted = SwingUtilities.convertPoint(src, aPt, lbtn);
                if (converted != null && bounds.contains(converted)) {
                    return true;
                }
            }
            return false;
        }
    };

    static {
        Toolkit.getDefaultToolkit().addAWTEventListener(awtMonitor,
                AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.MOUSE_WHEEL_EVENT_MASK
                | AWTEvent.WINDOW_EVENT_MASK);
    }

    public static void cancelPopupCalendar() {
        if (comboWithPopupShown != null && comboWithPopupShown.isCalendarShown()) {
            comboWithPopupShown.hideCalendar();
            assert comboWithPopupShown == null;
        }
    }

    private static class WindowWatchDog implements WindowListener, ComponentListener {

        @Override
        public void windowClosing(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowIconified(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowOpened(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void componentResized(ComponentEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            if (targetLocation == null || !targetLocation.equals(e.getComponent().getLocation())) {
                cancelPopupCalendar();
            }
        }

        @Override
        public void componentShown(ComponentEvent e) {
            cancelPopupCalendar();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            cancelPopupCalendar();
        }
        private Point targetLocation;

        private void setTargetSize(Point aPt) {
            targetLocation = aPt;
        }
    }
    private WindowWatchDog calendarWatchDog = new WindowWatchDog();

    /**
     * Creates a Calendar using the current Date and current Local settings.
     */
    public JCalendarComboBox() {
        panelCal = (Calendar) Calendar.getInstance().clone();
        panelLocale = Locale.getDefault();
        panelDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel();
        createGUI();
    }

    /**
     * Creates a Calendar using the current Date and current Local settings.
     *
     * @param calendarPanelDialogStyle
     */
    public JCalendarComboBox(boolean calendarPanelDialogStyle) {
        panelCal = (Calendar) Calendar.getInstance().clone();
        panelLocale = Locale.getDefault();
        panelDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        panelFlat = true;
        panelCalendarPanelDialogStyle = calendarPanelDialogStyle;
        //_calendarPanel = new JCalendarPanel();
        createGUI();
    }

    /**
     * Creates a Calendar using the cal-Date and current Locale Settings. It
     * doesn't use the Locale in the Calendar-Object !
     *
     * @param cal Calendar to use
     */
    public JCalendarComboBox(Calendar cal) {
        panelCal = cal;
        panelLocale = Locale.getDefault();
        panelDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel(cal);
        createGUI();
    }

    /**
     * Creates a Calendar using the current Date and the given Locale Settings.
     *
     * @param locale Locale to use
     */
    public JCalendarComboBox(Locale locale) {
        panelCal = (Calendar) Calendar.getInstance().clone();
        panelLocale = locale;
        panelDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel(locale);
        createGUI();
    }

    /**
     * Creates a Calender using the given Date and Locale
     *
     * @param cal Calendar to use
     * @param locale Locale to use
     */
    public JCalendarComboBox(Calendar cal, Locale locale) {
        panelCal = cal;
        panelLocale = locale;
        panelDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        _calendarPanel = new JCalendarPanel(cal, locale);
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     *
     * @param cal Calendar to use
     * @param locale Locale to use
     * @param dateFormat DateFormat for the ComboBox
     */
    public JCalendarComboBox(Calendar cal, Locale locale, DateFormat dateFormat) {
        panelCal = cal;
        panelLocale = locale;
        panelDateFormat = dateFormat;
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel(cal, locale, dateFormat);
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     *
     * @param cal Calendar to use
     * @param locale Locale to use
     * @param dateFormat DateFormat for the ComboBox
     * @param location Location of the Popup (LEFT, CENTER or RIGHT)
     */
    public JCalendarComboBox(Calendar cal, Locale locale,
            DateFormat dateFormat, int location) {
        panelCal = cal;
        panelLocale = locale;
        panelDateFormat = dateFormat;
        panelFlat = true;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel(cal, locale, dateFormat);
        _popupLocation = location;
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     *
     * @param cal Calendar to use
     * @param locale Locale to use
     * @param dateFormat DateFormat for the ComboBox
     * @param location Location of the Popup (LEFT, CENTER or RIGHT)
     * @param flat Flat Buttons for next/last Month/Year
     */
    public JCalendarComboBox(Calendar cal, Locale locale,
            DateFormat dateFormat, int location, boolean flat) {
        panelCal = cal;
        panelLocale = locale;
        panelDateFormat = dateFormat;
        panelFlat = flat;
        panelCalendarPanelDialogStyle = false;

        //_calendarPanel = new JCalendarPanel(cal, locale, dateFormat, flat);
        _popupLocation = location;
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     *
     * @param cal Calendar to use
     * @param locale Locale to use
     * @param dateFormat DateFormat for the ComboBox
     * @param location Location of the Popup (LEFT, CENTER or RIGHT)
     * @param flat Flat Buttons for next/last Month/Year
     * @param calendarPanelDialogStyle
     */
    public JCalendarComboBox(Calendar cal, Locale locale,
            DateFormat dateFormat, int location, boolean flat, boolean calendarPanelDialogStyle) {
        panelCal = cal;
        panelLocale = locale;
        panelDateFormat = dateFormat;
        panelFlat = flat;
        panelCalendarPanelDialogStyle = calendarPanelDialogStyle;

        _popupLocation = location;
        createGUI();
    }

    private Calendar panelCal;
    private Locale panelLocale;
    private DateFormat panelDateFormat;
    private boolean panelFlat;
    private boolean panelCalendarPanelDialogStyle;

    private void checkDialogGUI() {
        if (_calendarPanel == null) {
            createDialogGUI();
        }
    }

    /**
     * Creates the GUI for drop down part
     */
    private void createDialogGUI() {
        _calendarPanel = new JCalendarPanel(panelCal, panelLocale, panelDateFormat, panelFlat, panelCalendarPanelDialogStyle);

        _calendarPanel.setListenerModus(JCalendarPanel.FIRE_DAYCHANGES);
        _calendarPanel.setCalendar(_selected);
        _calendarPanel.addChangeListener(this);
        _calendarPanel.addComponentListener(new CalendarPanelComponentListener());
        _calendarPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    /**
     * Creates the GUI for the JCalendarComboBox
     */
    private void createGUI() {
        _selected = (Calendar) panelCal.clone();
        setLayout(new BorderLayout());
        _spinner = new JSpinner();
        _spinner.setModel(new NullableSpinnerDateModel());
        _spinner.setFont(getFont());
        SimpleDateFormat lFormat = (SimpleDateFormat) panelDateFormat;
        final JSpinner.DateEditor dEditor = new JSpinner.DateEditor(_spinner, lFormat.toPattern());
        DateFormatter df = new OptimisticDateFormatter(lFormat);
        df.setAllowsInvalid(false);
        df.setOverwriteMode(true);
        dEditor.getTextField().setFormatterFactory(new DefaultFormatterFactory(df));
        dEditor.getTextField().setEditable(editable);
        dEditor.getTextField().setDocument(new PlainDocument() {
            @Override
            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);
                dEditor.getTextField().getCaret().setDot(offs);
            }
        });

        _spinner.setEditor(dEditor);
        _spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Date date = (Date) _spinner.getModel().getValue();
                if (date != null) {
                    if (_selected == null) {
                        _selected = (Calendar) Calendar.getInstance().clone();
                    }
                    _selected.setTime(date);
                } else {
                    _selected = null;
                }
                if (_calendarPanel != null) {
                    _calendarPanel.setCalendar(_selected);
                }
                fireChangeEvent();
            }
        });

        ((JSpinner.DefaultEditor) _spinner.getEditor()).getTextField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Date date = (Date) _spinner.getModel().getValue();
                if (date != null) {
                    if (_selected == null) {
                        _selected = (Calendar) Calendar.getInstance().clone();
                    }
                    _selected.setTime(date);
                } else {
                    _selected = null;
                }
                if (_calendarPanel != null) {
                    _calendarPanel.setCalendar(_selected);
                }
                fireChangeEvent();
            }
        });

        add(_spinner, BorderLayout.CENTER);
        JToolBar btns = new JToolBar();
        btns.setRollover(true);
        btns.setFloatable(false);
        btns.setBorderPainted(false);
        btns.setBorder(null);
        URL url = JCalendarComboBox.class.getClassLoader().getResource("de/wannawork/jcalendar/calendar.png");
        ImageIcon icon = new ImageIcon(url);
        _dropDownButton = new JCalendarInvokerButton();
        _dropDownButton.setIcon(icon);
        _dropDownButton.setBorderPainted(false);
        _dropDownButton.setBorder(new EmptyBorder(0, 1, 0, 1));
        _dropDownButton.setEnabled(editable && isEnabled());
        btns.add(_dropDownButton);
        add(btns, BorderLayout.EAST);
        _dropDownButton.setFocusable(false);
        _dropDownButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (_dropDownButton.isEnabled()) {
                    if (!isCalendarShown()) {
                        showCalendar();
                    } else {
                        hideCalendar();
                    }
                } else {
                    cancelPopupCalendar();
                }
            }

        });
    }

    public void setBorderless(boolean aBorderless) {
        isBorderless = aBorderless;
        if (isBorderless) {
            _spinner.setBorder(null);
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aEditable) {
        editable = aEditable;
        if (_spinner != null && _spinner.getEditor() instanceof JSpinner.DateEditor) {
            JSpinner.DateEditor dEditor = (JSpinner.DateEditor) _spinner.getEditor();
            if (dEditor != null && dEditor.getTextField() != null) {
                dEditor.getTextField().setEditable(editable);
            }
        }
        if (_spinner != null) {
            _spinner.setEnabled(editable && isEnabled());
        }
        if (_dropDownButton != null) {
            _dropDownButton.setEnabled(editable && isEnabled());
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (isBorderless) {
            _spinner.setBorder(null);
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (_spinner != null) {
            _spinner.setFont(font);
        }
    }

    @Override
    public void setToolTipText(String aText) {
        super.setToolTipText(aText);
        if (_spinner != null) {
            _spinner.setToolTipText(aText);
        }
        if (_dropDownButton != null) {
            _dropDownButton.setToolTipText(aText);
        }
    }

    @Override
    public void setCursor(Cursor aCursor) {
        super.setCursor(aCursor);
        if (_spinner != null) {
            _spinner.setCursor(aCursor);
            if (_spinner.getEditor() != null) {
                JComponent comp = _spinner.getEditor();
                comp.setCursor(getCursor());
                for (int i = 0; i < comp.getComponentCount(); i++) {
                    if (comp.getComponent(i) != null) {
                        comp.getComponent(i).setCursor(getCursor());
                    }
                }
            }
        }
        if (_dropDownButton != null) {
            _dropDownButton.setCursor(aCursor);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (_spinner != null) {
            _spinner.setBackground(bg);
            if (_spinner.getEditor() != null && _spinner.getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor de = (JSpinner.DateEditor) _spinner.getEditor();
                if (de.getTextField() != null) {
                    de.getTextField().setBackground(bg);
                }
            }
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (_spinner != null) {
            _spinner.setForeground(fg);
            if (_spinner.getEditor() != null && _spinner.getEditor() instanceof JSpinner.DateEditor) {
                JSpinner.DateEditor de = (JSpinner.DateEditor) _spinner.getEditor();
                if (de.getTextField() != null) {
                    de.getTextField().setForeground(fg);
                }
            }
        }
    }

    /**
     * Creates the CalendarWindow-Popup
     */
    private void createCalendarWindow() {
        createCalendarWindow(null);
    }

    private void createCalendarWindow(Window aAncestor) {
        if (aAncestor != null) {
            _calendarWindow = new JCalendarPopupWindow(aAncestor);
        } else {
            _calendarWindow = new JCalendarPopupWindow();
        }

        JPanel contentPanel = (JPanel) _calendarWindow.getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(_calendarPanel, BorderLayout.CENTER);

        _calendarWindow.pack();
    }

    /**
     * Returns the current seleted Date as Calendar
     *
     * @return current selected Date
     */
    public Calendar getCalendar() {
        return _calendarPanel.getCalendar();
    }

    /**
     * Sets the current selected Date
     *
     * @param cal Date to select
     */
    public void setCalendar(Calendar cal) {
        _calendarPanel.setCalendar(cal);
        _spinner.getModel().setValue(cal != null ? cal.getTime() : null);
    }

    /**
     * Returns the JCalendarPanel that is shown in the PopUp
     *
     * @return JCalendarPanel in the PopUp
     */
    public JCalendarPanel getCalendarPanel() {
        return _calendarPanel;
    }

    /**
     * Sets the Popup Location (Left/Right/Center)
     *
     * @param location
     */
    public void setPopUpLocation(int location) {
        _popupLocation = location;
    }

    /**
     * Returns the Popup Location
     *
     * @return Location of the Popup
     */
    public int getPopUpLocation() {
        return _popupLocation;
    }

    /**
     * Sets the horizontal Position of the Text in the Button
     *
     * @param value RIGHT, LEFT, CENTER, LEADING, TRAILING for horizontal
     * Alignment
     */
    public void setHorizontalAlignment(int value) {
        ((JSpinner.DefaultEditor) _spinner.getEditor()).getTextField().setHorizontalAlignment(value);
    }

    /**
     * Hides the Calendar PopUp and fires a ChangeEvent if a change was made
     */
    public void hideCalendar() {
        if (_calendarPanel != null) {
            if (_calendarWindow.isVisible()) {
                _calendarWindow.setVisible(false);
                _calendarWindow.dispose();

                if (!_calendarPanel.isDialogStyle() && !_calendarPanel.getCalendar().getTime().equals(_spinner.getModel().getValue())) {
                    _changed = true;
                }

                if (_changed) {
                    if (_calendarPanel.getCalendar() != null) {
                        _spinner.getModel().setValue(_calendarPanel.getCalendar().getTime());
                        _selected = (Calendar) _calendarPanel.getCalendar().clone();
                    } else {
                        _spinner.getModel().setValue(null);
                        _selected = null;
                    }
                    _changed = false;
                    fireChangeEvent();
                }
                if (comboWithPopupShown == this) {
                    comboWithPopupShown = null;
                }
            }
        }
    }

    public boolean isCalendarShown() {
        return _calendarWindow != null && _calendarWindow.isVisible();
    }

    /**
     * Shows the Calendar Popup
     */
    public void showCalendar() {
        checkDialogGUI();

        Window ancestor = (Window) this.getTopLevelAncestor();

        if ((_calendarWindow == null) || (ancestor != _calendarWindow.getOwner())) {
            if (ancestor instanceof JDialog) {
                createCalendarWindow(ancestor);
            } else {
                createCalendarWindow();
            }
        }

        //Update the date from the spinner model
        Date date = (Date) _spinner.getModel().getValue();
        if (_selected != null) {
            _selected.setTime(date != null ? date : ((Calendar) Calendar.getInstance().clone()).getTime());
        }
        _calendarPanel.setCalendar(_selected);

        Point location = getLocationOnScreen();

        int x;

        if (_popupLocation == RIGHT) {
            x = (int) location.getX() + getSize().width - _calendarWindow.getSize().width;
        } else if (_popupLocation == CENTER) {
            x = (int) location.getX() + ((getSize().width - _calendarWindow.getSize().width) / 2);
        } else {
            x = (int) location.getX();
        }

        int y = (int) location.getY() + getHeight() - 1;

        Rectangle screenSize = getDesktopBounds();

        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }

        if (x + _calendarWindow.getWidth() > screenSize.width) {
            x = screenSize.width - _calendarWindow.getWidth();
        }

        if (y + 30 + _calendarWindow.getHeight() > screenSize.height) {
            y = (int) location.getY() - _calendarWindow.getHeight();
        }

        _calendarWindow.setBounds(x, y, _calendarWindow.getWidth(),
                _calendarWindow.getHeight());
        _calendarWindow.setVisible(true);

        if (comboWithPopupShown != null && comboWithPopupShown != this && comboWithPopupShown.isCalendarShown()) {
            comboWithPopupShown.hideCalendar();
            assert comboWithPopupShown == null;
        }
        comboWithPopupShown = this;

        Window w = SwingUtilities.getWindowAncestor(this);
        w.removeWindowListener(calendarWatchDog);
        w.removeComponentListener(calendarWatchDog);
        w.addWindowListener(calendarWatchDog);
        calendarWatchDog.setTargetSize(new Point(w.getLocation()));
        w.addComponentListener(calendarWatchDog);
    }

    public static void setFocusableRecursive(Component aComp, boolean aValue) {
        aComp.setFocusable(aValue);
        if (aComp instanceof JComponent) {
            JComponent jComp = (JComponent) aComp;
            for (int i = 0; i < jComp.getComponentCount(); i++) {
                setFocusableRecursive(jComp.getComponent(i), aValue);
            }
        }
    }

    /**
     * Gets the screensize. Takes into account multi-screen displays.
     *
     * @return a union of the bounds of the various screen devices present
     */
    private Rectangle getDesktopBounds() {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        final Rectangle[] screenDeviceBounds = new Rectangle[gd.length];
        Rectangle desktopBounds = new Rectangle();
        for (int i = 0; i < gd.length; i++) {
            final GraphicsConfiguration gc = gd[i].getDefaultConfiguration();
            screenDeviceBounds[i] = gc.getBounds();
            desktopBounds = desktopBounds.union(screenDeviceBounds[i]);
        }

        return desktopBounds;
    }

    public JCalendarInvokerButton getDropDownButton() {
        return _dropDownButton;
    }


    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    @Override
    public void ancestorAdded(AncestorEvent event) {
        hideCalendar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
     */
    @Override
    public void ancestorMoved(AncestorEvent event) {
        //hideCalendar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    @Override
    public void ancestorRemoved(AncestorEvent event) {
        hideCalendar();
    }

    /**
     * Listens to ChangeEvents of the JCalendarPanel and rembers if something
     * was changed.
     *
     * If the Day was changed, the PopUp is closed.
     *
     * @param e ChangeEvent
     * @see
     * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        _changed = true;
        hideCalendar();
    }

    /**
     * Adds a Changelistener to this JCalendarComboBox.
     *
     * It will be called everytime the ComboBox is closed and the Date was
     * changed
     *
     * @param listener ChangeListener
     */
    public void addChangeListener(ChangeListener listener) {
        _changeListener.add(listener);
    }

    /**
     * Removes a ChangeListener from this JCalendarComboBox
     *
     * @param listener listener to remove
     */
    public void removeChangeListener(ChangeListener listener) {
        _changeListener.remove(listener);
    }

    /**
     * Gets all ChangeListeners
     *
     * @return all ChangeListeners
     */
    public ChangeListener[] getChangeListener() {
        return (ChangeListener[]) _changeListener.toArray();
    }

    /**
     * Fires the ChangeEvent
     */
    protected void fireChangeEvent() {
        if (!_fireingChangeEvent) {
            _fireingChangeEvent = true;
            ChangeEvent event = new ChangeEvent(this);

            for (int i = 0; i < _changeListener.size(); i++) {
                ((ChangeListener) _changeListener.get(i)).stateChanged(event);
            }

            _fireingChangeEvent = false;
        }

    }

    /**
     * Enables/Disables the ComboBox
     *
     * @param enabled Enabled ?
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        _spinner.setEnabled(enabled);
        _dropDownButton.setEnabled(editable && enabled);
        for (int i = 0; i < _spinner.getComponentCount(); i++) {
            _spinner.getComponent(i).setEnabled(enabled);
        }
    }

    /**
     * Gets the Popup Location
     *
     * @return location of the Popup
     */
    public int getPopupLocation() {
        return _popupLocation;
    }

    /**
     * Sets the Location of the Popup (LEFT, CENTER or RIGHT)
     *
     * @param location Location of the PopUp
     */
    public void setPopupLocation(int location) {
        _popupLocation = location;
    }

    /**
     * Returns the model used to hold the Date object
     *
     * @return
     */
    public SpinnerDateModel getModel() {
        return (SpinnerDateModel) _spinner.getModel();
    }

    public JFormattedTextField getEditorComponent() {
        if (_spinner.getEditor() instanceof JSpinner.DateEditor) {
            return ((JSpinner.DateEditor) _spinner.getEditor()).getTextField();
        }
        return null;
    }

    /**
     * Sets the model used to hold the Date object
     *
     * @param model A SpinnerDateModel to be used by the spinner control.
     */
    public void setSpinnerDateModel(SpinnerDateModel model) {
        _spinner.setModel(model);
    }

    @Override
    public void setOpaque(boolean aValue) {
        super.setOpaque(aValue);
        if (_spinner != null) {
            _spinner.setOpaque(aValue);
            JComponent editor = _spinner.getEditor();
            if (editor != null) {
                editor.setOpaque(aValue);
                if (editor instanceof DateEditor) {
                    if (((DateEditor) editor).getTextField() != null) {
                        ((DateEditor) editor).getTextField().setOpaque(aValue);
                    }
                }
            }
        }
    }
    /**
     * Where should be the Popup?
     */
    private int _popupLocation = LEFT;
    protected Date _date2Hide;
    /**
     * Current selected Day
     */
    private Calendar _selected;
    private boolean isBorderless;
    private boolean editable = true;
    /**
     * The text field that holds the date
     */
    private JSpinner _spinner;
    private JCalendarInvokerButton _dropDownButton;
    /**
     * The JWindow for the Popup
     */
    private JWindow _calendarWindow;
    /**
     * The JCalendarPanel inside the PopUp
     */
    private JCalendarPanel _calendarPanel;
    /**
     * The list of ChangeListeners
     */
    private ArrayList _changeListener = new ArrayList();
    /**
     * Currently firing an ChangeEvent?
     */
    private boolean _fireingChangeEvent = false;
    /**
     * Something changed in the JCalendarPanel ?
     */
    private boolean _changed = false;
}

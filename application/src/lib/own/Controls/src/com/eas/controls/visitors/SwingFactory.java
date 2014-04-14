/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.visitors;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.ControlsUtils;
import com.eas.controls.DesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.FormEventsExecutor;
import com.eas.controls.HtmlContentEditorKit;
import com.eas.controls.containers.DesktopDesignInfo;
import com.eas.controls.containers.LayersDesignInfo;
import com.eas.controls.containers.PanelDesignInfo;
import com.eas.controls.containers.ScrollDesignInfo;
import com.eas.controls.containers.SplitDesignInfo;
import com.eas.controls.containers.TabsDesignInfo;
import com.eas.controls.containers.ToolbarDesignInfo;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.controls.layouts.constraints.AbsoluteConstraintsDesignInfo;
import com.eas.controls.layouts.constraints.LayersLayoutConstraintsDesignInfo;
import com.eas.controls.layouts.constraints.TabsConstraintsDesignInfo;
import com.eas.controls.menus.MenuCheckItemDesignInfo;
import com.eas.controls.menus.MenuDesignInfo;
import com.eas.controls.menus.MenuItemDesignInfo;
import com.eas.controls.menus.MenuRadioItemDesignInfo;
import com.eas.controls.menus.MenuSeparatorDesignInfo;
import com.eas.controls.menus.MenubarDesignInfo;
import com.eas.controls.menus.PopupDesignInfo;
import com.eas.controls.plain.ButtonDesignInfo;
import com.eas.controls.plain.ButtonGroupDesignInfo;
import com.eas.controls.plain.CheckDesignInfo;
import com.eas.controls.plain.DropDownButtonDesignInfo;
import com.eas.controls.plain.EditorPaneDesignInfo;
import com.eas.controls.plain.FormattedFieldDesignInfo;
import com.eas.controls.plain.LabelDesignInfo;
import com.eas.controls.plain.PasswordFieldDesignInfo;
import com.eas.controls.plain.ProgressBarDesignInfo;
import com.eas.controls.plain.RadioDesignInfo;
import com.eas.controls.plain.SliderDesignInfo;
import com.eas.controls.plain.TextFieldDesignInfo;
import com.eas.controls.plain.TextPaneDesignInfo;
import com.eas.controls.plain.ToggleButtonDesignInfo;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.gui.JDropDownButton;
import com.eas.resources.images.IconCache;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mg
 */
public class SwingFactory implements ControlsDesignInfoVisitor {

    protected static final String EMPTY_TEXT_PROP_NAME = "emptyText";

    // initialized from client code
    protected FormEventsExecutor eventsExecutor;
    // generated while working
    protected Map<String, ControlDesignInfo> controlDesignInfos = new HashMap<>();
    protected Map<String, JComponent> components = new HashMap<>();
    protected Map<String, JComponent> nonvisuals = new HashMap<>();
    protected Set<String> layoutedContainers = new HashSet<>();
    protected List<Runnable> postprocessing = new ArrayList<>();
    // current processed component / result when partial creation is used
    protected Component comp;
    // results
    protected JPanel result;

    /**
     * Constructor for entire form creation
     *
     * @param aEventsExecutor
     */
    public SwingFactory(FormEventsExecutor aEventsExecutor) {
        super();
        eventsExecutor = aEventsExecutor;
    }

    /**
     * Constructor for partial form creation. One by one control by it's design
     * info, for example.
     */
    public SwingFactory() {
        super();
    }

    public void applyAbsoluteConstraints(final Component comp, final AbsoluteConstraintsDesignInfo absolute) {
        postprocessing.add(new Runnable() {
            @Override
            public void run() {
                comp.setLocation(absolute.getLocation());
                Dimension size = new Dimension(absolute.getSize());
                Dimension preferredSize = comp.getPreferredSize();
                if (size.width < 0) {
                    size.width = preferredSize.width;
                }
                if (size.height < 0) {
                    size.height = preferredSize.height;
                }
                comp.setSize(size);
            }
        });
    }

    public void checkClassicContainerLayout(Container parent) {
        if (!isHiddenLayoutContainer(parent) && !layoutedContainers.contains(parent.getName())) {
            ControlDesignInfo parentDesignInfo = controlDesignInfos.get(parent.getName());
            assert parentDesignInfo instanceof ContainerDesignInfo;
            LayoutFactory lFactory = new LayoutFactory(parent, components);
            ((ContainerDesignInfo) parentDesignInfo).getLayout().accept(lFactory);
            parent.setLayout(lFactory.getResult());
            layoutedContainers.add(parent.getName());
        }
    }

    public Component getComp() {
        return comp;
    }

    public JPanel getResult() {
        return result;
    }

    public Map<String, JComponent> getComponents() {
        return components;
    }

    public Map<String, JComponent> getNonvisuals() {
        return nonvisuals;
    }

    public Map<String, ControlDesignInfo> getControlDesignInfos() {
        return controlDesignInfos;
    }

    public void processControlProperties(Component comp, ControlDesignInfo aInfo) {
        // properties
        comp.setName(aInfo.getName());
        if (aInfo.getBackground() != null) {
            comp.setBackground(aInfo.getBackground());
        }
        if (aInfo.getForeground() != null) {
            comp.setForeground(aInfo.getForeground());
        }
        comp.setEnabled(aInfo.isEnabled());
        comp.setFocusable(aInfo.isFocusable());
        if (aInfo.getFont() != null) {
            comp.setFont(aInfo.getFont());
        }
        if (!(comp instanceof JPopupMenu) && !(comp instanceof JMenuItem)) {
            comp.setVisible(aInfo.isVisible());

            if (aInfo.getMinimumSize() != null) {
                comp.setMinimumSize(aInfo.getMinimumSize());
            }
            if (aInfo.getPreferredSize() != null) {
                comp.setPreferredSize(aInfo.getPreferredSize());
            } else if (aInfo.getDesignedPreferredSize() != null) {
                comp.setPreferredSize(aInfo.getDesignedPreferredSize());
            }
            if (aInfo.getMaximumSize() != null) {
                comp.setMaximumSize(aInfo.getMaximumSize());
            }
        }

        if (comp instanceof JComponent) {
            JComponent jcomp = (JComponent) comp;
            Component popupComp = resolveComponent(aInfo.getComponentPopupMenu());
            assert popupComp == null || popupComp instanceof JPopupMenu;
            if (popupComp != null) {
                jcomp.setComponentPopupMenu((JPopupMenu) popupComp);
            }
            jcomp.setNextFocusableComponent(this.<Component>resolveComponent(aInfo.getNextFocusableComponent()));
            jcomp.setAutoscrolls(aInfo.isAutoscrolls());
            jcomp.setOpaque(aInfo.isOpaque());
            jcomp.setToolTipText(aInfo.getToolTipText());
            if (aInfo.getBorder() != null) {
                SwingBorderFactory bFactory = new SwingBorderFactory(this);
                aInfo.getBorder().accept(bFactory);
                jcomp.setBorder(bFactory.getBorder());
            }
        }
        try {
            comp.setCursor(Cursor.getPredefinedCursor(aInfo.getCursor()));
        } catch (IllegalArgumentException ex) {
            comp.setCursor(Cursor.getDefaultCursor());
        }
    }

    protected Class<?> findClassByDesignInfo(ControlDesignInfo aDi) {
        ControlClassFinder clsFinder = new ControlClassFinder();
        aDi.accept(clsFinder);
        return clsFinder.getResult();
    }

    @Override
    public void visit(FormDesignInfo aInfo) {
        result = new JPanel();
        comp = result;
        result.setName(aInfo.getName());
        processControlProperties(result, aInfo);
        processControlEvents(aInfo);
        result.setVisible(true);// hidden forms are made with windows hide, but this is root container and so it must be visible allways!
        if (aInfo.getDesignedPreferredSize() != null) {
            result.setPreferredSize(aInfo.getDesignedPreferredSize());
        }
        result.setOpaque(true);
        components.put(result.getName(), result);
        controlDesignInfos.put(aInfo.getName(), aInfo);
        List<DesignInfo> generalList = new ArrayList<>();
        generalList.addAll(aInfo.getNonvisuals());
        generalList.addAll(aInfo.getChildren());
        for (DesignInfo di : generalList) {
            if (di instanceof ControlDesignInfo) {
                ControlDesignInfo control = (ControlDesignInfo) di;
                Class cls = findClassByDesignInfo(control);
                if (cls != null) {
                    try {
                        controlDesignInfos.put(control.getName(), control);
                        Object instance = cls.newInstance();
                        if (instance instanceof JPopupMenu || instance instanceof ButtonGroup) {
                            if (instance instanceof ButtonGroup) {
                                instance = new ButtonGroupWrapper((ButtonGroup) instance);
                            }
                            nonvisuals.put(control.getName(), (JComponent) instance);
                        } else {
                            components.put(control.getName(), (JComponent) instance);
                        }
                        assert instance instanceof JComponent;
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(SwingFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }// parentless grid columns and map layers must be ignored
        }
        for (DesignInfo di : generalList) {
            if (di instanceof ControlDesignInfo) {
                ControlDesignInfo control = (ControlDesignInfo) di;
                comp = resolveComponent(control.getName());
                control.accept(this);
            }// parentless grid columns and map layers must be ignored
        }
        for (Runnable runnable : postprocessing) {
            runnable.run();
        }
    }

    @Override
    public void visit(LabelDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JLabel;
        JLabel lbl = (JLabel) comp;
        lbl.setIcon(resolveIcon(aInfo.getIcon()));
        lbl.setDisabledIcon(resolveIcon(aInfo.getDisabledIcon()));
        if (aInfo.getDisplayedMnemonic() != null) {
            lbl.setDisplayedMnemonic(aInfo.getDisplayedMnemonic());
        }
        lbl.setText(aInfo.getText());
        try {
            lbl.setDisplayedMnemonicIndex(aInfo.getDisplayedMnemonicIndex());
        } catch (IllegalArgumentException ex) {
            // no op. Bad mnemonic index is not harmful
            ex = null;
        }
        lbl.setHorizontalAlignment(aInfo.getHorizontalAlignment());
        lbl.setHorizontalTextPosition(aInfo.getHorizontalTextPosition());
        lbl.setIconTextGap(aInfo.getIconTextGap());
        lbl.setLabelFor(components.get(aInfo.getLabelFor()));
        lbl.setVerticalAlignment(aInfo.getVerticalAlignment());
        lbl.setVerticalTextPosition(aInfo.getVerticalTextPosition());
    }

    public void visitAbstractButton(ButtonDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof AbstractButton;
        AbstractButton btn = (AbstractButton) comp;
        btn.setBorderPainted(aInfo.isBorderPainted());
        btn.setContentAreaFilled(aInfo.isContentAreaFilled());
        btn.setFocusPainted(aInfo.isFocusPainted());
        btn.setRolloverEnabled(aInfo.isRolloverEnabled());
        btn.setSelected(aInfo.isSelected());
        btn.setDisabledIcon(resolveIcon(aInfo.getDisabledIcon()));
        btn.setDisabledSelectedIcon(resolveIcon(aInfo.getDisabledSelectedIcon()));
        btn.setHorizontalAlignment(aInfo.getHorizontalAlignment());
        btn.setHorizontalTextPosition(aInfo.getHorizontalTextPosition());
        btn.setIcon(resolveIcon(aInfo.getIcon()));
        btn.setIconTextGap(aInfo.getIconTextGap());
        btn.setPressedIcon(resolveIcon(aInfo.getPressedIcon()));
        btn.setRolloverIcon(resolveIcon(aInfo.getRolloverIcon()));
        btn.setRolloverSelectedIcon(resolveIcon(aInfo.getRolloverSelectedIcon()));
        btn.setSelectedIcon(resolveIcon(aInfo.getSelectedIcon()));
        btn.setText(aInfo.getText());
        btn.setVerticalAlignment(aInfo.getVerticalAlignment());
        btn.setVerticalTextPosition(aInfo.getVerticalTextPosition());
        btn.setMargin(aInfo.getMargin());

        ButtonGroupWrapper bgw = resolveComponent(aInfo.getButtonGroup());
        if (bgw != null) {
            bgw.add(btn);
        }
    }

    @Override
    public void visit(ButtonDesignInfo aInfo) {
        visitAbstractButton(aInfo);
        assert comp instanceof JButton;
        JButton btn = (JButton) comp;
        btn.setDefaultCapable(aInfo.isDefaultCapable());
    }

    @Override
    public void visit(DropDownButtonDesignInfo aInfo) {
        visitAbstractButton(aInfo);
        assert comp instanceof JDropDownButton;
        JDropDownButton btn = (JDropDownButton) comp;
        btn.setDefaultCapable(aInfo.isDefaultCapable());
        JPopupMenu menu = this.<JPopupMenu>resolveComponent(aInfo.getDropDownMenu());
        btn.setDropDownMenu(menu);
    }

    @Override
    public void visit(ButtonGroupDesignInfo aInfo) {
    }

    @Override
    public void visit(CheckDesignInfo aInfo) {
        visitAbstractButton((ButtonDesignInfo) aInfo);
        assert comp instanceof JCheckBox;
        JCheckBox check = (JCheckBox) comp;
        check.setBorderPaintedFlat(aInfo.isBorderPaintedFlat());
    }

    @Override
    public void visit(TextPaneDesignInfo aInfo) {
        assert comp instanceof JTextPane;
        JTextPane ed = (JTextPane) comp;
        visitTextComponent(aInfo);
    }

    @Override
    public void visit(EditorPaneDesignInfo aInfo) {
        try {
            assert comp instanceof JEditorPane;
            JEditorPane ed = (JEditorPane) comp;
            ed.setEditorKitForContentType("text/html", new HtmlContentEditorKit());
            ed.setContentType("text/html");
            if (aInfo.getPage() != null) {
                ed.setPage(aInfo.getPage());
            } else {
                visitTextComponent(aInfo);
            }
        } catch (IOException ex) {
            Logger.getLogger(SwingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void visit(FormattedFieldDesignInfo aInfo) {
        visit((TextFieldDesignInfo) aInfo);
        assert comp instanceof JFormattedTextField;
        JFormattedTextField tf = (JFormattedTextField) comp;
        tf.setFocusLostBehavior(aInfo.getFocusLostBehavior());
        tf.setFormatterFactory(ControlsUtils.formatterFactoryByFormat(aInfo.getFormat(), aInfo.getValueType()));
    }

    @Override
    public void visit(PasswordFieldDesignInfo aInfo) {
        visit((TextFieldDesignInfo) aInfo);
        assert comp instanceof JPasswordField;
        JPasswordField pf = (JPasswordField) comp;
    }

    @Override
    public void visit(ProgressBarDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JProgressBar;
        JProgressBar pb = (JProgressBar) comp;
        pb.setBorderPainted(aInfo.isBorderPainted());
        pb.setIndeterminate(aInfo.isIndeterminate());
        pb.setStringPainted(aInfo.isStringPainted());
        pb.setMaximum(aInfo.getMaximum());
        pb.setMinimum(aInfo.getMinimum());
        pb.setOrientation(aInfo.getOrientation());
        pb.setString(aInfo.getString());
        pb.setValue(aInfo.getValue());
    }

    @Override
    public void visit(RadioDesignInfo aInfo) {
        visitAbstractButton((ButtonDesignInfo) aInfo);
        assert comp instanceof JRadioButton;
        JRadioButton rb = (JRadioButton) comp;
    }

    @Override
    public void visit(SliderDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JSlider;
        JSlider sl = (JSlider) comp;
        sl.setInverted(aInfo.isInverted());
        sl.setPaintLabels(aInfo.isPaintLabels());
        sl.setPaintTicks(aInfo.isPaintTicks());
        sl.setPaintTrack(aInfo.isPaintTrack());
        sl.setSnapToTicks(aInfo.isSnapToTicks());
        sl.setExtent(aInfo.getExtent());
        sl.setMajorTickSpacing(aInfo.getMajorTickSpacing());
        sl.setMaximum(aInfo.getMaximum());
        sl.setMinimum(aInfo.getMinimum());
        sl.setMinorTickSpacing(aInfo.getMinorTickSpacing());
        sl.setOrientation(aInfo.getOrientation());
        sl.setValue(aInfo.getValue());
    }

    public void visitTextComponent(TextFieldDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JTextComponent;
        JTextComponent tf = (JTextComponent) comp;
        tf.setText(aInfo.getText());
        tf.setDragEnabled(aInfo.isDragEnabled());
        tf.setEditable(aInfo.isEditable());
        if (aInfo.getCaretColor() != null) {
            tf.setCaretColor(aInfo.getCaretColor());
        }
        tf.setCaretPosition(aInfo.getCaretPosition());
        if (aInfo.getDisabledTextColor() != null) {
            tf.setDisabledTextColor(aInfo.getDisabledTextColor());
        }
        if (aInfo.getSelectedTextColor() != null) {
            tf.setSelectedTextColor(aInfo.getSelectedTextColor());
        }
        if (aInfo.getSelectionColor() != null) {
            tf.setSelectionColor(aInfo.getSelectionColor());
        }
        tf.setSelectionEnd(aInfo.getSelectionEnd());
        tf.setSelectionStart(aInfo.getSelectionStart());
        tf.putClientProperty(EMPTY_TEXT_PROP_NAME, aInfo.getEmptyText());
    }

    @Override
    public void visit(TextFieldDesignInfo aInfo) {
        visitTextComponent(aInfo);
        assert comp instanceof JTextField;
        JTextField tf = (JTextField) comp;
        tf.setColumns(aInfo.getColumns());
        tf.setFont(aInfo.getFont());
        tf.setHorizontalAlignment(aInfo.getHorizontalAlignment());
        tf.setScrollOffset(aInfo.getScrollOffset());
    }

    @Override
    public void visit(ToggleButtonDesignInfo aInfo) {
        visitAbstractButton((ButtonDesignInfo) aInfo);
        assert comp instanceof JToggleButton;
        JToggleButton tb = (JToggleButton) comp;
    }

    @Override
    public void visit(DesktopDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JDesktopPane;
        JDesktopPane dp = (JDesktopPane) comp;
    }

    @Override
    public void visit(LayersDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JLayeredPane;
        JLayeredPane lp = (JLayeredPane) comp;
    }

    @Override
    public void visit(PanelDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JPanel;
        JPanel pnl = (JPanel) comp;
    }

    @Override
    public void visit(ScrollDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JScrollPane;
        JScrollPane sp = (JScrollPane) comp;
        sp.setWheelScrollingEnabled(aInfo.isWheelScrollingEnabled());
        Component columnHeader = resolveComponent(aInfo.getColumnHeader());
        if (columnHeader != null) {
            sp.setColumnHeaderView(columnHeader);
        }
        sp.setHorizontalScrollBarPolicy(aInfo.getHorizontalScrollBarPolicy());
        Component rowHeader = resolveComponent(aInfo.getRowHeader());
        if (rowHeader != null) {
            sp.setRowHeaderView(rowHeader);
        }
        sp.setVerticalScrollBarPolicy(aInfo.getVerticalScrollBarPolicy());
    }

    @Override
    public void visit(SplitDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JSplitPane;
        JSplitPane sp = (JSplitPane) comp;
        sp.setContinuousLayout(aInfo.isContinuousLayout());
        sp.setOneTouchExpandable(aInfo.isOneTouchExpandable());
        sp.setDividerLocation(aInfo.getDividerLocation());
        sp.setDividerSize(aInfo.getDividerSize());
        sp.setOrientation(aInfo.getOrientation());
        sp.setResizeWeight(aInfo.getResizeWeight());

        Component left = resolveComponent(aInfo.getLeftComponent());
        if (left != null) {
            sp.setLeftComponent(left);
        }
        Component right = resolveComponent(aInfo.getRightComponent());
        if (right != null) {
            sp.setRightComponent(right);
        }
    }

    @Override
    public void visit(TabsDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JTabbedPane;
        final JTabbedPane tp = (JTabbedPane) comp;
        tp.setTabPlacement(aInfo.getTabPlacement());
        tp.setTabLayoutPolicy(aInfo.getTabLayoutPolicy());
        final Component selected = resolveComponent(aInfo.getSelectedComponent());
        if (selected != null) {
            postprocessing.add(new Runnable() {
                @Override
                public void run() {
                    tp.setSelectedComponent(selected);
                }
            });
        }
    }

    @Override
    public void visit(ToolbarDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JToolBar;
        JToolBar tb = (JToolBar) comp;
        tb.setBorderPainted(aInfo.isBorderPainted());
        tb.setFloatable(aInfo.isFloatable());
        tb.setRollover(aInfo.isRollover());
        tb.setMargin(aInfo.getMargin());
        tb.setOrientation(aInfo.getOrientation());
    }

    @Override
    public void visit(MenuItemDesignInfo aInfo) {
        visitAbstractButton((ButtonDesignInfo) aInfo);
        assert comp instanceof JMenuItem;
        JMenuItem mi = (JMenuItem) comp;
        mi.setAccelerator(aInfo.getAccelerator());
    }

    @Override
    public void visit(MenuCheckItemDesignInfo aInfo) {
        visit((MenuItemDesignInfo) aInfo);
        assert comp instanceof JCheckBoxMenuItem;
        JCheckBoxMenuItem mi = (JCheckBoxMenuItem) comp;
    }

    @Override
    public void visit(MenuRadioItemDesignInfo aInfo) {
        visit((MenuItemDesignInfo) aInfo);
        assert comp instanceof JRadioButtonMenuItem;
        JRadioButtonMenuItem mi = (JRadioButtonMenuItem) comp;
    }

    @Override
    public void visit(MenuSeparatorDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JSeparator;
        JSeparator mi = (JSeparator) comp;
    }

    @Override
    public void visit(MenuDesignInfo aInfo) {
        visitAbstractButton((ButtonDesignInfo) aInfo);
        assert comp instanceof JMenu;
        JMenu m = (JMenu) comp;
        m.setDelay(aInfo.getDelay());
        //m.setComponentOrientation(aInfo.getOrientation());
    }

    @Override
    public void visit(MenubarDesignInfo aInfo) {
        visitContainer(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JMenuBar;
        JMenuBar m = (JMenuBar) comp;
        m.setBorderPainted(aInfo.isBorderPainted());
        m.setMargin(aInfo.getMargin());
    }

    @Override
    public void visit(PopupDesignInfo aInfo) {
        visitControl(aInfo);
        processControlEvents(aInfo);
        assert comp instanceof JPopupMenu;
        JPopupMenu m = (JPopupMenu) comp;
        m.setBorderPainted(aInfo.isBorderPainted());
        m.setLabel(aInfo.getLabel());
    }

    private void visitContainer(ContainerDesignInfo aInfo) {
        visitControl(aInfo);
        assert comp instanceof Container;
        Container cont = (Container) comp;
        checkClassicContainerLayout(cont);
        cont.setFocusCycleRoot(aInfo.isFocusCycleRoot());
    }

    protected ControlEventsIProxy createEventsProxy() {
        return new ControlEventsIProxy(eventsExecutor);
    }

    protected void processControlEvents(final ControlDesignInfo aInfo) {
        assert comp != null;
        if (eventsExecutor != null) {
            final ControlEventsIProxy proxy = createEventsProxy();
            proxy.setHandlee(comp);
        }
    }

    protected void visitControl(ControlDesignInfo aInfo) {
        if (comp == null) {
            try {
                Class<?> compClass = findClassByDesignInfo(aInfo);
                assert Component.class.isAssignableFrom(compClass);
                comp = (Component) compClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(SwingFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        assert comp != null;
        processControlProperties(comp, aInfo);
        // structure
        if (aInfo.getParent() != null && !aInfo.getParent().isEmpty()) {
            Component parentComp = resolveComponent(aInfo.getParent());
            assert parentComp instanceof Container;
            final Container parent = (Container) parentComp;
            if (isHiddenLayoutContainer(parent)) {
                if (parent instanceof JScrollPane) {
                    JScrollPane scroll = (JScrollPane) parent;
                    comp.setPreferredSize(aInfo.getDesignedPreferredSize());
                    scroll.setViewportView(comp);
                } else if (parent instanceof JSplitPane) {
                    // Split pane children are:
                    // - left component
                    // - right component
                    // Theese children are setted while resolving component references of a split pane.
                } else if (parent instanceof JToolBar) {
                    JToolBar tb = (JToolBar) parent;
                    tb.add(comp);
                } else if (parent instanceof JLayeredPane) {
                    JLayeredPane lp = (JLayeredPane) parent;
                    assert aInfo.getConstraints() instanceof LayersLayoutConstraintsDesignInfo;
                    LayersLayoutConstraintsDesignInfo lc = (LayersLayoutConstraintsDesignInfo) aInfo.getConstraints();
                    lp.add(comp, Integer.valueOf(lc.getLayer()));
                    applyAbsoluteConstraints(comp, lc);
                } else if (parent instanceof JTabbedPane) {
                    JTabbedPane tp = (JTabbedPane) parent;
                    assert aInfo.getConstraints() instanceof TabsConstraintsDesignInfo;
                    TabsConstraintsDesignInfo tc = (TabsConstraintsDesignInfo) aInfo.getConstraints();
                    tp.addTab(tc.getTabTitle(), resolveIcon(tc.getTabIcon()), comp, tc.getTabTooltipText());
                    tp.setDisabledIconAt(tp.getTabCount() - 1, resolveIcon(tc.getTabDisabledIcon()));
                    tp.setBackgroundAt(tp.getTabCount() - 1, tc.getTabBackground());
                    tp.setForegroundAt(tp.getTabCount() - 1, tc.getTabForeground());
                } else if (parent instanceof JMenuBar) {
                    JMenuBar mb = (JMenuBar) parent;
                    mb.add(comp);
                } else if (parent instanceof JPopupMenu) {
                    JPopupMenu popup = (JPopupMenu) parent;
                    popup.add(comp);
                } else if (parent instanceof JMenu) {
                    JMenu menu = (JMenu) parent;
                    menu.add(comp);
                }
            } else {
                // Classic containers
                if (parent.getLayout() == null)// Absolute positioning
                {
                    if (aInfo.getConstraints() instanceof AbsoluteConstraintsDesignInfo) {
                        AbsoluteConstraintsDesignInfo absolute = (AbsoluteConstraintsDesignInfo) aInfo.getConstraints();
                        applyAbsoluteConstraints(comp, absolute);
                    }
                    parent.add(comp);
                } else {
                    checkClassicContainerLayout(parent);
                    if (parent.getLayout() instanceof GroupLayout) {
                        // Nothing to do. All the work is performed while forming the group layout.
                    } else {
                        // Ordinary container with simple classic layout
                        // layout constraints
                        Object constraints = null;
                        if (aInfo.getConstraints() != null) {
                            LayoutConstraintsFactory lcFactory = new LayoutConstraintsFactory();
                            aInfo.getConstraints().accept(lcFactory);
                            constraints = lcFactory.getResult();
                        }
                        if (constraints != null) {
                            parent.add(comp, constraints);
                        } else {
                            parent.add(comp);
                        }
                    }
                }
            }
        }
    }

    public static void prefToMaxForBox(int axis, java.awt.Component comp) {
        if (axis == BoxLayout.LINE_AXIS || axis == BoxLayout.X_AXIS) {
            comp.setMaximumSize(new Dimension(comp.getPreferredSize().width, Integer.MAX_VALUE));
        } else {
            comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        }
    }

    public Icon resolveIcon(String aIconName) {
        return IconCache.getIcon(aIconName);
    }

    private <T> T resolveComponent(String aName) {
        Object res = components.get(aName);
        if (res == null) {
            res = nonvisuals.get(aName);
        }
        if (res != null) {
            return (T) res;
        }
        return null;
    }

    private boolean isHiddenLayoutContainer(Container parent) {
        return parent instanceof JScrollPane
                || parent instanceof JSplitPane
                || parent instanceof JToolBar
                || parent instanceof JLayeredPane
                || parent instanceof JTabbedPane
                || parent instanceof JMenuBar
                || parent instanceof JPopupMenu
                || parent instanceof JMenu;
    }
}

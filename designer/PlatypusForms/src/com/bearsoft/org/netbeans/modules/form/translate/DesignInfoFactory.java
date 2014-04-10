/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.ComponentReference;
import com.bearsoft.org.netbeans.modules.form.Event;
import com.bearsoft.org.netbeans.modules.form.FormProperty;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualFormContainer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGrid;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelMap;
import com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor;
import com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon;
import com.bearsoft.org.netbeans.modules.form.editors.NbBorder;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.controls.FormDesignInfo;
import com.eas.controls.borders.*;
import com.eas.controls.containers.*;
import com.eas.controls.layouts.*;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.controls.menus.*;
import com.eas.controls.plain.*;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.dbcontrols.scheme.DbSchemeDesignInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import com.eas.gui.JDropDownButton;
import com.eas.store.Object2Dom;
import com.eas.util.StringUtils;
import java.awt.*;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author mg
 */
public class DesignInfoFactory {

    public static DesignInfo create(Class<?> aBeanClass) {
        DesignInfo designInfo = null;
        // top-level containers
        if (RootPaneContainer.class.isAssignableFrom(aBeanClass)) {
            designInfo = new FormDesignInfo();
        } else // db controls
        if (DbCheck.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbCheckDesignInfo();
        } else if (DbCombo.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbComboDesignInfo();
        } else if (DbDate.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbDateDesignInfo();
        } else if (DbImage.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbImageDesignInfo();
        } else if (DbText.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbTextDesignInfo();
        } else if (DbLabel.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbLabelDesignInfo();
        } else if (DbScheme.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbSchemeDesignInfo();
        } else if (DbSpin.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbSpinDesignInfo();
        } else if (DbGrid.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbGridDesignInfo();
        } else if (DbMap.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DbMapDesignInfo();
            // non-visual components
        } else if (ButtonGroup.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ButtonGroupDesignInfo();
            // plain visual controls
        } else if (JLabel.class.isAssignableFrom(aBeanClass)) {
            designInfo = new LabelDesignInfo();
        } else if (JTextPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new TextPaneDesignInfo();
        } else if (JEditorPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new EditorPaneDesignInfo();
        } else if (JFormattedTextField.class.isAssignableFrom(aBeanClass)) {
            designInfo = new FormattedFieldDesignInfo();
        } else if (JPasswordField.class.isAssignableFrom(aBeanClass)) {
            designInfo = new PasswordFieldDesignInfo();
        } else if (JTextField.class.isAssignableFrom(aBeanClass)) {
            designInfo = new TextFieldDesignInfo();
        } else if (JProgressBar.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ProgressBarDesignInfo();
        } else if (JRadioButton.class.isAssignableFrom(aBeanClass)) {
            designInfo = new RadioDesignInfo();
        } else if (JCheckBox.class.isAssignableFrom(aBeanClass)) {
            designInfo = new CheckDesignInfo();
        } else if (JToggleButton.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ToggleButtonDesignInfo();
        } else if (JDropDownButton.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DropDownButtonDesignInfo();
        } else if (JButton.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ButtonDesignInfo();
        } else if (JSlider.class.isAssignableFrom(aBeanClass)) {
            designInfo = new SliderDesignInfo();
            // containers
        } else if (JDesktopPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new DesktopDesignInfo();
        } else if (JLayeredPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new LayersDesignInfo();
        } else if (JPanel.class.isAssignableFrom(aBeanClass)) {
            designInfo = new PanelDesignInfo();
        } else if (JScrollPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ScrollDesignInfo();
        } else if (JSplitPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new SplitDesignInfo();
        } else if (JTabbedPane.class.isAssignableFrom(aBeanClass)) {
            designInfo = new TabsDesignInfo();
        } else if (JToolBar.class.isAssignableFrom(aBeanClass)) {
            designInfo = new ToolbarDesignInfo();
            // menus
        } else if (JMenu.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenuDesignInfo();
        } else if (JCheckBoxMenuItem.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenuCheckItemDesignInfo();
        } else if (JRadioButtonMenuItem.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenuRadioItemDesignInfo();
        } else if (JMenuItem.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenuItemDesignInfo();
        } else if (JSeparator.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenuSeparatorDesignInfo();
        } else if (JMenuBar.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MenubarDesignInfo();
        } else if (JPopupMenu.class.isAssignableFrom(aBeanClass)) {
            designInfo = new PopupDesignInfo();
            // layouts
        } else if (BorderLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new BorderLayoutDesignInfo();
        } else if (BoxLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new BoxLayoutDesignInfo();
        } else if (CardLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new CardLayoutDesignInfo();
        } else if (FlowLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new FlowLayoutDesignInfo();
        } else if (GridBagLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new GridBagLayoutDesignInfo();
        } else if (GridLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new GridLayoutDesignInfo();
        } else if (GroupLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new GroupLayoutDesignInfo();
        } else if (AbsoluteLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new AbsoluteLayoutDesignInfo();
        } else if (MarginLayout.class.isAssignableFrom(aBeanClass)) {
            designInfo = new MarginLayoutDesignInfo();
        }
        return designInfo;
    }
    protected static Map<String, Map<String, Method>> settersCache = new HashMap<>();

    public static Map<String, Method> getSetters(Class<?> di) throws Exception {
        Map<String, Method> setters = settersCache.get(di.getName());
        if (setters == null) {
            setters = new HashMap<>();
            settersCache.put(di.getName(), setters);
            Method[] methods = di.getMethods();
            for (Method m : methods) {
                int modifiers = m.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers) && !m.isSynthetic() && !m.isVarArgs() && m.getParameterTypes() != null && m.getParameterTypes().length == 1 && m.getName().startsWith(Object2Dom.BEANY_SETTER_PREFIX) && m.getName().length() > Object2Dom.BEANY_SETTER_PREFIX.length()) {
                    String propName = m.getName().substring(Object2Dom.BEANY_SETTER_PREFIX.length());
                    setters.put(Introspector.decapitalize(propName), m);
                }
            }
        }
        return setters;
    }
    protected static Map<String, Map<String, Method>> gettersCache = new HashMap<>();

    public static Map<String, Method> getGetters(Class<?> di) throws Exception {
        Map<String, Method> getters = gettersCache.get(di.getName());
        if (getters == null) {
            getters = new HashMap<>();
            gettersCache.put(di.getName(), getters);
            Method[] methods = di.getMethods();
            for (Method m : methods) {
                int modifiers = m.getModifiers();
                if (Modifier.isPublic(modifiers)
                        && !Modifier.isAbstract(modifiers)
                        && !Modifier.isStatic(modifiers)
                        && !m.isSynthetic()
                        && !m.isVarArgs()
                        && (m.getParameterTypes() == null || m.getParameterTypes().length == 0)
                        && ((m.getName().startsWith(Object2Dom.BEANY_GETTER_PREFIX) && m.getName().length() > Object2Dom.BEANY_GETTER_PREFIX.length())
                        || (m.getName().startsWith(Object2Dom.BEANY_IS_PREFIX) && m.getName().length() > Object2Dom.BEANY_IS_PREFIX.length()))) {
                    String propName = null;
                    if (m.getName().startsWith(Object2Dom.BEANY_GETTER_PREFIX)) {
                        propName = m.getName().substring(Object2Dom.BEANY_GETTER_PREFIX.length());
                    } else {
                        assert m.getName().startsWith(Object2Dom.BEANY_IS_PREFIX);
                        propName = m.getName().substring(Object2Dom.BEANY_IS_PREFIX.length());
                    }
                    getters.put(Introspector.decapitalize(propName), m);
                }
            }
        }
        return getters;
    }

    public static void initWithComponent(DesignInfo designInfo, final RADComponent<?> aRadComp) throws Exception {
        // properties
        initWithProperties(designInfo, aRadComp.getAllBeanProperties());
        // form designed size
        if (designInfo instanceof FormDesignInfo && aRadComp instanceof RADVisualFormContainer) {
            RADVisualFormContainer radForm = (RADVisualFormContainer) aRadComp;
            FormDesignInfo fdi = (FormDesignInfo) designInfo;
            fdi.setDesignedPreferredSize(radForm.getDesignerSize());
        } else if (designInfo instanceof ControlDesignInfo) {
            ControlDesignInfo cdi = (ControlDesignInfo) designInfo;
            if (aRadComp instanceof RADVisualComponent<?>)// it may be button group, wich is not a visual component
            {
                Component comp = ((RADVisualComponent<?>) aRadComp).getBeanInstance();
                if (comp != null) {
                    cdi.setDesignedPreferredSize(comp.getSize());
                    if (aRadComp instanceof RADModelGrid) {
                        assert comp instanceof DbGrid;
                        assert cdi instanceof DbGridDesignInfo;
                        DbGridDesignInfo gridDi = (DbGridDesignInfo) cdi;
                        RADModelGrid radGrid = (RADModelGrid) aRadComp;
                        gridDi.getHeader().clear();
                        gridDi.getHeader().addAll(radGrid.getBeanInstance().getHeader());
                        initColumnsContainerWithEvents(radGrid);
                    } else if (aRadComp instanceof RADModelMap) {
                        assert comp instanceof DbMap;
                        assert cdi instanceof DbMapDesignInfo;
                        DbMapDesignInfo mapDi = (DbMapDesignInfo) cdi;
                        RADModelMap radMap = (RADModelMap) aRadComp;
                        mapDi.getFeatures().clear();
                        mapDi.getFeatures().addAll(radMap.getBeanInstance().getFeatures());
                    }
                }
            }
        }
        // name
        Map<String, Method> setters = getSetters(designInfo.getClass());
        Method nameSetter = setters.get("name");
        nameSetter.invoke(designInfo, aRadComp.getName());
    }

    public static void initWithProperties(DesignInfo di, FormProperty<?>[] aProperties) throws Exception {
        DesignInfo defaultInstance = di.getClass().newInstance();
        Map<String, Method> setters = getSetters(di.getClass());
        Map<String, Method> getters = getGetters(di.getClass());
        for (FormProperty<?> prop : aProperties) {
            Method setter = setters.get(prop.getName());
            if (setter != null) {
                Object propValue = prop.getValue();
                try {
                    if (prop.isDefaultValue()) {
                        Method defGetter = getters.get(prop.getName());
                        if (defGetter != null) {
                            propValue = defGetter.invoke(defaultInstance, new Object[]{});
                        } else {
                            propValue = null;
                        }
                    }
                    setter.invoke(di, propValue);
                } catch (IllegalArgumentException argex) {
                    if (propValue != null) {
                        if (propValue instanceof ComponentReference<?>
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && String.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            ComponentReference<?> compRef = (ComponentReference<?>) propValue;
                            if (compRef.getComponent() != null) {
                                setter.invoke(di, compRef.getComponent().getName());
                            }
                        } else if (propValue instanceof NbImageIcon
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && String.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            setter.invoke(di, ((NbImageIcon) propValue).getName());
                        } else if (propValue instanceof NbBorder
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1
                                && BorderDesignInfo.class.isAssignableFrom(setter.getParameterTypes()[0])) {
                            setter.invoke(di, convertNbBorderToBorderDesignInfo((NbBorder) propValue));
                        } else if (propValue instanceof java.awt.Cursor
                                && setter.getParameterTypes() != null && setter.getParameterTypes().length == 1) {
                            setter.invoke(di, ((java.awt.Cursor) propValue).getType());
                        }
                    }
                } catch (Exception ex) {
                    ex = null;
                }
            } else {
                if (RADComponent.FormatterFactoryProperty.FORMATTER_FACTORY_PROP_NAME.equals(prop.getName())) {
                    AbstractFormatterFactoryEditor.FormFormatter formatter = (AbstractFormatterFactoryEditor.FormFormatter) prop.getValue();
                    if (formatter != null) {
                        if (di instanceof FormattedFieldDesignInfo) {
                            ((FormattedFieldDesignInfo) di).setFormat(formatter.getFormat().getFormat());
                            ((FormattedFieldDesignInfo) di).setValueType(formatter.getFormat().getType());
                        }
                        if (di instanceof DbLabelDesignInfo) {
                            ((DbLabelDesignInfo) di).setFormat(formatter.getFormat().getFormat());
                            ((DbLabelDesignInfo) di).setValueType(formatter.getFormat().getType());
                        }
                    }
                }
            }
        }
    }

    protected static BorderDesignInfo convertNbBorderToBorderDesignInfo(NbBorder aBorder) throws Exception {
        if (aBorder != null) {
            Border lBorder = aBorder.toBorder();
            if (lBorder instanceof SoftBevelBorder) {
                SoftBevelBorder sbb = (SoftBevelBorder) lBorder;
                SoftBevelBorderDesignInfo sbbdi = new SoftBevelBorderDesignInfo();
                sbbdi.setBevelType(sbb.getBevelType());
                sbbdi.setHighlightInnerColor(sbb.getHighlightInnerColor());
                sbbdi.setHighlightOuterColor(sbb.getHighlightOuterColor());
                sbbdi.setShadowInnerColor(sbb.getShadowInnerColor());
                sbbdi.setShadowOuterColor(sbb.getShadowOuterColor());
                return sbbdi;
            } else if (lBorder instanceof BevelBorder) {
                BevelBorder bb = (BevelBorder) lBorder;
                BevelBorderDesignInfo bbdi = new BevelBorderDesignInfo();
                bbdi.setBevelType(bb.getBevelType());
                bbdi.setHighlightInnerColor(bb.getHighlightInnerColor());
                bbdi.setHighlightOuterColor(bb.getHighlightOuterColor());
                bbdi.setShadowInnerColor(bb.getShadowInnerColor());
                bbdi.setShadowOuterColor(bb.getShadowOuterColor());
                return bbdi;
            } else if (lBorder instanceof CompoundBorder) {
                CompoundBorderDesignInfo cbdi = new CompoundBorderDesignInfo();
                FormProperty<NbBorder> outsideProperty = aBorder.<FormProperty<NbBorder>>getPropertyOfName("outsideBorder");
                FormProperty<NbBorder> insideProperty = aBorder.<FormProperty<NbBorder>>getPropertyOfName("insideBorder");
                cbdi.setOutsideBorder(convertNbBorderToBorderDesignInfo(outsideProperty.getValue()));
                cbdi.setInsideBorder(convertNbBorderToBorderDesignInfo(insideProperty.getValue()));
                return cbdi;
            } else if (lBorder instanceof LineBorder) {
                LineBorder lb = (LineBorder) lBorder;
                LineBorderDesignInfo lbdi = new LineBorderDesignInfo();
                lbdi.setLineColor(lb.getLineColor());
                lbdi.setRoundedCorners(lb.getRoundedCorners());
                lbdi.setThickness(lb.getThickness());
                return lbdi;
            } else if (lBorder instanceof MatteBorder) {
                MatteBorder mb = (MatteBorder) lBorder;
                MatteBorderDesignInfo mbdi = new MatteBorderDesignInfo();
                mbdi.setMatteColor(mb.getMatteColor());
                FormProperty<NbImageIcon> iconProperty = aBorder.<FormProperty<NbImageIcon>>getPropertyOfName("tileIcon");
                if (iconProperty != null && iconProperty.getValue() != null) {
                    NbImageIcon icon = iconProperty.getValue();
                    mbdi.setTileIcon(icon.getName());
                }
                mbdi.setBottom(mb.getBorderInsets().bottom);
                mbdi.setLeft(mb.getBorderInsets().left);
                mbdi.setRight(mb.getBorderInsets().right);
                mbdi.setTop(mb.getBorderInsets().top);
                return mbdi;
            } else if (lBorder instanceof EmptyBorder) {
                EmptyBorder eb = (EmptyBorder) lBorder;
                EmptyBorderDesignInfo ebdi = new EmptyBorderDesignInfo();
                ebdi.setBottom(eb.getBorderInsets().bottom);
                ebdi.setLeft(eb.getBorderInsets().left);
                ebdi.setRight(eb.getBorderInsets().right);
                ebdi.setTop(eb.getBorderInsets().top);
                return ebdi;
            } else if (lBorder instanceof EtchedBorder) {
                EtchedBorder eb = (EtchedBorder) lBorder;
                EtchedBorderDesignInfo ebdi = new EtchedBorderDesignInfo();
                ebdi.setEtchType(eb.getEtchType());
                ebdi.setHighlightColor(eb.getHighlightColor());
                ebdi.setShadowColor(eb.getShadowColor());
                return ebdi;
            } else if (lBorder instanceof TitledBorder) {
                TitledBorder tb = (TitledBorder) lBorder;
                TitledBorderDesignInfo tbdi = new TitledBorderDesignInfo();
                FormProperty<NbBorder> borderProperty = aBorder.<FormProperty<NbBorder>>getPropertyOfName("border");
                if (borderProperty != null/* && borderProperty.getValue() instanceof BorderDesignSupport*/) {
                    tbdi.setBorder(convertNbBorderToBorderDesignInfo(borderProperty.getValue()));
                }
                tbdi.setTitle(tb.getTitle());
                tbdi.setTitleColor(tb.getTitleColor());
                tbdi.setTitleFont(tb.getTitleFont());
                tbdi.setTitleJustification(tb.getTitleJustification());
                tbdi.setTitlePosition(tb.getTitlePosition());
                return tbdi;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void initColumnsContainerWithEvents(ComponentContainer aColumnsContainer) throws Exception {
        for (RADComponent<?> aRadComp : aColumnsContainer.getSubBeans()) {
            assert aRadComp instanceof RADModelGridColumn;
            RADModelGridColumn radColumn = (RADModelGridColumn) aRadComp;
            initColumnsContainerWithEvents(radColumn);
        }
    }
}

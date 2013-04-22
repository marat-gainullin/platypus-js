/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.translate;

import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualContainer;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.LayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.*;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.AbsoluteLayoutSupport.AbsoluteLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.BorderLayoutSupport.BorderLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.CardLayoutSupport.CardLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.JLayeredPaneSupport.LayeredLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.JSplitPaneSupport.SplitLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.JTabbedPaneSupport.TabLayoutConstraints;
import com.bearsoft.org.netbeans.modules.form.layoutsupport.delegates.MarginLayoutSupport.MarginLayoutConstraints;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.DesignInfo;
import com.eas.controls.containers.SplitDesignInfo;
import com.eas.controls.layouts.constraints.*;
import com.eas.controls.layouts.margin.MarginConstraints;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.JSplitPane;

/**
 *
 * @author mg
 */
public class LayoutConstraintsProcessor {

    public static Class<?> resolveDelegateClass(LayoutConstraints<?> radConstraints) {
        if (radConstraints != null) {
            if (radConstraints instanceof TabLayoutConstraints) {
                return JTabbedPaneSupport.class;
            } else if (radConstraints instanceof LayeredLayoutConstraints) {
                return JLayeredPaneSupport.class;
            } else if (radConstraints instanceof AbsoluteLayoutConstraints) {
                return AbsoluteLayoutSupport.class;
            } else if (radConstraints instanceof CardLayoutConstraints) {
                return CardLayoutSupport.class;
            } else if (radConstraints instanceof BorderLayoutConstraints) {
                return BorderLayoutSupport.class;
            } else if (radConstraints instanceof SplitLayoutConstraints) {
                return JSplitPaneSupport.class;
            } else if (radConstraints instanceof MarginLayoutConstraints) {
                return MarginLayoutSupport.class;
            }
            assert false : "Unknown LayoutConstraints implementation found:" + radConstraints.getClass().getName();
        }
        return null;
    }

    /**
     *
     * @param controlDi
     * @param radComponent
     * @param radConstraints
     * @return True if <code>ControlDesignInfo</code> has been succesfully
     * processed. If returned value is false, than the component or it's parts
     * need to be resolved.
     */
    public static boolean processToDesignInfo(ControlDesignInfo controlDi, RADVisualComponent<?> radComponent, LayoutConstraints<?> radConstraints) {
        // Complex constraints, involving inter-component refernces
        if (radConstraints instanceof TabLayoutConstraints) {
            TabLayoutConstraints tc = (TabLayoutConstraints) radConstraints;
            TabsConstraintsDesignInfo tcdi = null;
            if (controlDi.getConstraints() instanceof TabsConstraintsDesignInfo) {
                tcdi = (TabsConstraintsDesignInfo) controlDi.getConstraints();
            } else {
                tcdi = new TabsConstraintsDesignInfo();
                controlDi.setConstraints(tcdi);
            }
            tcdi.setTabTitle(tc.getTitle());
            tcdi.setTabTooltipText(tc.getToolTip());
            // TODO: Solve problem with icons
            // Simple ordinary constraints of real layouts
        } else if (radConstraints instanceof LayeredLayoutConstraints) {
            LayeredLayoutConstraints lc = (LayeredLayoutConstraints) radConstraints;
            LayersLayoutConstraintsDesignInfo llcdi = new LayersLayoutConstraintsDesignInfo();
            llcdi.setLayer(lc.getLayer());
            llcdi.setLocation(lc.getBounds().getLocation());
            llcdi.setSize(new Dimension(lc.getBounds().width, lc.getBounds().height));
            controlDi.setConstraints(llcdi);
        } else if (radConstraints instanceof AbsoluteLayoutConstraints) {
            AbsoluteLayoutConstraints alc = (AbsoluteLayoutConstraints) radConstraints;
            AbsoluteConstraintsDesignInfo alcdi = new AbsoluteConstraintsDesignInfo();
            alcdi.setLocation(alc.getBounds().getLocation());
            alcdi.setSize(new Dimension(alc.getBounds().width, alc.getBounds().height));
            controlDi.setConstraints(alcdi);
        } else if (radConstraints instanceof CardLayoutConstraints) {
            CardLayoutConstraints cc = (CardLayoutConstraints) radConstraints;
            CardLayoutConstraintsDesignInfo cdi = new CardLayoutConstraintsDesignInfo();
            cdi.setCardName(cc.getConstraintsObject());
            controlDi.setConstraints(cdi);
        } else if (radConstraints instanceof BorderLayoutConstraints) {
            BorderLayoutConstraints bc = (BorderLayoutConstraints) radConstraints;
            BorderLayoutConstraintsDesignInfo bdi = new BorderLayoutConstraintsDesignInfo();
            bdi.setPlace(bc.getConstraintsObject());
            controlDi.setConstraints(bdi);
        } else if (radConstraints instanceof SplitLayoutConstraints) {
            return false;
        } else if (radConstraints instanceof MarginLayoutConstraints) {
            MarginLayoutConstraints mlc = (MarginLayoutConstraints) radConstraints;
            MarginConstraints mc = mlc.getConstraintsObject();
            MarginConstraintsDesignInfo mcdi = new MarginConstraintsDesignInfo();
            mcdi.setMarginLeft(mc.getLeft());
            mcdi.setMarginTop(mc.getTop());
            mcdi.setMarginRight(mc.getRight());
            mcdi.setMarginBottom(mc.getBottom());
            mcdi.setMarginWidth(mc.getWidth());
            mcdi.setMarginHeight(mc.getHeight());
            controlDi.setConstraints(mcdi);
        } else {
            assert false : "Unknown LayoutConstraints implementation found:" + radConstraints.getClass().getName();
        }
        return true;
    }

    public static void resolveToDesignInfo(Map<String, ControlDesignInfo> involvedDesignInfos, RADVisualComponent<?> visualComp, LayoutConstraints<?> radConstraints) {
        if (radConstraints instanceof SplitLayoutConstraints) {
            // This layout constraints lead to resolve inter-component references.
            // So, we have to perform such work in resolving references step.
            RADVisualContainer<?> parent = visualComp.getParentComponent();
            DesignInfo oSplitDesignInfo = involvedDesignInfos.get(parent.getName());
            assert oSplitDesignInfo instanceof SplitDesignInfo;
            SplitDesignInfo splitDesignInfo = (SplitDesignInfo) oSplitDesignInfo;
            SplitLayoutConstraints sc = (SplitLayoutConstraints) radConstraints;
            String place = sc.getConstraintsObject();
            switch (place) {
                case JSplitPane.BOTTOM:
                    splitDesignInfo.setRightComponent(visualComp.getName());
                    break;
                case JSplitPane.TOP:
                    splitDesignInfo.setLeftComponent(visualComp.getName());
                    break;
                case JSplitPane.LEFT:
                    splitDesignInfo.setLeftComponent(visualComp.getName());
                    break;
                case JSplitPane.RIGHT:
                    splitDesignInfo.setRightComponent(visualComp.getName());
                    break;
            }
        }
    }

    public static LayoutConstraints<?> processFromDesignInfo(ControlDesignInfo controlDi) {
        if (controlDi != null && controlDi.getConstraints() != null) {
            ConstraintsDesignInfoProcessor processor = new ConstraintsDesignInfoProcessor();
            controlDi.getConstraints().accept(processor);
            return processor.getResult();
        }
        return null;
    }

    /**
     * Component's constraints processor (visitor) responsible only on real
     * constraints and libraries non-layput constraints. Only netbeans
     * constraints cases are out of scope (such as split cnstraintss).
     */
    protected static class ConstraintsDesignInfoProcessor implements ConstraintsDesignInfoVisitor {

        protected LayoutConstraints<?> result;

        public LayoutConstraints<?> getResult() {
            return result;
        }

        @Override
        public void visit(BorderLayoutConstraintsDesignInfo di) {
            result = new BorderLayoutConstraints(di.getPlace());
        }

        @Override
        public void visit(CardLayoutConstraintsDesignInfo di) {
            result = new CardLayoutConstraints(di.getCardName());
        }

        @Override
        public void visit(GridBagLayoutConstraintsDesignInfo di) {
        }

        @Override
        public void visit(TabsConstraintsDesignInfo di) {
            result = new TabLayoutConstraints(di.getTabTitle(), null/*di.getTabIcon()*/, di.getTabTooltipText());
            // TODO: solve problem with icons
        }

        @Override
        public void visit(AbsoluteConstraintsDesignInfo di) {
            result = new AbsoluteLayoutConstraints(di.getLocation().x, di.getLocation().y, di.getSize().width, di.getSize().height);
        }

        @Override
        public void visit(LayersLayoutConstraintsDesignInfo di) {
            result = new LayeredLayoutConstraints(di.getLayer(), di.getLocation().x, di.getLocation().y, di.getSize().width, di.getSize().height);
        }

        @Override
        public void visit(MarginConstraintsDesignInfo di) {
            result = new MarginLayoutConstraints(di.getMarginLeft(),
                    di.getMarginTop(), di.getMarginRight(),
                    di.getMarginBottom(), di.getMarginWidth(), di.getMarginHeight());
        }
    }
}

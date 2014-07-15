/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.visitors;

import com.eas.controls.layouts.*;
import com.eas.controls.layouts.box.BoxLayout;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.controls.wrappers.PlatypusCardLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class LayoutFactory implements LayoutDesignInfoVisitor {

    public static final int PADDING_SEPARATE_VALUE = 10;
    protected Container targetContainer;
    protected Map<String, JComponent> components;
    protected Map<Integer, List<JComponent>> linkedSizeGroups;
    protected LayoutManager result;

    public LayoutFactory(Container aTargetContainer, Map<String, JComponent> aComponents) {
        super();
        targetContainer = aTargetContainer;
        components = aComponents;
    }

    public LayoutManager getResult() {
        return result;
    }

    public void processSizeLinks(int dimension, GroupLayoutGroupDesignInfo designGroup, JComponent component) {
        if (dimension == SwingConstants.HORIZONTAL) {
            if (designGroup.getHorizontalSizeLinkId() != GroupLayoutGroupDesignInfo.UNSETTED) {
                List<JComponent> linked = linkedSizeGroups.get(designGroup.getHorizontalSizeLinkId());
                if (linked == null) {
                    linked = new ArrayList<>();
                    linkedSizeGroups.put(designGroup.getHorizontalSizeLinkId(), linked);
                }
                linked.add(component);
            }
        } else {
            assert dimension == SwingConstants.VERTICAL;
            if (designGroup.getVerticalSizeLinkId() != GroupLayoutGroupDesignInfo.UNSETTED) {
                List<JComponent> linked = linkedSizeGroups.get(designGroup.getVerticalSizeLinkId());
                if (linked == null) {
                    linked = new ArrayList<>();
                    linkedSizeGroups.put(designGroup.getVerticalSizeLinkId(), linked);
                }
                linked.add(component);
            }
        }
    }

    @Override
    public void visit(BorderLayoutDesignInfo aInfo) {
        result = new BorderLayout();
        BorderLayout value = (BorderLayout) result;
        value.setHgap(aInfo.getHgap());
        value.setVgap(aInfo.getVgap());
    }

    @Override
    public void visit(BoxLayoutDesignInfo aInfo) {
        result = new BoxLayout(targetContainer, aInfo.getAxis(), aInfo.getHgap(), aInfo.getVgap());
    }

    @Override
    public void visit(CardLayoutDesignInfo aInfo) {
        result = new PlatypusCardLayout(aInfo.getHgap(), aInfo.getVgap());
    }

    @Override
    public void visit(FlowLayoutDesignInfo aInfo) {
        result = new FlowLayout(aInfo.getAlignment(), aInfo.getHgap(), aInfo.getVgap());
    }

    @Override
    public void visit(GridBagLayoutDesignInfo aInfo) {
        result = new GridBagLayout();
    }

    @Override
    public void visit(GridLayoutDesignInfo aInfo) {
        result = new GridLayout(aInfo.getRows(), aInfo.getColumns(), aInfo.getHgap(), aInfo.getVgap());
    }

    @Override
    public void visit(GroupLayoutDesignInfo aInfo) {
        GroupLayout layout = new GroupLayout(targetContainer);
        result = layout;
        layout.setAutoCreateContainerGaps(aInfo.isAutoCreateContainerGaps());
        layout.setAutoCreateGaps(aInfo.isAutoCreateGaps());
        layout.setHonorsVisibility(aInfo.isHonorsVisibility());

        linkedSizeGroups = new HashMap<>();
        layout.setHorizontalGroup(constructGroup(SwingConstants.HORIZONTAL, layout, null, aInfo.getHorizontalGroup()));
        for (List<JComponent> comps : linkedSizeGroups.values()) {
            layout.linkSize(SwingConstants.HORIZONTAL, comps.toArray(new Component[0]));
        }

        linkedSizeGroups = new HashMap<>();
        layout.setVerticalGroup(constructGroup(SwingConstants.VERTICAL, layout, null, aInfo.getVerticalGroup()));
        for (List<JComponent> comps : linkedSizeGroups.values()) {
            layout.linkSize(SwingConstants.VERTICAL, comps.toArray(new Component[0]));
        }
    }

    @Override
    public void visit(GroupLayoutGroupDesignInfo aInfo) {
    }

    @Override
    public void visit(AbsoluteLayoutDesignInfo aInfo) {
        result = null;
    }

    private Group constructGroup(int dimension, GroupLayout layout, Group rootGroup, GroupLayoutGroupDesignInfo designGroup) {
        if (designGroup.isComponent()) {
            assert rootGroup != null;
            JComponent component = components.get(designGroup.getComponent());
            processSizeLinks(dimension, designGroup, component);
            if (rootGroup instanceof GroupLayout.ParallelGroup) {
                if (designGroup.getAlignment() != null && !designGroup.getAlignment().isEmpty()) {
                    ((GroupLayout.ParallelGroup) rootGroup).addComponent(component, GroupLayout.Alignment.valueOf(designGroup.getAlignment()), designGroup.getMin(), designGroup.getPref(), designGroup.getMax());
                } else {
                    ((GroupLayout.ParallelGroup) rootGroup).addComponent(component, designGroup.getMin(), designGroup.getPref(), designGroup.getMax());
                }
            } else {
                ((GroupLayout.SequentialGroup) rootGroup).addComponent(component, designGroup.getMin(), designGroup.getPref(), designGroup.getMax());
            }
        } else if (designGroup.isGap()) {
            if (designGroup.isPreferredGap()) {
                assert rootGroup instanceof SequentialGroup;
                ((SequentialGroup) rootGroup).addPreferredGap(LayoutStyle.ComponentPlacement.valueOf(designGroup.getComponentPlacement()), designGroup.getPref(), designGroup.getMax());
            } else {
                int min = designGroup.getMin();
                int pref = designGroup.getPref();
                int max = designGroup.getMax();
                if (min < 0) {
                    min = PADDING_SEPARATE_VALUE;
                }
                if (pref < 0) {
                    pref = PADDING_SEPARATE_VALUE;
                }
                if (max < 0) {
                    max = PADDING_SEPARATE_VALUE;
                }
                pref = Math.max(min, pref);
                max = Math.max(pref, max);
                rootGroup.addGap(min, pref, max);
            }
        } else {
            assert designGroup.isGroup();
            Group group = null;
            if (designGroup.isParallel()) {
                group = layout.createParallelGroup(GroupLayout.Alignment.valueOf(designGroup.getChildrenAlignment()), designGroup.isResizeable());
                /*
                 if (designGroup.isBaselineGroup()) {
                 group = layout.createBaselineGroup(designGroup.isResizeable(), true);
                 } else {
                 group = layout.createParallelGroup(GroupLayout.Alignment.valueOf(designGroup.getChildrenAlignment()), designGroup.isResizeable());
                 }
                 */
            } else {
                group = layout.createSequentialGroup();
            }
            if (rootGroup != null) {
                if (rootGroup instanceof GroupLayout.ParallelGroup) {
                    GroupLayout.ParallelGroup pg = (GroupLayout.ParallelGroup) rootGroup;
                    if (designGroup.getAlignment() != null && !designGroup.getAlignment().isEmpty()) {
                        pg.addGroup(GroupLayout.Alignment.valueOf(designGroup.getAlignment()), group);
                    } else {
                        pg.addGroup(group);
                    }
                } else {
                    GroupLayout.SequentialGroup sg = (GroupLayout.SequentialGroup) rootGroup;
                    sg.addGroup(group);
                    //sg.addGroup(/*useAsBaseline, */group);
                }
                for (GroupLayoutGroupDesignInfo subgroup : designGroup.getChildren()) {
                    constructGroup(dimension, layout, group, subgroup);
                }
            } else {
                for (GroupLayoutGroupDesignInfo subgroup : designGroup.getChildren()) {
                    constructGroup(dimension, layout, group, subgroup);
                }
                return group;
            }
        }
        return null;
    }

    @Override
    public void visit(MarginLayoutDesignInfo aInfo) {
        result = new MarginLayout();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelEditingValidator;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.gui.ResultingDialog;
import com.eas.client.model.gui.view.EntityViewDoubleClickListener;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ApplicationModelView;
import com.eas.client.model.gui.view.model.DbSchemeModelView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.gui.view.model.QueryModelView;
import com.eas.client.model.query.QueryModel;
import com.eas.client.utils.scalableui.JScalableScrollPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 *
 * @author mg
 */
public class ModelElementSelector {

    /**
     * Any datasource (with datamodel parameters) is valid
     */
    public static final int DATASOURCE_SELECTION_SUBJECT = 1;
    /**
     * Fields from datasources and datamadel parameters are valid
     */
    public static final int FIELD_SELECTION_SUBJECT = 2;
    /**
     * Only datamodel parameters element is valid
     */
    public static final int PARAMETER_SELECTION_SUBJECT = 3;
    /**
     * Only datasources fields are valid (not datamodel parameters)
     */
    public static final int STRICT_DATASOURCE_FIELD_SELECTION_SUBJECT = 4;
    /**
     * Only datasource parameters are valid (not datamodel parameters)
     */
    public static final int STRICT_DATASOURCE_PARAMETER_SELECTION_SUBJECT = 5;

    public static <E extends Entity<?, ?, E>> ModelElementRef selectDatamodelElement(final Model<E, ?, ?, ?> aModel, final ModelElementRef aOldValue, int selectionSubject, ModelElementValidator aValidator, Component aParentComponent, String aTitle) {
        if (aModel != null) {
            final ModelElementRef selected = new ModelElementRef();
            ResultingDialog dlg = prepareDialog(aModel, aTitle, selected, selectionSubject, aValidator, aOldValue, null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                return selected;
            }
        }
        return null;
    }

    public static <E extends Entity<?, ?, E>> ResultingDialog prepareDialog(final Model<E, ?, ?, ?> aModel, String aTitle, ModelElementRef trackSubject, int selectionSubject, ModelElementValidator aValidator, final ModelElementRef aOldValue, ActionListener aOkActionListener) {
        ModelView<E, ?, ?> mView = null;
        if (aModel instanceof ApplicationDbModel) {
            mView = (ModelView<E, ?, ?>) new ApplicationModelView((ApplicationDbModel) aModel, null, null, null);
        } else if (aModel instanceof QueryModel) {
            mView = (ModelView<E, ?, ?>) new QueryModelView((QueryModel) aModel, null, null);
        } else if (aModel instanceof DbSchemeModel) {
            mView = (ModelView<E, ?, ?>) new DbSchemeModelView((DbSchemeModel) aModel, null);
        }
        mView.setAutoscrolls(true);
        JScalableScrollPane scroll = new JScalableScrollPane();
        scroll.setViewportView(mView);
        scroll.setPreferredSize(new Dimension(550, 500));
        final ResultingDialog dlg = new ResultingDialog(aOkActionListener, null, aTitle, scroll);
        mView.addModelSelectionListener(new ModelElementRefSelectionValidator<E>(trackSubject, dlg.getOkAction(), selectionSubject, aValidator));
        dlg.setSize(650, 600);
        dlg.getOkAction().setEnabled(false);
        mView.addEntityViewDoubleClickListener(new EntityViewDoubleClickListener<E>() {
            @Override
            public void clicked(EntityView<E> eView, boolean fieldsClicked, boolean paramsClicked) {
                if (dlg.getOkAction().isEnabled()) {
                    dlg.getOkAction().actionPerformed(null);
                }
            }
        });
        dlg.addWindowListener(new Positioner<>(aOldValue, aModel, mView));
        return dlg;
    }
    /*
     private static class ClonedEntityDesign {

     public Entity entity;
     public boolean iconified;
     public int x;
     public int y;
     public int width;
     public int height;
     }
     */

    private static class Positioner<E extends Entity<?, ?, E>> extends WindowAdapter {

        private final ModelElementRef oldValue;
        private final Model<E, ?, ?, ?> model;
        private final ModelView<E, ?, ?> mView;
        private final ModelEditingValidator<E> stopper = new ModelEditingStopper<>();

        public Positioner(ModelElementRef aOldValue, Model<E, ?, ?, ?> aModel, ModelView<E, ?, ?> aMView) {
            super();
            oldValue = aOldValue;
            model = aModel;
            mView = aMView;
        }

        @Override
        public void windowOpened(WindowEvent e) {
            model.addEditingValidator(stopper);
            ModelElementRef ldmRef = oldValue;
            if (ldmRef != null) {
                Map<Long, E> ents = model.getAllEntities();
                if (ents != null) {
                    E ent = ents.get(ldmRef.getEntityId());
                    if (ent != null) {
                        EntityView<E> eView = mView.getEntityView(ent);
                        if (eView != null) {
                            mView.makeVisible(eView, true);
                            if (ldmRef.getField() != null) {
                                if (ldmRef.isField()) {
                                    eView.setSelectedField(ldmRef.getField());
                                } else {
                                    eView.setSelectedParameter((Parameter) ldmRef.getField());
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {
            model.removeEditingValidator(stopper);
            mView.setModel(null);
        }
    }

    private static class ModelEditingStopper<E extends Entity<?, ?, E>> implements ModelEditingValidator<E> {

        public ModelEditingStopper() {
        }

        @Override
        public boolean validateRelationAdding(Relation<E> aRelation) {
            return false;
        }

        @Override
        public boolean validateRelationRemoving(Relation<E> aRelation) {
            return false;
        }

        @Override
        public boolean validateEntityAdding(E aEntity) {
            return false;
        }

        @Override
        public boolean validateEntityRemoving(E aEntity) {
            return false;
        }
    }
}

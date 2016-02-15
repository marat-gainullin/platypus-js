/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.ModelVisitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public abstract class Model2XmlDom<E extends Entity<M, ?, E>, M extends Model<E, ?>> implements ModelVisitor<E, M> {

    protected final static String YES_STRING = "yes";
    protected final static String NO_STRING = "no";
    public final static String DATAMODEL_TAG_NAME = "datamodel";
    public final static String PARAMETER_TAG_NAME = "parameter";
    public final static String PARAMETERS_TAG_NAME = "parameters";
    public final static String ENTITY_TAG_NAME = "entity";
    public final static String FIELDS_ENTITY_TAG_NAME = "fieldsEntity";
    public final static String PARAMETERS_ENTITY_TAG_NAME = "parametersEntity";
    public final static String RELATION_TAG_NAME = "relation";
    public final static String LIGHT_RELATION_TAG_NAME = "lightRelation";
    public final static String PRIMARY_KEYS_TAG_NAME = "primaryKeys";
    public final static String FOREIGN_KEYS_TAG_NAME = "foreignKeys";
    public final static String PRIMARY_KEY_TAG_NAME = "primaryKey";
    public final static String FOREIGN_KEY_TAG_NAME = "foreignKey";
    // setup documents framework
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // declaring documents framework
    protected DocumentBuilder builder;
    protected Document doc;
    protected Node currentNode;

    protected Model2XmlDom() {
        super();
        try {
            // setup documents framework
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            builder = null;
            Logger.getLogger(Model2XmlDom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Document model2XmlDom(M aModel) {
        if (aModel != null && builder != null) {
            doc = builder.newDocument();
            doc.setXmlStandalone(true);
            currentNode = doc;
            aModel.accept((ModelVisitor<E, Model<E, ?>>)this);
            return doc;
        }
        return null;
    }

    public void reset() {
        doc = null;
        currentNode = null;
    }

    public void writeModel(M aModel) {
        if (aModel != null) {
            Element datamodelNode = doc.createElement(DATAMODEL_TAG_NAME);
            currentNode.appendChild(datamodelNode);
            currentNode = datamodelNode;
            writeEntities(aModel);
            writeRelations(aModel);
        }
    }

    protected void writeRelations(M aModel) {
        if (aModel.getRelations() != null) {
            aModel.getRelations().stream().forEach((relation) -> {
                relation.accept(this);
            });
        }
    }

    protected void writeEntities(M aModel) throws DOMException {
        if (aModel.getEntities() != null) {
            aModel.getEntities().values().stream().forEach((entity) -> {
                entity.accept(this);
            });
        }
    }
    public static final String NAME_ATTR_NAME = "name";
    public static final String DATASOURCE_NAMEE_ATTR_NAME = "Name";
    public static final String DATASOURCE_TITLE_ATTR_NAME = "Title";
    public static final String DESCRIPTION_ATTR_NAME = "description";
    public static final String TYPE_ATTR_NAME = "type";
    public static final String SIZE_ATTR_NAME = "size";
    public static final String SCALE_ATTR_NAME = "scale";
    public static final String PRECISION_ATTR_NAME = "precision";
    public static final String SIGNED_ATTR_NAME = "signed";
    public static final String NULLABLE_ATTR_NAME = "nullable";
    public static final String MODE_ATTR_NAME = "parameterMode";
    public static final String IS_PK_ATTR_NAME = "isPk";
    public static final String FK_TAG_NAME = "fk";
    public static final String SELECTION_FORM_TAG_NAME = "selectionForm";
    public static final String DEFAULT_VALUE_TAG_NAME = "defaultValue";
    public static final String CLASS_HINT_TAG_NAME = "classHint";

    @Override
    public void visit(Field aField) {
        if (aField != null) {
            Element node = doc.createElement(PARAMETER_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(NAME_ATTR_NAME, aField.getName());
            node.setAttribute(DESCRIPTION_ATTR_NAME, aField.getDescription());
            node.setAttribute(TYPE_ATTR_NAME, aField.getType());
            node.setAttribute(NULLABLE_ATTR_NAME, String.valueOf(aField.isNullable()));
            if (aField instanceof Parameter) {
                node.setAttribute(SELECTION_FORM_TAG_NAME, String.valueOf(((Parameter) aField).getSelectionForm()));

                Object ov = ((Parameter) aField).getDefaultValue();
                if (ov != null) {
                    Element dvNode = doc.createElement(DEFAULT_VALUE_TAG_NAME);
                    node.appendChild(dvNode);
                    dvNode.setAttribute(CLASS_HINT_TAG_NAME, ov.getClass().getSimpleName());
                    dvNode.setNodeValue(ov.toString());
                }
                node.setAttribute(MODE_ATTR_NAME, String.valueOf(((Parameter) aField).getMode()));
            }
            if (aField.isPk()) {
                node.setAttribute(IS_PK_ATTR_NAME, String.valueOf(aField.isPk()));
            }
            ForeignKeySpec lfk = aField.getFk();
            if (lfk != null) {
                Node lcurrentNode = currentNode;
                try {
                    currentNode = node;
                    visit(lfk.getReferee());
                } finally {
                    currentNode = lcurrentNode;
                }
            }
        }
    }
    public static final String ENTITY_ID_ATTR_NAME = "entityId";
    public static final String QUERY_ID_ATTR_NAME = "queryId";
    public static final String TABLE_DB_ID_ATTR_NAME = "tableDbId";
    public static final String TABLE_SCHEMA_NAME_ATTR_NAME = "tableSchemaName";
    public static final String TABLE_NAME_ATTR_NAME = "tableName";
    public static final String ENTITY_TABLE_ALIAS = "tableAlias";
    public static final String ENTITY_LOCATION_X = "entityLocationX";
    public static final String ENTITY_LOCATION_Y = "entityLocationY";
    public static final String ENTITY_SIZE_WIDTH = "entityWidth";
    public static final String ENTITY_SIZE_HEIGHT = "entityHeight";
    public static final String ENTITY_ICONIFIED = "entityIconified";

    protected void writeEntityDesignAttributes(Element node, E entity) {
        node.setAttribute(ENTITY_LOCATION_X, String.valueOf(entity.getX()));
        node.setAttribute(ENTITY_LOCATION_Y, String.valueOf(entity.getY()));
        node.setAttribute(ENTITY_SIZE_WIDTH, String.valueOf(entity.getWidth()));
        node.setAttribute(ENTITY_SIZE_HEIGHT, String.valueOf(entity.getHeight()));
        node.setAttribute(ENTITY_ICONIFIED, String.valueOf(entity.isIconified()));
    }
    public static final String LEFT_ENTITY_ID_ATTR_NAME = "leftEntityId";
    public static final String LEFT_ENTITY_FIELD_ATTR_NAME = "leftEntityFieldName";
    public static final String LEFT_ENTITY_PARAMETER_ATTR_NAME = "leftEntityParameterName";
    public static final String RIGHT_ENTITY_ID_ATTR_NAME = "rightEntityId";
    public static final String RIGHT_ENTITY_FIELD_ATTR_NAME = "rightEntityFieldName";
    public static final String RIGHT_ENTITY_PARAMETER_ATTR_NAME = "rightEntityParameterName";
    public static final String POLYLINE_ATTR_NAME = "polyline";

    @Override
    public void visit(Relation<E> aRelation) {
        writeRelation(aRelation, doc.createElement(RELATION_TAG_NAME));
    }

    protected void writeRelation(Relation<E> relation, Element node) {
        if (relation != null && relation.getLeftField() != null && relation.getRightField() != null) {
            currentNode.appendChild(node);
            assert relation.getLeftEntity() != null : " A relation without left side detected";
            node.setAttribute(LEFT_ENTITY_ID_ATTR_NAME, String.valueOf(relation.getLeftEntity().getEntityId()));
            if (relation.isLeftField()) {
                node.setAttribute(LEFT_ENTITY_FIELD_ATTR_NAME, relation.getLeftField().getName());
            } else {
                node.setAttribute(LEFT_ENTITY_PARAMETER_ATTR_NAME, relation.getLeftParameter().getName());
            }

            assert relation.getRightEntity() != null : " A relation without right side detected";
            node.setAttribute(RIGHT_ENTITY_ID_ATTR_NAME, String.valueOf(relation.getRightEntity().getEntityId()));
            if (relation.isRightField()) {
                node.setAttribute(RIGHT_ENTITY_FIELD_ATTR_NAME, relation.getRightField().getName());
            } else {
                node.setAttribute(RIGHT_ENTITY_PARAMETER_ATTR_NAME, relation.getRightParameter().getName());
            }
            if (relation.getXs() != null && relation.getYs() != null && relation.getXs().length == relation.getYs().length) {
                node.setAttribute(POLYLINE_ATTR_NAME, polylineToString(relation));
            }
        }
    }

    private String polylineToString(Relation<E> relation) {
        if (relation.getXs() != null && relation.getYs() != null && relation.getXs().length == relation.getYs().length) {
            StringBuilder sb = new StringBuilder();
            int[] xs = relation.getXs();
            int[] ys = relation.getYs();
            for (int i = 0; i < xs.length; i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(xs[i]).append(";").append(ys[i]);
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    private void visit(PrimaryKeySpec pk) {
        if (pk != null) {
            Element node = doc.createElement(PRIMARY_KEY_TAG_NAME);
            currentNode.appendChild(node);
            if (pk.getCName() != null) {
                node.setAttribute(CONSTRAINT_NAME_ATTR_NAME, pk.getCName());
            }
            if (pk.getSchema() != null) {
                node.setAttribute(CONSTRAINT_SCHEMA_ATTR_NAME, pk.getSchema());
            }
            node.setAttribute(CONSTRAINT_TABLE_ATTR_NAME, pk.getTable());
            node.setAttribute(CONSTRAINT_FIELD_ATTR_NAME, pk.getField());
        }
    }
    //public static final String FIELD_NAME_ATTR_NAME = "updateRule";
    public static final String CONSTRAINT_NAME_ATTR_NAME = "name";
    public static final String CONSTRAINT_DBID_ATTR_NAME = "DbId";
    public static final String CONSTRAINT_SCHEMA_ATTR_NAME = "schema";
    public static final String CONSTRAINT_TABLE_ATTR_NAME = "table";
    public static final String CONSTRAINT_FIELD_ATTR_NAME = "field";
    /*
     *
     * WARNING! This is very useful code. It is not used because foreign key
     * information is serialized and deserialized within relations
     * serialization/deserialization.
     *
     *
     * private void visit(ForeignKeySpec fk) { if (fk != null) { Element node =
     * doc.createElement(FOREIGN_KEY_TAG_NAME);
     *
     * node.setAttribute(CONSTRAINT_NAME_ATTR_NAME, fk.getCName());
     * node.setAttribute(CONSTRAINT_SCHEMA_ATTR_NAME, fk.getSchema());
     * node.setAttribute(CONSTRAINT_TABLE_ATTR_NAME, fk.getTable());
     * node.setAttribute(CONSTRAINT_FIELD_ATTR_NAME, fk.getField());
     *
     * node.setAttribute(DEFERRABLE_ATTR_NAME,
     * String.valueOf(fk.getFkDeferrable()));
     * node.setAttribute(DELETE_ATTR_NAME,
     * String.valueOf(fk.getFkDeleteRule()));
     * node.setAttribute(UPDATE_ATTR_NAME,
     * String.valueOf(fk.getFkUpdateRule()));
     *
     * currentNode.appendChild(node); PrimaryKeySpec pk = fk.getReferee(); if
     * (pk != null) { Node lcurrentNode = currentNode; try { currentNode = node;
     * visit(pk); } finally { currentNode = lcurrentNode; } } } }
     */
    public static final String DATAMODEL_DB_ID = "datamodelDbId";
    public static final String DATAMODEL_DB_SCHEMA_NAME = "datamodelSchemaName";
    public static final String DATAMODEL_DATASOURCE = "datasource";
    public static final String DATAMODEL_DATASOURCE_SCHEMA_NAME = "schema";
}

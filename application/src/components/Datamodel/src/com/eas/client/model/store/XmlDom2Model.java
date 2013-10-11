/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.bearsoft.rowset.metadata.*;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.script.StoredFunction;
import com.eas.xml.dom.XmlDomUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Time;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 */
public abstract class XmlDom2Model<E extends Entity<?, ?, E>> implements ModelVisitor<E> {

    public static final int DEFAULT_ENTITY_HEIGHT = 200;
    public static final int DEFAULT_ENTITY_WIDTH = 150;
    protected Document doc;
    protected Element currentNode;
    protected Model<E, ?, ?, ?> currentModel;
    protected Collection<Runnable> relationsResolvers = new ArrayList<>();

    protected XmlDom2Model() {
        super();
    }

    protected Element getElementByTagName(Document aDoc, String name) {
        List<Element> nl = XmlDomUtils.elementsByTagName(aDoc, name);
        if (nl != null && nl.size() == 1) {
            return nl.get(0);
        }
        return null;
    }

    protected Element getElementByTagName(Element aElement, String name) {
        List<Element> nl = XmlDomUtils.elementsByTagName(aElement, name);
        if (nl != null && nl.size() == 1) {
            return nl.get(0);
        }
        return null;
    }

    public void readModel(final Model<E, ?, ?, ?> aModel) {
        Element el = getElementByTagName(doc, Model2XmlDom.DATAMODEL_TAG_NAME);
        if (el != null && aModel != null) {
            currentModel = aModel;
            try {
                currentNode = el;
                Element paramsEl = getElementByTagName(currentNode, Model2XmlDom.PARAMETERS_TAG_NAME);
                if (paramsEl != null) {
                    Parameters parameters = aModel.getParameters();
                    List<Element> pnl = XmlDomUtils.elementsByTagName(paramsEl, Model2XmlDom.PARAMETER_TAG_NAME);
                    if (pnl != null && parameters != null) {
                        Element lcurrentNode = currentNode;
                        try {
                            Set<String> names = new HashSet<>();
                            for (int i = 0; i < pnl.size(); i++) {
                                currentNode = pnl.get(i);
                                Parameter param = new Parameter();
                                visit(param);
                                String paramName = param.getName();
                                if (paramName != null && !paramName.isEmpty() && !names.contains(paramName)) {
                                    names.add(paramName);
                                    parameters.add(param);
                                }
                            }
                        } finally {
                            currentNode = lcurrentNode;
                        }
                    }
                }
                Element paramsEntityEl = getElementByTagName(currentNode, Model2XmlDom.PARAMETERS_ENTITY_TAG_NAME);
                if (paramsEntityEl != null) {
                    E pe = aModel.getParametersEntity();
                    if (pe != null) {
                        Element lcurrentNode = currentNode;
                        try {
                            currentNode = paramsEntityEl;
                            pe.accept(this);
                        } finally {
                            currentNode = lcurrentNode;
                        }
                    }
                }
                List<Element> dsnl = XmlDomUtils.elementsByTagName(currentNode, Model2XmlDom.ENTITY_TAG_NAME);
                List<Element> fnl = XmlDomUtils.elementsByTagName(currentNode, Model2XmlDom.FIELDS_ENTITY_TAG_NAME);
                List<Element> nl = new ArrayList<>();
                if (dsnl != null) {
                    nl.addAll(dsnl);
                }
                if (fnl != null) {
                    nl.addAll(fnl);
                }
                if (nl != null && !nl.isEmpty()) {
                    Element lcurrentNode = currentNode;
                    try {
                        for (int i = 0; i < nl.size(); i++) {
                            currentNode = nl.get(i);
                            E entity = aModel.newGenericEntity();
                            entity.accept(this);
                        }
                    } finally {
                        currentNode = lcurrentNode;
                    }
                }
                readRelations();
                final Runnable[] resolvers = relationsResolvers.toArray(new Runnable[]{});
                Runnable relationsResolver = new Runnable() {
                    @Override
                    public void run() {
                        for (Runnable resolver : resolvers) {
                            resolver.run();
                        }
                        // Let's check relations in our model for integrity
                        aModel.checkRelationsIntegrity();
                    }
                };
                if (currentModel.getClient() != null) {
                    relationsResolver.run();
                } else {
                    currentModel.setResolver(relationsResolver);
                }
            } finally {
                relationsResolvers.clear();
                currentModel = null;
            }
        }
    }

    public void readEntity(E entity) {
        if (entity != null) {
            entity.setEntityId(readLongAttribute(Model2XmlDom.ENTITY_ID_ATTR_NAME, null));
            if (currentNode.hasAttribute(Model2XmlDom.QUERY_ID_ATTR_NAME)) {
                String sQueryId = currentNode.getAttribute(Model2XmlDom.QUERY_ID_ATTR_NAME);
                if (!sQueryId.equals("null")) {
                    entity.setQueryId(sQueryId);
                }
            }
            if (currentNode.hasAttribute(Model2XmlDom.TABLE_DB_ID_ATTR_NAME)) {
                String aTableDbId = currentNode.getAttribute(Model2XmlDom.TABLE_DB_ID_ATTR_NAME);
                if (!aTableDbId.equals("null")) {
                    entity.setTableDbId(aTableDbId);
                }
            }
            entity.setTableSchemaName(currentNode.getAttribute(Model2XmlDom.TABLE_SCHEMA_NAME_ATTR_NAME));
            entity.setTableName(currentNode.getAttribute(Model2XmlDom.TABLE_NAME_ATTR_NAME));
            readEntityDesignAttributes(entity);
            readOldUserData(entity);
            Model<E, ?, ?, ?> dm = entity.getModel();
            if (dm != null) {
                dm.addEntity(entity);
            }
        }
    }

    protected void readEntityDesignAttributes(E entity) {
        entity.setX(readIntegerAttribute(Model2XmlDom.ENTITY_LOCATION_X, 0));
        entity.setY(readIntegerAttribute(Model2XmlDom.ENTITY_LOCATION_Y, 0));
        entity.setWidth(readIntegerAttribute(Model2XmlDom.ENTITY_SIZE_WIDTH, 0));
        entity.setHeight(readIntegerAttribute(Model2XmlDom.ENTITY_SIZE_HEIGHT, 0));
        entity.setIconified(readBooleanAttribute(Model2XmlDom.ENTITY_ICONIFIED, false));
    }

    @Override
    public void visit(final Relation<E> relation) {
        if (relation != null) {
            final Long leftEntityId = readLongAttribute(Model2XmlDom.LEFT_ENTITY_ID_ATTR_NAME, null);
            final String leftFieldName = currentNode.getAttribute(Model2XmlDom.LEFT_ENTITY_FIELD_ATTR_NAME);
            final String leftParameterName = currentNode.getAttribute(Model2XmlDom.LEFT_ENTITY_PARAMETER_ATTR_NAME);
            final Long rightEntityId = readLongAttribute(Model2XmlDom.RIGHT_ENTITY_ID_ATTR_NAME, null);
            final String rightFieldName = currentNode.getAttribute(Model2XmlDom.RIGHT_ENTITY_FIELD_ATTR_NAME);
            final String rightParameterName = currentNode.getAttribute(Model2XmlDom.RIGHT_ENTITY_PARAMETER_ATTR_NAME);
            if (currentNode.hasAttribute(Model2XmlDom.POLYLINE_ATTR_NAME)) {
                final String polyline = currentNode.getAttribute(Model2XmlDom.POLYLINE_ATTR_NAME);
                if (polyline != null && !polyline.isEmpty()) {
                    readPolyline(polyline, relation);
                }
            }
            final Model<E, ?, ?, ?> model = currentModel;
            relationsResolvers.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        E lEntity = model.getEntityById(leftEntityId);
                        if (Model.PARAMETERS_ENTITY_ID == leftEntityId) {
                            lEntity = model.getParametersEntity();
                            if (leftParameterName != null && !leftParameterName.isEmpty()) {
                                Fields fields = lEntity.getFields();
                                if (fields != null) {
                                    relation.setLeftField(fields.get(leftParameterName));
                                }
                            } else if (leftFieldName != null && !leftFieldName.isEmpty()) {
                                Fields fields = lEntity.getFields();
                                if (fields != null) {
                                    relation.setLeftField(fields.get(leftFieldName));
                                }
                            }
                        } else if (lEntity != null) {
                            if (leftParameterName != null && !leftParameterName.isEmpty()) {
                                Query query = lEntity.getQuery();
                                if (query != null) {
                                    relation.setLeftField(query.getParameters().get(leftParameterName));
                                }
                            } else if (leftFieldName != null && !leftFieldName.isEmpty()) {
                                Fields fields = lEntity.getFields();
                                if (fields != null) {
                                    relation.setLeftField(fields.get(leftFieldName));
                                }
                            }
                        }
                        if (lEntity != null) {
                            relation.setLeftEntity(lEntity);
                            lEntity.addOutRelation(relation);
                        }

                        E rEntity = model.getEntityById(rightEntityId);
                        if (Model.PARAMETERS_ENTITY_ID == rightEntityId) {
                            rEntity = model.getParametersEntity();
                            if (rightParameterName != null && !rightParameterName.isEmpty()) {
                                Fields fields = rEntity.getFields();
                                if (fields != null) {
                                    relation.setRightField(fields.get(rightParameterName));
                                }
                            } else if (rightFieldName != null && !rightFieldName.isEmpty()) {
                                Fields fields = rEntity.getFields();
                                if (fields != null) {
                                    relation.setRightField(fields.get(rightFieldName));
                                }
                            }
                        } else if (rEntity != null) {
                            if (rightParameterName != null && !rightParameterName.isEmpty()) {
                                Query query = rEntity.getQuery();
                                if (query != null) {
                                    relation.setRightField(query.getParameters().get(rightParameterName));
                                }
                            } else if (rightFieldName != null && !rightFieldName.isEmpty()) {
                                Fields fields = rEntity.getFields();
                                if (fields != null) {
                                    relation.setRightField(fields.get(rightFieldName));
                                }
                            }
                        }
                        if (rEntity != null) {
                            relation.setRightEntity(rEntity);
                            rEntity.addInRelation(relation);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(XmlDom2Model.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
            });
        }
    }

    private void readPolyline(String aPolyline, Relation aRelation) {
        String[] points = aPolyline.split(" ");
        if (points != null && points.length > 0) {
            int[] xs = new int[points.length];
            int[] ys = new int[points.length];
            for (int i = 0; i < points.length; i++) {
                String[] xy = points[i].split(";");
                xs[i] = Integer.valueOf(xy[0]);
                ys[i] = Integer.valueOf(xy[1]);
            }
            aRelation.setXYs(xs, ys);
        }
    }

    @Override
    public void visit(Field aField) {
        try {
            aField.setName(currentNode.getAttribute(Model2XmlDom.NAME_ATTR_NAME));
            aField.setDescription(currentNode.getAttribute(Model2XmlDom.DESCRIPTION_ATTR_NAME));
            aField.getTypeInfo().setSqlType(readIntegerAttribute(Model2XmlDom.TYPE_ATTR_NAME, Types.LONGVARCHAR));
            aField.getTypeInfo().setSqlTypeName(currentNode.getAttribute(Model2XmlDom.TYPE_NAME_ATTR_NAME));
            aField.setSize(readIntegerAttribute(Model2XmlDom.SIZE_ATTR_NAME, 100));
            aField.setScale(readIntegerAttribute(Model2XmlDom.SCALE_ATTR_NAME, 0));
            aField.setPrecision(readIntegerAttribute(Model2XmlDom.PRECISION_ATTR_NAME, 0));
            aField.setSigned(readBooleanAttribute(Model2XmlDom.SIGNED_ATTR_NAME, true));
            aField.setNullable(readBooleanAttribute(Model2XmlDom.NULLABLE_ATTR_NAME, true));
            aField.setPk(readBooleanAttribute(Model2XmlDom.IS_PK_ATTR_NAME, false));

            if (aField instanceof Parameter) {
                ((Parameter) aField).setMode(readIntegerAttribute(Model2XmlDom.MODE_ATTR_NAME, ParameterMetaData.parameterModeUnknown));
                ((Parameter) aField).setSelectionForm(readLongAttribute(Model2XmlDom.SELECTION_FORM_TAG_NAME, null));

                List<Element> dvEls = XmlDomUtils.elementsByTagName(currentNode, Model2XmlDom.DEFAULT_VALUE_TAG_NAME);
                if (dvEls != null && dvEls.size() == 1) {
                    Element dvTag = dvEls.get(0);
                    String simpleClassName = dvTag.getAttribute(Model2XmlDom.CLASS_HINT_TAG_NAME);
                    if (simpleClassName != null && !simpleClassName.isEmpty() && !simpleClassName.toLowerCase().equals("null")) {
                        String val = dvTag.getNodeValue();
                        if (val != null && !val.isEmpty() && !val.toLowerCase().equals("null")) {
                            Object dvO = readTypedValueFromString(simpleClassName.toLowerCase(), val);
                            ((Parameter) aField).setDefaultValue(dvO);
                        }
                    }
                }
            }
            Element lcurrentNode = currentNode;
            try {
                currentNode = getElementByTagName(currentNode, Model2XmlDom.PRIMARY_KEY_TAG_NAME);
                if (currentNode != null) {
                    PrimaryKeySpec pk = new PrimaryKeySpec();
                    visit(pk);
                    ForeignKeySpec fk = new ForeignKeySpec("", "", "", "", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, pk.getSchema(), pk.getTable(), pk.getField(), pk.getCName());
                    aField.setFk(fk);
                }
            } finally {
                currentNode = lcurrentNode;
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(XmlDom2Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void visit(PrimaryKeySpec pk) {
        if (pk != null) {
            pk.setCName(currentNode.getAttribute(Model2XmlDom.CONSTRAINT_NAME_ATTR_NAME));
            pk.setSchema(currentNode.getAttribute(Model2XmlDom.CONSTRAINT_SCHEMA_ATTR_NAME));
            pk.setTable(currentNode.getAttribute(Model2XmlDom.CONSTRAINT_TABLE_ATTR_NAME));
            pk.setField(currentNode.getAttribute(Model2XmlDom.CONSTRAINT_FIELD_ATTR_NAME));
        }
    }

    public void visit(Map<String, Object> unknownTags) {
        if (unknownTags != null && currentNode != null) {
            NamedNodeMap nm = currentNode.getAttributes();
            if (nm != null) {
                for (int i = 0; i < nm.getLength(); i++) {
                    Node node = nm.item(i);
                    String name = node.getNodeName();
                    if (name != null && !name.isEmpty()) {
                        String[] splitted = name.split("\\.");
                        if (splitted != null && splitted.length == 2) {
                            unknownTags.put(splitted[0], readTypedAttribute(splitted[1].toLowerCase(), name, node));
                        }
                    }
                }
            }
        }
    }

    protected Object readTypedValueFromString(String typeName, String aValue) {
        Object resO = null;
        if (typeName != null && !typeName.isEmpty() && aValue != null && !aValue.isEmpty()) {
            aValue = aValue.trim();
            if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                try {
                    if (typeName.equalsIgnoreCase(Long.class.getSimpleName())) {
                        resO = Long.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Integer.class.getSimpleName())) {
                        resO = Integer.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Boolean.class.getSimpleName())) {
                        resO = Boolean.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Short.class.getSimpleName())) {
                        resO = Short.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Double.class.getSimpleName())) {
                        resO = Double.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Float.class.getSimpleName())) {
                        resO = Float.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(Byte.class.getSimpleName())) {
                        resO = Byte.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(BigDecimal.class.getSimpleName())) {
                        resO = BigDecimal.valueOf(Double.valueOf(aValue));
                    } else if (typeName.equalsIgnoreCase(BigInteger.class.getSimpleName())) {
                        resO = BigInteger.valueOf(Long.valueOf(aValue));
                    } else if (typeName.equalsIgnoreCase(java.sql.Date.class.getSimpleName())) {
                        resO = Date.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(java.sql.Time.class.getSimpleName())) {
                        resO = Time.valueOf(aValue);
                    } else if (typeName.equalsIgnoreCase(String.class.getSimpleName())) {
                        resO = aValue;
                    }
                } catch (NumberFormatException ex) {
                    Logger.getLogger(XmlDom2Model.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(XmlDom2Model.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return resO;
    }

    protected Object readTypedAttribute(String typeName, String name, Node node) {
        Object resO = null;
        if (typeName.equals(Long.class.getSimpleName().toLowerCase())) {
            resO = readLongAttribute(name, null);
        } else if (typeName.equals(Integer.class.getSimpleName().toLowerCase())) {
            resO = readIntegerAttribute(name, null);
        } else if (typeName.equals(Boolean.class.getSimpleName().toLowerCase())) {
            resO = readBooleanAttribute(name, null);
        } else if (typeName.equals(Short.class.getSimpleName().toLowerCase())) {
            resO = readShortAttribute(name, null);
        } else if (typeName.equals(Double.class.getSimpleName().toLowerCase())) {
            resO = readDoubleAttribute(name, null);
        } else if (typeName.equals(Float.class.getSimpleName().toLowerCase())) {
            resO = readFloatAttribute(name, null);
        } else if (typeName.equals(Byte.class.getSimpleName().toLowerCase())) {
            resO = readByteAttribute(name, null);
        } else if (typeName.equals(BigDecimal.class.getSimpleName().toLowerCase())) {
            resO = readBigDecimalAttribute(name, null);
        } else if (typeName.equals(BigInteger.class.getSimpleName().toLowerCase())) {
            resO = readBigIntegerAttribute(name, null);
        } else if (typeName.equals(java.sql.Date.class.getSimpleName().toLowerCase())) {
            resO = readDateAttribute(name, null);
        } else if (typeName.equals(java.sql.Time.class.getSimpleName().toLowerCase())) {
            resO = readTimeAttribute(name, null);
        } else if (typeName.equals(String.class.getSimpleName().toLowerCase())) {
            resO = node.getNodeValue();
        }
        return resO;
    }

    protected Long readLongAttribute(String attributeName, Long defaulValue) {
        return XmlDomUtils.readLongAttribute(currentNode, attributeName, defaulValue);
    }

    protected Integer readIntegerAttribute(String attributeName, Integer defaulValue) {
        return XmlDomUtils.readIntegerAttribute(currentNode, attributeName, defaulValue);
    }

    protected ForeignKeySpec.ForeignKeyRule readForeignKeyRuleAttribute(String attributeName, ForeignKeySpec.ForeignKeyRule defaultValue) {
        try {
            return ForeignKeySpec.ForeignKeyRule.valueOf(currentNode.getAttribute(attributeName));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    protected Boolean readBooleanAttribute(String attributeName, Boolean defaultValue) {
        return XmlDomUtils.readBooleanAttribute(currentNode, attributeName, defaultValue);
    }

    protected Short readShortAttribute(String attributeName, Short defaulValue) {
        return XmlDomUtils.readShortAttribute(currentNode, attributeName, defaulValue);
    }

    protected Double readDoubleAttribute(String attributeName, Double defaultValue) {
        return XmlDomUtils.readDoubleAttribute(currentNode, attributeName, defaultValue);
    }

    protected Float readFloatAttribute(String attributeName, Float defaultValue) {
        return XmlDomUtils.readFloatAttribute(currentNode, attributeName, defaultValue);
    }

    protected Byte readByteAttribute(String attributeName, Byte defaultValue) {
        return XmlDomUtils.readByteAttribute(currentNode, attributeName, defaultValue);
    }

    protected BigDecimal readBigDecimalAttribute(String attributeName, BigDecimal defaultValue) {
        return XmlDomUtils.readBigDecimalAttribute(currentNode, attributeName, defaultValue);
    }

    protected BigInteger readBigIntegerAttribute(String attributeName, BigInteger defaultValue) {
        return XmlDomUtils.readBigIntegerAttribute(currentNode, attributeName, defaultValue);
    }

    protected Date readDateAttribute(String attributeName, Date defaultValue) {
        return XmlDomUtils.readDateAttribute(currentNode, attributeName, defaultValue);
    }

    protected Time readTimeAttribute(String attributeName, Time defaultValue) {
        return XmlDomUtils.readTimeAttribute(currentNode, attributeName, defaultValue);
    }
    protected static final String UNKNOWN_TAGS_TAG_NAME = "userData";

    protected void readOldUserData(E entity) {
        Element tagsElement = getElementByTagName(currentNode, UNKNOWN_TAGS_TAG_NAME);
        if (tagsElement != null) {
            Element lcurrentNode = currentNode;
            try {
                currentNode = tagsElement;
                Map<String, Object> unknownTags = new HashMap<>();
                visit(unknownTags);
                convertUnknownTags(entity, unknownTags);
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }

    /**
     * Converts old unknown tags into normal entity-related data.
     *
     * @param entity
     * @param unknownTags
     */
    private void convertUnknownTags(E entity, Map<String, Object> unknownTags) {
        if (unknownTags != null) {
            for (String unknownTag : unknownTags.keySet()) {
                Object value = unknownTags.get(unknownTag);
                if (value != null) {
                    int intValue = 0;
                    if (value instanceof Number) {
                        intValue = ((Number) value).intValue();
                    }
                    String stringValue = "";
                    if (value instanceof String) {
                        stringValue = (String) value;
                    }
                    if (Model.DATASOURCE_NAME_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        entity.setName(stringValue);
                    } else if (Model.DATASOURCE_TITLE_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        entity.setTitle(stringValue);
                    } else if (Model2XmlDom.ENTITY_LOCATION_X.equalsIgnoreCase(unknownTag)) {
                        entity.setX(intValue);
                    } else if (Model2XmlDom.ENTITY_LOCATION_Y.equalsIgnoreCase(unknownTag)) {
                        entity.setY(intValue);
                    } else if (Model2XmlDom.ENTITY_SIZE_WIDTH.equalsIgnoreCase(unknownTag)) {
                        entity.setWidth(intValue);
                    } else if (Model2XmlDom.ENTITY_SIZE_HEIGHT.equalsIgnoreCase(unknownTag)) {
                        entity.setHeight(intValue);
                    } else if (Model2XmlDom.ENTITY_ICONIFIED.equalsIgnoreCase(unknownTag)) {
                        if (value instanceof Boolean) {
                            Boolean boolValue = (Boolean) value;
                            entity.setIconified(boolValue);
                        }
                    } else if (Model2XmlDom.ENTITY_TABLE_ALIAS.equalsIgnoreCase(unknownTag)) {
                        if (entity instanceof QueryEntity) {
                            ((QueryEntity) entity).setAlias(stringValue);
                        }
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnAfterChange(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnAfterDelete(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnFiltered(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnAfterInsert(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnRequeried(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnAfterScroll(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnBeforeChange(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnBeforeDelete(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnBeforeInsert(stringValue != null ? new StoredFunction(stringValue) : null);
                    } else if (entity instanceof ApplicationEntity<?, ?, ?>
                            && Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME.equalsIgnoreCase(unknownTag)) {
                        final ApplicationEntity<?, ?, ?> appEntity = (ApplicationEntity<?, ?, ?>) entity;
                        appEntity.setOnBeforeScroll(stringValue != null ? new StoredFunction(stringValue) : null);
                    }
                }
            }
        }
        if (entity.getWidth() == 0) {
            entity.setWidth(DEFAULT_ENTITY_WIDTH);
        }
        if (entity.getHeight() == 0) {
            entity.setHeight(DEFAULT_ENTITY_HEIGHT);
        }
    }

    protected void readRelations() {
        List<Element> nl = XmlDomUtils.elementsByTagName(currentNode, Model2XmlDom.RELATION_TAG_NAME);
        if (nl != null) {
            Element lcurrentNode = currentNode;
            try {
                for (int i = 0; i < nl.size(); i++) {
                    currentNode = nl.get(i);
                    Relation<E> relation = new Relation<>();
                    relation.accept(this);
                }
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.queries.Query;
import com.eas.script.Scripts;
import com.eas.xml.dom.XmlDomUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Time;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public abstract class XmlDom2Model<E extends Entity<M, ?, E>, M extends Model<E, ?>> implements ModelVisitor<E, M> {

    public static final int DEFAULT_ENTITY_HEIGHT = 200;
    public static final int DEFAULT_ENTITY_WIDTH = 150;
    protected Document doc;
    protected Element modelElement;
    protected Element currentNode;
    protected M currentModel;
    protected Collection<Runnable> relationsResolvers = new ArrayList<>();

    protected XmlDom2Model() {
        super();
    }

    public Runnable readModel(final M aModel) {
        Element elModel = doc != null ? doc.getDocumentElement() : modelElement;
        if (elModel != null && aModel != null) {
            currentModel = aModel;
            try {
                currentNode = elModel;
                readEntities(aModel);
                readRelations();
                final Runnable[] resolvers = relationsResolvers.toArray(new Runnable[]{});
                Runnable relationsResolver = () -> {
                    for (Runnable resolver : resolvers) {
                        resolver.run();
                    }
                    // Let's check relations in our model for integrity
                    aModel.checkRelationsIntegrity();
                };
                return relationsResolver;
            } finally {
                relationsResolvers.clear();
                currentModel = null;
            }
        }
        return null;
    }

    protected void readEntities(final M aModel) {
        List<Element> dsnl = XmlDomUtils.elementsByTagName(currentNode, "e", Model2XmlDom.ENTITY_TAG_NAME);
        List<Element> fnl = XmlDomUtils.elementsByTagName(currentNode, "fe", Model2XmlDom.FIELDS_ENTITY_TAG_NAME);
        List<Element> nl = new ArrayList<>();
        if (dsnl != null) {
            nl.addAll(dsnl);
        }
        if (fnl != null) {
            nl.addAll(fnl);
        }
        if (!nl.isEmpty()) {
            Element lcurrentNode = currentNode;
            try {
                nl.stream().forEach((Element nl1) -> {
                    currentNode = nl1;
                    E entity = aModel.newGenericEntity();
                    entity.accept(this);
                });
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }

    public void readEntity(E entity) {
        if (entity != null) {
            entity.setEntityId(readLongAttribute(currentNode, "ei", Model2XmlDom.ENTITY_ID_ATTR_NAME, null));
            if (XmlDomUtils.hasAttribute(currentNode, "qi", Model2XmlDom.QUERY_ID_ATTR_NAME)) {
                String sQueryId = XmlDomUtils.getAttribute(currentNode, "qi", Model2XmlDom.QUERY_ID_ATTR_NAME);
                if (!sQueryId.equals("null")) {
                    entity.setQueryName(sQueryId);
                }
            }
            if (XmlDomUtils.hasAttribute(currentNode, "tbn", Model2XmlDom.TABLE_DB_ID_ATTR_NAME)) {
                String aTableDbId = XmlDomUtils.getAttribute(currentNode, "tbn", Model2XmlDom.TABLE_DB_ID_ATTR_NAME);
                if (!aTableDbId.equals("null")) {
                    entity.setTableDatasourceName(aTableDbId);
                }
            }
            entity.setTableSchemaName(XmlDomUtils.getAttribute(currentNode, "tsn", Model2XmlDom.TABLE_SCHEMA_NAME_ATTR_NAME));
            entity.setTableName(XmlDomUtils.getAttribute(currentNode, "tn", Model2XmlDom.TABLE_NAME_ATTR_NAME));
            readEntityDesignAttributes(entity);
            Model<E, ?> dm = entity.getModel();
            if (dm != null) {
                dm.addEntity(entity);
            }
        }
    }

    protected void readEntityDesignAttributes(E entity) {
        entity.setX(readIntegerAttribute(currentNode, Model2XmlDom.ENTITY_LOCATION_X, Model2XmlDom.ENTITY_LOCATION_X, 0));
        entity.setY(readIntegerAttribute(currentNode, Model2XmlDom.ENTITY_LOCATION_Y, Model2XmlDom.ENTITY_LOCATION_Y, 0));
        entity.setWidth(readIntegerAttribute(currentNode, Model2XmlDom.ENTITY_SIZE_WIDTH, Model2XmlDom.ENTITY_SIZE_WIDTH, 0));
        entity.setHeight(readIntegerAttribute(currentNode, Model2XmlDom.ENTITY_SIZE_HEIGHT, Model2XmlDom.ENTITY_SIZE_HEIGHT, 0));
        entity.setIconified(readBooleanAttribute(currentNode, Model2XmlDom.ENTITY_ICONIFIED, Model2XmlDom.ENTITY_ICONIFIED, false));
    }

    @Override
    public void visit(final Relation<E> relation) {
        if (relation != null) {
            final Long leftEntityId = readLongAttribute(currentNode, "lei", Model2XmlDom.LEFT_ENTITY_ID_ATTR_NAME, null);
            final String leftFieldName = XmlDomUtils.getAttribute(currentNode, "lef", Model2XmlDom.LEFT_ENTITY_FIELD_ATTR_NAME);
            final String leftParameterName = XmlDomUtils.getAttribute(currentNode, "lep", Model2XmlDom.LEFT_ENTITY_PARAMETER_ATTR_NAME);
            final Long rightEntityId = readLongAttribute(currentNode, "rei", Model2XmlDom.RIGHT_ENTITY_ID_ATTR_NAME, null);
            final String rightFieldName = XmlDomUtils.getAttribute(currentNode, "ref", Model2XmlDom.RIGHT_ENTITY_FIELD_ATTR_NAME);
            final String rightParameterName = XmlDomUtils.getAttribute(currentNode, "rep", Model2XmlDom.RIGHT_ENTITY_PARAMETER_ATTR_NAME);
            if (currentNode.hasAttribute(Model2XmlDom.POLYLINE_ATTR_NAME)) {
                final String polyline = currentNode.getAttribute(Model2XmlDom.POLYLINE_ATTR_NAME);
                if (polyline != null && !polyline.isEmpty()) {
                    readPolyline(polyline, relation);
                }
            }
            M model = currentModel;
            relationsResolvers.add((Runnable) () -> {
                resolveRelation(model, leftEntityId, leftParameterName, relation, leftFieldName, rightEntityId, rightParameterName, rightFieldName);
            });
        }
    }

    protected void resolveRelation(final M aModel, final Long leftEntityId, final String leftParameterName, final Relation<E> relation, final String leftFieldName, final Long rightEntityId, final String rightParameterName, final String rightFieldName) {
        try {
            E lEntity = aModel.getEntityById(leftEntityId);
            if (lEntity != null) {
                Query query = lEntity.getQuery();
                if (leftParameterName != null && !leftParameterName.isEmpty()) {
                    if (query != null) {
                        relation.setLeftField(query.getParameters().get(leftParameterName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setLeftField(new Parameter(leftParameterName));
                    }
                } else if (leftFieldName != null && !leftFieldName.isEmpty()) {
                    Fields fields = lEntity.getFields();
                    if (fields != null && query.isMetadataAccessible()) {
                        relation.setLeftField(fields.get(leftFieldName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setLeftField(new Field(leftFieldName));
                    }
                }
                relation.setLeftEntity(lEntity);
                lEntity.addOutRelation(relation);
            }
            E rEntity = aModel.getEntityById(rightEntityId);
            if (rEntity != null) {
                Query query = rEntity.getQuery();
                if (rightParameterName != null && !rightParameterName.isEmpty()) {
                    if (query != null) {
                        relation.setRightField(query.getParameters().get(rightParameterName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setRightField(new Parameter(rightParameterName));
                    }
                } else if (rightFieldName != null && !rightFieldName.isEmpty()) {
                    Fields fields = rEntity.getFields();
                    if (fields != null && query.isMetadataAccessible()) {
                        relation.setRightField(fields.get(rightFieldName));
                    } else if (!aModel.isRelationsAgressiveCheck()) {
                        relation.setRightField(new Field(rightFieldName));
                    }
                }
                relation.setRightEntity(rEntity);
                rEntity.addInRelation(relation);
            }
        } catch (Exception ex) {
            Logger.getLogger(XmlDom2Model.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    private void readPolyline(String aPolyline, Relation<E> aRelation) {
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

    private static final Set<String> legacyStringTypes = new HashSet<>(Arrays.asList(new String[]{"1", "12", "2005"}));
    private static final Set<String> legacyNumberTypes = new HashSet<>(Arrays.asList(new String[]{"2", "3", "-5", "4", "5", "7", "8"}));
    private static final Set<String> legacyDateTypes = new HashSet<>(Arrays.asList(new String[]{"91", "92", "93"}));
    private static final Set<String> legacyBooleanTypes = new HashSet<>(Arrays.asList(new String[]{"-7", "16"}));
    private static final Set<String> legacyNullTypes = new HashSet<>(Arrays.asList(new String[]{"2004", "1111"}));

    @Override
    public void visit(Field aField) {
        try {
            aField.setName(XmlDomUtils.getAttribute(currentNode, "n", Model2XmlDom.NAME_ATTR_NAME));
            aField.setDescription(XmlDomUtils.getAttribute(currentNode, "d", Model2XmlDom.DESCRIPTION_ATTR_NAME));
            String fieldType = XmlDomUtils.getAttribute(currentNode, "t", Model2XmlDom.TYPE_ATTR_NAME);
            if (legacyStringTypes.contains(fieldType)) {
                aField.setType(Scripts.STRING_TYPE_NAME);
            } else if (legacyNumberTypes.contains(fieldType)) {
                aField.setType(Scripts.NUMBER_TYPE_NAME);
            } else if (legacyDateTypes.contains(fieldType)) {
                aField.setType(Scripts.DATE_TYPE_NAME);
            } else if (legacyBooleanTypes.contains(fieldType)) {
                aField.setType(Scripts.BOOLEAN_TYPE_NAME);
            } else if (legacyNullTypes.contains(fieldType)) {
                aField.setType(null);
            } else {
                aField.setType(fieldType);// modern code :-)
            }
            aField.setNullable(readBooleanAttribute(currentNode, "nl", Model2XmlDom.NULLABLE_ATTR_NAME, true));
            aField.setPk(readBooleanAttribute(currentNode, "p", Model2XmlDom.IS_PK_ATTR_NAME, false));

            if (aField instanceof Parameter) {
                ((Parameter) aField).setMode(readIntegerAttribute(currentNode, "pm", Model2XmlDom.MODE_ATTR_NAME, ParameterMetaData.parameterModeUnknown));
                ((Parameter) aField).setSelectionForm(XmlDomUtils.getAttribute(currentNode, "sf", Model2XmlDom.SELECTION_FORM_TAG_NAME));

                List<Element> dvEls = XmlDomUtils.elementsByTagName(currentNode, "dv", Model2XmlDom.DEFAULT_VALUE_TAG_NAME);
                if (dvEls != null && dvEls.size() == 1) {
                    Element dvTag = dvEls.get(0);
                    String simpleClassName = XmlDomUtils.getAttribute(dvTag, "ch", Model2XmlDom.CLASS_HINT_TAG_NAME);
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
                currentNode = XmlDomUtils.getElementByTagName(currentNode, "pr", Model2XmlDom.PRIMARY_KEY_TAG_NAME);
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
            pk.setCName(XmlDomUtils.getAttribute(currentNode, "n", Model2XmlDom.CONSTRAINT_NAME_ATTR_NAME));
            pk.setSchema(XmlDomUtils.getAttribute(currentNode, "s", Model2XmlDom.CONSTRAINT_SCHEMA_ATTR_NAME));
            pk.setTable(XmlDomUtils.getAttribute(currentNode, "tl", Model2XmlDom.CONSTRAINT_TABLE_ATTR_NAME));
            pk.setField(XmlDomUtils.getAttribute(currentNode, "f", Model2XmlDom.CONSTRAINT_FIELD_ATTR_NAME));
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
                }
            }
        }
        return resO;
    }

    protected void readRelations() {
        List<Element> nl = XmlDomUtils.elementsByTagName(currentNode, "r", Model2XmlDom.RELATION_TAG_NAME);
        if (nl != null) {
            Element lcurrentNode = currentNode;
            try {
                nl.stream().forEach((Element nl1) -> {
                    currentNode = nl1;
                    Relation<E> relation = new Relation<>();
                    relation.accept(this);
                });
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }

    protected static ForeignKeySpec.ForeignKeyRule readForeignKeyRuleAttribute(Element aElement, String aShortName, String aLongName, ForeignKeySpec.ForeignKeyRule defaultValue) {
        try {
            return ForeignKeySpec.ForeignKeyRule.valueOf(XmlDomUtils.getAttribute(aElement, aShortName, aLongName));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    protected Long readLongAttribute(Element aElement, String aShortName, String aLongName, Long defaulValue) {
        if (aElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readLongAttribute(aElement, aShortName, defaulValue);
        } else {
            return XmlDomUtils.readLongAttribute(aElement, aLongName, defaulValue);
        }
    }

    protected static int readIntegerAttribute(Element aElement, String aShortName, String aLongName, int defaulValue) {
        if (aElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readIntegerAttribute(aElement, aShortName, defaulValue);
        } else {
            return XmlDomUtils.readIntegerAttribute(aElement, aLongName, defaulValue);
        }
    }

    protected static boolean readBooleanAttribute(Element aElement, String aShortName, String aLongName, boolean defaultValue) {
        if (aElement.hasAttribute(aShortName)) {
            return XmlDomUtils.readBooleanAttribute(aElement, aShortName, defaultValue);
        } else {
            return XmlDomUtils.readBooleanAttribute(aElement, aLongName, defaultValue);
        }
    }

    /*
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
     */
}

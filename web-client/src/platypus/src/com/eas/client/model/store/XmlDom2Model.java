/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.Utils;
import com.eas.client.application.AppClient;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelVisitor;
import com.eas.client.model.ReferenceRelation;
import com.eas.client.model.Relation;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author mg
 */
public class XmlDom2Model implements ModelVisitor {

	protected final static String YES_STRING = "yes";
	protected final static String NO_STRING = "no";

	public static final String DATAMODEL_TAG_NAME = "datamodel";
	public static final String DATASOURCE_NAME_TAG_NAME = "Name";
	public static final String DATASOURCE_TITLE_TAG_NAME = "Title";
	public static final String ENTITY_TAG_NAME = "entity";
	public static final String FIELDS_ENTITY_TAG_NAME = "fieldsEntity";
	public static final String PARAMETERS_ENTITY_TAG_NAME = "parametersEntity";
	public static final String RELATION_TAG_NAME = "relation";
	public static final String LIGHT_RELATION_TAG_NAME = "lightRelation";
	public static final String REFERENCE_RELATION_TAG_NAME = "referenceRelation";
	public static final String SCALAR_PROP_NAME_ATTR_NAME = "scalarPropertyName";
	public static final String COLLECTION_PROP_NAME_ATTR_NAME = "collectionPropertyName";
	public static final String PRIMARY_KEYS_TAG_NAME = "primaryKeys";
	public static final String FOREIGN_KEYS_TAG_NAME = "foreignKeys";
	public static final String PRIMARY_KEY_TAG_NAME = "primaryKey";
	public static final String FOREIGN_KEY_TAG_NAME = "foreignKey";
	public static final String NAME_ATTR_NAME = "name";
	public static final String DESCRIPTION_ATTR_NAME = "description";
	public static final String TYPE_ATTR_NAME = "type";
	public static final String TYPE_NAME_ATTR_NAME = "typeName";
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
	public static final String ENTITY_ID_ATTR_NAME = "entityId";
	public static final String QUERY_ID_ATTR_NAME = "queryId";
	public static final String ENTITY_TABLE_ALIAS = "tableAlias";
	public static final String ENTITY_LOCATION_X = "entityLocationX";
	public static final String ENTITY_LOCATION_Y = "entityLocationY";
	public static final String ENTITY_SIZE_WIDTH = "entityWidth";
	public static final String ENTITY_SIZE_HEIGHT = "entityHeight";
	public static final String ENTITY_ICONIFIED = "entityIconified";
	public static final String LEFT_ENTITY_ID_ATTR_NAME = "leftEntityId";
	public static final String LEFT_ENTITY_FIELD_ATTR_NAME = "leftEntityFieldName";
	public static final String LEFT_ENTITY_PARAMETER_ATTR_NAME = "leftEntityParameterName";
	public static final String RIGHT_ENTITY_ID_ATTR_NAME = "rightEntityId";
	public static final String RIGHT_ENTITY_FIELD_ATTR_NAME = "rightEntityFieldName";
	public static final String RIGHT_ENTITY_PARAMETER_ATTR_NAME = "rightEntityParameterName";
	public static final String CONSTRAINT_NAME_ATTR_NAME = "name";
	public static final String CONSTRAINT_FIELD_ATTR_NAME = "field";
	public static final String CONSTRAINT_SCHEMA_ATTR_NAME = "schema";
	public static final String CONSTRAINT_TABLE_ATTR_NAME = "table";
	protected Document doc;
	protected JavaScriptObject module;
	protected Element currentTag;
	protected Model model;
	protected Collection<Runnable> relationsResolvers = new ArrayList<Runnable>();

	public static Model transform(Document doc, JavaScriptObject aTarget) throws Exception {
		try {
			Model model = new Model(AppClient.getInstance());
			XmlDom2Model transformer = new XmlDom2Model(doc, aTarget);
			model.accept(transformer);
			return model;
		} catch (Exception ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}
	}

	protected XmlDom2Model(Document aDoc, JavaScriptObject aTarget) {
		super();
		doc = aDoc;
		module = aTarget;
	}

	protected void readModel(Model aModel) throws Exception {
		Element el = DATAMODEL_TAG_NAME.equals(doc.getDocumentElement().getNodeName()) ? doc.getDocumentElement() : Utils.getElementByTagName(doc.getDocumentElement(), DATAMODEL_TAG_NAME);
		if (el != null && aModel != null) {
			model = aModel;
			try {
				currentTag = el;
				NodeList nl = currentTag.getChildNodes();
				if (nl != null && nl.getLength() > 0) {
					Element lcurrentTag = currentTag;
					try {
						for (int i = 0; i < nl.getLength(); i++) {
							if (ENTITY_TAG_NAME.equals(nl.item(i).getNodeName())) {
								currentTag = (Element) nl.item(i);
								Entity entity = new Entity();
								entity.accept(this);
							} else if (RELATION_TAG_NAME.equals(nl.item(i).getNodeName())) {
								currentTag = (Element) nl.item(i);
								Relation relation = new Relation();
								relation.accept(this);
							} else if (REFERENCE_RELATION_TAG_NAME.equals(nl.item(i).getNodeName())) {
								currentTag = (Element) nl.item(i);
								Relation relation = new ReferenceRelation();
								relation.accept(this);
							}
						}
					} finally {
						currentTag = lcurrentTag;
					}
				}
				model.validateQueries();
				for (Runnable resolver : relationsResolvers) {
					resolver.run();
				}
				model.checkRelationsIntegrity();
			} finally {
				relationsResolvers = null;
				model = null;
			}
		}
	}

	protected void readEntity(Entity entity) {
		if (entity != null) {
			String entityId = readAttribute(ENTITY_ID_ATTR_NAME, null);
			if ("null".equals(entityId)) {
				entityId = null;
			}
			assert entityId != null : "Entity id must be provided";
			entity.setEntityId(entityId);
			String queryId = readAttribute(QUERY_ID_ATTR_NAME, null);
			if ("null".equals(queryId)) {
				queryId = null;
			}
			assert queryId != null : "Query id must be provided";
			entity.setQueryName(queryId);
			assert model != null;
			entity.setModel(model);
			model.addEntity(entity);
		}
	}

	@Override
	public void visit(Entity entity) {
		NamedNodeMap attrs = currentTag.getAttributes();
		Node a = attrs.getNamedItem(DATASOURCE_NAME_TAG_NAME);
		if (a != null) {
			entity.setName(a.getNodeValue());
		}
		a = attrs.getNamedItem(DATASOURCE_TITLE_TAG_NAME);
		if (a != null) {
			entity.setTitle(a.getNodeValue());
		}
		readEntity(entity);
	}

	@Override
	public void visit(final Relation relation) {
		if (relation != null && model != null) {
			NamedNodeMap attrs = currentTag.getAttributes();
			Node lefna = attrs.getNamedItem(LEFT_ENTITY_FIELD_ATTR_NAME);
			Node lepna = attrs.getNamedItem(LEFT_ENTITY_PARAMETER_ATTR_NAME);
			Node refna = attrs.getNamedItem(RIGHT_ENTITY_FIELD_ATTR_NAME);
			Node repna = attrs.getNamedItem(RIGHT_ENTITY_PARAMETER_ATTR_NAME);

			final String leftEntityId = readAttribute(LEFT_ENTITY_ID_ATTR_NAME, null);
			final String leftFieldName = lefna != null ? lefna.getNodeValue() : null;
			final String leftParameterName = lepna != null ? lepna.getNodeValue() : null;
			final String rightEntityId = readAttribute(RIGHT_ENTITY_ID_ATTR_NAME, null);
			final String rightFieldName = refna != null ? refna.getNodeValue() : null;
			final String rightParameterName = repna != null ? repna.getNodeValue() : null;

			model.addRelation(relation);

			Runnable resolver = new Runnable() {
				@Override
				public void run() {
					try {
						Entity lEntity = model.getEntityById(leftEntityId);
						if (lEntity != null) {
							if (leftParameterName != null && !leftParameterName.isEmpty()) {
								relation.setLeftField(lEntity.getQuery().getParameters().get(leftParameterName));
							} else if (leftFieldName != null && !leftFieldName.isEmpty()) {
								relation.setLeftField(lEntity.getFields().get(leftFieldName));
							}
						}
						relation.setLeftEntity(lEntity);
						lEntity.addOutRelation(relation);

						Entity rEntity = model.getEntityById(rightEntityId);
						if (rEntity != null) {
							if (rightParameterName != null && !rightParameterName.isEmpty()) {
								relation.setRightField(rEntity.getQuery().getParameters().get(rightParameterName));
							} else if (rightFieldName != null && !rightFieldName.isEmpty()) {
								relation.setRightField(rEntity.getFields().get(rightFieldName));
							}
						}
						relation.setRightEntity(rEntity);
						rEntity.addInRelation(relation);
					} catch (Exception ex) {
						Logger.getLogger(XmlDom2Model.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			};
			relationsResolvers.add(resolver);
		}
	}

	@Override
	public void visit(final ReferenceRelation relation) {
		visit((Relation) relation);
		NamedNodeMap attrs = currentTag.getAttributes();
		Node scalarna = attrs.getNamedItem(SCALAR_PROP_NAME_ATTR_NAME);
		Node collectionna = attrs.getNamedItem(COLLECTION_PROP_NAME_ATTR_NAME);

		String scalarPropertyName = scalarna != null ? scalarna.getNodeValue() : null;
		;
		String collectionPropertyName = collectionna != null ? collectionna.getNodeValue() : null;
		;
		relation.setScalarPropertyName(scalarPropertyName != null ? scalarPropertyName.trim() : null);
		relation.setCollectionPropertyName(collectionPropertyName != null ? collectionPropertyName.trim() : null);
	}

	@Override
	public void visit(Field aField) {
		try {
			NamedNodeMap attrs = currentTag.getAttributes();
			Node a = attrs.getNamedItem(NAME_ATTR_NAME);
			if (a != null) {
				aField.setName(a.getNodeValue());
			}
			a = attrs.getNamedItem(DESCRIPTION_ATTR_NAME);
			if (a != null) {
				aField.setDescription(a.getNodeValue());
			}
			aField.getTypeInfo().setType(readIntegerAttribute(TYPE_ATTR_NAME, Types.LONGVARCHAR));
			a = attrs.getNamedItem(TYPE_NAME_ATTR_NAME);
			if (a != null) {
				aField.getTypeInfo().setTypeName(a.getNodeValue());
			}
			aField.setSize(readIntegerAttribute(SIZE_ATTR_NAME, 100));
			aField.setScale(readIntegerAttribute(SCALE_ATTR_NAME, 0));
			aField.setPrecision(readIntegerAttribute(PRECISION_ATTR_NAME, 0));
			aField.setSigned(readBooleanAttribute(SIGNED_ATTR_NAME, true));
			aField.setNullable(readBooleanAttribute(NULLABLE_ATTR_NAME, true));
			aField.setPk(readBooleanAttribute(IS_PK_ATTR_NAME, false));

			if (aField instanceof Parameter) {
				((Parameter) aField).setMode(readIntegerAttribute(MODE_ATTR_NAME, 0/*
																					 * ParameterMetaData
																					 * .
																					 * parameterModeUnknown
																					 */));
				if (currentTag.getAttributes().getNamedItem(SELECTION_FORM_TAG_NAME) != null && !"null".equals(currentTag.getAttributes().getNamedItem(SELECTION_FORM_TAG_NAME).getNodeValue()))
					((Parameter) aField).setSelectionForm(readDoubleAttribute(SELECTION_FORM_TAG_NAME, null));

				Node dvTag = Utils.getElementByTagName(currentTag, DEFAULT_VALUE_TAG_NAME);
				if (dvTag != null) {
					Node classHintA = dvTag.getAttributes().getNamedItem(CLASS_HINT_TAG_NAME);
					if (classHintA != null) {
						String simpleClassName = classHintA.getNodeValue();
						if (simpleClassName != null && !simpleClassName.isEmpty() && !simpleClassName.toLowerCase().equals("null")) {
							String val = dvTag.getFirstChild().getNodeValue();
							if (val != null && !val.isEmpty() && !val.toLowerCase().equals("null")) {
								Object dvO = readTypedValueFromString(simpleClassName.toLowerCase(), val);
								((Parameter) aField).setDefaultValue(dvO);
							}
						}
					}
				}
			}
			Element lcurrentTag = currentTag;
			try {
				currentTag = Utils.getElementByTagName(currentTag, PRIMARY_KEY_TAG_NAME);
				if (currentTag != null) {
					PrimaryKeySpec pk = new PrimaryKeySpec();
					visit(pk);
					ForeignKeySpec fk = new ForeignKeySpec("", "", "", "", ForeignKeySpec.ForeignKeyRule.CASCADE, ForeignKeySpec.ForeignKeyRule.CASCADE, false, pk.getSchema(), pk.getTable(),
					        pk.getField(), pk.getCName());
					aField.setFk(fk);
				}
			} finally {
				currentTag = lcurrentTag;
			}
		} catch (NumberFormatException ex) {
			Logger.getLogger(XmlDom2Model.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void visit(PrimaryKeySpec pk) {
		if (pk != null) {
			NamedNodeMap attrs = currentTag.getAttributes();
			Node a = attrs.getNamedItem(CONSTRAINT_NAME_ATTR_NAME);
			if (a != null) {
				pk.setCName(a.getNodeValue());
			}
			a = attrs.getNamedItem(CONSTRAINT_SCHEMA_ATTR_NAME);
			if (a != null) {
				pk.setSchema(a.getNodeValue());
			}
			a = attrs.getNamedItem(CONSTRAINT_TABLE_ATTR_NAME);
			if (a != null) {
				pk.setTable(a.getNodeValue());
			}
			a = attrs.getNamedItem(CONSTRAINT_FIELD_ATTR_NAME);
			if (a != null) {
				pk.setField(a.getNodeValue());
			}
		}
	}

	protected Object readTypedValueFromString(String typeName, String aValue) {
		Object resO = null;
		if (typeName != null && !typeName.isEmpty() && aValue != null && !aValue.isEmpty()) {
			aValue = aValue.trim();
			if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
				try {
					if (typeName.equalsIgnoreCase("Long")) {
						resO = Long.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Integer")) {
						resO = Integer.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Boolean")) {
						resO = Boolean.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Short")) {
						resO = Short.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Double")) {
						resO = Double.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Float")) {
						resO = Float.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Byte")) {
						resO = Byte.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("BigDecimal")) {
						resO = BigDecimal.valueOf(Double.valueOf(aValue));
					} else if (typeName.equalsIgnoreCase("BigInteger")) {
						resO = BigInteger.valueOf(Long.valueOf(aValue));
					} else if (typeName.equalsIgnoreCase("Date")) {
						resO = Date.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("Time")) {
						resO = Time.valueOf(aValue);
					} else if (typeName.equalsIgnoreCase("String")) {
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

	protected Object readTypedAttribute(String aTypeName, String aAttrName, Node aAttrNode) {
		Object resO = null;
		if (aTypeName.equalsIgnoreCase("Long")) {
			resO = readDoubleAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Integer")) {
			resO = readIntegerAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Boolean")) {
			resO = readBooleanAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Short")) {
			resO = readShortAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Double")) {
			resO = readDoubleAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Float")) {
			resO = readFloatAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Byte")) {
			resO = readByteAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("BigDecimal")) {
			resO = readBigDecimalAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("BigInteger")) {
			resO = readBigIntegerAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Date")) {
			resO = readDateAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("Time")) {
			resO = readTimeAttribute(aAttrName, null);
		} else if (aTypeName.equalsIgnoreCase("String")) {
			resO = aAttrNode.getNodeValue();
		}
		return resO;
	}

	protected Integer readIntegerAttribute(String attributeName, Integer defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Integer.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected ForeignKeySpec.ForeignKeyRule readForeignKeyRuleAttribute(String attributeName, ForeignKeySpec.ForeignKeyRule defaultValue) {
		try {
			Node a = currentTag.getAttributes().getNamedItem(attributeName);
			return ForeignKeySpec.ForeignKeyRule.valueOf(a.getNodeValue());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	protected Boolean readBooleanAttribute(String attributeName, Boolean defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Boolean.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected Short readShortAttribute(String attributeName, Short defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Short.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected Double readDoubleAttribute(String attributeName, Double defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Double.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected Float readFloatAttribute(String attributeName, Float defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Float.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected Byte readByteAttribute(String attributeName, Byte defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return Byte.valueOf(a.getNodeValue());
		}
		return defaultValue;
	}

	protected String readAttribute(String attributeName, String defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return a.getNodeValue();
		}
		return defaultValue;
	}

	protected BigDecimal readBigDecimalAttribute(String attributeName, BigDecimal defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return new BigDecimal(a.getNodeValue());
		}
		return defaultValue;
	}

	protected BigInteger readBigIntegerAttribute(String attributeName, BigInteger defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return new BigInteger(a.getNodeValue());
		}
		return defaultValue;
	}

	protected Date readDateAttribute(String attributeName, Date defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return new Date(Long.valueOf(a.getNodeValue()));
		}
		return defaultValue;
	}

	protected Time readTimeAttribute(String attributeName, Time defaultValue) {
		Node a = currentTag.getAttributes().getNamedItem(attributeName);
		if (a != null) {
			return new Time(Long.valueOf(a.getNodeValue()));
		}
		return defaultValue;
	}

	@Override
	public void visit(Model aModel) {
		try {
			readModel(aModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.model.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.AppClient;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.core.Utils;
import com.eas.model.Entity;
import com.eas.model.Model;
import com.eas.model.ModelVisitor;
import com.eas.model.ReferenceRelation;
import com.eas.model.Relation;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author mg
 */
public class XmlDom2Model implements ModelVisitor {

	protected final static String YES_STRING = "yes";
	protected final static String NO_STRING = "no";

	// Tags
	public static final String DATAMODEL_TAG_NAME = "datamodel";
	public static final String DATASOURCE_NAME_ATTR_NAME = "Name";
	public static final String DATASOURCE_TITLE_ATTR_NAME = "Title";
	public static final String ENTITY_TAG_NAME = "entity";
	// public static final String FIELDS_ENTITY_TAG_NAME = "fieldsEntity";
	// public static final String PARAMETERS_ENTITY_TAG_NAME =
	// "parametersEntity";
	public static final String RELATION_TAG_NAME = "relation";
	// public static final String LIGHT_RELATION_TAG_NAME = "lightRelation";
	public static final String REFERENCE_RELATION_TAG_NAME = "referenceRelation";

	// Attributes
	public static final String SCALAR_PROP_NAME_ATTR_NAME = "scalarPropertyName";
	public static final String COLLECTION_PROP_NAME_ATTR_NAME = "collectionPropertyName";
	// public static final String PRIMARY_KEYS_TAG_NAME = "primaryKeys";
	// public static final String FOREIGN_KEYS_TAG_NAME = "foreignKeys";
	public static final String PRIMARY_KEY_TAG_NAME = "primaryKey";
	// public static final String FOREIGN_KEY_TAG_NAME = "foreignKey";
	public static final String NAME_ATTR_NAME = "name";
	public static final String DESCRIPTION_ATTR_NAME = "description";
	public static final String TYPE_ATTR_NAME = "type";
	public static final String NULLABLE_ATTR_NAME = "nullable";
	public static final String MODE_ATTR_NAME = "parameterMode";
	public static final String IS_PK_ATTR_NAME = "isPk";
	// public static final String FK_TAG_NAME = "fk";
	public static final String SELECTION_FORM_TAG_NAME = "selectionForm";
	// public static final String CLASS_HINT_TAG_NAME = "classHint";
	public static final String ENTITY_ID_ATTR_NAME = "entityId";
	public static final String QUERY_ID_ATTR_NAME = "queryId";
	// public static final String ENTITY_TABLE_ALIAS = "tableAlias";
	public static final String LEFT_ENTITY_ID_ATTR_NAME = "leftEntityId";
	public static final String LEFT_ENTITY_FIELD_ATTR_NAME = "leftEntityFieldName";
	public static final String LEFT_ENTITY_PARAMETER_ATTR_NAME = "leftEntityParameterName";
	public static final String RIGHT_ENTITY_ID_ATTR_NAME = "rightEntityId";
	public static final String RIGHT_ENTITY_FIELD_ATTR_NAME = "rightEntityFieldName";
	public static final String RIGHT_ENTITY_PARAMETER_ATTR_NAME = "rightEntityParameterName";
	public static final String CONSTRAINT_NAME_ATTR_NAME = NAME_ATTR_NAME;
	public static final String CONSTRAINT_FIELD_ATTR_NAME = "field";
	public static final String CONSTRAINT_SCHEMA_ATTR_NAME = "schema";
	public static final String CONSTRAINT_TABLE_ATTR_NAME = "table";
	protected Element rootTag;
	protected JavaScriptObject module;
	protected Element currentTag;
	protected Model model;
	protected Collection<Runnable> relationsResolvers = new ArrayList<Runnable>();

	public static Model transform(Document aDocument, String aModuleName, JavaScriptObject aTarget) throws Exception {
		try {
			Element modelElement = aModuleName != null ? findModelElementByBundleName(aDocument.getDocumentElement(), aModuleName) : aDocument.getDocumentElement();
			if (modelElement != null) {
				XmlDom2Model transformer = new XmlDom2Model(modelElement, aTarget);
				Model model = new Model(AppClient.getInstance());
				model.accept(transformer);
				return model;
			} else
				return null;
		} catch (Exception ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}
	}

	private static Element findModelElementByBundleName(Element aElement, String aBundleName) {
		if (aElement.getTagName().equals("datamodel")) {
			return aElement;// the high level code had to do everything in the
							// right way
		} else {
			Node child = aElement.getFirstChild();
			while (child != null) {
				if (child instanceof Element) {
					Element el = (Element) child;
					if (el.hasAttribute("bundle-name")) {
						String bundleName = el.getAttribute("bundle-name");
						if (bundleName.equals(aBundleName)) {
							return el;
						}
					}
				}
				child = child.getNextSibling();
			}
		}
		return null;
	}

	protected XmlDom2Model(Element aRootTag, JavaScriptObject aTarget) {
		super();
		rootTag = aRootTag;
		module = aTarget;
	}

	protected void readModel(Model aModel) throws Exception {
		Element el = rootTag;
		if (el != null && aModel != null) {
			model = aModel;
			try {
				currentTag = el;
				NodeList nl = currentTag.getChildNodes();
				if (nl != null && nl.getLength() > 0) {
					Element lcurrentTag = currentTag;
					try {
						for (int i = 0; i < nl.getLength(); i++) {
							String nodeName = nl.item(i).getNodeName();
							if ("e".equals(nodeName) || ENTITY_TAG_NAME.equals(nodeName)) {
								currentTag = (Element) nl.item(i);
								Entity entity = new Entity();
								entity.accept(this);
							} else if ("r".equals(nodeName) || RELATION_TAG_NAME.equals(nodeName)) {
								currentTag = (Element) nl.item(i);
								Relation relation = new Relation();
								relation.accept(this);
							} else if ("rr".equals(nodeName) || REFERENCE_RELATION_TAG_NAME.equals(nodeName)) {
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
			String entityId = Utils.getAttribute(currentTag, "ei", ENTITY_ID_ATTR_NAME, null);
			if ("null".equals(entityId)) {
				entityId = null;
			}
			assert entityId != null : "Entity id must present";
			entity.setEntityId(entityId);
			String queryId = Utils.getAttribute(currentTag, "qi", QUERY_ID_ATTR_NAME, null);
			if ("null".equals(queryId)) {
				queryId = null;
			}
			assert queryId != null : "Query id must present";
			entity.setQueryName(queryId);
			assert model != null;
			entity.setModel(model);
			model.addEntity(entity);
		}
	}

	@Override
	public void visit(Entity entity) {
		String name = Utils.getAttribute(currentTag, "n", DATASOURCE_NAME_ATTR_NAME, null);
		if (name != null) {
			entity.setName(name);
		}
		String title = Utils.getAttribute(currentTag, "tt", DATASOURCE_TITLE_ATTR_NAME, null);
		if (title != null) {
			entity.setTitle(title);
		}
		readEntity(entity);
	}

	@Override
	public void visit(final Relation relation) {
		if (relation != null && model != null) {
			final String leftEntityId = Utils.getAttribute(currentTag, "lei", LEFT_ENTITY_ID_ATTR_NAME, null);
			final String leftFieldName = Utils.getAttribute(currentTag, "lef", LEFT_ENTITY_FIELD_ATTR_NAME, null);
			final String leftParameterName = Utils.getAttribute(currentTag, "lep", LEFT_ENTITY_PARAMETER_ATTR_NAME, null);
			final String rightEntityId = Utils.getAttribute(currentTag, "rei", RIGHT_ENTITY_ID_ATTR_NAME, null);
			final String rightFieldName = Utils.getAttribute(currentTag, "ref", RIGHT_ENTITY_FIELD_ATTR_NAME, null);
			final String rightParameterName = Utils.getAttribute(currentTag, "rep", RIGHT_ENTITY_PARAMETER_ATTR_NAME, null);

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
							relation.setLeftEntity(lEntity);
							lEntity.addOutRelation(relation);
						}
						Entity rEntity = model.getEntityById(rightEntityId);
						if (rEntity != null) {
							if (rightParameterName != null && !rightParameterName.isEmpty()) {
								relation.setRightField(rEntity.getQuery().getParameters().get(rightParameterName));
							} else if (rightFieldName != null && !rightFieldName.isEmpty()) {
								relation.setRightField(rEntity.getFields().get(rightFieldName));
							}
							relation.setRightEntity(rEntity);
							rEntity.addInRelation(relation);
						}
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

		String scalarPropertyName = Utils.getAttribute(currentTag, "spn", SCALAR_PROP_NAME_ATTR_NAME, null);
		String collectionPropertyName = Utils.getAttribute(currentTag, "cpn", COLLECTION_PROP_NAME_ATTR_NAME, null);
		relation.setScalarPropertyName(scalarPropertyName != null ? scalarPropertyName.trim() : null);
		relation.setCollectionPropertyName(collectionPropertyName != null ? collectionPropertyName.trim() : null);
	}

	@Override
	public void visit(Field aField) {
		try {
			String fieldName = Utils.getAttribute(currentTag, "n", NAME_ATTR_NAME, null);
			if (fieldName != null) {
				aField.setName(fieldName);
			}
			String fieldDesc = Utils.getAttribute(currentTag, "d", DESCRIPTION_ATTR_NAME, null);
			if (fieldDesc != null) {
				aField.setDescription(fieldDesc);
			}
			String fieldType = Utils.getAttribute(currentTag, "t", TYPE_ATTR_NAME, null);
			if (fieldType != null) {
				aField.setType(fieldType);
			}
			aField.setNullable(Utils.getBooleanAttribute(currentTag, "nl", NULLABLE_ATTR_NAME, true));
			aField.setPk(Utils.getBooleanAttribute(currentTag, "p", IS_PK_ATTR_NAME, false));

			if (aField instanceof Parameter) {
				((Parameter) aField).setMode(Utils.getIntegerAttribute(currentTag, "pm", MODE_ATTR_NAME, 0/*
																										 * ParameterMetaData
																										 * .
																										 * parameterModeUnknown
																										 */));
				String selectionForm = Utils.getAttribute(currentTag, "sf", SELECTION_FORM_TAG_NAME, null);
				if (selectionForm != null && !"null".equals(selectionForm)) {
					((Parameter) aField).setSelectionForm(selectionForm);
				}
			}
			Element lcurrentTag = currentTag;
			try {
				currentTag = Utils.scanForElementByTagName(currentTag, "pr", PRIMARY_KEY_TAG_NAME);
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
			String name = Utils.getAttribute(currentTag, "n", CONSTRAINT_NAME_ATTR_NAME, null);
			if (name != null) {
				pk.setCName(name);
			}
			String schema = Utils.getAttribute(currentTag, "s", CONSTRAINT_SCHEMA_ATTR_NAME, null);
			if (schema != null) {
				pk.setSchema(schema);
			}
			String table = Utils.getAttribute(currentTag, "tl", CONSTRAINT_TABLE_ATTR_NAME, null);
			if (table != null) {
				pk.setTable(table);
			}
			String field = Utils.getAttribute(currentTag, "f", CONSTRAINT_FIELD_ATTR_NAME, null);
			if (field != null) {
				pk.setField(field);
			}
		}
	}

	public static ForeignKeySpec.ForeignKeyRule readForeignKeyRuleAttribute(Element aElement, String aShortName, String aLongName, ForeignKeySpec.ForeignKeyRule defaultValue) {
		try {
			String attrValue = Utils.getAttribute(aElement, aShortName, aLongName, null);
			return attrValue != null ? ForeignKeySpec.ForeignKeyRule.valueOf(attrValue) : defaultValue;
		} catch (Exception ex) {
			return defaultValue;
		}
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

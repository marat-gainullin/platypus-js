/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.datastore;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.client.model.application.ApplicationEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.AbstractDataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author pk
 */
public class DatamodelDataStore extends AbstractDataStore {

    public static boolean isGeometry(DataTypeInfo aTypeInfo) {
        return DataTypeInfo.GEOMETRY.equals(aTypeInfo);
        //This is bad way because abstraction became.
        /* return (/*Oracle*//*aTypeInfo.getSqlType() == Types.STRUCT || aTypeInfo.getSqlType() == java.sql.Types.OTHER) && (aTypeInfo.getSqlTypeName().endsWith("GEOMETRY") || aTypeInfo.getSqlTypeName().endsWith("CURVE")
                /*|| aTypeInfo.getSqlTypeName().endsWith("POLYGON") || aTypeInfo.getSqlTypeName().endsWith("LINESTRING") || aTypeInfo.getSqlTypeName().endsWith("POINT")
                /*|| aTypeInfo.getSqlTypeName().endsWith("SURFACE")/*Oracle*/
                /*|| /*PostGIS*/ /*"point".equalsIgnoreCase(aTypeInfo.getSqlTypeName()) || "line".equalsIgnoreCase(aTypeInfo.getSqlTypeName())
                || "lseg".equalsIgnoreCase(aTypeInfo.getSqlTypeName()) || "box".equalsIgnoreCase(aTypeInfo.getSqlTypeName())
                || "path".equalsIgnoreCase(aTypeInfo.getSqlTypeName()) || "polygon".equalsIgnoreCase(aTypeInfo.getSqlTypeName())
                /*|| "circle".equalsIgnoreCase(aTypeInfo.getSqlTypeName()))/*PostGIS*/
    }

    protected static class TypeInfoEntry {

        public Locator featureLocator;
        public int[] featurePksBinds;
        public int featureFieldsCount;
        public int typeKeyColIndex;
        public List<Row> rows;
    }
    public static final String ROW_ATTR_NAME = "Row";
    protected final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
    protected Map<String, RowsetFeatureDescriptor> featureDescriptors = new HashMap<>();
    protected Map<String, SimpleFeatureType> featureTypes = new HashMap<>();
    private final Map<String, TypeInfoEntry> typeInfos = new HashMap<>();

    public DatamodelDataStore() {
        super();
    }

    private TypeInfoEntry achieveTypeInfo(String aTypeName, final RowsetFeatureDescriptor descriptor, Rowset rowset) throws RowsetException, IOException, IllegalStateException {
        TypeInfoEntry typeInfo;
        synchronized (typeInfos) {
            typeInfo = typeInfos.get(aTypeName);
            if (typeInfo == null) {
                typeInfo = new TypeInfoEntry();
                List<Row> rows = new ArrayList<>();
                if (descriptor.getTypeRef() != null) {
                    typeInfo.typeKeyColIndex = rowset.getFields().find(descriptor.getTypeRef().getFieldName());
                    if (typeInfo.typeKeyColIndex <= 0) {
                        throw new IOException(String.format("type must be valid field value. it must be convertable to col index. Field name: %s; resulted col index %d", descriptor.getTypeRef().getFieldName(), typeInfo.typeKeyColIndex));
                    }
                    Locator loc = rowset.createLocator();
                    loc.beginConstrainting();
                    try {
                        loc.addConstraint(typeInfo.typeKeyColIndex);
                    } finally {
                        loc.endConstrainting();
                    }
                    assert loc != null;
                    Object toFind = rowset.getConverter().convert2RowsetCompatible(descriptor.getTypeValue(), rowset.getFields().get(typeInfo.typeKeyColIndex).getTypeInfo());
                    if (loc.find(toFind)) {
                        for (int i = 0; i < loc.getSize(); i++) {
                            rows.add(loc.getRow(i));
                        }
                    }
                    typeInfo.featureLocator = loc;
                } else {
                    rows.addAll(rowset.getCurrent());
                }
                typeInfo.rows = rows;
                typeInfo.featurePksBinds = bindReaderPks(rowset.getFields());
                typeInfo.featureFieldsCount = rowset.getFields().getFieldsCount();
                typeInfos.put(aTypeName, typeInfo);
            }
        }
        return typeInfo;
    }

    public void clearTypeInfoCache(String aTypeName) {
        synchronized (typeInfos) {
            if (aTypeName == null) {
                typeInfos.clear();
            } else {
                typeInfos.remove(aTypeName);
            }
        }
    }

    private int[] bindReaderPks(Fields aFields) {
        List<Field> pks = aFields.getPrimaryKeys();
        int[] pkFieldsIdxes = new int[pks.size()];
        for (int i = 0; i < pkFieldsIdxes.length; i++) {
            pkFieldsIdxes[i] = aFields.find(pks.get(i).getName());
        }
        return pkFieldsIdxes;
    }

    public Map<String, RowsetFeatureDescriptor> getFeatureDescriptors() {
        return featureDescriptors;
    }

    public void setFeatureDescriptors(Map<String, RowsetFeatureDescriptor> aFeatureDescriptors) {
        featureDescriptors = aFeatureDescriptors;
        featureTypes = new HashMap<>();
    }

    public void setFeatureDescriptors(Collection<RowsetFeatureDescriptor> aDescriptors) {
        featureDescriptors = new HashMap<>();
        for (RowsetFeatureDescriptor descriptor : aDescriptors) {
            featureDescriptors.put(descriptor.getTypeName(), descriptor);
        }
        featureTypes = new HashMap<>();
    }

    @Override
    public String[] getTypeNames() throws IOException {
        String[] typeNames = new String[featureDescriptors.size()];
        typeNames = featureDescriptors.keySet().toArray(typeNames);
        return typeNames;
    }

    @Override
    public SimpleFeatureType getSchema(String aTypeName) throws IOException {
        if (!featureDescriptors.containsKey(aTypeName)) {
            throw new IllegalArgumentException(String.format("No such feature type: %s", aTypeName));
        }
        if (!featureTypes.containsKey(aTypeName)) {
            SimpleFeatureType ft = constructFeatureType(aTypeName);
            featureTypes.put(aTypeName, ft);
        }
        return featureTypes.get(aTypeName);
    }

    private SimpleFeatureType constructFeatureType(String aTypeName) throws IOException {
        try {
            ApplicationEntity<?, ?, ?> entity = getEntityForType(aTypeName);
            final RowsetFeatureDescriptor desc = featureDescriptors.get(aTypeName);
            simpleFeatureTypeBuilder.setName(aTypeName);
            if (desc.getCrsWkt() != null && !desc.getCrsWkt().isEmpty()) {
                simpleFeatureTypeBuilder.setCRS(CRS.parseWKT(desc.getCrsWkt()));
            }
            final Fields fields = entity.getFields();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                if (isGeometry(fields.get(i).getTypeInfo())) {
                    simpleFeatureTypeBuilder.add(fields.get(i).getName(), desc.getGeometryBindingClass());
                } else {
                    simpleFeatureTypeBuilder.add(fields.get(i).getName(), Class.forName(fields.get(i).getTypeInfo().getJavaClassName()));
                }
            }
            simpleFeatureTypeBuilder.add(DatamodelDataStore.ROW_ATTR_NAME, Row.class);
            SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
            return featureType;
        } catch (NullPointerException | FactoryException | ClassNotFoundException ex) {
            if (!(ex instanceof IOException)) {
                throw new IOException(ex);
            } else {
                throw (IOException) ex;
            }
        }
    }

    private ApplicationEntity<?, ?, ?> getEntityForType(String typeName) throws NullPointerException {
        final RowsetFeatureDescriptor descriptor = featureDescriptors.get(typeName);
        if (descriptor == null) {
            throw new NullPointerException("null descriptor for feature type " + typeName);
        }
        final ApplicationEntity<?, ?, ?> entity = descriptor.getEntity();
        if (entity == null) {
            throw new NullPointerException("null entity for descriptor of feature type " + typeName);
        }
        return entity;
    }

    @Override
    public synchronized FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String aTypeName) throws IOException {
        try {
            final SimpleFeatureType featureType = getSchema(aTypeName);
            final RowsetFeatureDescriptor descriptor = featureDescriptors.get(aTypeName);
            if (descriptor == null) {
                throw new NullPointerException("null descriptor for feature type " + aTypeName);
            }
            final ApplicationEntity<?, ?, ?> entity = descriptor.getEntity();
            if (entity == null) {
                throw new NullPointerException("null entity for descriptor of feature type " + aTypeName);
            }
            Rowset rowset = entity.getRowset();
            if (rowset == null) {
                throw new NullPointerException("null rowset for descriptor entity of feature type " + aTypeName);
            }
            TypeInfoEntry typeInfo = achieveTypeInfo(aTypeName, descriptor, rowset);
            return new RowsFeatureReader(typeInfo.rows, typeInfo.featurePksBinds, typeInfo.featureFieldsCount, featureType);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public SimpleFeatureSource getFeatureSource(String aTypeName) throws IOException {
        try {
            final RowsetFeatureDescriptor descriptor = featureDescriptors.get(aTypeName);
            if (descriptor == null) {
                throw new NullPointerException("null descriptor for feature type " + aTypeName);
            }
            return new RowsFeatureSource(this, getSchema(aTypeName), descriptor);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}

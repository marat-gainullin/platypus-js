/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author mg
 */
public class RowsFeatureSourceEventsTest  extends GeoBaseTest{

    protected final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
    protected SimpleFeatureType featureType;
    protected final FeatureStyleDescriptor styleDescriptor = new FeatureStyleDescriptor();
    protected Fields fields;
    protected Rowset testRowset;
    protected static final int START_FEATURES_TYPE = 50010;
    protected static final int FURTHER_FEATURES_TYPE = 50050;
    protected final RowsetFeatureDescriptor descriptor1 = new RowsetFeatureDescriptor(); // START_FEATURES_TYPE
    protected final RowsetFeatureDescriptor descriptor2 = new RowsetFeatureDescriptor(); // FURTHER_FEATURES_TYPE
    protected final RowsetFeatureDescriptor descriptor3 = new RowsetFeatureDescriptor(); // null
    protected final RowsetFeatureDescriptor descriptor = new RowsetFeatureDescriptor(); // null
    protected RowsFeatureSource featureSource1;
    protected RowsFeatureSource featureSource2;
    protected RowsFeatureSource featureSource3;
    protected RowsFeatureSource featureSource;
    protected TestFeatureListener listener1;
    protected TestFeatureListener listener2;
    protected TestFeatureListener listener3;
    protected TestFeatureListener listener;

    private int insertSamleData(Rowset aRowset, int toInsert, Integer aType) throws RowsetException {
        for (int i = 0; i < toInsert; i++) {
            aRowset.insert(new Object[]{
                        1, aRowset.size(), // pk
                        2, aType, // type
                        3, GisUtilities.createPoint(45, 45), // geometry
                        4, "label for geometrty binding tests № " + String.valueOf(i), // label field
                        5, "sample test row for geometrty binding tests № " + String.valueOf(i) // free field
                    });
            aRowset.currentToOriginal();
            aRowset.beforeFirst();
        }
        return toInsert;
    }

    private void refreshListeners() {
        featureSource1.removeFeatureListener(listener1);
        listener1 = new TestFeatureListener();
        featureSource1.addFeatureListener(listener1);

        featureSource2.removeFeatureListener(listener2);
        listener2 = new TestFeatureListener();
        featureSource2.addFeatureListener(listener2);

        featureSource3.removeFeatureListener(listener3);
        listener3 = new TestFeatureListener();
        featureSource3.addFeatureListener(listener3);

        featureSource.removeFeatureListener(listener);
        listener = new TestFeatureListener();
        featureSource.addFeatureListener(listener);
    }

    protected class TestFeatureListener implements FeatureListener {

        public int eventsCount = 0;

        @Override
        public void changed(FeatureEvent featureEvent) {
            eventsCount++;
        }
    }

    @Before
    public void prepare() throws Exception {
        fields = new Fields();
        Field pkField = new Field("pkField"); // no comments
        pkField.setTypeInfo(DataTypeInfo.BIGINT);
        pkField.setPk(true);
        fields.add(pkField);
        Field typeField = new Field("typeField"); // type of the features
        typeField.setTypeInfo(DataTypeInfo.TINYINT);
        fields.add(typeField);
        Field geometryField = new Field("geometryField"); // Geometry
        // oracle style of geometry...
        geometryField.setTypeInfo(DataTypeInfo.STRUCT);
        geometryField.getTypeInfo().setSqlTypeName("MDSYS.SDO_GEOMETRY");
        geometryField.getTypeInfo().setJavaClassName(Geometry.class.getName());
        fields.add(geometryField);
        Field labelField = new Field("labelField");
        labelField.setTypeInfo(DataTypeInfo.VARCHAR);
        fields.add(labelField);
        // field, with no meaning for mapping, so no events will be accepted on it.
        // And we have to ensure that it ignored.
        Field freeField = new Field("freeField");
        freeField.setTypeInfo(DataTypeInfo.VARCHAR);
        fields.add(freeField);

        testRowset = new Rowset(fields);
        insertSamleData(testRowset, 4, START_FEATURES_TYPE);
        ApplicationDbModel model = new ApplicationDbModel(dbClient);
        ApplicationDbEntity entity = new ApplicationDbEntity(model);
        entity.setRowset(testRowset);
        // setup feature style
        styleDescriptor.setLabelField(new ModelElementRef(labelField, true, entity.getEntityID()));
        // setup feature descriptor1
        descriptor1.setEntity(entity);
        descriptor1.setRef(new ModelEntityRef(entity.getEntityID()));
        descriptor1.setTypeRef(new ModelElementRef(typeField, true, entity.getEntityID()));
        descriptor1.setTypeValue(START_FEATURES_TYPE);
        descriptor1.setStyle(styleDescriptor);
        // setup feature descriptor2
        descriptor2.setEntity(entity);
        descriptor2.setRef(new ModelEntityRef(entity.getEntityID()));
        descriptor2.setTypeRef(new ModelElementRef(typeField, true, entity.getEntityID()));
        descriptor2.setTypeValue(FURTHER_FEATURES_TYPE);
        descriptor2.setStyle(styleDescriptor);
        // setup feature descriptor3
        descriptor3.setEntity(entity);
        descriptor3.setRef(new ModelEntityRef(entity.getEntityID()));
        descriptor3.setTypeRef(new ModelElementRef(typeField, true, entity.getEntityID()));
        descriptor3.setTypeValue(null);
        descriptor3.setStyle(styleDescriptor);
        // setup feature descriptor
        descriptor.setEntity(entity);
        descriptor.setRef(new ModelEntityRef(entity.getEntityID()));
        descriptor.setStyle(styleDescriptor);
        // setup feature type
        simpleFeatureTypeBuilder.setName(this.getClass().getName());
        simpleFeatureTypeBuilder.add(pkField.getName(), Class.forName(pkField.getTypeInfo().getJavaClassName()));
        simpleFeatureTypeBuilder.add(typeField.getName(), Class.forName(typeField.getTypeInfo().getJavaClassName()));
        simpleFeatureTypeBuilder.add(geometryField.getName(), Point.class);
        simpleFeatureTypeBuilder.add(labelField.getName(), Class.forName(labelField.getTypeInfo().getJavaClassName()));
        simpleFeatureTypeBuilder.add(freeField.getName(), Class.forName(freeField.getTypeInfo().getJavaClassName()));
        featureType = simpleFeatureTypeBuilder.buildFeatureType();
        // test data store
        DatamodelDataStore store = new DatamodelDataStore();
        List<RowsetFeatureDescriptor> descriptors = new ArrayList<>();
        descriptors.add(descriptor1);
        descriptors.add(descriptor2);
        descriptors.add(descriptor3);
        descriptors.add(descriptor);
        store.setFeatureDescriptors(descriptors);
        featureSource1 = new RowsFeatureSource(store, featureType, descriptor1);
        featureSource2 = new RowsFeatureSource(store, featureType, descriptor2);
        featureSource3 = new RowsFeatureSource(store, featureType, descriptor3);
        featureSource = new RowsFeatureSource(store, featureType, descriptor);
    }

    @Test
    public void testInsert() throws RowsetException {
        refreshListeners();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        assertEquals(toInsert, listener1.eventsCount);
        assertEquals(0, listener2.eventsCount);
        assertEquals(0, listener3.eventsCount);
        assertEquals(toInsert, listener.eventsCount);

        refreshListeners();
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        assertEquals(0, listener1.eventsCount);
        assertEquals(toInsert, listener2.eventsCount);
        assertEquals(0, listener3.eventsCount);
        assertEquals(toInsert, listener.eventsCount);

        refreshListeners();
        insertSamleData(testRowset, toInsert, null);
        assertEquals(0, listener1.eventsCount);
        assertEquals(0, listener2.eventsCount);
        assertEquals(toInsert, listener3.eventsCount);
        assertEquals(toInsert, listener.eventsCount);

        refreshListeners();
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        assertEquals(0, listener1.eventsCount);
        assertEquals(0, listener2.eventsCount);
        assertEquals(0, listener3.eventsCount);
        assertEquals(toInsert, listener.eventsCount);
    }

    @Test
    public void testDelete() throws RowsetException {
        testRowset.deleteAll();
        refreshListeners();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, null);
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        refreshListeners();
        while (!testRowset.isEmpty()) {
            testRowset.first();
            testRowset.delete();
        }
        assertEquals(toInsert, listener1.eventsCount);
        assertEquals(toInsert, listener2.eventsCount);
        assertEquals(toInsert, listener3.eventsCount);
        assertEquals(toInsert * 4, listener.eventsCount);
    }

    @Test
    public void testUpdateType() throws RowsetException {
        testRowset.deleteAll();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, null);
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        refreshListeners();
        testRowset.beforeFirst();
        while (testRowset.next()) {
            if (testRowset.getObject(2) == null) {
                testRowset.updateObject(2, FURTHER_FEATURES_TYPE);
            }
        }
        assertEquals(0, listener1.eventsCount);
        assertEquals(toInsert, listener2.eventsCount);
        assertEquals(toInsert, listener3.eventsCount);
        assertEquals(0, listener.eventsCount);
    }

    @Test
    public void testUpdateLabel() throws RowsetException {
        testRowset.deleteAll();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, null);
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        refreshListeners();
        testRowset.beforeFirst();
        while (testRowset.next()) {
            testRowset.updateObject(4, "label for label updating tests");
        }
        assertEquals(toInsert, listener1.eventsCount);
        assertEquals(toInsert, listener2.eventsCount);
        assertEquals(toInsert, listener3.eventsCount);
        assertEquals(toInsert * 4, listener.eventsCount);
    }

    @Test
    public void testUpdateGeometry() throws RowsetException {
        testRowset.deleteAll();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, null);
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        refreshListeners();
        testRowset.beforeFirst();
        while (testRowset.next()) {
            testRowset.updateObject(3, GisUtilities.createPoint(58, 5));
        }
        assertEquals(toInsert, listener1.eventsCount);
        assertEquals(toInsert, listener2.eventsCount);
        assertEquals(toInsert, listener3.eventsCount);
        assertEquals(toInsert * 4, listener.eventsCount);
    }

    @Test
    public void testUpdateFreeField() throws RowsetException {
        testRowset.deleteAll();
        int toInsert = 7;
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, FURTHER_FEATURES_TYPE);
        insertSamleData(testRowset, toInsert, null);
        insertSamleData(testRowset, toInsert, START_FEATURES_TYPE + 1);// bad value
        refreshListeners();
        testRowset.beforeFirst();
        while (testRowset.next()) {
            testRowset.updateObject(5, "Sample updating value for testing free field");
        }
        assertEquals(0, listener1.eventsCount);
        assertEquals(0, listener2.eventsCount);
        assertEquals(0, listener3.eventsCount);
        assertEquals(0, listener.eventsCount);
    }
}

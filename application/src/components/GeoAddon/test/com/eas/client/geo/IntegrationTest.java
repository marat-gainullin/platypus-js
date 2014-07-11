/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.*;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.operation.MathTransformFactory;

/**
 * This TestCase contains integration and functional tests which are more
 * complex, than individual classes' unit-tests.
 *
 * <p>
 * The tests here test overall integration of Platypus Platform datasources into
 * the GeoTools infrastructure.</p>
 *
 * @author pk
 */
public class IntegrationTest extends GeoBaseTest {

    private static final String DELAWARE_MAP_CRS_WKT = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
    private final static String[] DELAWARE_MAP_TABLES = {
        "delaware_administrative", "delaware_natural", "delaware_water", "delaware_coastline", "delaware_highways", /*-- too expensive to query*/ "delaware_points"
    };
    private static final String PROJECTION_HUMAN_NAME = "Stereographic North Pole";
    private static final String PROJECTION_QUALIFIED_NAME = "Stereographic_North_Pole";
    private static Map<String, RowsetFeatureDescriptor> map;
    private static ApplicationDbModel model;
    private static MathTransformFactory mtFactory;
    private static CRSFactory crsFactory;
    private Mockery context = new JUnit4Mockery();
    private int featuresRendered; // used in RendererListener

    public IntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        GeoBaseTest.setUpClass();
        // Create geotools factories.
        mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
        crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
        // Populate datamodel.
        model = new ApplicationDbModel(dbClient);
        model.requery();
        map = new HashMap<>();
        for (String tableName : DELAWARE_MAP_TABLES) {
            ApplicationDbEntity e = new ApplicationDbEntity(model);
            e.regenerateId();
            e.setTableName(tableName);
            e.validateQuery();
            model.addEntity(e);
            final Rowset rowset = e.getRowset();
            assertNotNull(rowset);
            rowset.refresh();
            assertTrue(rowset.size() > 0);
            final RowsetFeatureDescriptor rfd = new RowsetFeatureDescriptor(e.getTableName(), e);
            rfd.setCrsWkt(DELAWARE_MAP_CRS_WKT);
            map.put(e.getTableName(), rfd);
        }

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws SQLException {
    }

    @After
    public void tearDown() {
    }

    /**
     * Tests Delaware map data not contain point (0, 0).
     *
     * <p>
     * (0,0) can be in Delaware map data because .shp files contained it by
     * error.</p>
     *
     * @throws IOException
     */
    @Test
    public void testLayerBoundsCalculation() throws IOException {
        System.out.println("Testing layer bounds calculation");
        // Create datasource and populate it with features.
        DatamodelDataStore myDS = new DatamodelDataStore();
        myDS.setFeatureDescriptors(map);
        // Create map context and populate it with data.
        final MapContent mapContent = new MapContent();
        for (String typeName : DELAWARE_MAP_TABLES) {
            mapContent.addLayer(new FeatureLayer(myDS.getFeatureSource(typeName), null));
        }
        // Look for a "bad" layer...
        for (int i = 0; i < mapContent.layers().size(); i++) {
            final Layer layer = mapContent.layers().get(i);
            final FeatureSource<? extends FeatureType, ? extends Feature> featureSource = layer.getFeatureSource();
            final String featureTypeName = featureSource.getSchema().getName().getLocalPart();
            // Check individual features inside a layer.
            for (FeatureIterator<? extends Feature> it = featureSource.getFeatures().features(); it.hasNext();) {
                final Feature feature = it.next();
                assertFalse(String.format("Feature %s contains point with coordinates (0, 0), though it should not", String.valueOf(feature)),
                        feature.getBounds().contains(0, 0));
            }
            // Check feature source and layer boundaries.
            assertFalse(String.format("Feature source %d (%s) contains point with coordinates (0, 0), though it should not", i, featureTypeName),
                    featureSource.getBounds().contains(0, 0));
            assertFalse(String.format("Layer %d (%s) contains point with coordinates (0, 0), though it should not", i, featureTypeName),
                    layer.getBounds().contains(0, 0));
        }
        // Check that total map boundaries do not contain (0, 0).
        final ReferencedEnvelope layerBounds = mapContent.getMaxBounds();
        assertFalse(layerBounds.contains(0, 0));
    }


    /**
     * Tests that refreshing datamodel, or individual entities results in MapContext
     * change eventss.
     *
     * <p>Uses Delaware map for this.</p>
     *
     */
    @Test
    public void testMapContextUpdatingOnEntityRefreshing() throws Exception {
        System.out.println("Test that refreshing datamodel and/or individual entities causes a MapContext to fire event that it has changed.");
        // Create datasource and populate it with features.
        DatamodelDataStore myDS = new DatamodelDataStore();
        myDS.setFeatureDescriptors(map);
        // Create map context and populate it with data.
        final MapContent mapContext = new MapContent();
        for (String typeName : DELAWARE_MAP_TABLES) {
            mapContext.addLayer(new FeatureLayer(myDS.getFeatureSource(typeName), null));
        }
        final MapLayerListListener listener = context.mock(MapLayerListListener.class);
        mapContext.addMapLayerListListener(listener);
        context.checking(new Expectations() {
            {
                exactly(DELAWARE_MAP_TABLES.length).of(listener).layerChanged(with(any(MapLayerListEvent.class)));
            }
        });
        model.requery();
    }
}

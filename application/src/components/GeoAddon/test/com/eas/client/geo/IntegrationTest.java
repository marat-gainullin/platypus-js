/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapLayer;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.operation.DefiningConversion;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.*;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;

/**
 * This TestCase contains integration and functional tests which are more
 * complex, than individual classes' unit-tests.
 *
 * <p>The tests here test overall integration of Platypus Platform datasources
 * into the GeoTools infrastructure.</p>
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
    private static ApplicationDbModel datamodel;
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
        datamodel = new ApplicationDbModel(dbClient);
        datamodel.setRuntime(true);
        map = new HashMap<>();
        for (String tableName : DELAWARE_MAP_TABLES) {
            ApplicationDbEntity e = new ApplicationDbEntity(datamodel);
            e.regenerateID();
            e.setTableName(tableName);
            datamodel.addEntity(e);
            final Rowset rowset = e.getRowset();
            assertNotNull(rowset);
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
     * <p>(0,0) can be in Delaware map data because .shp files contained it by
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
        final DefaultMapContext mapContext = new DefaultMapContext();
        for (String typeName : DELAWARE_MAP_TABLES) {
            mapContext.addLayer(myDS.getFeatureSource(typeName), null);
        }
        // Look for a "bad" layer...
        for (int i = 0; i < mapContext.getLayerCount(); i++) {
            final MapLayer layer = mapContext.getLayer(i);
            final FeatureSource<? extends FeatureType, ? extends Feature> featureSource = layer.getFeatureSource();
            final String featureTypeName = featureSource.getSchema().getName().getLocalPart();
            // Check individual features inside a layer.
            for (Iterator<? extends Feature> it = featureSource.getFeatures().iterator(); it.hasNext();) {
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
        final ReferencedEnvelope layerBounds = mapContext.getLayerBounds();
        assertFalse(layerBounds.contains(0, 0));
    }

    /**
     * Tests that projected map of Delaware has valid bounds.
     *
     * @throws IOException
     * @throws TransformException
     * @throws FactoryException
     */
    @Test
    public void testProjectionTransformation() throws IOException, FactoryException, TransformException {
        System.out.println("Testing map rendering with custom projection.");
        // Create datasource and populate it with features.
        DatamodelDataStore myDS = new DatamodelDataStore();
        myDS.setFeatureDescriptors(map);
        // Create map context and populate it with data.
        final DefaultMapContext mapContext = new DefaultMapContext();
        for (String typeName : DELAWARE_MAP_TABLES) {
            mapContext.addLayer(myDS.getFeatureSource(typeName), null);
        }
        // Create geographic CRS which defines coordinates, stored in DB (for Delaware map).
        GeographicCRS geoCRS = (GeographicCRS) CRS.parseWKT(DELAWARE_MAP_CRS_WKT);
        assertFalse("Map bounds is empty.", mapContext.getLayerBounds().isEmpty()); //NOI18N
        // Create north-pole stereographic projection.
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters(PROJECTION_QUALIFIED_NAME);
        // Maybe, tune parameters, but usually, default values are ok.
        final DefiningConversion conversion = new DefiningConversion(PROJECTION_HUMAN_NAME, parameters);
        final ProjectedCRS projectedCRS = crsFactory.createProjectedCRS(Collections.singletonMap(IdentifiedObject.NAME_KEY, PROJECTION_HUMAN_NAME), geoCRS, conversion, DefaultCartesianCS.GENERIC_2D);
        mapContext.setCoordinateReferenceSystem(projectedCRS);
        // Check that no layer bounds are empty.
        for (int i = 0; i < mapContext.getLayerCount(); i++) {
            final MapLayer layer = mapContext.getLayer(i);
            final FeatureSource<? extends FeatureType, ? extends Feature> featureSource = layer.getFeatureSource();
            final String featureTypeName = featureSource.getSchema().getName().getLocalPart();
            final ReferencedEnvelope layerBounds = layer.getBounds();
            System.out.printf("Layer %d (%s) bounds %s, CRS %s\n", i, featureTypeName, String.valueOf(layerBounds), String.valueOf(layerBounds.getCoordinateReferenceSystem())); //NOI18N
            assertNotNull(String.format("Layer %d (%s) bounds CRS is empty", i, featureTypeName, String.valueOf(layerBounds)), layerBounds.getCoordinateReferenceSystem()); //NOI18N
            assertFalse(String.format("Layer %d (%s) bounds is empty", i, featureTypeName), layerBounds.isEmpty()); //NOI18N
        }
        System.out.println(mapContext.getLayerBounds());
        assertFalse("Projected map bounds is empty.", mapContext.getLayerBounds().isEmpty()); //NOI18N
        final BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        final StreamingRenderer renderer = new StreamingRenderer();
        renderer.setContext(mapContext);
        final Graphics2D graphics = image.createGraphics();
        // Unable to use jMock here, because it causes heap memory exhaustion.
        renderer.addRenderListener(new RenderListener() {
            @Override
            public void featureRenderer(SimpleFeature sf) {
                featuresRendered++;
            }

            @Override
            public void errorOccurred(Exception excptn) {
                System.out.println("Error occured: " + excptn);
                fail("Error occured: " + excptn); //NOI18N
            }
        });
        featuresRendered = 0;
        final int totalFeatures = getMapContextTotalFeatures(mapContext);
        mapContext.setAreaOfInterest(mapContext.getLayerBounds());
        renderer.paint(graphics, new Rectangle(800, 600), mapContext.getAreaOfInterest());
        System.out.println(String.format("Features rendered: %d", featuresRendered)); //NOI18N
        assertEquals("Not all features rendered.", totalFeatures, featuresRendered);
    }

    private int getMapContextTotalFeatures(final DefaultMapContext mapContext) throws IOException, IndexOutOfBoundsException {
        int totalFeatures = 0;
        for (int i = 0; i < mapContext.getLayerCount(); i++) {
            totalFeatures += mapContext.getLayer(i).getFeatureSource().getFeatures().size();
        }
        return totalFeatures;
    }


    /* Tests that refreshing datamodel, or individual entities results in MapContext
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
        final DefaultMapContext mapContext = new DefaultMapContext();
        for (String typeName : DELAWARE_MAP_TABLES) {
            mapContext.addLayer(myDS.getFeatureSource(typeName), null);
        }
        final MapLayerListListener listener = context.mock(MapLayerListListener.class);
        mapContext.addMapLayerListListener(listener);
        context.checking(new Expectations() {
            {
                exactly(DELAWARE_MAP_TABLES.length).of(listener).layerChanged(with(any(MapLayerListEvent.class)));
            }
        });
        datamodel.requery();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.controls.mappane.map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author mg
 */
public class MapGraphicTest {

    public static final String MERCATOR_WKT = ""
            + "PROJCS[\"WGS84 / Google Mercator\", GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]], PROJECTION[\"Mercator_1SP\"], PARAMETER[\"semi_minor\", 6378137.0], PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"central_meridian\", 0.0], PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 0.0], PARAMETER[\"false_northing\", 0.0], UNIT[\"m\", 1.0], AXIS[\"x\", EAST], AXIS[\"y\", NORTH], AUTHORITY[\"EPSG\",\"900913\"]]";
    public static final String ORTHOGRAPHIC_WKT = ""
            + "PROJCS[\"WGS84 / Spheric sample\", GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]], PROJECTION[\"Orthographic\"], PARAMETER[\"semi_minor\", 6378137.0], PARAMETER[\"latitude_of_origin\", 0.0], PARAMETER[\"central_meridian\", 0.0], PARAMETER[\"scale_factor\", 1.0], PARAMETER[\"false_easting\", 0.0], PARAMETER[\"false_northing\", 0.0], UNIT[\"m\", 1.0], AXIS[\"x\", EAST], AXIS[\"y\", NORTH], AUTHORITY[\"EPSG\",\"900913\"]]";
    public static final String DATA_WKT = ""
            + "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Longitude\", EAST], AXIS[\"Latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]]";
    public static final Set<Geometry> points = new HashSet<>();
    public static final Set<Geometry> polies = new HashSet<>();
    protected static final double precision = 1e-2;

    static {
        GeometryFactory factory = new GeometryFactory();
        Point point = null;
        point = factory.createPoint(new Coordinate(0 * precision, 0 * precision));
        points.add(point);
        point = factory.createPoint(new Coordinate(10 * precision, -8 * precision));
        points.add(point);
        point = factory.createPoint(new Coordinate(5 * precision, -20 * precision));
        points.add(point);
        point = factory.createPoint(new Coordinate(13 * precision, 25 * precision));
        points.add(point);

        Coordinate[] coords1 = new Coordinate[4];
        coords1[0] = new Coordinate(-10 * precision, -21 * precision);
        coords1[1] = new Coordinate(-20 * precision, 32 * precision);
        coords1[2] = new Coordinate(-30 * precision, 16 * precision);
        coords1[3] = coords1[0];
        polies.add(factory.createPolygon(factory.createLinearRing(coords1), null));

        Coordinate[] coords2 = new Coordinate[5];
        coords2[0] = new Coordinate(22 * precision, 17 * precision);
        coords2[1] = new Coordinate(4 * precision, 8 * precision);
        coords2[2] = new Coordinate(34 * precision, 12 * precision);
        coords2[3] = new Coordinate(21 * precision, 6 * precision);
        coords2[4] = coords2[0];
        polies.add(factory.createPolygon(factory.createLinearRing(coords2), null));
    }
    protected static final Set<LineString> lineStrings = new HashSet<>();
    //feature instance creation
    protected static final WKTReader wktReader = new WKTReader();
    protected static final StyleBuilder sb = new StyleBuilder();
    protected static CoordinateReferenceSystem dataCrs;
    protected static CoordinateReferenceSystem projectedCrs;

    static {
        try {
            dataCrs = CRS.parseWKT(DATA_WKT);
            projectedCrs = CRS.parseWKT(MERCATOR_WKT);
            lineStrings.add((LineString) wktReader.read("LINESTRING (-1 -1, 1 1)"));
            lineStrings.add((LineString) wktReader.read("LINESTRING (-1 1, 1 -1)"));
            lineStrings.add((LineString) wktReader.read("LINESTRING (1 -1, 3 1)"));
            lineStrings.add((LineString) wktReader.read("LINESTRING (1 1, 3 -1)"));
            lineStrings.add((LineString) wktReader.read("LINESTRING (3 -1, 5 1)"));
            lineStrings.add((LineString) wktReader.read("LINESTRING (3 1, 5 -1)"));
        } catch (FactoryException | ParseException ex) {
            Logger.getLogger(SyncMapTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void geoToolsBaseSettingsTest() throws InterruptedException, FactoryException, ParseException {
        // cartesian view point
        Point2D.Double viewPoint = new Point2D.Double(2 * 111313.83923667614, 0);

        JFrame fr = new JFrame();
        fr.setTitle("geoToolsBaseSettingsTest");
        Container container = fr.getContentPane();
        container.setLayout(new BorderLayout());

        // Prepare styles
        // Create line symbolizer
        LineSymbolizer lineSymb = sb.createLineSymbolizer(Color.green, 1);

        Style lineStyle = sb.createStyle(lineSymb);

        ReferencedEnvelope aoi = new ReferencedEnvelope(new Rectangle2D.Double(viewPoint.x - 100, viewPoint.y - 100, 4 * 111000, 200), projectedCrs); // meters

        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setCRS(dataCrs);
        typeBuilder.setName("Line type");
        typeBuilder.add("graphicRepresentation", LineString.class);
        typeBuilder.add("name", String.class);
        SimpleFeatureType featureType = typeBuilder.buildFeatureType();

        List<SimpleFeature> lst = new ArrayList<>();
        
        Object[] attrs = new Object[2];
        int lNo = 0;
        for (LineString line : lineStrings) {
            attrs[0] = line;
            String lStringId = String.valueOf(lNo++);
            attrs[1] = "line " + lStringId;
            SimpleFeature feature = SimpleFeatureBuilder.build(featureType, attrs, lStringId);
            lst.add(feature);
        }
        
        FeatureCollection fcollection = new ListFeatureCollection(featureType, lst);
        
        final MapContent mainContent = new MapContent(projectedCrs);
        mainContent.getViewport().setBounds(aoi);

        Layer layer1 = new FeatureLayer(fcollection, lineStyle, "Main layer");
        mainContent.addLayer(layer1);

        final GTRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(mainContent);

        final AffineTransform transform = new AffineTransform();
        transform.scale(8e-4, 8e-4);
        transform.translate(-viewPoint.x, -viewPoint.y);
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension size = getSize();
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(size.width / 2, size.height / 2);
                Rectangle screenArea = new Rectangle(-200, -200, 400, 400);
                renderer.paint(g2d, new Rectangle(-200, -200, 400, 400), mainContent.getViewport().getBounds(), transform);
                //renderer.paint(g2d, screenArea, mainContext.getLayerBounds(), transform);
                g2d.draw(screenArea);
            }
        };
        container.add(panel, BorderLayout.CENTER);
        fr.setSize(600, 600);
        fr.setVisible(true);
        Thread.sleep(1000);
        fr.dispose();
    }
}

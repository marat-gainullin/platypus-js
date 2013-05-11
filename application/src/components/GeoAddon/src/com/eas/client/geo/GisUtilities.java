/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.geo.datastore.DatamodelDataStore;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author mg
 */
public class GisUtilities {

    protected static final StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    protected static FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
    protected static StyleBuilder sb = new StyleBuilder();
    protected static final GeometryFactory gFactory = new GeometryFactory();
    protected static final CoordinateSequenceFactory csFactory = gFactory.getCoordinateSequenceFactory();

    public static Polygon createPolygon(Point begPoint, Point endPoint) {
        Coordinate[] coordinates = new Coordinate[4];
        coordinates[0] = new Coordinate(begPoint.getX(), begPoint.getY());
        coordinates[1] = new Coordinate(endPoint.getX(), endPoint.getY());
        coordinates[2] = new Coordinate(endPoint.getX(), endPoint.getY());
        coordinates[3] = new Coordinate(begPoint.getX(), begPoint.getY());
        CoordinateSequence cSeq = csFactory.create(coordinates);
        return gFactory.createPolygon(new LinearRing(cSeq, gFactory), null);
    }

    public static Polygon addPoint2Polygon(Polygon aPoly, Point point2Add) {
        Coordinate[] oldCoordinates = aPoly.getCoordinates();
        Coordinate[] newCoordinates = new Coordinate[oldCoordinates.length + 1];
        System.arraycopy(oldCoordinates, 0, newCoordinates, 0, oldCoordinates.length - 1);
        newCoordinates[newCoordinates.length - 2] = point2Add.getCoordinate();
        newCoordinates[newCoordinates.length - 1] = oldCoordinates[oldCoordinates.length - 1];
        CoordinateSequence cSeq = csFactory.create(newCoordinates);
        return gFactory.createPolygon(new LinearRing(cSeq, gFactory), null);
    }

    public static LineString createLineString(Point begPoint, Point endPoint) {
        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(begPoint.getX(), begPoint.getY());
        coordinates[1] = new Coordinate(endPoint.getX(), endPoint.getY());
        CoordinateSequence cSeq = csFactory.create(coordinates);
        return new LineString(cSeq, gFactory);
    }

    public static LineString addPoint2LineString(LineString aPoly, Point point2Add) {
        Coordinate[] oldCoordinates = aPoly.getCoordinates();
        Coordinate[] newCoordinates = new Coordinate[oldCoordinates.length + 1];
        System.arraycopy(oldCoordinates, 0, newCoordinates, 0, oldCoordinates.length);
        newCoordinates[newCoordinates.length - 1] = point2Add.getCoordinate();
        CoordinateSequence cSeq = csFactory.create(newCoordinates);
        return new LineString(cSeq, gFactory);
    }

    public static Point createPoint(Point2D.Double aPoint) {
        return createPoint(aPoint.x, aPoint.y);
    }

    public static Point createPoint(double aX, double aY) {
        Coordinate coordinate = new Coordinate(aX, aY);
        return createPoint(coordinate);
    }

    public static Point createPoint(Coordinate aCoordinate) {
        return gFactory.createPoint(aCoordinate);
    }

    public static Polygon constructRectyPolygon(Point2D.Double aLeftUpperCorner, Point2D.Double aBottomRightCorner) {
        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(aLeftUpperCorner.x, aLeftUpperCorner.y);
        coordinates[1] = new Coordinate(aBottomRightCorner.x, aLeftUpperCorner.y);
        coordinates[2] = new Coordinate(aBottomRightCorner.x, aBottomRightCorner.y);
        coordinates[3] = new Coordinate(aLeftUpperCorner.x, aBottomRightCorner.y);
        coordinates[4] = new Coordinate(aLeftUpperCorner.x, aLeftUpperCorner.y);
        CoordinateSequence cSeq = csFactory.create(coordinates);
        return gFactory.createPolygon(new LinearRing(cSeq, gFactory), null);
    }

    public static void convertSimpleFeature2SelectionEntries(SimpleFeature aFeature, List<SelectionEntry> aDestination, RowsetFeatureDescriptor aFeatureDescriptor) throws Exception {
        ApplicationEntity<?, ?, ?> entity = aFeatureDescriptor.getEntity();
        Rowset rs = entity.getRowset();
        String featureID = aFeature.getID();
        Object oGeometry = aFeature.getAttribute(aFeature.getFeatureType().getGeometryDescriptor().getLocalName());
        assert oGeometry instanceof Geometry;
        Object oRow = aFeature.getAttribute(DatamodelDataStore.ROW_ATTR_NAME);
        assert oRow instanceof Row;
        Geometry geom = (Geometry) oGeometry;
        String geometryAttrName = aFeature.getFeatureType().getGeometryDescriptor().getLocalName();
        int geometryColIndex = rs.getFields().find(geometryAttrName);
        if (geom instanceof MultiPolygon
                || geom instanceof MultiLineString
                || geom instanceof MultiPoint) {
            MultiPolygon mPolygon = (MultiPolygon) geom;
            for (int g = 0; g < mPolygon.getNumGeometries(); g++) {
                Geometry section = mPolygon.getGeometryN(g);
                Coordinate[] coordinates = section.getCoordinates();
                for (int i = 0; i < coordinates.length; i++) {
                    SelectionEntry entry = new SelectionEntry(entity.getEntityId(), (Row) oRow, featureID, geometryColIndex, g, i, coordinates[i]);
                    aDestination.add(entry);
                }
            }
        } else {
            Coordinate[] coordinates = geom.getCoordinates();
            for (int i = 0; i < coordinates.length; i++) {
                SelectionEntry entry = new SelectionEntry(entity.getEntityId(), (Row) oRow, featureID, geometryColIndex, -1, i, coordinates[i]);
                aDestination.add(entry);
            }
        }
    }

    public static Set<Geometry> convertSelectionEntries2Geometries(List<SelectionEntry> aSelection) throws Exception {
        Set<Geometry> collectedGeometries = new HashSet<>();
        for (SelectionEntry sEntry : aSelection) {
            collectedGeometries.add((Geometry) sEntry.getRow().getColumnObject(sEntry.getGeometryColIndex()));
        }
        return collectedGeometries;
    }

    public static LineString createLineString(Coordinate[] aCoordinates) {
        CoordinateSequence cSeq = csFactory.create(aCoordinates);
        return new LineString(cSeq, gFactory);
    }

    public static Polygon createPolygon(Coordinate[] aCoordinates) {
        if (!aCoordinates[0].equals(aCoordinates[aCoordinates.length - 1])) {
            aCoordinates = Arrays.copyOf(aCoordinates, aCoordinates.length + 1);
            aCoordinates[aCoordinates.length - 1] = aCoordinates[0];
        }
        CoordinateSequence cSeq = csFactory.create(aCoordinates);
        return gFactory.createPolygon(new LinearRing(cSeq, gFactory), null);
    }

    public static MultiPoint createMultiPoint(Coordinate[] aCoordinates) {
        return gFactory.createMultiPoint(aCoordinates);
    }

    public static MultiLineString createMultiLineString(List<Coordinate[]> aData) {
        LineString[] lineStrings = new LineString[aData.size()];
        for (int i = 0; i < lineStrings.length; i++) {
            lineStrings[i] = createLineString(aData.get(i));
        }
        return gFactory.createMultiLineString(lineStrings);
    }

    public static MultiPolygon createMultiPolygon(List<Coordinate[]> aData) {
        Polygon[] polygons = new Polygon[aData.size()];
        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = createPolygon(aData.get(i));
        }
        return gFactory.createMultiPolygon(polygons);
    }

    public static boolean isValidGeometryDataSection(Coordinate[] aSection, Class<?> aGeometryClass) {
        if (Point.class.isAssignableFrom(aGeometryClass) || MultiPoint.class.isAssignableFrom(aGeometryClass)) {
            return aSection.length == 1;
        } else if (LineString.class.isAssignableFrom(aGeometryClass) || MultiLineString.class.isAssignableFrom(aGeometryClass)) {
            return aSection.length >= 2;
        } else if (Polygon.class.isAssignableFrom(aGeometryClass) || MultiPolygon.class.isAssignableFrom(aGeometryClass)) {
            return aSection.length >= 4;
        } else {
            return false;
        }
    }

    public static boolean isValidGeometryData(List<Coordinate[]> aData, Class<?> aGeometryClass) {
        if (Point.class.isAssignableFrom(aGeometryClass)
                || LineString.class.isAssignableFrom(aGeometryClass)
                || Polygon.class.isAssignableFrom(aGeometryClass)) {
            return aData.size() == 1 && isValidGeometryDataSection(aData.get(0), aGeometryClass);
        } else if (MultiPoint.class.isAssignableFrom(aGeometryClass)
                || MultiLineString.class.isAssignableFrom(aGeometryClass)
                || MultiPolygon.class.isAssignableFrom(aGeometryClass)) {
            if (aData.size() >= 1) {
                for (int i = 0; i < aData.size(); i++) {
                    if (!isValidGeometryDataSection(aData.get(i), aGeometryClass)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static Geometry constructGeometry(List<Coordinate[]> aData, Class<?> aGeometryClass, int aSrid) {
        Geometry g = null;
        if (Point.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            assert aData.get(0).length == 1;
            Point pt = createPoint(aData.get(0)[0]);
            g = pt;
        } else if (LineString.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            LineString ls = createLineString(aData.get(0));
            g = ls;
        } else if (Polygon.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            Coordinate[] coordinates = aData.get(0);
            coordinates[coordinates.length - 1].x = coordinates[0].x;
            coordinates[coordinates.length - 1].y = coordinates[0].y;
            Polygon poly = createPolygon(coordinates);
            g = poly;
        } else if (MultiPoint.class.isAssignableFrom(aGeometryClass)) {
            Coordinate[] mpData = new Coordinate[aData.size()];
            for (int i = 0; i < mpData.length; i++) {
                Coordinate[] pData = aData.get(i);
                assert pData.length == 1;
                mpData[i] = pData[0];
            }
            MultiPoint mp = createMultiPoint(mpData);
            g = mp;
        } else if (MultiLineString.class.isAssignableFrom(aGeometryClass)) {
            MultiLineString mls = createMultiLineString(aData);
            g = mls;
        } else if (MultiPolygon.class.isAssignableFrom(aGeometryClass)) {
            MultiPolygon mp = createMultiPolygon(aData);
            g = mp;
        }
        g.setSRID(aSrid);
        return g;
    }

    public static void packNullsInGeometryData(List<Coordinate[]> aCoordsCloud, Class<?> aGeometryClass) {
        for (int s = aCoordsCloud.size() - 1; s >= 0; s--) {
            Coordinate[] sectionWithNulls = aCoordsCloud.get(s);
            List<Coordinate> alSection = new ArrayList<>();
            for (int c = 0; c < sectionWithNulls.length; c++) {
                if (sectionWithNulls[c] != null) {
                    alSection.add(sectionWithNulls[c]);
                }
            }
            Coordinate[] section = new Coordinate[alSection.size()];
            alSection.toArray(section);
            if (!isValidGeometryDataSection(section, aGeometryClass)) {
                aCoordsCloud.remove(s);
            } else {
                aCoordsCloud.set(s, section);
            }
        }
    }

    public static boolean inject(Style aStyle, Graphic aGraphic) {
        if (aStyle != null) {
            List<FeatureTypeStyle> fstyles = aStyle.featureTypeStyles();
            if (fstyles != null && fstyles.size() > 0) {
                FeatureTypeStyle fstyle = fstyles.get(0);
                if (fstyle != null) {
                    List<Rule> rules = fstyle.rules();
                    if (rules != null && rules.size() > 0) {
                        Rule rule = rules.get(0);
                        if (rule != null) {
                            Symbolizer[] symbolizers = rule.getSymbolizers();
                            for (Symbolizer symbolizer : symbolizers) {
                                if (symbolizer instanceof PointSymbolizer) {
                                    PointSymbolizer ps = (PointSymbolizer) symbolizers[0];
                                    ps.setGraphic(aGraphic);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Graphic createGraphic(URL aUrl, String mimeType) {
        ExternalGraphic externalGraphic = sb.createExternalGraphic(aUrl, mimeType);
        return sb.createGraphic(externalGraphic, null, null);
    }

    public static Style buildSelectionPhantomStyle() {
        Stroke stroke = sb.createStroke(Color.darkGray, 1, new float[]{7, 5});
        stroke.setOpacity(ff.literal(0.8f));
        LineSymbolizer ls = sf.createLineSymbolizer(stroke, null);
        Style style = sf.createStyle();
        Rule rule1 = sf.createRule();
        rule1.symbolizers().add(ls);
        Rule[] rules = new Rule[]{rule1};
        FeatureTypeStyle tStyle = sf.createFeatureTypeStyle(rules);
        style.featureTypeStyles().add(tStyle);
        return style;
    }

    public static Style buildSelectionStyle() {
        FeatureStyleDescriptor styleHelper = new FeatureStyleDescriptor();
        styleHelper.setFillColor(Color.green.brighter());
        styleHelper.setLineColor(Color.black);
        styleHelper.setSize(5.0f);
        styleHelper.setPointSymbol(PointSymbol.SQUARE);
        return styleHelper.buildStyle(SelectionEntry.SELECTION_ENTRY_GEOMETRY_BINDING_CLASS);
    }
}

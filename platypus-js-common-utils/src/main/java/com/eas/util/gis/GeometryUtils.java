/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.gis;

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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Andrew
 */
public class GeometryUtils {

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

    public static Polygon createPolygonWithHoles(Polygon aPolygon, Geometry[] aHoles) {
        CoordinateSequence polygonSeq = csFactory.create(getPolygonShell(aPolygon).getCoordinates());
        LinearRing shell = new LinearRing(polygonSeq, gFactory);
        List<LinearRing> holes = new ArrayList<>();
        if (aHoles != null) {
            for (Geometry aHole : aHoles) {
                Coordinate[] coord = aHole.getCoordinates();
                if (coord.length > 1) {
                    CoordinateSequence holeSeq = csFactory.create(coord);
                    holes.add(new LinearRing(holeSeq, gFactory));
                }
            }
        }
        LinearRing[] arHoles = new LinearRing[0];
        return gFactory.createPolygon(shell, holes.toArray(arHoles));
    }

    public static Polygon createPolygonWithHoles(Polygon aPolygon, Geometry[] aHoles, int aSrid) {
        Polygon polygon = createPolygonWithHoles(aPolygon, aHoles);
        polygon.setSRID(aSrid);
        return polygon;
    }

    public static Polygon getPolygonShell(Polygon aPolygon) {
        if (aPolygon != null) {
            return gFactory.createPolygon(aPolygon.getExteriorRing().getCoordinates());
        }
        return null;
    }

    public static Polygon[] getPolygonHoles(Polygon aPolygon) {
        if (aPolygon != null) {
            Polygon[] holes = new Polygon[aPolygon.getNumInteriorRing()];
            for (int i = 0; i < aPolygon.getNumInteriorRing(); i++) {
                Coordinate[] coord = aPolygon.getInteriorRingN(i).getCoordinates();
                if (coord.length > 1) {
                    holes[i] = gFactory.createPolygon(coord);
                }
            }
            return holes;
        }
        return null;
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

    public static MultiPoint createMultiPoint(Point[] aPoints) {
        return gFactory.createMultiPoint(aPoints);
    }

    public static MultiLineString createMultiLineString(List<Geometry> aData) {
        LineString[] lineStrings = new LineString[aData.size()];
        for (int i = 0; i < lineStrings.length; i++) {
            assert aData.get(i) instanceof LineString;
            lineStrings[i] = (LineString) aData.get(i);
        }
        return gFactory.createMultiLineString(lineStrings);
    }

    public static MultiPolygon createMultiPolygon(List<Geometry> aData) {
        Polygon[] polygons = new Polygon[aData.size()];
        for (int i = 0; i < polygons.length; i++) {
            assert aData.get(i) instanceof Polygon;
            polygons[i] = (Polygon) aData.get(i);
        }
        return gFactory.createMultiPolygon(polygons);
    }

    public static boolean isValidGeometryDataSection(Coordinate[] aSection, Class aGeometryClass) {
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

    public static boolean isValidGeometryData(List<Geometry> aData, Class aGeometryClass) {
        if (Point.class.isAssignableFrom(aGeometryClass)
                || LineString.class.isAssignableFrom(aGeometryClass)
                || Polygon.class.isAssignableFrom(aGeometryClass)) {
            return aData.size() == 1 && isValidGeometryDataSection(aData.get(0).getCoordinates(), aGeometryClass);
        } else if (MultiPoint.class.isAssignableFrom(aGeometryClass)
                || MultiLineString.class.isAssignableFrom(aGeometryClass)
                || MultiPolygon.class.isAssignableFrom(aGeometryClass)) {
            if (aData.size() >= 1) {
                for (int i = 0; i < aData.size(); i++) {
                    if (!isValidGeometryDataSection(aData.get(i).getCoordinates(), aGeometryClass)) {
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

    public static Geometry constructGeometry(List<Geometry> aData, Class aGeometryClass, int aSrid) {
        Geometry g = null;
        if (Point.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            assert aData.get(0).getCoordinates().length == 1;
            g = aData.get(0);
        } else if (LineString.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            g = aData.get(0);
        } else if (Polygon.class.isAssignableFrom(aGeometryClass)) {
            assert aData.size() == 1;
            g = aData.get(0);
        } else if (MultiPoint.class.isAssignableFrom(aGeometryClass)) {
            Point[] mpData = new Point[aData.size()];
            for (int i = 0; i < mpData.length; i++) {
                assert aData.get(i) instanceof Point;
                mpData[i] = (Point) aData.get(i);
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
        if (g != null) {
            g.setSRID(aSrid);
        }
        return g;
    }

    public static Geometry constructGeometry(Coordinate[] aCoordinates, Class aGeometryClass) {
        Geometry g = null;
        if (isValidGeometryDataSection(aCoordinates, aGeometryClass)) {
            if (Point.class.isAssignableFrom(aGeometryClass)) {
                g = gFactory.createPoint(aCoordinates[0]);
            } else if (LineString.class.isAssignableFrom(aGeometryClass)) {
                g = gFactory.createLineString(aCoordinates);
            } else if (Polygon.class.isAssignableFrom(aGeometryClass)) {
                g = gFactory.createPolygon(aCoordinates);
            } else if (MultiPoint.class.isAssignableFrom(aGeometryClass)) {
                g = gFactory.createMultiPoint(aCoordinates);
            }
            return g;
        }
        return null;
    }

    public static Coordinate[] deletePointsFromCoordinates(Geometry aGeometry, List<Integer> aDeleteCoordinates) {
        if (aGeometry != null && aDeleteCoordinates != null && aDeleteCoordinates.size() > 0) {
            List<Coordinate> lst = new LinkedList<>(Arrays.asList(aGeometry.getCoordinates()));
            for (int coordIndex : aDeleteCoordinates) {
                lst.remove(coordIndex);
            }
            if (aGeometry instanceof Polygon) {
                if (lst.size() > 1 && !lst.get(0).equals(lst.get(lst.size() - 1))) {
                    lst.add(lst.get(0));
                }
            }
            Coordinate[] section = new Coordinate[lst.size()];
            return lst.toArray(section);
        }
        return null;
    }
}

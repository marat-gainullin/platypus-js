/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.mousetools;

import com.bearsoft.rowset.Row;
import com.eas.client.controls.geopane.mousetools.GeoPaneTool;
import com.eas.client.controls.geopane.mousetools.MouseToolCapability;
import com.eas.client.geo.GisUtilities;
import com.eas.client.geo.datastore.RowsFeatureSource;
import com.eas.client.geo.selectiondatastore.SelectionEntry;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.map.DbMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.map.MapLayer;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author mg
 */
public class MousePointsAdder extends MapTool {

    private class Distance {

        private double distance;
        private int center;

        public Distance(double aDistance, int aCenter) {
            distance = aDistance;
            center = aCenter;
        }

        /**
         * @return the distance
         */
        public double getDistance() {
            return distance;
        }

        /**
         * @return the center
         */
        public int getCenter() {
            return center;
        }
    }
    protected GeoPaneTool selector;

    public MousePointsAdder(DbMap aMap, GeoPaneTool aSelector) {
        super(aMap);
        selector = aSelector;
    }

    private Distance checkGeomertysDistance(Coordinate[] aCoordinates, Point aPoint) {
        double distance = Double.MAX_VALUE;
        int centerIdx = -1;
        for (int c = 1; c < aCoordinates.length; c++) {
            LineString ls = GisUtilities.createLineString(GisUtilities.createPoint(aCoordinates[c - 1]), GisUtilities.createPoint(aCoordinates[c]));
            double ldistance = ls.distance(aPoint);
            if (ldistance < distance) {
                distance = ldistance;
                centerIdx = c;
            }
        }
        return new Distance(distance, centerIdx);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Point2D.Double cartesianPt = pane.awtScreen2Cartesian(e.getPoint());
            Point2D.Double geoPt = pane.cartesian2Geo(cartesianPt);
            Point point2Add = GisUtilities.createPoint(geoPt);
            if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
                if (map.getSelection().isEmpty()) {
                    selector.mouseClicked(e);
                } else if (map.hitSelection(point2Add).isEmpty()) {
                    List<SelectionEntry> sEntries2Edit = map.getSelection().getSelection();
                    assert sEntries2Edit != null;
                    Map<String, Row> rows = new HashMap<>();
                    Map<String, Integer> rowsColIndexes = new HashMap<>();
                    Map<String, Geometry> oldGeometries = new HashMap<>();
                    Map<String, ApplicationEntity<?, ?, ?>> features2Entities = new HashMap<>();
                    for (SelectionEntry sEntry : sEntries2Edit) {
                        Row row = sEntry.getRow();
                        Object oGeometry = row.getColumnObject(sEntry.getGeometryColIndex());
                        assert oGeometry instanceof Geometry;
                        Geometry geometry = (Geometry) oGeometry;
                        rows.put(sEntry.getFeatureId(), row);
                        oldGeometries.put(sEntry.getFeatureId(), geometry);
                        rowsColIndexes.put(sEntry.getFeatureId(), sEntry.getGeometryColIndex());
                        features2Entities.put(sEntry.getFeatureId(), sEntry.getEntity());
                    }
                    double distance = Double.MAX_VALUE;
                    Geometry closestGeometry = null;
                    int cIdx = -1;
                    int gIdx = -1;
                    int gHoleIdx = -1;
                    String featureId2InsertTo = null;
                    for (String fKey : oldGeometries.keySet()) {
                        Geometry geom = oldGeometries.get(fKey);
                        for (int i = 0; i < geom.getNumGeometries(); i++) {
                            Geometry testGeom = geom.getGeometryN(i);
                            double lDistance = Double.MAX_VALUE;
                            int centerIdx = -1;
                            int holeIdx = -1;
                            if (testGeom instanceof Polygon && ((Polygon) testGeom).getNumInteriorRing() > 0) {
                                Polygon polygon = ((Polygon) testGeom);
                                Coordinate[] shellCoord = polygon.getExteriorRing().getCoordinates();
                                Distance minShellDist = checkGeomertysDistance(shellCoord, point2Add);
                                if (minShellDist.getDistance() < lDistance) {
                                    lDistance = minShellDist.getDistance();
                                    centerIdx = minShellDist.getCenter();
                                }
                                for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                                    Distance minHoleDist = checkGeomertysDistance(polygon.getInteriorRingN(j).getCoordinates(), point2Add);
                                    if (minHoleDist.getDistance() < lDistance) {
                                        lDistance = minHoleDist.getDistance();
                                        centerIdx = minHoleDist.getCenter();
                                        holeIdx = j;
                                    }
                                }
                            } else {
                                Distance lDist = checkGeomertysDistance(geom.getGeometryN(i).getCoordinates(), point2Add);
                                if (lDist.getDistance() < lDistance) {
                                    lDistance = lDist.getDistance();
                                    centerIdx = lDist.getCenter();
                                }
                            }
                                if (lDistance < distance) {
                                    distance = lDistance;
                                    closestGeometry = geom;
                                    gIdx = i;
                                cIdx = centerIdx;
                                gHoleIdx = holeIdx;
                                    featureId2InsertTo = fKey;
                                }

                        }
                    }
                    List<Geometry> gData = new ArrayList<>();
                    for (int i = 0; i < closestGeometry.getNumGeometries(); i++) {
                        Geometry innerGeometry = closestGeometry.getGeometryN(i);
                        if (i == gIdx) {
                            Coordinate[] coords;
                            if (innerGeometry instanceof Polygon && ((Polygon) innerGeometry).getNumInteriorRing() > 0) {
                                if (gHoleIdx > -1) {
                                    coords = ((Polygon) innerGeometry).getInteriorRingN(gHoleIdx).getCoordinates();
                                } else {
                                    coords = ((Polygon) innerGeometry).getExteriorRing().getCoordinates();
                                }
                            } else {
                                coords = innerGeometry.getCoordinates();
                            }

                            Coordinate[] head = Arrays.copyOfRange(coords, 0, cIdx);
                            Coordinate[] tail = Arrays.copyOfRange(coords, cIdx, coords.length);
                            Coordinate[] newCoordinates = new Coordinate[coords.length + 1];
                            int j = 0;
                            for (int h = 0; h < head.length; h++) {
                                newCoordinates[j++] = head[h];
                            }
                            newCoordinates[j++] = point2Add.getCoordinate();
                            for (int t = 0; t < tail.length; t++) {
                                newCoordinates[j++] = tail[t];
                            }
                            Geometry newGeom;
                            if (innerGeometry instanceof Polygon && ((Polygon) innerGeometry).getNumInteriorRing() > 0) {
                                Polygon polygon = ((Polygon) innerGeometry);
                                Geometry[] holes = GisUtilities.getPolygonHoles(polygon);
                                Polygon shell;
                                if (gHoleIdx > -1) {
                                    holes[gHoleIdx] = GisUtilities.createPolygon(newCoordinates);
                                    shell = GisUtilities.getPolygonShell(polygon);
                                } else {
                                    shell = GisUtilities.createPolygon(newCoordinates);
                                }
                                newGeom = GisUtilities.createPolygonWithHoles(shell, holes);
                            } else {
                                newGeom = GisUtilities.constructGeometry(newCoordinates, innerGeometry.getClass());
                            }
                            gData.add(newGeom);
                        } else {
                            gData.add(innerGeometry);
                        }
                    }
                    for (SelectionEntry sEntry : sEntries2Edit) {
                        if (sEntry.getFeatureId().equals(featureId2InsertTo)
                                && sEntry.getGeometryOfInterestIndex() == gIdx
                                && sEntry.getCoordinateOfInterestIndex() >= cIdx) {
                            sEntry.setCoordinateOfInterestIndex(sEntry.getCoordinateOfInterestIndex() + 1);
                        }
                    }

                    // new geometries construction and assigning
                    Row row2Update = rows.get(featureId2InsertTo);
                    assert GisUtilities.isValidGeometryData(gData, closestGeometry.getClass());
                    row2Update.setColumnObject(rowsColIndexes.get(featureId2InsertTo), GisUtilities.constructGeometry(gData, closestGeometry.getClass(), closestGeometry.getSRID()));
                    features2Entities.get(featureId2InsertTo).getRowset().setModified(true);
                    map.getSelection().clear();
                    map.getSelection().getSelection().add(new SelectionEntry(features2Entities.get(featureId2InsertTo), rows.get(featureId2InsertTo), featureId2InsertTo, rowsColIndexes.get(featureId2InsertTo), closestGeometry.getNumGeometries() > 1 ? gIdx : -1, cIdx, gHoleIdx, point2Add.getCoordinate()));
                    map.fireSelectionChanged();
                    // Let's fire feature chaned event to all editabble layers
                    MapLayer[] lightLayers = map.getPane().getLightweightMapContext().getLayers();
                    for(MapLayer layer:lightLayers)
                    {
                        FeatureSource<? extends FeatureType, ? extends Feature> source = layer.getFeatureSource();
                        assert source instanceof RowsFeatureSource;
                        RowsFeatureSource rowsSource = (RowsFeatureSource)source;
                        rowsSource.fireFeaturesChanged();
                    }
                }
            } else {
                map.getSelection().clear();
            }
        } catch (Exception ex) {
            Logger.getLogger(MousePointsAdder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isCapable(MouseToolCapability aCapability) {
        return aCapability == MouseToolCapability.BUTTONS;
    }
}

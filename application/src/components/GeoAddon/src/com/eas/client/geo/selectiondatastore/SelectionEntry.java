/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.geo.selectiondatastore;

import com.bearsoft.rowset.Row;
import com.eas.client.geo.GisUtilities;
import com.eas.client.model.application.ApplicationEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 *
 * @author mg
 */
public class SelectionEntry {

    public static final Class<? extends Geometry> SELECTION_ENTRY_GEOMETRY_BINDING_CLASS = Point.class;
    public static final Class<?> SELECTION_PHANTOM_GEOMETRY_BINDING_CLASS = Geometry.class;
    public static final String VIEW_SHAPE_ATTR_NAME = "viewShape";
    public static final String ENTITY_ID_ATTR_NAME = "entityId";
    public static final String GEOM_COL_INDEX_ATTR_NAME = "geometryColIndex";
    public static final String MULTI_GEOMETRY_INDEX_ATTR_NAME = "geometryOfInterestIndex";
    public static final String COORDINATE_INDEX_ATTR_NAME = "coordinateOfInterestIndex";
    public static final String THIS_ENTRY_ATTR_NAME = "this";
    // data
    protected ApplicationEntity<?, ?, ?> entity; // Required field
    protected Row row; // Required field
    protected String featureId; // Required field
    // geometry column index
    // It's possible that one row will comprise more than one shape
    protected Integer geometryColIndex;  // Required field
    protected Integer geometryOfInterestIndex; // Required field
    protected Integer coordinateOfInterestIndex; // Required field
    private Integer holeOfInterestIndex; // Required field
    // view
    protected Point viewShape;

    public SelectionEntry(ApplicationEntity<?, ?, ?> aEntity, Row aRow, String aFeatureId, int aGeometryColIndex, int aGeometryOfInterestIndex, int aCoordinateOfInterestIndex, int aHoleOfInterest, Coordinate aCoordinate)
    {
        super();
        entity = aEntity;
        row = aRow;
        featureId = aFeatureId;
        geometryColIndex = aGeometryColIndex;
        geometryOfInterestIndex = aGeometryOfInterestIndex;
        coordinateOfInterestIndex = aCoordinateOfInterestIndex;
        holeOfInterestIndex = aHoleOfInterest;
        viewShape = GisUtilities.createPoint(aCoordinate);
    }

    public Row getRow() {
        return row;
    }

    public String getFeatureId() {
        return featureId;
    }

    public Integer getGeometryColIndex() {
        return geometryColIndex;
    }

    public Integer getGeometryOfInterestIndex() {
        return geometryOfInterestIndex;
    }

    public Integer getCoordinateOfInterestIndex() {
        return coordinateOfInterestIndex;
    }

    public Point getViewShape() {
        return viewShape;
    }

    public void setViewShape(Point aViewShape) {
        viewShape = aViewShape;
    }

    public ApplicationEntity<?, ?, ?> getEntity() {
        return entity;
    }

    public void setCoordinateOfInterestIndex(int aIdx) {
        coordinateOfInterestIndex = aIdx;
    }

    /**
     * @return the holeOfInterestIndex
     */
    public Integer getHoleOfInterestIndex() {
        return holeOfInterestIndex;
    }

    /**
     * @param aHoleOfInterestIndex the holeOfInterestIndex to set
     */
    public void setHoleOfInterestIndex(int aHoleOfInterestIndex) {
        holeOfInterestIndex = aHoleOfInterestIndex;
    }
}

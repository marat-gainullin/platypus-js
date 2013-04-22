/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view;

import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import com.eas.client.model.gui.view.visibilitygraph.PathedVertex;
import com.eas.client.model.gui.view.visibilitygraph.Segment;
import com.eas.client.model.gui.view.visibilitygraph.VerticalEdgedVertex;
import com.eas.client.model.gui.view.visibilitygraph.VisibilityGraph;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class PathsFinder<E extends Entity<?, ?, E>, EV extends EntityView<E>> {

    protected static final double FLOAT_TOLERANCE = 1.0E-10;
    public static final int MAX_PATH_ITERATIONS = 1000;
    protected static final int POINTS_PER_ARC = 5;
    public static final int LEFT_2_RIGHT_SEGMENT = 0;
    public static final int RIGHT_2_LEFT_SEGMENT = 1;
    public static final int TOP_2_BOTTOM_SEGMENT = 2;
    public static final int BOTTOM_2_TOP_SEGMENT = 3;

    public boolean insetsContains(Rectangle rt) {
        return isValid() ? insetsIdx.contains(rt) : false;
    }

    public boolean isValid() {
        return (insetsIdx != null);
    }

    public void clearOccupiedCells() {
        horizontalCalcedArcs.clear();
        verticalCalcedArcs.clear();
        connectorsOccupiedCells.clear();
    }

    protected Set<List<Point>> add2Occupied(String occKey, Point pt1, Point pt2, boolean isSlot) {
        List<Point> lsegment = null;
        if (isSlot) {
            lsegment = new SlotVector<>();
        } else {
            lsegment = new ArrayList<>();
        }
        lsegment.add(pt1);
        lsegment.add(pt2);
        Set<List<Point>> lsegments = connectorsOccupiedCells.get(occKey);
        if (lsegments == null) {
            lsegments = new HashSet<>();
            connectorsOccupiedCells.put(occKey, lsegments);
        }
        if (!lsegments.contains(lsegment)) {
            lsegments.add(lsegment);
        }
        return lsegments;
    }

    protected boolean isIntersecting(Point fpt, Point lpt, List<Point> possibleIntersector) {
        if (fpt != null && lpt != null && possibleIntersector != null && possibleIntersector.size() == 2) {
            Point lfpt = possibleIntersector.get(0);
            Point llpt = possibleIntersector.get(1);
            if (!fpt.equals(lfpt) && !fpt.equals(llpt) && !lpt.equals(lfpt) && !lpt.equals(llpt)) {
                if (possibleIntersector instanceof SlotVector) {
                    if (fpt.y == lpt.y && lfpt.x == llpt.x) {
                        return (((lfpt.y <= fpt.y && fpt.y <= llpt.y) || (llpt.y <= fpt.y && fpt.y <= lfpt.y))
                                && ((fpt.x <= lfpt.x && lfpt.x <= lpt.x) || (lpt.x <= lfpt.x && lfpt.x <= fpt.x)));
                    } else if (fpt.x == lpt.x && lfpt.y == llpt.y) {
                        return (((fpt.y <= lfpt.y && lfpt.y <= lpt.y) || (lpt.y <= lfpt.y && lfpt.y <= fpt.y))
                                && ((lfpt.x <= fpt.x && fpt.x <= llpt.x) || (llpt.x <= fpt.x && fpt.x <= lfpt.x)));
                    }
                } else {
                    if (fpt.y == lpt.y && lfpt.x == llpt.x) {
                        return (((lfpt.y < fpt.y && fpt.y < llpt.y) || (llpt.y < fpt.y && fpt.y < lfpt.y))
                                && ((fpt.x < lfpt.x && lfpt.x < lpt.x) || (lpt.x < lfpt.x && lfpt.x < fpt.x)));
                    } else if (fpt.x == lpt.x && lfpt.y == llpt.y) {
                        return (((fpt.y < lfpt.y && lfpt.y < lpt.y) || (lpt.y < lfpt.y && lfpt.y < fpt.y))
                                && ((lfpt.x < fpt.x && fpt.x < llpt.x) || (llpt.x < fpt.x && fpt.x < lfpt.x)));
                    }
                }
            }
        }
        return false;
    }
    protected static final int MAX_BUS_ITERATIONS = 10;
    protected static final int BUS_OFFSET = EntityView.INSET_ZONE + 5;

    protected boolean isAnyCoaxialSegmentRect(Set<ConnectorSegmentRectangle> aObstacles, ConnectorSegmentRectangle aRect2Test) {
        if (aObstacles != null && !aObstacles.isEmpty() && aRect2Test != null) {
            for (ConnectorSegmentRectangle o : aObstacles) {
                if (o != null) {
                    if (aRect2Test.getRelation() != o.getRelation()) {
                        if ((aRect2Test.width == 0 && o.width == 0 && aRect2Test.x == o.x)
                                || (aRect2Test.height == 0 && o.height == 0 && aRect2Test.y == o.y)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void arcConnectors(Set<Relation<E>> rels) {
        if (rels != null) {
            for (Relation<E> rel : rels) {
                if (rel != null) {
                    RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                    List<Point> lconnector = designInfo.getConnector();
                    if (lconnector != null) {
                        List<EstimatedArc> larcs = arcConnectorWithOccupied(lconnector);
                        if (larcs != null && !larcs.isEmpty()) {
                            designInfo.setConnectorEstimatedArcs(larcs);
                        }
                    }
                }
            }
        }
    }

    private class SlotVector<E> extends ArrayList<E> {
    }

    public void addSlot2Occupied(Point pt1, Point pt2) {
        int lTopAeY = calcTopAeY(pt2.y);
        int lBottomAeY = calcBottomAeY(pt2.y);

        int lLeftAeX = calcLeftAeX(pt2.x);
        int lRightAeX = calcRightAeX(pt2.x);

        String occKey = String.valueOf((int) (((long) lLeftAeX + (long) lRightAeX) / 2)) + "x" + String.valueOf((int) ((long) lTopAeY + (long) lBottomAeY) / 2);
        add2Occupied(occKey, pt1, pt2, true);
    }

    public class ConnectorSegmentRectangle extends Rectangle {

        protected Relation<E> relation = null;

        public ConnectorSegmentRectangle(Relation<E> aRel, Rectangle aRect) {
            super(aRect);
            relation = aRel;
        }

        public ConnectorSegmentRectangle(ConnectorSegmentRectangle aCSRect) {
            super(aCSRect);
            relation = aCSRect.getRelation();
        }

        public Relation<E> getRelation() {
            return relation;
        }

        public void offset(int dx, int dy) {
            x += dx;
            y += dy;
        }
    }

    protected ConnectorSegmentRectangle twoPoints2Rect(Point pt1, Point pt2, Relation<E> rel) {
        return new ConnectorSegmentRectangle(rel, VisibilityGraph.points2Rect(pt1, pt2));
    }

    public void busConnectors(Set<Relation<E>> rels) {
        if (busedSegments != null) {
            busedSegments.clear();
            if (rels != null && !rels.isEmpty()) {
                for (Relation<E> rel : rels) {
                    if (rel != null) {
                        RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                        List<Point> lconnector = designInfo.getConnector();
                        if (lconnector != null && !lconnector.isEmpty()) {
                            lconnector.add(0, new Point(lconnector.get(0)));
                            lconnector.add(new Point(lconnector.get(lconnector.size() - 1)));
                            busConnector(rel, lconnector);
                        }
                    }
                }
            }
        }
    }

    protected boolean isSegmentIntersectingObstacles(ConnectorSegmentRectangle aSegment) {
        if (aSegment != null && aSegment.height == 0) {
            Rectangle mSegment = new Rectangle(aSegment);
            mSegment.grow(-1, 0);
            return insetsIdx.contains(mSegment);
        } else {
            return insetsIdx.contains(aSegment);
        }
    }
    // Before applying this function we need to add dummy points in
    // second (copy of first point) and prelast (copy of last point) positions.

    protected void busConnector(Relation<E> aRel, List<Point> aPoints) {
        if (aPoints != null && aRel != null) {
            for (int i = 2; i < aPoints.size() - 1; i++) {
                Point pt1 = aPoints.get(i - 1);
                Point pt2 = aPoints.get(i);
                ConnectorSegmentRectangle segmentRect = twoPoints2Rect(pt1, pt2, aRel);
                busedSegments.put(segmentRect, segmentRect);
                /*
                ConnectorSegmentRectangle movedSegmentRect = new ConnectorSegmentRectangle(segmentRect);
                int ldistance = VisibilityGraph.calcOrthoDistance(pt1, pt2);
                int loffset = BUS_OFFSET;
                int j = 0;
                if (ldistance > 3 * BUS_OFFSET) {
                while (j < 2 * MAX_BUS_ITERATIONS) {
                if (j != MAX_BUS_ITERATIONS) {
                HashSet<ConnectorSegmentRectangle> lobstacles = busedSegments.get(movedSegmentRect);
                if (!isAnyCoaxialSegmentRect(lobstacles, movedSegmentRect) && !isSegmentIntersectingObstacles(movedSegmentRect)) {
                j = 0;
                break;
                }
                }
                if (pt1.x == pt2.x) {
                movedSegmentRect.offset(loffset, 0);
                if (movedSegmentRect.x <= 0) {
                movedSegmentRect.offset(-loffset, 0);
                }
                } else {
                movedSegmentRect.offset(0, loffset);
                if (movedSegmentRect.y <= 0) {
                movedSegmentRect.offset(0, -loffset);
                }
                }
                if (j == MAX_BUS_ITERATIONS - 1) {
                loffset = -loffset;
                movedSegmentRect = new ConnectorSegmentRectangle(segmentRect);
                }
                j++;
                }
                }
                if (j == 2 * MAX_BUS_ITERATIONS || movedSegmentRect.equals(segmentRect)) {
                busedSegments.put(segmentRect, segmentRect);
                } else {
                if (movedSegmentRect.width == 0) {
                pt1.x = movedSegmentRect.x;
                pt2.x = movedSegmentRect.x;
                } else {
                pt1.y = movedSegmentRect.y;
                pt2.y = movedSegmentRect.y;
                }
                busedSegments.put(movedSegmentRect, movedSegmentRect);
                }
                 */
            }
            if (aPoints.size() > 1) {
                Point pt1 = aPoints.get(0);
                Point pt2 = aPoints.get(1);
                ConnectorSegmentRectangle segmentRect = twoPoints2Rect(pt1, pt2, aRel);
                busedSegments.put(segmentRect, segmentRect);
                pt1 = aPoints.get(aPoints.size() - 2);
                pt2 = aPoints.get(aPoints.size() - 1);
                segmentRect = twoPoints2Rect(pt1, pt2, aRel);
                busedSegments.put(segmentRect, segmentRect);
            }
        }
    }

    public Rectangle calcSlotsSegmentsBounds(Set<ConnectorSegmentRectangle> aSlotsSegments) {
        if (aSlotsSegments != null && !aSlotsSegments.isEmpty()) {
            Rectangle bounds = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (ConnectorSegmentRectangle segment : aSlotsSegments) {
                if (segment.x < bounds.x) {
                    bounds.x = segment.x;
                }
                if (segment.y < bounds.y) {
                    bounds.y = segment.y;
                }
                if (segment.x + segment.width > bounds.width) {
                    bounds.width = (segment.x + segment.width);
                }
                if (segment.y + segment.height > bounds.height) {
                    bounds.height = (segment.y + segment.height);
                }
            }
            bounds.width = bounds.width - bounds.x;
            bounds.height = bounds.height - bounds.y;
            return bounds;
        } else {
            return null;
        }
    }

    public Set<ConnectorSegmentRectangle> hittestRelationsSlots(Point aPt, int aPrecision) {
        Set<ConnectorSegmentRectangle> hitedSlots = new HashSet<>();
        if (aPt != null) {
            EV eView = insetsIdx.get(aPt);
            if (eView != null) {
                Rectangle lcBounds = eView.getBounds();
                if (!lcBounds.contains(aPt)) {
                    E ent = eView.getEntity();
                    if (ent != null) {
                        Set<Relation<E>> rels = ent.getInRelations();
                        if (rels != null && !rels.isEmpty()) {
                            for (Relation<E> rel : rels) {
                                if (rel != null) {
                                    RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                                    Segment slot = designInfo.getLastSlot();
                                    if (slot != null) {
                                        Point pt1 = slot.firstPoint;
                                        Point pt2 = slot.lastPoint;
                                        ConnectorSegmentRectangle segmentRect = twoPoints2Rect(pt1, pt2, rel);
                                        if (segmentRect != null) {
                                            segmentRect.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                                            Rectangle rect2Test = new Rectangle(aPt);
                                            rect2Test.grow(aPrecision, aPrecision);
                                            if (segmentRect.intersects(rect2Test)) {
                                                segmentRect.grow(2, 2);
                                                hitedSlots.add(segmentRect);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return hitedSlots;
    }

    public Set<Relation<E>> hittestRelationsConnectors(Point aPt, int aPrecision) {
        Set<Relation<E>> hittedRels = new HashSet<>();
        Rectangle hitTestingArea = new Rectangle(aPt);
        hitTestingArea.grow(aPrecision, aPrecision);
        Set<ConnectorSegmentRectangle> hitted = busedSegments.get(hitTestingArea);
        if (hitted != null && !hitted.isEmpty()) {
            for (ConnectorSegmentRectangle csr : hitted) {
                if (csr != null && csr.getRelation() != null) {
                    hittedRels.add(csr.getRelation());
                }
            }
        }
        return hittedRels;
    }

    protected Point arcsIntersectingPoint(Point fpt, Point lpt, List<Point> aSegment) {
        Point center = new Point();
        Point lfpt = aSegment.get(0);
        Point llpt = aSegment.get(1);
        if (fpt.y == lpt.y && lfpt.x == llpt.x) {
            center.x = lfpt.x;
            center.y = fpt.y;
        } else {
            center.x = fpt.x;
            center.y = lfpt.y;
        }
        return center;
    }

    protected void checkSegmentLimits(Point fpt, Point lpt, Point pt2Check, int inset2Check) {
        //left to right segment
        if (fpt.x < lpt.x && fpt.y == lpt.y) {
            if ((pt2Check.x - fpt.x) < inset2Check) {
                pt2Check.x = fpt.x + inset2Check;
            }
            if ((lpt.x - pt2Check.x) < inset2Check) {
                pt2Check.x = lpt.x - inset2Check;
            }
        } else //right to left segment
        if (fpt.x > lpt.x && fpt.y == lpt.y) {
            if ((pt2Check.x - lpt.x) < inset2Check) {
                pt2Check.x = lpt.x + inset2Check;
            }
            if ((fpt.x - pt2Check.x) < inset2Check) {
                pt2Check.x = fpt.x - inset2Check;
            }
        } else // top to bottom segment
        if (fpt.y < lpt.y && fpt.x == lpt.x) {
            if ((pt2Check.y - fpt.y) < inset2Check) {
                pt2Check.y = fpt.y + inset2Check;
            }
            if ((lpt.y - pt2Check.y) < inset2Check) {
                pt2Check.y = lpt.y - inset2Check;
            }
        } else // bottom to top segment
        if (fpt.y > lpt.y && fpt.x == lpt.x) {
            if ((pt2Check.y - lpt.y) < inset2Check) {
                pt2Check.y = lpt.y + inset2Check;
            }
            if ((fpt.y - pt2Check.y) < inset2Check) {
                pt2Check.y = fpt.y - inset2Check;
            }
        }
    }

    protected List<List<Point>> sortCalcedArcs(int sortDirection, Point fpt, Point lpt, Set<List<Point>> possibleIntersectors) {
        TreeMap<Integer, List<Point>> lsorter = new TreeMap<>();
        for (List<Point> lsegment : possibleIntersectors) {
            if (isIntersecting(fpt, lpt, lsegment)) {
                switch (sortDirection) {
                    case LEFT_2_RIGHT_SEGMENT:// Left to right segment
                        lsorter.put(new Integer(lsegment.get(0).x), lsegment);
                        break;
                    case RIGHT_2_LEFT_SEGMENT:// Right to left segment
                        lsorter.put(new Integer(lsegment.get(0).x), lsegment);
                        break;
                    case TOP_2_BOTTOM_SEGMENT:// Top to bottom segment
                        lsorter.put(new Integer(lsegment.get(0).y), lsegment);
                        break;
                    case BOTTOM_2_TOP_SEGMENT:// Bottom to top segment
                        lsorter.put(new Integer(lsegment.get(0).y), lsegment);
                        break;
                }
            }
        }
        Set<Entry<Integer, List<Point>>> lset = null;
        if (sortDirection == 0 || sortDirection == 2) {
            lset = lsorter.entrySet();
        } else {
            NavigableMap<Integer, List<Point>> lmap = lsorter.descendingMap();
            if (lmap != null) {
                lset = lmap.entrySet();
            }
        }
        if (lset != null) {
            Iterator<Entry<Integer, List<Point>>> llit = lset.iterator();
            if (llit != null) {
                List<List<Point>> lress = new ArrayList<>();
                while (llit.hasNext()) {
                    Entry<Integer, List<Point>> lentry = llit.next();
                    lress.add(lentry.getValue());
                }
                return lress;
            }
        }
        return null;
    }

    protected int arcsSortDirection(Point fpt, Point lpt) {
        if (fpt.y == lpt.y) //Horizontal segment
        {
            if (fpt.x < lpt.x) // Left to right segment
            {
                return LEFT_2_RIGHT_SEGMENT;
            } else // Right to left segment
            {
                return RIGHT_2_LEFT_SEGMENT;
            }
        } else { // Vertical segment
            if (fpt.y < lpt.y) // Top to bottom segment
            {
                return TOP_2_BOTTOM_SEGMENT;
            } else // Bottom to top segment
            {
                return BOTTOM_2_TOP_SEGMENT;
            }
        }
    }

    protected void arcGenerate(int sortDirection, Point aCenter, EstimatedArc aArc) {
        double angleStep = Math.PI / (POINTS_PER_ARC - 1);
        double angleStart = 0;
        double angleEnd = 0;
        switch (sortDirection) {
            case 0:// Left to right segment
                angleStart = Math.PI;
                angleStep = -angleStep;
                break;
            case 1:// Right to left segment
                angleEnd = Math.PI;
                break;
            case 2:// Top to bottom segment
                // Y axis oriented from top to bottom
                angleStart = -Math.PI / 2;
                angleEnd = Math.PI / 2;
                break;
            case 3:// Bottom to top segment
                // Y axis oriented from top to bottom
                angleStart = Math.PI / 2;
                angleEnd = -Math.PI / 2;
                angleStep = -angleStep;
                break;
        }
        for (double angle = angleStart; aArc.size() < POINTS_PER_ARC; angle += angleStep) {
            if (aArc.size() == POINTS_PER_ARC - 1) {
                angle = angleEnd;
            }
            Point lpt = new Point((int) Math.round(Math.cos(angle) * EntityView.INSET_ZONE) + aCenter.x, (int) Math.round(Math.sin(angle) * EntityView.INSET_ZONE) + aCenter.y);
            aArc.add(lpt);
        }
    }

    protected List<EstimatedArc> arcConnector(int segmentNumber, Point fpt, Point lpt, Set<List<Point>> possibleIntersectors, String cellKey) {
        // possibleIntersectors - contains segments possibly in wrong order.
        // Thus, they shoud be sorted along fpt-lpt segment!
        List<EstimatedArc> larcs = new ArrayList<>();
        int sortDirection = arcsSortDirection(fpt, lpt);
        List<List<Point>> lsorted = sortCalcedArcs(sortDirection, fpt, lpt, possibleIntersectors);
        for (int i = 0; i < lsorted.size(); i++) {
            Point lcenter = arcsIntersectingPoint(fpt, lpt, lsorted.get(i));
            checkSegmentLimits(fpt, lpt, lcenter, EntityView.INSET_ZONE);
            EstimatedArc larc = new EstimatedArc(segmentNumber, lcenter);
            if (!isOrthogonalArc(larc, sortDirection, cellKey)) {
                arcGenerate(sortDirection, lcenter, larc);
                larcs.add(larc);
            }
        }
        return larcs;
    }

    public Set<EV> getInsetsIntersecting(Rectangle aRect) {
        return insetsIdx.get(aRect);
    }

    protected void add2GeneratedArcs(int direction, String cellKey, List<EstimatedArc> aArcs) {
        if (aArcs != null && !aArcs.isEmpty()) {
            Set<EstimatedArc> larcs = null;
            if (direction == LEFT_2_RIGHT_SEGMENT || direction == RIGHT_2_LEFT_SEGMENT) {
                larcs = horizontalCalcedArcs.get(cellKey);
                if (larcs == null) {
                    larcs = new HashSet<>();
                    horizontalCalcedArcs.put(cellKey, larcs);
                }
            } else if (direction == TOP_2_BOTTOM_SEGMENT || direction == BOTTOM_2_TOP_SEGMENT) {
                larcs = verticalCalcedArcs.get(cellKey);
                if (larcs == null) {
                    larcs = new HashSet<>();
                    verticalCalcedArcs.put(cellKey, larcs);
                }
            }
            if (larcs != null) {
                for (int i = 0; i < aArcs.size(); i++) {
                    larcs.add(aArcs.get(i));
                }
            }
        }
    }

    protected boolean isOrthogonalArc(EstimatedArc aArc, int direction, String cellKey) {
        if (aArc != null && cellKey != null) {
            Set<EstimatedArc> larcs = null;
            if (direction == LEFT_2_RIGHT_SEGMENT || direction == RIGHT_2_LEFT_SEGMENT) {
                larcs = verticalCalcedArcs.get(cellKey);
            } else if (direction == TOP_2_BOTTOM_SEGMENT || direction == BOTTOM_2_TOP_SEGMENT) {
                larcs = horizontalCalcedArcs.get(cellKey);
            }
            if (larcs != null) {
                for (EstimatedArc larc : larcs) {
                    Point larcCenter = larc.getCenter();
                    Point arcCenter = aArc.getCenter();
                    if (larcCenter != null && arcCenter != null) {
                        double ldistance = Math.sqrt(Math.pow(larcCenter.x - arcCenter.x, 2) + Math.pow(larcCenter.y - arcCenter.y, 2));
                        if (ldistance <= FLOAT_TOLERANCE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Map<VerticalEdgedVertex, Set<Relation<E>>> sortRelationsByFirstVertex(Set<Relation<E>> aRelations) {
        if (aRelations != null) {
            Map<VerticalEdgedVertex, Set<Relation<E>>> beginingVertices = new HashMap<>();
            for (Relation<E> rel : aRelations) {
                if (rel != null) {
                    RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                    PathedVertex lfv = designInfo.getFirstVertex();
                    if (lfv != null && lfv instanceof VerticalEdgedVertex) {
                        VerticalEdgedVertex fpv = (VerticalEdgedVertex) lfv;
                        if (!beginingVertices.containsKey(fpv)) {
                            beginingVertices.put(fpv, new HashSet<Relation<E>>());
                        }
                        Set<Relation<E>> rels = beginingVertices.get(fpv);
                        rels.add(rel);
                    }
                }
            }
            return beginingVertices;
        }
        return null;
    }

    protected Set<PathedVertex> relsSet2EndVerticesSet(Set<Relation<E>> aRels) {
        Set<PathedVertex> lverts = new HashSet<>();
        if (aRels != null && !aRels.isEmpty()) {
            for (Relation<E> rel : aRels) {
                if (rel != null) {
                    RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                    PathedVertex pv = designInfo.getLastVertex();
                    if (pv != null) {
                        lverts.add(pv);
                    }
                }
            }
        }
        return lverts;
    }

    protected void findConnectorsPaths(Map<VerticalEdgedVertex, Set<Relation<E>>> beginingVertices) {
        if (beginingVertices != null && !beginingVertices.isEmpty()) {
            Set<Entry<VerticalEdgedVertex, Set<Relation<E>>>> mappingsSet = beginingVertices.entrySet();
            if (mappingsSet != null) {
                for (Entry<VerticalEdgedVertex, Set<Relation<E>>> mpE : mappingsSet) {
                    if (mpE != null) {
                        VerticalEdgedVertex pv = mpE.getKey();
                        Set<Relation<E>> toProcessRels = mpE.getValue();
                        if (pv != null && toProcessRels != null && !toProcessRels.isEmpty()) {
                            visibilityGraph.findShortestPaths(pv, relsSet2EndVerticesSet(toProcessRels));
                            for (Relation<E> rel : toProcessRels) {
                                List<Point> lconnector = visibilityGraph.relationPath2Vector(rel);
                                if (lconnector != null) {
                                    RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
                                    designInfo.setConnector(lconnector);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void calcConnectors(Set<Relation<E>> aRelations) {
        if (aRelations != null) {
            Map<VerticalEdgedVertex, Set<Relation<E>>> beginingVertices = sortRelationsByFirstVertex(aRelations);
            findConnectorsPaths(beginingVertices);
        }
    }

    public class AxisElement {

        protected Integer coordinate = null;
        protected Map<Long, EV> views = new HashMap<>();

        public AxisElement() {
            super();
        }

        public AxisElement(Integer aCoordinate) {
            super();
            coordinate = aCoordinate;
        }

        public int getEntitiesViewsSize() {
            if (views != null && !views.isEmpty()) {
                return views.size();
            }
            return 0;
        }

        public Set<Entry<Integer, EV>> getAscendingElements(boolean vertical) {
            if (views != null && !views.isEmpty()) {
                Map<Integer, EV> sorter = new TreeMap<>();
                Set<Entry<Long, EV>> frSet = views.entrySet();
                if (frSet != null) {
                    Iterator<Entry<Long, EV>> frIt = frSet.iterator();
                    if (frIt != null) {
                        while (frIt.hasNext()) {
                            Entry<Long, EV> frE = frIt.next();
                            if (frE != null) {
                                EV frComp = frE.getValue();
                                if (frComp != null) {
                                    Rectangle bounds = frComp.getBounds();
                                    bounds.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
                                    if (vertical) {
                                        sorter.put(bounds.y, frComp);
                                    } else {
                                        sorter.put(bounds.x, frComp);
                                    }
                                }
                            }
                        }
                    }
                }
                return sorter.entrySet();
            }
            return null;
        }

        public boolean isEmpty() {
            return views.isEmpty();
        }

        public int size() {
            return views.size();
        }

        public EV get(Long aEntityId) {
            return views.get(aEntityId);
        }

        public void put(EV aView) {
            Long lKey = aView.getEntityID();
            if (lKey != null && !views.containsKey(lKey)) {
                views.put(lKey, aView);
            }
        }

        public boolean contains(EV aView) {
            return views.containsKey(aView.getEntityID());
        }

        public void remove(EV aView) {
            Long lKey = aView.getEntityID();
            if (lKey != null) {
                views.remove(lKey);
            }
        }

        public Integer getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(Integer aCoordinate) {
            coordinate = aCoordinate;
        }

        public void setCoordinate(int aCoordinate) {
            coordinate = aCoordinate;
        }
    }
    protected ModelView<E, ?, ?> view;
    protected TreeMap<Integer, PathsFinder<E, EV>.AxisElement> xIdx = new TreeMap<>();
    protected TreeMap<Integer, PathsFinder<E, EV>.AxisElement> yIdx = new TreeMap<>();
    protected SpatialRectanglesTree<EV> insetsIdx = null;
    protected Map<String, Set<List<Point>>> connectorsOccupiedCells = new HashMap<>();
    protected Map<String, Set<EstimatedArc>> horizontalCalcedArcs = new HashMap<>();
    protected Map<String, Set<EstimatedArc>> verticalCalcedArcs = new HashMap<>();
    // 
    protected VisibilityGraph<E, EV> visibilityGraph = null;
    protected SpatialRectanglesTree<ConnectorSegmentRectangle> busedSegments = null;
    //
    public static final String CONNECTOR_POINTS_VECTOR = "connectorPointsVector";
    public static final String CONNECTOR_ESTIMATED_ARCS = "connectorEstimatedArcs";

    public PathsFinder(ModelView<E, ?, ?> aView) {
        super();
        view = aView;
    }

    public void setupInsetsIndex(Rectangle aBounds, int epsilon, Map<Long, EV> aObstacles) {
        insetsIdx = new SpatialRectanglesTree<>(aBounds, epsilon);
        Rectangle busedBounds = new Rectangle(aBounds.x, aBounds.y, aBounds.width + 2 * MAX_BUS_ITERATIONS * BUS_OFFSET, aBounds.height + 2 * MAX_BUS_ITERATIONS * BUS_OFFSET);
        busedSegments = new SpatialRectanglesTree<>(busedBounds, epsilon);
    }

    public void paintVisibilityGraph(Graphics2D g2d) {
        if (visibilityGraph != null) {
            visibilityGraph.paint(g2d);
        }
    }

    public void wideIndexes() {
        if (insetsIdx != null && xIdx != null) {
            Rectangle lbounds = insetsIdx.getBounds();

            Integer lx = lbounds.x;
            if (!xIdx.containsKey(lx)) {
                xIdx.put(lx, new AxisElement(lx));
            }

            lx = lbounds.x + lbounds.width + 2 * MAX_BUS_ITERATIONS * BUS_OFFSET;
            if (!xIdx.containsKey(lx)) {
                xIdx.put(lx, new AxisElement(lx));
            }

            Integer ly = lbounds.y;
            if (!yIdx.containsKey(ly)) {
                yIdx.put(ly, new AxisElement(ly));
            }
            ly = lbounds.y + lbounds.height + 2 * MAX_BUS_ITERATIONS * BUS_OFFSET;
            if (!yIdx.containsKey(ly)) {
                yIdx.put(ly, new AxisElement(ly));
            }
        }
    }

    public void put(EV aView) {
        if (isValid()) {
            Rectangle rt = aView.getBounds();
            rt.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            putIntern(aView, rt);
            insetsIdx.put(rt, aView);
        }
    }

    protected void putIntern(EV aView, Rectangle aRect) {
        Integer x1 = new Integer(aRect.x);
        Integer x2 = new Integer(aRect.x + aRect.width);
        AxisElement x1ae = null;
        if (!xIdx.containsKey(x1)) {
            x1ae = new AxisElement(x1);
            xIdx.put(x1, x1ae);
        } else {
            x1ae = xIdx.get(x1);
        }
        assert (x1ae != null);
        x1ae.put(aView);

        AxisElement x2ae = null;
        if (!xIdx.containsKey(x2)) {
            x2ae = new AxisElement(x2);
            xIdx.put(x2, x2ae);
        } else {
            x2ae = xIdx.get(x2);
        }
        assert (x2ae != null);
        x2ae.put(aView);

        Integer y1 = new Integer(aRect.y);
        Integer y2 = new Integer(aRect.y + aRect.height);
        AxisElement y1ae = null;
        if (!yIdx.containsKey(y1)) {
            y1ae = new AxisElement(y1);
            yIdx.put(y1, y1ae);
        } else {
            y1ae = yIdx.get(y1);
        }
        assert (y1ae != null);
        y1ae.put(aView);

        AxisElement y2ae = null;
        if (!yIdx.containsKey(y2)) {
            y2ae = new AxisElement(y2);
            yIdx.put(y2, y2ae);
        } else {
            y2ae = yIdx.get(y2);
        }
        assert (y2ae != null);
        y2ae.put(aView);
    }

    /**
     * Removes all occurances of a <code>aView</code> component from all indexes, relying on it's bounds.
     * Warning! th e component must removed from path finder before it's bounds would be changed.
     * @param aView Component to be removed from path finder indexes.
     */
    public void remove(EV aView) {
        if (isValid()) {
            Rectangle rt = aView.getBounds();
            rt.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            removeIntern(aView, rt);
            insetsIdx.remove(rt, aView);
        }
    }

    protected void removeIntern(EV aView, Rectangle aRect) {
        Integer x1 = aRect.x;
        Integer x2 = aRect.x + aRect.width;
        AxisElement x1ae = xIdx.get(x1);
        if (x1ae != null) {
            x1ae.remove(aView);
            if (x1ae.isEmpty()) {
                xIdx.remove(x1);
            }
        }
        AxisElement x2ae = xIdx.get(x2);
        if (x2ae != null) {
            x2ae.remove(aView);
            if (x2ae.isEmpty()) {
                xIdx.remove(x2);
            }
        }

        Integer y1 = aRect.y;
        Integer y2 = aRect.y + aRect.height;
        AxisElement y1ae = yIdx.get(y1);
        if (y1ae != null) {
            y1ae.remove(aView);
            if (y1ae.isEmpty()) {
                yIdx.remove(y1);
            }
        }

        AxisElement y2ae = yIdx.get(y2);
        if (y2ae != null) {
            y2ae.remove(aView);
            if (y2ae.isEmpty()) {
                yIdx.remove(y2);
            }
        }
    }

    public void rebuildVisibilityGraph(Map<Long, EV> aObstacles) {
        if (insetsIdx != null && xIdx != null) {
            visibilityGraph = new VisibilityGraph<>(view, aObstacles, insetsIdx, xIdx);
            visibilityGraph.rebuild();
        }
    }

    public void clearIndexes() {
        xIdx.clear();
        yIdx.clear();
        if (insetsIdx != null) {
            insetsIdx.clear();
        }
    }

    public Iterator<AxisElement> getXAxis() {
        Collection<AxisElement> lcol = xIdx.values();
        if (lcol != null) {
            return lcol.iterator();
        }
        return null;
    }

    public Iterator<AxisElement> getYAxis() {
        Collection<AxisElement> lcol = yIdx.values();
        if (lcol != null) {
            return lcol.iterator();
        }
        return null;
    }

    public List<Point> calcConnector(ModelView<E, ?, ?> aView, List<Point> fslot, List<Point> lslot, boolean isBothOnTheRight) {
        Point fpt = new Point(fslot.get(1));
        Point lpt = new Point(lslot.get(1));

        List<Point> lConnector = new ArrayList<>();

        Point m1 = new Point();
        Point m2 = new Point();
        calcMiddlePoints(fpt, lpt, m1, m2, isBothOnTheRight);

        lConnector.add(fpt);
        lConnector.add(m1);
        lConnector.add(m2);
        lConnector.add(lpt);
        return lConnector;
    }

    public List<Point> relationPath2Vector(Relation<E> aRel) {
        if (visibilityGraph != null) {
            return visibilityGraph.relationPath2Vector(aRel);
        }
        return null;
    }

    public void calcMiddlePoints(Point fpt, Point lpt, Point m1, Point m2, boolean isBothOnTheRight) {
        m1.x = Math.round((lpt.x + fpt.x) / 2);
        m1.y = fpt.y;
        m2.x = Math.round((lpt.x + fpt.x) / 2);
        m2.y = lpt.y;
        if (isBothOnTheRight) {
            m1.x = Math.max(fpt.x, lpt.x);
            m2.x = Math.max(fpt.x, lpt.x);
        }
    }

    protected int calcLeftAeX(int aX) {
        int lLeftAeX = Integer.MIN_VALUE;
        Entry<Integer, AxisElement> lEntry = xIdx.lowerEntry(aX);
        AxisElement lLeftAe = null;
        if (lEntry != null) {
            lLeftAe = lEntry.getValue();
            if (lLeftAe != null) {
                lLeftAeX = lLeftAe.getCoordinate();
            }
        }
        return lLeftAeX;
    }

    protected int calcRightAeX(int aX) {
        int lRightAeX = Integer.MAX_VALUE;
        Entry<Integer, AxisElement> lEntry = xIdx.ceilingEntry(aX);
        AxisElement lRightAe = null;
        if (lEntry != null) {
            lRightAe = lEntry.getValue();
            if (lRightAe != null) {
                lRightAeX = lRightAe.getCoordinate();
            }
        }
        return lRightAeX;
    }

    protected int calcTopAeY(int aY) {
        int lTopAeY = Integer.MIN_VALUE;
        Entry<Integer, AxisElement> lEntry = yIdx.lowerEntry(aY);
        AxisElement lTopAe = null;
        if (lEntry != null) {
            lTopAe = lEntry.getValue();
            if (lTopAe != null) {
                lTopAeY = lTopAe.getCoordinate();
            }
        }
        return lTopAeY;
    }

    protected int calcBottomAeY(int aY) {
        int lBottomAeY = Integer.MAX_VALUE;
        Entry<Integer, AxisElement> lEntry = yIdx.ceilingEntry(aY);
        AxisElement lBottomAe = null;
        if (lEntry != null) {
            lBottomAe = lEntry.getValue();
            if (lBottomAe != null) {
                lBottomAeY = lBottomAe.getCoordinate();
            }
        }
        return lBottomAeY;
    }

    public void addArcs2Connector(List<Point> aConnector, List<EstimatedArc> aArcs, int aInitialSize) {
        if (aArcs != null) {
            EstimatedArc lPrevArc = null;
            EstimatedArc lArc = null;
            for (int i = 0; i < aArcs.size(); i++) {
                lPrevArc = lArc;
                lArc = aArcs.get(i);
                int lSegmentNumber = lArc.getSegmentNumber();
                if (lPrevArc == null || !lArc.equals(lPrevArc)) {
                    aConnector.addAll(aConnector.size() - (aInitialSize - lSegmentNumber), lArc);
                }
            }
        }
    }

    public List<EstimatedArc> arcConnectorWithOccupied(List<Point> aPath) {
        List<EstimatedArc> lcalcedArcs = new ArrayList<>();
        for (int i = 1; i < aPath.size(); i++) { // through segments
            Point pt1 = aPath.get(i - 1);
            Point pt2 = aPath.get(i);
            if (pt1.y == pt2.y) {   // one horizontal segment
                int lTopAeY = calcTopAeY(pt1.y);
                int lBottomAeY = calcBottomAeY(pt1.y);
                int ly = (int) ((long) lTopAeY + (long) lBottomAeY) / 2;
                // Left to right
                if (pt1.x <= pt2.x) {
                    int lLeftAeX = calcLeftAeX(pt1.x);
                    int lRightAeX = calcRightAeX(pt1.x);
                    while (lLeftAeX <= pt2.x) {
                        String occKey = String.valueOf((int) (((long) lLeftAeX + (long) lRightAeX) / 2)) + "x" + String.valueOf(ly);
                        List<EstimatedArc> larcs = arcConnector(i, pt1, pt2, add2Occupied(occKey, pt1, pt2, false), occKey);
                        add2GeneratedArcs(LEFT_2_RIGHT_SEGMENT, occKey, larcs);
                        lcalcedArcs.addAll(larcs);

                        Entry<Integer, AxisElement> lhigherEntry = xIdx.higherEntry(lRightAeX);
                        if (lhigherEntry != null) {
                            lLeftAeX = lRightAeX;
                            lRightAeX = lhigherEntry.getKey();
                        } else {
                            break;
                        }
                    }
                } else {// Right to left
                    int lRightAeX = calcRightAeX(pt1.x);
                    int lLeftAeX = calcLeftAeX(pt1.x);
                    while (lRightAeX >= pt2.x) {
                        String occKey = String.valueOf((int) (((long) lLeftAeX + (long) lRightAeX) / 2)) + "x" + String.valueOf(ly);
                        List<EstimatedArc> larcs = arcConnector(i, pt1, pt2, add2Occupied(occKey, pt1, pt2, false), occKey);
                        add2GeneratedArcs(RIGHT_2_LEFT_SEGMENT, occKey, larcs);
                        lcalcedArcs.addAll(larcs);

                        Entry<Integer, AxisElement> llowerEntry = xIdx.lowerEntry(lLeftAeX);
                        if (llowerEntry != null) {
                            lRightAeX = lLeftAeX;
                            lLeftAeX = llowerEntry.getKey();
                        } else {
                            break;
                        }
                    }
                }
            } else {
                // One vertical segment
                int lLeftAeX = calcLeftAeX(pt1.x);
                int lRightAeX = calcRightAeX(pt1.x);
                int lx = (int) ((long) lLeftAeX + (long) lRightAeX) / 2;
                // Top to bottom
                if (pt1.y <= pt2.y) {
                    int lTopAeY = calcTopAeY(pt1.y);
                    int lBottomAeY = calcBottomAeY(pt1.y);
                    while (lTopAeY <= pt2.y) {
                        String occKey = String.valueOf(lx) + "x" + String.valueOf((int) (((long) lTopAeY + (long) lBottomAeY) / 2));
                        List<EstimatedArc> larcs = arcConnector(i, pt1, pt2, add2Occupied(occKey, pt1, pt2, false), occKey);
                        add2GeneratedArcs(TOP_2_BOTTOM_SEGMENT, occKey, larcs);
                        lcalcedArcs.addAll(larcs);

                        Entry<Integer, AxisElement> lhigherEntry = yIdx.higherEntry(lBottomAeY);
                        if (lhigherEntry != null) {
                            lTopAeY = lBottomAeY;
                            lBottomAeY = lhigherEntry.getKey();
                        } else {
                            break;
                        }
                    }
                } else // Bottom to top
                {
                    int lBottomAeY = calcBottomAeY(pt1.y);
                    int lTopAeY = calcTopAeY(pt1.y);
                    while (lBottomAeY >= pt2.y) {
                        String occKey = String.valueOf(lx) + "x" + String.valueOf((int) (((long) lTopAeY + (long) lBottomAeY) / 2));
                        List<EstimatedArc> larcs = arcConnector(i, pt1, pt2, add2Occupied(occKey, pt1, pt2, false), occKey);
                        add2GeneratedArcs(BOTTOM_2_TOP_SEGMENT, occKey, larcs);
                        lcalcedArcs.addAll(larcs);

                        Entry<Integer, AxisElement> lowerEntry = yIdx.lowerEntry(lTopAeY);
                        if (lowerEntry != null) {
                            lBottomAeY = lTopAeY;
                            lTopAeY = lowerEntry.getKey();
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return lcalcedArcs;
    }
}

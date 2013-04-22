/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.view.visibilitygraph;

import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.gui.view.PathsFinder;
import com.eas.client.model.gui.view.RelationDesignInfo;
import com.eas.client.model.gui.view.SpatialRectanglesTree;
import com.eas.client.model.gui.view.entities.EntityView;
import com.eas.client.model.gui.view.model.ModelView;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class VisibilityGraph<E extends Entity<?, ?, E>, EV extends EntityView<E>> extends Graph<PathedVertex> {

    protected Rectangle bounds = null;
    protected int epsilon = 1;
    // Views on witch we need to work
    protected Map<Long, EV> obstacles = null;
    protected SpatialRectanglesTree<EV> obstaclesIdx = null;
    protected TreeMap<Integer, PathsFinder<E, EV>.AxisElement> sweepStops = null;
    // Vertexes are weighted by summed path distance from the initial vertex
    protected TreeMap<Integer, Set<PathedVertex>> weightedVertices = new TreeMap<>();
    protected ModelView<E, ?, ?> view;

    public VisibilityGraph(ModelView<E, ?, ?> aView, Map<Long, EV> aObstacles, SpatialRectanglesTree<EV> aObstaclesIdx, TreeMap<Integer, PathsFinder<E, EV>.AxisElement> aSweepStops) {
        super();
        view = aView;
        obstacles = aObstacles;
        obstaclesIdx = aObstaclesIdx;
        epsilon = aObstaclesIdx.getEpsilon();
        bounds = aObstaclesIdx.getBounds();
        bounds.width += 2;
        bounds.height += 2;
        sweepStops = aSweepStops;
    }

    public void paint(Graphics2D g2d) {
        g2d.setColor(Color.ORANGE);
        for (PathedVertex pv : vertices) {
            List<PathedVertex> linked = pv.getLinked();
            for (int i = 0; i < linked.size(); i++) {
                g2d.drawLine(pv.point.x, pv.point.y, linked.get(i).point.x, linked.get(i).point.y);
            }
        }
        g2d.setColor(Color.RED);
        for (PathedVertex pv : vertices) {
            g2d.drawRect(pv.point.x - 1, pv.point.y - 1, 2, 2);
        }
    }

    @Override
    public void clear() {
        viewsObstacles.clear();
        weightedVertices.clear();
        super.clear();
    }

    protected Point getRelationOuterPt(E ent, Relation<E> rel) {
        RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
        if (rel.getLeftEntity() == ent) {
            Segment slotSegment = designInfo.getFirstSlot();
            if (slotSegment != null) {
                return new Point(slotSegment.lastPoint);
            }
        } else if (rel.getRightEntity() == ent) {
            Segment slotSegment = designInfo.getLastSlot();
            if (slotSegment != null) {
                return new Point(slotSegment.lastPoint);
            }
        }
        return null;
    }

    protected Point getRelationOuterPtInverse(E ent, Relation<E> rel) {
        RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
        if (rel.getRightEntity() == ent) {
            Segment slotSegment = designInfo.getLastSlot();
            if (slotSegment != null) {
                return new Point(slotSegment.lastPoint);
            }
        } else if (rel.getLeftEntity() == ent) {
            Segment slotSegment = designInfo.getFirstSlot();
            if (slotSegment != null) {
                return new Point(slotSegment.lastPoint);
            }
        }
        return null;
    }

    protected void collectLeftRightRelations(EV eView, List<Entry<Point, Relation<E>>> leftRels, List<Entry<Point, Relation<E>>> rightRels) {
        if (leftRels != null && rightRels != null) {
            E ent = eView.getEntity();
            Rectangle linsets = eView.getBounds();
            linsets.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            Set<Relation<E>> rels = ent.getInOutRelations();
            if (rels != null) {
                TreeMap<Integer, Set<Relation<E>>> leftRelsSorter = new TreeMap<>();
                TreeMap<Integer, Set<Relation<E>>> rightRelsSorter = new TreeMap<>();
                for (Relation<E> rel : rels) {
                    Point outerSlotPt = getRelationOuterPt(ent, rel);
                    if (outerSlotPt != null) {
                        putRelation2EndgesSorters(outerSlotPt, linsets, leftRelsSorter, rel, rightRelsSorter);
                    }
                    if (rel.getLeftEntity() == rel.getRightEntity()) {
                        outerSlotPt = getRelationOuterPtInverse(ent, rel);
                        if (outerSlotPt != null) {
                            putRelation2EndgesSorters(outerSlotPt, linsets, leftRelsSorter, rel, rightRelsSorter);
                        }
                    }
                }
                if (!leftRelsSorter.isEmpty()) {
                    Set<Entry<Integer, Set<Relation<E>>>> lset = leftRelsSorter.entrySet();
                    if (lset != null) {
                        for (Entry<Integer, Set<Relation<E>>> lentry : lset) {
                            if (lentry != null) {
                                Set<Relation<E>> rhs = lentry.getValue();
                                if (rhs != null) {
                                    for (Relation<E> rel : rhs) {
                                        leftRels.add(new SimpleEntry<>(new Point(linsets.x, lentry.getKey()), rel));
                                    }
                                }
                            }
                        }
                    }
                }
                if (!rightRelsSorter.isEmpty()) {
                    Set<Entry<Integer, Set<Relation<E>>>> lset = rightRelsSorter.entrySet();
                    if (lset != null) {
                        for (Entry<Integer, Set<Relation<E>>> lentry : lset) {
                            if (lentry != null) {
                                Set<Relation<E>> rhs = lentry.getValue();
                                if (rhs != null) {
                                    for (Relation<E> rel : rhs) {
                                        rightRels.add(new SimpleEntry<>(new Point(linsets.x + linsets.width, lentry.getKey()), rel));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    protected Map<EV, PathObstacle> viewsObstacles = new HashMap<>();

    protected PathObstacle createAndLinkViewVertices(EV aView) {
        if (aView != null) {
            Rectangle linsets = aView.getBounds();
            linsets.grow(EntityView.INSET_ZONE, EntityView.INSET_ZONE);
            PathObstacle po = viewsObstacles.get(aView);
            if (po == null) {
                po = new PathObstacle(linsets);

                List<Entry<Point, Relation<E>>> leftRels = new ArrayList<>();
                List<Entry<Point, Relation<E>>> rightRels = new ArrayList<>();
                collectLeftRightRelations(aView, leftRels, rightRels);
                //Top to bottom segment on the left 
                HorizontalEdgedVertex ltV = new HorizontalEdgedVertex();
                ltV.point = new Point(linsets.x, linsets.y);
                addVertex(ltV);
                po.topLeftV = ltV;
                PathedVertex prevV = ltV;
                for (int i = 0; i < leftRels.size(); i++) {
                    Relation<E> rel = leftRels.get(i).getValue();
                    Point lpt = leftRels.get(i).getKey();
                    // In any case we shoud bind vertex with relation
                    if (lpt != null && !lpt.equals(prevV.point)) {
                        VerticalEdgedVertex currentV = new VerticalEdgedVertex();
                        currentV.point = lpt;
                        addVertex(currentV);
                        po.addLeftVertex(currentV);
                        prevV = currentV;
                    }
                    putGraphVertex2Relation(rel, prevV);
                }
                HorizontalEdgedVertex lbV = new HorizontalEdgedVertex();
                lbV.point = new Point(linsets.x, linsets.y + linsets.height);
                addVertex(lbV);
                po.setBottomLeftV(lbV);

                //Top to bottom segment on the right
                HorizontalEdgedVertex rtV = new HorizontalEdgedVertex();
                rtV.point = new Point(linsets.x + linsets.width, linsets.y);
                addVertex(rtV);
                po.topRightV = rtV;

                prevV = rtV;
                for (int i = 0; i < rightRels.size(); i++) {
                    Relation<E> rel = rightRels.get(i).getValue();
                    Point lpt = rightRels.get(i).getKey();
                    // In any case we shoud bind vertex with relation
                    if (lpt != null && !lpt.equals(prevV.point)) {
                        VerticalEdgedVertex currentV = new VerticalEdgedVertex();
                        currentV.point = lpt;
                        addVertex(currentV);
                        po.addRightVertex(currentV);
                        prevV = currentV;
                    }
                    putGraphVertex2Relation(rel, prevV);
                }
                HorizontalEdgedVertex rbV = new HorizontalEdgedVertex();
                rbV.point = new Point(linsets.x + linsets.width, linsets.y + linsets.height);
                addVertex(rbV);
                po.setBottomRightV(rbV);
                viewsObstacles.put(aView, po);
            }
            return po;
        }
        return null;
    }

    protected void link2ObstaclesByViews(EV aView1, EV aView2, TreeMap<Integer, PathObstacle> sweepObstacles, int x) {
        if (aView1 != null && aView2 != null && sweepObstacles != null) {
            PathObstacle po1 = viewsObstacles.get(aView1);
            PathObstacle po2 = viewsObstacles.get(aView2);
            if (po1 != null && po2 != null) {
                Entry<Integer, PathObstacle> poE = sweepObstacles.lowerEntry(po2.bounds.y);
                if (poE != null && poE.getValue() == po1) {
                    HorizontalEdgedVertex topV = null;
                    HorizontalEdgedVertex bottomV = null;
                    if (po2.topLeftV.point.x == po1.bottomLeftV.point.x && po1.bottomLeftV.point.x == x) {
                        topV = po2.topLeftV;
                        bottomV = po1.bottomLeftV;
                    } else if (po2.topRightV.point.x == po1.bottomLeftV.point.x && po1.bottomLeftV.point.x == x) {
                        topV = po2.topRightV;
                        bottomV = po1.bottomLeftV;
                    } else if (po2.topLeftV.point.x == po1.bottomRightV.point.x && po1.bottomRightV.point.x == x) {
                        topV = po2.topLeftV;
                        bottomV = po1.bottomRightV;
                    } else if (po2.topRightV.point.x == po1.bottomRightV.point.x && po1.bottomRightV.point.x == x) {
                        topV = po2.topRightV;
                        bottomV = po1.bottomRightV;
                    }
                    if (topV != null && bottomV != null) {
                        topV.addLinkedVertex(bottomV, PathedVertex.TOP_BOTTOM_FLAG);
                        bottomV.addLinkedVertex(topV, PathedVertex.BOTTOM_TOP_FLAG);
                    }
                }
            }
        }
    }

    protected void addSweepedVertices(TreeMap<Integer, PathedVertex> sweepedVertices, List<PathObstacle> obstacles, boolean toRight, int x) {
        if (obstacles != null && sweepedVertices != null) {
            for (int i = 0; i < obstacles.size(); i++) {
                PathObstacle po = obstacles.get(i);
                if (po.bounds.x == x) {
                    sweepedVertices.put(po.topLeftV.point.y, po.topLeftV);
                    if (!toRight) {
                        List<PathedVertex> leftVertices = po.getLeftVertices();
                        for (int j = 0; j < leftVertices.size(); j++) {
                            PathedVertex hv = leftVertices.get(j);
                            sweepedVertices.put(hv.point.y, hv);
                        }
                    }
                    sweepedVertices.put(po.bottomLeftV.point.y, po.bottomLeftV);
                } else {
                    sweepedVertices.put(po.topRightV.point.y, po.topRightV);
                    if (toRight) {
                        List<PathedVertex> rightVertices = po.getRightVertices();
                        for (int j = 0; j < rightVertices.size(); j++) {
                            PathedVertex hv = rightVertices.get(j);
                            sweepedVertices.put(hv.point.y, hv);
                        }
                    }
                    sweepedVertices.put(po.bottomRightV.point.y, po.bottomRightV);
                }
            }
        }
    }

    protected void deleteSweepedVertices(TreeMap<Integer, PathedVertex> sweepedVertices, List<PathObstacle> obstacles) {
        if (obstacles != null && sweepedVertices != null) {
            for (int i = 0; i < obstacles.size(); i++) {
                PathObstacle po = obstacles.get(i);
                Integer y1 = new Integer(po.bounds.y);
                Integer y2 = new Integer(po.bounds.y + po.bounds.height);

                Integer toDelKey = sweepedVertices.ceilingKey(y1);
                while (toDelKey != null && toDelKey <= y2) {
                    sweepedVertices.remove(toDelKey);
                    toDelKey = sweepedVertices.ceilingKey(y1);
                }
            }
        }
    }

    protected void reportVertex(TreeMap<Integer, PathedVertex> sweepedVertices, PathedVertex aV, int lowerY, int higherY, boolean toRight) {
        if (aV != null) {
            Entry<Integer, PathedVertex> lE = sweepedVertices.ceilingEntry(lowerY);
            while (lE != null && lE.getKey() <= higherY) {
                PathedVertex eV = lE.getValue();
                if (toRight) {
                    if (eV.point.y >= aV.point.y) {
                        eV.addLinkedVertex(aV, PathedVertex.RIGHT_TOP_FLAG);
                        aV.addLinkedVertex(eV, PathedVertex.interDirections[PathedVertex.RIGHT_TOP_FLAG]);
                    } else {
                        eV.addLinkedVertex(aV, PathedVertex.RIGHT_BOTTOM_FLAG);
                        aV.addLinkedVertex(eV, PathedVertex.interDirections[PathedVertex.RIGHT_BOTTOM_FLAG]);
                    }
                } else {
                    if (eV.point.y >= aV.point.y) {
                        eV.addLinkedVertex(aV, PathedVertex.LEFT_TOP_FLAG);
                        aV.addLinkedVertex(eV, PathedVertex.interDirections[PathedVertex.LEFT_TOP_FLAG]);
                    } else {
                        eV.addLinkedVertex(aV, PathedVertex.LEFT_BOTTOM_FLAG);
                        aV.addLinkedVertex(eV, PathedVertex.interDirections[PathedVertex.LEFT_BOTTOM_FLAG]);
                    }
                }
                lE = sweepedVertices.higherEntry(lE.getKey());
            }
        }
    }

    protected void reportVertices(TreeMap<Integer, PathedVertex> sweepedVertices, TreeMap<Integer, PathObstacle> sweepObstacles, List<PathObstacle> obstacles, boolean toRight, int x) {
        if (obstacles != null && sweepedVertices != null) {
            for (int i = 0; i < obstacles.size(); i++) {
                PathObstacle po = obstacles.get(i);
                Entry<Integer, PathObstacle> tE = sweepObstacles.lowerEntry(po.topLeftV.point.y);
                Entry<Integer, PathObstacle> bE = sweepObstacles.higherEntry(po.bottomLeftV.point.y);
                Integer y1 = new Integer(0);
                if (tE != null) {
                    y1 = new Integer(tE.getValue().bottomLeftV.point.y);
                }
                Integer y2 = new Integer(bounds.height);
                if (bE != null) {
                    y2 = new Integer(bE.getValue().topLeftV.point.y);
                }
                if (po.bounds.x == x) {
                    List<PathedVertex> leftVertices = po.getLeftVertices();
                    reportVertex(sweepedVertices, po.topLeftV, y1, (leftVertices != null && !leftVertices.isEmpty()) ? leftVertices.get(0).point.y : po.bottomLeftV.point.y, toRight);
                    if (leftVertices != null) {
                        Integer iy1 = null;
                        Integer iy2 = null;
                        for (int j = 0; j < leftVertices.size(); j++) {
                            if (j > 0) {
                                iy1 = leftVertices.get(j - 1).point.y;
                            } else {
                                iy1 = po.topLeftV.point.y;
                            }
                            if (j < leftVertices.size() - 1) {
                                iy2 = leftVertices.get(j + 1).point.y;
                            } else {
                                iy2 = po.bottomLeftV.point.y;
                            }
                            reportVertex(sweepedVertices, leftVertices.get(j), iy1, iy2, toRight);
                        }
                    }
                    reportVertex(sweepedVertices, po.bottomLeftV, (leftVertices != null && !leftVertices.isEmpty()) ? leftVertices.get(leftVertices.size() - 1).point.y : po.topLeftV.point.y, y2, toRight);
                } else {
                    List<PathedVertex> rightVertices = po.getRightVertices();
                    reportVertex(sweepedVertices, po.topRightV, y1, (rightVertices != null && !rightVertices.isEmpty()) ? rightVertices.get(0).point.y : po.bottomLeftV.point.y, toRight);
                    if (rightVertices != null) {
                        Integer iy1 = null;
                        Integer iy2 = null;
                        for (int j = 0; j < rightVertices.size(); j++) {
                            if (j > 0) {
                                iy1 = rightVertices.get(j - 1).point.y;
                            } else {
                                iy1 = po.topLeftV.point.y;
                            }
                            if (j < rightVertices.size() - 1) {
                                iy2 = rightVertices.get(j + 1).point.y;
                            } else {
                                iy2 = po.bottomLeftV.point.y;
                            }
                            reportVertex(sweepedVertices, rightVertices.get(j), iy1, iy2, toRight);
                        }
                    }
                    reportVertex(sweepedVertices, po.bottomRightV, (rightVertices != null && !rightVertices.isEmpty()) ? rightVertices.get(rightVertices.size() - 1).point.y : po.topLeftV.point.y, y2, toRight);
                }
            }
        }
    }

    protected void sweep(boolean toRight) {
        if (sweepStops != null && !sweepStops.isEmpty()) {
            TreeMap<Integer, PathObstacle> sweepObstacles = new TreeMap<>();
            TreeMap<Integer, PathedVertex> sweepedVertices = new TreeMap<>();

            Set<Entry<Integer, PathsFinder<E, EV>.AxisElement>> stopsSet = null;
            if (toRight) {
                stopsSet = sweepStops.entrySet();
            } else {
                stopsSet = sweepStops.descendingMap().entrySet();
            }
            for (Entry<Integer, PathsFinder<E, EV>.AxisElement> stopE : stopsSet) {
                if (stopE != null) {
                    Integer key = stopE.getKey();
                    PathsFinder<E, EV>.AxisElement ae = stopE.getValue();
                    if (key != null && ae != null) {
                        Set<Map.Entry<Integer, EV>> viewsChain = ae.getAscendingElements(true);
                        if (viewsChain != null) {
                            // prepare obstacles from x-indexed views
                            List<Integer> toDelete = new ArrayList<>();
                            List<PathObstacle> toReport = new ArrayList<>();
                            EV prevFr = null;
                            for (Map.Entry<Integer, EV> frE : viewsChain) {
                                if (frE != null) {
                                    EV frComp = frE.getValue();
                                    if (frComp != null) {
                                        PathObstacle po = createAndLinkViewVertices(frComp);
                                        Integer vKey = new Integer(po.bounds.y);
                                        if (sweepObstacles.containsKey(vKey)) {
                                            toDelete.add(vKey);
                                        } else {
                                            sweepObstacles.put(vKey, po);
                                        }
                                        toReport.add(po);
                                        if (prevFr != null) {
                                            link2ObstaclesByViews(prevFr, frComp, sweepObstacles, key);
                                        }
                                        prevFr = frComp;
                                    }
                                }
                            }
                            // connect visible vertices to vertices in met obstacles
                            reportVertices(sweepedVertices, sweepObstacles, toReport, toRight, key);
                            // delete from sweeped vertices by met obstacles
                            deleteSweepedVertices(sweepedVertices, toReport);
                            // add to sweeped vertices from met obstacles
                            addSweepedVertices(sweepedVertices, toReport, toRight, key);
                            // delete completly passed obstacles
                            for (int i = 0; i < toDelete.size(); i++) {
                                sweepObstacles.remove(toDelete.get(i));
                            }
                        }
                    }
                }
            }
        }
    }

    public void rebuild() {
        clear();
        sweep(true);
        sweep(false);
        modifyGraphWithTurns();
    }

    public static Rectangle points2Rect(Point firstPt, Point secondPt) {
        if (firstPt != null && secondPt != null) {
            return new Rectangle(firstPt.x < secondPt.x ? firstPt.x : secondPt.x, firstPt.y < secondPt.y ? firstPt.y : secondPt.y, Math.abs(secondPt.x - firstPt.x), Math.abs(secondPt.y - firstPt.y));
        } else {
            return null;
        }
    }

    public List<Point> relationPath2Vector(Relation<E> aRel) {
        if (aRel != null) {
            RelationDesignInfo designInfo = view.getRelationDesignInfo(aRel);
            List<Point> points = new ArrayList<>();
            PathedVertex lv = designInfo.getLastVertex();
            PathedVertex fv = designInfo.getFirstVertex();
            if (lv != null && fv != null) {
                PathedVertex prevV = lv;
                points.add(0, new Point(lv.point));
                Point prevPt = lv.point;
                Point prevprevPt = null;
                int j = 0;
                do {
                    lv = lv.prevVertexInPath;
                    if (lv == null || points.size() >= PathsFinder.MAX_PATH_ITERATIONS) {
                        // if there is no any path we return point for direct path
                        points.clear();
                        points.add(fv.point);
                        points.add(fv.point);
                        return points;
                    }
                    if (prevV != null && !isOnTheSameLine(prevV.point, prevV.point, lv.point)) {
                        Point lmiddlePt = calcMiddlePoint(lv, prevV, PathedVertex.interDirections[prevV.prevVertexInPathKind]);
                        if (prevPt != null && prevprevPt != null && isOnTheSameLine(prevprevPt, prevPt, lmiddlePt)) {
                            points.set(0, lmiddlePt);
                        } else {
                            points.add(0, lmiddlePt);
                        }
                        prevprevPt = prevPt;
                        prevPt = lmiddlePt;
                    }
                    if (prevPt != null && prevprevPt != null && isOnTheSameLine(prevprevPt, prevPt, lv.point)) {
                        points.set(0, new Point(lv.point));
                    } else {
                        points.add(0, new Point(lv.point));
                    }
                    prevprevPt = prevPt;
                    prevPt = lv.point;
                    prevV = lv;
                } while (lv != fv && ++j <= PathsFinder.MAX_PATH_ITERATIONS);
                return points;
            }
        }
        return null;
    }

    public void findShortestPaths(VerticalEdgedVertex fromVertex, Set<PathedVertex> endVertices) {
        if (fromVertex != null) {
            initPathsAndLabels(fromVertex);
            findDijkstraPaths(fromVertex, endVertices);
        }
    }

    protected void modifyGraphWithTurns() {
        HorizontalEdgedVertex[] graphVertices = new HorizontalEdgedVertex[vertices.size()];
        int gvCount = 0;
        int j = 0;
        for (PathedVertex hpv : vertices) {
            if (hpv instanceof HorizontalEdgedVertex) {
                graphVertices[j++] = (HorizontalEdgedVertex) hpv;
                ++gvCount;
            }
        }
        for (j = 0; j < gvCount; j++) {
            PathedVertex pv = graphVertices[j];
            if (pv instanceof HorizontalEdgedVertex) {
                List<PathedVertex> linked = pv.getLinked();
                List<Integer> linkedKinds = pv.getLinkedKinds();
                if (linked != null && linkedKinds != null) {
                    VerticalEdgedVertex vev = null;
                    for (int i = 0; i < linked.size(); i++) {
                        Integer lkind = linkedKinds.get(i);
                        PathedVertex lpv = linked.get(i);
                        if (isVerticalEdgeKind(pv, lpv, lkind)) {
                            if (vev == null) {
                                vev = new VerticalEdgedVertex();
                                vev.point = pv.point;
                                addVertex(vev);
                            }
                            vev.addLinkedVertex(lpv, lkind);
                            lpv.addLinkedVertex(vev, PathedVertex.interDirections[lkind]);

                            lpv.add2DeleteVertex(pv);
                            pv.add2DeleteVertex(lpv);
                        }
                    }
                    if (vev != null) {
                        vev.addLinkedVertex(pv, -1);
                        pv.addLinkedVertex(vev, -1);
                    }
                }
            }
        }
        for (j = 0; j < gvCount; j++) {
            graphVertices[j].deleteMarked4Delete();
        }
    }

    protected void initPathsAndLabels(PathedVertex fromVertex) {
        weightedVertices.clear();
        if (vertices != null) {
            for (PathedVertex pv : vertices) {
                pv.prevVertexInPath = null;
                pv.prevVertexInPathKind = -1;
                if (pv == fromVertex) {
                    pv.weightInPaths = 0;
                    pv.permanentLabeled = true;
                } else {
                    pv.weightInPaths = Integer.MAX_VALUE;
                    add2WeightedVertices(pv);
                    pv.permanentLabeled = false;
                }
            }
        }
    }

    protected void findDijkstraPaths(PathedVertex fpv, Set<PathedVertex> endVertices) {
        if (fpv != null && endVertices != null && !endVertices.isEmpty()) {
            PathedVertex currentV = fpv;
            int iterationsCount = vertices.size();
            for (int i = 0; i < iterationsCount - 1; i++) {
                try2ReorientAjacents(currentV);
                currentV = chooseNewCurrentVertex();
                deleteFromWeightedVertices(currentV);
                currentV.permanentLabeled = true;
                if (endVertices.contains(currentV)) {
                    endVertices.remove(currentV);
                    if (endVertices.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    protected PathedVertex chooseNewCurrentVertex() {
        if (weightedVertices != null) {
            Entry<Integer, Set<PathedVertex>> le = weightedVertices.firstEntry();
            if (le != null) {
                Set<PathedVertex> pvHs = le.getValue();
                if (pvHs != null && !pvHs.isEmpty()) {
                    Iterator<PathedVertex> pvIt = pvHs.iterator();
                    if (pvIt != null && pvIt.hasNext()) {
                        return pvIt.next();
                    }
                }
            }
        }
        return null;
    }

    protected void try2ReorientAjacents(PathedVertex aVertex) {
        if (aVertex != null && aVertex.linked != null) {
            int baseWeight = aVertex.weightInPaths;
            for (int i = 0; i < aVertex.linked.size(); i++) {
                PathedVertex pv = aVertex.linked.get(i);
                if (pv != null && !pv.isPermanentLabeled()) {
                    if (baseWeight + calcOrthoDistance(aVertex.point, pv.point) + calcBendCorrection(aVertex, pv) < pv.weightInPaths) {// Change the weightInPaths
                        deleteFromWeightedVertices(pv);
                        pv.weightInPaths = baseWeight + calcOrthoDistance(aVertex.point, pv.point);
                        add2WeightedVertices(pv);
                        // Reorient vertex
                        pv.prevVertexInPath = aVertex;
                        pv.prevVertexInPathKind = PathedVertex.interDirections[aVertex.linkedKinds.get(i)];
                    }
                }
            }
        }
    }

    public static int calcOrthoDistance(Point pt1, Point pt2) {
        if (pt1 != null && pt2 != null) {
            return Math.abs(pt2.y - pt1.y) + Math.abs(pt2.x - pt1.x);
        }
        return Integer.MAX_VALUE;
    }

    protected int calcBendCorrection(PathedVertex pv1, PathedVertex pv2) {
        if (pv1 != null && pv2 != null
                && (pv1 instanceof HorizontalEdgedVertex && pv2 instanceof VerticalEdgedVertex
                || pv2 instanceof HorizontalEdgedVertex && pv1 instanceof VerticalEdgedVertex)) {
            return 1;
        }
        return 0;
    }

    protected boolean containsInWeightedVertices(PathedVertex pv) {
        if (weightedVertices != null && pv != null) {
            Set<PathedVertex> pvHs = weightedVertices.get(pv.weightInPaths);
            if (pvHs != null) {
                return pvHs.contains(pv);
            }
        }
        return false;
    }

    protected void add2WeightedVertices(PathedVertex pv) {
        if (weightedVertices != null && pv != null) {
            Set<PathedVertex> pvHs = weightedVertices.get(pv.weightInPaths);
            if (pvHs == null) {
                pvHs = new HashSet<>();
                weightedVertices.put(pv.weightInPaths, pvHs);
            }
            pvHs.add(pv);
        }
    }

    protected void deleteFromWeightedVertices(PathedVertex pv) {
        if (weightedVertices != null && pv != null) {
            Set<PathedVertex> pvHs = weightedVertices.get(pv.weightInPaths);
            if (pvHs != null) {
                pvHs.remove(pv);
                if (pvHs.isEmpty()) {
                    weightedVertices.remove(pv.weightInPaths);
                }
            }
        }
    }

    protected boolean isOnTheSameLine(Point pt1, Point pt2, Point pt3) {
        return ((pt1.x == pt2.x && pt2.x == pt3.x)
                || (pt1.y == pt2.y && pt2.y == pt3.y));
    }

    protected Point calcMiddlePoint(PathedVertex fv, PathedVertex sv, int kind) {
        Point res = new Point();
        switch (kind) {
            case PathedVertex.RIGHT_TOP_FLAG:
            case PathedVertex.RIGHT_BOTTOM_FLAG:
            case PathedVertex.LEFT_TOP_FLAG:
            case PathedVertex.LEFT_BOTTOM_FLAG:
                res.y = fv.point.y;
                res.x = sv.point.x;
                break;

            case PathedVertex.TOP_RIGHT_FLAG:
            case PathedVertex.TOP_LEFT_FLAG:
            case PathedVertex.BOTTOM_RIGHT_FLAG:
            case PathedVertex.BOTTOM_LEFT_FLAG:
                res.x = fv.point.x;
                res.y = sv.point.y;
                break;
        }
        return res;
    }

    protected boolean isVerticalEdgeKind(PathedVertex fv, PathedVertex sv, int kind) {
        if (fv.point.y != sv.point.y) {
            switch (kind) {
                case PathedVertex.TOP_RIGHT_FLAG:
                case PathedVertex.TOP_LEFT_FLAG:
                case PathedVertex.BOTTOM_RIGHT_FLAG:
                case PathedVertex.BOTTOM_LEFT_FLAG:
                    return true;
                case PathedVertex.RIGHT_TOP_FLAG:
                case PathedVertex.RIGHT_BOTTOM_FLAG:
                case PathedVertex.LEFT_TOP_FLAG:
                case PathedVertex.LEFT_BOTTOM_FLAG:
                    return false;
            }
        }
        return false;
    }

    private void putRelation2EndgesSorters(Point outerSlotPt, Rectangle linsets, TreeMap<Integer, Set<Relation<E>>> leftRelsSorter, Relation<E> rel, TreeMap<Integer, Set<Relation<E>>> rightRelsSorter) {
        if (outerSlotPt.x <= linsets.x) {
            // relation end point is on the left
            Integer lkey = new Integer(outerSlotPt.y);
            Set<Relation<E>> rhs = leftRelsSorter.get(lkey);
            if (rhs == null) {
                rhs = new HashSet<>();
                leftRelsSorter.put(lkey, rhs);
            }
            rhs.add(rel);
        } else if (outerSlotPt.x >= linsets.x + linsets.width) {
            // relation end point is on the right
            Integer lkey = new Integer(outerSlotPt.y);
            Set<Relation<E>> rhs = rightRelsSorter.get(lkey);
            if (rhs == null) {
                rhs = new HashSet<>();
                rightRelsSorter.put(lkey, rhs);
            }
            rhs.add(rel);
        }
    }

    private void putGraphVertex2Relation(Relation<E> rel, PathedVertex aVertex) {
        RelationDesignInfo designInfo = view.getRelationDesignInfo(rel);
        Segment fslot = designInfo.getFirstSlot();
        Segment lslot = designInfo.getLastSlot();
        if (fslot != null && lslot != null) {
            Point fPt = fslot.lastPoint;
            Point lPt = lslot.lastPoint;
            if (aVertex.point.equals(fPt)) {
                designInfo.setFirstVertex(aVertex);
            } else {
                assert aVertex.point.equals(lPt);
                designInfo.setLastVertex(aVertex);
            }

        }
    }
}

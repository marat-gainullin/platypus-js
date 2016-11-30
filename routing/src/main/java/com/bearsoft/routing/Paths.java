/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing;

import com.bearsoft.routing.graph.Vertex;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 *
 * @author mg
 */
public class Paths {

    protected List<Vertex<PathFragment>> graph;
    protected QuadTree<Vertex<PathFragment>> verticesIndex;

    public Paths(List<Vertex<PathFragment>> aGraph, QuadTree<Vertex<PathFragment>> aVerticesIndex) {
        graph = aGraph;
        verticesIndex = aVerticesIndex;
    }

    protected void clean() {
        for (Vertex<PathFragment> v : graph) {
            if (v.attribute != null) {
                v.attribute.clear();
            }
        }
    }

    public Connector find(Point aStart, Point aEnd) {
        clean();
        Vertex<PathFragment> start = findVertex(aStart);
        Vertex<PathFragment> end = findVertex(aEnd);
        Vertex<PathFragment> lstart = start;
        Vertex<PathFragment> lend = end;
        if (start != null && end != null) {
            start.attribute.pastCost = 0;
            start.attribute.futureCost = futureCost(start, aEnd);
            start.attribute.point = aStart;
            end.attribute.point = aEnd;
            NavigableSet<Vertex<PathFragment>> openSet = new TreeSet<>(new Comparator<Vertex<PathFragment>>() {
                @Override
                public int compare(Vertex<PathFragment> o1, Vertex<PathFragment> o2) {
                    return o1.attribute.pastCost + o1.attribute.futureCost - o2.attribute.pastCost - o2.attribute.futureCost;
                }
            });
            openSet.add(start);
            while (!openSet.isEmpty()) {
                Vertex<PathFragment> current = openSet.pollFirst();
                if (current == end) {
                    end = current;
                    break;
                }
                for (Vertex<PathFragment> ajacent : current.getAjacent()) {
                    int ajacentPastCost = current.attribute.pastCost + calcDistance(aStart, aEnd, lstart, lend, current, ajacent);
                    if (ajacentPastCost < ajacent.attribute.pastCost) {
                        if (ajacent.attribute.previous != null) {
                            openSet.remove(ajacent);
                        }
                        ajacent.attribute.previous = current;
                        ajacent.attribute.pastCost = ajacentPastCost;
                        ajacent.attribute.futureCost = futureCost(ajacent, aEnd);
                        openSet.add(ajacent);
                    }
                }
            }
            if (end.attribute.previous != null) {
                List<Vertex<PathFragment>> path = new ArrayList<>();
                while (end != null) {
                    path.add(0, end);
                    end = end.attribute.previous;
                }
                return convertPath2Connector(aStart, aEnd, path);
            } else {
                return convertPath2Connector(aStart, aEnd, null);
            }
        } else {
            return convertPath2Connector(aStart, aEnd, null);
        }
    }

    private int futureCost(Vertex<PathFragment> aVertex, Point aEnd) {
        Point vertexCenter = new Point(aVertex.attribute.rect.x + aVertex.attribute.rect.width / 2, aVertex.attribute.rect.y + aVertex.attribute.rect.height / 2);
        return (int) Math.sqrt(Math.pow(vertexCenter.x - aEnd.x, 2) + Math.pow(vertexCenter.y - aEnd.y, 2));
    }

    private Vertex<PathFragment> findVertex(Point aPoint) {
        List<Vertex<PathFragment>> vertices = verticesIndex.query(aPoint);
        for (Vertex<PathFragment> v : vertices) {
            if (v.attribute.rect.x <= aPoint.x && aPoint.x <= v.attribute.rect.x + v.attribute.rect.width - 1
                    && v.attribute.rect.y <= aPoint.y && aPoint.y <= v.attribute.rect.y + v.attribute.rect.height - 1) {
                return v;
            }
        }
        return null;
    }

    private Connector convertPath2Connector(Point aStartPoint, Point aEndPoint, List<Vertex<PathFragment>> aPath) {
        List<Point> points = new ArrayList<>();
        rleAdd(points, aStartPoint);
        boolean fallback = true;
        if (aPath != null && aPath.size() > 1) {
            fallback = false;
            Point prevPt = aStartPoint;
            Vertex<PathFragment> prevV = aPath.get(0);
            for (int i = 1; i < aPath.size(); i++) {
                Vertex<PathFragment> v = aPath.get(i);
                Point pt = new Point();
                calcNextPoint(aStartPoint, aEndPoint, prevV, v, pt);
                // Let's correct calcDistance()'s trace
                v.attribute.point = pt;
                Point pt0 = new Point(prevPt.x, pt.y);
                rleAdd(points, pt0);
                rleAdd(points, pt);
                /*
                if(inCorridor){
                    rleAdd(points, pt);
                }else{
                    Point pt0 = new Point(prevPt.x, pt.y);
                    rleAdd(points, pt0);
                    rleAdd(points, pt);
                }
                */ 
                prevPt = pt;
                prevV = v;
                /*
                int y1 = Math.max(prevV.attribute.rect.y, v.attribute.rect.y);
                int y2 = Math.min(prevV.attribute.rect.y + prevV.attribute.rect.height - 1, v.attribute.rect.y + v.attribute.rect.height - 1);
                boolean hContainsEndPoint = v.attribute.rect.x <= aEndPoint.x && aEndPoint.x <= v.attribute.rect.x + v.attribute.rect.width - 1;
                boolean vContainsEndPoint = y1 <= aEndPoint.y && aEndPoint.y <= y2;
                if (prevPt.y >= y1 && prevPt.y <= y2) {
                    Point pt = new Point(hContainsEndPoint ? aEndPoint.x : v.attribute.rect.x + v.attribute.rect.width / 2, prevPt.y);
                    rleAdd(points, pt);
                    prevPt = pt;
                } else {
                    Point pt = new Point(hContainsEndPoint ? aEndPoint.x : v.attribute.rect.x + v.attribute.rect.width / 2, vContainsEndPoint ? aEndPoint.y : (y1 + y2) / 2);
                    Point pt0 = new Point(prevPt.x, pt.y);
                    rleAdd(points, pt0);
                    rleAdd(points, pt);
                    prevPt = pt;
                }
                prevV = v;
                */ 
            }
        } else {
            rleAdd(points, new Point((aStartPoint.x + aEndPoint.x) / 2, aStartPoint.y));
            rleAdd(points, new Point((aStartPoint.x + aEndPoint.x) / 2, aEndPoint.y));
        }
        rleAdd(points, aEndPoint);
        Connector connector = pointsToConnector(points);
        connector.setFalled(fallback);
        return connector;
    }

    protected void rleAdd(List<Point> aPoints, Point aPoint) {
        if (aPoints.isEmpty()) {// first point is allways acceptable
            aPoints.add(aPoint);
        } else {
            Point prev = aPoints.get(aPoints.size() - 1);
            if (prev.x != aPoint.x || prev.y != aPoint.y) {
                if (aPoints.size() > 1) {
                    Point prevPrev = aPoints.get(aPoints.size() - 2);
                    if (prevPrev.x == prev.x && prev.x == aPoint.x
                            || prevPrev.y == prev.y && prev.y == aPoint.y) {
                        prev.x = aPoint.x;
                        prev.y = aPoint.y;
                    } else {
                        aPoints.add(aPoint);
                    }
                } else {// if second point is not the same, than it is acceptable
                    aPoints.add(aPoint);
                }
            }// same point as previous is omitted
        }
    }

    private Connector pointsToConnector(List<Point> points) {
        int[] x = new int[points.size()];
        int[] y = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point pt = points.get(i);
            x[i] = pt.x;
            y[i] = pt.y;
        }
        return new Connector(x, y);
    }

    private int calcDistance(Point aStartPoint, Point aEndPoint, Vertex<PathFragment> aStart, Vertex<PathFragment> aEnd, Vertex<PathFragment> from, Vertex<PathFragment> to) {
        if (from == to) {
            return 0;
        } else {
            if (to != aStart && to != aEnd) {
                Point calcedTo = new Point();
                calcNextPoint(aStartPoint, aEndPoint, from, to, calcedTo);
                to.attribute.point = calcedTo;
                /*
                int y1 = Math.max(from.attribute.rect.y, to.attribute.rect.y);
                int y2 = Math.min(from.attribute.rect.y + from.attribute.rect.height - 1, to.attribute.rect.y + to.attribute.rect.height - 1);
                boolean hContainsEndPoint = to.attribute.rect.x <= aEndPoint.x && aEndPoint.x <= to.attribute.rect.x + to.attribute.rect.width - 1;
                boolean vContainsEndPoint = y1 <= aEndPoint.y && aEndPoint.y <= y2;
                if (from.attribute.point.y >= y1 && from.attribute.point.y <= y2) {
                    to.attribute.point = new Point(hContainsEndPoint ? aEndPoint.x : to.attribute.rect.x + to.attribute.rect.width / 2, from.attribute.point.y);
                } else {
                    to.attribute.point = new Point(hContainsEndPoint ? aEndPoint.x : to.attribute.rect.x + to.attribute.rect.width / 2, vContainsEndPoint ? aEndPoint.y : (y1 + y2) / 2);
                }
                */ 
            }
            return Math.abs(from.attribute.point.x - to.attribute.point.x) + Math.abs(from.attribute.point.y - to.attribute.point.y);
        }
    }

    /**
     * Calculates
     * <code>toPoint</code> values and returns indoor status of
     * from.attribute.point.y coordinate.
     *
     * @param aStartPoint Global start point.
     * @param aEndPoint Global end point
     * @param from Current from vertex of visibility graph.
     * @param to Current to vertex of visibility graph.
     * @param resPoint Out parameter, the result is placed in.
     * @return True if of from.attribute.point.y is in free space corridor
     * between ajacent rectangles
     */
    private boolean calcNextPoint(Point aStartPoint, Point aEndPoint, Vertex<PathFragment> from, Vertex<PathFragment> to, Point resPoint) {
        int y1 = Math.max(from.attribute.rect.y, to.attribute.rect.y);
        int y2 = Math.min(from.attribute.rect.y + from.attribute.rect.height - 1, to.attribute.rect.y + to.attribute.rect.height - 1);
        boolean hContainsEndPoint = to.attribute.rect.x <= aEndPoint.x && aEndPoint.x <= to.attribute.rect.x + to.attribute.rect.width - 1;
        boolean vContainsEndPoint = y1 <= aEndPoint.y && aEndPoint.y <= y2;
        if (from.attribute.point.y >= y1 && from.attribute.point.y <= y2) {
            resPoint.x = hContainsEndPoint ? aEndPoint.x : to.attribute.rect.x + to.attribute.rect.width / 2;
            resPoint.y = from.attribute.point.y;
            return true;
        } else {
            resPoint.x = hContainsEndPoint ? aEndPoint.x : to.attribute.rect.x + to.attribute.rect.width / 2;
            resPoint.y = vContainsEndPoint ? aEndPoint.y : (y1 + y2) / 2;
            return false;
        }
    }
}

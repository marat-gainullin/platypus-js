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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class PathObstacle {

    public Rectangle bounds = null;
    public HorizontalEdgedVertex topLeftV = null;
    public HorizontalEdgedVertex bottomLeftV = null;
    public HorizontalEdgedVertex topRightV = null;
    public HorizontalEdgedVertex bottomRightV = null;
    protected List<PathedVertex> leftVertices = new ArrayList<>();
    protected List<PathedVertex> rightVertices = new ArrayList<>();

    public PathObstacle(Rectangle aBounds) {
        super();
        bounds = aBounds;
    }

    public void addLeftVertex(PathedVertex aVertex) {
        if (aVertex != null) {
            if (leftVertices.size() > 0) {
                PathedVertex prevV = leftVertices.get(leftVertices.size() - 1);
                if (prevV != null) {
                    prevV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                    aVertex.addLinkedVertex(prevV, PathedVertex.BOTTOM_TOP_FLAG);
                }
            } else {
                topLeftV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                aVertex.addLinkedVertex(topLeftV, PathedVertex.BOTTOM_TOP_FLAG);
            }
            leftVertices.add(aVertex);
        }
    }

    public void addRightVertex(PathedVertex aVertex) {
        if (aVertex != null) {
            if (rightVertices.size() > 0) {
                PathedVertex prevV = rightVertices.get(rightVertices.size() - 1);
                if (prevV != null) {
                    prevV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                    aVertex.addLinkedVertex(prevV, PathedVertex.BOTTOM_TOP_FLAG);
                }
            } else {
                topRightV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                aVertex.addLinkedVertex(topRightV, PathedVertex.BOTTOM_TOP_FLAG);
            }
            rightVertices.add(aVertex);
        }
    }

    public void setBottomLeftV(HorizontalEdgedVertex aVertex) {
        if (aVertex != null) {
            bottomLeftV = aVertex;
            PathedVertex prevV = topLeftV;
            if (leftVertices.size() > 0) {
                prevV = leftVertices.get(leftVertices.size() - 1);
            }
            if (prevV != null) {
                prevV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                aVertex.addLinkedVertex(prevV, PathedVertex.BOTTOM_TOP_FLAG);
            }
        }
    }

    public void setBottomRightV(HorizontalEdgedVertex aVertex) {
        if (aVertex != null) {
            bottomRightV = aVertex;
            PathedVertex prevV = topRightV;
            if (rightVertices.size() > 0) {
                prevV = rightVertices.get(rightVertices.size() - 1);
            }
            if (prevV != null) {
                prevV.addLinkedVertex(aVertex, PathedVertex.TOP_BOTTOM_FLAG);
                aVertex.addLinkedVertex(prevV, PathedVertex.BOTTOM_TOP_FLAG);
            }
        }
    }

    public List<PathedVertex> getLeftVertices() {
        return leftVertices;
    }

    public List<PathedVertex> getRightVertices() {
        return rightVertices;
    }
}

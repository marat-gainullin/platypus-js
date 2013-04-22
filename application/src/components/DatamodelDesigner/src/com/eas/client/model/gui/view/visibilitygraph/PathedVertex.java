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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class PathedVertex extends WeightedVertex<PathedVertex> {

    protected static final int FIRST_FLAG = 0;
    public static final int RIGHT_TOP_FLAG = FIRST_FLAG;
    public static final int RIGHT_BOTTOM_FLAG = 1;
    public static final int LEFT_TOP_FLAG = 2;
    public static final int LEFT_BOTTOM_FLAG = 3;
    public static final int TOP_RIGHT_FLAG = 4;
    public static final int TOP_LEFT_FLAG = 5;
    public static final int BOTTOM_RIGHT_FLAG = 6;
    public static final int BOTTOM_LEFT_FLAG = 7;
    // Odinary directions defaults
    public static final int TOP_BOTTOM_FLAG = BOTTOM_RIGHT_FLAG;
    public static final int BOTTOM_TOP_FLAG = TOP_RIGHT_FLAG;
    public static final int LEFT_RIGHT_FLAG = LEFT_BOTTOM_FLAG;
    public static final int RIGHT_LEFT_FLAG = RIGHT_BOTTOM_FLAG;
    public static final int[] interDirections = {
        BOTTOM_LEFT_FLAG, //RIGHT_TOP_FLAG
        TOP_LEFT_FLAG, //RIGHT_BOTTOM_FLAG

        BOTTOM_RIGHT_FLAG, //LEFT_TOP_FLAG
        TOP_RIGHT_FLAG, //LEFT_BOTTOM_FLAG

        LEFT_BOTTOM_FLAG, //TOP_RIGHT_FLAG
        RIGHT_BOTTOM_FLAG, //TOP_LEFT_FLAG

        LEFT_TOP_FLAG, //BOTTOM_RIGHT_FLAG
        RIGHT_TOP_FLAG //BOTTOM_LEFT_FLAG        
    };
    protected static final int LAST_FLAG = BOTTOM_LEFT_FLAG;
    protected Set<PathedVertex> toDelete = new HashSet<>();
    protected List<Integer> linkedKinds = new ArrayList<>();
    public boolean permanentLabeled = false;
    public PathedVertex prevVertexInPath = null;
    public int prevVertexInPathKind = -1;
    public int weightInPaths = 0;

    public PathedVertex() {
        super();
    }

    public boolean addLinkedVertex(PathedVertex aVertex, Integer aChildKind) {
        if (aVertex != null && aChildKind != null
                && FIRST_FLAG <= aChildKind && aChildKind <= LAST_FLAG
                && linkedKinds.add(aChildKind)) {
            return super.addLinkedVertex(aVertex);
        }
        return false;
    }

    public List<Integer> getLinkedKinds() {
        return linkedKinds;
    }

    public void clear2DeleteMarks() {
        toDelete.clear();
    }

    public void add2DeleteVertex(PathedVertex delTarget) {
        toDelete.add(delTarget);
    }

    public void deleteMarked4Delete() {
        for (int i = linked.size() - 1; i >= 0; i--) {
            PathedVertex pv = linked.get(i);
            if (toDelete.contains(pv)) {
                linked.remove(i);
                linkedKinds.remove(i);
            }
        }
        clear2DeleteMarks();
    }

    public boolean isPermanentLabeled() {
        return permanentLabeled;
    }
}

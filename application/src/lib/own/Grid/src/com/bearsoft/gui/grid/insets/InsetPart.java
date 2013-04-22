/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.insets;

/**
 *
 * @author Gala
 */
public class InsetPart {

    public static int AFTER_INSET_BIAS = Integer.MAX_VALUE / 2;

    public enum PartKind {
        BEFORE,
        CONTENT,
        AFTER
    }
    protected PartKind kind;
    protected int value;

    public InsetPart(PartKind aKind, int aValue) {
        super();
        kind = aKind;
        value = aValue;
    }

    public PartKind getKind() {
        return kind;
    }

    public int getValue() {
        return value;
    }
}

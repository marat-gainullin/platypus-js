/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing;

/**
 *
 * @author mg
 */
public class Connector {

    protected boolean falled;
    protected boolean manual;
    protected int[] x;
    protected int[] y;

    Connector(int[] aX, int[] aY) {
        x = aX;
        y = aY;
    }

    public int[] getX() {
        return x;
    }

    public int[] getY() {
        return y;
    }

    public int getSize() {
        if (x != null && y != null) {
            assert y.length == x.length;
            return y.length;
        } else {
            return 0;
        }
    }

    public boolean isFalled() {
        return falled;
    }

    public void setFalled(boolean falled) {
        this.falled = falled;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
}
